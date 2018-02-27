package jp.ac.kyushu_u.csce.modeltool.spec.speceditor.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Dictionary;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryUtil;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryView;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;
import jp.ac.kyushu_u.csce.modeltool.spec.Messages;
import jp.ac.kyushu_u.csce.modeltool.spec.ModelToolSpecPlugin;
import jp.ac.kyushu_u.csce.modeltool.spec.constant.SpecPreferenceConstants;
import jp.ac.kyushu_u.csce.modeltool.spec.dialog.DictionarySelectionDialog;
import jp.ac.kyushu_u.csce.modeltool.spec.speceditor.SpecEditor;
import jp.ac.kyushu_u.csce.modeltool.spec.speceditor.handler.SpecInspector.InspectException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * 仕様書エディター<br>
 * 仕様書検査処理のハンドラークラス
 * @author KBK yoshimura
 */
public class InspectHandler extends AbstractHandler {

	/**
	 * execute処理<br>
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// アクティブページの取得
		IWorkbenchPage page = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();

		// シェルの取得
		Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();

		// アクティブエディターの取得
		IEditorPart editor = (IEditorPart)page.getActiveEditor();
		if (editor instanceof SpecEditor == false) {
			return null;		// 仕様書エディター以外のエディターでは処理を行わない
		}

		// 辞書ビューの取得
		DictionaryView dicView = (DictionaryView)page.findView(DictionaryConstants.PART_ID_DICTIONARY);
		// 辞書ビューが開いていない場合、検査処理を行わない
		if (dicView == null) {
			MessageDialog.openWarning(shell, Messages.InspectHandler_0, Messages.InspectHandler_1);
			return null;
		}
		page.activate(dicView);

		// 辞書ビューが検査モードでない場合
		if (DictionaryUtil.isInspectMode() == false) {
			boolean mode = MessageDialog.openQuestion(shell, Messages.InspectHandler_0,
					Messages.InspectHandler_3);
			if (mode == false) {
				return null;
			}
			// 表示モードへ切替
			DictionaryUtil.setInspectMode();
		}

		// 辞書ビューのアクティブなタブを取得
		TableTab tab = dicView.getActiveTableTab(false);
		if (tab == null) {
			MessageDialog.openWarning(shell, Messages.InspectHandler_0, Messages.InspectHandler_5);
			return null;
		}

		// 複数辞書対応
		List<Dictionary> dictionaries = new ArrayList<Dictionary>();
		// 複数辞書を開いている場合
		if (dicView.getTabs().size() > 1) {
			// 辞書選択ダイアログを使用
			DictionarySelectionDialog dialog = new DictionarySelectionDialog(shell, DictionarySelectionDialog.MODE_INSPECT);
			dialog.setDictionaries(dicView.getTabs());
			if (dialog.open() != IDialogConstants.OK_ID) {
				return null;
			}
			for (Iterator<TableTab> itr = dialog.getResult().iterator(); itr.hasNext(); ) {
				dictionaries.add(itr.next().getDictionary());
			}
		} else {
			dictionaries.add(tab.getDictionary());
		}

		// 出力先フォルダの取得
		IContainer container = getOutputContainer(shell);
		if (container == null) {
			return null;
		}

		// 検査処理の実行
		IFile inspectedFile = (IFile)editor.getEditorInput().getAdapter(IFile.class);
		IFile markedFile = null;

		try {
			markedFile = new SpecInspector().inspectEditor(editor, dictionaries, container);
		} catch (InspectException e) {
			MessageDialog.openError(shell, Messages.InspectHandler_0,
					Messages.InspectHandler_7 + "\n" + //$NON-NLS-1$
					Messages.InspectHandler_9 + PluginHelper.getRelativePath(inspectedFile));
			throw new ExecutionException("failed to inspect specification", e); //$NON-NLS-1$
		}

		// 仕様書エディターのアクティブ化
		page.activate(editor);

		MessageDialog.openInformation(shell, Messages.InspectHandler_0,
				Messages.InspectHandler_12 + "\n" + //$NON-NLS-1$
				Messages.InspectHandler_9 + PluginHelper.getRelativePath(inspectedFile) + "\n" + //$NON-NLS-1$
				Messages.InspectHandler_16 + PluginHelper.getRelativePath(markedFile));

		return null;
	}

	/**
	 * 出力先フォルダをプリファレンスストアから取得する。
	 * 未指定の場合、またはワークスペースに存在しないフォルダを指定した場合、
	 * 続行するかどうか確認し、続行の場合はフォルダ選択ダイアログを表示する。
	 * @param parent エラーメッセージ出力用シェル
	 * @return 出力先フォルダ。キャンセルした場合はnullを返す。
	 */
	private IContainer getOutputContainer(Shell parent) {

		boolean openDialog = false;
		boolean cancel = false;
		IContainer container = null;

		IPreferenceStore store = ModelToolSpecPlugin.getDefault().getPreferenceStore();

		// 既定フォルダに出力する場合
		if (SpecPreferenceConstants.PV_MARK_FIXED ==
				store.getInt(SpecPreferenceConstants.PK_MARK_FOLDER_SETTING)) {

			// 出力先フォルダパスをプリファレンスストアから取得
			String location = store.getString(SpecPreferenceConstants.PK_MARK_FIXED_PATH);

			// 出力先が未設定の場合
			if (PluginHelper.isEmpty(location)) {
				openDialog = MessageDialog.openQuestion(parent, Messages.InspectHandler_17,
						Messages.InspectHandler_18 +
						Messages.InspectHandler_19 + "\n" + //$NON-NLS-1$
				Messages.InspectHandler_21);
				cancel = ! openDialog;

				// 出力先が設定されている場合
			} else {

				// パスからコンテナを取得
				container = PluginHelper.getFolder(location);

				// パスがワークスペース外または存在しない場合
				if (container == null) {
					openDialog = MessageDialog.openQuestion(parent, Messages.InspectHandler_17,
							Messages.InspectHandler_23 +
							Messages.InspectHandler_19 + "\n\n" + //$NON-NLS-1$
							Messages.InspectHandler_26 + location + "\n" + //$NON-NLS-1$
					Messages.InspectHandler_21);
					cancel = ! openDialog;
					container = null;
				}

				// プロジェクトが閉じられている場合
				else if (container != null && container.getProject().isOpen() == false) {
					openDialog = MessageDialog.openQuestion(parent, Messages.InspectHandler_17,
							Messages.InspectHandler_30 +
							Messages.InspectHandler_19 + "\n\n" + //$NON-NLS-1$
							Messages.InspectHandler_26 + location + "\n" + //$NON-NLS-1$
					Messages.InspectHandler_21);
					cancel = ! openDialog;
					container = null;
				}
			}

		} else {
			// 出力時に選択する場合
			openDialog = true;
		}

		// フォルダ選択ダイアログの表示
		if (openDialog) {

			// フォルダ選択ダイアログ
			ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
					parent, new WorkbenchLabelProvider(), new WorkbenchContentProvider());
			dialog.setTitle(Messages.InspectHandler_36);
			dialog.setMessage(Messages.InspectHandler_37);

			// フィルターの設定
			dialog.addFilter(new ViewerFilter() {
				public boolean select(Viewer viewer, Object parentElement, Object element) {
					// 閉じたプロジェクトは表示しない、ファイルも表示しない
					if (element instanceof IContainer) {
						if (element instanceof IProject) {
							IProject project = (IProject)element;
							return project.isOpen();
						}
						return true;
					}
					return false;
				}
			});

			// 複数選択不可
			dialog.setAllowMultiple(false);
			// ヘルプ非表示
			dialog.setHelpAvailable(false);
			// ワークスペースのルートの配下を表示
			dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());

			// ダイアログを開く
			if (dialog.open() == Dialog.OK) {
				container = (IContainer)dialog.getFirstResult();
				cancel = false;
			} else {
				cancel = true;
			}
		}

		// キャンセルメッセージの表示
		if (cancel) {
			MessageDialog.openInformation(parent, Messages.InspectHandler_0, Messages.InspectHandler_39);
			return null;
		}

		return container;
	}
}
