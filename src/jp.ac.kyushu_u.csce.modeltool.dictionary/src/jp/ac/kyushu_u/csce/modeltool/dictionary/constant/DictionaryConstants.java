package jp.ac.kyushu_u.csce.modeltool.dictionary.constant;

import jp.ac.kyushu_u.csce.modeltool.dictionary.Messages;

/**
 * 定数クラス
 * @author KBK yoshimura
 */
public class DictionaryConstants {

	private DictionaryConstants() {}

	private static final String PLUGIN_ID_ROOT = "jp.ac.kyushu_u.csce.modeltool"; //$NON-NLS-1$

	// パートID
	/** 辞書ビューID */
	public static final String PART_ID_DICTIONARY			= PLUGIN_ID_ROOT + ".dictionaryview"; //$NON-NLS-1$
	/** 辞書編集ビューID */
	public static final String PART_ID_DICTIONARY_EDIT	= PLUGIN_ID_ROOT + ".dictionary.editview"; //$NON-NLS-1$

	// コマンドID
	// 　辞書ビュー
	/** コマンドID：形式モデル出力 */
	public static final String COMMAND_ID_OUTPUT_MODEL	= PLUGIN_ID_ROOT + ".dictionaryview.outputmodel"; //$NON-NLS-1$
	/** コマンドID：検査／出力モード切替 */
	public static final String COMMAND_ID_CHANGE_MODE		= PLUGIN_ID_ROOT + ".dictionaryview.changemode"; //$NON-NLS-1$
	/** コマンドID：見出し語の追加 */
	public static final String COMMAND_ID_ADD				= PLUGIN_ID_ROOT + ".dictionaryview.add"; //$NON-NLS-1$
	/** コマンドID：見出し語の削除 */
	public static final String COMMAND_ID_REMOVE			= PLUGIN_ID_ROOT + ".dictionaryview.remove"; //$NON-NLS-1$
	/** コマンドID：見出し語の上移動 */
	public static final String COMMAND_ID_UP				= PLUGIN_ID_ROOT + ".dictionaryview.up"; //$NON-NLS-1$
	/** コマンドID：見出し語の下移動 */
	public static final String COMMAND_ID_DOWN			= PLUGIN_ID_ROOT + ".dictionaryview.down"; //$NON-NLS-1$
	/** コマンドID：見出し語の切り取り */
	public static final String COMMAND_ID_CUT				= PLUGIN_ID_ROOT + ".dictionaryview.cut"; //$NON-NLS-1$
	/** コマンドID：見出し語のコピー */
	public static final String COMMAND_ID_COPY			= PLUGIN_ID_ROOT + ".dictionaryview.copy"; //$NON-NLS-1$
	/** コマンドID：見出し語の貼り付け */
	public static final String COMMAND_ID_PASTE			= PLUGIN_ID_ROOT + ".dictionaryview.paste"; //$NON-NLS-1$
	/** コマンドID：追加位置切替 */
	public static final String COMMAND_ID_ADD_POSITION	= PLUGIN_ID_ROOT + ".dictionaryview.addposition"; //$NON-NLS-1$
	/** コマンドID：辞書情報表示 */
	public static final String COMMAND_ID_INFORMATION		= PLUGIN_ID_ROOT + ".dictionaryview.information"; //$NON-NLS-1$
	/** コマンドID：辞書の新規作成 */
	public static final String COMMAND_ID_CREATE			= PLUGIN_ID_ROOT + ".dictionaryview.create"; //$NON-NLS-1$
	/** コマンドID：辞書の読込 */
	public static final String COMMAND_ID_LOAD			= PLUGIN_ID_ROOT + ".dictionaryview.load"; //$NON-NLS-1$
	/** コマンドID：辞書の保存 */
	public static final String COMMAND_ID_SAVE			= PLUGIN_ID_ROOT + ".dictionaryview.save"; //$NON-NLS-1$
	/** コマンドID：設定ページを開く */
	public static final String COMMAND_ID_OPEN_PREFERENCE	= PLUGIN_ID_ROOT + ".dictionaryview.openPreference"; //$NON-NLS-1$
	/** コマンドID：名前の変更 */
	public static final String COMMAND_ID_RENAME			= PLUGIN_ID_ROOT + ".dictionaryview.rename"; //$NON-NLS-1$
	/** コマンドID：インポート */
	public static final String COMMAND_ID_IMPORT			= PLUGIN_ID_ROOT + ".dictionaryview.import"; //$NON-NLS-1$
	/** コマンドID：エクスポート */
	public static final String COMMAND_ID_EXPORT			= PLUGIN_ID_ROOT + ".dictionaryview.export"; //$NON-NLS-1$
	/** コマンドID：元に戻す */
	public static final String COMMAND_ID_UNDO			= PLUGIN_ID_ROOT + ".dictionaryview.undo"; //$NON-NLS-1$
	/** コマンドID：やり直し */
	public static final String COMMAND_ID_REDO			= PLUGIN_ID_ROOT + ".dictionaryview.redo"; //$NON-NLS-1$

