package jp.ac.kyushu_u.csce.modeltool.explorer.explorer;

import jp.ac.kyushu_u.csce.modeltool.base.constant.ToolConstants;
import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * 拡張子フィルター<br>
 * 指定した拡張子のファイルのみ表示する
 */
public class FileExtensionFilter extends ViewerFilter {

	/**
	 * 表示対象の拡張子
	 */
	private String[] FILE_EXTENSIONS = (String[])PluginHelper.arrayConcat(String.class,
				ToolConstants.SPEC_EXTENSIONS,
				ToolConstants.EXCEL_EXTENSIONS,
				ToolConstants.DICTIONARY_EXTENSIONS,
				ToolConstants.MODEL_EXTENSIONS);

	/**
	 * フィルタを掛けるかどうか判定する
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(Viewer, Object, Object)
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {

		if (element instanceof IFile) {
			boolean select = false;

			// ファイルの拡張子
			String extension = ((IFile)element).getFileExtension();

			// 拡張ポイントで定義された拡張子
			String[] extendedExtensions =
					new ExplorerExtensionManager().getEditorMap().keySet().toArray(new String[0]);

			// 定数定義した拡張子＋拡張ポイント定義の拡張子に、ファイル拡張子が含まれていれば表示する
			String[] extensions =
					(String[])PluginHelper.arrayConcat(String.class, FILE_EXTENSIONS, extendedExtensions);
			select = PluginHelper.in(extension, false, extensions);
			return select;

		} else {
			// ファイル以外は表示
			return true;
		}
	}
}
