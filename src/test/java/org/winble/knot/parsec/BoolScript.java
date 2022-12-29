package org.winble.knot.parsec;

import org.winble.knot.parsec.type.ParseResult;
import org.winble.knot.parsec.type.Parser;

import static org.winble.knot.parsec.Combinators.*;
import static org.winble.knot.parsec.Parsers.*;

/**
 * @author bowenzhang
 * Create on 2022/12/29
 * {true | false | !}
 * S ::= true && S | false || S | T | F | !S | (S)
 * T ::= true || S | true | !false
 * F ::= false && S | false | !true
 */
public class BoolScript {

    public static final Parser<Boolean> boolExpression = defer(BoolScript::boolExpression);

    public static final Parser<Boolean> isTrue = string("true||").skip(boolExpression).or(string("!false")).or(string("true")).map(true);

    public static final Parser<Boolean> isFalse = string("false&&").skip(boolExpression).or(string("!true")).or(string("false")).map(false);

    public static final Parser<Boolean> negateExpression = isChar('!').then(boolExpression).map(r -> !r);

    public static final Parser<Boolean> bracketExpression = isChar('(').then(boolExpression).skip(isChar(')'));

//    public static final Parser<Boolean> andExpression = boolExpression.skip(string("&&")).bind(r -> r ? boolExpression : boolExpression.map(false));
//
//    public static final Parser<Boolean> orExpression = boolExpression.skip(string("||")).bind(r -> r ? boolExpression.map(true) : boolExpression);

    public static final Parser<Boolean> trueAndExpression = string("true&&").then(boolExpression);

    public static final Parser<Boolean> falseOrExpression = string("false||").then(boolExpression);

    public static final Parser<Boolean> boolScript = or(negateExpression, bracketExpression, trueAndExpression, falseOrExpression, isTrue, isFalse);

    private static Parser<Boolean> boolExpression() {
        return boolScript;
    }

    public static boolean eval(String expression) {
        expression = expression.replaceAll(" ", "");
        ParseResult<Boolean> result = boolScript.parse(expression);
        if (!result.isSuccess()) {
            throw result.getError();
        }
        return result.getResult();
    }
}
