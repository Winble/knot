package org.winble.knot.parsec;

import org.winble.knot.parsec.type.Parser;
import org.winble.knot.parsec.util.ParserUtils;

import java.util.*;

import static org.winble.knot.parsec.Combinators.*;
import static org.winble.knot.parsec.Parsers.*;
import static org.winble.knot.parsec.util.ParserUtils.invoke;

/**
 * @author bowenzhang
 * Create on 2022/12/29
 * {true | false}
 * S ::= T && S | F || S | T | F | !S | (S) | EQUAL | IS_EMPTY | CONTAINS
 * T ::= true || S | true
 * F ::= false && S | false
 * T ::= !F
 * F ::= !T
 * EQUAL := ITEM == ITEM | LIST == LIST
 * EMPTY := LIST.isEmpty()
 * CONTAINS := LIST.containsAll(LIST) | LIST.contains(ITEM)
 * LIST ::= [ITEMS]
 * ITEMS :: = STRING,ITEMS | ITEM
 * ITEM ::= STRING | null | e
 * eg:
 * true && (null == _content_manage_user || ("none" == _content_manage_user || ["none"] == _content_manage_user)) && (null == _content_manage_platform || (_content_manage_platform instanceof LIST ? (!_content_manage_platform.isEmpty() && ["TDS","VBK","6381"].containsAll(_content_manage_platform)) : ["TDS","VBK","6381"].contains(_content_manage_platform)))
 */
public class DataRuleScript {

    public static final Parser<?> ignoreSpace = isChar(' ').skipMany();

    public static final Parser<Boolean> boolExpression = defer(() -> DataRuleScript.script.wrap(ignoreSpace));

    public static final Parser<Boolean> trueExpression = string("true").then(string("||").wrap(ignoreSpace)).skip(boolExpression).or(string("true")).as(true);

    public static final Parser<Boolean> falseExpression = string("false").then(string("&&").wrap(ignoreSpace)).skip(boolExpression).or(string("false")).as(false);

    public static final Parser<Boolean> negateExpression = isChar('!').then(boolExpression).map(r -> !r);

    public static final Parser<Boolean> bracketExpression = isChar('(').then(boolExpression).skip(isChar(')'));

    public static final Parser<Boolean> andExpression = trueExpression.skip(string("&&").wrap(ignoreSpace)).bind(r -> r ? boolExpression : boolExpression.as(false));

    public static final Parser<Boolean> orExpression = falseExpression.skip(string("||").wrap(ignoreSpace)).bind(r -> r ? boolExpression.as(true) : boolExpression);

    public static final Parser<String> stringExtractor = isChar('\"').then(next().until(isChar('\"'))).skip().wrap(ignoreSpace).map(ParserUtils::charsToString);

    public static final Parser<List<Object>> listExtractor = isChar('[').then(stringExtractor.or(string(",").ignore()).until(isChar(']'))).skip().wrap(ignoreSpace).map();

    public static final Parser<Object> valueExtractor = or(stringExtractor.map(), listExtractor.map());

    public static final Parser<Boolean> equalExpression = valueExtractor.skipMany(isChar(' ')).skip(string("==")).skipMany(isChar(' ')).union(valueExtractor).map(invoke(DataRuleScript::equalEval));

    public static final Parser<Boolean> containsOneExpression = listExtractor.skip(string(".contains(")).union(valueExtractor).skip(isChar(')')).map(invoke(DataRuleScript::containsEval));

    public static final Parser<Boolean> containsAllExpression = listExtractor.skip(string(".containsAll(")).union(listExtractor).skip(isChar(')')).map(invoke(DataRuleScript::containsAllEval));

    public static final Parser<Boolean> containsExpression = or(containsOneExpression, containsAllExpression);

    public static final Parser<Boolean> script = or(negateExpression, bracketExpression, andExpression, orExpression, equalExpression, containsExpression, trueExpression, falseExpression);

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

    public static boolean eval(String expression) {
        return script.parse(expression).get();
    }
}
