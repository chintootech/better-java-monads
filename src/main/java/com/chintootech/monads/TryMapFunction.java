package com.chintootech.monads;


/**
 * The interface Try map function.
 *
 * @param <T> the type parameter
 * @param <R> the type parameter
 */
public interface TryMapFunction<T, R> {
    /**
     * Apply r.
     *
     * @param t the t
     * @return the r
     * @throws Throwable the throwable
     */
    R apply(T t) throws Throwable;
}
