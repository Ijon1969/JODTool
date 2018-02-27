package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.handler;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import jp.ac.kyushu_u.csce.modeltool.base.utility.FileAccessException;
import jp.ac.kyushu_u.csce.modeltool.base.utility.FileAccessor;
import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;
import jp.ac.kyushu_u.csce.modeltool.dictionary.Messages;
import jp.ac.kyushu_u.csce.modeltool.dictionary.ModelToolDictionaryPlugin;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryPreferenceConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryUtil;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryView;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.Model;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.ModelElement;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.ModelError;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.ModelManager;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.FileEditorInput;

/**
 * モデル出力ハンドラクラス
 *
 * @author KBK yoshimura
 */
public class OutputHandler extends AbstractHandler implements IHandler {

	/**
	 * execute
	 * @see org.eclipse.core.commands.IHandler#execute(ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchPage page = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();

		// 辞書ビューの取得（開いていない場合処理なし）
		DictionaryView view = (DictionaryView)page.findView(DictionaryConstants.PART_ID_DICTIONARY);
		if (view == null) {
			return null;
		}

		// アクティブなタブの取得（アクティブなタブがない場合は処理なし）
		TableTab tab = view.getActiveTableTab(false);
		if (tab == null) {
			return null;
		}

		// シェルの取得
		Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();

		// 出力モードでない場合、処理なし
		if (DictionaryUtil.isOutputMode() == false) {
			MessageDialog.openWarning(shell, Messages.OutputHandler_0, Messages.OutputHandler_1);
		}

		// 出力先フォルダの取得
		IContainer container = getOutputContainer(shell);
		if (container == null) {
			return null;
		}

		// 辞書→モデル変換
		ModelManager manager = ModelManager.getInstance();
		Model model = manager.getModel(tab.getDictionary());
		List<ModelElement> output = new ArrayList<ModelElement>();  // 変換結果リスト
		List<ModelError> modelErrors = new ArrayList<ModelError>(); // 変換エラーリスト
		model.convert(tab.getDictionary(), output, modelErrors);

		// エディターID
		String editorId = manager.getEditorIdByModelKey(manager.getModelKey(tab.getDictionary()));

		// プリファレンスストア
		IPreferenceStore store = ModelToolDictionaryPlugin.getDefault().getPreferenceStore();

		// 出力ファイルリスト
		List<String> outputFiles = new ArrayList<String>();
		// ファイル例外リスト
		List<String> fileErrors = new ArrayList<String>();

		for (ModelElement element : output) {
			// 出力ファイルのパスを取得
			IPath path = container.getFullPath().append(element.getName()).addFileExtension(model.getExtension());

			try {
				// ファイル出力
				IFile file = FileAccessor.writeFile(container, path.lastSegment(), element.getElement().toString());
				// 結果リストに追加
				outputFiles.add(path.toString());

				if (store.getBoolean(DictionaryPreferenceConstants.PK_OPEN_OUTPUT_MODEL_FILE)) {
					// エディターIDが指定されている場合
					if (editorId != null) {
						try {
							// ファイルをエディターで開く
							page.openEditor(new FileEditorInput(file), editorId);
						} catch (PartInitException e) {
						}
					}
				}

			} catch (FileAccessException e) {
				// エラーログ
				IStatus status = new Status(IStatus.ERROR, ModelToolDictionaryPlugin.PLUGIN_ID,
						MessageFormat.format(Messages.OutputHandler_2, path.toString()), e);
				ModelToolDictionaryPlugin.getDefault().getLog().log(status);
				// ファイル例外リストに追加
				fileErrors.add(path.toString());
			}
		}

		// 結果の表示
		openResultDialog(shell, tab, outputFiles, fileErrors, modelErrors);

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

		IPreferenceStore store = ModelToolDictionaryPlugin.getDefault().getPreferenceStore();

		// 既定フォルダに出力する場合
		if (DictionaryPreferenceConstants.PV_OUTPUT_FIXED ==
				store.getInt(DictionaryPreferenceConstants.PK_OUTPUT_FOLDER_SETTING)) {

			// 出力先フォルダパスをプリファレンスストアから取得
			String location = store.getString(DictionaryPreferenceConstants.PK_OUTPUT_FIXED_PATH);

			// 出力先が未設定の場合
			if (PluginHelper.isEmpty(location)) {
				openDialog = MessageDialog.openQuestion(parent, Messages.OutputHandler_4,
						Messages.OutputHandler_5 +
						Messages.OutputHandler_6 + "\n" + //$NON-NLS-1$
						Messages.OutputHandler_8);
				cancel = ! openDialog;

			// 出力先が設定されている場合
			} else {

				// パスからコンテナを取得
				container = PluginHelper.getFolder(location);

				// パスがワークスペース外または存在しない場合
				if (container == null) {
					openDialog = MessageDialog.openQuestion(parent, Messages.OutputHandler_4,
							Messages.OutputHandler_10 +
							Messages.OutputHandler_11 + "\n\n" + //$NON-NLS-1$
							Messages.OutputHandler_13 + location + "\n" + //$NON-NLS-1$
							Messages.OutputHandler_8);
					cancel = ! openDialog;
					container = null;
				}

				// プロジェクトが閉じられている場合
				else if (container != null && container.getProject().isOpen() == false) {
					openDialog = MessageDialog.openQuestion(parent, Messages.OutputHandler_4,
							Messages.OutputHandler_17 +
							Messages.OutputHandler_11 + "\n\n" + //$NON-NLS-1$
							Messages.OutputHandler_13 + location + "\n" + //$NON-NLS-1$
							Messages.OutputHandler_8);
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
			dialog.setTitle(Messages.OutputHandler_23);
			dialog.setMessage(Messages.OutputHandler_24);

			// フィルターの設定
			dialog.addFilter(new ViewerFilter() {
				public boolean select(Viewer viewer, Object parentElement, Object element) {
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
//			MessageDialog.openInformation(parent, Messages.OutputHandler_25, Messages.OutputHandler_26);
			return null;
		}

		return container;
	}

	/**
	 * 出力結果ダイアログを開く
	 * @param shell
	 * @param tab
	 * @param result
	 * @param exceptions
	 * @param errors
	 */
	private void openResultDialog(Shell shell, final TableTab tab, final List<String> result,
			final List<String> exceptions, final List<ModelError> errors) {

		Dialog dialog = new Dialog(shell) {

			/**
			 * ダイアログエリアの作成
			 */
			protected Control createDialogArea(Composite parent) {

				Composite composite = (Composite)super.createDialogArea(parent);

				createTitleArea(composite);

				// モデル出力結果
				createResultArea(composite);

				// モデル出力エラー
				if (errors.isEmpty() == false) {
					createErrorArea(composite);
				}

				// ファイル出力エラー
				if (exceptions.isEmpty() == false) {
					createExceptionArea(composite);
				}

				return composite;
			}

			/**
			 * タイトルエリアの作成
			 * @param parent
			 */
			protected void createTitleArea(Composite parent) {

				Composite composite = new Composite(parent, SWT.NONE);
				GridLayout layout = new GridLayout(1, false);
				layout.verticalSpacing = 0;
				layout.horizontalSpacing = 0;
				composite.setLayout(layout);
				composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

				Label label = new Label(composite, SWT.NONE);
				if (tab.getFile() == null) {
					label.setText(Messages.OutputHandler_27 + tab.getDictionaryName());
				} else {
					label.setText(Messages.OutputHandler_27 + tab.getFile().getFullPath());
				}
				label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
			}

			/**
			 * 結果エリアの作成
			 * @param parent
			 * @return
			 */
			protected Text createResultArea(Composite parent) {

				Composite composite = new Composite(parent, SWT.NONE);
				GridLayout layout = new GridLayout(1, false);
				layout.verticalSpacing = 0;
				layout.horizontalSpacing = 0;
				composite.setLayout(layout);
				composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

				Label label = new Label(composite, SWT.NONE);
				label.setText(Messages.OutputHandler_29);
				label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));

				Text text = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.READ_ONLY);// | SWT.V_SCROLL | SWT.H_SCROLL);
				text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
				text.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

