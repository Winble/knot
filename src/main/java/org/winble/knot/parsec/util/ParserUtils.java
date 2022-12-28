package org.winble.knot.parsec.util;

/**
 * @author bowenzhang
 * Create on 2022/12/28
 */
public class ParserUtils {

    public static boolean isEnd(String input) {
        return null == input || input.length() == 0 || input.trim().isEmpty();
    }
}
