package jp.ac.kyushu_u.csce.modeltool.explorer.explorer;

import jp.ac.kyushu_u.csce.modeltool.base.ModelToolBasePlugin;
import jp.ac.kyushu_u.csce.modeltool.base.constant.ToolConstants;
import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * 仕様書／辞書エクスプローラ ラベルプロバイダー・クラス
 *
 * @author KBK yoshimura
 */
public class FileTreeLabelProvider extends DecoratingLabelProvider {

	/**
	 * 辞書ファイルのアイコン
	 */
	private Image iconDictionary;

	/**
	 * コンストラクタ
	 * @param provider
	 * @param decorator
	 */
	public FileTreeLabelProvider(ILabelProvider provider,
			ILabelDecorator decorator) {
		super(provider, decorator);

		// アイコンの作成
		ImageDescriptor descriptor =
			ModelToolBasePlugin.imageDescriptorFromPlugin(ModelToolBasePlugin.PLUGIN_ID, ToolConstants.IMG_DICFILE);
		iconDictionary = descriptor.createImage();
	}

	/**
	 * 表示する文字列を取得する
	 * @see org.eclipse.jface.viewers.DecoratingLabelProvider#getText(Object)
	 */
	public String getText(Object element) {
		return ((IResource)element).getName();
	}

	/**
	 * 表示する画像を取得する
	 * @see org.eclipse.jface.viewers.DecoratingLabelProvider#getImage(Object)
	 */
	public Image getImage(Object element) {
		if (element instanceof IFile) {
			IFile file = (IFile)element;
			String extension = file.getFileExtension();
			if (PluginHelper.in(extension, false, ToolConstants.DICTIONARY_EXTENSIONS)) {
				return iconDictionary;
			}
		}
		return super.getImage(element);
	}

	/**
	 * dispose処理
	 */
	public void dispose() {
		super.dispose();
		iconDictionary.dispose();
	}
}
