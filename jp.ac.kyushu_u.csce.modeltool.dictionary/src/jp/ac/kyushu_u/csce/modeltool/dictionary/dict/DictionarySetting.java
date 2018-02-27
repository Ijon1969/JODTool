package jp.ac.kyushu_u.csce.modeltool.dictionary.dict;

import java.util.Arrays;

import jp.ac.kyushu_u.csce.modeltool.base.ModelToolBasePlugin;
import jp.ac.kyushu_u.csce.modeltool.base.constant.BasePreferenceConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

/**
 * 辞書設定情報クラス<br>
 * 辞書のマーク色設定を管理する。
 *
 * @author KBK yoshimura
 */
public class DictionarySetting implements DictionaryInfo {

	/** 種別設定クラス */
	public class Category {
		/** No */
		private int no;
		/** 種別名 */
		private String name;
		/** 初回 */
		private RGB primary;
		/** 2回目～ */
		private RGB secondary;

		/**
		 * Noの取得
		 * @return No
		 */
		public int getNo() {
			return no;
		}
		/**
		 * 種別名の取得
		 * @return 種別名
		 */
		public String getName() {
			return name;
		}
		/**
		 * 初回色の取得
		 * @return 初回色
		 */
		public RGB getPrimary() {
			return primary;
		}
		/**
		 * 2回目以降色の取得
		 * @return 2回目以降色の取得
		 */
		public RGB getSecondary() {
			return secondary;
		}
	}

	/**
	 * 種別設定 最大種別数
	 * @deprecated
	 * @see DictionaryConstants#MAX_CATEGORY_NO
	 */
	public static final int CATEGORY_COUNT = 256;
	/** 種別設定配列 */
//	private Category[] categoryArray = new Category[CATEGORY_COUNT];
	private Category[] categoryArray = new Category[DictionaryConstants.MAX_CATEGORY_NO + 1];

	/** デフォルト設定フラグ */
	private boolean isDefaultCategory;

	/** コンストラクタ */
	public DictionarySetting() {
		init();
	}

	/**
	 * 初期化処理
	 */
	private void init() {

		IPreferenceStore storeBase = ModelToolBasePlugin.getDefault().getPreferenceStore();

		isDefaultCategory = true;
		setCategory(
				DictionaryConstants.CATEGORY_NO_NOUN,
				DictionaryConstants.CATEGORY_NOUN,
				PreferenceConverter.getColor(storeBase, BasePreferenceConstants.PK_COLOR_NOUN_FIRST),
				PreferenceConverter.getColor(storeBase, BasePreferenceConstants.PK_COLOR_NOUN));
		setCategory(
				DictionaryConstants.CATEGORY_NO_VERB,
				DictionaryConstants.CATEGORY_VERB,
				PreferenceConverter.getColor(storeBase, BasePreferenceConstants.PK_COLOR_VERB_FIRST),
				PreferenceConverter.getColor(storeBase, BasePreferenceConstants.PK_COLOR_VERB));
		setCategory(
				DictionaryConstants.CATEGORY_NO_STATE,
				DictionaryConstants.CATEGORY_STATE,
				PreferenceConverter.getColor(storeBase, BasePreferenceConstants.PK_COLOR_STATE_FIRST),
				PreferenceConverter.getColor(storeBase, BasePreferenceConstants.PK_COLOR_STATE));
	}

	/**
	 * クリア
	 */
	public void clear() {
		clearCategory();
	}

	/**
	 * マーク色設定
	 * @param index 種別No
	 * @param name 種別名
	 * @param primary 初回色
	 * @param secondary 以降色
	 */
	public void setCategory(int index, String name, RGB primary, RGB secondary) {
		Category category = categoryArray[index];
		if (category == null) {
			category = new Category();
			categoryArray[index] = category;
			category.no = index;
		}
		category.name = name;
		category.primary = primary;
		category.secondary = secondary;
	}

	/**
	 * マーク色取得
	 * @param index 種別No
	 * @return [初回色, 以降色]
	 */
	public Category getCategory(int index) {
		Category category = categoryArray[index];
		return category;
	}

	/**
	 * マーク色のクリア
	 * @param index 種別No
	 */
	public void clearCategory(int index) {
		categoryArray[index] = null;
	}

	/**
	 * クリア
	 */
	public void clearCategory() {
		Arrays.fill(categoryArray, null);
//		setDefaultCategory(true);
	}

	/**
	 * デフォルト設定フラグの取得
	 * @return デフォルト設定フラグ
	 */
	public boolean isDefaultCategory() {
		return isDefaultCategory;
	}

	/**
	 * デフォルト設定フラグの設定
	 * @param isDefaultCategory デフォルト設定フラグ
	 */
	public void setDefaultCategory(boolean isDefaultCategory) {
		this.isDefaultCategory = isDefaultCategory;
		if (isDefaultCategory) {
			init();
		}
	}

	/**
	 * デフォルト設定か判定する
	 * @return デフォルトの場合true
	 *
	 * @deprecated 使ってないです
	 */
	public boolean checkDefaultCategory() {

		boolean check = true;

		if (categoryArray[0] != null) {
			check = false;
		}
		for (int i=4; i<=CATEGORY_COUNT; i++) {
			if (categoryArray[i] != null) {
				check = false;
			}
		}

		return check;
	}

	/**
	 * ディープコピーの取得
	 * @return ディープコピー
	 */
	public DictionarySetting deepCopy() {

		DictionarySetting setting = new DictionarySetting();

		if (!this.isDefaultCategory) {
			setting.clearCategory();
			for (Category org : this.categoryArray) {
				if (org != null) {
					setting.setCategory(org.no, org.name, org.primary, org.secondary);
				}
			}
		}

		setting.isDefaultCategory = this.isDefaultCategory;

		return setting;
	}

	/**
	 * 設定情報を引数の内容で上書きする
	 * @param setting 設定
	 */
	public void set(DictionarySetting setting) {

		this.isDefaultCategory = setting.isDefaultCategory;

		this.clearCategory();
		for (Category category :  setting.categoryArray) {
			if (category != null) {
				this.setCategory(category.no, category.name, category.primary, category.secondary);
			}
		}

	}

	/**
	 * 種別№が存在するかチェックする
	 * @param categoryNo 種別No
	 * @return 存在する場合true
	 */
	public boolean containsCategory(int categoryNo) {
		if (categoryArray[categoryNo] != null) {
			return true;
		}
		return false;
	}

	/**
	 * 設定されている種別の個数を返す
	 * @return 種別の個数
	 */
	public int categorySize() {
		int size = 0;
		for (int i=1; i<=DictionaryConstants.MAX_CATEGORY_NO; i++) {
			if (categoryArray[i] != null) {
				size++;
			}
		}
		return size;
	}
}
