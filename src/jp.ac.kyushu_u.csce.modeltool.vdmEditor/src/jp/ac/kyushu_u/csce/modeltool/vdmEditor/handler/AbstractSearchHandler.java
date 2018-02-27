package jp.ac.kyushu_u.csce.modeltool.vdmEditor.handler;

import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryView;
import jp.ac.kyushu_u.csce.modeltool.vdmEditor.Messages;
import jp.ac.kyushu_u.csce.modeltool.vdmEditor.editor.VDMEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * VDM-辞書間検索用抽象クラス<br>
 * VDMエディターおよび辞書ビューの取得を行う。
 *
 * @author KBK yoshimura
 */
public abstract class AbstractSearchHandler extends AbstractHandler {

	/** メッセージダイアログのタイトル */
	protected String fMessageTitle;
	/** WorkbenchPage */
	protected IWorkbenchPage fPage;
	/** Shell */
	protected Shell fShell;

	@Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

		fMessageTitle = getMessageTitle();

		// アクティブページの取得
        fPage = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();

        // シェルの取得
        fShell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();

        // アクティブエディターの取得
        IEditorPart editor = fPage.getActiveEditor();
        if(editor instanceof VDMEditor == false) {
        	// VDMエディター以外のエディターでは処理を行わない
        	showWarningDialog(Messages.AbstractSearchHandler_0);
        	return null;
        }

        // 辞書ビューの取得
        DictionaryView dicView = (DictionaryView)fPage.findView(DictionaryConstants.PART_ID_DICTIONARY);
        // 辞書ビューが開いていない場合、検査処理を行わない
        if(dicView == null) {
        	showWarningDialog(Messages.AbstractSearchHandler_1);
            return null;
        }
        // 辞書ビューをフォーカス
        fPage.activate(dicView);

        // 検索処理実行
        Object ret = doExecute(event, (VDMEditor)editor, dicView);

        // VDMエディターをフォーカス
        fPage.activate(editor);

        return ret;
    }

    /**
     * エディター、ビューチェック後の処理
     * @param executionevent
     * @param vdmeditor
     * @param dictionaryview
     * @return
     */
    protected abstract Object doExecute(ExecutionEvent executionevent, VDMEditor vdmeditor, DictionaryView dictionaryview);

    /**
     * メッセージタイトルの取得
     * @return メッセージタイトル
     */
    protected String getMessageTitle() {
		return Messages.AbstractSearchHandler_2;
	}

    /**
     * 警告ダイアログの表示
     * @param message メッセージ
     */
    protected void showWarningDialog(String message) {
    	MessageDialog.openWarning(fShell, fMessageTitle, message);
    }
}
