package com.chintootech.monads;

import com.chintootech.monads.Try.Success.Failure;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.chintootech.monads.TryModule.sneakyThrow;

/**
 * Monadic Try type.
 * Represents a result type that could have succeeded with type T or failed with a Throwable.
 * Originally was Exception but due to seeing issues with eg play with checked Throwable,
 * And also seeing that Scala deals with throwable,
 * I made the decision to change it to use Throwable.
 *
 * @param <T> the type parameter
 */
public abstract class Try<T> {

    /**
     * Instantiates a new Try.
     */
    protected Try() {
    }

    /**
     * Creates try of TrySupplier.
     *
     * @param <U>      the type parameter
     * @param supplier the supplier
     * @return {@code Success(supplier.get())} if no exception occurs, otherwise {@code Failure(throwable)} if an
     * exception occurs calling {@code supplier.get()}.
     */
    public static <U> Try<U> ofFailable(final TrySupplier<U> supplier) {
        Objects.requireNonNull(supplier, "supplier is null");

        try {
            return Try.successful(supplier.get());
        } catch (Throwable t) {
            return Try.failure(t);
        }
    }

    /**
     * Factory method for failure.
     *
     * @param <U> Type
     * @param e   throwable to create the failed Try with
     * @return a new Failure
     */
    public static <U> Try<U> failure(final Throwable e) {
        return new Failure<>(e);
    }

    /**
     * Factory method for success.
     *
     * @param <U> Type
     * @param x   value to create the successful Try with
     * @return a new Success
     */
    public static <U> Try<U> successful(final U x) {
        return new Success<>(x);
    }

    /**
     * Transform success or pass on failure.
     * Takes an optional type parameter of the new type.
     * You need to be specific about the new type if changing type
     *
     * <p>Try.ofFailable(() -&gt; "1").&lt;Integer&gt;map((x) -&gt; Integer.valueOf(x))
     *
     * @param <U> new type (optional)
     * @param f   function to apply to successful value.
     * @return Success &lt;U&gt; or Failure&lt;U&gt;
     */
    public abstract <U> Try<U> map(TryMapFunction<? super T, ? extends U> f);

    /**
     * Transform success or pass on failure, taking a Try&lt;U&gt; as the result.
     * Takes an optional type parameter of the new type.
     * You need to be specific about the new type if changing type.
     * <p>
     * Try.ofFailable(() -&gt; "1").&lt;Integer&gt;flatMap((x) -&gt; Try.ofFailable(() -&gt; Integer.valueOf(x)))
     * returns Integer(1)
     *
     * @param <U> new type (optional)
     * @param f   function to apply to successful value.
     * @return new composed Try
     */
    public abstract <U> Try<U> flatMap(TryMapFunction<? super T, Try<U>> f);

    /**
     * Specifies a result to use in case of failure.
     * Gives access to the exception which can be pattern matched on.
     * <p>
     * Try.ofFailable(() -&gt; "not a number")
     * .&lt;Integer&gt;flatMap((x) -&gt; Try.ofFailable(() -&gt;Integer.valueOf(x)))
     * .recover((t) -&gt; 1)
     * returns Integer(1)
     *
     * @param f function to execute on successful result.
     * @return new composed Try
     */
    public abstract T recover(Function<? super Throwable, T> f);

    /**
     * Try applying f(t) on the case of failure.
     *
     * @param f function that takes throwable and returns result
     * @return a new Try in the case of failure, or the current Success.
     */
    public abstract Try<T> recoverWith(TryMapFunction<? super Throwable, Try<T>> f);

    /**
     * Return a value in the case of a failure.
     * This is similar to recover but does not expose the exception type.
     *
     * @param value return the try's value or else the value specified.
     * @return new composed Try
     */
    public abstract T orElse(T value);

    /**
     * Return another try in the case of failure.
     * Like recoverWith but without exposing the exception.
     *
     * @param f return the value or the value from the new try.
     * @return new composed Try
     */
    public abstract Try<T> orElseTry(TrySupplier<T> f);

    /**
     * Gets the value T on Success or throws the cause of the failure.
     *
     * @param <X>               the type parameter
     * @param exceptionSupplier the exception supplier
     * @return T t
     * @throws X the x
     */
    public abstract <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X;

    /**
     * Gets the value T on Success or throws the cause of the failure.
     *
     * @return T t
     */
    public abstract T get();

    /**
     * Gets the value T on Success or throws the cause of the failure wrapped into a RuntimeException.
     *
     * @return T unchecked
     * @throws RuntimeException
     */
    public abstract T getUnchecked();

    /**
     * Is success boolean.
     *
     * @return the boolean
     */
    public abstract boolean isSuccess();

    /**
     * Performs the provided action, when successful.
     *
     * @param <E>    the type parameter
     * @param action action to run
     * @return new composed Try
     * @throws E if the action throws an exception
     */
    public abstract <E extends Throwable> Try<T> onSuccess(TryConsumer<T, E> action) throws E;

    /**
     * Performs the provided action, when failed.
     *
     * @param <E>    the type parameter
     * @param action action to run
     * @return new composed Try
     * @throws E if the action throws an exception
     */
    public abstract <E extends Throwable> Try<T> onFailure(TryConsumer<Throwable, E> action) throws E;

    /**
     * If a Try is a Success and the predicate holds true, the Success is passed further.
     * Otherwise (Failure or predicate doesn't hold), pass Failure.
     *
     * @param pred predicate applied to the value held by Try
     * @return For Success, the same success if predicate holds true, otherwise Failure
     */
    public abstract Try<T> filter(Predicate<T> pred);

