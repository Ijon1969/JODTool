package jp.ac.kyushu_u.csce.modeltool.explorer.explorer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;
import jp.ac.kyushu_u.csce.modeltool.explorer.Messages;
import jp.ac.kyushu_u.csce.modeltool.explorer.ModelToolExplorerPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IEditorRegistry;

/**
 * 仕様書／辞書エクスプローラの拡張ポイントを管理するクラス
 *
 * @author KBK yoshimura
 */
public class ExplorerExtensionManager {

	/** 拡張レジストリ */
	private IExtensionRegistry fRegistry;
	/** 拡張子－エディタIDのマップ */
	private Map<String, String> fEditorMap;
	/** 拡張子－Opener マップ */
	private Map<String, IOpener> fOpenerMap;
	/** 拡張子リスト */
	private Set<String> fExtensionList;

	/** エディタレジストリ */
	private IEditorRegistry fEditorRegistry;

	/**
	 * コンストラクタ
	 */
	public ExplorerExtensionManager() {

		fEditorMap = new HashMap<String, String>();
		fOpenerMap = new HashMap<String, IOpener>();
		fExtensionList = new HashSet<String>();

		// エディタレジストリ
		fEditorRegistry = ModelToolExplorerPlugin.getDefault().getWorkbench().getEditorRegistry();

		// 拡張レジストリ
		fRegistry = Platform.getExtensionRegistry();
		// 拡張ポイント「jp.ac.kyushu_u.csce.modeltool.explorerExtension」
		IExtensionPoint point = fRegistry.getExtensionPoint(ModelToolExplorerPlugin.PLUGIN_ID, "explorerExtension"); //$NON-NLS-1$

		// 拡張
		IExtension[] extensions = point.getExtensions();
		for (IExtension extension : extensions) {

			// 拡張要素
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {

				// 拡張要素「fileExtension」
				if (element.getName().equals("fileExtension")) { //$NON-NLS-1$
					boolean flg = true;
					// 属性「extensions」
					String fileExtensions = element.getAttribute("extensions"); //$NON-NLS-1$
					// 属性「editorId」
					String editorId = element.getAttribute("editorId"); //$NON-NLS-1$
					if (!PluginHelper.isEmpty(editorId)) {
						if (fEditorRegistry.findEditor(editorId) == null) {
							flg = false;
						}
					}
					// 属性「opener」
					IOpener opener = null;
					if (element.getAttribute("opener") != null) { //$NON-NLS-1$
						try {
							opener = (IOpener)element.createExecutableExtension("opener"); //$NON-NLS-1$
						} catch (CoreException e) {
							// エラーログ出力
							IStatus status = new Status(
									IStatus.ERROR, ModelToolExplorerPlugin.PLUGIN_ID,
									Messages.ExplorerExtensionManager_7,
									e);
							ModelToolExplorerPlugin.getDefault().getLog().log(status);
							flg = false;
						}
					}

					if (flg) {
						// extensions（拡張子）属性をカンマで分割
						String[] fileExtensionArray = fileExtensions.split(","); //$NON-NLS-1$
						for (String fileExtension : fileExtensionArray) {

							// 拡張子リストに登録
							fExtensionList.add(fileExtension);

							// 拡張子とファイル読込クラスをマップに登録
							if (opener != null) {
								fOpenerMap.put(fileExtension.trim(), opener);
							}

							// 拡張子とエディタIDをマップに登録
							if (fileExtension.trim().length() > 0) {
								fEditorMap.put(fileExtension.trim(), editorId);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 拡張－エディタIDマップを取得する
	 * @return マップ
	 */
	public Map<String, String> getEditorMap() {
		return fEditorMap;
	}

	/**
	 * 拡張－OPENERを取得する
	 * @return マップ
	 */
	public Map<String, IOpener> getOpenerMap() {
		return fOpenerMap;
	}

	/**
	 * 拡張子リストを取得する
	 * @return マップ
	 */
	public Set<String> getExtensionList() {
		return fExtensionList;
	}
}
