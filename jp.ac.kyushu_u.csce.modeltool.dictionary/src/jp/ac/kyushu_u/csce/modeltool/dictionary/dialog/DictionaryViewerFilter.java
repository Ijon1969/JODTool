package jp.ac.kyushu_u.csce.modeltool.dictionary.dialog;

import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * 辞書ファイル表示用フィルター<br>
 * ファイル選択ダイアログに辞書ファイルの存在するフォルダのみ表示するフィルター
 *
 * @author KBK yoshimura
 */
public class DictionaryViewerFilter extends ViewerFilter {

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return hasValidChildren((IResource)element);
	}

	/**
	 * 辞書ファイルを配下に持つか再帰チェックを行う
	 * @param resource リソース
	 * @return 指定されたリソース（フォルダ）配下に、
	 *          辞書ファイルが存在する場合にtrueを返す。
	 */
	private boolean hasValidChildren(IResource resource) {

		// ファイルの場合
		if (resource instanceof IFile) {
			// 辞書ファイルの場合、trueを返す
			return PluginHelper.in(((IFile)resource).getFileExtension(),
					false, DictionaryConstants.DICTIONARY_EXTENSIONS);
		}

		// コンテナの場合
		if (resource instanceof IContainer) {

			// プロジェクトは表示する
			if (resource instanceof IProject) {
				return true;
			}

			// メンバーに対して再帰チェック
			IResource[] members = null;
			try {
				members = ((IContainer)resource).members();
			} catch (CoreException e) {
			}
			if (members != null && members.length > 0) {
				for (IResource member : members) {
					if (hasValidChildren(member)) {
						return true;
					}
				}
			}
			return false;
		}

		return false;
	}
}
