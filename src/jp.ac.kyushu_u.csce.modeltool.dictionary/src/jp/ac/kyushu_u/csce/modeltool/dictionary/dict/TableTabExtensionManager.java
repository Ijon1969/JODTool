package jp.ac.kyushu_u.csce.modeltool.dictionary.dict;

import java.util.ArrayList;
import java.util.List;

import jp.ac.kyushu_u.csce.modeltool.dictionary.ModelToolDictionaryPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

/**
 * 辞書タブ初期化管理クラス
 * @author yoshimura
 */
public class TableTabExtensionManager {

	/** 拡張レジストリ */
	private IExtensionRegistry fRegistry;

	/** イニシャライザ配列 */
	private List<ITableTabInitializer> initializers;

	/**
	 * コンストラクタ
	 */
	public TableTabExtensionManager() {
		// 拡張レジストリ
		fRegistry = Platform.getExtensionRegistry();

		initializers = new ArrayList<ITableTabInitializer>();
	}

	/**
	 * 初期化
	 * @param _tab
	 */
	public void initialize(TableTab _tab) {
		// 拡張ポイント「jp.ac.kyushu_u.csce.modeltool.dictionary.tableTabInitializer」
		IExtensionPoint fPoint = fRegistry.getExtensionPoint(ModelToolDictionaryPlugin.PLUGIN_ID, "tableTabInitializer"); //$NON-NLS-1$
		// 拡張
		IExtension[] extensions = fPoint.getExtensions();
		for (IExtension extension : extensions) {

			// 拡張要素
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for(IConfigurationElement element : elements){

				// 拡張要素「initializer」
				if(element.getName().equals("initializer")){ //$NON-NLS-1$
					try {
						// 属性「class」
						ITableTabInitializer initializer = (ITableTabInitializer)element.createExecutableExtension("class"); //$NON-NLS-1$
						// 配列に追加
						initializers.add(initializer);
						// 初期化
						initializer.initialize(_tab);
					} catch (CoreException e) {
						// エラーログ出力
						IStatus status = new Status(
								IStatus.ERROR, ModelToolDictionaryPlugin.PLUGIN_ID,
								"initializer error", //$NON-NLS-1$
								e);
						ModelToolDictionaryPlugin.getDefault().getLog().log(status);
					}
				}
			}
		}
	}

	/**
	 * ロード後処理
	 * @param _tab
	 */
	public void afterLoad(TableTab _tab) {
		// 拡張ポイント「jp.ac.kyushu_u.csce.modeltool.dictionary.fileLoader」
		IExtensionPoint fPoint = fRegistry.getExtensionPoint(ModelToolDictionaryPlugin.PLUGIN_ID, "fileLoader"); //$NON-NLS-1$
		// 拡張
		IExtension[] extensions = fPoint.getExtensions();
		for (IExtension extension : extensions) {

			// 拡張要素
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for(IConfigurationElement element : elements){

				// 拡張要素「loader」
				if(element.getName().equals("loader")){ //$NON-NLS-1$
					try {
						// 属性「class」
						ITableTabFileLoader loader = (ITableTabFileLoader)element.createExecutableExtension("class"); //$NON-NLS-1$
						// 初期化
						loader.afterLoadFile(_tab);
					} catch (CoreException e) {
						// エラーログ出力
						IStatus status = new Status(
								IStatus.ERROR, ModelToolDictionaryPlugin.PLUGIN_ID,
								"loader error", //$NON-NLS-1$
								e);
						ModelToolDictionaryPlugin.getDefault().getLog().log(status);
					}
				}
			}
		}
	}

	/**
	 * 終了処理
	 * @param _tab
	 */
	public void finalize(TableTab _tab) {
		for (ITableTabInitializer initializer : initializers) {
			initializer.finalize(_tab);
		}
	}
}
