package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;
import jp.ac.kyushu_u.csce.modeltool.dictionary.ModelToolDictionaryPlugin;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Dictionary;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.Model.Section;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

/**
 * 形式モデルインスタンス生成クラス
 *
 * @author KBK yoshimura
 */
public class ModelManager {

	/** モデルキー（既定値）*/
	public static final String DEFAULT_MODEL_KEY = DictionaryConstants.MODEL_KEY_VDMPP;

	/** インスタンス */
	private static ModelManager manager;

	/** 拡張レジストリ */
	private IExtensionRegistry fRegistry;

	/** 辞書－モデル対応付けマップ */
	private Map<Dictionary, Model> map = new HashMap<Dictionary, Model>();

	/** モデル管理マップ*/
	private Map<String, ModelItem> itemMap = new HashMap<String, ModelItem>();

	/** 空モデル */
	private static final Model EMPTY_MODEL = new EmptyModel();

	/** モデルキー - エディターID 対応付けマップ */
	private Map<String, String> editorMap = new HashMap<String, String>();

	/**
	 * コンストラクタ
	 */
	private ModelManager() {
		// 拡張レジストリ
		fRegistry = Platform.getExtensionRegistry();
		// 拡張設定
		initialize();
		initializeForOverture();
	}

	/**
	 * インスタンス生成
	 * @return インスタンス
	 */
	public static ModelManager getInstance() {
		if (manager == null) {
			manager = new ModelManager();
		}
		return manager;
	}

	/**
	 * 拡張ポイント情報の読込
	 */
	private void initialize() {
		// 拡張ポイント「jp.ac.kyushu_u.csce.modeltool.dictionary.formalModel」
		IExtensionPoint fPoint = fRegistry.getExtensionPoint(ModelToolDictionaryPlugin.PLUGIN_ID, "formalModel"); //$NON-NLS-1$
		// 拡張
		IExtension[] extensions = fPoint.getExtensions();
		for (IExtension extension : extensions) {

			// 拡張要素
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for(IConfigurationElement element : elements){

				// 拡張要素「model」
				if(element.getName().equals("model")){ //$NON-NLS-1$
					try {
						// 属性「key」
						String key = element.getAttribute("key"); //$NON-NLS-1$
						// 属性「name」
						String name = element.getAttribute("name"); //$NON-NLS-1$
						// 属性「class」
						Model model = (Model)element.createExecutableExtension("class"); //$NON-NLS-1$

						// Model定義のチェック
						for (Model.Section section : model.getSectionDefs()) {
							Assert.isTrue((section.hasBg() && section.getDefaultBg() != null) ||
									(!section.hasBg() && section.getDefaultBg() == null));
						}

						// モデル管理クラスへ追加
						addModelItem(key, name, model);

					} catch (CoreException e) {
						// エラーログ出力
						IStatus status = new Status(
								IStatus.ERROR, ModelToolDictionaryPlugin.PLUGIN_ID,
								"formal model extension error", e); //$NON-NLS-1$
						ModelToolDictionaryPlugin.getDefault().getLog().log(status);
					}
				}
			}
		}
	}

	/**
	 * 拡張ポイント情報の読込（Overture用）
	 */
	private void initializeForOverture() {
		// 拡張ポイント「jp.ac.kyushu_u.csce.modeltool.dictionary.outputModel」
		IExtensionPoint fPoint = fRegistry.getExtensionPoint(ModelToolDictionaryPlugin.PLUGIN_ID, "outputModel"); //$NON-NLS-1$
		// 拡張
		IExtension[] extensions = fPoint.getExtensions();
		for (IExtension extension : extensions) {

			// 拡張要素
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for(IConfigurationElement element : elements){

				// 拡張要素「modelEditor」
				if(element.getName().equals("modelEditor")){ //$NON-NLS-1$
					// 属性「key」
					String key = element.getAttribute("key"); //$NON-NLS-1$
					// 属性「editorId」
					String editorId = element.getAttribute("editorId"); //$NON-NLS-1$

					// マップへ追加
					editorMap.put(key, editorId);
				}
			}
		}
	}

	/**
	 * 形式モデルインスタンス生成
	 * @param dictionary 辞書
	 * @return 形式モデルインスタンス
	 */
	private Model create(Dictionary dictionary) {

		// 辞書情報の取得
		String modelKey = getModelKey(dictionary);

		// モデル管理マップから取得
		Model model = null;
		if (itemMap.containsKey(modelKey)) {
			model = itemMap.get(modelKey).model;
		} else {
			// モデル管理マップにない場合、デフォルトキーを使用
			model = EMPTY_MODEL;
			addModelItem(modelKey, modelKey, model);
		}

		// モデルマップに追加
		map.put(dictionary, model);

		return model;
	}

