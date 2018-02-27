package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column;

import jp.ac.kyushu_u.csce.modeltool.dictionary.Messages;
import jp.ac.kyushu_u.csce.modeltool.dictionary.ModelToolDictionaryPlugin;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.HandlerUpdater;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;

/**
 * 辞書ビュー 出力カラムクラス
 *
 * @author KBK yoshimura
 */
public class OutputColumn extends AbstractColumn {

	/** 画像 チェックなし */
	private Image imgNoCheck;
	/** 画像 チェックあり */
	private Image imgCheck;

	/**
	 * コンストラクタ
	 * @param tab 辞書タブ
	 */
	public OutputColumn(TableTab tab) {

		super(tab);

		text = Messages.OutputColumn_0;
		width = 40;
		activate = ACTIVATE_CHECK;

		// アイコンの作成
		ImageDescriptor descriptor =
			ModelToolDictionaryPlugin.imageDescriptorFromPlugin(ModelToolDictionaryPlugin.PLUGIN_ID, DictionaryConstants.IMG_NOT_EXPORT);
		imgNoCheck = descriptor.createImage();
		descriptor =
			ModelToolDictionaryPlugin.imageDescriptorFromPlugin(ModelToolDictionaryPlugin.PLUGIN_ID, DictionaryConstants.IMG_EXPORT);
		imgCheck = descriptor.createImage();
	}

	/**
	 * カラムIDの取得
	 */
	public int getId() {
		return DictionaryConstants.DIC_COL_ID_OUTPUT;
	}

	/**
	 * セルエディターの取得
	 * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
	 */
	public CellEditor getCellEditor(Object element) {
		if (editor == null) {
			// チェックボックス
			editor = new CheckboxCellEditor(getTableViewer().getTable());
		}
		return editor;
	}

	/**
	 * 表示テキストの取得
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.IColumn#getColumnText(java.lang.Object)
	 */
	public String getColumnText(Object element) {
		// テキスト表示なし
		return ""; //$NON-NLS-1$
	}

	/**
	 * 表示画像の取得
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.AbstractColumn#getColumnImage(java.lang.Object)
	 */
	public Image getColumnImage(Object element) {

		// 出力対象
		if (((Entry)element).isOutput()) {
			return imgCheck;	// チェックあり
		}

		return imgNoCheck;	// チェックなし
	}

	/**
	 * 値の取得
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.AbstractColumn#getValue(java.lang.Object)
	 */
	public Object getValue(Object element) {
		return ((Entry)element).isOutput();
	}

	/**
	 * 値の設定
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.AbstractColumn#doSetValue(java.lang.Object, java.lang.Object)
	 */
	protected void doSetValue(Object element, Object value) {
		Entry entry = (Entry)element;
		entry.setOutput((Boolean)value);

		// ツールバーの状態更新
		new HandlerUpdater().update(tab);
	}

	/**
	 * 更新
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.edit.IDictionaryEditTabListener#update(jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry)
	 */
	public void update(Entry entry) {
		// テーブルの表示を更新
		tab.getTableViewer().update(entry, null);
	}

	/**
	 * 変更通知の可否
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.IColumn#notifyChange()
	 */
	public boolean notifyChange() {
		return false;
	}

	/**
	 * カラムヘッダークリック時のリスナーを取得
	 * @return リスナー　リスナーを設定しない場合はnullを返す
	 */
	public SelectionListener getHeaderClickListener() {
		return new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				tab.switchAllOutput();
			}
			public void widgetDefaultSelected(SelectionEvent e) {}
		};
	}

	/**
	 * dispose
	 */
	public void dispose() {
		// 画像のdispose
		imgNoCheck.dispose();
		imgCheck.dispose();
	}
}
