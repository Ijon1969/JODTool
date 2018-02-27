package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column;

import jp.ac.kyushu_u.csce.modeltool.dictionary.Messages;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;

/**
 * 辞書ビュー 種別カラムクラス
 *
 * @author KBK yoshimura
 */
public class TypeColumn extends AbstractColumn {

	/**
	 * コンストラクタ
	 * @param tab
	 */
	public TypeColumn(TableTab tab) {

		super(tab);

		text = Messages.TypeColumn_0;
		width = 100;
		activate = ACTIVATE_VARIABLE;
	}

	/**
	 * カラムIDの取得
	 */
	public int getId() {
		return DictionaryConstants.DIC_COL_ID_TYPE;
	}

	/**
	 * セルエディターの取得
	 * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
	 */
	protected CellEditor getCellEditor(Object element) {
		if (editor == null) {

			// テキストボックス
			editor = new TextCellEditor(getTableViewer().getTable());

			// リスナーの追加
			setCellEditorListener(editor, element);
		}
		return editor;
	}

	/**
	 * 表示テキストの取得
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.IColumn#getColumnText(java.lang.Object)
	 */
	public String getColumnText(Object element) {
		return ((Entry)element).getType();
	}

	/**
	 * 値の取得
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.AbstractColumn#getValue(java.lang.Object)
	 */
	public Object getValue(Object element) {
		return ((Entry)element).getType();
	}

	/**
	 * 値の設定
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.AbstractColumn#doSetValue(java.lang.Object, java.lang.Object)
	 */
	protected void doSetValue(Object element, Object value) {
		// Undoヒストリの追加
		addUndoHistory(element, value);

		Entry entry = (Entry)element;
		entry.setType((String)value);
	}

	/**
	 * 表示可否の判定
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.AbstractColumn#canEdit(java.lang.Object)
	 */
	protected boolean canEdit(Object element) {
		// 編集カラムがチェックされている場合は、編集不可
		return ((Entry)element).isEdit()? false : super.canEdit(element);
	}
}
