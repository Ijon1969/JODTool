package jp.ac.kyushu_u.csce.modeltool.dictionary;

import jp.ac.kyushu_u.csce.modeltool.base.utility.ColorManager;
import jp.ac.kyushu_u.csce.modeltool.base.utility.HandlerActivationManager;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.ModelManager;
import jp.ac.kyushu_u.csce.modeltool.dictionary.utility.LanguageUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 */
public class ModelToolDictionaryPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "jp.ac.kyushu_u.csce.modeltool.dictionary"; //$NON-NLS-1$

	// The shared instance
	private static ModelToolDictionaryPlugin plugin;

	// カラーマネージャー
	private static ColorManager colorManager;

	// ハンドラマネージャー
	private static HandlerActivationManager handlerManager;

	// 入力言語ユーティリティ
	private static LanguageUtil languageUtil;

	/**
	 * The constructor
	 */
	public ModelToolDictionaryPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		colorManager = new ColorManager();
		handlerManager = new HandlerActivationManager();
		languageUtil = LanguageUtil.getInstance();

		// モデルマネジャー初期化のためプラグインロード時にインスタンスを生成しておく
		ModelManager.getInstance();
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
	public static ModelToolDictionaryPlugin getDefault() {
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
	 * 入力言語ユーティリティを返す
	 * @return 入力言語ユーティリティ
	 */
	public static LanguageUtil getLanguageUtil() {
		return languageUtil;
	}

	/**
	 * プログレスモニターの取得<br>
	 * WorkbenchWindowから直接取ろうとすると警告が出るため
	 * アクティブなエディターまたはビューから取得しています。
	 * @return
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
}
