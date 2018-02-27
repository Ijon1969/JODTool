package jp.ac.kyushu_u.csce.modeltool.explorer.explorer;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * リソースのソート順を規定するクラス
 *
 * @author KBK yoshimura
 */
public class ResourceSorter extends ViewerSorter {

	/** コンパレータ */
	protected ViewerComparator comparator;

	/**
	 * コンストラクタ
	 */
	public ResourceSorter() {
		comparator = new ViewerComparator();
	}

	/**
	 * リソース１がリソース２より小さい、等しい、大きい場合に、
	 * それぞれ負、0、正の値を返す
	 * @param viewer ビューアー
	 * @param e1 リソース１
	 * @param e2 リソース２
	 */
	public int compare(Viewer viewer, Object e1, Object e2) {

		// リソース１、２がフォルダの場合
		if (e1 instanceof IContainer && e2 instanceof IContainer) {
			return comparator.compare(viewer, e1, e2);
		}

		// リソース１がフォルダ、リソース２がファイルの場合
		if (e1 instanceof IContainer && e2 instanceof IFile) {
			return -1;
		}

		// リソース１がファイル、リソース２がフォルダの場合
		if (e1 instanceof IFile && e2 instanceof IContainer) {
			return 1;
		}

		// リソース１，２がファイルの場合
		if (e1 instanceof IFile && e2 instanceof IFile) {
			return comparator.compare(viewer, e1, e2);
		}

		return comparator.compare(viewer, e1, e2);
	}
}
