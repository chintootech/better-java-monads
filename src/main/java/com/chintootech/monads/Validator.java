package com.chintootech.monads;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Class representing Monad design pattern. Monad is a way of chaining operations on the given
 * object together step by step. In Validator each step results in either success or failure
 * indicator, giving a way of receiving each of them easily and finally getting validated object or
 * list of exceptions.
 *
 * @param <T> Placeholder for an object.
 */
public final class Validator<T> {
    /**
     * Object that is validated.
     */
    private final T t;

    /**
     * List of exception thrown during validation.
     */
    private final List<Throwable> exceptions = new ArrayList<>();

    /**
     * Creates a monadic value of given object.
     *
     * @param t object to be validated
     */
    private Validator(final T t) {
        this.t = t;
    }

    /**
     * Creates validator against given object.
     *
     * @param <T> object's type
     * @param t   object to be validated
     * @return new instance of a validator
     */
    public static <T> Validator<T> of(final T t) {
        return new Validator<>(Objects.requireNonNull(t));
    }

    /**
     * Checks if the validation is successful.
     *
     * @param validation one argument boolean-valued function that represents one step of validation.
     *                   Adds exception to main validation exception list when single step validation
     *                   ends with failure.
     * @param message    error message when object is invalid
     * @return this validator
     */
    public Validator<T> validate(final Predicate<T> validation, final String message) {
        if (!validation.test(t)) {
            exceptions.add(new IllegalStateException(message));
        }
        return this;
    }

    /**
     * Extension for the {@link Validator#validate(Function, Predicate, String)} method, dedicated for
     * objects, that need to be projected before requested validation.
     *
     * @param <U>        see {@link Validator#validate(Function, Predicate, String)}
     * @param projection function that gets an objects, and returns projection representing element to
     *                   be validated.
     * @param validation see {@link Validator#validate(Function, Predicate, String)}
     * @param message    see {@link Validator#validate(Function, Predicate, String)}
     * @return this validator
     */
    public <U> Validator<T> validate(final Function<T, U> projection, final Predicate<U> validation,
                                     final String message) {
        return validate(projection.andThen(validation::test)::apply, message);
    }

    /**
     * Receives validated object or throws exception when invalid.
     *
     * @return object that was validated
     * @throws IllegalStateException when any validation step results with failure
     */
    public T get() throws IllegalStateException {
        if (isValid()) {
            return t;
        }
        IllegalStateException e = new IllegalStateException();
        exceptions.forEach(e::addSuppressed);
        throw e;
    }

    /**
     * Is valid checks if validator has no exception.
     *
     * @return the true of valid else false
     */
    public boolean isValid() {
        return exceptions.isEmpty();
    }
}
