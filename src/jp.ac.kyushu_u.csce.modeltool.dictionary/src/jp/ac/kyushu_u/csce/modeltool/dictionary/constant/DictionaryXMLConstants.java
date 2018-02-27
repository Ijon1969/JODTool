package jp.ac.kyushu_u.csce.modeltool.dictionary.constant;

/**
 * XML用定数定義クラス
 * @author KBK yoshimura
 */
public class DictionaryXMLConstants {

	private DictionaryXMLConstants() {};

	/** 辞書ルート */
	public static final String TAG_DICTIONARY		= "dictionary"; //$NON-NLS-1$

	/** 見出し語 */
	public static final String TAG_ENTRIES		= "entries"; //$NON-NLS-1$
	/** 見出し語レコード */
	public static final String TAG_ENTRY			= "entry"; //$NON-NLS-1$
	/** 見出し語項目『検査用行番号』 */
	public static final String TAG_SEQNO			= "seqNo"; //$NON-NLS-1$
	/** 見出し語項目『出力用行番号』 */
	public static final String TAG_OUTNO			= "outNo"; //$NON-NLS-1$
	/** 見出し語項目『見出し語』 */
	public static final String TAG_WORD			= "word"; //$NON-NLS-1$
	/** 見出し語項目『種別』 */
	public static final String TAG_CATEGORY		= "category"; //$NON-NLS-1$
	/** 見出し語項目『種別No』 */
	@Deprecated
	public static final String TAG_CATEGORY_NO	= "categoryNo"; //$NON-NLS-1$
	/** 見出し語項目『非形式的定義』 */
	public static final String TAG_INFORMAL		= "informal"; //$NON-NLS-1$
	/** 見出し語項目『形式的定義』 */
	public static final String TAG_FORMAL			= "formal"; //$NON-NLS-1$
	/** 見出し語項目『形式的種別』 */
	public static final String TAG_SECTION		= "section"; //$NON-NLS-1$
	/** 見出し語項目『型』 */
	public static final String TAG_TYPE			= "type"; //$NON-NLS-1$

	/** クラス */
	public static final String TAG_CLASS			= "class"; //$NON-NLS-1$
	/** クラス『問題領域』 */
	public static final String TAG_DOMAIN			= "domain"; //$NON-NLS-1$
	/** クラス『プロジェクト名』 */
	public static final String TAG_PROJECT		= "project"; //$NON-NLS-1$
	/** クラス『入力言語』 */
	public static final String TAG_LANGUAGE		= "language"; //$NON-NLS-1$
	/** 『出力モデル』 */
	public static final String TAG_MODEL			= "model"; //$NON-NLS-1$

	/** 設定 */
	public static final String TAG_SETTING		= "setting"; //$NON-NLS-1$
	/** 種別設定 */
	public static final String TAG_CATEGORIES		= "categories"; //$NON-NLS-1$
	/** 種別個別設定 */
	public static final String TAG_CATEGORY_S		= "category"; //$NON-NLS-1$
	/** 種別個別設定『No』 */
	public static final String TAG_NO				= "no"; //$NON-NLS-1$
	/** 種別個別設定『種別名』 */
	public static final String TAG_NAME			= "name"; //$NON-NLS-1$
	/** 種別個別設定『マーク色：初回』 */
	public static final String TAG_PRIMARY		= "primary"; //$NON-NLS-1$
	/** 種別個別設定『マーク色：2回目以降』 */
	public static final String TAG_SECONDARY		= "secondary"; //$NON-NLS-1$
}
