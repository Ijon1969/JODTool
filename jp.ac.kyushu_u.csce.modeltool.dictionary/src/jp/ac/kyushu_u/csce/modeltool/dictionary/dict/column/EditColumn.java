package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column;

import jp.ac.kyushu_u.csce.modeltool.dictionary.Messages;
import jp.ac.kyushu_u.csce.modeltool.dictionary.ModelToolDictionaryPlugin;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.HandlerUpdater;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.WordCheckerExtensionManager;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.edit.DictionaryEditView;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.edit.IDictionaryEditTabListener;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPage;

/**
 * 辞書ビュー 編集カラムクラス
 *
 * @author KBK yoshimura
 */
public class EditColumn extends AbstractColumn implements IDictionaryEditTabListener {

	/** 画像 チェックなし */
	private Image imgNoCheck;
	/** 画像 チェックあり */
	private Image imgCheck;
	/** 画像 エラー */
	private Image imgRegExError;

	/**
	 * コンストラクタ
	 * @param tab 辞書タブ
	 */
	public EditColumn(TableTab tab) {

		super(tab);

		text = Messages.EditColumn_0;
		width = 40;
		activate = ACTIVATE_CHECK;

		// アイコンの作成
		ImageDescriptor descriptor =
			ModelToolDictionaryPlugin.imageDescriptorFromPlugin(ModelToolDictionaryPlugin.PLUGIN_ID, DictionaryConstants.IMG_DISABLED);
		imgNoCheck = descriptor.createImage();
		descriptor =
			ModelToolDictionaryPlugin.imageDescriptorFromPlugin(ModelToolDictionaryPlugin.PLUGIN_ID, DictionaryConstants.IMG_EDITING);
		imgCheck = descriptor.createImage();
		descriptor =
			ModelToolDictionaryPlugin.imageDescriptorFromPlugin(ModelToolDictionaryPlugin.PLUGIN_ID, DictionaryConstants.IMG_ERROR);
		imgRegExError = descriptor.createImage();
	}

	/**
	 * カラムIDの取得
	 */
	public int getId() {
		return DictionaryConstants.DIC_COL_ID_EDIT;
	}

	/**
	 * セルエディターの取得
	 * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
	 */
	public CellEditor getCellEditor(Object element) {
		if (editor == null) {

			// チェックボックス
			editor = new CheckboxCellEditor(getTableViewer().getTable());

			// リスナーの追加
			editor.addListener(new ICellEditorListener() {
				public void editorValueChanged(boolean oldValidState, boolean newValidState) {}
				public void cancelEditor() {}

				// チェックボックスのON/OFF切り替え
				public void applyEditorValue() {

					// 選択された行オブジェクトの取得
//					Object[] objs = ((IStructuredSelection)tab.getTableViewer().getSelection()).toArray();
//					if (objs == null || objs.length != 1) {
//						return;
//					}
//					Entry entry = (Entry)objs[0];
					Entry entry = tab.getSelection();
					if (entry == null) {
						return;
					}

					// ワークベンチページ
					IWorkbenchPage page = ModelToolDictionaryPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
					DictionaryEditView editView = null;

					// チェックON
					if ((Boolean)editor.getValue()) {
						try {
							// 辞書編集ビューを開く
							editView = (DictionaryEditView)page.showView(
									DictionaryConstants.PART_ID_DICTIONARY_EDIT, null, IWorkbenchPage.VIEW_VISIBLE);
						} catch (Exception e) {
							ModelToolDictionaryPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, ModelToolDictionaryPlugin.PLUGIN_ID,
									Messages.EditColumn_1, e));
							return;
						}
						if (editView == null) {
							return;
						}
						// 辞書編集ビューに新規タブを追加
						editView.createNewTab(tab, entry, EditColumn.this);

						// チェックOFF
					} else {
						// TableTab#removeEntry(Entry, boolean)に同様のロジックあり
						// ここを修正する場合、上記も併せて修正すること
						// 辞書編集ビューの取得
						editView = (DictionaryEditView)page.findView(DictionaryConstants.PART_ID_DICTIONARY_EDIT);
						if (editView == null) {
							return;
						}
						// 辞書編集ビューから該当タブを削除
						editView.removeTab(entry, false);
						// 辞書編集ビューでの編集内容を辞書ビューに反映しない
						tab.getTableViewer().update(entry, null);
					}

					// 辞書ビューをフォーカス
					page.activate(tab.getView());
				}
			});
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

		// 編集中
		if (((Entry)element).isEdit()) {
			return imgCheck;	// チェックあり
		}

		// 正規表現エラーチェック
		if (! new WordCheckerExtensionManager().check((Entry)element)) {
			return imgRegExError;	// エラー
		}

		return imgNoCheck;	// チェックなし
	}

	/**
	 * 値の取得
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.AbstractColumn#getValue(java.lang.Object)
	 */
	public Object getValue(Object element) {
		return ((Entry)element).isEdit();
	}

	/**
	 * 値の設定
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.AbstractColumn#doSetValue(java.lang.Object, java.lang.Object)
	 */
	protected void doSetValue(Object element, Object value) {
		Entry entry = (Entry)element;
		entry.setEdit((Boolean)value);

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
		if (entry.isModified()) {
			// ※変更履歴の追加は辞書編集ビューの方で行っている
			// 変更があればdirtyに
			tab.setDirty(true);
		}
		// ツールバーの状態更新
		new HandlerUpdater().update(tab);
	}

	/**
	 * 変更通知の可否
	 * @see jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.IColumn#notifyChange()
	 */
	public boolean notifyChange() {
		return false;
	}

	/**
	 * dispose
	 */
	public void dispose() {
		// 画像のdispose
		imgNoCheck.dispose();
		imgCheck.dispose();
		imgRegExError.dispose();
	}
}
