package jp.ac.kyushu_u.csce.modeltool.vdmEditor.editor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jface.text.rules.*;

/**
 * VDM単語ルールクラス
 *
 * @author KBK yoshimura
 */
public class VDMWordRule extends SingleLineRule {

	/**
	 * コンストラクタ
	 * @param sequence
	 * @param token
	 * @param escapeCharacter
	 * @param breaksOnEOF
	 * @param escapeContinuesLine
	 */
    protected VDMWordRule(String sequence, IToken token, char escapeCharacter, boolean breaksOnEOF, boolean escapeContinuesLine) {
        super(sequence, "", token, escapeCharacter, breaksOnEOF, escapeContinuesLine); //$NON-NLS-1$
    }

    /**
     * コンストラクタ
     * @param sequence
     * @param token
     * @param escapeCharacter
     * @param breaksOnEOF
     */
    protected VDMWordRule(String sequence, IToken token, char escapeCharacter, boolean breaksOnEOF) {
        super(sequence, "", token, escapeCharacter, breaksOnEOF); //$NON-NLS-1$
    }

    /**
     * コンストラクタ
     * @param sequence
     * @param token
     * @param escapeCharacter
     */
    protected VDMWordRule(String sequence, IToken token, char escapeCharacter) {
        super(sequence, "", token, escapeCharacter); //$NON-NLS-1$
    }

    /**
     * コンストラクタ
     * @param sequence
     * @param token
     */
    protected VDMWordRule(String sequence, IToken token) {
        super(sequence, "", token); //$NON-NLS-1$
    }

    /**
     * 終端文字列の検索
     * @see PatternRule#endSequenceDetected(ICharacterScanner)
     */
    protected boolean endSequenceDetected(ICharacterScanner scanner) {
        return true;
    }

    /**
     * ルールに合致するかを評価する
     * @see PatternRule#doEvaluate(ICharacterScanner, boolean)
     */
    protected IToken doEvaluate(ICharacterScanner scanner, boolean resume) {

    	// デフォルトトークン
        IToken token = Token.UNDEFINED;

        int adjust = 0;
        boolean searchFlg = false;
        StringBuffer sb = new StringBuffer();

        // スキャナ位置が先頭でない場合
        if(scanner.getColumn() > 0) {
            scanner.unread();
            adjust = 1;
        }
        // スキャナ位置-1から検索文字列+2文字分、文字列を取得
        for(int i = 0; i <= fStartSequence.length + adjust; i++) {
            int c = scanner.read();
            sb.append((char)c);
        }

        // スキャナ位置を元に戻す
        for(int i = 0; i <= fStartSequence.length; i++) {
        	scanner.unread();
        }

        // 検査用正規表現（検索文字列の前後に単語境界(\W)）
        Pattern pattern = Pattern.compile(
        		(scanner.getColumn() <= 0 ? "" : "\\W") + String.valueOf(fStartSequence) + "\\W"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Matcher matcher = pattern.matcher(sb.toString());
        searchFlg = matcher.matches();
        if(searchFlg) {
            super.doEvaluate(scanner, resume);
            token = fToken;
        }
        return token;
    }
}
