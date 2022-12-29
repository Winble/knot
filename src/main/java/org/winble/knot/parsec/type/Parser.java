package org.winble.knot.parsec.type;

import org.winble.knot.parsec.Combinators;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

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

    default <V> Parser<V> then(Parser<V> then) {
        return Combinators.then(this, then);
    }

    default Parser<List<R>> many() {
        return Combinators.many(this);
    }

    default Parser<List<R>> until(Predicate<String> check) {
        return Combinators.until(this, check);
    }

    default <V> Parser<V> map(Function<R, V> mapper) {
        return Combinators.map(this, mapper);
    }

    default <V> Parser<V> map(V value) {
        return Combinators.map(this, r -> value);
    }

    default Parser<R> or(Parser<R> otherwise) {
        return Combinators.or(this, otherwise);
    }

    default Parser<R> skip(Parser<?> peek) {
        return Combinators.skip(this, peek);
    }
}
