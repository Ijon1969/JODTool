package jp.ac.kyushu_u.csce.modeltool.spec.speceditor.handler;

import java.io.File;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import jp.ac.kyushu_u.csce.modeltool.base.dialog.MessageLinkDialog;
import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;
import jp.ac.kyushu_u.csce.modeltool.dictionary.ModelToolDictionaryPlugin;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryPreferenceConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryView;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;
import jp.ac.kyushu_u.csce.modeltool.spec.Messages;
import jp.ac.kyushu_u.csce.modeltool.spec.ModelToolSpecPlugin;
import jp.ac.kyushu_u.csce.modeltool.spec.constant.SpecConstants;
import jp.ac.kyushu_u.csce.modeltool.spec.constant.SpecPreferenceConstants;
import jp.ac.kyushu_u.csce.modeltool.spec.dialog.DictionarySelectionDialog;
import jp.ac.kyushu_u.csce.modeltool.spec.speceditor.SpecEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * 仕様書エディター<br>
 * キーワード抽出処理のハンドラークラス
 * @author KBK yoshimura
 */
public class PickoutHandler extends AbstractHandler {

	/**
	 * execute処理<br>
	 * 仕様書エディターで選択したキーワードを、辞書ビューへ登録する
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// アクティブページの取得
		IWorkbenchPage page = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();

		// アクティブエディターの取得
		IEditorPart editor = (IEditorPart)page.getActiveEditor();
		if (editor instanceof SpecEditor == false) {
			return null;		// 仕様書エディター以外のエディターでは処理を行わない
		}

		// 選択した語句の取得
		ITextSelection selection =
			(ITextSelection)editor.getEditorSite().getSelectionProvider().getSelection();
		String keyword = selection.getText();
		if (PluginHelper.isEmpty(keyword)) {
			MessageDialog.openWarning(editor.getSite().getShell(), Messages.PickoutHandler_0, Messages.PickoutHandler_1);
			return null;
		} else if (keyword.indexOf('\n') >= 0 || keyword.indexOf('\r') >= 0) {
			MessageDialog.openWarning(editor.getSite().getShell(), Messages.PickoutHandler_0, Messages.PickoutHandler_3);
			return null;
		}

		// 辞書ビューの取得
		DictionaryView view = (DictionaryView)page.findView(DictionaryConstants.PART_ID_DICTIONARY);
		boolean isOpened = (view != null);
		if (!isOpened) {
			try {
				view = (DictionaryView)page.showView(DictionaryConstants.PART_ID_DICTIONARY);
			} catch (PartInitException e) {
				// エラーログ出力
				IStatus status = new Status(
						IStatus.ERROR, ModelToolSpecPlugin.PLUGIN_ID,
						"failed to init part", //$NON-NLS-1$
						e);
				ModelToolSpecPlugin.getDefault().getLog().log(status);
				return null;
			}
		} else {
			page.activate(view);
		}

		// 登録対象タブ
		TableTab tab = null;

		// プリファレンスストアの取得
		IPreferenceStore store = ModelToolSpecPlugin.getDefault().getPreferenceStore();
		int registerDictionary = store.getInt(SpecPreferenceConstants.PK_REGISTER_DICTIONARY);

		switch (registerDictionary) {

			// 既定の辞書を使用する場合
			case SpecPreferenceConstants.PV_REGISTER_FIXED:

				// プリファレンスストアから、既定ファイルのパスを取得し
				String dictionaryPath = store.getString(SpecPreferenceConstants.PK_REGISTER_FIXED_PATH);
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
				IFile file = root.getFile(new Path(dictionaryPath));

				// ファイルを開いているタブを取得
				tab = view.getTabOfFile(file);

				// 開いているタブがない場合
				if (tab == null) {
					File localFile = file.getLocation().toFile();
					if (localFile.exists() == false || file == null) {
						Map<String, String> linkMap = new LinkedHashMap<String, String>();
						linkMap.put(Messages.PickoutHandler_5, SpecConstants.PREF_PAGE_ID_SPEC);
						MessageLinkDialog.openWarning(editor.getSite().getShell(), Messages.PickoutHandler_6,
								MessageFormat.format(Messages.PickoutHandler_7, dictionaryPath)
								+ Messages.PickoutHandler_9,
								linkMap);
						return null;
					}

					// ファイルを開く
					if (!isOpened) {
						tab = view.loadDictionaryToActiveTab(file);
					} else {
						tab = view.loadDictionary(file);
					}

					// 追加位置（フォーカス位置）の設定
					if (ModelToolDictionaryPlugin.getDefault().getPreferenceStore().getBoolean(
							DictionaryPreferenceConstants.PK_ENTRY_ADD_UNDER) &&
							tab.getDictionary().size() > 1) {
						// 最数行にフォーカス
						tab.setSelection(tab.getDictionary().get(tab.getDictionary().size() - 1));
					}

				// 既に開かれていた場合
				} else {
					tab.activate();
				}

				break;

			// 登録時に選択する場合
			case SpecPreferenceConstants.PV_REGISTER_SELECT:
				// タブが複数存在する場合
				if (view.getNumberOfTabs() > 1) {
					// 辞書選択ダイアログで選択する
					DictionarySelectionDialog dialog = new DictionarySelectionDialog(
							editor.getSite().getShell(), DictionarySelectionDialog.MODE_PICKOUT);
					dialog.setDictionaries(view.getTabs());
					if (dialog.open() != IDialogConstants.OK_ID) {
						return null;
					}
					tab = dialog.getResult().get(0);
					tab.activate();

				// タブ数＝0 or 1の場合
				} else {
					// 辞書ビューのアクティブなタブにキーワードを登録
					tab = view.getActiveTableTab(true);
				}
				break;

			// アクティブな辞書に登録する場合
			case SpecPreferenceConstants.PV_REGISTER_ACTIVE:
				tab = view.getActiveTableTab(true);
				break;

			// その他の場合
			default:
				// 処理なし
				return null;
		}

		if (tab == null) {
			return null;
		}
//		TableViewer viewer = tab.getTableViewer();

		// 選択されたテーブルの行を取得
//		IStructuredSelection sSelection = (IStructuredSelection)viewer.getSelection();
//		Entry selectEntry = (Entry)sSelection.getFirstElement();
		Entry selectEntry = tab.getSelection();

		Entry entry = new Entry(keyword);
		tab.addEntry(selectEntry, entry);
		tab.setSelection(entry);

		// 仕様書エディターのアクティブ化
		page.activate(editor);

		return null;
	}
}
