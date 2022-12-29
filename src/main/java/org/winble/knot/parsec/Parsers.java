package org.winble.knot.parsec;

import org.winble.knot.parsec.exception.UnexpectedException;
import org.winble.knot.parsec.type.ParseResult;
import org.winble.knot.parsec.type.Parser;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.winble.knot.parsec.Combinators.*;
import static org.winble.knot.parsec.util.ParserUtils.*;

/**
 * @author bowenzhang
 * Create on 2022/12/28
 */
public class Parsers {

    public static Parser<Character> isChar(char c) {
        return satisfy(ch -> c == ch);
    }

    public static Parser<Character> notChar(char c) {
        return satisfy(ch -> c != ch);
    }

    public static Parser<Character> next() {
        return satisfy(ignore -> true);
    }

    public static <V> Parser<V> equal(Supplier<V> supplier) {
        return input -> ParseResult.success(supplier.get(), input);
    }

    public static <V> Parser<V> equal(V value) {
        return equal(() -> value);
    }

    public static Parser<String> string(String str) {
        return isEnd(str) ? input -> ParseResult.success(str, input) : isChar(str.charAt(0)).then(defer(() -> string(str.substring(1))));
    }

    public static <R> Parser<R> fail() {
        return input -> ParseResult.failure(new UnexpectedException(input, input));
    }
}
