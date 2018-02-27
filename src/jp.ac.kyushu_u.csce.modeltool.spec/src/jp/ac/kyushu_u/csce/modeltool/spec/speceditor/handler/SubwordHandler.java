package jp.ac.kyushu_u.csce.modeltool.spec.speceditor.handler;

import java.text.MessageFormat;
import java.util.List;

import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryView;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;
import jp.ac.kyushu_u.csce.modeltool.spec.Messages;
import jp.ac.kyushu_u.csce.modeltool.spec.ModelToolSpecPlugin;
import jp.ac.kyushu_u.csce.modeltool.spec.speceditor.SpecEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * 仕様書エディター<br>
 * キーワード抽出処理のハンドラークラス
 * @author KBK yoshimura
 */
public class SubwordHandler extends AbstractHandler {

	/**
	 * execute処理<br>
	 * 仕様書エディターで選択したキーワードを、辞書ビューへ登録する
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// アクティブページの取得
		IWorkbenchPage page = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();

		// アクティブエディターの取得
		IEditorPart editor = (IEditorPart)page.getActiveEditor();
		if (editor instanceof SpecEditor == false) {
			return null;		// 仕様書エディター以外のエディターでは処理を行わない
		}

		// 選択した語句の取得
		ITextSelection selection =
			(ITextSelection)editor.getEditorSite().getSelectionProvider().getSelection();
		String keyword = selection.getText();
		if (PluginHelper.isEmpty(keyword)) {
			MessageDialog.openWarning(editor.getSite().getShell(), Messages.PickoutHandler_0, Messages.PickoutHandler_1);
			return null;
		} else if (keyword.indexOf('\n') >= 0 || keyword.indexOf('\r') >= 0) {
			MessageDialog.openWarning(editor.getSite().getShell(), Messages.PickoutHandler_0, Messages.PickoutHandler_3);
			return null;
		}

		// 辞書ビューの取得
		DictionaryView view = (DictionaryView)page.findView(DictionaryConstants.PART_ID_DICTIONARY);
		boolean isOpened = (view != null);
		if (!isOpened) {
			try {
				view = (DictionaryView)page.showView(DictionaryConstants.PART_ID_DICTIONARY);
			} catch (PartInitException e) {
				// エラーログ出力
				IStatus status = new Status(
						IStatus.ERROR, ModelToolSpecPlugin.PLUGIN_ID,
						"failed to init part", //$NON-NLS-1$
						e);
				ModelToolSpecPlugin.getDefault().getLog().log(status);
				return null;
			}
		} else {
			page.activate(view);
		}

		// 登録対象タブ
		TableTab tab = view.getActiveTableTab(true);
		if (tab == null) {
			MessageDialog.openWarning(editor.getSite().getShell(), Messages.PickoutHandler_0, Messages.SubwordHandler_0);
			return null;
		}
//		TableViewer viewer = tab.getTableViewer();

		// 選択されたテーブルの行を取得
//		IStructuredSelection sSelection = (IStructuredSelection)viewer.getSelection();
//		Entry entry = (Entry)sSelection.getFirstElement();
		Entry entry = tab.getSelection();
		if (entry == null) {
			MessageDialog.openWarning(editor.getSite().getShell(), Messages.PickoutHandler_0, Messages.SubwordHandler_0);
			return null;
		}

		// 追加前の副キーワードリストの退避
		List<String> oldSubwords = entry.copySubwords();

		// 副キーワードの追加
		if (!tab.addSubword(entry, keyword)) {
			MessageDialog.openWarning(editor.getSite().getShell(), Messages.PickoutHandler_0,
					MessageFormat.format(Messages.SubwordHandler_1, DictionaryConstants.MAX_COL_SUBWORD));
			return null;
		}

		// Undoヒストリの追加
		tab.getHistoryHelper().addCellHisotry(
				tab.getDictionary().indexOf(entry),
				DictionaryConstants.DIC_COL_ID_SUBWORD,
				oldSubwords,
				entry.copySubwords());

		// 仕様書エディターのアクティブ化
		page.activate(editor);

		// 選択行のアクティブ化
		tab.setSelection(entry);

		return null;
	}
}
