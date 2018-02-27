package jp.ac.kyushu_u.csce.modeltool.base.constant;

/**
 * 定数クラス<br>
 * プラグイン分割に伴い使わなさそうな定数はコメントアウトしました。<br>
 * テスト後に不要なコメントは消しておきます。
 * @author KBK yoshimura
 */
public class ToolConstants {

	/** コンストラクタ */
	private ToolConstants() {}

	/** プラグインID ルート */
	private static final String PLUGIN_ID_ROOT = "jp.ac.kyushu_u.csce.modeltool"; //$NON-NLS-1$

	// パートID
//	/** 仕様書エディターID */
//	public static final String PART_ID_SPECEDITOR			= PLUGIN_ID_ROOT + ".speceditor";
//	/** 辞書ビューID */
//	public static final String PART_ID_DICTIONARY			= PLUGIN_ID_ROOT + ".dictionaryview";
//	/** 辞書編集ビューID */
//	public static final String PART_ID_DICTIONARY_EDIT	= PLUGIN_ID_ROOT + ".dictionary.editview";
	/** 仕様書エクスプローラービューID */
	public static final String PART_ID_EXPLORER			= PLUGIN_ID_ROOT + ".explorerview"; //$NON-NLS-1$

//	// コマンドID
//	//   仕様書エディター
//	/** コマンドID：抽出 */
//	public static final String COMMAND_ID_PICKOUT			= PLUGIN_ID_ROOT + ".speceditor.pickout";
//	/** コマンドID：検査 */
//	public static final String COMMAND_ID_INSPECT			= PLUGIN_ID_ROOT + ".speceditor.inspect";
//	/** コマンドID：折り返し */
//	public static final String COMMAND_ID_FOLDING			= PLUGIN_ID_ROOT + ".speceditor.folding";
//	/** コマンドID：正規表現 */
//	public static final String COMMAND_ID_REG_EX			= PLUGIN_ID_ROOT + ".speceditor.regex";
//	// 　辞書ビュー
//	/** コマンドID：形式モデル出力 */
//	public static final String COMMAND_ID_OUTPUT_MODEL	= PLUGIN_ID_ROOT + ".dictionaryview.outputmodel";
//	/** コマンドID：検査／出力モード切替 */
//	public static final String COMMAND_ID_CHANGE_MODE		= PLUGIN_ID_ROOT + ".dictionaryview.changemode";
//	/** コマンドID：見出し語の追加 */
//	public static final String COMMAND_ID_ADD				= PLUGIN_ID_ROOT + ".dictionaryview.add";
//	/** コマンドID：見出し語の削除 */
//	public static final String COMMAND_ID_REMOVE			= PLUGIN_ID_ROOT + ".dictionaryview.remove";
//	/** コマンドID：見出し語の上移動 */
//	public static final String COMMAND_ID_UP				= PLUGIN_ID_ROOT + ".dictionaryview.up";
//	/** コマンドID：見出し語の下移動 */
//	public static final String COMMAND_ID_DOWN			= PLUGIN_ID_ROOT + ".dictionaryview.down";
//	/** コマンドID：見出し語の切り取り */
//	public static final String COMMAND_ID_CUT				= PLUGIN_ID_ROOT + ".dictionaryview.cut";
//	/** コマンドID：見出し語のコピー */
//	public static final String COMMAND_ID_COPY			= PLUGIN_ID_ROOT + ".dictionaryview.copy";
//	/** コマンドID：見出し語の貼り付け */
//	public static final String COMMAND_ID_PASTE			= PLUGIN_ID_ROOT + ".dictionaryview.paste";
//	/** コマンドID：追加位置切替 */
//	public static final String COMMAND_ID_ADD_POSITION	= PLUGIN_ID_ROOT + ".dictionaryview.addposition";
//	/** コマンドID：辞書情報表示 */
//	public static final String COMMAND_ID_INFORMATION		= PLUGIN_ID_ROOT + ".dictionaryview.information";
//	/** コマンドID：辞書の新規作成 */
//	public static final String COMMAND_ID_CREATE			= PLUGIN_ID_ROOT + ".dictionaryview.create";
//	/** コマンドID：辞書の読込 */
//	public static final String COMMAND_ID_LOAD			= PLUGIN_ID_ROOT + ".dictionaryview.load";
//	/** コマンドID：辞書の保存 */
//	public static final String COMMAND_ID_SAVE			= PLUGIN_ID_ROOT + ".dictionaryview.save";
//	/** コマンドID：設定ページを開く */
//	public static final String COMMAND_ID_OPEN_PREFERENCE	= PLUGIN_ID_ROOT + ".dictionaryview.openPreference";
//	/** コマンドID：名前の変更 */
//	public static final String COMMAND_ID_RENAME			= PLUGIN_ID_ROOT + ".dictionaryview.rename";
//	// 仕様書／辞書エクスプローラビュー
//	/** コマンドID：リフレッシュ */
//	public static final String COMMAND_ID_REFRESH			= PLUGIN_ID_ROOT + ".explorerview.refresh";
//	/** コマンドID：名前の変更 */
//	public static final String COMMAND_ID_EXPLORER_RENAME	= PLUGIN_ID_ROOT + ".explorerview.rename";

