package jp.ac.kyushu_u.csce.modeltool.dictionary.preference;

import static jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryPreferenceConstants.*;
import jp.ac.kyushu_u.csce.modeltool.dictionary.ModelToolDictionaryPlugin;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.Model;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.ModelManager;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;

/**
 * プリファレンスのデフォルト設定を行うクラス
 *
 * @author KBK yoshimura
 */
public class DictionaryPreferenceInitializer extends AbstractPreferenceInitializer {

	/**
	 * プリファレンスのデフォルト値設定
	 */
	public void initializeDefaultPreferences() {

		IPreferenceStore store = ModelToolDictionaryPlugin.getDefault().getPreferenceStore();

		/******** 辞書設定 ********/
		// 抽出・追加位置
		store.setDefault(PK_ENTRY_ADD_UNDER, true);

		// 辞書表示モード
		store.setDefault(PK_DICTIONARY_DISPLAY_MODE, PV_DIC_DISP_INSPECT);

		// 出力先フォルダの指定方法
		store.setDefault(PK_OUTPUT_FOLDER_SETTING, PV_OUTPUT_SELECT);

		// 既定の出力先フォルダ
		store.setDefault(PK_OUTPUT_FIXED_PATH, ""); //$NON-NLS-1$

		// 「元に戻す」ヒストリーのサイズ
		store.setDefault(PK_DICTIONARY_HISTORY_SIZE, PV_DICTIONARY_HISTORY_SIZE_DEFAULT);

		// クラスエントリ背景色
		ModelManager modelManager = ModelManager.getInstance();
		for (String modelKey : modelManager.getKeyList()) {
			Model model = modelManager.getModelByKey(modelKey);
			if (modelManager.getSectionBgCount(model) > 0) {
				for (Model.Section section : model.getSectionDefs()) {
					if (section.hasBg()) {
						String prefKey = PK_DICTIONARY_SECTION_BG + modelKey + "_" + //$NON-NLS-1$
											String.valueOf(section.getCd());
						PreferenceConverter.setDefault(store, prefKey, section.getDefaultBg());
					}
				}
			}
		}

		// 形式的定義の補完を使用する
		store.setDefault(PK_FORMAL_DEFINITION_COMPLETION, false);

		// モデル出力時にファイルを開く
		store.setDefault(PK_OPEN_OUTPUT_MODEL_FILE, false);
	}
}
