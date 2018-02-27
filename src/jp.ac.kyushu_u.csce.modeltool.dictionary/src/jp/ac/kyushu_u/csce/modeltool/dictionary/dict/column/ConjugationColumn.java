package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import jp.ac.kyushu_u.csce.modeltool.dictionary.Messages;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;

public class ConjugationColumn extends AbstractColumn {

	/** 列番号 */
	private int index;

	/**
	 * コンストラクタ
	 * @param tab
	 * @param index
	 */
	public ConjugationColumn(TableTab tab, int index) {
		super(tab);
		this.index = index;

		text = MessageFormat.format(Messages.ConjugationColumn_1, index + 1);
		width = 100;
		activate = ACTIVATE_VARIABLE;
	}

	/**
	 * カラムIDの取得
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.IColumn#getId()
	 */
	public int getId() {
		return DictionaryConstants.DIC_COL_ID_CONJUGATION;
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
		List<String> conjugations = ((Entry)element).getConjugations();
		int size = conjugations.size();
		if (size <= this.index) {
			for (int i=0; i<=this.index-size; i++) {
				conjugations.add(""); //$NON-NLS-1$
			}
		}
		return conjugations.get(this.index);
	}

	/**
	 * 値の取得
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.AbstractColumn#getValue(java.lang.Object)
	 */
	public Object getValue(Object element) {
		return getColumnText(element);
	}

	/**
	 * 値の設定
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.AbstractColumn#doSetValue(java.lang.Object, java.lang.Object)
	 */
	protected void doSetValue(Object element, Object value) {
		List<String> conjugations = ((Entry)element).getConjugations();

		// 変更前リストの退避
		List<String> oldConjugations = new ArrayList<String>(conjugations);

		int size = conjugations.size();
		if (size <= this.index) {
			for (int i=0; i<=this.index-size; i++) {
				conjugations.add(""); //$NON-NLS-1$
			}
		}
		if (StringUtils.isNotBlank(conjugations.get(this.index)) &&
				StringUtils.isBlank((String)value)) {
			conjugations.remove(this.index);
		} else {
			conjugations.set(this.index, (String)value);
			for (int i = this.index - 1; i >= 0; i--) {
				if (StringUtils.isBlank(conjugations.get(i))) {
					conjugations.remove(i);
				}
			}
		}

		// Undoヒストリの追加
//		tab.getHistoryManager().add(new History(
//				History.TYPE_CELL,
//				getId(),
//				DictionaryUtil.getMode(),
//				tab.getDictionary().indexOf((Entry)element),
//				oldConjugations,
//				new ArrayList<String>(conjugations),
//				tab.isDirty(),
//				true));
		tab.getHistoryHelper().addCellHisotry(
				tab.getDictionary().indexOf((Entry)element), getId(), oldConjugations, new ArrayList<String>(conjugations));

		// カラムの表示制御
		tab.showColumns(getId());
	}

	/**
	 * 編集可否の判定
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.AbstractColumn#canEdit(java.lang.Object)
	 */
	protected boolean canEdit(Object element) {
		// 未登録モデルの場合、編集不可
		if (!isRegisteredModel()) return false;
		// 編集カラムがチェックされている場合は、編集不可
		Entry entry = (Entry)element;
		return entry.isEdit()? false : super.canEdit(element);
	}

	/**
	 * 複数カラム
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.IColumn#multiple()
	 */
	public boolean multiple() {
		return true;
	}
}
