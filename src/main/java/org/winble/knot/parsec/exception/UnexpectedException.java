package org.winble.knot.parsec.exception;

/**
 * @author bowenzhang
 * Create on 2022/12/28
 */
public class UnexpectedException extends ParseException {

    private final Object o;

    public UnexpectedException(Object o, String input) {
        super(input);
        this.o = o;
    }

    @Override
    public String toString() {
        return "UnexpectedException{" +
                "o=" + o +
                "} " + super.toString();
    }
}