	// プリファレンスページID
	/** プリファレンスページID：辞書設定 */
	public static final String PREF_PAGE_ID_DICTIONARY	= PLUGIN_ID_ROOT + ".preference.dictionary"; //$NON-NLS-1$

	// キーバインドスコープID
	/** キーバインドスコープID：辞書ビュー */
	public static final String KB_SCOPE_ID_DICTIONARY		= PLUGIN_ID_ROOT + ".dictionaryviewScope"; //$NON-NLS-1$

	// コンテキストメニューID
	/** コンテキストメニューID：辞書ビュー・辞書テーブル */
	public static final String CTX_ID_DICTIONARY_TABLE	= PLUGIN_ID_ROOT + ".dictionaryview.table.context"; //$NON-NLS-1$
	/** コンテキストメニューID：辞書ビュー・タブ */
	public static final String CTX_ID_DICTIONARY_TAB		= PLUGIN_ID_ROOT + ".dictionaryview.tab.context"; //$NON-NLS-1$

	// 種別
	/** 種別：なし */
	public static final String CATEGORY_NONE		= ""; //$NON-NLS-1$
	/** 種別：なし */
	public static final String CATEGORY_NONE2		= Messages.DictionaryConstants_24;
	/** 種別：名詞句 */
	public static final String CATEGORY_NOUN		= Messages.DictionaryConstants_25;
	/** 種別：動詞句 */
	public static final String CATEGORY_VERB		= Messages.DictionaryConstants_26;
	/** 種別：状態 */
	public static final String CATEGORY_STATE		= Messages.DictionaryConstants_27;
	/** 種別No：なし */
	public static final int CATEGORY_NO_NONE		= 0;
	/** 種別No：名詞句 */
	public static final int CATEGORY_NO_NOUN		= 1;
	/** 種別No：動詞句 */
	public static final int CATEGORY_NO_VERB		= 2;
	/** 種別No：状態 */
	public static final int CATEGORY_NO_STATE		= 3;

	/** 種別：合計 */
	public static final String CATEGORY_TOTAL		= Messages.DictionaryConstants_28;

	/** 種別の配列 */
	public static final String[] CATEGORIES = {
		CATEGORY_NONE,
//		CATEGORY_NOUN,			// 種別1～3も変更可にするため削除
//		CATEGORY_VERB,
//		CATEGORY_STATE,
	};

	// 形式的種別
	/** 形式的種別：なし */
	public static final String SECTION_NONE				= ""; //$NON-NLS-1$
	/** 形式的種別：クラス */
	public static final String SECTION_CLASS				= Messages.DictionaryConstants_30;
	/** 形式的種別：変数 */
	public static final String SECTION_INSTANCE_VARIABLE	= Messages.DictionaryConstants_31;
	/** 形式的種別：定数 */
	public static final String SECTION_VALUE				= Messages.DictionaryConstants_32;
	/** 形式的種別：関数 */
	public static final String SECTION_FUNCTION			= Messages.DictionaryConstants_33;
	/** 形式的種別：手続き */
	public static final String SECTION_OPERATION			= Messages.DictionaryConstants_34;
	/** 形式的種別：型 */
	public static final String SECTION_TYPE				= Messages.DictionaryConstants_2;
	/** 形式的種別：スレッド */
	public static final String SECTION_THREAD				= Messages.DictionaryConstants_35;
	/** 形式的種別：トレース */
	public static final String SECTION_TRACE				= Messages.DictionaryConstants_36;

	/** 形式的種別No：なし */
	public static final int SECTION_NO_NONE					= 0;
	/** 形式的種別No：クラス */
	public static final int SECTION_NO_CLASS					= 1;
	/** 形式的種別No：変数 */
	public static final int SECTION_NO_INSTANCE_VARIABLE		= 2;
	/** 形式的種別No：定数 */
	public static final int SECTION_NO_VALUE					= 3;
	/** 形式的種別No：関数 */
	public static final int SECTION_NO_FUNCTION				= 4;
	/** 形式的種別No：手続き */
	public static final int SECTION_NO_OPERATION				= 5;
	/** 形式的種別No：型 */
	public static final int SECTION_NO_TYPE					= 6;
	// 7, 8はVDM-SLで使用するため欠番
	/** 形式的種別No：スレッド */
	public static final int SECTION_NO_THREAD					= 9;
	/** 形式的種別No：トレース */
	public static final int SECTION_NO_TRACE					= 10;

//	/** 形式的種別の配列 */
//	public static final String[] SECTIONS = {
//		SECTION_NONE,
//		SECTION_CLASS,
//		SECTION_INSTANCE_VARIABLE,
//		SECTION_VALUE,
//		SECTION_FUNCTION,
//		SECTION_OPERATION,
//	};

	// 辞書名
	/** デフォルト辞書名 */
	public static final String DEFAULT_DIC_NAME	= "Default"; //$NON-NLS-1$

	// 見出し語
	/** デフォルト見出し語 */
	public static final String DEFAULT_ENTRY		= "default"; //$NON-NLS-1$