	/**
	 * 形式モデルインスタンス取得
	 * @param dictionary 辞書
	 * @return 形式モデルインスタンス
	 */
	public Model getModel(Dictionary dictionary) {
		if (map.containsKey(dictionary)) {
			return map.get(dictionary);
		} else {
			return create(dictionary);
		}
	}

	/**
	 * 形式モデルインスタンス更新
	 *   - 辞書のクラス情報が変更された場合に呼び出す。
	 * @param dictionary 辞書
	 */
	public Model updateModel(Dictionary dictionary) {
		return create(dictionary);
	}

	/**
	 * 形式モデル管理アイテムクラス
	 */
	private class ModelItem {
		/** 名称 （= 辞書情報ダイアログの形式モデルコンボックス表示名）*/
		String name;
		/** モデル */
		Model model;

		/**
		 * コンストラクタ
		 * @param key キー
		 * @param name 名称
		 * @param modelClass 形式モデル
		 */
		ModelItem(String name, Model model) {
			this.name = name;
			this.model = model;
		}
	}

	/**
	 * 形式モデル管理アイテムの追加
	 * @param key キー
	 * @param name 名称
	 * @param modelClass 形式モデルクラス
	 */
	private void addModelItem(String key, String name, Model model) {
		itemMap.put(key, new ModelItem(name, model));
	}

	/**
	 * モデルキーのリストを取得する
	 * @return モデルキーのリスト
	 */
	public List<String> getKeyList() {
		List<String> list = new ArrayList<String>();
		for (String key : itemMap.keySet()) {
			if (!(itemMap.get(key).model instanceof EmptyModel)) {
				list.add(key);
			}
		}
		Collections.sort(list);
		return list;
	}

	/**
	 * モデルキーからモデルインスタンスを取得する
	 * @param key モデルキー
	 * @return 形式モデルインスタンス
	 */
	public Model getModelByKey(String key) {
		return itemMap.get(key).model;
	}

	/**
	 * モデルキーから名称を取得する
	 * @param key モデルキー
	 * @return 名称
	 */
	public String getModelName(String key) {
		return itemMap.get(key).name;
	}

	/**
	 * 辞書から名称を取得する
	 * @param dictionary 辞書
	 * @return 名称
	 */
	public String getModelName(Dictionary dictionary) {
		return itemMap.get(getModelKey(dictionary)).name;
	}

	/**
	 * 非形式的定義（コード）を取得する
	 * @param dictionary 辞書
	 * @param name 非形式的定義（名称）
	 * @return 非形式的定義（コード）
	 */
	public int getSectionCd(Dictionary dictionary, String name) {
		return getSectionCd(getModelKey(dictionary), name);
	}

	/**
	 * 非形式的定義（コード）を取得する
	 * @param modelKey モデルキー
	 * @param name 非形式的定義（名称）
	 * @return 非形式的定義（コード）
	 */
	public int getSectionCd(String modelKey, String name) {
		ModelItem item = itemMap.get(modelKey);
		if (item != null) {
			return getSectionCd(item.model, name);
		}
		return 0;
	}

	/**
	 * 非形式的定義（コード）を取得する
	 * @param model モデル
	 * @param name 非形式的定義（名称）
	 * @return 非形式的定義（コード）
	 */
	public int getSectionCd(Model model, String name) {
		for (Model.Section section : model.getSectionDefs()) {
			if (section.getName().equals(name)) return section.getCd();
		}
		return 0;
	}

	/**
	 * 非形式的定義（名称）を取得する
	 * @param dictionary 辞書
	 * @param cd 非形式的定義（コード）
	 * @return 非形式的定義（名称）
	 */
	public String getSectionName(Dictionary dictionary, int cd) {
		return getSectionName(getModelKey(dictionary), cd);
	}

	/**
	 * 非形式的定義（名称）を取得する
	 * @param modelKey モデルキー
	 * @param cd 非形式的定義（コード）
	 * @return 非形式的定義（名称）
	 */
	public String getSectionName(String modelKey, int cd) {
		ModelItem item = itemMap.get(modelKey);
		if (item != null) {
			return getSectionName(item.model, cd);
		}
		return ""; //$NON-NLS-1$
	}

	/**
	 * 非形式的定義（名称）を取得する
	 * @param model モデル
	 * @param cd 非形式的定義（コード）
	 * @return 非形式的定義（名称）
	 */
	public String getSectionName(Model model, int cd) {
		for (Model.Section section : model.getSectionDefs()) {
			if (section.getCd() == cd) return section.getName();
		}
		return ""; //$NON-NLS-1$
	}

	/**
	 * 非形式的定義（背景色フラグ）を取得する
	 * @param dictionary 辞書
	 * @param cd 非形式的定義（コード）
	 * @return 非形式的定義（背景色フラグ）
	 */
	public boolean isSectionBg(Dictionary dictionary, int cd) {
		return isSectionBg(getModelKey(dictionary), cd);
	}

