

package com.didiglobal.logi.elasticsearch.client.parser.query_string.parser;

import com.didiglobal.logi.elasticsearch.client.parser.query_string.ast.QSNode;

import java.io.StringReader;

public class QSParser extends QSBaseParser implements QSParserConstants {
    public QSParser() {
        this(new FastCharStream(new StringReader("")));
    }

    public QSNode parse(String queryString) throws ParseException {
        ReInit(new FastCharStream(new StringReader(queryString)));
        return super.parse(queryString);
    }




    final public QSNode TopLevelQuery() throws ParseException {
        QSNode node;
        node = Query();
        jj_consume_token(0);
        {
            if ("" != null) return node;
        }
        throw new Error("Missing return statement in function");
    }

    final public QSNode Query() throws ParseException {
        QSNodeBuilder builder = new QSNodeBuilder();
        Modifiers(builder);
        Clause(builder);
        label_1:
        while (true) {
            switch ((jj_ntk == -1) ? jj_ntk_f() : jj_ntk) {
                case AND1:
                case AND2:
                case AND3:
                case OR1:
                case OR2:
                case OR3:
                case NOT1:
                case NOT2:
                case NOT3:
                case PLUS:
                case MINUS:
                case BAREOPER:
                case LPAREN:
                case STAR:
                case QUOTED:
                case TERM:
                case PREFIXTERM:
                case WILDTERM:
                case REGEXPTERM:
                case RANGEIN_START:
                case RANGEEX_START:
                case NUMBER: {
                    ;
                    break;
                }
                default:
                    jj_la1[0] = jj_gen;
                    break label_1;
            }
            Conjunction(builder);
            Modifiers(builder);
            Clause(builder);
        }
        {
            if ("" != null) return builder.toNode();
        }
        throw new Error("Missing return statement in function");
    }


