package org.winble.knot.parsec;

import org.winble.knot.parsec.exception.EndOfInputException;
import org.winble.knot.parsec.exception.UnexpectedException;
import org.winble.knot.parsec.type.Pair;
import org.winble.knot.parsec.type.ParseResult;
import org.winble.knot.parsec.type.Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.winble.knot.parsec.util.ParserUtils.*;

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
            return predicate.test(c) ? ParseResult.success(c, input.substring(1)): ParseResult.failure(new UnexpectedException(c, input));
        };
    }

    public static <R1, R2> Parser<R2> bind(Parser<R1> p, Function<R1, Parser<R2>> flatMap) {
        return input -> p.parse(input).ifSuccess(r -> flatMap.apply(r.getResult()).parse(r.getRemain()));
    }

    public static <R1, R2> Parser<R2> map(Parser<R1> p, Function<R1, R2> mapper) {
        return input -> p.parse(input).ifSuccess(r -> ParseResult.success(mapper.apply(r.getResult()), r.getRemain()));
    }

    public static <R1, R2> Parser<R2> then(Parser<R1> p1, Parser<R2> p2) {
        return bind(p1, ignore -> p2);
    }

    public static <R> Parser<R> skip(Parser<R> p1, Parser<?> p2) {
        return p1.bind(p2::as);
    }

    public static <R> Parser<R> skipMany(Parser<R> p1, Parser<?> p2) {
        return p1.bind(p2.many()::as);
    }

    public static <R> Parser<R> skipMany(Parser<R> p) {
        return skipMany(input -> ParseResult.success(null, input), p);
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

    public static <R> Parser<R> and(Parser<R> p1, Parser<R> p2) {
        return input -> p1.parse(input).ifSuccess(r -> p2.parse(input));
    }

    public static <R1, R2> Parser<Pair<R1, R2>> union(Parser<R1> p1, Parser<R2> p2) {
        return p1.bind(r1 -> p2.map(r2 -> Pair.of(r1, r2)));
    }

    public static <R> Parser<R> defer(Supplier<Parser<R>> supplier) {
        return input -> supplier.get().parse(input);
    }

    public static <R> Parser<R> either(Predicate<String> whether, Parser<R> success, Parser<R> failure) {
        return input -> whether.test(input) ? success.parse(input) : failure.parse(input);
    }

    public static <R> Parser<List<R>> many(Parser<R> p) {
        return input -> {
            ParseResult<R> r = p.parse(input);
            if (r.isSuccess()) {
                ParseResult<List<R>> rs = many(p).parse(r.getRemain());
                return ParseResult.success(merge(r.getResult(), rs.getResult()), rs.getRemain());
            } else {
                return ParseResult.success(new ArrayList<>(0), input);
            }
        };
    }

    public static <R> Parser<List<R>> until(Parser<R> p, Parser<?> peek) {
        return either(input -> !peek.parse(input).isSuccess(), p, Parsers.fail()).many();
    }

    public static <R> Parser<List<R>> when(Parser<R> p, Parser<?> peek) {
        return either(input -> peek.parse(input).isSuccess(), p, Parsers.fail()).many();
    }

    public static <R1> Parser<R1> wrap(Parser<R1> p1, Parser<?> p2) {
        return p2.then(p1).skip(p2);
    }

    public static <R> Parser<R> optional(Parser<R> p) {
        return input -> {
            ParseResult<R> result = p.parse(input);
            if (result.isSuccess()) {
                return result;
            } else {
                return ParseResult.success(null, input);
            }
        };
    }
}
