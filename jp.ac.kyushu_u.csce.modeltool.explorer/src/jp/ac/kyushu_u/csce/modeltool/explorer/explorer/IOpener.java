package jp.ac.kyushu_u.csce.modeltool.explorer.explorer;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

public interface IOpener {

	public IWorkbenchPart open(IWorkbenchPage _page, IFile _file);
}