    final public void Clause(QSNodeBuilder builder) throws ParseException {
        Token fieldToken = null, boost = null;
        QSNode node;
        if (jj_2_1(2)) {
            switch ((jj_ntk == -1) ? jj_ntk_f() : jj_ntk) {
                case TERM: {
                    fieldToken = jj_consume_token(TERM);
                    jj_consume_token(COLON);
                    builder.addFieldEq(fieldToken);
                    break;
                }
                case STAR: {
                    fieldToken = jj_consume_token(STAR);
                    jj_consume_token(COLON);
                    builder.addFieldEq(fieldToken);
                    break;
                }
                default:
                    jj_la1[1] = jj_gen;
                    jj_consume_token(-1);
                    throw new ParseException();
            }
        } else {
            ;
        }
        switch ((jj_ntk == -1) ? jj_ntk_f() : jj_ntk) {
            case BAREOPER:
            case STAR:
            case QUOTED:
            case TERM:
            case PREFIXTERM:
            case WILDTERM:
            case REGEXPTERM:
            case RANGEIN_START:
            case RANGEEX_START:
            case NUMBER: {
                Term(builder);
                break;
            }
            case LPAREN: {
                jj_consume_token(LPAREN);
                node = Query();
                jj_consume_token(RPAREN);
                switch ((jj_ntk == -1) ? jj_ntk_f() : jj_ntk) {
                    case CARAT: {
                        jj_consume_token(CARAT);
                        boost = jj_consume_token(NUMBER);
                        break;
                    }
                    default:
                        jj_la1[2] = jj_gen;
                        ;
                }
                builder.addParen(node, boost);
                break;
            }
            default:
                jj_la1[3] = jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
    }


    final public void Term(QSNodeBuilder builder) throws ParseException {
        Token term, boost = null, fuzzySlop = null, goop1, goop2;
        boolean startInc = false;
        boolean endInc = false;
        switch ((jj_ntk == -1) ? jj_ntk_f() : jj_ntk) {
            case BAREOPER:
            case STAR:
            case TERM:
            case PREFIXTERM:
            case WILDTERM:
            case REGEXPTERM:
            case NUMBER: {
                switch ((jj_ntk == -1) ? jj_ntk_f() : jj_ntk) {
                    case TERM: {
                        term = jj_consume_token(TERM);
                        break;
                    }
                    case STAR: {
                        term = jj_consume_token(STAR);
                        break;
                    }
                    case PREFIXTERM: {
                        term = jj_consume_token(PREFIXTERM);
                        break;
                    }
                    case WILDTERM: {
                        term = jj_consume_token(WILDTERM);
                        break;
                    }
                    case REGEXPTERM: {
                        term = jj_consume_token(REGEXPTERM);
                        break;
                    }
                    case NUMBER: {
                        term = jj_consume_token(NUMBER);
                        break;
                    }
                    case BAREOPER: {
                        term = jj_consume_token(BAREOPER);
                        break;
                    }
                    default:
                        jj_la1[4] = jj_gen;
                        jj_consume_token(-1);
                        throw new ParseException();
                }
                switch ((jj_ntk == -1) ? jj_ntk_f() : jj_ntk) {
                    case FUZZY_SLOP: {
                        fuzzySlop = jj_consume_token(FUZZY_SLOP);
                        break;
                    }
                    default:
                        jj_la1[5] = jj_gen;
                        ;
                }
                switch ((jj_ntk == -1) ? jj_ntk_f() : jj_ntk) {
                    case CARAT: {
                        jj_consume_token(CARAT);
                        boost = jj_consume_token(NUMBER);
                        switch ((jj_ntk == -1) ? jj_ntk_f() : jj_ntk) {
                            case FUZZY_SLOP: {
                                fuzzySlop = jj_consume_token(FUZZY_SLOP);
                                break;
                            }
                            default:
                                jj_la1[6] = jj_gen;
                                ;
                        }
                        break;
                    }
                    default:
                        jj_la1[7] = jj_gen;
                        ;
                }
                builder.addValue(term, fuzzySlop, boost);
                break;
            }
            case RANGEIN_START:
            case RANGEEX_START: {
                switch ((jj_ntk == -1) ? jj_ntk_f() : jj_ntk) {
                    case RANGEIN_START: {
                        jj_consume_token(RANGEIN_START);
                        startInc = true;
                        break;
                    }
                    case RANGEEX_START: {
                        jj_consume_token(RANGEEX_START);
                        break;
                    }
                    default:
                        jj_la1[8] = jj_gen;
                        jj_consume_token(-1);
                        throw new ParseException();
                }
                switch ((jj_ntk == -1) ? jj_ntk_f() : jj_ntk) {
                    case RANGE_GOOP: {
                        goop1 = jj_consume_token(RANGE_GOOP);
                        break;
                    }
                    case RANGE_QUOTED: {
                        goop1 = jj_consume_token(RANGE_QUOTED);
                        break;
                    }
                    default:
                        jj_la1[9] = jj_gen;
                        jj_consume_token(-1);
                        throw new ParseException();
                }
                switch ((jj_ntk == -1) ? jj_ntk_f() : jj_ntk) {
                    case RANGE_TO: {
                        jj_consume_token(RANGE_TO);
                        break;
                    }
                    default:
                        jj_la1[10] = jj_gen;
                        ;
                }
                switch ((jj_ntk == -1) ? jj_ntk_f() : jj_ntk) {
                    case RANGE_GOOP: {
                        goop2 = jj_consume_token(RANGE_GOOP);
                        break;
                    }
                    case RANGE_QUOTED: {
                        goop2 = jj_consume_token(RANGE_QUOTED);
                        break;
                    }
                    default:
                        jj_la1[11] = jj_gen;
                        jj_consume_token(-1);
                        throw new ParseException();
                }
                switch ((jj_ntk == -1) ? jj_ntk_f() : jj_ntk) {
                    case RANGEIN_END: {
                        jj_consume_token(RANGEIN_END);
                        endInc = true;
                        break;
                    }
                    case RANGEEX_END: {
                        jj_consume_token(RANGEEX_END);
                        break;
                    }
                    default:
                        jj_la1[12] = jj_gen;
                        jj_consume_token(-1);
                        throw new ParseException();
                }
                switch ((jj_ntk == -1) ? jj_ntk_f() : jj_ntk) {
                    case CARAT: {
                        jj_consume_token(CARAT);
                        boost = jj_consume_token(NUMBER);
                        break;
                    }
                    default:
                        jj_la1[13] = jj_gen;
                        ;
                }
                builder.addRange(goop1, goop2, startInc, endInc);
                break;
            }
            case QUOTED: {
                term = jj_consume_token(QUOTED);
                switch ((jj_ntk == -1) ? jj_ntk_f() : jj_ntk) {
                    case FUZZY_SLOP: {
                        fuzzySlop = jj_consume_token(FUZZY_SLOP);
                        break;
                    }
                    default:
                        jj_la1[14] = jj_gen;
                        ;
                }
                switch ((jj_ntk == -1) ? jj_ntk_f() : jj_ntk) {
                    case CARAT: {
                        jj_consume_token(CARAT);
                        boost = jj_consume_token(NUMBER);
                        break;
                    }
                    default:
                        jj_la1[15] = jj_gen;
                        ;
                }
                builder.addValue(term, fuzzySlop, boost);
                break;
            }
            default:
                jj_la1[16] = jj_gen;
                jj_consume_token(-1);
                throw new ParseException();
        }
    }

    final public void Conjunction(QSNodeBuilder builder) throws ParseException {
        Token conj;
        switch ((jj_ntk == -1) ? jj_ntk_f() : jj_ntk) {
            case AND1:
            case AND2:
            case AND3:
            case OR1:
            case OR2:
            case OR3: {
                switch ((jj_ntk == -1) ? jj_ntk_f() : jj_ntk) {
                    case AND1: {
                        conj = jj_consume_token(AND1);
                        builder.addAnd(conj);
                        break;
                    }
                    case AND2: {
                        conj = jj_consume_token(AND2);
                        builder.addAnd(conj);
                        break;
                    }
                    case AND3: {
                        conj = jj_consume_token(AND3);
                        builder.addAnd(conj);
                        break;
                    }
                    case OR1: {
                        conj = jj_consume_token(OR1);
                        builder.addOr(conj);
                        break;
                    }
                    case OR2: {
                        conj = jj_consume_token(OR2);
                        builder.addOr(conj);
                        break;
                    }
                    case OR3: {
                        conj = jj_consume_token(OR3);
                        builder.addOr(conj);
                        break;
                    }
                    default:
                        jj_la1[17] = jj_gen;
                        jj_consume_token(-1);
                        throw new ParseException();
                }
                break;
            }
            default:
                jj_la1[18] = jj_gen;
                ;
        }
    }

    final public void Modifiers(QSNodeBuilder builder) throws ParseException {
        Token mods;
        switch ((jj_ntk == -1) ? jj_ntk_f() : jj_ntk) {
            case NOT1:
            case NOT2:
            case NOT3:
            case PLUS:
            case MINUS: {
                switch ((jj_ntk == -1) ? jj_ntk_f() : jj_ntk) {
                    case PLUS: {
                        mods = jj_consume_token(PLUS);
                        builder.addPlus(mods);
                        break;
                    }
                    case MINUS: {
                        mods = jj_consume_token(MINUS);
                        builder.addMinus(mods);
                        break;
                    }
                    case NOT1: {
                        mods = jj_consume_token(NOT1);
                        builder.addNot(mods);
                        break;
                    }
                    case NOT2: {
                        mods = jj_consume_token(NOT2);
                        builder.addNot(mods);
                        break;
                    }
                    case NOT3: {
                        mods = jj_consume_token(NOT3);
                        builder.addNot(mods);
                        break;
                    }
                    default:
                        jj_la1[19] = jj_gen;
                        jj_consume_token(-1);
                        throw new ParseException();
                }
                break;
            }
            default:
                jj_la1[20] = jj_gen;
                ;
        }
    }

    private boolean jj_2_1(int xla) {
        jj_la = xla;
        jj_lastpos = jj_scanpos = token;
        try {
            return !jj_3_1();
        } catch (LookaheadSuccess ls) {
            return true;
        } finally {
            jj_save(0, xla);
        }
    }

    private boolean jj_3R_2() {
        if (jj_scan_token(TERM)) return true;
        if (jj_scan_token(COLON)) return true;
        return false;
    }

    private boolean jj_3_1() {
        Token xsp;
        xsp = jj_scanpos;
        if (jj_3R_2()) {
            jj_scanpos = xsp;
            if (jj_3R_3()) return true;
        }
        return false;
    }

    private boolean jj_3R_3() {
        if (jj_scan_token(STAR)) return true;
        if (jj_scan_token(COLON)) return true;
        return false;
    }


    public QSParserTokenManager token_source;

    public Token token;

    public Token jj_nt;
    private int jj_ntk;
    private Token jj_scanpos, jj_lastpos;
    private int jj_la;
    private int jj_gen;
    final private int[] jj_la1 = new int[21];
    static private int[] jj_la1_0;
    static private int[] jj_la1_1;

    static {
        jj_la1_init_0();
        jj_la1_init_1();
    }

    private static void jj_la1_init_0() {
        jj_la1_0 = new int[]{0xf69fff00, 0x4800000, 0x1000000, 0xf6980000, 0x74880000, 0x8000000, 0x8000000, 0x1000000, 0x80000000, 0x0, 0x0, 0x0, 0x0, 0x1000000, 0x8000000, 0x1000000, 0xf6880000, 0x3f00, 0x3f00, 0x7c000, 0x7c000,};
    }

    private static void jj_la1_init_1() {
        jj_la1_1 = new int[]{0x3, 0x0, 0x0, 0x3, 0x2, 0x0, 0x0, 0x0, 0x1, 0x60, 0x4, 0x60, 0x18, 0x0, 0x0, 0x0, 0x3, 0x0, 0x0, 0x0, 0x0,};
    }

    final private JJCalls[] jj_2_rtns = new JJCalls[1];
    private boolean jj_rescan = false;
    private int jj_gc = 0;


    public QSParser(CharStream stream) {
        token_source = new QSParserTokenManager(stream);
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 21; i++) jj_la1[i] = -1;
        for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
    }


