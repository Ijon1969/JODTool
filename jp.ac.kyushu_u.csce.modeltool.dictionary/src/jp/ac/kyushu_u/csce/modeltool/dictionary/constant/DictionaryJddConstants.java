package jp.ac.kyushu_u.csce.modeltool.dictionary.constant;

/**
 * JDD用定数定義クラス
 * @author KBK yoshimura
 */
public class DictionaryJddConstants {

	private DictionaryJddConstants() {};

	/** 辞書ルート */
	public static final String TAG_DICTIONARY		= "dictionary"; //$NON-NLS-1$

	/** 見出し語 */
	public static final String TAG_ENTRIES		= "entries"; //$NON-NLS-1$
	/** 見出し語レコード */
	public static final String TAG_ENTRY			= "entry"; //$NON-NLS-1$
	/** 見出し語項目『検査用行番号』 */
	public static final String TAG_SEQ_NO			= "seqNo"; //$NON-NLS-1$
	/** 見出し語項目『出力用行番号』 */
	public static final String TAG_OUTPUT_NO		= "outputNo"; //$NON-NLS-1$
	/** 見出し語項目『見出し語』 */
	public static final String TAG_KEYWORD		= "word"; //$NON-NLS-1$
	/** 見出し語項目『種別』 */
	public static final String TAG_CATEGORY		= "category"; //$NON-NLS-1$
	/** 見出し語項目『非形式的定義』リスト */
	public static final String TAG_INFORMALS		= "informals"; //$NON-NLS-1$
	/** 見出し語項目『非形式的定義』 */
	public static final String TAG_INFORMAL		= "informal"; //$NON-NLS-1$
	/** 見出し語項目『形式的定義』 */
	public static final String TAG_FORMAL			= "formal"; //$NON-NLS-1$
	/** 見出し語項目『形式的種別』 */
	public static final String TAG_SECTION		= "section"; //$NON-NLS-1$
	/** 見出し語項目『型』
	 * @deprecated */
	public static final String TAG_TYPE			= "type"; //$NON-NLS-1$
	/** 見出し語項目『副キーワード』リスト */
	public static final String TAG_SUB_KEYWORDS	= "subKeywords"; //$NON-NLS-1$
	/** 見出し語項目『副キーワード』 */
	public static final String TAG_SUB_KEYWORD	= "subKeyword"; //$NON-NLS-1$
	/** 見出し語項目『活用形』リスト */
	public static final String TAG_CONJUGATIONS	= "conjugations"; //$NON-NLS-1$
	/** 見出し語項目『活用形』 */
	public static final String TAG_CONJUGATION	= "conjugation"; //$NON-NLS-1$
	/** 見出し語項目『拡張元見出し語』 */
	public static final String TAG_OVERRIDE		= "override"; //$NON-NLS-1$
	/** 見出し語項目『拡張ターゲット』 */
	public static final String TAG_TARGET			= "target"; //$NON-NLS-1$

	/** クラス */
	public static final String TAG_CLASS			= "class"; //$NON-NLS-1$
	/** クラス『問題領域』 */
	public static final String TAG_DOMAIN			= "domain"; //$NON-NLS-1$
	/** クラス『プロジェクト名』 */
	public static final String TAG_PROJECT		= "project"; //$NON-NLS-1$
	/** クラス『入力言語』リスト */
	public static final String TAG_LANGUAGES		= "languages"; //$NON-NLS-1$
	/** クラス『入力言語』 */
	public static final String TAG_LANGUAGE		= "language"; //$NON-NLS-1$
	/** クラス『出力モデル』 */
	public static final String TAG_MODEL			= "model"; //$NON-NLS-1$
	/** クラス『拡張元辞書』 */
	public static final String TAG_EXTEND			= "extend"; //$NON-NLS-1$

	/** 設定 */
	public static final String TAG_SETTING		= "setting"; //$NON-NLS-1$
	/** 種別設定 */
	public static final String TAG_CATEGORIES		= "categories"; //$NON-NLS-1$
	/** 種別個別設定 */
	// TAG_CATEGORY と同じなのでこちらは使用しない
//	public static final String TAG_CATEGORY_S		= "category"; //$NON-NLS-1$
	/** 種別個別設定『No』 */
	public static final String TAG_NO				= "no"; //$NON-NLS-1$
	/** 種別個別設定『種別名』 */
	public static final String TAG_NAME			= "name"; //$NON-NLS-1$
	/** 種別個別設定『マーク色：初回』 */
	public static final String TAG_PRIMARY		= "primary"; //$NON-NLS-1$
	/** 種別個別設定『マーク色：2回目以降』 */
	public static final String TAG_SECONDARY		= "secondary"; //$NON-NLS-1$

	/** 汎用TEXT */
	public static final String TAG_TEXT			= "text"; //$NON-NLS-1$

	/** 属性：ID */
	public static final String ATTR_ID			= "id"; //$NON-NLS-1$
	/** 属性：言語ID */
	public static final String ATTR_LANG_ID		= "langId"; //$NON-NLS-1$
	/** 属性：バージョン */
	public static final String ATTR_VERSION		= "version"; //$NON-NLS-1$
	/** 属性：日付 */
	public static final String ATTR_DATE			= "date"; //$NON-NLS-1$
}
