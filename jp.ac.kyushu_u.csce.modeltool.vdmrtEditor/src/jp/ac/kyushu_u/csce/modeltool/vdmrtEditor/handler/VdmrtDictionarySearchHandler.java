package jp.ac.kyushu_u.csce.modeltool.vdmrtEditor.handler;

import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Dictionary;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryView;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;
import jp.ac.kyushu_u.csce.modeltool.vdmrtEditor.Messages;
import jp.ac.kyushu_u.csce.modeltool.vdmrtEditor.editor.VdmrtEditor;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;

/**
 * VDM→辞書検索処理ハンドラクラス
 *
 * @author KBK yoshimura
 */
public class VdmrtDictionarySearchHandler extends AbstractVdmrtSearchHandler {

	@Override
	protected Object doExecute(ExecutionEvent event, VdmrtEditor editor, DictionaryView view) {

		// エディターで選択された語句を取得
		ITextSelection selection = (ITextSelection)editor.getEditorSite().getSelectionProvider().getSelection();
		String keyword = selection.getText();
		if(PluginHelper.isEmpty(keyword)) {
			showWarningDialog(Messages.VdmrtDictionarySearchHandler_0);
			return null;
		}

		// 辞書の取得
		TableTab tab = view.getActiveTableTab(false);
		if(tab == null) {
			// 辞書が選択されていない場合エラー
			showWarningDialog(Messages.VdmrtDictionarySearchHandler_1);
			return null;
		}
		Dictionary dictionary = tab.getDictionary();

		// 辞書件数の取得
		int size = dictionary.size();
		if (size == 0) {
			showWarningDialog(Messages.VdmrtDictionarySearchHandler_2);
			return null;
		}

		// 辞書のインデックス
		int selIdx = -1;
		TableViewer viewer = tab.getTableViewer();
		IStructuredSelection tselection = (IStructuredSelection)viewer.getSelection();
		Entry selectEntry = (Entry)tselection.getFirstElement();
		if(selectEntry != null) {
			selIdx = dictionary.indexOf(selectEntry);
		}

		// 選択位置の次の見出し語から検索
		boolean found = false;
		for (int i=0; i<size; i++) {
			int index = (i + selIdx + 1) % size;
			Entry entry = dictionary.get(index);
			String word = entry.getWord();

			// 見つかったらフォーカス移動
			if (word.indexOf(keyword) >= 0) {
				viewer.setSelection(new StructuredSelection(entry), true);
				found = true;
				break;
			}
		}
		if (found == false) {
			showWarningDialog(Messages.VdmrtDictionarySearchHandler_2);
			return null;
		}

		return null;
	}
}
