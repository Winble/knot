package org.winble.knot.parsec.type;

import org.winble.knot.parsec.Combinators;

import java.util.function.Function;

/**
 * @author bowenzhang
 * Create on 2022/12/28
 */
@FunctionalInterface
public interface Parser<R> {

    ParseResult<R> parse(String input);

    default <V> Parser<V> bind(Function<R, Parser<V>> flatMap) {
        return Combinators.bind(this, flatMap);
    }

    default <V> Parser<V> map(Function<R, V> mapper) {
        return Combinators.map(this, mapper);
    }

    default Parser<R> or(Parser<R> otherwise) {
        return Combinators.or(this, otherwise);
    }
}
