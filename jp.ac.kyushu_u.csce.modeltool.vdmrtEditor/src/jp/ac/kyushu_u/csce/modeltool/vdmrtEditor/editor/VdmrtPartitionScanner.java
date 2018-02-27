package jp.ac.kyushu_u.csce.modeltool.vdmrtEditor.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.text.rules.*;

/**
 * パーティションスキャナ
 *
 * @author KBK yoshimura
 */
public class VdmrtPartitionScanner extends RuleBasedPartitionScanner {

    /** トークンID：文字列 */
    public static final String VDM_STRING = "__vdm_string"; //$NON-NLS-1$
    /** トークンID：文字 */
    public static final String VDM_CHARACTER = "__vdm_character"; //$NON-NLS-1$
    /** トークンID：1行コメント */
    public static final String VDM_SINGLE_LINE_COMMENT = "__vdm_singleline_comment"; //$NON-NLS-1$
    /** トークンID：複数行コメント */
    public static final String VDM_MULTI_LINE_COMMENT = "__vdm_multiline_comment"; //$NON-NLS-1$
    /** トークンID：キーワード */
    public static final String VDM_KEYWORD = "__vdm_keyword"; //$NON-NLS-1$

    /** キーワード配列 */
    private String KEYWORDS[] = {
            "#act", //$NON-NLS-1$
            "#active", //$NON-NLS-1$
            "#fin", //$NON-NLS-1$
            "#req", //$NON-NLS-1$
            "#waiting", //$NON-NLS-1$
            "abs", //$NON-NLS-1$
            "all", //$NON-NLS-1$
            "always", //$NON-NLS-1$
            "and", //$NON-NLS-1$
            "atomic", //$NON-NLS-1$
            "be st", //$NON-NLS-1$
            "bool", //$NON-NLS-1$
            "by", //$NON-NLS-1$
            "card", //$NON-NLS-1$
            "cases", //$NON-NLS-1$
            "char", //$NON-NLS-1$
            "class", //$NON-NLS-1$
            "comp", //$NON-NLS-1$
            "compose", //$NON-NLS-1$
            "conc", //$NON-NLS-1$
            "dcl", //$NON-NLS-1$
            "def", //$NON-NLS-1$
            "dinter", //$NON-NLS-1$
            "div", //$NON-NLS-1$
            "do", //$NON-NLS-1$
            "dom", //$NON-NLS-1$
            "dunion", //$NON-NLS-1$
            "elems", //$NON-NLS-1$
            "else", //$NON-NLS-1$
            "elseif", //$NON-NLS-1$
            "end", //$NON-NLS-1$
            "error", //$NON-NLS-1$
            "errs", //$NON-NLS-1$
            "exists", //$NON-NLS-1$
            "exists1", //$NON-NLS-1$
            "exit", //$NON-NLS-1$
            "ext", //$NON-NLS-1$
            "false", //$NON-NLS-1$
            "floor", //$NON-NLS-1$
            "for", //$NON-NLS-1$
            "forall", //$NON-NLS-1$
            "functions", //$NON-NLS-1$
            "hd", //$NON-NLS-1$
            "if", //$NON-NLS-1$
            "in", //$NON-NLS-1$
            "in set", //$NON-NLS-1$
            "inds", //$NON-NLS-1$
            "inmap", //$NON-NLS-1$
            "instance variables", //$NON-NLS-1$
            "int", //$NON-NLS-1$
            "inter", //$NON-NLS-1$
            "inv", //$NON-NLS-1$
            "inverse", //$NON-NLS-1$
            "iota", //$NON-NLS-1$
            "is_", //$NON-NLS-1$
            "is not yet specified", //$NON-NLS-1$
            "is subclass responsibility", //$NON-NLS-1$
            "is subclass of", //$NON-NLS-1$
            "isofbaseclass", //$NON-NLS-1$
            "isofclass", //$NON-NLS-1$
            "lambda", //$NON-NLS-1$
            "len", //$NON-NLS-1$
            "let", //$NON-NLS-1$
            "map", //$NON-NLS-1$
            "measure", //$NON-NLS-1$
            "merge", //$NON-NLS-1$
            "mk_", //$NON-NLS-1$
            "mod", //$NON-NLS-1$
            "mu", //$NON-NLS-1$
            "munion", //$NON-NLS-1$
            "mutex", //$NON-NLS-1$
            "nat", //$NON-NLS-1$
            "nat1", //$NON-NLS-1$
            "new", //$NON-NLS-1$
            "nil", //$NON-NLS-1$
            "not", //$NON-NLS-1$
            "not in set", //$NON-NLS-1$
            "of", //$NON-NLS-1$
            "operations", //$NON-NLS-1$
            "or", //$NON-NLS-1$
            "others", //$NON-NLS-1$
            "per", //$NON-NLS-1$
            "post", //$NON-NLS-1$
            "power", //$NON-NLS-1$
            "pre", //$NON-NLS-1$
            "private", //$NON-NLS-1$
            "protected", //$NON-NLS-1$
            "psubset", //$NON-NLS-1$
            "public", //$NON-NLS-1$
            "rat", //$NON-NLS-1$
            "rd", //$NON-NLS-1$
            "real", //$NON-NLS-1$
            "rem", //$NON-NLS-1$
            "return", //$NON-NLS-1$
            "reverse", //$NON-NLS-1$
            "rng", //$NON-NLS-1$
            "samebaseclass", //$NON-NLS-1$
            "sameclass", //$NON-NLS-1$
            "self", //$NON-NLS-1$
            "seq of", //$NON-NLS-1$
            "seq1 of", //$NON-NLS-1$
            "set of", //$NON-NLS-1$
            "skip", //$NON-NLS-1$
            "start", //$NON-NLS-1$
            "startlist", //$NON-NLS-1$
            "static", //$NON-NLS-1$
            "subset", //$NON-NLS-1$
            "sync", //$NON-NLS-1$
            "then", //$NON-NLS-1$
            "thread", //$NON-NLS-1$
            "threadid", //$NON-NLS-1$
            "tl", //$NON-NLS-1$
            "tixe", //$NON-NLS-1$
            "to", //$NON-NLS-1$
            "token", //$NON-NLS-1$
            "traces", //$NON-NLS-1$
            "trap", //$NON-NLS-1$
            "true", //$NON-NLS-1$
            "types", //$NON-NLS-1$
            "undefined", //$NON-NLS-1$
            "union", //$NON-NLS-1$
            "values", //$NON-NLS-1$
            "while", //$NON-NLS-1$
            "with", //$NON-NLS-1$
            "wr", //$NON-NLS-1$
            "RESULT", //$NON-NLS-1$

            // VICE
            "async", //$NON-NLS-1$
            "periodic", //$NON-NLS-1$

            // 未使用？
            "from", //$NON-NLS-1$
            "input", //$NON-NLS-1$

            // ↑ここまでVDM++と同じ

            // ↓以下VDM-RTで追加
            "time", //$NON-NLS-1$
            "cycles", //$NON-NLS-1$
            "duration", //$NON-NLS-1$
            "system", //$NON-NLS-1$
    };