	// プリファレンスページID
	/** プリファレンスページID：ツール設定 */
	public static final String PREF_PAGE_ID_TOOL			= PLUGIN_ID_ROOT + ".preference"; //$NON-NLS-1$
//	/** プリファレンスページID：要求仕様書設定 */
//	public static final String PREF_PAGE_ID_SPEC			= PLUGIN_ID_ROOT + ".preference.spec";
//	/** プリファレンスページID：辞書設定 */
//	public static final String PREF_PAGE_ID_DICTIONARY	= PLUGIN_ID_ROOT + ".preference.dictionary";
//	/** プリファレンスページID：検査設定 */
//	public static final String PREF_PAGE_ID_INSPECTION	= PLUGIN_ID_ROOT + ".preference.inspection";

	// キーバインドスコープID
//	/** キーバインドスコープID：仕様書エディター */
//	public static final String KB_SCOPE_ID_SPECEDITOR		= PLUGIN_ID_ROOT + ".speceditorScope";
//	/** キーバインドスコープID：辞書ビュー */
//	public static final String KB_SCOPE_ID_DICTIONARY		= PLUGIN_ID_ROOT + ".dictionaryviewScope";
	/** キーバインドスコープID：仕様書エクスプローラービュー */
	public static final String KB_SCOPE_ID_EXPLORER		= PLUGIN_ID_ROOT + ".explorerviewScope"; //$NON-NLS-1$

//	// コンテキストメニューID
//	/** コンテキストメニューID：辞書ビュー・辞書テーブル */
//	public static final String CTX_ID_DICTIONARY_TABLE	= PLUGIN_ID_ROOT + ".dictionaryview.table.context";
//	/** コンテキストメニューID：辞書ビュー・タブ */
//	public static final String CTX_ID_DICTIONARY_TAB		= PLUGIN_ID_ROOT + ".dictionaryview.tab.context";

	// エンコーディング
	/** エンコーディング：UTF-8 */
	public static final String ENCODING_UTF_8		= "UTF-8"; //$NON-NLS-1$

//	// 種別
//	/** 種別：なし */
//	public static final String CATEGORY_NONE		= "";
//	/** 種別：なし */
//	public static final String CATEGORY_NONE2		= "(指定なし)";
//	/** 種別：名詞句 */
//	public static final String CATEGORY_NOUN		= "名詞句";
//	/** 種別：動詞句 */
//	public static final String CATEGORY_VERB		= "動詞句";
//	/** 種別：状態 */
//	public static final String CATEGORY_STATE		= "状態";
//	/** 種別No：なし */
//	public static final int CATEGORY_NO_NONE		= 0;
//	/** 種別No：名詞句 */
//	public static final int CATEGORY_NO_NOUN		= 1;
//	/** 種別No：動詞句 */
//	public static final int CATEGORY_NO_VERB		= 2;
//	/** 種別No：状態 */
//	public static final int CATEGORY_NO_STATE		= 3;
//
//	/** 種別：合計 */
//	public static final String CATEGORY_TOTAL		= "合計";
//
//	/** 種別の配列 */
//	public static final String[] CATEGORIES = {
//		CATEGORY_NONE,
//		CATEGORY_NOUN,
//		CATEGORY_VERB,
//		CATEGORY_STATE,
//	};
//
//	// 形式的種別
//	/** 形式的種別：なし */
//	public static final String SECTION_NONE				= "";
//	/** 形式的種別：クラス */
//	public static final String SECTION_CLASS				= "クラス";
//	/** 形式的種別：変数 */
//	public static final String SECTION_INSTANCE_VARIABLE	= "変数";
//	/** 形式的種別：定数 */
//	public static final String SECTION_VALUE				= "定数";
//	/** 形式的種別：関数 */
//	public static final String SECTION_FUNCTION			= "関数";
//	/** 形式的種別：手続き */
//	public static final String SECTION_OPERATION			= "手続き";
//
//	/** 形式的種別No：なし */
//	public static final int SECTION_NO_NONE					= 0;
//	/** 形式的種別No：クラス */
//	public static final int SECTION_NO_CLASS					= 1;
//	/** 形式的種別No：変数 */
//	public static final int SECTION_NO_INSTANCE_VARIABLE		= 2;
//	/** 形式的種別No：定数 */
//	public static final int SECTION_NO_VALUE					= 3;
//	/** 形式的種別No：関数 */
//	public static final int SECTION_NO_FUNCTION				= 4;
//	/** 形式的種別No：手続き */
//	public static final int SECTION_NO_OPERATION				= 5;
//
//	/** 形式的種別の配列 */
//	public static final String[] SECTIONS = {
//		SECTION_NONE,
//		SECTION_CLASS,
//		SECTION_INSTANCE_VARIABLE,
//		SECTION_VALUE,
//		SECTION_FUNCTION,
//		SECTION_OPERATION,
//	};
//
//
//	// 辞書名
//	/** デフォルト辞書名 */
//	public static final String DEFAULT_DIC_NAME	= "Default";
//
//	// 見出し語
//	/** デフォルト見出し語 */
//	public static final String DEFAULT_ENTRY		= "default";
//

