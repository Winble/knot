package org.winble.knot.parsec.exception;

/**
 * @author bowenzhang
 * Create on 2022/12/28
 */
public class ParseException extends RuntimeException {

    private final String input;

    public ParseException(String input) {
        this.input = input;
    }
}
