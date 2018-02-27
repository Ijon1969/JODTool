package jp.ac.kyushu_u.csce.modeltool.base.utility;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener2;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;

/**
 * ビューのベースとなる抽象クラス<br>
 *
 * ビューを作成する際は当クラスを拡張し、必要なメソッドのみ実装すること。
 *
 * @author KBK yoshimura
 */
public abstract class AbstractViewPart extends ViewPart
			implements IPartListener2, IPerspectiveListener2, IWorkbenchListener {

	// IViewPart オーバーライドメソッド
	@Override
	public void setFocus() {}

	// IPartListner2 オーバーライドメソッド
	@Override
	public void partActivated(IWorkbenchPartReference partRef) {}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {}

	@Override
	public void partClosed(IWorkbenchPartReference partRef) {}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {}

	@Override
	public void partHidden(IWorkbenchPartReference partRef) {}

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {}

	@Override
	public void partOpened(IWorkbenchPartReference partRef) {}

	@Override
	public void partVisible(IWorkbenchPartReference partRef) {}


	// IPerspectiveListener2 オーバーライドメソッド
	@Override
	public void perspectiveActivated(IWorkbenchPage page,
			IPerspectiveDescriptor perspective) {}

	@Override
	public void perspectiveChanged(IWorkbenchPage page,
			IPerspectiveDescriptor perspective, String changeId) {}

	@Override
	public void perspectiveChanged(IWorkbenchPage page,
			IPerspectiveDescriptor perspective,
			IWorkbenchPartReference partRef, String changeId) {}


	// IWorkbenchListener オーバーライドメソッド
	@Override
	public void postShutdown(IWorkbench workbench) {}

	@Override
	public boolean preShutdown(IWorkbench workbench, boolean forced) {
		return true;
	}
}
