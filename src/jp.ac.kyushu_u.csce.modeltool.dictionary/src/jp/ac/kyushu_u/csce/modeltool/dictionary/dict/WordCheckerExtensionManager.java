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
 * 見出し語チェック拡張管理クラス
 * @author yoshimura
 */
public class WordCheckerExtensionManager {

	/** 拡張レジストリ */
	private IExtensionRegistry fRegistry;

	/** 見出し語チェック配列 */
	private List<IWordChecker> checkers = new ArrayList<IWordChecker>();

	/**
	 * コンストラクタ
	 */
	public WordCheckerExtensionManager() {
		// 拡張レジストリ
		fRegistry = Platform.getExtensionRegistry();		// 拡張ポイント「jp.ac.kyushu_u.csce.modeltool.dictionary.wordChecker」
		IExtensionPoint fPoint = fRegistry.getExtensionPoint(ModelToolDictionaryPlugin.PLUGIN_ID, "wordChecker"); //$NON-NLS-1$
		// 拡張
		IExtension[] extensions = fPoint.getExtensions();
		for (IExtension extension : extensions) {

			// 拡張要素
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for(IConfigurationElement element : elements){

				// 拡張要素「checker」
				if(element.getName().equals("checker")){ //$NON-NLS-1$
					try {
						// 属性「class」
						IWordChecker checker = (IWordChecker)element.createExecutableExtension("class"); //$NON-NLS-1$
						checkers.add(checker);
					} catch (CoreException e) {
						// エラーログ出力
						IStatus status = new Status(
								IStatus.ERROR, ModelToolDictionaryPlugin.PLUGIN_ID,
								"word checker error", //$NON-NLS-1$
								e);
						ModelToolDictionaryPlugin.getDefault().getLog().log(status);
					}
				}
			}
		}
	}

	/**
	 * 初期化
	 * @param _tab
	 */
	public boolean check(Entry _entry) {
		for (IWordChecker checker : checkers) {
			if (!checker.check(_entry)) {
				return false;
			}
		}
		return true;
	}
}
