package jp.ac.kyushu_u.csce.modeltool.dictionary.dict;

import java.util.ArrayList;
import java.util.List;

/**
 * 辞書クラス情報
 *
 * @author KBK yoshimura
 */
public class DictionaryClass implements DictionaryInfo {

	/** 問題領域 */
	public String domain;
	/** プロジェクト名 */
	public String project;
	/** 入力言語 */
	@Deprecated
	public String language;
	/** 出力モデル */
	public String model;
	/** 拡張元辞書 */
	public String extend;

	/** 入力言語リスト */
	public List<String> languages;

	/**
	 * コンストラクタ
	 */
	public DictionaryClass() {
		domain = ""; //$NON-NLS-1$
		project = ""; //$NON-NLS-1$
		language = ""; //$NON-NLS-1$
		model = ""; //$NON-NLS-1$
		extend = ""; //$NON-NLS-1$
		languages = new ArrayList<String>();
	}

	/**
	 * クリア
	 */
	public void clear() {
		domain = ""; //$NON-NLS-1$
		project = ""; //$NON-NLS-1$
		model = ""; //$NON-NLS-1$
		extend = ""; //$NON-NLS-1$
		if (languages == null) {
			languages = new ArrayList<String>();
		} else {
			languages.clear();
		}
	}

	/**
	 * ディープコピーの取得
	 * @return ディープコピー
	 */
	public DictionaryClass deepCopy() {
		DictionaryClass dstClass = new DictionaryClass();
		dstClass.domain = this.domain;
		dstClass.project = this.project;
		dstClass.model = this.model;
		dstClass.extend = this.extend;
		dstClass.languages = new ArrayList<String>(this.languages);
		return dstClass;
	}

	/**
	 * クラス情報を引数の内容で上書きする
	 * @param dictClass クラス
	 */
	public void set(DictionaryClass dictClass) {
		this.domain = dictClass.domain;
		this.project = dictClass.project;
		this.model = dictClass.model;
		this.extend = dictClass.extend;
		this.languages.clear();
		this.languages.addAll(dictClass.languages);
	}
}