	// アイコンのファイルパス
	/** アイコン：disabled.gif */
	public static final String IMG_DISABLED		= "icons/edic/disabled.gif"; //$NON-NLS-1$
	/** アイコン：enabled.gif */
	public static final String IMG_ENABLED		= "icons/edic/enabled.gif"; //$NON-NLS-1$
	/** アイコン：error.gif */
	public static final String IMG_ERROR			= "icons/edic/error.gif"; //$NON-NLS-1$
	/** アイコン：export.gif */
	public static final String IMG_EXPORT			= "icons/edic/export.gif"; //$NON-NLS-1$
	/** アイコン：not_export.gif */
	public static final String IMG_NOT_EXPORT		= "icons/edic/not_export.gif"; //$NON-NLS-1$
	/** アイコン：editing.gif */
	public static final String IMG_EDITING		= "icons/edic/editing.gif"; //$NON-NLS-1$
	/** アイコン：warning.gif */
	public static final String IMG_WARNING		= "icons/edic/warning.gif"; //$NON-NLS-1$

	// 拡張子
	//  ※拡張子、拡張子配列の定数は基底ﾌﾟﾗｸﾞｲﾝ(ToolConstants)などと2重管理になっています。
	//    修正を行う場合は、関連するﾌﾟﾗｸﾞｲﾝの定数ｸﾗｽと一緒に修正してください。
	/**	拡張子：xml */
	public static final String EXTENSION_XML		= "xml"; //$NON-NLS-1$
	/**	拡張子：jdd */
	public static final String EXTENSION_JDD		= "jdd"; //$NON-NLS-1$
//	/** 拡張子：vpp */
//	public static final String EXTENSION_VPP		= "vpp"; //$NON-NLS-1$
	/** 拡張子：vdmpp */
	public static final String EXTENSION_VDMPP	= "vdmpp"; //$NON-NLS-1$
	/** 拡張子：json */
	public static final String EXTENSION_JSON		= "json"; //$NON-NLS-1$
	/** 拡張子：csv */
	public static final String EXTENSION_CSV		= "csv"; //$NON-NLS-1$

	/** 辞書ファイルの拡張子配列 */
	public static final String[] DICTIONARY_EXTENSIONS = {
//		EXTENSION_XML,
		EXTENSION_JDD,
	};

	/** モデルファイルの拡張子配列 */
	public static final String[] MODEL_EXTENSIONS = {
//		EXTENSION_VPP,
		EXTENSION_VDMPP,
	};

	// 入力言語の最大数
	/** 入力言語の最大数 */
	public static final int MAX_LANGUAGES_COUNT	= 5;

	// カラムの最大数
	/** 最大カラム数：副キーワード */
	public static final int MAX_COL_SUBWORD		= 5;
	/** 最大カラム数：活用形 */
	public static final int MAX_COL_CONJUGATION	= 5;
	/** 最大カラム数：非形式的定義 */
	public static final int MAX_COL_INFORMAL		= MAX_LANGUAGES_COUNT;

	// 種別の最大数
	/** 種別№の最大値 */
	public static final int MAX_CATEGORY_NO = 20;

	// カラムID
	/** 辞書ビュー カラムID：編集 */
	public static final int DIC_COL_ID_EDIT			= 1;
	/** 辞書ビュー カラムID：行番号 */
	public static final int DIC_COL_ID_NUMBER		= 2;
	/** 辞書ビュー カラムID：見出し語 */
	public static final int DIC_COL_ID_WORD			= 3;
	/** 辞書ビュー カラムID：種別 */
	public static final int DIC_COL_ID_CATEGORY		= 4;
	/** 辞書ビュー カラムID：非形式的定義 */
	public static final int DIC_COL_ID_INFORMAL		= 5;
	/** 辞書ビュー カラムID：形式的種別 */
	public static final int DIC_COL_ID_SECTION		= 6;
	/** 辞書ビュー カラムID：形式的定義 */
	public static final int DIC_COL_ID_FORMAL		= 7;
	/** 辞書ビュー カラムID：型 */
	public static final int DIC_COL_ID_TYPE			= 8;
	/** 辞書ビュー カラムID：副キーワード */
	public static final int DIC_COL_ID_SUBWORD		= 9;
	/** 辞書ビュー カラムID：活用形 */
	public static final int DIC_COL_ID_CONJUGATION	= 10;
	/** 辞書ビュー カラムID：出力 */
	public static final int DIC_COL_ID_OUTPUT		= 11;

	/** 辞書ビュー カラムID：未指定 */
	public static final int DIC_COL_ID_NONE			= -1;	// 履歴で使用する

	// 表示モード
	/** 表示モード：検査 */
	public static final String DISPLAY_MODE_INSPECT	= Messages.DictionaryConstants_0;
	/** 表示モード：出力 */
	public static final String DISPLAY_MODE_OUTPUT	= Messages.DictionaryConstants_1;

	// propertiesファイル
//	/** propertiesファイル：言語コード */ ⇒ LanguageUtil.javaに移動
//	public static final String PROP_FILE_LANGUAGES	= "languages.properties"; //$NON-NLS-1$

	// 形式モデルキー
	/** 形式モデルキー：VDM++ */
	public static final String MODEL_KEY_VDMPP	= "vdmpp"; //$NON-NLS-1$
}
