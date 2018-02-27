package jp.ac.kyushu_u.csce.modeltool.dictionary.utility;

import java.util.ArrayList;
import java.util.List;

import jp.ac.kyushu_u.csce.modeltool.base.dialog.CInputDialog;
import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;
import jp.ac.kyushu_u.csce.modeltool.dictionary.Messages;
import jp.ac.kyushu_u.csce.modeltool.dictionary.ModelToolDictionaryPlugin;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryView;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;
import jp.ac.kyushu_u.csce.modeltool.dictionary.listener.IDictionaryRenameListener;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * 辞書の名前変更を行うクラス<br>
 * 辞書ビュー及び仕様書／辞書エクスプローラービューの双方から呼び出されるため
 * ビューとは別クラスに分割しています。
 *
 * @author KBK yoshimura
 */
public class DictionaryRenameUtility {

	/**
	 * 辞書名変更用のパラメータークラス
	 */
	private class DictionaryRenameParameter {

		/** 辞書タブ */
		TableTab tab;
		/** 辞書ファイル */
		IFile file;
	}

	/** インスタンス */
	private static DictionaryRenameUtility dr;

	private List<IDictionaryRenameListener> listeners;

	/**
	 * コンストラクタ
	 */
	private DictionaryRenameUtility() {
		listeners = new ArrayList<IDictionaryRenameListener>();
	}

	/**
	 * リスナーの追加
	 * @param listener リスナー
	 */
	public static void addListener(IDictionaryRenameListener listener) {
		if (dr == null) {
			dr = new DictionaryRenameUtility();
		}
		dr.listeners.add(listener);
	}

	/**
	 * 辞書名変更
	 * @param tab 辞書タブ - 辞書ビューから変更する場合に指定する
	 * @param file 辞書ファイル - エクスプローラービューから変更する場合は必須、
	 * 								辞書ビューからの変更の場合はファイルが存在すれば指定する
	 * @return true:正常終了／false:キャンセル
	 */
	public static boolean rename(TableTab tab, IFile file) {

		if (file != null && !PluginHelper.in(file.getFileExtension(), false, DictionaryConstants.DICTIONARY_EXTENSIONS)) {
			return false;
		}

		if (dr == null) {
			dr = new DictionaryRenameUtility();
		}

		DictionaryRenameParameter param = dr.new DictionaryRenameParameter();
		param.tab = tab;
		param.file = file;

		return dr.renameDictionary(param);
	}

	/**
	 * 名前変更
	 * @return 変更結果
	 */
	private boolean renameDictionary(final DictionaryRenameParameter param) {

		// 引数がすべてnullの場合、エラー
		if  (param.tab == null && param.file == null) {
			RuntimeException e = new RuntimeException();
			IStatus status = new Status(
					IStatus.ERROR, ModelToolDictionaryPlugin.PLUGIN_ID,
					"rename dictionary error : all arguments are null", //$NON-NLS-1$
					e);
			ModelToolDictionaryPlugin.getDefault().getLog().log(status);
			return false;
		}

		IWorkbenchPage page = ModelToolDictionaryPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();

		// 各ビューの取得
		DictionaryView dicView = (DictionaryView)page.findView(DictionaryConstants.PART_ID_DICTIONARY);

		// エクスプローラーから変更 かつ 辞書ビューオープンの場合
		if (param.tab == null && dicView != null) {
			// ファイルを開いているタブを取得する
			param.tab = dicView.getTabOfFile(param.file);
		}

		// Shellの取得
		Shell shell = null;
		if (param.tab != null) {
			shell = dicView.getSite().getShell();

			// 辞書が編集されている場合
			if (param.tab.isDirty() && param.file != null) {
				if (MessageDialog.openConfirm(shell, Messages.DictionaryRename_1,
						param.tab.getDictionaryName() + Messages.DictionaryRename_2 +
						Messages.DictionaryRename_3) == false) {
					return false;
				}
			}
		} else {
			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			shell = window.getShell();
		}

		// 変更前の名前の取得
		final String oldName = (param.file != null)?
						PluginHelper.getFileNameWithoutExtension(param.file) : param.tab.getDictionaryName();

		CInputDialog dialog = new CInputDialog(shell,
				Messages.DictionaryRename_1, Messages.DictionaryRename_5, oldName,
				new IInputValidator() {
			public String isValid(String newText) {
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IStatus status = workspace.validateName(newText, IResource.FILE);
				if (newText.equals(oldName)) {
					return ""; //$NON-NLS-1$
				}
				if (status.isOK() == false) {
					return status.getMessage();
				}
				if (param.file != null) {
					IContainer container = param.file.getParent();
					if (container.findMember(newText + "." + param.file.getFileExtension()) != null) { //$NON-NLS-1$
						return Messages.DictionaryRename_8;
					}
				}
				return null;
			}
		});
		if (CInputDialog.CANCEL == dialog.open()) {
			return false;
		}

		// 変更後の名前の取得
		final String newName = dialog.getValue();

		// ファイルが存在する場合、ファイル名変更
		if (param.file != null) {
			// ファイルがローカルに存在しない場合（ツール外のプラグインで削除・変更された）
			if (param.file.exists() == false) {
				MessageDialog.openError(shell,
						Messages.DictionaryRename_1, Messages.DictionaryRename_10);
				return false;
			}

			if (param.tab != null && param.tab.isDirty()) {
				param.tab.save(false);
			}

			IPath oldPath = param.file.getFullPath();
			final IPath newPath = param.file.getParent().getFullPath().append(newName + "." + oldPath.getFileExtension()); //$NON-NLS-1$

			if (param.tab != null) {
				param.tab.clearFile();
			}

			// 別スレッドでファイル名変更処理を行う
			page.getWorkbenchWindow().getShell().getDisplay().asyncExec(new Runnable() {

				public void run() {
					try {
						param.file.move(newPath, true, ModelToolDictionaryPlugin.getProgressMonitor());
					} catch (CoreException e) {
						// エラーログ出力
						IStatus status = new Status(
								IStatus.ERROR, ModelToolDictionaryPlugin.PLUGIN_ID,
								"failed to rename file", //$NON-NLS-1$
								e);
						ModelToolDictionaryPlugin.getDefault().getLog().log(status);
					}

					param.file = PluginHelper.getFile(newPath);
					if (param.tab != null) {
						param.tab.setFile(param.file);
					}

					// タブで開いている場合、タブ名変更
					if (param.tab != null) {
						param.tab.activate();
						param.tab.setText(newName);
						if (param.file != null) {
							param.tab.setToolTipText(PluginHelper.getRelativePath(param.file));
						}
					}

					// リスナーを実行
					for (IDictionaryRenameListener listener : dr.listeners) {
						listener.dictionaryRenamed(param.file);
					}
				}
			});

		// タブで開いている場合、タブ名変更
		} else if (param.tab != null) {
			param.tab.activate();
			param.tab.setText(newName);
			if (param.file != null) {
				param.tab.setToolTipText(PluginHelper.getRelativePath(param.file));
			} else {
				param.tab.setToolTipText(newName);
			}
		}

		return true;
	}
}
