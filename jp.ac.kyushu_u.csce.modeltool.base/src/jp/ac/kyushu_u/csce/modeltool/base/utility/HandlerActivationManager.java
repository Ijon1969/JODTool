package jp.ac.kyushu_u.csce.modeltool.base.utility;

import java.util.HashMap;
import java.util.Map;

import jp.ac.kyushu_u.csce.modeltool.base.ModelToolBasePlugin;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.State;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.handlers.RegistryToggleState;

/**
 * ハンドラ、コマンドの管理を行うクラス
 *
 * @author KBK yoshimura
 */
public class HandlerActivationManager {

	/**
	 * コマンドID－ハンドラアクティベーション 対応マップ
	 */
	protected Map<String, IHandlerActivation> handlerMap =
		new HashMap<String, IHandlerActivation>();

	/**
	 * ハンドラサービスの取得
	 * @return ハンドラサービス
	 */
	private IHandlerService getHandlerService() {
		IWorkbenchWindow window = ModelToolBasePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		return (IHandlerService)window.getService(IHandlerService.class);
	}

	/**
	 * コマンドサービスの取得
	 * @return コマンドサービス
	 */
	private ICommandService getCommandService() {
		IWorkbenchWindow window = ModelToolBasePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		return (ICommandService)window.getService(ICommandService.class);
	}

	/**
	 * コマンドにハンドラーを設定する。
	 * @param commandId コマンドID
	 * @param handler ハンドラーインスタンス
	 */
	public void activate(String commandId, IHandler handler) {

		Assert.isNotNull(handler);

		// ハンドラサービスの取得
		IHandlerService service = getHandlerService();

		// 登録済みハンドラの削除
		IHandlerActivation activation = handlerMap.get(commandId);
		if (activation != null) {
			service.deactivateHandler(activation);
		}

		// ハンドラの登録
		handlerMap.put(commandId, service.activateHandler(commandId, handler));
	}

	/**
	 * コマンドにハンドラーを設定する。
	 * @param commandId コマンドID
	 * @param handlerClass ハンドラークラス
	 */
	public void activate(String commandId, Class<? extends IHandler> handlerClass) {

		Assert.isNotNull(handlerClass);

		// ハンドラサービスの取得
		IHandlerService service = getHandlerService();

		// 登録済みハンドラの削除
		IHandlerActivation activation = handlerMap.get(commandId);
		if (activation != null) {
			if (! handlerClass.equals(activation.getHandler().getClass())) {
				// 登録済みハンドラと追加するハンドラが異なる場合に、削除を行う
				service.deactivateHandler(activation);
			} else {
				return;
			}
		}

		try {
			// ハンドラインスタンスの作成
			IHandler handler = (IHandler)handlerClass.newInstance();
			// ハンドラの登録
			handlerMap.put(commandId, service.activateHandler(commandId, handler));

		} catch (Exception e) {
			e.printStackTrace();
			IStatus status = new Status(
					IStatus.ERROR, ModelToolBasePlugin.PLUGIN_ID,
					"failed to create handler class \"" + handlerClass.getName() + "\"", //$NON-NLS-1$ //$NON-NLS-2$
					e);
			ModelToolBasePlugin.getDefault().getLog().log(status);
		}
	}

	/**
	 * コマンドのハンドラーを無効化する。
	 * @param commandId コマンドID
	 */
	public void deactivate(String commandId) {

		IHandlerService service = getHandlerService();

		IHandlerActivation activation = handlerMap.get(commandId);
		if (activation != null) {
			service.deactivateHandler(activation);
			handlerMap.remove(commandId);
		}
	}

	/**
	 * 全ハンドラの削除
	 */
	public void clearActivation() {

		IHandlerService service = getHandlerService();

		for (IHandlerActivation activation : handlerMap.values()) {
			service.deactivateHandler(activation);
		}

		handlerMap.clear();
	}

	/**
	 * トグルコマンドのON/OFF切替
	 * @param commandId
	 */
	public void changeToggleState(String commandId) {

		ICommandService commandService = getCommandService();
		Command toggleCommand = commandService.getCommand(commandId);
		State state = toggleCommand.getState(RegistryToggleState.STATE_ID);
		state.setValue(!((Boolean)state.getValue()));
		commandService.refreshElements(toggleCommand.getId(), null);
	}

	/**
	 * トグルコマンドの状態設定
	 * @param commandId
	 * @param value
	 */
	public void changeToggleState(String commandId, boolean value) {

		ICommandService commandService = getCommandService();
		Command toggleCommand = commandService.getCommand(commandId);
		State state = toggleCommand.getState(RegistryToggleState.STATE_ID);
		state.setValue(value);
		commandService.refreshElements(toggleCommand.getId(), null);
	}

	/**
	 * オブジェクトの破棄
	 */
	public void dispose() {
	}
}
