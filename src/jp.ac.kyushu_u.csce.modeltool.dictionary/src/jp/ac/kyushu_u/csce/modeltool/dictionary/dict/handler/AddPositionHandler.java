package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.handler;

import jp.ac.kyushu_u.csce.modeltool.dictionary.ModelToolDictionaryPlugin;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryPreferenceConstants;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * 追加位置設定を変更するハンドラクラス
 *
 * @author KBK yoshimura
 */
public class AddPositionHandler extends AbstractHandler {

	/**
	 * execute
	 * @see org.eclipse.core.commands.IHandler#execute(ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// トグルボタンの状態を取得（ボタンが押される前の状態）
		boolean toggle = HandlerUtil.toggleCommandState(event.getCommand());

		// プリファレンスの設定
		IPreferenceStore store = ModelToolDictionaryPlugin.getDefault().getPreferenceStore();
		store.setValue(DictionaryPreferenceConstants.PK_ENTRY_ADD_UNDER, ! toggle);

		return null;
	}
}
