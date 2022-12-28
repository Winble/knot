package org.winble.knot.parsec.type;

import org.winble.knot.parsec.exception.ParseException;

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

    public static <R> ParseResult<R> succeed(R result, String remain) {
        return new ParseResult<>(result, remain, null);
    }

    public boolean isSuccess() {
        return null == error;
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

    @Override
    public String toString() {
        return "ParseResult{" +
                "result=" + result +
                ", remain='" + remain + '\'' +
                ", error=" + error +
                '}';
    }
}
