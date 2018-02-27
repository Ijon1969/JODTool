package jp.ac.kyushu_u.csce.modeltool.base.dialog;

import jp.ac.kyushu_u.csce.modeltool.base.Messages;
import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;


/**
 * カスタムファイルダイアログ<br>
 * FileDialogクラスに、IFileおよびIContainer取得機能を付加したクラス
 *
 * 使用していません　削除予定
 *
 * @author KBK yoshimura
 * @deprecated
 */
public class CustomFileDialog {

	/** ファイルダイアログ */
	private FileDialog dialog;

	/** コンテナハンドル */
	private IContainer container;

	/** ファイルハンドル */
	private IFile file;

	/** ファイル名 */
	private String fileName;

	/** フィルターパス */
	private String filterPath;

	/** フルパス */
	private String fullPath;

	/**
	 * コンストラクタ<br>
	 * FileDialogの設定を行った後、当クラスにセットする
	 */
	public CustomFileDialog(FileDialog dialog) {
		this.dialog = dialog;
	}

	/**
	 * ファイルダイアログを開く
	 * @return 選択したファイルを示すファイルハンドル。選択をキャンセルした場合または
	 * エラーが発生した場合にnullを返す
	 */
	public String open2() {

		String fullPath = dialog.open();
		if (fullPath == null) {
			return null;
		}

		// フォルダパス・ファイル名取得
		filterPath = dialog.getFilterPath();
		fileName = dialog.getFileName();

		// MacOS(cocoa)対応のため、filterPath及びfileNameはfullPathから取得する
		fileName = new Path(fullPath).lastSegment();
		int index = fullPath.lastIndexOf(fileName);
		filterPath = fullPath.substring(0, index);

		// フォルダハンドルの取得
		IPath path = new Path(filterPath);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		container = (IContainer)root.getContainerForLocation(path);

		// ワークスペース直下を指定した場合
		if (root.equals(container)) {
			if ((dialog.getStyle() & SWT.SAVE) != 0) {
				MessageDialog.openWarning(dialog.getParent(), Messages.CustomFileDialog_0, Messages.CustomFileDialog_1);
			} else {
				MessageDialog.openWarning(dialog.getParent(), Messages.CustomFileDialog_2, Messages.CustomFileDialog_3);
			}
			return null;
		}

		// ワークスペース外を指定した場合
		if (container == null) {
			if ((dialog.getStyle() & SWT.SAVE) != 0) {
				MessageDialog.openWarning(dialog.getParent(), Messages.CustomFileDialog_0, Messages.CustomFileDialog_5);
			} else {
				MessageDialog.openWarning(dialog.getParent(), Messages.CustomFileDialog_2, Messages.CustomFileDialog_7);
			}
			return null;
		}

		// ファイルハンドルの取得
		file = PluginHelper.getFile(container, fileName);

		return fullPath;
	}

	private int openInternal() {

		fullPath = dialog.open();
		if (fullPath == null) {
			return 0;
		}

		// フォルダパス・ファイル名取得
		filterPath = dialog.getFilterPath();
		fileName = dialog.getFileName();

		// MacOS(cocoa)対応のため、filterPath及びfileNameはfullPathから取得する
		fileName = new Path(fullPath).lastSegment();
		int index = fullPath.lastIndexOf(fileName);
		filterPath = fullPath.substring(0, index);

		// フォルダハンドルの取得
		IPath path = new Path(filterPath);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		container = (IContainer)root.getContainerForLocation(path);

		// ワークスペース直下を指定した場合
		if (root.equals(container)) {
			if ((dialog.getStyle() & SWT.SAVE) != 0) {
				MessageDialog.openWarning(dialog.getParent(), Messages.CustomFileDialog_0, Messages.CustomFileDialog_1);
			} else {
				MessageDialog.openWarning(dialog.getParent(), Messages.CustomFileDialog_2, Messages.CustomFileDialog_3);
			}
			return 1;
		}

		// ワークスペース外を指定した場合
		if (container == null) {
			if ((dialog.getStyle() & SWT.SAVE) != 0) {
				MessageDialog.openWarning(dialog.getParent(), Messages.CustomFileDialog_0, Messages.CustomFileDialog_5);
			} else {
				MessageDialog.openWarning(dialog.getParent(), Messages.CustomFileDialog_2, Messages.CustomFileDialog_7);
			}
			return 1;
		}

		// ファイルハンドルの取得
		file = PluginHelper.getFile(container, fileName);

		// SAVEモードかつファイルが存在する場合
		if ((dialog.getStyle() & SWT.SAVE) != 0 && file.exists()) {
			MessageDialog.openWarning(dialog.getParent(), Messages.CustomFileDialog_0, Messages.CustomFileDialog_17);
			return 1;
		}

		return 0;
	}

	public String open() {

		while (openInternal() != 0) {
			dialog.setFilterPath(filterPath);
			dialog.setFileName(fileName);
		}
		return fullPath;
	}

	/**
	 * 選択したフォルダのハンドルを取得する
	 * @return フォルダハンドル
	 */
	public IContainer getConteiner() {
		return container;
	}

	/**
	 * 選択したファイルのハンドルを取得する
	 * @return ファイルハンドル
	 */
	public IFile getFile() {
		return file;
	}

	/**
	 * ファイル名の取得
	 * @return ファイル名
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * フィルターパスの取得
	 * @return フィルターパス
	 */
	public String getFilterPath() {
		return filterPath;
	}
}
