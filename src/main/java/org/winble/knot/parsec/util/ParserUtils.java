package org.winble.knot.parsec.util;

import org.winble.knot.parsec.type.Pair;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author bowenzhang
 * Create on 2022/12/28
 */
public class ParserUtils {

    public static boolean isEnd(String input) {
        return null == input || input.length() == 0;
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

    public static <L, R, T> Function<Pair<L, R>, T> invoke(BiFunction<L, R, T> func) {
        return pair -> func.apply(pair.getLeft(), pair.getRight());
    }

    public static String charsToString(Collection<Character> chs) {
        return chs.isEmpty() ? "" : chs.stream().map(String::valueOf).collect(Collectors.joining());
    }
}
