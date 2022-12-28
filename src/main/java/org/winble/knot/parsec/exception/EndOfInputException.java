package org.winble.knot.parsec.exception;

/**
 * @author bowenzhang
 * Create on 2022/12/28
 */
public class EndOfInputException extends ParseException {
    public EndOfInputException(String input) {
        super(input);
    }
}
