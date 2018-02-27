package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column;

import jp.ac.kyushu_u.csce.modeltool.dictionary.Messages;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryPreferenceConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;

import org.eclipse.jface.viewers.CellEditor;

/**
 * 辞書ビュー 行番号カラムクラス
 *
 * @author KBK yoshimura
 */
public class NumberColumn extends AbstractColumn {

	/**
	 * コンストラクタ
	 * @param tab
	 */
	public NumberColumn(TableTab tab) {

		super(tab);

		text = Messages.NumberColumn_0;
		width = 60;
		activate = ACTIVATE_FALSE;
	}

	/**
	 * カラムIDの取得
	 */
	public int getId() {
		return DictionaryConstants.DIC_COL_ID_NUMBER;
	}

	/**
	 * セルエディターの取得
	 * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
	 */
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	/**
	 * 表示テキストの取得
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.IColumn#getColumnText(java.lang.Object)
	 */
	public String getColumnText(Object element) {

		// 表示モード取得
		int mode = store.getInt(DictionaryPreferenceConstants.PK_DICTIONARY_DISPLAY_MODE);
		// 検査モード時
		if (mode == DictionaryPreferenceConstants.PV_DIC_DISP_INSPECT) {
			return String.valueOf(((Entry)element).getSeqNo());
		// 出力モード時
		} else {
			return String.valueOf(((Entry)element).getOutNo());
		}
	}

	/**
	 * 値の取得
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.AbstractColumn#getValue(java.lang.Object)
	 */
	public Object getValue(Object element) {

		// 表示モード取得
		int mode = store.getInt(DictionaryPreferenceConstants.PK_DICTIONARY_DISPLAY_MODE);
		// 検査モード時
		if (mode == DictionaryPreferenceConstants.PV_DIC_DISP_INSPECT) {
			return String.valueOf(((Entry)element).getSeqNo());
		// 出力モード時
		} else {
			return String.valueOf(((Entry)element).getOutNo());
		}
	}

	/**
	 * 値の設定
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.AbstractColumn#doSetValue(java.lang.Object, java.lang.Object)
	 */
	protected void doSetValue(Object element, Object value) {

		// 表示モード取得
		int mode = store.getInt(DictionaryPreferenceConstants.PK_DICTIONARY_DISPLAY_MODE);
		// 検査モード時
		if (mode == DictionaryPreferenceConstants.PV_DIC_DISP_INSPECT) {
			((Entry)element).setSeqNo((Integer)value);
		// 出力モード時
		} else {
			((Entry)element).setOutNo((Integer)value);
		}
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
	 * 表示スタイルの取得
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.AbstractColumn#getStyle()
	 */
	public int getStyle() {
		// 右揃え
		return RIGHT;
	}
}
