package com.chintootech.monads;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The type Either.
 *
 * @param <L> the type parameter
 * @param <R> the type parameter
 */
public abstract class Either<L, R> {

    /**
     * Either either.
     *
     * @param <L>           the type parameter
     * @param <R>           the type parameter
     * @param leftSupplier  the left supplier
     * @param rightSupplier the right supplier
     * @return the either
     */
    public static <L, R> Either<L, R> either(Supplier<L> leftSupplier, Supplier<R> rightSupplier) {
        R rightValue = rightSupplier.get();
        if (rightValue != null) {
            return Either.right(rightValue);
        } else {
            return Either.left(leftSupplier.get());
        }
    }

    /**
     * Left either.
     *
     * @param <L>  the type parameter
     * @param <R>  the type parameter
     * @param left the left
     * @return the either
     */
    public static <L, R> Either<L, R> left(L left) {
        return new Left<>(left);
    }

    /**
     * Right either.
     *
     * @param <L>   the type parameter
     * @param <R>   the type parameter
     * @param right the right
     * @return the either
     */
    public static <L, R> Either<L, R> right(R right) {
        return new Right<>(right);
    }

    /**
     * Returns the left value.
     *
     * <pre>{@code
     * // prints "error"
     * System.out.println(Either.left("error").getLeft());
     *
     * // throws NoSuchElementException
     * System.out.println(Either.right(42).getLeft());
     * }</pre>
     *
     * @return The left value.
     * @throws NoSuchElementException if this is a {@code Right}.
     */
    public abstract L getLeft();

    /**
     * Gets right.
     *
     * @return the right
     */
    public abstract R getRight();

    /**
     * Returns whether this Either is a Left.
     *
     * <pre>{@code
     * // prints "true"
     * System.out.println(Either.left("error").isLeft());
     *
     * // prints "false"
     * System.out.println(Either.right(42).isLeft());
     * }</pre>
     *
     * @return true, if this is a Left, false otherwise
     */
    public abstract boolean isLeft();

    /**
     * Returns whether this Either is a Right.
     *
     * <pre>{@code
     * // prints "true"
     * System.out.println(Either.right(42).isRight());
     *
     * // prints "false"
     * System.out.println(Either.left("error").isRight());
     * }</pre>
     *
     * @return true, if this is a Right, false otherwise
     */
    public abstract boolean isRight();

    /**
     * Converts a {@code Left} to a {@code Right} vice versa by wrapping the value in a new type.
     *
     * @return a new {@code Either}
     */
    public final Either<R, L> swap() {
        if (isRight()) {
            return new Left<>(getRight());
        } else {
            return new Right<>(getLeft());
        }
    }

    /**
     * Folds either the left or the right side of this disjunction.
     *
     * <pre>{@code
     * Either<Exception, Integer> success = Either.right(3);
     *
     * // prints "Users updated: 3"
     * System.out.println(success.fold(Exception::getMessage, count -> "Users updated: " + count));
     *
     * Either<Exception, Integer> failure = Either.left(new Exception("Failed to update users"));
     *
     * // prints "Failed to update users"
     * System.out.println(failure.fold(Exception::getMessage, count -> "Users updated: " + count));
     * }</pre>
     *
     * @param <T>            ttype of the folded value
     * @param transformLeft  transforms left value if this is a Left
     * @param transformRight transforms right value if this is a Right
     * @return A value of type U
     */
    public abstract <T> T fold(Function<L, T> transformLeft, Function<R, T> transformRight);