    /**
     * Try contents wrapped in Optional.
     *
     * @return Optional of T, if Success, Empty if Failure or null value
     */
    public abstract Optional<T> toOptional();

    public abstract Throwable getCause();

    /**
     * Folds either the {@code Failure} or the {@code Success} side of the Try value.
     *
     * @param ifFail maps the left value if this is a {@code Failure}
     * @param f      maps the value if this is a {@code Success}
     * @param <X>    type of the folded value
     * @return A value of type X
     */
    public final <X> X fold(Function<? super Throwable, ? extends X> ifFail, Function<? super T, ? extends X> f) {
        if (isSuccess()) {
            return f.apply(get());
        } else {
            return ifFail.apply(getCause());
        }
    }

    /**
     * The type Success.
     *
     * @param <T> the type parameter
     */
    static class Success<T> extends Try<T> {
        /**
         * represents success value of type T.
         */
        private final T value;

        /**
         * Instantiates a new Success.
         *
         * @param value the value
         */
        Success(final T value) {
            this.value = value;
        }

        @Override
        public <U> Try<U> flatMap(final TryMapFunction<? super T, Try<U>> f) {
            Objects.requireNonNull(f);
            try {
                return f.apply(value);
            } catch (Throwable t) {
                return Try.failure(t);
            }
        }

        @Override
        public T recover(final Function<? super Throwable, T> f) {
            Objects.requireNonNull(f);
            return value;
        }

        @Override
        public Try<T> recoverWith(final TryMapFunction<? super Throwable, Try<T>> f) {
            Objects.requireNonNull(f);
            return this;
        }

        @Override
        public T orElse(T value) {
            return isSuccess() ? this.value : value;
        }

        @Override
        public Try<T> orElseTry(final TrySupplier<T> f) {
            Objects.requireNonNull(f);
            return this;
        }

        @Override
        public <X extends Throwable> T orElseThrow(final Supplier<? extends X> exceptionSupplier) throws X {
            return value;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public T getUnchecked() {
            return value;
        }

        @Override
        public <U> Try<U> map(final TryMapFunction<? super T, ? extends U> f) {
            Objects.requireNonNull(f);
            try {
                return new Success<>(f.apply(value));
            } catch (Throwable t) {
                return Try.failure(t);
            }
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public <E extends Throwable> Try<T> onSuccess(final TryConsumer<T, E> action) throws E {
            action.accept(value);
            return this;
        }

        @Override
        public Try<T> filter(final Predicate<T> p) {
            Objects.requireNonNull(p);

            if (p.test(value)) {
                return this;
            } else {
                return Try.failure(new NoSuchElementException("Predicate does not match for " + value));
            }
        }

        @Override
        public Optional<T> toOptional() {
            return Optional.ofNullable(value);
        }

        @Override
        public Throwable getCause() {
            throw new UnsupportedOperationException("getCause on Success");
        }

        @Override
        public <E extends Throwable> Try<T> onFailure(final TryConsumer<Throwable, E> action) {
            return this;
        }


        /**
         * The type Failure.
         *
         * @param <T> the type parameter
         */
        static class Failure<T> extends Try<T> {
            /**
             * represents failure value of throwable.
             */
            private final Throwable e;

            /**
             * Instantiates a new Failure.
             *
             * @param e the e
             */
            Failure(final Throwable e) {
                this.e = e;
            }

            @Override
            public <U> Try<U> map(final TryMapFunction<? super T, ? extends U> f) {
                Objects.requireNonNull(f);
                return Try.failure(e);
            }

            @Override
            public <U> Try<U> flatMap(final TryMapFunction<? super T, Try<U>> f) {
                Objects.requireNonNull(f);
                return Try.failure(e);
            }

            @Override
            public T recover(final Function<? super Throwable, T> f) {
                Objects.requireNonNull(f);
                return f.apply(e);
            }

            @Override
            public Try<T> recoverWith(final TryMapFunction<? super Throwable, Try<T>> f) {
                Objects.requireNonNull(f);
                try {
                    return f.apply(e);
                } catch (Throwable t) {
                    return Try.failure(t);
                }
            }

            @Override
            public T orElse(final T value) {
                return value;
            }

            @Override
            public Try<T> orElseTry(final TrySupplier<T> f) {
                Objects.requireNonNull(f);
                return Try.ofFailable(f);
            }

            @Override
            public <X extends Throwable> T orElseThrow(final Supplier<? extends X> exceptionSupplier) throws X {
                throw exceptionSupplier.get();
            }

            @Override
            public T get() {
                return sneakyThrow(e);
            }

            @Override
            public T getUnchecked() {
                throw new RuntimeException(e);
            }

            @Override
            public boolean isSuccess() {
                return false;
            }

            @Override
            public <E extends Throwable> Try<T> onSuccess(final TryConsumer<T, E> action) {
                return this;
            }

            @Override
            public Try<T> filter(final Predicate<T> pred) {
                return this;
            }

            @Override
            public Optional<T> toOptional() {
                return Optional.empty();
            }

            @Override
            public Throwable getCause() {
                return e;
            }

            @Override
            public <E extends Throwable> Try<T> onFailure(final TryConsumer<Throwable, E> action) throws E {
                action.accept(e);
                return this;
            }
        }
    }
}
