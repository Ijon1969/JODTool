package jp.ac.kyushu_u.csce.modeltool.spec.constant;

/**
 * 定数クラス
 * @author KBK yoshimura
 */
public class SpecConstants {

	private SpecConstants() {}

	private static final String PLUGIN_ID_ROOT = "jp.ac.kyushu_u.csce.modeltool"; //$NON-NLS-1$

	// パートID
	/** 仕様書エディターID */
	public static final String PART_ID_SPECEDITOR			= PLUGIN_ID_ROOT + ".speceditor"; //$NON-NLS-1$
	/** 仕様書エクスプローラービューID */
	public static final String PART_ID_EXPLORER			= PLUGIN_ID_ROOT + ".explorerview"; //$NON-NLS-1$

	// コマンドID
	//   仕様書エディター
	/** コマンドID：抽出 */
	public static final String COMMAND_ID_PICKOUT			= PLUGIN_ID_ROOT + ".speceditor.pickout"; //$NON-NLS-1$
	/** コマンドID：検査 */
	public static final String COMMAND_ID_INSPECT			= PLUGIN_ID_ROOT + ".speceditor.inspect"; //$NON-NLS-1$
	/** コマンドID：折り返し */
	public static final String COMMAND_ID_FOLDING			= PLUGIN_ID_ROOT + ".speceditor.folding"; //$NON-NLS-1$
	/** コマンドID：正規表現 */
	public static final String COMMAND_ID_REG_EX			= PLUGIN_ID_ROOT + ".speceditor.regex"; //$NON-NLS-1$
	/** コマンドID：辞書ビューとのリンク */
	public static final String COMMAND_ID_LINK			= PLUGIN_ID_ROOT + ".speceditor.link"; //$NON-NLS-1$
	// 仕様書／辞書エクスプローラビュー
	/** コマンドID：リフレッシュ */
	public static final String COMMAND_ID_REFRESH			= PLUGIN_ID_ROOT + ".explorerview.refresh"; //$NON-NLS-1$
	/** コマンドID：名前の変更 */
	public static final String COMMAND_ID_EXPLORER_RENAME	= PLUGIN_ID_ROOT + ".explorerview.rename"; //$NON-NLS-1$

	// プリファレンスページID
	/** プリファレンスページID：ツール設定 */
	public static final String PREF_PAGE_ID_TOOL			= PLUGIN_ID_ROOT + ".preference"; //$NON-NLS-1$
	/** プリファレンスページID：要求仕様書設定 */
	public static final String PREF_PAGE_ID_SPEC			= PLUGIN_ID_ROOT + ".preference.spec"; //$NON-NLS-1$
	/** プリファレンスページID：検査設定 */
	public static final String PREF_PAGE_ID_INSPECTION	= PLUGIN_ID_ROOT + ".preference.inspection"; //$NON-NLS-1$

	// キーバインドスコープID
	/** キーバインドスコープID：仕様書エディター */
	public static final String KB_SCOPE_ID_SPECEDITOR		= PLUGIN_ID_ROOT + ".speceditorScope"; //$NON-NLS-1$
	/** キーバインドスコープID：仕様書エクスプローラービュー */
	public static final String KB_SCOPE_ID_EXPLORER		= PLUGIN_ID_ROOT + ".explorerviewScope"; //$NON-NLS-1$

	// アイコンのファイルパス
	/** アイコン：dicfile.gif */
	public static final String IMG_DICFILE		= "icons/etool/dicfile.gif"; //$NON-NLS-1$
	/** アイコン：disabled.gif */
	public static final String IMG_DISABLED		= "icons/etool/disabled.gif"; //$NON-NLS-1$
	/** アイコン：enabled.gif */
	public static final String IMG_ENABLED		= "icons/etool/enabled.gif"; //$NON-NLS-1$

	// 拡張子
	//  ※拡張子、拡張子配列の定数は基底ﾌﾟﾗｸﾞｲﾝ(ToolConstants)などと2重管理になっています。
	//    修正を行う場合は、関連するﾌﾟﾗｸﾞｲﾝの定数ｸﾗｽと一緒に修正してください。
	/** 拡張子：txt */
	public static final String EXTENSION_TXT		= "txt"; //$NON-NLS-1$
	/** 拡張子：html */
	public static final String EXTENSION_HTML		= "html"; //$NON-NLS-1$
	/** 拡張子：xls */
	public static final String EXTENSION_XLS		= "xls"; //$NON-NLS-1$
	/** 拡張子：xlsx */
	public static final String EXTENSION_XLSX		= "xlsx"; //$NON-NLS-1$

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
}
