package jp.ac.kyushu_u.csce.modeltool.dictionary.dict;

import jp.ac.kyushu_u.csce.modeltool.dictionary.ModelToolDictionaryPlugin;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.edit.DictionaryEditView;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IWorkbenchPage;

/**
 * 辞書テーブルのコンテンツプロバイダー・クラス<br>
 * @author KBK yoshimura
 */
public class TableContentProvider
		implements IStructuredContentProvider, IDictionaryListener {

	/**
	 * テーブルビューアー
	 */
	private TableViewer viewer;

	/**
	 * コンストラクタ
	 */
	public TableContentProvider() {
	}

	/**
	 * ドメインオブジェクトを行データの配列へ変換する
	 */
	public Object[] getElements(Object inputElement) {
		return ((Dictionary)inputElement).getEntries().toArray();
	}

	/**
	 * dispose処理
	 */
	public void dispose() {}

	/**
	 * inputChanged処理
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TableViewer)viewer;
		if (oldInput != null) {
			((Dictionary)oldInput).removeDictionaryListener(this);
		}
		if (newInput != null) {
			((Dictionary)newInput).addDictionaryListener(this);
		}
	}

	/**
	 * ドメインオブジェクトが更新された場合の処理
	 */
	public void update() {
		// テーブルビューアーの表示リフレッシュ
		if (viewer != null) {
			viewer.refresh();
		}

		// ハンドラーの更新
		new HandlerUpdater().update(viewer);

		// 辞書編集ビューの更新
		IWorkbenchPage page = ModelToolDictionaryPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page == null) {
			return;
		}
		DictionaryEditView editView = (DictionaryEditView)page.findView(DictionaryConstants.PART_ID_DICTIONARY_EDIT);
		if (editView != null) {
			editView.UpdateTab();
		}
	}
}
