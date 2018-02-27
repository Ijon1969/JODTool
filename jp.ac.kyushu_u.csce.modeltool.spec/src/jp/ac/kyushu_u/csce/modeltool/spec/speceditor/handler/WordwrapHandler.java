package jp.ac.kyushu_u.csce.modeltool.spec.speceditor.handler;

import jp.ac.kyushu_u.csce.modeltool.spec.ModelToolSpecPlugin;
import jp.ac.kyushu_u.csce.modeltool.spec.constant.SpecPreferenceConstants;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * 仕様書エディター<br>
 * 折り返し切り替えのハンドラークラス
 * @author KBK yoshimura
 */
public class WordwrapHandler extends AbstractHandler {

	/**
	 * execute処理<br>
	 * エディターの端で行を折り返すかどうか切り替える
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// トグルボタンの状態を取得
		boolean toggle = HandlerUtil.toggleCommandState(event.getCommand());

		// プリファレンスの設定
		IPreferenceStore store = ModelToolSpecPlugin.getDefault().getPreferenceStore();
		store.setValue(SpecPreferenceConstants.PK_SPECEDITOR_WORDWRAP, ! toggle);

		return null;
	}
}