	// アイコンのファイルパス
	/** アイコン：dicfile.gif */
	public static final String IMG_DICFILE		= "icons/etool/dicfile.gif"; //$NON-NLS-1$
	/** アイコン：disabled.gif */
	public static final String IMG_DISABLED		= "icons/etool/disabled.gif"; //$NON-NLS-1$
	/** アイコン：enabled.gif */
	public static final String IMG_ENABLED		= "icons/etool/enabled.gif"; //$NON-NLS-1$
	/** アイコン：error.gif */
	public static final String IMG_ERROR			= "icons/etool/error.gif"; //$NON-NLS-1$
	/** アイコン：empty.gif */
	public static final String IMG_EMPTY			= "icons/etool/empty.gif"; //$NON-NLS-1$


	// 拡張子
	//  ※拡張子、拡張子配列の定数は仕様書ｴﾃﾞｨﾀｰﾌﾟﾗｸﾞｲﾝ(SpecConstants)、辞書編集ﾌﾟﾗｸﾞｲﾝ(DictionaryConstants)などと
	//    2重管理になっています。
	//    修正を行う場合は、関連するﾌﾟﾗｸﾞｲﾝの定数ｸﾗｽと一緒に修正してください。
	/**	拡張子：xml */
	public static final String EXTENSION_XML		= "xml"; //$NON-NLS-1$
	/** 拡張子：txt */
	public static final String EXTENSION_TXT		= "txt"; //$NON-NLS-1$
	/** 拡張子：html */
	public static final String EXTENSION_HTML		= "html"; //$NON-NLS-1$
	/** 拡張子：xls */
	public static final String EXTENSION_XLS		= "xls"; //$NON-NLS-1$
	/** 拡張子：xlsx */
	public static final String EXTENSION_XLSX		= "xlsx"; //$NON-NLS-1$
//	/** 拡張子：vpp */
//	public static final String EXTENSION_VPP		= "vpp"; //$NON-NLS-1$
	/** 拡張子：vdmpp */
	public static final String EXTENSION_VDMPP	= "vdmpp"; //$NON-NLS-1$
	/**	拡張子：jdd */
	public static final String EXTENSION_JDD		= "jdd"; //$NON-NLS-1$
	/**	拡張子：json */
	public static final String EXTENSION_JSON		= "json"; //$NON-NLS-1$
	/**	拡張子：csv */
	public static final String EXTENSION_CSV		= "csv"; //$NON-NLS-1$

	/** 仕様書ファイルの拡張子配列 */
	public static final String[] SPEC_EXTENSIONS = {
		EXTENSION_TXT,
		EXTENSION_HTML,
	};

	/** 仕様書Excelファイルの拡張子配列 */
	public static final String[] EXCEL_EXTENSIONS = {
		EXTENSION_XLS,
		EXTENSION_XLSX,
	};

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
}
