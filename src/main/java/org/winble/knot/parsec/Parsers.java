package org.winble.knot.parsec;

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

    public static void main(String[] args) {
        ParseResult<Character> result = or(Parsers.isChar('a'), Parsers.isChar('b'), Parsers.isChar('c')).parse("bca");
        System.out.println(result);
    }
}
