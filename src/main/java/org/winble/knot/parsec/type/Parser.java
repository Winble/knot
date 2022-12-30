package org.winble.knot.parsec.type;

import org.winble.knot.parsec.Combinators;
import org.winble.knot.parsec.Parsers;

import java.util.List;
import java.util.function.BiFunction;
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

    default <V> Parser<V> then(Parser<V> p) {
        return Combinators.then(this, p);
    }

    default Parser<List<R>> many() {
        return Combinators.many(this);
    }

    default Parser<List<R>> until(Parser<?> check) {
        return Combinators.until(this, check);
    }

    default <V> Parser<V> map(Function<R, V> mapper) {
        return Combinators.map(this, mapper);
    }

    default <V> Parser<V> as(V value) {
        return Combinators.map(this, r -> value);
    }

    default <V> Parser<V> ignore() {
        return Combinators.map(this, r -> null);
    }

    @SuppressWarnings("unchecked")
    default <V> Parser<V> map() {
        return Combinators.map(this, r -> (V) r);
    }

    default Parser<R> or(Parser<R> p) {
        return Combinators.or(this, p);
    }

    default Parser<R> and(Parser<R> p) {
        return Combinators.and(this, p);
    }

    default Parser<R> skip(Parser<?> p) {
        return Combinators.skip(this, p);
    }

    default Parser<R> skipMany(Parser<?> p) {
        return Combinators.skipMany(this, p);
    }

    default Parser<R> skipMany() {
        return Combinators.skipMany(this);
    }

    default Parser<R> skip() {
        return Combinators.skip(this, Parsers.next());
    }

    default <V> Parser<Pair<R, V>> union(Parser<V> p) {
        return Combinators.union(this, p);
    }

    default Parser<R> wrap(Parser<?> p) {
        return Combinators.wrap(this, p);
    }
}
