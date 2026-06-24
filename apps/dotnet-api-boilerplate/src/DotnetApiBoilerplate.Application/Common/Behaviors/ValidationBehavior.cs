using FluentValidation;
using FluentValidation.Results;
using MediatR;
using ValidationException = DotnetApiBoilerplate.Application.Common.Exceptions.ValidationException;

namespace DotnetApiBoilerplate.Application.Common.Behaviors;

/// <summary>
///     MediatR pipeline behavior that runs every registered <see cref="IValidator{T}" /> for a request
///     before it reaches its handler. Throws <see cref="ValidationException" /> on the first failing batch.
/// </summary>
public sealed class ValidationBehavior<TRequest, TResponse>(IEnumerable<IValidator<TRequest>> validators)
    : IPipelineBehavior<TRequest, TResponse>
    where TRequest : notnull
{
    private readonly IEnumerable<IValidator<TRequest>> _validators = validators;

    public async Task<TResponse> Handle(
        TRequest request,
        RequestHandlerDelegate<TResponse> next,
        CancellationToken cancellationToken)
    {
        if (!_validators.Any())
        {
            return await next(cancellationToken);
        }

        ValidationContext<TRequest> context = new(request);

        ValidationResult[] results = await Task.WhenAll(
            _validators.Select(v => v.ValidateAsync(context, cancellationToken)));

        ValidationFailure[] failures = results
            .SelectMany(r => r.Errors)
            .Where(f => f is not null)
            .ToArray();

        if (failures.Length != 0)
        {
            Dictionary<string, string[]> errors = failures
                .GroupBy(f => f.PropertyName, f => f.ErrorMessage)
                .ToDictionary(g => g.Key, g => g.Distinct().ToArray());

            throw new ValidationException(errors);
        }

        return await next(cancellationToken);
    }
}