    /**
     * Maps either the left or the right side of this disjunction.
     *
     * <pre>{@code
     * Either<?, AtomicInteger> success = Either.right(new AtomicInteger(42));
     *
     * // prints "Right(42)"
     * System.out.println(success.bimap(Function1.identity(), AtomicInteger::get));
     *
     * Either<Exception, ?> failure = Either.left(new Exception("error"));
     *
     * // prints "Left(error)"
     * System.out.println(failure.bimap(Exception::getMessage, Function1.identity()));
     * }</pre>
     *
     * @param leftMapper  maps the left value if this is a Left
     * @param rightMapper maps the right value if this is a Right
     * @param <X>         The new left type of the resulting Either
     * @param <Y>         The new right type of the resulting Either
     * @return A new Either instance
     */
    public final <X, Y> Either<X, Y> bimap(Function<? super L, ? extends X> leftMapper, Function<? super R, ? extends Y> rightMapper) {
        Objects.requireNonNull(leftMapper, "leftMapper is null");
        Objects.requireNonNull(rightMapper, "rightMapper is null");
        if (isRight()) {
            return new Right<>(rightMapper.apply(getRight()));
        } else {
            return new Left<>(leftMapper.apply(getLeft()));
        }
    }

    /**
     * Map either.
     *
     * @param <T>            the type parameter
     * @param <U>            the type parameter
     * @param transformLeft  the transform left
     * @param transformRight the transform right
     * @return the either
     */
    public abstract <T, U> Either<T, U> map(Function<L, T> transformLeft, Function<R, U> transformRight);

    /**
     * Run.
     *
     * @param runLeft  the run left
     * @param runRight the run right
     */
    public abstract void run(Consumer<L> runLeft, Consumer<R> runRight);

    /**
     * The type Left.
     *
     * @param <L> the type parameter
     * @param <R> the type parameter
     */
    public static class Left<L, R> extends Either<L, R> {

        /**
         * The Left value.
         */
        protected L leftValue;

        private Left(L left) {
            this.leftValue = left;
        }

        @Override
        public L getLeft() {
            return this.leftValue;
        }

        @Override
        public R getRight() {
            throw new NoSuchElementException("Tried to getRight from a Left");
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public boolean isRight() {
            return false;
        }

        @Override
        public <T> T fold(Function<L, T> transformLeft, Function<R, T> transformRight) {
            Objects.requireNonNull(transformLeft, "transformLeft is null");
            Objects.requireNonNull(transformRight, "transformRight is null");
            return transformLeft.apply(this.leftValue);
        }

        @Override
        public <T, U> Either<T, U> map(Function<L, T> transformLeft, Function<R, U> transformRight) {
            return Either.left(transformLeft.apply(this.leftValue));
        }

        @Override
        public void run(Consumer<L> runLeft, Consumer<R> runRight) {
            runLeft.accept(this.leftValue);
        }


        @Override
        public int hashCode() {
            return this.leftValue.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof Left<?, ?>) {
                final Left<?, ?> otherAsLeft = (Left<?, ?>) other;
                return this.leftValue.equals(otherAsLeft.leftValue);
            } else {
                return false;
            }
        }

    }

    /**
     * The type Right.
     *
     * @param <L> the type parameter
     * @param <R> the type parameter
     */
    public static class Right<L, R> extends Either<L, R> {

        /**
         * The Right value.
         */
        protected R rightValue;

        private Right(R right) {
            this.rightValue = right;
        }

        @Override
        public L getLeft() {
            throw new NoSuchElementException("Tried to getLeft from a Right");
        }

        @Override
        public R getRight() {
            return rightValue;
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public boolean isRight() {
            return true;
        }

        @Override
        public <T> T fold(Function<L, T> transformLeft, Function<R, T> transformRight) {
            Objects.requireNonNull(transformLeft, "transformLeft is null");
            Objects.requireNonNull(transformRight, "transformRight is null");

            return transformRight.apply(this.rightValue);
        }

        @Override
        public <T, U> Either<T, U> map(Function<L, T> transformLeft, Function<R, U> transformRight) {
            return Either.right(transformRight.apply(this.rightValue));
        }

        @Override
        public void run(Consumer<L> runLeft, Consumer<R> runRight) {
            runRight.accept(this.rightValue);
        }


        @Override
        public int hashCode() {
            return this.rightValue.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof Right<?, ?>) {
                final Right<?, ?> otherAsRight = (Right<?, ?>) other;
                return this.rightValue.equals(otherAsRight.rightValue);
            } else {
                return false;
            }
        }

    }
}
