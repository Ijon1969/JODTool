package jp.ac.kyushu_u.csce.modeltool.dictionary.constant;

import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Dictionary;

/**
 * プリファレンス用定数定義クラス
 * @author KBK yoshimura
 */
public class DictionaryPreferenceConstants {

	private DictionaryPreferenceConstants() {};

	// 辞書設定
	/** key 抽出・追加位置（下に追加）*/
	public static final String PK_ENTRY_ADD_UNDER			= "entry_add_under"; //$NON-NLS-1$

	/** key 辞書表示モード */
	public static final String PK_DICTIONARY_DISPLAY_MODE	= "dictionary_display_mode"; //$NON-NLS-1$
	/** value 辞書表示モード：検査モード */
	public static final int PV_DIC_DISP_INSPECT			= Dictionary.SEQNO;
	/** value 辞書表示モード：出力モード */
	public static final int PV_DIC_DISP_OUTPUT			= Dictionary.OUTNO;

	/** key モデル出力フォルダの指定方法 */
	public static final String PK_OUTPUT_FOLDER_SETTING	= "output_folder_setting"; //$NON-NLS-1$
	/** key 既定の出力フォルダ */
	public static final String PK_OUTPUT_FIXED_PATH		= "output_fixed_path"; //$NON-NLS-1$
	/** value モデル出力フォルダの指定方法：既定フォルダ */
	public static final int PV_OUTPUT_FIXED				= 1;
	/** value モデル出力フォルダの指定方法：出力時指定 */
	public static final int PV_OUTPUT_SELECT				= 2;

	/** key Undoヒストリーのサイズ */
	public static final String PK_DICTIONARY_HISTORY_SIZE			= "dictionary_history_size"; //$NON-NLS-1$
	/** value Undoヒストリーのデフォルトサイズ */
	public static final int PV_DICTIONARY_HISTORY_SIZE_DEFAULT	= 200;
	/** value Undoヒストリーの最大サイズ */
	public static final int PV_DICTIONARY_HISTORY_SIZE_MAX		= 1000;

	/** key 辞書ビューのクラスエントリ背景色 */
	@Deprecated
	public static final String PK_DICTIONARY_CLASS_BACKCOLOR		= "dictionary_class_backcolor"; //$NON-NLS-1$
	/** key 辞書ビューのクラスエントリ背景色(プリフィックス) */
	public static final String PK_DICTIONARY_SECTION_BG			= "dictionary_section_bg_"; //$NON-NLS-1$

	/** key 形式的定義の補完の有無 */
	public static final String PK_FORMAL_DEFINITION_COMPLETION	= "formal_definition_completion"; //$NON-NLS-1$

	/** key モデル出力時にファイルを開く */
	public static final String PK_OPEN_OUTPUT_MODEL_FILE = "open_output_model_file"; //$NON-NLS-1$
}
