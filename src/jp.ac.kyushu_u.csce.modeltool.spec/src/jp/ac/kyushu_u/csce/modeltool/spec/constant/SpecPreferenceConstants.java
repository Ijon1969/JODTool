package jp.ac.kyushu_u.csce.modeltool.spec.constant;


/**
 * プリファレンス用定数定義クラス
 * @author KBK yoshimura
 */
public class SpecPreferenceConstants {

	private SpecPreferenceConstants() {};

	// 検査設定
	/** key マーク付き仕様書出力フォルダの指定方法 */
	public static final String PK_MARK_FOLDER_SETTING	= "mark_folder_setting"; //$NON-NLS-1$
	/** key マーク付き仕様書の出力先 */
	public static final String PK_MARK_FIXED_PATH 	= "mark_fixed_path"; //$NON-NLS-1$
	/** value マーク付き仕様書出力フォルダの指定方法：既定フォルダ */
	public static final int PV_MARK_FIXED				= 1;
	/** value マーク付き仕様書出力フォルダの指定方法：出力時選択 */
	public static final int PV_MARK_SELECT			= 2;

	// マーク色設定はBaseへ移動しました
//	/** key マーク色：名詞句（初回） */
//	public static final String PK_COLOR_NOUN			= "color_noun";
//	/** key マーク色：名詞句 */
//	public static final String PK_COLOR_NOUN_FIRST	= "color_noun_first";
//
//	/** key マーク色：動詞句（初回） */
//	public static final String PK_COLOR_VERB			= "color_verb";
//	/** key マーク色：動詞句 */
//	public static final String PK_COLOR_VERB_FIRST	= "color_verb_first";
//
//	/** key マーク色：状態（初回） */
//	public static final String PK_COLOR_STATE			= "color_state";
//	/** key マーク色：状態 */
//	public static final String PK_COLOR_STATE_FIRST	= "color_state_first";

	/** key 検査モード */
	public static final String PK_INSPECT_MODE		= "inspect_mode"; //$NON-NLS-1$
	/** value 検査モード：モード１ */
	public static final int PV_INSPECT_MODE_1			= 1;
	/** value 検査モード：モード２ */
	public static final int PV_INSPECT_MODE_2			= 2;

	/** key 正規表現モード */
	public static final String PK_USE_REGULAR_EXPRESSION	= "use_regular_expression"; //$NON-NLS-1$


	// 要求仕様書設定
	/** key 仕様書エディターの折り返し有無 */
	public static final String PK_SPECEDITOR_WORDWRAP		= "speceditor_wordwrap"; //$NON-NLS-1$

	/** key 登録辞書の指定方法 */
	public static final String PK_REGISTER_DICTIONARY		= "register_dictionary"; //$NON-NLS-1$
	/** key 既定の登録先辞書 */
	public static final String PK_REGISTER_FIXED_PATH		= "register_fixed_path"; //$NON-NLS-1$
	/** value 登録辞書の指定方法：既定辞書へ登録 */
	public static final int PV_REGISTER_FIXED				= 1;
	/** value 登録辞書の指定方法：登録時に選択 */
	public static final int PV_REGISTER_SELECT			= 2;
	/** value 登録辞書の指定方法：アクティブな辞書へ登録 */
	public static final int PV_REGISTER_ACTIVE			= 3;

	/** key 辞書ビューとのリンク有無 */
	public static final String PK_LINK_DICTIONARY			= "link_dictionary"; //$NON-NLS-1$
	/** key 要求仕様書の強調表示背景色（キーワード） */
	public static final String PK_SPEC_HIGHLIGHT_BACKCOLOR		= "spec_highlight_backcolor"; //$NON-NLS-1$
	/** key 要求仕様書の強調表示前景色（キーワード） */
	public static final String PK_SPEC_HIGHLIGHT_FORECOLOR		= "spec_highlight_forecolor"; //$NON-NLS-1$
	/** key 要求仕様書の強調表示背景色（副キーワード） */
	public static final String PK_SPEC_SUB_HIGHLIGHT_BACKCOLOR	= "spec_sub_highlight_backcolor"; //$NON-NLS-1$
	/** key 要求仕様書の強調表示前景色（副キーワード） */
	public static final String PK_SPEC_SUB_HIGHLIGHT_FORECOLOR	= "spec_sub_highlight_forecolor"; //$NON-NLS-1$
	/** key 要求仕様書の強調表示背景色（活用形） */
	public static final String PK_SPEC_CONJ_HIGHLIGHT_BACKCOLOR	= "spec_conj_highkight_backcolor"; //$NON-NLS-1$
	/** key 要求仕様書の強調表示前景色（活用形） */
	public static final String PK_SPEC_CONJ_HIGHLIGHT_FORECOLOR	= "spec_conj_highkight_forecolor"; //$NON-NLS-1$
}
