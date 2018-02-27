package jp.ac.kyushu_u.csce.modeltool.explorer.explorer;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * 仕様書／辞書エクスプローラ コンテンツプロバイダー・クラス
 *
 * @author KBK yoshimura
 */
public class FileTreeContentProvider implements ITreeContentProvider {

	/**
	 * 子要素の取得
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(Object)
	 */
	public Object[] getChildren(Object parent) {

		// フォルダの場合
		if (parent instanceof IContainer) {
			try {
				// プロジェクトが閉じられている場合
				if (parent instanceof IProject &&
						((IProject)parent).isOpen() == false) {
					// 子要素0個
					return new Object[0];
				}
				// 配下メンバを返す
				return ((IContainer) parent).members();
			} catch (CoreException e) {
			}
		}

		// フォルダ以外は子要素0個
		return new Object[0];
	}

	/**
	 * 子要素の取得
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getElements(Object)
	 */
	public Object[] getElements(Object element) {
		return getChildren(element);
	}

	/**
	 * 子要素の有無
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(Object)
	 */
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	/**
	 * 親要素の取得
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(Object)
	 */
	public Object getParent(Object element) {
		return ((IResource)element).getParent();
	}

	/**
	 * dispose
	 */
	public void dispose() {
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#inputChanged(Viewer, Object, Object)
	 */
	public void inputChanged(Viewer viewer, Object old_input,
			Object new_input) {
	}
}