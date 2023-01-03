package org.winble.knot.parsec;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.winble.knot.parsec.exception.UnexpectedException;

import static org.winble.knot.parsec.BoolScript.eval;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author bowenzhang
 * Create on 2022/12/29
 */
public class BoolScriptTest {

    @Test
    public void testExpression() {
        assertTrue(eval("true"));
        assertTrue(eval("!false"));
        assertTrue(eval("!!true"));
        assertTrue(eval("true && true"));
        assertTrue(eval("true || false || true"));
        assertTrue(eval("!false && true"));

        assertFalse(eval("false"));
        assertFalse(eval("!true"));
        assertFalse(eval("!!false"));
        assertFalse(eval("false || false"));
        assertFalse(eval("false && true && false"));
        assertFalse(eval("!true || false"));
        assertFalse(eval("true && true && false"));

        assertTrue(eval("false || !(true && false)"));
        assertTrue(eval("true && (false || true)  "));
        assertThrows(UnexpectedException.class, () -> eval("true && (false || true)  any  "));
    }
}
