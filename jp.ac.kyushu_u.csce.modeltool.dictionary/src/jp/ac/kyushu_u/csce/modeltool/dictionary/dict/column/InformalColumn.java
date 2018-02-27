package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import jp.ac.kyushu_u.csce.modeltool.base.dialog.MultiInputDialog2;
import jp.ac.kyushu_u.csce.modeltool.dictionary.Messages;
import jp.ac.kyushu_u.csce.modeltool.dictionary.ModelToolDictionaryPlugin;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * 辞書ビュー 非形式的定義カラムクラス
 *
 * @author KBK yoshimura
 */
public class InformalColumn extends AbstractColumn {

	/** 列番号 */
	private int index;

	/**
	 * コンストラクタ
	 * @param tab
	 */
	public InformalColumn(TableTab tab, int index) {

		super(tab);
		this.index = index;

		text = Messages.InformalColumn_0 ;
		width = 100;
		activate = ACTIVATE_TRUE;
	}

	/**
	 * カラムIDの取得
	 */
	public int getId() {
		return DictionaryConstants.DIC_COL_ID_INFORMAL;
	}

	/**
	 * ヘッダテキストの取得
	 * @return ヘッダテキスト
	 */
	public String getHeaderText() {
		String langCd = ""; //$NON-NLS-1$
		String langName = ""; //$NON-NLS-1$
		List<String> languages = tab.getDictionary().getDictionaryClass().languages;
		if (index < languages.size()) {
			langCd = languages.get(index);
			if (StringUtils.isNotBlank(langCd)) {
				langName = ModelToolDictionaryPlugin.getLanguageUtil().getLanguageMap().get(langCd);
			}
		}
		if (StringUtils.isNotBlank(langName)) {
			return MessageFormat.format(Messages.InformalColumn_2, langName);
		} else {
			return Messages.InformalColumn_0;
		}
	}

	/**
	 * セルエディターの取得
	 * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
	 */
	protected CellEditor getCellEditor(final Object element) {

		if (editor == null) {

			// ダイアログ
			editor = new DialogCellEditor(getTableViewer().getTable()) {

				/**
				 * ダイアログを開く
				 */
				protected Object openDialogBox(Control cellEditorWindow) {

					// 編集前の値を退避
					preEditValue = getValue();

					// 複数行入力ダイアログを開く
					MultiInputDialog2 dialog = new MultiInputDialog2(cellEditorWindow.getShell(),
												MessageFormat.format(Messages.InformalColumn_1, text),
												preEditValue.toString(), 10) {
						protected void createButtonsForButtonBar(Composite parent) {
							super.createButtonsForButtonBar(parent);
							// 未登録モデルの場合、OKボタン不可
							if (!isRegisteredModel()) {
								getButton(IDialogConstants.OK_ID).setEnabled(false);
							}
						}
					};

					if (dialog.open() == Dialog.OK) {
//						// 変更があればdirtyに
//						if (preEditValue.equals(dialog.getValue()) == false) {
//							tab.setDirty(true);
//						}
						return dialog.getValue();
					} else {
						return preEditValue;
					}
				}
			};

			// リスナーの追加
			// ※DialogCellEditorだと追加されないけど、ほかのカラムと処理を合わせるため入れておく
			setCellEditorListener(editor, element);
		}
		return editor;
	}

	/**
	 * 表示テキストの取得
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.IColumn#getColumnText(java.lang.Object)
	 */
	public String getColumnText(Object element) {
//		return ((Entry)element).getInformal();
		List<String> informals = ((Entry)element).getInformals();
		int size = informals.size();
		if (size <= this.index) {
			for (int i=0; i<=this.index-size; i++) {
				informals.add(""); //$NON-NLS-1$
			}
		}
		return informals.get(this.index);
	}

	/**
	 * 値の取得
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.AbstractColumn#getValue(java.lang.Object)
	 */
	public Object getValue(Object element) {
//		return ((Entry)element).getInformal();
		return getColumnText(element);
	}

	/**
	 * 値の設定
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.AbstractColumn#doSetValue(java.lang.Object, java.lang.Object)
	 */
	protected void doSetValue(Object element, Object value) {
//		((Entry)element).setInformal((String)value);
		List<String> informals = ((Entry)element).getInformals();

		// 変更前リストの退避
		List<String> oldInformals = new ArrayList<String>(informals);

		int size = informals.size();
		if (size <= this.index) {
			for (int i=0; i<=this.index-size; i++) {
				informals.add(""); //$NON-NLS-1$
			}
		}
		informals.set(this.index, (String)value);

		// Undoヒストリの追加
//		tab.getHistoryManager().add(new History(
//				History.TYPE_CELL,
//				getId(),
//				DictionaryUtil.getMode(),
//				tab.getDictionary().indexOf((Entry)element),
//				oldInformals,
//				new ArrayList<String>(informals),
//				tab.isDirty(),
//				true));
		tab.getHistoryHelper().addCellHisotry(
				tab.getDictionary().indexOf((Entry)element), getId(), oldInformals, new ArrayList<String>(informals));
	}

	/**
	 * 編集可否の判定
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.AbstractColumn#canEdit(java.lang.Object)
	 */
	protected boolean canEdit(Object element) {
		// 編集カラムがチェックされている場合は、編集不可
		return ((Entry)element).isEdit()? false : super.canEdit(element);
	}

	/**
	 * 複数カラム
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.IColumn#multiple()
	 */
	public boolean multiple() {
		return true;
	}
}
