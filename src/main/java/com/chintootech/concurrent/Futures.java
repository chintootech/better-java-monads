package com.chintootech.concurrent;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * The type Futures.
 */
public final class Futures {
    private Futures() {
    }

    /**
     * Convert List of CompletableFutures to CompletableFuture with a List.
     *
     * @param <T>     type
     * @param futures List of Futures
     * @return CompletableFuture with a List
     */
    public static <T> CompletableFuture<List<T>> sequence(final List<CompletableFuture<T>> futures) {
        return CompletableFuture.
                allOf(futures.toArray(new CompletableFuture[futures.size()])).
                thenApply(v ->
                        futures.stream().
                                map(CompletableFuture::join).
                                collect(Collectors.toList())
                );
    }
}
