package org.winble.knot.parsec.util;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author bowenzhang
 * Create on 2022/12/28
 */
public class ParserUtils {

    public static boolean isEnd(String input) {
        return null == input || input.length() == 0 || input.trim().isEmpty();
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> List<T> merge(Collection<T>... values) {
        if (null == values) {
            return Collections.emptyList();
        }
        return Arrays.stream(values).filter(Objects::nonNull).flatMap(Collection::stream)
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static <T> List<T> merge(T v, Collection<T> vs) {
        return merge(Collections.singleton(v), vs);
    }
}
