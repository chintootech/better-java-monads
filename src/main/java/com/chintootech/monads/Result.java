package com.chintootech.monads;

import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * The type Result.
 *
 * @param <R> the type parameter
 */
public abstract class Result<R> {

    /**
     * Attempt result.
     *
     * @param <R>            the type parameter
     * @param resultSupplier the result supplier
     * @return the result
     */
    public static <R> Result<R> attempt(final ExceptionThrowingSupplier<R> resultSupplier) {
        try {
            R resultValue = resultSupplier.get();
            return Result.ok(resultValue);
        } catch (Exception e) {
            return Result.err(e);
        }
    }

    /**
     * Err result.
     *
     * @param <R> the type parameter
     * @param e   the e
     * @return the result
     */
    public static <R> Result<R> err(final Exception e) {
        return new Err<>(e);
    }

    /**
     * Ok result.
     *
     * @param <R>    the type parameter
     * @param result the result
     * @return the result
     */
    public static <R> Result<R> ok(final R result) {
        return new Ok<>(result);
    }

    /**
     * Gets exception.
     *
     * @return the exception
     */
    public abstract Exception getLeftValue();

    /**
     * Gets result.
     *
     * @return the result
     */
    public abstract R getResult();

    /**
     * Is err boolean.
     *
     * @return the boolean
     */
    public abstract boolean isErr();

    /**
     * Is ok boolean.
     *
     * @return the boolean
     */
    public abstract boolean isOk();

    /**
     * Fold t.
     *
     * @param <T>                the type parameter
     * @param transformException the transform exception
     * @param transformValue     the transform value
     * @return the t
     */
    public abstract <T> T fold(Function<Exception, T> transformException, Function<R, T> transformValue);

    /**
     * Map result.
     *
     * @param <T>            the type parameter
     * @param transformValue the transform value
     * @return the result
     */
    public abstract <T> Result<T> map(ExceptionThrowingFunction<R, T> transformValue);

    /**
     * Flat map result.
     *
     * @param <T>            the type parameter
     * @param transformValue the transform value
     * @return the result
     */
    public abstract <T> Result<T> flatMap(ExceptionThrowingFunction<R, Result<T>> transformValue);

    /**
     * The type Err.
     *
     * @param <R> the type parameter
     */
    public static final class Err<R> extends Result<R> {
        /**
         * holds exception value.
         */
        private Exception leftValue;

        private Err(final Exception e) {
            this.leftValue = e;
        }

        @Override
        public Exception getLeftValue() {
            return this.leftValue;
        }

        @Override
        public R getResult() {
            throw new NoSuchElementException("Tried to getResult from an Err");
        }

        @Override
        public boolean isErr() {
            return true;
        }

        @Override
        public boolean isOk() {
            return false;
        }

        @Override
        public <T> T fold(final Function<Exception, T> transformException, final Function<R, T> transformValue) {
            return transformException.apply(this.leftValue);
        }

        @Override
        public <T> Result<T> map(final ExceptionThrowingFunction<R, T> transformRight) {
            return Result.err(this.leftValue);
        }

        @Override
        public <T> Result<T> flatMap(final ExceptionThrowingFunction<R, Result<T>> transformValue) {
            return Result.err(this.leftValue);
        }

        @Override
        public int hashCode() {
            return this.leftValue.hashCode();
        }

        @Override
        public boolean equals(final Object other) {
            if (other instanceof Err<?>) {
                final Err<?> otherAsErr = (Err<?>) other;
                return this.leftValue.equals(otherAsErr.leftValue);
            } else {
                return false;
            }
        }

    }

    /**
     * The type Ok.
     *
     * @param <R> the type parameter
     */
    public static final class Ok<R> extends Result<R> {
        /**
         * holds result value.
         */
        private R rightValue;

        private Ok(final R value) {
            this.rightValue = value;
        }

        @Override
        public Exception getLeftValue() {
            throw new NoSuchElementException("Tried to getException from an Ok");
        }

        @Override
        public R getResult() {
            return rightValue;
        }

        @Override
        public boolean isErr() {
            return false;
        }

        @Override
        public boolean isOk() {
            return true;
        }

        @Override
        public <T> T fold(final Function<Exception, T> transformException, final Function<R, T> transformValue) {
            return transformValue.apply(this.rightValue);
        }

        @Override
        public int hashCode() {
            return this.rightValue.hashCode();
        }

        @Override
        public boolean equals(final Object other) {
            if (other instanceof Ok<?>) {
                final Ok<?> otherAsOk = (Ok<?>) other;
                return this.rightValue.equals(otherAsOk.rightValue);
            } else {
                return false;
            }
        }

        @Override
        public <T> Result<T> map(final ExceptionThrowingFunction<R, T> transformValue) {
            return Result.attempt(() -> transformValue.apply(this.rightValue));
        }

        @Override
        public <T> Result<T> flatMap(final ExceptionThrowingFunction<R, Result<T>> transformValue) {
            try {
                return transformValue.apply(this.rightValue);
            } catch (Exception e) {
                return new Err<T>(e);
            }
        }
    }
}
