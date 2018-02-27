package jp.ac.kyushu_u.csce.modeltool.dictionary.dict;

import static jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryPreferenceConstants.*;

import jp.ac.kyushu_u.csce.modeltool.dictionary.ModelToolDictionaryPlugin;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * 辞書ビュー／辞書編集ビュー関連ユーティリティクラス
 *
 * @author KBK yoshimura
 */
public class DictionaryUtil {

	/**
	 * プリファレンスストア
	 */
	private static IPreferenceStore store;

	/**
	 * プリファレンスストアの取得
	 * @return
	 */
	public static IPreferenceStore getPreferenceStore() {
		if (store == null) {
			store = ModelToolDictionaryPlugin.getDefault().getPreferenceStore();
		}
		return store;
	}

	public static int getMode() {
		return getPreferenceStore().getInt(PK_DICTIONARY_DISPLAY_MODE);
	}

	/**
	 * 辞書ビューの表示モードが検査の場合にtrueを返す。
	 * @return
	 */
	public static boolean isInspectMode() {
		return checkMode(PV_DIC_DISP_INSPECT);
	}

	/**
	 * 辞書ビューの表示モードが出力の場合にtrueを返す。
	 * @return
	 */
	public static boolean isOutputMode() {
		return checkMode(PV_DIC_DISP_OUTPUT);
	}

	/**
	 * 表示モードのチェック - プリファレンスストアのモードと引数のモードが等しいかチェックする。
	 * @param mode モード
	 * @return
	 */
	private static boolean checkMode(int mode) {
		return mode == getPreferenceStore().getInt(PK_DICTIONARY_DISPLAY_MODE);
	}

	/**
	 * 表示モードの変更
	 * @param mode
	 */
	public static void changeMode(int mode) {

		// 現在のモード取得
		int current = getPreferenceStore().getInt(PK_DICTIONARY_DISPLAY_MODE);
		if (current == mode) {
			return;
		}

		// プリファレンスストアにセット
		getPreferenceStore().setValue(PK_DICTIONARY_DISPLAY_MODE, mode);
	}

	/**
	 * 表示モードへ変更する。
	 */
	public static void setInspectMode() {
		changeMode(PV_DIC_DISP_INSPECT);
	}

	/**
	 * 出力モードへ変更する。
	 */
	public static void setOutputMode() {
		changeMode(PV_DIC_DISP_OUTPUT);
	}
}
