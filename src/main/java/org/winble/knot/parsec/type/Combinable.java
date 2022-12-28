package org.winble.knot.parsec.type;

/**
 * @author bowenzhang
 * Create on 2022/12/28
 */
public interface Combinable<T> {

    T combine(T anther);
}
