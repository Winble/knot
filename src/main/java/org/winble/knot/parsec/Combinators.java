package org.winble.knot.parsec;

import org.winble.knot.parsec.exception.EndOfInputException;
import org.winble.knot.parsec.exception.UnexpectedException;
import org.winble.knot.parsec.type.ParseResult;
import org.winble.knot.parsec.type.Parser;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.winble.knot.parsec.util.ParserUtils.isEnd;

/**
 * @author bowenzhang
 * Create on 2022/12/28
 */
public class Combinators {

    public static Parser<Character> satisfy(Predicate<Character> predicate) {
        return input -> {
            if (isEnd(input)) {
                return ParseResult.failure(new EndOfInputException(input));
            }
            char c = input.charAt(0);
            return predicate.test(c) ? ParseResult.succeed(c, input.substring(1)): ParseResult.failure(new UnexpectedException(c, input));
        };
    }

    public static <R1, R2> Parser<R2> bind(Parser<R1> p, Function<R1, Parser<R2>> flatMap) {
        return input -> {
            ParseResult<R1> r1 = p.parse(input);
            return r1.isSuccess() ? flatMap.apply(r1.getResult()).parse(r1.getRemain()) : ParseResult.failure(r1.getError());
        };
    }

    public static <R1, R2> Parser<R2> map(Parser<R1> p, Function<R1, R2> mapper) {
        return input -> {
            ParseResult<R1> r1 = p.parse(input);
            return r1.isSuccess() ? ParseResult.succeed(mapper.apply(r1.getResult()), r1.getRemain()) : ParseResult.failure(r1.getError());
        };
    }

    public static <R1, R2> Parser<R2> then(Parser<R1> p1, Parser<R2> p2) {
        return bind(p1, ignore -> p2);
    }

    public static <R> Parser<R> or(Parser<R> left, Parser<R> right) {
        return input -> {
            ParseResult<R> r = left.parse(input);
            return r.isSuccess() ? r : right.parse(input);
        };
    }

    @SafeVarargs
    public static <R> Parser<R> or(Parser<R> p1, Parser<R> p2, Parser<R>... ps) {
        return Arrays.stream(ps).reduce(p1.or(p2), Combinators::or);
    }

    public static <R> Parser<R> defer(Supplier<Parser<R>> supplier) {
        return input -> supplier.get().parse(input);
    }
}
