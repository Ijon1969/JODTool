package jp.ac.kyushu_u.csce.modeltool.base;

import jp.ac.kyushu_u.csce.modeltool.base.utility.ColorManager;
import jp.ac.kyushu_u.csce.modeltool.base.utility.HandlerActivationManager;
import jp.ac.kyushu_u.csce.modeltool.base.utility.ScopeUtility;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 */
public class ModelToolBasePlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "jp.ac.kyushu_u.csce.modeltool.base"; //$NON-NLS-1$

	// The shared instance
	private static ModelToolBasePlugin plugin;

	// カラーマネージャー
	private static ColorManager colorManager;

	// ハンドラマネージャー
	private static HandlerActivationManager handlerManager;

	// インスタンススコープ
	private static IScopeContext scopeContext;

	/**
	 * The constructor
	 */
	public ModelToolBasePlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@SuppressWarnings("deprecation")
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		colorManager = new ColorManager();
		handlerManager = new HandlerActivationManager();

		// インスタンススコープの取得
		// 3.6以前のバージョンだと実行時エラーになるので対応
		// 3.7でコンパイルしているのでコンパイルエラーになりません
		try {
			scopeContext = InstanceScope.INSTANCE;
		} catch (NoSuchFieldError e) {
			scopeContext = new InstanceScope();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		colorManager.dispose();
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ModelToolBasePlugin getDefault() {
		return plugin;
	}

	/**
	 * カラーマネージャーを返す
	 * @return カラーマネージャー
	 */
	public static ColorManager getColorManager() {
		return colorManager;
	}

	public static HandlerActivationManager getHandlerActivationManager() {
		return handlerManager;
	}

	/**
	 * プログレスモニターの取得<br>
	 * WorkbenchWindowから直接取ろうとすると警告が出るため
	 * アクティブなエディターまたはビューから取得しています。
	 * @return プログレスモニター
	 */
	public static IProgressMonitor getProgressMonitor() {

		IWorkbenchPart part =
			getDefault().getWorkbench().getActiveWorkbenchWindow()
			.getActivePage().getActivePart();

		if (part instanceof IEditorPart) {
			return ((IEditorPart)part).getEditorSite().getActionBars()
					.getStatusLineManager().getProgressMonitor();
		}

		if (part instanceof IViewPart) {
			return ((IViewPart)part).getViewSite().getActionBars()
					.getStatusLineManager().getProgressMonitor();
		}

		return null;
	}

	/**
	 * インスタンススコープの取得<br>
	 * {@link ScopeUtility#getInstanceScope()}を使ってください
	 * @return インスタンススコープ
	 */
	public static IScopeContext getInstanceScope() {
		return scopeContext;
	}
}