				StringBuffer sb = new StringBuffer();
				for (String file : result) {
					sb.append(file).append("\n"); //$NON-NLS-1$
				}
				if (sb.toString().isEmpty()) {
					sb.append(Messages.OutputHandler_31);
					if (exceptions.isEmpty()) {
						sb.append("\n").append(Messages.OutputHandler_32); //$NON-NLS-1$
					}
				}
				text.setText(sb.toString());

				return text;
			}

			/**
			 * 例外エリアの作成
			 * @param parent
			 * @return
			 */
			protected Text createExceptionArea(Composite parent) {

				Composite composite = new Composite(parent, SWT.NONE);
				GridLayout layout = new GridLayout(2, false);
				layout.verticalSpacing = 0;
				composite.setLayout(layout);
				composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

				Label image = new Label(composite, SWT.NONE);
				image.setImage(ModelToolDictionaryPlugin.imageDescriptorFromPlugin(ModelToolDictionaryPlugin.PLUGIN_ID, DictionaryConstants.IMG_ERROR).createImage());
				image.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

				Label label = new Label(composite, SWT.NONE);
				label.setText(Messages.OutputHandler_33);
				label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

				Text text = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.READ_ONLY);// | SWT.V_SCROLL | SWT.H_SCROLL);
				text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
				text.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

				StringBuffer sb = new StringBuffer();
				for (String file : exceptions) {
					sb.append(file).append("\n"); //$NON-NLS-1$
				}
				text.setText(sb.toString());

				return text;
			}

			/**
			 * エラーエリアの作成
			 * @param parent
			 * @return
			 */
			protected Text createErrorArea(Composite parent) {

//				Composite composite = new Composite(parent, SWT.NONE);
//				GridLayout layout = new GridLayout(2, false);
//				layout.verticalSpacing = 0;
//				composite.setLayout(layout);
//				composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
//
//				Label image = new Label(composite, SWT.NONE);
//				image.setImage(ModelToolDictionaryPlugin.imageDescriptorFromPlugin(ModelToolDictionaryPlugin.PLUGIN_ID, DictionaryConstants.IMG_ERROR).createImage());
//				image.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
//
//				Label label = new Label(composite, SWT.NONE);
//				label.setText(Messages.OutputHandler_35);
//				label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
//
//				Text text = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.READ_ONLY);// | SWT.V_SCROLL | SWT.H_SCROLL);
//				text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
//				text.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
//
//				StringBuffer sb = new StringBuffer();
//				for (ModelError error : errors) {
//					if (error.getErrorType() == ModelError.TYPE_ERROR) {
//						sb.append(error.getErrorMessage()).append("\n"); //$NON-NLS-1$
//					}
//				}
//				text.setText(sb.toString());
//
//				return text;

				Composite composite = new Composite(parent, SWT.NONE);
				GridLayout layout = new GridLayout(1, false);
				layout.verticalSpacing = 0;
				composite.setLayout(layout);
				composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

				Label label = new Label(composite, SWT.NONE);
				label.setText(Messages.OutputHandler_38);
				label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

				TableViewer viewer = new TableViewer(composite,
						SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
				final Table table = viewer.getTable();
				GridData data = new GridData(GridData.GRAB_HORIZONTAL
						| GridData.HORIZONTAL_ALIGN_FILL
						| GridData.GRAB_VERTICAL
						| GridData.VERTICAL_ALIGN_FILL);
				data.horizontalSpan = 1;
				table.setLayoutData(data);
				table.setHeaderVisible(true);
				table.setLinesVisible(true);
				String[] columnNames = new String[]{"", Messages.OutputHandler_39, Messages.OutputHandler_40, Messages.OutputHandler_41}; //$NON-NLS-1$
				int[] columnWidths = new int[]{35, 35, 100, 250};
				int[] columnAligns = new int[]{SWT.CENTER, SWT.RIGHT, SWT.LEFT, SWT.LEFT};
				for (int i=0; i<columnNames.length; i++) {
					TableColumn tableColumn = new TableColumn(table, columnAligns[i]);
					tableColumn.setText(columnNames[i]);
					tableColumn.setWidth(columnWidths[i]);
				}

				viewer.setLabelProvider(new ErrorTableLabelProvider());
				viewer.setContentProvider(new ArrayContentProvider());
				viewer.setInput(errors.toArray());

				return null;
			}

			/**
			 * サイズ変更可否設定
			 */
			protected boolean isResizable() {
				return true;
			}

			/**
			 * ボタンバーの作成
			 */
			protected void createButtonsForButtonBar(Composite parent) {
				createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
						true);
			}

			/**
			 * シェルの設定
			 */
			protected void configureShell(Shell newShell) {
				super.configureShell(newShell);
				newShell.setText(Messages.OutputHandler_37);
			}
		};

		dialog.open();
	}

	/**
	 * 出力エラー表示テーブル用プロバイダー
	 */
	private class ErrorTableLabelProvider
			extends LabelProvider
			implements ITableLabelProvider {
		public Image getColumnImage(Object element, int columnIndex) {
			if (columnIndex == 0) {
				ModelError error = (ModelError) element;
				switch (error.getErrorType()) {
					case ModelError.TYPE_ERROR:
						return ModelToolDictionaryPlugin.imageDescriptorFromPlugin(
								ModelToolDictionaryPlugin.PLUGIN_ID, DictionaryConstants.IMG_ERROR).createImage();
					case ModelError.TYPE_WARNING:
						return ModelToolDictionaryPlugin.imageDescriptorFromPlugin(
								ModelToolDictionaryPlugin.PLUGIN_ID, DictionaryConstants.IMG_WARNING).createImage();
				}
			}
			return null;
		}
		public String getColumnText(Object element, int columnIndex) {
			ModelError error = (ModelError) element;
			switch (columnIndex) {
				case 0:
					return null;
				case 1:
					return error.getNo() == null? null : String.valueOf(error.getNo());
				case 2:
					return error.getWord();
				case 3:
					return error.getError();
			}
			return null;
		}
	}
}
