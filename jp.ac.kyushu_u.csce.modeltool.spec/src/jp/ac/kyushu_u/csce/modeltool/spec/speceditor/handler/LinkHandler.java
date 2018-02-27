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
 * 辞書ビューとのリンクのハンドラークラス
 * @author KBK yoshimura
 */
public class LinkHandler extends AbstractHandler {

	/**
	 * execute処理<br>
	 * 辞書ビューとリンクするかどうか切り替える
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// トグルボタンの状態を取得
		boolean toggle = HandlerUtil.toggleCommandState(event.getCommand());

		// プリファレンスの設定
		IPreferenceStore store = ModelToolSpecPlugin.getDefault().getPreferenceStore();
		store.setValue(SpecPreferenceConstants.PK_LINK_DICTIONARY, ! toggle);

		return null;
	}
}
