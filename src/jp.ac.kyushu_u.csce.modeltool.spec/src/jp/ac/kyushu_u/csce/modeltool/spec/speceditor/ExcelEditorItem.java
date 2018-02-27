package jp.ac.kyushu_u.csce.modeltool.spec.speceditor;

import java.util.ArrayList;
import java.util.List;

/**
 * Excelエディタのアイテム<br>
 * Excelファイルの各セルを表す。
 *
 * @author KBK yoshimura
 */
public class ExcelEditorItem {

	/** 階層 */
	private int level;
	/** 内容 */
	private String content;
	/** 空フラグ */
	private boolean empty;
	/** 親アイテム */
	private ExcelEditorItem parent;
	/** 子アイテム */
	private List<ExcelEditorItem> children;

	/**
	 * コンストラクタ
	 */
	public ExcelEditorItem() {
		level = 0;
		content = null;
		empty = false;
		parent = null;
		children = new ArrayList<ExcelEditorItem>();
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}
	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}
	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * @return the empty
	 */
	public boolean isEmpty() {
		return empty;
	}
	/**
	 * @param empty the empty to set
	 */
	public void setEmpty(boolean empty) {
		this.empty = empty;
	}
	/**
	 * @return the parent
	 */
	public ExcelEditorItem getParent() {
		return parent;
	}
	/**
	 * @param parent the parent to set
	 */
	public void setParent(ExcelEditorItem parent) {
		this.parent = parent;
	}
	/**
	 * @return the children
	 */
	public List<ExcelEditorItem> getChildren() {
		return children;
	}
	/**
	 * @param children the children to set
	 */
	public void setChildren(List<ExcelEditorItem> children) {
		this.children = children;
	}

//	/**
//	 * 子アイテムの数を取得する
//	 * @param depth 探索する深さ
//	 *    　0：対象の子のみ、1：対象の子＋子の子、…、負の値：最下層まで
//	 * @return 子アイテム数
//	 */
//	public int getChildrenSize(int depth) {
//
//		int size = children.size();
//
//		if (size != 0 & depth != 0) {
//			for (ExcelEditorItem child : children) {
//				size += child.getChildrenSize(depth - 1);
//			}
//		}
//
//		return size;
//	}
}
