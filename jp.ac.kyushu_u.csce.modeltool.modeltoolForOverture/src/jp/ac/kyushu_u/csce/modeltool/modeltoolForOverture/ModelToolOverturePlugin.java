package jp.ac.kyushu_u.csce.modeltool.modeltoolForOverture;

import jp.ac.kyushu_u.csce.modeltool.modeltoolForOverture.listener.VdmProjectCreatedListener;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle<br>
 * <br>
 * Eclipse起動時に当プラグインも起動させるため {@link org.eclipse.ui.IStartup} インターフェースを実装し、
 * 拡張ポイント {@code org.eclipse.ui} に登録する。<br>
 * ※起動時にワークスペースへ {@link IResourceChangeListener} の登録を行うため。
 */
public class ModelToolOverturePlugin extends AbstractUIPlugin implements IStartup {

	// The plug-in ID
	public static final String PLUGIN_ID = "jp.ac.kyushu_u.csce.modeltool.modeltoolForOverture"; //$NON-NLS-1$

	// The shared instance
	private static ModelToolOverturePlugin plugin;

	// リソース変更リスナー
	private static IResourceChangeListener rcListener;

	/**
	 * The constructor
	 */
	public ModelToolOverturePlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		// リソース変更リスナーの追加
		rcListener = new VdmProjectCreatedListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(rcListener, IResourceChangeEvent.POST_CHANGE);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);

		// リソース変更リスナーの削除
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(rcListener);
		rcListener = null;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ModelToolOverturePlugin getDefault() {
		return plugin;
	}

	/*
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	@Override
	public void earlyStartup() {
		// 処理なし
		// startupメソッドが実行されるため、こちらのメソッドは何もしない
	}

}
