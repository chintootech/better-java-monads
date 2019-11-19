/**
 * This package defines various useful monads.
 * <p>
 * Monads come in handy in following situations:-
 * <li>when you want to chain operations easily</li>
 * <li>when you want to apply each function regardless of the result of any of them</li>
 * <p>
 * Monad pattern based on monad from linear algebra represents the way of chaining operations together step by step.
 * Binding functions can be described as passing one's output to another's input basing on the 'same type' contract.
 * Formally, monad consists of a type constructor M and two operations: bind - that takes monadic object and a function
 * from plain object to monadic value and returns monadic value return - that takes plain type object and returns this
 * object wrapped in a monadic value.
 */
package com.chintootech.monads;
