package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model;

/**
 * 形式手法モデルの要素を表すクラス
 *
 * @author KBK yoshimura
 */
public class ModelElement {

	/** 要素名 */
	private String name;

	/** 要素 */
	private Object element;

	/**
	 * コンストラクタ
	 * @param name
	 * @param element
	 */
	public ModelElement(String name, Object element) {
		this.name = name;
		this.element = element;
	}

	/**
	 * 要素名の取得
	 */
	public String getName() {
		return name;
	}

	/**
	 * 要素の取得
	 */
	public Object getElement() {
		return element;
	}
}
