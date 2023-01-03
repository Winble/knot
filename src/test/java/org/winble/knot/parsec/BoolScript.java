package org.winble.knot.parsec;

import org.winble.knot.parsec.type.ParseResult;
import org.winble.knot.parsec.type.Parser;

import java.util.function.Predicate;

import static org.winble.knot.parsec.Combinators.*;
import static org.winble.knot.parsec.Parsers.*;

/**
 * @author bowenzhang
 * Create on 2022/12/29
 * {true | false}
 * S ::= S && S | S || S | (S) | !S | S ? S : S | E
 * S ::= E S' | (S) | !S
 * S' ::= && S S' | || S S' | ? S : S S' | ε
 * E ::= true | false
 */
public class BoolScript {

    public static final Parser<?> ignoreSpace = isChar(' ').skipMany();

    public static final Parser<Boolean> boolExpression = defer(() -> BoolScript.script);

    public static final Parser<Predicate<Boolean>> predicateExpression = defer(() -> BoolScript.predicate.wrap(ignoreSpace));

    public static final Parser<Predicate<Boolean>> andPredicate = string("&&").then(boolExpression).union(predicateExpression).map(r -> pre -> pre && r.getRight().test(r.getLeft()));

    public static final Parser<Predicate<Boolean>> orPredicate = string("||").then(boolExpression).union(predicateExpression).map(r -> pre -> pre || r.getRight().test(r.getLeft()));

    public static final Parser<Predicate<Boolean>> ternaryPredicate = string("?").then(boolExpression).skip(string(":")).union(boolExpression).union(predicateExpression).map(r -> pre -> pre ? r.getLeft().getLeft() : r.getRight().test(r.getLeft().getRight()));

    public static final Parser<Predicate<Boolean>> predicate = or(andPredicate, orPredicate, ternaryPredicate, as(pre -> pre));

    public static final Parser<Boolean> negateExpression = isChar('!').then(boolExpression).map(r -> !r);

    public static final Parser<Boolean> bracketExpression = isChar('(').then(boolExpression).skip(isChar(')'));

    public static final Parser<Boolean> evaluateExpression = or(string("true").as(true), string("false").as(false)).wrap(ignoreSpace);

    public static final Parser<Boolean> script = or(bracketExpression, negateExpression, evaluateExpression.bind(pre -> predicateExpression.map(r -> r.test(pre)))).wrap(ignoreSpace);

    public static boolean eval(String expression) {
        return script.ended().parse(expression).get();
    }
}