    /** 文字列引用符 */
    private static final String STRING_QUATATION = "\""; //$NON-NLS-1$
    /** 文字引用符 */
    private static final String CHARACTER_QUATATION = "'"; //$NON-NLS-1$
    /** 1行コメント文字列 */
    private static final String SINGLE_LINE_COMMENT = "--"; //$NON-NLS-1$
    /** 複数行コメント文字列 */
    private static final String MULTI_LINE_COMMENT[] = {
        "/*", "*/" //$NON-NLS-1$ //$NON-NLS-2$
    };

    /**
     * コンストラクタ
     */
    public VdmrtPartitionScanner() {

        // トークンの生成
        IToken vdmString = new Token(VDM_STRING);
        IToken vdmCharacter = new Token(VDM_CHARACTER);
        IToken singleLineComment = new Token(VDM_SINGLE_LINE_COMMENT);
        IToken multiLineComment = new Token(VDM_MULTI_LINE_COMMENT);
        IToken vdmKeyword = new Token(VDM_KEYWORD);

        // ルールリスト
        List<IPredicateRule> rules = new ArrayList<IPredicateRule>();

        // ルールの追加
        // 文字列
        rules.add(new SingleLineRule(STRING_QUATATION, STRING_QUATATION, vdmString));
        // 文字
        rules.add(new SingleLineRule(CHARACTER_QUATATION, CHARACTER_QUATATION, vdmCharacter));
        // 1行コメント
        rules.add(new EndOfLineRule(SINGLE_LINE_COMMENT, singleLineComment));
        // 複数行コメント
        rules.add(new MultiLineRule(MULTI_LINE_COMMENT[0], MULTI_LINE_COMMENT[1], multiLineComment));

        // キーワード
        //   キーワードの長い方から順に登録する（登録順にスキャンされる）
        Arrays.sort(KEYWORDS, new Comparator<String>() {
            public int compare(String o1, String o2) {
                if (o1.length() > o2.length()) {
                    return -1;
                }
                if (o1.length() < o2.length()) {
                    return 1;
                }
                return o1.compareTo(o2);
            }
        });
        int j = KEYWORDS.length;
        for(int i = 0; i < j; i++) {
            String strKeyword = KEYWORDS[i];
//            System.out.println(String.valueOf(i) + " " + strKeyword.length() + " " + strKeyword);
            rules.add(new VdmrtWordRule(strKeyword, vdmKeyword));
        }

        // ルールの設定
        setPredicateRules((IPredicateRule[])rules.toArray(new IPredicateRule[rules.size()]));
    }
}
