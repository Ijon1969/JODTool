package jp.ac.kyushu_u.csce.modeltool.dictionaryExp.explorer;

import jp.ac.kyushu_u.csce.modeltool.base.ModelToolBasePlugin;
import jp.ac.kyushu_u.csce.modeltool.explorer.explorer.IOpener;
import jp.ac.kyushu_u.csce.modeltool.dictionaryExp.Messages;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryView;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;

/**
 * 仕様書/辞書エクスプローラーで辞書を開くためのクラス<br>
 * 拡張ポイント「jp.ac.kyushu_u.csce.modeltool.explorer.explorerExtension」で使用
 */
public class DictionaryOpener implements IOpener {

	@Override
	public IWorkbenchPart open(IWorkbenchPage _page, IFile _file) {

		DictionaryView view = null;
		try {
			view = (DictionaryView)_page.findView(DictionaryConstants.PART_ID_DICTIONARY);
			if (view != null) {
				_page.showView(DictionaryConstants.PART_ID_DICTIONARY);
				view.loadDictionary(_file);
			} else {
				view = (DictionaryView)_page.showView(DictionaryConstants.PART_ID_DICTIONARY);
				view.loadDictionaryToActiveTab(_file);
			}
		} catch (PartInitException e) {
			MessageDialog.openError(
					_page.getActivePart().getSite().getShell(),
					Messages.DictionaryOpener_0, Messages.DictionaryOpener_1);
			IStatus status = new Status(
					IStatus.ERROR, ModelToolBasePlugin.PLUGIN_ID,
					"failed to open file [path:" + _file.getLocation().toOSString() + "]", //$NON-NLS-1$ //$NON-NLS-2$
					e);
			ModelToolBasePlugin.getDefault().getLog().log(status);
		}
		return view;
	}
}
