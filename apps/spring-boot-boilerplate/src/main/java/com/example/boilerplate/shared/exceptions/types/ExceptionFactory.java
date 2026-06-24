package com.example.boilerplate.shared.exceptions.types;

@FunctionalInterface
public interface ExceptionFactory {

    RuntimeException create(String message, Throwable cause);

}
