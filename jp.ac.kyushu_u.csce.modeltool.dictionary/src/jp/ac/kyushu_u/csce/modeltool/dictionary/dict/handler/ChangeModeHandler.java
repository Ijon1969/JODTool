package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.handler;

import jp.ac.kyushu_u.csce.modeltool.dictionary.ModelToolDictionaryPlugin;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryPreferenceConstants;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * 検査モード／出力モード切替のハンドラハンドラクラス
 *
 * @author KBK yoshimura
 */
public class ChangeModeHandler extends AbstractHandler {

	/**
	 * execute
	 * @see org.eclipse.core.commands.IHandler#execute(ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// トグルボタンの状態を取得（ボタンが押される前の状態）
		boolean toggle = HandlerUtil.toggleCommandState(event.getCommand());

		// プリファレンスの設定
		IPreferenceStore store = ModelToolDictionaryPlugin.getDefault().getPreferenceStore();
		int mode;
		if (toggle) {
			mode = DictionaryPreferenceConstants.PV_DIC_DISP_INSPECT;
		} else {
			mode = DictionaryPreferenceConstants.PV_DIC_DISP_OUTPUT;
		}
		store.setValue(DictionaryPreferenceConstants.PK_DICTIONARY_DISPLAY_MODE, mode);

		// PropertyChangeListener(TableTab)で処理を行うため、以下の処理を削除
//		IWorkbenchPage page = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();
//
//		// 辞書ビューの取得（開いていない場合処理なし）
//		DictionaryView view = (DictionaryView)page.findView(DictionaryConstants.PART_ID_DICTIONARY);
//		if (view == null) {
//			return null;
//		}
//
//		// アクティブなタブの取得（アクティブなタブがない場合は処理なし）
//		TableTab tab = view.getActiveTableTab(false);
//		if (tab == null) {
//			return null;
//		}
//
//		// 選択されたテーブルの行を取得（未選択の場合処理なし）
//		IStructuredSelection selection = (IStructuredSelection)tab.getTableViewer().getSelection();
//		Entry entry = null;
//		if (selection.isEmpty() == false) {
//			entry = (Entry)selection.getFirstElement();
//		}
//
//		// 辞書のソート
//		Dictionary dictionary = tab.getDictionary();
//		dictionary.sort(mode | Dictionary.ASC);
//
//		// モード切替前に選択されていた項目にフォーカス
//		if (entry != null) {
//			tab.setSelection(entry);
//		}

		return null;
	}
}