	/**
	 * 非形式的定義（背景色フラグ）を取得する
	 * @param modelKey モデルキー
	 * @param cd 非形式的定義（コード）
	 * @return 非形式的定義（背景色フラグ）
	 */
	public boolean isSectionBg(String modelKey, int cd) {
		ModelItem item = itemMap.get(modelKey);
		if (item != null) {
			return isSectionBg(item.model, cd);
		}
		return false;
	}

	/**
	 * 非形式的定義（背景色フラグ）を取得する
	 * @param model モデル
	 * @param cd 非形式的定義（コード）
	 * @return 非形式的定義（背景色フラグ）
	 */
	public boolean isSectionBg(Model model, int cd) {
		for (Model.Section section : model.getSectionDefs()) {
			if (section.getCd() == cd) return section.hasBg();
		}
		return false;
	}

	/**
	 * 非形式的定義（名称）の配列を取得する
	 * @param dictionary 辞書
	 * @return 非形式的定義（名称）の配列
	 */
	public String[] getSectionNames(Dictionary dictionary) {
		return getSectionNames(getModelKey(dictionary));
	}

	/**
	 * 非形式的定義（名称）の配列を取得する
	 * @param modelKey モデルキー
	 * @return 非形式的定義（名称）の配列
	 */
	public String[] getSectionNames(String modelKey) {
		ModelItem item = itemMap.get(modelKey);
		if (item != null) {
			return getSectionNames(item.model);
		}
		return new String[]{};
	}

	/**
	 * 非形式的定義（名称）の配列を取得する
	 * @param model モデル
	 * @return 非形式的定義（名称）の配列
	 */
	public String[] getSectionNames(Model model) {
		String[] names = new String[model.getSectionDefs().size()];
		for (int i=0; i< model.getSectionDefs().size(); i++) {
			names[i] = model.getSectionDefs().get(i).getName();
		}
		return names;
	}

	/**
	 * 背景色設定ありの種別数を取得する
	 * @param model モデル
	 * @return 背景色設定ありの種別数
	 */
	public int getSectionBgCount(Model model) {
		int cnt = 0;
		for (Model.Section section : model.getSectionDefs()) {
			if (section.hasBg()) cnt++;
		}
		return cnt;
	}

	/**
	 * モデルキーの取得
	 * @param dictionary 辞書
	 * @return モデルキー
	 */
	public String getModelKey(Dictionary dictionary) {
		return defaultModelKey(dictionary.getDictionaryClass().model);
	}

	/**
	 * デフォルトモデルキーの取得
	 * @param key モデルキー
	 * @return モデルキーが空の場合デフォルト値を返す。空でなければ引数のまま。
	 */
	private String defaultModelKey(String key) {
		return PluginHelper.defaultIfBlank(key, DEFAULT_MODEL_KEY);
	}

	/**
	 * 辞書をモデルマップから削除する
	 * @param dictionary 削除する辞書
	 */
	public void removeDictionary(Dictionary dictionary) {
		if (map.containsKey(dictionary)) {
			map.remove(dictionary);
		}
	}

	/**
	 * 登録済み（インストール済み）モデルかどうか判定する。
	 * @param modelKey モデルキー
	 * @return true - 登録済み／false - 未登録
	 */
	public boolean isResisteredModel(String modelKey) {
		// モデル管理マップ未登録の場合false
		if (!itemMap.containsKey(modelKey)) {
			return false;
		}
		// 登録済みでも空モデルの場合はfalse
		if (itemMap.get(modelKey).model instanceof EmptyModel) {
			return false;
		}
		return true;
	}

	/**
	 * 形式的定義のテンプレート文字列を取得する。
	 * @param modelKey モデルキー
	 * @param sectionCd 種別コード
	 * @return テンプレート文字列
	 */
	public String getTemplate(String modelKey, int sectionCd) {

		// 未登録の場合、処理なし
		if (!isResisteredModel(modelKey)) return null;

		// モデルの種別定義の中でコードが一致するものがあればテンプレートを返す
		Model model = getModelByKey(modelKey);
		for (Section section : model.getSectionDefs()) {
			if (sectionCd == section.getCd()) {
				return section.getTemplate();
			}
		}

		return null;
	}

	/**
	 * 出力モデルを開くエディターの設定有無
	 * @return 出力モデルを開くエディターの設定有無
	 */
	public boolean canOpenModelEditor() {
		return editorMap.size() > 0;
	}

	/**
	 * 出力モデルを開くエディターIDの取得
	 * @param modelKey キー
	 * @return エディターID
	 */
	public String getEditorIdByModelKey(String modelKey) {
		return editorMap.get(modelKey);
	}
}
