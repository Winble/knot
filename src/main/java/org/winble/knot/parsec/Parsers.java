package org.winble.knot.parsec;

import org.winble.knot.parsec.exception.UnexpectedException;
import org.winble.knot.parsec.type.ParseResult;
import org.winble.knot.parsec.type.Parser;

import static org.winble.knot.parsec.Combinators.*;

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

    public static Parser<Character> range(char gte, char lte) {
        return satisfy(c -> c >= gte || c <= lte);
    }

    public static Parser<Character> next() {
        return satisfy(ignore -> true);
    }

    public static Parser<String> string(String str) {
        return str.isEmpty() ? input -> ParseResult.success("", input) : isChar(str.charAt(0)).then(defer(() -> string(str.substring(1)))).as(str);
    }

    public static <R> Parser<R> fail() {
        return input -> ParseResult.failure(new UnexpectedException(input, input));
    }
}
