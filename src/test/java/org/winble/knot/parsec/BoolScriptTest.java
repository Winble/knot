package org.winble.knot.parsec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.winble.knot.parsec.BoolScript.eval;

/**
 * @author bowenzhang
 * Create on 2022/12/29
 */
public class BoolScriptTest {

    @Test
    public void testExpression() {
        Assertions.assertTrue(eval("true"));
        Assertions.assertTrue(eval("!false"));
        Assertions.assertTrue(eval("!!true"));
        Assertions.assertTrue(eval("true && true"));
        Assertions.assertTrue(eval("true || false || true"));
        Assertions.assertTrue(eval("!false && true"));

        Assertions.assertFalse(eval("false"));
        Assertions.assertFalse(eval("!true"));
        Assertions.assertFalse(eval("!!false"));
        Assertions.assertFalse(eval("false || false"));
        Assertions.assertFalse(eval("false && true && false"));
        Assertions.assertFalse(eval("!true || false"));

        Assertions.assertTrue(eval("false || !(true && false)"));
        Assertions.assertTrue(eval("true && (false || true)"));
    }
}
