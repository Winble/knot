package org.winble.knot.parsec;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.winble.knot.parsec.DataRuleScript.eval;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author bowenzhang
 * Create on 2022/12/30
 */
public class DataRuleScriptTest {

    @Test
    public void testExpression() {
        assertTrue(eval("[\"123\",\"t\"].contains(\"t\")"));
        assertTrue(eval("\"abcd\"==\"abcd\""));
        assertTrue(eval("true && \"abcd\" == \"abcd\" && [\"123\",\"t\"].contains(\"t\")"));
    }
}
