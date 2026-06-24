# DotnetApiBoilerplate API — common developer tasks.
# Usage: make <target>

SOLUTION   := DotnetApiBoilerplate.slnx
API_PROJ   := src/DotnetApiBoilerplate.Api/DotnetApiBoilerplate.Api.csproj
INFRA_PROJ := src/DotnetApiBoilerplate.Infrastructure/DotnetApiBoilerplate.Infrastructure.csproj
CONFIG     ?= Debug
COV_DIR    := coverage

.DEFAULT_GOAL := help

.PHONY: help
help: ## Show this help
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-18s\033[0m %s\n", $$1, $$2}'

.PHONY: restore
restore: ## Restore NuGet packages
	dotnet restore $(SOLUTION)

.PHONY: build
build: ## Build the solution
	dotnet build $(SOLUTION) -c $(CONFIG) --no-restore

.PHONY: test
test: ## Run all tests
	dotnet test $(SOLUTION) -c $(CONFIG) --no-restore

.PHONY: coverage
coverage: ## Run tests and collect coverage (Cobertura)
	dotnet test $(SOLUTION) -c $(CONFIG) --collect:"XPlat Code Coverage" --results-directory ./$(COV_DIR)

.PHONY: run
run: ## Run the API locally
	dotnet run --project $(API_PROJ)

.PHONY: watch
watch: ## Run the API with hot reload
	dotnet watch --project $(API_PROJ) run

.PHONY: format
format: ## Format the code
	dotnet format $(SOLUTION)

.PHONY: format-check
format-check: ## Verify formatting (CI-friendly)
	dotnet format $(SOLUTION) --verify-no-changes

# --- EF Core migrations (dotnet-ef is pinned in .config/dotnet-tools.json; run `dotnet tool restore` once) ---
# Infrastructure is also the startup project: it hosts the DesignTimeDbContextFactory and references
# Microsoft.EntityFrameworkCore.Design, so the API (which does not) is not needed at design time.
.PHONY: migration
migration: ## Add a migration: make migration NAME=Initial
	dotnet ef migrations add $(NAME) --project $(INFRA_PROJ) --startup-project $(INFRA_PROJ) --output-dir Persistence/Migrations

.PHONY: migrate
migrate: ## Apply migrations to the database
	dotnet ef database update --project $(INFRA_PROJ) --startup-project $(INFRA_PROJ)

.PHONY: migration-remove
migration-remove: ## Remove the last migration
	dotnet ef migrations remove --project $(INFRA_PROJ) --startup-project $(INFRA_PROJ)

# --- Docker ---
.PHONY: up
up: ## Start API + database with Docker Compose
	docker compose up --build -d

.PHONY: down
down: ## Stop Docker Compose stack
	docker compose down

.PHONY: clean
clean: ## Remove build output
	dotnet clean $(SOLUTION) -c $(CONFIG)
	rm -rf $(COV_DIR)
