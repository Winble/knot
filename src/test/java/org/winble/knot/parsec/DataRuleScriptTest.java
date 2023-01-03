package org.winble.knot.parsec;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.winble.knot.parsec.DataRuleScript.eval;

/**
 * @author bowenzhang
 * Create on 2022/12/30
 */
public class DataRuleScriptTest {

    @Test
    public void testExpression() {
        assertTrue(eval("[\"123\",\"t\"].contains(\"t\")"));
        assertTrue(eval("\"abcd\" == \"abcd\""));
        assertTrue(eval("true && \"abcd\" == \"abcd\" && [\"123\",\"t\"].contains(\"t\")"));
        assertTrue(eval("true ? true : false"));

        assertFalse(eval("false ? true : false"));
        assertFalse(eval("[\"123\",\"t\"].contains(\"b\")"));
        assertFalse(eval("true && \"abc\" == \"abcd\" && [\"123\",\"t\"].contains(\"t\")"));
        assertFalse(eval("true && \"abcd\" == \"abcd\" && [\"123\",\"t\"].contains(\"b\")"));

        assertTrue(eval("[\"123\",\"t\"] instanceof List ? \"abcd\" == \"abcd\" : [\"123\",\"t\"].contains(\"b\")"));
    }
}
