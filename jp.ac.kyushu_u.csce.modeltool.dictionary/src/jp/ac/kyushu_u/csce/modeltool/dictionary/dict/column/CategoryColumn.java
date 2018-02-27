package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column;

import java.util.ArrayList;
import java.util.List;

import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;
import jp.ac.kyushu_u.csce.modeltool.dictionary.Messages;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionarySetting.Category;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

/**
 * 辞書ビュー 種別カラムクラス
 *
 * @author KBK yoshimura
 */
public class CategoryColumn extends AbstractColumn {

	/** コンボボックスアイテム */
	private static String[] COMBO_ITEMS = DictionaryConstants.CATEGORIES;

	/**
	 * コンストラクタ
	 * @param tab
	 */
	public CategoryColumn(TableTab tab) {

		super(tab);

		text = Messages.CategoryColumn_0;
		width = 100;
		activate = ACTIVATE_TRUE;
	}

	/**
	 * アイテムの取得
	 * @return
	 */
	public String[] getItems() {

		List<String> items = new ArrayList<String>();
		for (int i=1; i <= DictionaryConstants.MAX_CATEGORY_NO; i++) {
			Category category = tab.getDictionary().getSetting().getCategory(i);
			if (category != null) {
				items.add(category.getName());
			}
		}
		return (String[]) PluginHelper.arrayConcat(String.class, COMBO_ITEMS, items.toArray());

//		return COMBO_ITEMS;
	}

	/**
	 * カラムIDの取得
	 */
	public int getId() {
		return DictionaryConstants.DIC_COL_ID_CATEGORY;
	}

	/**
	 * セルエディターの取得
	 * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
	 */
	protected CellEditor getCellEditor(Object element) {

		if (editor != null) {
			// エディタ、リスナの削除
			// （辞書情報により動的にコンボボックスの内容が変わるため）
			removeCellEditorListener(editor);
			editor.dispose();
		}

//		if (editor == null) {
		// コンボボックス
//		editor = new ComboBoxCellEditor(getTableViewer().getTable(), COMBO_ITEMS, SWT.READ_ONLY | SWT.FLAT);
		editor = new ComboBoxCellEditor(getTableViewer().getTable(), getItems(), SWT.READ_ONLY | SWT.FLAT);
		editor.getControl().setBackground(
				Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

		// リスナーの追加
		setCellEditorListener(editor, element);
//		}
		return editor;
	}

	/**
	 * 表示テキストの取得
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.IColumn#getColumnText(java.lang.Object)
	 */
	public String getColumnText(Object element) {
//		return COMBO_ITEMS[((Entry)element).getCategoryNo()];
		int no = ((Entry)element).getCategoryNo();
		if (no <= 0 || no > DictionaryConstants.MAX_CATEGORY_NO) {
			return null;
		} else {
			Category category = tab.getDictionary().getSetting().getCategory(no);
			if (category == null) return null;
			return category.getName();
		}
	}

	/**
	 * 値の取得
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.AbstractColumn#getValue(java.lang.Object)
	 */
	public Object getValue(Object element) {
		Entry entry = (Entry)element;
//		return entry.getCategoryNo();

		if (entry.getCategoryNo() <= 0) return 0;

		int catIdx = 0;
		for (int i=1; i <= DictionaryConstants.MAX_CATEGORY_NO; i++) {
			Category category = tab.getDictionary().getSetting().getCategory(i);
			if (category != null) {
				catIdx++;
				if (entry.getCategoryNo() == category.getNo()) {
					return catIdx;
				}
			}
		}

		return 0;
	}

	/**
	 * 値の設定
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.AbstractColumn#doSetValue(java.lang.Object, java.lang.Object)
	 */
	protected void doSetValue(Object element, Object value) {

		// Undoヒストリの追加
		addUndoHistory(element, value);

		Entry entry = (Entry)element;

		// インデックスが範囲外の場合、何もしない
		int index = (Integer)value;
//		if (index < 0 || index >= COMBO_ITEMS.length) {
		if (index < 0 || index > DictionaryConstants.MAX_CATEGORY_NO) {
			return;
		}

//		entry.setCategoryNo((Integer)value);
		if (index == 0) {
			entry.setCategoryNo(0);
		} else {
			int catIdx = 0;
			for (int i=1; i <= DictionaryConstants.MAX_CATEGORY_NO; i++) {
				Category category = tab.getDictionary().getSetting().getCategory(i);
				if (category != null) {
					catIdx++;
					if (catIdx == index) {
						entry.setCategoryNo(category.getNo());
						break;
					}
				}
			}
		}

//		entry.setCategory(COMBO_ITEMS[(Integer)value]);
		entry.setCategory(getItems()[(Integer)value]);
	}

	/**
	 * 編集可否の判定
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.AbstractColumn#canEdit(java.lang.Object)
	 */
	protected boolean canEdit(Object element) {
		// 未登録モデルの場合、編集不可
		if (!isRegisteredModel()) return false;
		// 編集カラムがチェックされている場合は、編集不可
		return ((Entry)element).isEdit()? false : super.canEdit(element);
	}
}
