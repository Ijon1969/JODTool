package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column;

import static jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryPreferenceConstants.PK_FORMAL_DEFINITION_COMPLETION;

import java.text.MessageFormat;

import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;
import jp.ac.kyushu_u.csce.modeltool.dictionary.Messages;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;

/**
 * 辞書ビュー 見出し語カラムクラス
 *
 * @author KBK yoshimura
 */
public class WordColumn extends AbstractColumn {

	/**
	 * コンストラクタ
	 * @param tab
	 */
	public WordColumn(TableTab tab) {

		super(tab);

		text = Messages.WordColumn_0;
		width = 100;
		activate = ACTIVATE_VARIABLE;
	}

	/**
	 * カラムIDの取得
	 */
	public int getId() {
		return DictionaryConstants.DIC_COL_ID_WORD;
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
		return ((Entry)element).getWord();
	}

	/**
	 * 値の取得
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.AbstractColumn#getValue(java.lang.Object)
	 */
	public Object getValue(Object element) {
		return ((Entry)element).getWord();
	}

	/**
	 * 値の設定
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.AbstractColumn#doSetValue(java.lang.Object, java.lang.Object)
	 */
	protected void doSetValue(Object element, Object value) {

		Entry entry = (Entry)element;

		// 形式的定義テンプレート
		String template = null;

		// 形式的定義補完のありの場合
		if (store.getBoolean(PK_FORMAL_DEFINITION_COMPLETION)) {
			// 形式的定義が空、かつ形式的種別、見出し語ともに空でない場合
			if (PluginHelper.isEmpty(entry.getFormal()) &&
					entry.getSection() > 0 && !PluginHelper.isEmpty(entry.getWord())) {
				// テンプレートを取得
				template = manager.getTemplate(manager.getModelKey(tab.getDictionary()), entry.getSection());
			}
		}

		// 補完しない場合
		if (template == null) {
			// Undoヒストリの追加
			addUndoHistory(element, value);
			// 見出し語の設定
			entry.setWord((String)value);

		// 補完した場合
		} else {
			// 変更前エントリの退避
			Entry before = entry.deepCopy();
			// テンプレートに見出し語を代入してエントリにセット
			entry.setFormal(MessageFormat.format(template, value));
			// 見出し語の設定
			entry.setWord((String)value);
			// Undoヒストリ（エントリ）を追加
			addEntryUndoHistory(before, entry);
		}
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
}
