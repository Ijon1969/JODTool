package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column;

import static jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryPreferenceConstants.PK_FORMAL_DEFINITION_COMPLETION;

import java.text.MessageFormat;

import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;
import jp.ac.kyushu_u.csce.modeltool.dictionary.Messages;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

/**
 * 辞書ビュー 形式的種別カラムクラス
 *
 * @author KBK yoshimura
 */
public class SectionColumn extends AbstractColumn {

	/** 空文字列の配列 */
	private static final String[] EMPTY_SECTIONS = {""}; //$NON-NLS-1$

	/**
	 * コンストラクタ
	 * @param tab
	 */
	public SectionColumn(TableTab tab) {

		super(tab);

		text = Messages.SectionColumn_1;
		width = 100;
		activate = ACTIVATE_TRUE;
	}

	/**
	 * カラムIDの取得
	 */
	public int getId() {
		return DictionaryConstants.DIC_COL_ID_SECTION;
	}

	/**
	 * コンボボックスアイテムの取得
	 */
	private String[] getComboItems() {
		return (String[])PluginHelper.arrayConcat(String.class, EMPTY_SECTIONS,
					manager.getSectionNames(tab.getDictionary()));
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

		// コンボボックス
		editor = new ComboBoxCellEditor(getTableViewer().getTable(), getComboItems(), SWT.READ_ONLY | SWT.FLAT);
		editor.getControl().setBackground(
				Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

		// リスナーの追加
		setCellEditorListener(editor, element);

		return editor;
	}

	/**
	 * 表示文字列の取得
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.IColumn#getColumnText(java.lang.Object)
	 */
	public String getColumnText(Object element) {

		Entry entry = (Entry)element;
		int section = entry.getSection();

//		// インデックスが範囲外の場合何もしない
//		if (section < 0 || section >= getComboItems().length) {
//			return ""; //$NON-NLS-1$
//		}
//
//		return getComboItems()[((Entry)element).getSection()];
		return StringUtils.defaultString(manager.getSectionName(tab.getDictionary(), section));
	}

	/**
	 * 値の取得
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.AbstractColumn#getValue(java.lang.Object)
	 */
	public Object getValue(Object element) {
		Entry entry = (Entry)element;
//		return entry.getSection();
		return PluginHelper.indexOf(getComboItems(), manager.getSectionName(tab.getDictionary(), entry.getSection()));
	}

	/**
	 * 値の設定
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.AbstractColumn#doSetValue(java.lang.Object, java.lang.Object)
	 */
	public void doSetValue(Object element, Object value) {

		Entry entry = (Entry)element;

		// インデックスが範囲外の場合何もしない
		int index = (Integer)value;
		if (index < 0 || index >= getComboItems().length) {
			return;
		}

		// 種別コードの取得
		String model = manager.getModelKey(tab.getDictionary());
		int section = manager.getSectionCd(model, getComboItems()[index]);

		// 形式的定義テンプレート
		String template = null;

		// 形式的定義補完のありの場合
		if (store.getBoolean(PK_FORMAL_DEFINITION_COMPLETION)) {
			// 形式的定義が空、かつ形式的種別、見出し語ともに空でない場合
			if (PluginHelper.isEmpty(entry.getFormal()) &&
					section > 0 && !PluginHelper.isEmpty(entry.getWord())) {
				// テンプレートを取得
				template = manager.getTemplate(model, section);
			}
		}

		// 補完しない場合
		if (template == null) {
			// Undoヒストリの追加
			addUndoHistory(element, section);
			// 値の設定
			entry.setSection(section);

		// 補完した場合
		} else {
			// 変更前エントリの退避
			Entry before = entry.deepCopy();
			// テンプレートに見出し語を代入してエントリにセット
			entry.setFormal(MessageFormat.format(template, entry.getWord()));
			// 種別をセット
			entry.setSection(section);
			// Undoヒストリ（エントリ）を追加
			addEntryUndoHistory(before, entry);
		}
	}

	/**
	 * 表示可否の判定
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.AbstractColumn#canEdit(java.lang.Object)
	 */
	protected boolean canEdit(Object element) {
		// 未登録モデルの場合、編集不可
		if (!isRegisteredModel()) return false;
		// 編集カラムがチェックされている場合は、編集不可
		return ((Entry)element).isEdit()? false : super.canEdit(element);
	}
}