    public void ReInit(CharStream stream) {
        token_source.ReInit(stream);
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 21; i++) jj_la1[i] = -1;
        for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
    }


    public QSParser(QSParserTokenManager tm) {
        token_source = tm;
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 21; i++) jj_la1[i] = -1;
        for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
    }


    public void ReInit(QSParserTokenManager tm) {
        token_source = tm;
        token = new Token();
        jj_ntk = -1;
        jj_gen = 0;
        for (int i = 0; i < 21; i++) jj_la1[i] = -1;
        for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
    }

    private Token jj_consume_token(int kind) throws ParseException {
        Token oldToken;
        if ((oldToken = token).next != null) token = token.next;
        else token = token.next = token_source.getNextToken();
        jj_ntk = -1;
        if (token.kind == kind) {
            jj_gen++;
            if (++jj_gc > 100) {
                jj_gc = 0;
                for (int i = 0; i < jj_2_rtns.length; i++) {
                    JJCalls c = jj_2_rtns[i];
                    while (c != null) {
                        if (c.gen < jj_gen) c.first = null;
                        c = c.next;
                    }
                }
            }
            return token;
        }
        token = oldToken;
        jj_kind = kind;
        throw generateParseException();
    }

    @SuppressWarnings("serial")
    static private final class LookaheadSuccess extends java.lang.Error {
    }

    final private LookaheadSuccess jj_ls = new LookaheadSuccess();

    private boolean jj_scan_token(int kind) {
        if (jj_scanpos == jj_lastpos) {
            jj_la--;
            if (jj_scanpos.next == null) {
                jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
            } else {
                jj_lastpos = jj_scanpos = jj_scanpos.next;
            }
        } else {
            jj_scanpos = jj_scanpos.next;
        }
        if (jj_rescan) {
            int i = 0;
            Token tok = token;
            while (tok != null && tok != jj_scanpos) {
                i++;
                tok = tok.next;
            }
            if (tok != null) jj_add_error_token(kind, i);
        }
        if (jj_scanpos.kind != kind) return true;
        if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
        return false;
    }



    final public Token getNextToken() {
        if (token.next != null) token = token.next;
        else token = token.next = token_source.getNextToken();
        jj_ntk = -1;
        jj_gen++;
        return token;
    }


    final public Token getToken(int index) {
        Token t = token;
        for (int i = 0; i < index; i++) {
            if (t.next != null) t = t.next;
            else t = t.next = token_source.getNextToken();
        }
        return t;
    }

    private int jj_ntk_f() {
        if ((jj_nt = token.next) == null)
            return (jj_ntk = (token.next = token_source.getNextToken()).kind);
        else
            return (jj_ntk = jj_nt.kind);
    }

    private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
    private int[] jj_expentry;
    private int jj_kind = -1;
    private int[] jj_lasttokens = new int[100];
    private int jj_endpos;

    private void jj_add_error_token(int kind, int pos) {
        if (pos >= 100) return;
        if (pos == jj_endpos + 1) {
            jj_lasttokens[jj_endpos++] = kind;
        } else if (jj_endpos != 0) {
            jj_expentry = new int[jj_endpos];
            for (int i = 0; i < jj_endpos; i++) {
                jj_expentry[i] = jj_lasttokens[i];
            }
            jj_entries_loop:
            for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext(); ) {
                int[] oldentry = (int[]) (it.next());
                if (oldentry.length == jj_expentry.length) {
                    for (int i = 0; i < jj_expentry.length; i++) {
                        if (oldentry[i] != jj_expentry[i]) {
                            continue jj_entries_loop;
                        }
                    }
                    jj_expentries.add(jj_expentry);
                    break jj_entries_loop;
                }
            }
            if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
        }
    }


    public ParseException generateParseException() {
        jj_expentries.clear();
        boolean[] la1tokens = new boolean[39];
        if (jj_kind >= 0) {
            la1tokens[jj_kind] = true;
            jj_kind = -1;
        }
        for (int i = 0; i < 21; i++) {
            if (jj_la1[i] == jj_gen) {
                for (int j = 0; j < 32; j++) {
                    if ((jj_la1_0[i] & (1 << j)) != 0) {
                        la1tokens[j] = true;
                    }
                    if ((jj_la1_1[i] & (1 << j)) != 0) {
                        la1tokens[32 + j] = true;
                    }
                }
            }
        }
        for (int i = 0; i < 39; i++) {
            if (la1tokens[i]) {
                jj_expentry = new int[1];
                jj_expentry[0] = i;
                jj_expentries.add(jj_expentry);
            }
        }
        jj_endpos = 0;
        jj_rescan_token();
        jj_add_error_token(0, 0);
        int[][] exptokseq = new int[jj_expentries.size()][];
        for (int i = 0; i < jj_expentries.size(); i++) {
            exptokseq[i] = jj_expentries.get(i);
        }
        return new ParseException(token, exptokseq, tokenImage);
    }


    final public void enable_tracing() {
    }


    final public void disable_tracing() {
    }

    private void jj_rescan_token() {
        jj_rescan = true;
        for (int i = 0; i < 1; i++) {
            try {
                JJCalls p = jj_2_rtns[i];
                do {
                    if (p.gen > jj_gen) {
                        jj_la = p.arg;
                        jj_lastpos = jj_scanpos = p.first;
                        switch (i) {
                            case 0:
                                jj_3_1();
                                break;
                        }
                    }
                    p = p.next;
                } while (p != null);
            } catch (LookaheadSuccess ls) {
            }
        }
        jj_rescan = false;
    }

    private void jj_save(int index, int xla) {
        JJCalls p = jj_2_rtns[index];
        while (p.gen > jj_gen) {
            if (p.next == null) {
                p = p.next = new JJCalls();
                break;
            }
            p = p.next;
        }
        p.gen = jj_gen + xla - jj_la;
        p.first = token;
        p.arg = xla;
    }

    static final class JJCalls {
        int gen;
        Token first;
        int arg;
        JJCalls next;
    }

}
