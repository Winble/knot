package org.winble.knot.parsec;

import groovy.lang.GroovyShell;
import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
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
        assertTrue(eval("[\"123\",\"t\"].containsAll([\"t\"])"));
        assertTrue(eval("\"a\" == \"b\" || ([\"123\",\"t\"].containsAll([\"t\"]) && true)"));

        assertFalse(eval("false ? true : false"));
        assertFalse(eval("[\"123\",\"t\"].contains(\"b\")"));
        assertFalse(eval("true && \"abc\" == \"abcd\" && [\"123\",\"t\"].contains(\"t\")"));
        assertFalse(eval("true && \"abcd\" == \"abcd\" && [\"123\",\"t\"].contains(\"b\")"));

        assertTrue(eval("[\"123\",\"t\"] instanceof List ? \"abcd\" == \"abcd\" : [\"123\",\"t\"].contains(\"b\")"));
    }

    @Test
    public void testPerformance() {
        StopWatch knotWatch = new StopWatch("Knot");
        Function<String, Object> knot = loop(knotWatch, DataRuleScript::eval, 1000);
        StopWatch groovyWatch = new StopWatch("Groovy");
        GroovyShell groovyShell = new GroovyShell();
        Function<String, Object> groovy = loop(groovyWatch, groovyShell::evaluate, 1000);
        assertFuncEquals(knot, groovy, "[\"123\",\"t\"].contains(\"t\")");
        assertFuncEquals(knot, groovy, "\"a\" == \"b\" || ([\"123\",\"t\"].containsAll([\"t\"]) && true)");
        assertFuncEquals(knot, groovy, "[\"123\",\"t\"] instanceof List ? \"abcd\" == \"abcd\" : [\"123\",\"t\"].contains(\"b\")");
        System.out.println(knotWatch.prettyPrint());
        System.out.println(groovyWatch.prettyPrint());
    }

    private Function<String, Object> loop(StopWatch stopWatch, Function<String, Object> engine, int times) {
        return script -> {
            stopWatch.start(script);
            Object r = null;
            for (int i = 0; i < times; i++) {
                r = engine.apply(script);
            }
            stopWatch.stop();
            return r;
        };
    }

    private <T, R> void assertFuncEquals(Function<T, R> f1, Function<T, R> f2, T input) {
        assertEquals(f1.apply(input), f2.apply(input));
    }
}
