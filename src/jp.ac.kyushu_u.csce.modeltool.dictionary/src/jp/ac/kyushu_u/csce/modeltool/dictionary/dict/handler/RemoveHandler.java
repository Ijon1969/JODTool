package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.handler;

import java.util.List;

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
 * 行の削除を行うハンドラクラス
 *
 * @author KBK yoshimura
 */
public class RemoveHandler extends AbstractHandler implements IHandler {

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

		// 選択されたテーブルの行を取得（未選択の場合処理なし）
		List<Entry> selectEntries = tab.getSelections();
		if (selectEntries == null || selectEntries.isEmpty()) {
			return null;
		}

		// 選択行の削除
		Entry next = tab.removeEntries(selectEntries);

		// フォーカス
		tab.setSelection(next);

		return null;
	}
}
