package jp.ac.kyushu_u.csce.modeltool.vdmslEditor.handler;

import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryView;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;
import jp.ac.kyushu_u.csce.modeltool.vdmslEditor.Messages;
import jp.ac.kyushu_u.csce.modeltool.vdmslEditor.editor.VdmslEditor;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.texteditor.IDocumentProvider;

/**
 * 辞書→VDM検索処理ハンドラクラス
 *
 * @author KBK yoshimura
 */
public class VdmslSearchHandler extends AbstractVdmslSearchHandler {

	@Override
	protected Object doExecute(ExecutionEvent event, VdmslEditor editor, DictionaryView view) {

		// 辞書の取得
		TableTab tab = view.getActiveTableTab(false);
		if(tab == null) {
			// 辞書が選択されていない場合エラー
			showWarningDialog(Messages.VdmslSearchHandler_0);
			return null;
		}

		// 見出し語の取得
//		TableViewer viewer = tab.getTableViewer();
//		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
//		Entry selectEntry = (Entry)selection.getFirstElement();
		Entry selectEntry = tab.getSelection();
		if(selectEntry == null) {
			// 見出し語が選択されていない場合エラー
			showWarningDialog(Messages.VdmslSearchHandler_1);
			return null;
		}

		// VDMエディターからドキュメントを取得
		IDocumentProvider prov = editor.getDocumentProvider();
		IDocument document = prov.getDocument(editor.getEditorInput());
		FindReplaceDocumentAdapter adapter = new FindReplaceDocumentAdapter(document);

		// 現在の選択位置
		ITextSelection tselection = (ITextSelection)editor.getSelectionProvider().getSelection();
		int offset = tselection.getOffset();
		int length = tselection.getLength();

		try {
			// 検索処理実行
			IRegion region = adapter.find(offset + length, selectEntry.getWord(), true, true, false, false);

			// 見つからなかった場合、先頭からもう一度
			if(region == null) {
				region = adapter.find(0, selectEntry.getWord(), true, true, false, false);
			}
			// ２回目も見つからなかった場合エラー
			if(region == null) {
				showWarningDialog(Messages.VdmslSearchHandler_2);
				return null;
			}

			// 検索結果をフォーカス
			editor.getSelectionProvider().setSelection(new TextSelection(region.getOffset(), region.getLength()));

		} catch(BadLocationException e) {
			// TODO:例外処理
		}
		return null;
	}
}
