package com.chintootech.monads;

@FunctionalInterface
public interface ExceptionThrowingFunction<T, R> {
    R apply(T t) throws Exception;
}
