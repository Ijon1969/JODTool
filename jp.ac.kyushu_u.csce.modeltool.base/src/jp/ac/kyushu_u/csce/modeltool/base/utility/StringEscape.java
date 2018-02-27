package jp.ac.kyushu_u.csce.modeltool.base.utility;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * 文字列のエスケープ処理を行うクラス
 *
 * @author KBK yoshimura
 */
public class StringEscape {

	/**
	 * エスケープ文字のマップ<br>
	 * key - エスケープ前文字<br>
	 * value - エスケープ後文字列
	 */
	private static final Map<Character, String> escapeMap = new LinkedHashMap<Character, String>();
	static {
		escapeMap.put('"', "&quot;"); //$NON-NLS-1$
		escapeMap.put('&', "&amp;"); //$NON-NLS-1$
		escapeMap.put('<', "&lt;"); //$NON-NLS-1$
		escapeMap.put('>', "&gt;"); //$NON-NLS-1$
		escapeMap.put(' ', "&nbsp;"); //$NON-NLS-1$
	}

	/**
	 * アンエスケープ文字のマップ<br>
	 * <code>escapeMap</code> のkey, valueを逆にしたもの
	 *
	 * @deprecated 一応用意はしたけど、実際には使ってない
	 */
	private static final Map<String, Character> unescapeMap = new LinkedHashMap<String, Character>();
	static {
		for (char c : escapeMap.keySet()) {
			unescapeMap.put(escapeMap.get(c), c);
		}
	}

	/**
	 * HTMLエスケープ<br>
	 * {@link StringEscapeUtils#escapeHtml(String)}では、マルチバイト文字すべてをエスケープするため、
	 * 独自メソッドでエスケープを行っています
	 * @param s 文字列
	 * @return エスケープ結果
	 */
	public static String escapeHtml(String s) {
		if (s == null) {
			return null;
		}

		StringBuffer escaped = new StringBuffer();
		for (int i=0; i<s.length(); i++) {
			char c = s.charAt(i);
			if (escapeMap.containsKey(c)) {
				escaped.append(escapeMap.get(c));
			} else {
				escaped.append(c);
			}
		}

		return escaped.toString();
	}

	/**
	 * HTMLアンエスケープ<br>
	 * {@link StringEscapeUtils#unescapeHtml(String)}では、ノーブレークスペース(&nbsp;)から
	 * 半角スペースへの変換ができないため、独自メソッドでアンエスケープを行っています
	 * @param s
	 * @return
	 */
	public static String unescapeHtml(String s) {
		if (s == null) {
			return null;
		}

		// ノーブレークスペースを半角スペースに置換
		String unescaped = s.replace("&nbsp;", " "); //$NON-NLS-1$ //$NON-NLS-2$

		return StringEscapeUtils.unescapeHtml(unescaped);
	}
}
