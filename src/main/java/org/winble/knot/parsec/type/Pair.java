package org.winble.knot.parsec.type;

/**
 * @author bowenzhang
 * Create on 2022/12/28
 */
public class Pair<L, R> {

    private L left;

    private R right;

    public static <L, R> Pair<L, R> of(L left, R right) {
        Pair<L, R> pair = new Pair<>();
        pair.left = left;
        pair.right = right;
        return pair;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }
}
