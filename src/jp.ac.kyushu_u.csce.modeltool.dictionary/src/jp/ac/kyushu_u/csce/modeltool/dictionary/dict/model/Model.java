package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.graphics.RGB;

import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Dictionary;

/**
 * 形式手法モデル 抽象クラス
 *
 * @author KBK yoshimura
 */
public abstract class Model {

	/**
	 * 辞書から形式モデルへの変換を行う
	 * @param dictionary 辞書
	 * @param elements （変換結果）形式モデル要素リスト
	 * @param errors （変換結果）変換エラーリスト
	 */
	public final void convert(Dictionary dictionary,
			List<ModelElement> elements, List<ModelError> errors) {

		// アサーション
		Assert.isNotNull(dictionary);
		Assert.isNotNull(elements);
		Assert.isNotNull(errors);

		// 結果リストのクリア
		elements.clear();
		errors.clear();

		// 変換実行
		doConvert(dictionary, elements, errors);
	}

	/**
	 * 形式モデル変換本体
	 */
	abstract protected void doConvert(Dictionary dictionary,
			List<ModelElement> elements, List<ModelError> errors);

	/**
	 * 形式的種別情報リストの取得
	 * @return リスト
	 */
	abstract public List<Section> getSectionDefs();

	/**
	 * 形式モデルの拡張子の取得
	 * @return 拡張子
	 */
	abstract public String getExtension();

	/**
	 * 形式的種別情報クラス
	 */
	public static class Section {
		/** コード */
		private int cd;
		/** 名称 */
		private String name;
		/** 背景色設定フラグ */
		private boolean bHasBg;
		/** 背景色デフォルト値 */
		private RGB defaultBg;
		/** 形式的定義テンプレート */
		private String template;

		/** コンストラクタ */
		public Section(int cd, String name, String template, boolean hasBg, RGB defaultBg) {
			this.cd = cd;
			this.name = name;
			this.bHasBg = hasBg;
			this.defaultBg = defaultBg;
			this.template = template;
		}

		/** コンストラクタ */
		public Section(int cd, String name, String template) {
			this(cd, name, template, false, null);
		}

		/**
		 * コードを取得する
		 * @return コード
		 */
		public int getCd() {
			return cd;
		}
		/**
		 * 名称を取得する
		 * @return 名称
		 */
		public String getName() {
			return name;
		}
		/**
		 * 背景色設定フラグを取得する
		 * @return 背景色設定フラグ
		 */
		public boolean hasBg() {
			return bHasBg;
		}
		/**
		 * 背景色デフォルト値を取得する
		 * @return 背景色デフォルト値
		 */
		public RGB getDefaultBg() {
			return defaultBg;
		}
		/**
		 * 形式的定義テンプレートを取得する
		 * @return 形式的定義テンプレート
		 */
		public String getTemplate() {
			return template;
		}
	}
}
