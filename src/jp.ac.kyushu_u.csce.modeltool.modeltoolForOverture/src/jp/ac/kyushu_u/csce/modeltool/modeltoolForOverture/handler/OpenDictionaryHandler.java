package jp.ac.kyushu_u.csce.modeltool.modeltoolForOverture.handler;

import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryView;
import jp.ac.kyushu_u.csce.modeltool.modeltoolForOverture.constants.OvertureConstants;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.overture.ide.ui.navigator.VdmNavigator;

/**
 * 辞書ビュー表示ハンドラ―
 *
 * @author KBK yoshimura
 */
public class OpenDictionaryHandler extends AbstractHandler implements IHandler {

	/*
	 * execute
	 * @see org.eclipse.core.commands.IHandler#execute(ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchPage page = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();

		// VDMエクスプローラービューの取得（開いていない場合処理なし）
		VdmNavigator view = (VdmNavigator)page.findView(OvertureConstants.VIEW_ID_VDM_EXPLORER);
		if (view == null) {
			return null;
		}

		// 選択されたリソースの取得
		// 未選択の場合処理なし
		IStructuredSelection selection = (IStructuredSelection)view.getCommonViewer().getSelection();
		if (selection == null) {
			return null;
		}
		// ファイルの以外の場合、処理なし
		IResource resource = (IResource)selection.getFirstElement();
		if (!(resource instanceof IFile)) {
			return null;
		}

		try {
			// 辞書ビューでファイルを開く
			DictionaryView dictionaryView = (DictionaryView)page.findView(DictionaryConstants.PART_ID_DICTIONARY);
			if (dictionaryView != null) {
				page.showView(DictionaryConstants.PART_ID_DICTIONARY);
				dictionaryView.loadDictionary((IFile)resource);
			} else {
				dictionaryView = (DictionaryView)page.showView(DictionaryConstants.PART_ID_DICTIONARY);
				dictionaryView.loadDictionaryToActiveTab((IFile)resource);
			}
		} catch (PartInitException e) {
		}

		return null;
	}

}
