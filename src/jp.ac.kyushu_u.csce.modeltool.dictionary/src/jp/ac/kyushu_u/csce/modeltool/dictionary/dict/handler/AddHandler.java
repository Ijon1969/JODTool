package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.handler;

import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryView;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * 見出し語の追加を行うハンドラクラス
 *
 * @author KBK yoshimura
 */
public class AddHandler extends AbstractHandler implements IHandler {

	/**
	 * execute
	 * @see org.eclipse.core.commands.IHandler#execute(ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchPage page = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();

		// 辞書ビューの取得（開いていない場合処理なし）
		DictionaryView view = (DictionaryView)page.findView(DictionaryConstants.PART_ID_DICTIONARY);
		if (view == null) {
			return null;
		}

		// アクティブなタブの取得（アクティブなタブがない場合は処理なし）
		TableTab tab = view.getActiveTableTab(false);
		if (tab == null) {
			return null;
		}

//		TableViewer viewer = tab.getTableViewer();

		// 選択されたテーブルの行を取得
//		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
//		Entry selectEntry = (Entry)selection.getFirstElement();
		Entry selectEntry = tab.getSelection();

		Entry entry = new Entry(DictionaryConstants.DEFAULT_ENTRY);
		tab.addEntry(selectEntry, entry);

		// 新規レコードにフォーカス
		tab.setSelection(entry);

		return null;
	}
}
