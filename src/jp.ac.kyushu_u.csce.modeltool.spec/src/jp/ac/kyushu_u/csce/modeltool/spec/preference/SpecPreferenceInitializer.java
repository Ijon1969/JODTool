package jp.ac.kyushu_u.csce.modeltool.spec.preference;

import static jp.ac.kyushu_u.csce.modeltool.spec.constant.SpecPreferenceConstants.*;
import jp.ac.kyushu_u.csce.modeltool.base.utility.ColorName;
import jp.ac.kyushu_u.csce.modeltool.spec.ModelToolSpecPlugin;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;

/**
 * プリファレンスのデフォルト設定を行うクラス
 *
 * @author KBK yoshimura
 */
public class SpecPreferenceInitializer extends AbstractPreferenceInitializer {

	/**
	 * プリファレンスのデフォルト値設定
	 */
	public void initializeDefaultPreferences() {

		IPreferenceStore store = ModelToolSpecPlugin.getDefault().getPreferenceStore();

		/******** 検査設定 ********/
		// マークつき仕様書の出力先
		store.setDefault(PK_MARK_FIXED_PATH, ""); //$NON-NLS-1$

		// 検査モード
		store.setDefault(PK_INSPECT_MODE, PV_INSPECT_MODE_1);

		// 正規表現検索
//		store.setDefault(PK_USE_REGULAR_EXPRESSION, false);
		store.setDefault(PK_USE_REGULAR_EXPRESSION, true);  //正規表現をデフォルトに変更

		/******** 要求仕様書設定 ********/
		// 登録辞書の指定方法
		store.setDefault(PK_REGISTER_DICTIONARY, PV_REGISTER_SELECT);

		// 既定の登録先辞書
		store.setDefault(PK_REGISTER_FIXED_PATH, ""); //$NON-NLS-1$

		// 折り返し表示
		store.setDefault(PK_SPECEDITOR_WORDWRAP, false);

		// 辞書ビューとのリンク
		store.setDefault(PK_LINK_DICTIONARY, false);

		// 要求仕様書の強調表示背景色（キーワード）
		PreferenceConverter.setDefault(store, PK_SPEC_HIGHLIGHT_BACKCOLOR, ColorName.RGB_YELLOW);
		// 要求仕様書の強調表示前景色（キーワード）
		PreferenceConverter.setDefault(store, PK_SPEC_HIGHLIGHT_FORECOLOR, ColorName.RGB_BLACK);
		// 要求仕様書の強調表示背景色（副キーワード）
		PreferenceConverter.setDefault(store, PK_SPEC_SUB_HIGHLIGHT_BACKCOLOR, ColorName.RGB_YELLOW);
		// 要求仕様書の強調表示前景色（副キーワード）
		PreferenceConverter.setDefault(store, PK_SPEC_SUB_HIGHLIGHT_FORECOLOR, ColorName.RGB_BLACK);
		// 要求仕様書の強調表示背景色（活用形）
		PreferenceConverter.setDefault(store, PK_SPEC_CONJ_HIGHLIGHT_BACKCOLOR, ColorName.RGB_YELLOW);
		// 要求仕様書の強調表示前景色（活用形）
		PreferenceConverter.setDefault(store, PK_SPEC_CONJ_HIGHLIGHT_FORECOLOR, ColorName.RGB_BLACK);
	}
}
