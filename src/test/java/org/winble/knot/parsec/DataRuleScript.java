package org.winble.knot.parsec;

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
 *
 * true && (null == _content_manage_user || ("none" == _content_manage_user || ["none"] == _content_manage_user)) && (null == _content_manage_platform || (_content_manage_platform instanceof List ? (!_content_manage_platform.isEmpty() && ["TDS","VBK","6381"].containsAll(_content_manage_platform)) : ["TDS","VBK","6381"].contains(_content_manage_platform)))
 */
public class DataRuleScript {
    
//    public final static Parser<Boolean> equalExpression = ;
}
