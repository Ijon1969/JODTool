package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.handler;

import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryView;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;
import jp.ac.kyushu_u.csce.modeltool.dictionary.utility.DictionaryRenameUtility;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * 辞書の名前変更を行うハンドラクラス
 *
 * @author KBK yoshimura
 */
public class RenameHandler extends AbstractHandler implements IHandler {

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

		// アクティブなタブの取得
		TableTab tab = view.getActiveTableTab(false);
		if (tab == null) {
			return null;
		}

		// リネーム
		DictionaryRenameUtility.rename(tab, tab.getFile());

		return null;
	}
}
