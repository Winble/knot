package org.winble.knot.parsec;

import org.winble.knot.parsec.type.ParseResult;
import org.winble.knot.parsec.type.Parser;
import org.winble.knot.parsec.util.ParserUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import static org.winble.knot.parsec.Combinators.defer;
import static org.winble.knot.parsec.Combinators.or;
import static org.winble.knot.parsec.Parsers.*;
import static org.winble.knot.parsec.util.ParserUtils.biInvoke;

/**
 * @author bowenzhang
 * Create on 2022/12/29
 * {true | false}
 * S ::= S && S | S || S | (S) | !S | S ? S : S | E
 * S ::= E S' | (S) | !S
 * S' ::= && S S' | || S S' | ? S : S S' | ε
 * E ::= EQUAL | CONTAINS | EMPTY | INSTANCE | true | false
 * EQUAL ::= VALUE == VALUE
 * CONTAINS ::= LIST.contains(VALUE) | LIST.containsAll(LIST)
 * EMPTY ::= LIST.isEmpty()
 * INSTANCE ::= VALUE instanceof CLASS
 * LIST ::= [] | [VALUES]
 * VALUES ::= VALUE | VALUE, VALUES
 * VALUE ::= LIST | STRING
 * CLASS ::= List
 * eg:
 * true && (null == _content_manage_user || ("none" == _content_manage_user || ["none"] == _content_manage_user)) && (null == _content_manage_platform || (_content_manage_platform instanceof LIST ? (!_content_manage_platform.isEmpty() && ["TDS","VBK","6381"].containsAll(_content_manage_platform)) : ["TDS","VBK","6381"].contains(_content_manage_platform)))
 */
public class DataRuleScript {

    public static final Parser<?> ignoreSpace = isChar(' ').skipMany();

    public static final Parser<Boolean> boolExpression = defer(() -> DataRuleScript.script);

    public static final Parser<Predicate<Boolean>> predicateExpression = defer(() -> DataRuleScript.predicate.wrap(ignoreSpace));

    public static final Parser<Predicate<Boolean>> andPredicate = string("&&").then(boolExpression).union(predicateExpression).map(r -> pre -> pre && r.getRight().test(r.getLeft()));

    public static final Parser<Predicate<Boolean>> orPredicate = string("||").then(boolExpression).union(predicateExpression).map(r -> pre -> pre || r.getRight().test(r.getLeft()));

    public static final Parser<Predicate<Boolean>> ternaryPredicate = string("?").then(boolExpression).skip(string(":")).union(boolExpression).union(predicateExpression).map(r -> pre -> pre ? r.getLeft().getLeft() : r.getRight().test(r.getLeft().getRight()));

    public static final Parser<Predicate<Boolean>> predicate = or(andPredicate, orPredicate, ternaryPredicate, as(pre -> pre));

    public static final Parser<Boolean> negateExpression = isChar('!').then(boolExpression).map(r -> !r);

    public static final Parser<Boolean> bracketExpression = isChar('(').then(boolExpression).skip(isChar(')'));

    public static final Parser<String> stringExtractor = isChar('\"').then(next().until(isChar('\"'))).skip().wrap(ignoreSpace).map(ParserUtils::charsToString);

    public static final Parser<List<Object>> listExtractor = isChar('[').then(stringExtractor.or(string(",").ignore()).until(isChar(']'))).skip().wrap(ignoreSpace).convert();

    public static final Parser<Object> valueExtractor = or(stringExtractor.convert(), listExtractor.convert()).wrap(ignoreSpace);

    public static final Parser<Boolean> equalExpression = valueExtractor.skip(string("==")).union(valueExtractor).map(biInvoke(DataRuleScript::equalEval));

    public static final Parser<Boolean> containsOneExpression = listExtractor.skip(string(".contains(")).union(valueExtractor).skip(isChar(')')).map(biInvoke(DataRuleScript::containsEval));

    public static final Parser<Boolean> containsAllExpression = listExtractor.skip(string(".containsAll(")).union(listExtractor).skip(isChar(')')).map(biInvoke(DataRuleScript::containsAllEval));

    public static final Parser<Boolean> containsExpression = or(containsOneExpression, containsAllExpression);

    public static final Parser<Boolean> isEmptyExpression = listExtractor.skip(string(".isEmpty()")).map(DataRuleScript::isEmptyEval);

    public static final Parser<Boolean> instanceOfExpression = valueExtractor.skip(string("instanceof")).skipMany(isChar(' ')).union(next().until(isChar(' ')).map(ParserUtils::charsToString)).map(biInvoke(DataRuleScript::instanceOfEval));

    public static final Parser<Boolean> evaluateExpression = or(equalExpression, containsExpression, isEmptyExpression, instanceOfExpression, string("true").as(true), string("false").as(false)).wrap(ignoreSpace);

    public static final Parser<Boolean> script = or(bracketExpression, negateExpression, evaluateExpression.bind(pre -> predicateExpression.map(r -> r.test(pre)))).wrap(ignoreSpace);

    private static boolean equalEval(Object l, Object r) {
        if (l == r) {
            return true;
        }
        if (null == l || null == r) {
            return false;
        }
        if (l instanceof Collection<?> && r instanceof Collection<?>) {
            if (((Collection<?>) l).isEmpty() && ((Collection<?>) r).isEmpty()) {
                return true;
            }
            if (((Collection<?>) l).size() != ((Collection<?>) r).size()) {
                return false;
            }
            Iterator<?> li = ((Collection<?>) l).iterator();
            Iterator<?> ri = ((Collection<?>) r).iterator();
            while (li.hasNext() && ri.hasNext()) {
                if (!equalEval(li.next(), ri.next())) {
                    return false;
                }
            }
            return true;
        }
        return l.equals(r);
    }

    private static boolean containsEval(Collection<Object> vs, Object v) {
        if (null == vs || vs.isEmpty()) {
            return false;
        }
        return vs.contains(v);
    }

    private static boolean containsAllEval(Collection<Object> vs, Collection<Object> v) {
        if (null == vs || vs.isEmpty()) {
            return false;
        }
        return vs.containsAll(v);
    }

    private static boolean isEmptyEval(Collection<?> vs) {
        return null == vs || vs.isEmpty();
    }

    public static boolean instanceOfEval(Object v, String className) {
        if (className.equals("List")) {
            return v instanceof List;
        }
        return false;
    }

    public static boolean eval(String expression) {
        return script.ended().parse(expression).get();
    }
}
