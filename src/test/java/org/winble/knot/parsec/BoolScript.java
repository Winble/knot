package org.winble.knot.parsec;

import org.winble.knot.parsec.type.ParseResult;
import org.winble.knot.parsec.type.Parser;

import static org.winble.knot.parsec.Combinators.*;
import static org.winble.knot.parsec.Parsers.*;

/**
 * @author bowenzhang
 * Create on 2022/12/29
 * {true | false}
 * S ::= T && S | F || S | T | F | !S | (S)
 * T ::= true || S | true
 * F ::= false && S | false
 * T ::= !F
 * F ::= !T
 */
public class BoolScript {

    public static final Parser<?> ignoreSpace = isChar(' ').skipMany();

    public static final Parser<Boolean> boolExpression = defer(() -> BoolScript.script.wrap(ignoreSpace));

    public static final Parser<Boolean> trueExpression = string("true").then(string("||").wrap(ignoreSpace)).skip(boolExpression).or(string("true")).as(true);

    public static final Parser<Boolean> falseExpression = string("false").then(string("&&").wrap(ignoreSpace)).skip(boolExpression).or(string("false")).as(false);

    public static final Parser<Boolean> negateExpression = isChar('!').then(boolExpression).map(r -> !r);

    public static final Parser<Boolean> bracketExpression = isChar('(').then(boolExpression).skip(isChar(')'));

    public static final Parser<Boolean> andExpression = trueExpression.skip(string("&&").wrap(ignoreSpace)).bind(r -> r ? boolExpression : boolExpression.as(false));

    public static final Parser<Boolean> orExpression = falseExpression.skip(string("||").wrap(ignoreSpace)).bind(r -> r ? boolExpression.as(true) : boolExpression);

    public static final Parser<Boolean> script = or(negateExpression, bracketExpression, andExpression, orExpression, trueExpression, falseExpression);

    public static boolean eval(String expression) {
        expression = expression.trim();
        return script.parse(expression).get();
    }

    public static void main(String[] args) {
        ParseResult<String> result = string("true").wrap(ignoreSpace).parse(" true    ");
        System.out.println(result);
    }
}
