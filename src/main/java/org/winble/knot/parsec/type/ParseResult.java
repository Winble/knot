package org.winble.knot.parsec.type;

import org.winble.knot.parsec.Parsers;
import org.winble.knot.parsec.exception.EndOfInputException;
import org.winble.knot.parsec.exception.ParseException;
import org.winble.knot.parsec.util.ParserUtils;

import java.util.function.Function;

/**
 * @author bowenzhang
 * Create on 2022/12/28
 */
public class ParseResult<R> {

    R result;

    String remain;

    ParseException error;

    public ParseResult() {
    }

    public ParseResult(R result, String remain, ParseException error) {
        this.result = result;
        this.remain = remain;
        this.error = error;
    }

    public static <R> ParseResult<R> failure(ParseException error) {
        return new ParseResult<>(null, null, error);
    }

    public static <R> ParseResult<R> success(R result, String remain) {
        return new ParseResult<>(result, remain, null);
    }

    public boolean isSuccess() {
        return null == error;
    }

    public <V> ParseResult<V> ifSuccess(Function<ParseResult<R>, ParseResult<V>> mapper) {
        return isSuccess() ? mapper.apply(this) : failure(this.getError());
    }

    public ParseResult<R> ifFailure(Function<ParseResult<R>, ParseResult<R>> orElse) {
        return isSuccess() ? this : orElse.apply(this);
    }

    public ParseResult<R> ifEnded(R r) {
        return error instanceof EndOfInputException || ParserUtils.isEnded(remain) ? success(r, null) : this;
    }

    public R getResult() {
        return result;
    }

    public String getRemain() {
        return remain;
    }

    public ParseException getError() {
        return error;
    }

    public R get() {
        if (!isSuccess()) {
            throw error;
        }
        return result;
    }

    @Override
    public String toString() {
        return "ParseResult{" +
                "result=" + result +
                ", remain='" + remain + '\'' +
                ", error=" + error +
                '}';
    }
}
