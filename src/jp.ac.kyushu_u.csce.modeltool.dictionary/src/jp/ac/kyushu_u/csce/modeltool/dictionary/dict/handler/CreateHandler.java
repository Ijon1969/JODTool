package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.handler;

import java.util.ArrayList;
import java.util.List;

import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryView;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * 辞書タブの追加を行うハンドラクラス
 *
 * @author KBK yoshimura
 */
public class CreateHandler extends AbstractHandler implements IHandler {

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

		// ビュー上の辞書名の取得
		List<String> tabList = new ArrayList<String>();
		String[][] tabArray = view.getArrayOfTabText();
		for (int i=0; i<tabArray.length; i++) {
			tabList.add(tabArray[i][0]);
		}

		// 辞書名重複対応
		String dicNameDefault = DictionaryConstants.DEFAULT_DIC_NAME;
		String dicName = dicNameDefault;
		int duplication = 1;
		while(true) {

			// 重複無しの場合
			if (tabList.contains(dicName) == false) {
				break;
			}

			// 重複有りの場合
			duplication++;
			dicName = dicNameDefault + "(" + duplication + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		// 新規タブの追加
		view.createNewTab(dicName);

		return null;
	}
}
