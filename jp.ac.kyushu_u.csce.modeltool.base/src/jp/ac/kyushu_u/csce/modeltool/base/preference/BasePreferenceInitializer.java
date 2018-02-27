package jp.ac.kyushu_u.csce.modeltool.base.preference;

import jp.ac.kyushu_u.csce.modeltool.base.ModelToolBasePlugin;
import jp.ac.kyushu_u.csce.modeltool.base.constant.BasePreferenceConstants;
import jp.ac.kyushu_u.csce.modeltool.base.utility.ColorName;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;

/**
 * プリファレンスのデフォルト設定を行うクラス
 *
 * @author KBK yoshimura
 */
public class BasePreferenceInitializer extends AbstractPreferenceInitializer {

	/**
	 * プリファレンスのデフォルト値設定
	 */
	public void initializeDefaultPreferences() {

		IPreferenceStore store = ModelToolBasePlugin.getDefault().getPreferenceStore();

		// マーク色設定
		//   設定ページはBaseではなく、Specプラグインで管理しています
		// 　　　　　　初回　　　　　　　2回目～
		// 　名詞句　red (#ff0000)　　magenta(#ff00ff)
		// 　動詞句　lime(#00ff00)　　yellow (#ffff00)
		// 　状態　　blue(#0000ff)　　cyan   (#00ffff)
		store.setDefault(BasePreferenceConstants.PK_COLOR_NOUN_FIRST, StringConverter.asString(ColorName.RGB_RED));
		store.setDefault(BasePreferenceConstants.PK_COLOR_NOUN, StringConverter.asString(ColorName.RGB_MAGENTA));

		store.setDefault(BasePreferenceConstants.PK_COLOR_VERB_FIRST, StringConverter.asString(ColorName.RGB_LIME));
		store.setDefault(BasePreferenceConstants.PK_COLOR_VERB, StringConverter.asString(ColorName.RGB_YELLOW));

		store.setDefault(BasePreferenceConstants.PK_COLOR_STATE_FIRST, StringConverter.asString(ColorName.RGB_BLUE));
		store.setDefault(BasePreferenceConstants.PK_COLOR_STATE, StringConverter.asString(ColorName.RGB_CYAN));

	}
}
