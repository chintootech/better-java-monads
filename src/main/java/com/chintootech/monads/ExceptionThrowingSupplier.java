package com.chintootech.monads;

@FunctionalInterface
public interface ExceptionThrowingSupplier<T> {
    T get() throws Exception;
}

