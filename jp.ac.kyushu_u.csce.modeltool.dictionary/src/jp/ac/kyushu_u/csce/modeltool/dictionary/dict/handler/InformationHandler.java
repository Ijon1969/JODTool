package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.handler;

import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryView;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * 辞書情報ダイアログを表示するハンドラクラス
 *
 * @author KBK yoshimura
 */
public class InformationHandler extends AbstractHandler implements IHandler {

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

		// タブの取得
		TableTab tab = view.getActiveTableTab(false);
		if (tab == null) {
			return null;
		}

		// 辞書情報を開く
		tab.openInfomation();

		return null;
	}
}
