package jp.ac.kyushu_u.csce.modeltool.spec.speceditor.handler;

import jp.ac.kyushu_u.csce.modeltool.spec.ModelToolSpecPlugin;
import jp.ac.kyushu_u.csce.modeltool.spec.constant.SpecPreferenceConstants;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * 正規表現検索の切り替えハンドラークラス
 * @author KBK yoshimura
 */
public class SwitchRegExHandler extends AbstractHandler {

	/**
	 * execute処理<br>
	 * 正規表現を用いて検索を行うかどうか切り替える
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// トグルボタンの状態を取得
		boolean toggle = HandlerUtil.toggleCommandState(event.getCommand());

		// プリファレンスの設定
		IPreferenceStore store = ModelToolSpecPlugin.getDefault().getPreferenceStore();
		store.setValue(SpecPreferenceConstants.PK_USE_REGULAR_EXPRESSION, !toggle);

		return null;
	}
}
