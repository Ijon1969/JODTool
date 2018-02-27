package jp.ac.kyushu_u.csce.modeltool.explorer.explorer;

import java.text.MessageFormat;
import java.util.Map;

import jp.ac.kyushu_u.csce.modeltool.base.ModelToolBasePlugin;
import jp.ac.kyushu_u.csce.modeltool.base.constant.ToolConstants;
import jp.ac.kyushu_u.csce.modeltool.base.utility.AbstractViewPart;
import jp.ac.kyushu_u.csce.modeltool.base.utility.FileAccessException;
import jp.ac.kyushu_u.csce.modeltool.base.utility.FileAccessor;
import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;
import jp.ac.kyushu_u.csce.modeltool.explorer.Messages;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.FileEditorInput;

/**
 * 要求定義書エクスプローラービュー<br>
 *
 * @author KBK yoshimura
 */
//public class ExplorerView {
public class ExplorerView extends AbstractViewPart {

	/** ツリービューアー */
	private TreeViewer viewer;

	/** ツリーのルート・リソース */
	private IResource root;

	/** リソース変化リスナー */
	private IResourceChangeListener rcListener;

	/** アクティブなコンテキスト */
	private IContextActivation contextActivation = null;

	/** 拡張ポイントマネージャ */
	private ExplorerExtensionManager extManager;

	/**
	 * コントロールの生成
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(Composite)
	 */
	public void createPartControl(Composite parent) {

		// ツリービューア生成
		viewer = new TreeViewer(parent, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);

		// プロバイダ設定
		viewer.setContentProvider(new FileTreeContentProvider());
		viewer.setLabelProvider(new FileTreeLabelProvider(
				new WorkbenchLabelProvider(), ModelToolBasePlugin.getDefault().getWorkbench()
						.getDecoratorManager().getLabelDecorator()));

		// 拡張ポイントマネージャ
		extManager = new ExplorerExtensionManager();

		// ツリーのルートにワークスペースのルートフォルダを設定
		root = ResourcesPlugin.getWorkspace().getRoot();
		viewer.setInput(root);

		// フィルターの設定
		viewer.addFilter(new FileExtensionFilter());

		// ソーターの設定
		viewer.setSorter(new ResourceSorter());

		// ダブルクリックリスナーの追加
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			// ダブルクリック時の処理
			public void doubleClick(DoubleClickEvent event) {
				IResource resource = (IResource)((IStructuredSelection)event.getSelection()).getFirstElement();
				// フォルダ
				if (resource instanceof IContainer) {
					// 展開／折りたたみの切り替え
					viewer.setExpandedState(resource, ! viewer.getExpandedState(resource));
				// ファイル
				} else if (resource instanceof IFile) {
					// ファイルを開く
					open((IFile)resource);
				}
			}
		});

		// リソース変化リスナーの登録
		rcListener = new ResourceChangeListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(rcListener, IResourceChangeEvent.POST_CHANGE);

		// コンテキストメニューの設定
		MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {

				IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
				IResource resource = (IResource)selection.getFirstElement();

				// 選択されたリソースがファイルの場合
				if (resource instanceof IFile) {
					IFile file = (IFile)resource;

					// 拡張ポイントで定義されている拡張子の場合
					if (PluginHelper.in(file.getFileExtension(), false,
							extManager.getExtensionList().toArray(new String[0]))) {
						manager.add(new FileOpenAction());
					}
					// 上記以外の既定の拡張子
					else if (PluginHelper.in(file.getFileExtension(), false,
									ToolConstants.SPEC_EXTENSIONS,
									ToolConstants.EXCEL_EXTENSIONS,
									ToolConstants.DICTIONARY_EXTENSIONS,
									ToolConstants.MODEL_EXTENSIONS)) {
						manager.add(new FileOpenAction());
					}
//					if (PluginHelper.in(file.getFileExtension(), false, ToolConstants.DICTIONARY_EXTENSIONS)) {
//						manager.add(new FileRenameAction());
//					}
					manager.add(new FileDeleteAction());
				}
				// 選択されたリソースがフォルダの場合
				if (resource instanceof IContainer) {
					manager.add(new FileCreateAction());
				}
				manager.add(new RefreshAction());
				manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			}
		});
		Menu menu = manager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(ToolConstants.PART_ID_EXPLORER + ".popup", manager, viewer);

		// SelectionProviderの設定
		getSite().setSelectionProvider(viewer);

		// 自身をパートリスナーとして登録
		getSite().getWorkbenchWindow().getPartService().addPartListener(this);
	}

	/**
	 * dispose処理
	 */
	public void dispose() {

		// リスナーの削除
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(rcListener);
		getSite().getWorkbenchWindow().getPartService().removePartListener(this);

		super.dispose();
	}

	/**
	 * ビューがアクティブになった際の処理
	 * @see jp.ac.kyushu_u.csce.modeltool.base.utility.AbstractViewPart#partActivated(IWorkbenchPartReference)
	 */
	public void partActivated(IWorkbenchPartReference partRef) {
		IContextService contextService = (IContextService)getSite().getService(IContextService.class);
		contextActivation = contextService.activateContext(ToolConstants.KB_SCOPE_ID_EXPLORER);
	}

	/**
	 * ビューがアクティブでなくなった際の処理
	 * @see jp.ac.kyushu_u.csce.modeltool.base.utility.AbstractViewPart#partDeactivated(IWorkbenchPartReference)
	 */
	public void partDeactivated(IWorkbenchPartReference partRef) {
		IContextService contextService = (IContextService)getSite().getService(IContextService.class);
		contextService.deactivateContext(contextActivation);
	}

	/**
	 * ファイルを開く
	 * @param file ファイル
	 */
	private void open(IFile file) {

		String extension = file.getFileExtension();

		// 拡張ポイントで定義されている拡張子とエディターを取得
		Map<String, IOpener> openMap = extManager.getOpenerMap();
		IOpener opener = openMap.get(extension);
		Map<String, String> extMap = extManager.getEditorMap();
		String editorId = extMap.get(extension);
		if (opener != null) {
			// 拡張ポイントが定義されている場合、指定のクラスで開く
			opener.open(getSite().getPage(), file);
		} else if (editorId != null) {
			// 拡張ポイントが定義されている場合、指定のエディタで開く
			open(file, editorId);

		} else {
			// 開いているかチェック
			boolean opened = false;
			IEditorPart editor = getViewSite().getPage().findEditor(new FileEditorInput(file));
			if (editor != null) {
				getViewSite().getPage().activate(editor);
				opened = true;
			} else {
				// ファイルを開く
				try {
					IDE.openEditor(getViewSite().getPage(), file);
				} catch (PartInitException e) {
					MessageDialog.openError(
							getSite().getShell(), Messages.ExplorerView_2, Messages.ExplorerView_3);
					IStatus status = new Status(
							IStatus.ERROR, ModelToolBasePlugin.PLUGIN_ID,
							"failed to open file [path:" + file.getLocation().toOSString() + "]", //$NON-NLS-1$ //$NON-NLS-2$
							e);
					ModelToolBasePlugin.getDefault().getLog().log(status);
					return;
				}
			}

			if (!opened) {
				// 仕様書の場合、警告表示
				if (PluginHelper.in(extension, false,
						new String[]{ToolConstants.EXTENSION_TXT}, ToolConstants.EXCEL_EXTENSIONS)) {
					MessageDialog.openWarning(getSite().getShell(), Messages.ExplorerView_6, Messages.ExplorerView_7);
//					openWithSpecEditor(file);
				}
				// 辞書の場合、警告表示
				else if (PluginHelper.in(extension, false, ToolConstants.DICTIONARY_EXTENSIONS)) {
					MessageDialog.openWarning(getSite().getShell(), Messages.ExplorerView_6, Messages.ExplorerView_9);
//					openWithDictionaryView(file);
				}
			}
		}
	}

	/**
	 * エディターでファイルを開く<br>
	 * @param file ファイル
	 * @param editorId エディターID
	 * @return エディター
	 */
	private IEditorPart open(IFile file, String editorId) {
		try {
			return getViewSite().getPage().openEditor(new FileEditorInput(file), editorId);
		} catch (Exception e) {
			MessageDialog.openError(
					getSite().getShell(), Messages.ExplorerView_2, Messages.ExplorerView_3);
			IStatus status = new Status(
					IStatus.ERROR, ModelToolBasePlugin.PLUGIN_ID,
					"failed to open file [path:" + file.getLocation().toOSString() + "]", //$NON-NLS-1$ //$NON-NLS-2$
					e);
			ModelToolBasePlugin.getDefault().getLog().log(status);
			return null;
		}
	}

//	/**
//	 * 要求仕様書エディターでファイルを開く
//	 * @param file 仕様書ファイル
//	 * @return エディター
//	 */
//	private IEditorPart openWithSpecEditor(IFile file) {
////		return open(file, ToolConstants.PART_ID_SPECEDITOR);
//		return null;
//	}
//
//	/**
//	 * 辞書ビューでファイルを開く
//	 * @param file 辞書ファイル
//	 */
//	private void openWithDictionaryView(IFile file) {
//
////		try {
////			DictionaryView view = (DictionaryView)getSite().getPage().findView(ToolConstants.PART_ID_DICTIONARY);
////			if (view != null) {
////				getSite().getPage().showView(ToolConstants.PART_ID_DICTIONARY);
////				view.loadDictionary(file);
////			} else {
////				view = (DictionaryView)getSite().getPage().showView(ToolConstants.PART_ID_DICTIONARY);
////				view.loadDictionaryToActiveTab(file);
////			}
////		} catch (PartInitException e) {
////			MessageDialog.openError(
////					getSite().getShell(), "辞書ファイルの読込", "辞書ビューを開くことができませんでした。");
////			IStatus status = new Status(
////					IStatus.ERROR, ModelToolBasePlugin.PLUGIN_ID,
////					"failed to open file [path:" + file.getLocation().toOSString() + "]",
////					e);
////			ModelToolBasePlugin.getDefault().getLog().log(status);
////			return;
////		}
//	}

	/**
	 * リフレッシュ
	 * @param resource リソース（リフレッシュするノード）
	 */
	public void refresh(IResource resource) {
		try {
			// リソースを指定しない場合は、ビュー全体をリフレッシュする
			if (resource == null) {
				root.refreshLocal(IResource.DEPTH_INFINITE, null);
				viewer.refresh(root);
			} else {
				resource.refreshLocal(IResource.DEPTH_INFINITE, null);
				viewer.refresh(resource);
			}
		} catch (CoreException e) {
			MessageDialog.openError(getSite().getShell(), Messages.ExplorerView_14, Messages.ExplorerView_15);
			IStatus status = new Status(
					IStatus.ERROR, ModelToolBasePlugin.PLUGIN_ID,
					"failed to refresh explorer view", //$NON-NLS-1$
					e);
			ModelToolBasePlugin.getDefault().getLog().log(status);
		}
	}

	/**
	 * リソース変化リスナー<br>
	 * リソースに変化のあった場合に呼び出されるリスナーです。
	 * @author KBK yoshimura
	 */
	public class ResourceChangeListener implements IResourceChangeListener {

		public void resourceChanged(IResourceChangeEvent event) {
			IResourceDelta delta = event.getDelta();
			if (delta == null) {
				return;
			}
			IResourceDelta[] projDeltas = delta.getAffectedChildren();
			if (projDeltas.length > 0 && getSite().getShell() != null) {
				getSite().getShell().getDisplay().syncExec(
						new Runnable() {
							public void run() {
								viewer.refresh();
							}
						});
				return;
			}
		}
	}

	/**
	 * ファイルの新規作成処理クラス
	 * @author KBK yoshimura
	 */
	public class FileCreateAction extends Action {

		/** デフォルトファイル名 */
		private static final String DEFAULT_FILE_NAME = "Default.txt"; //$NON-NLS-1$

		/** コンストラクタ */
		public FileCreateAction() {
			super(Messages.ExplorerView_18);
		}

		/** 新規作成 */
		public void run() {
			IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
			IResource resource = (IResource)selection.getFirstElement();

			if (resource instanceof IContainer) {
				IFile file = null;
				try {
					file = FileAccessor.createFile((IContainer)resource, DEFAULT_FILE_NAME);
				} catch (FileAccessException e) {
					// メッセージダイアログ出力
					MessageDialog.openInformation(getSite().getShell(), Messages.ExplorerView_19,
							Messages.ExplorerView_20);
					// エラーログ出力
					IStatus status = new Status(
							IStatus.ERROR, ModelToolBasePlugin.PLUGIN_ID,
							"failed to create new file [" //$NON-NLS-1$
								+ PluginHelper.getFile((IContainer)resource,DEFAULT_FILE_NAME).getLocation().toOSString()
								+ "]", e); //$NON-NLS-1$
					ModelToolBasePlugin.getDefault().getLog().log(status);
				}
				if (file != null) {
					refresh(resource);
					open(file);
				}
			}
		}
	}

	/**
	 * ファイルの削除処理クラス
	 */
	public class FileDeleteAction extends Action {

		/** コンストラクタ */
		public FileDeleteAction() {
			super(Messages.ExplorerView_23);
		}

		/** ファイル削除 */
		public void run() {
			IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
			IResource resource = (IResource)selection.getFirstElement();
			final IFile file = (IFile)resource;

			if (resource instanceof IFile) {

				// 確認メッセージ
				boolean delete = MessageDialog.openConfirm(
							getSite().getShell(), Messages.ExplorerView_24,
							MessageFormat.format(Messages.ExplorerView_25, resource.getName()));

				if (delete) {
					getSite().getShell().getDisplay().asyncExec(new Runnable() {
						public void run() {
							IContainer container = file.getParent();
							try {
								// リソース削除
								file.delete(true, null);
								refresh(container);
							} catch (CoreException e) {
								MessageDialog.openError(
										getSite().getShell(), Messages.ExplorerView_24, Messages.ExplorerView_28);
								IStatus status = new Status(
										IStatus.ERROR, ModelToolBasePlugin.PLUGIN_ID,
										"failed to delete file [path:" + file.getLocation().toOSString() + "]", //$NON-NLS-1$ //$NON-NLS-2$
										e);
								ModelToolBasePlugin.getDefault().getLog().log(status);
							}
						}
					});
				}
			} else {
				MessageDialog.openInformation(getSite().getShell(), Messages.ExplorerView_24, Messages.ExplorerView_32);
			}
		}
	}

	/**
	 * ビューのリフレッシュ処理クラス
	 */
	public class RefreshAction extends Action {

		/** コンストラクタ */
		public RefreshAction() {
			super(Messages.ExplorerView_33);
//			setActionDefinitionId(ToolConstants.COMMAND_ID_REFRESH);
		}

		/** リフレッシュ */
		public void run() {
			// 選択されたリソースの親フォルダ配下をリフレッシュ
			IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
			IResource resource = (IResource)selection.getFirstElement();
			if (resource == null) {
				refresh(null);
			} else {
				refresh(resource.getParent());
			}
		}
	}

	/**
	 * ファイルの読込処理クラス
	 */
	public class FileOpenAction extends Action {

		/** コンストラクタ */
		public FileOpenAction() {
			super(Messages.ExplorerView_34);
		}

		/** 開く */
		public void run() {
			IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
			IResource resource = (IResource)selection.getFirstElement();
			if (resource instanceof IFile) {
				open((IFile)resource);
			}
		}
	}

	/**
	 * ツリービューワ―の取得
	 * @return TreeViwer
	 */
	public TreeViewer getTreeViewer() {
		return viewer;
	}

//	/**
//	 * ファイル名変更処理クラス
//	 */
//	public class FileRenameAction extends Action {
//
//		/** コンストラクタ */
//		public FileRenameAction() {
//			super("名前の変更(&N)");
//			setActionDefinitionId(ToolConstants.COMMAND_ID_EXPLORER_RENAME);
//		}
//
////		/** 名前の変更 */
////		public void run() {
////			IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
////			IResource resource = (IResource)selection.getFirstElement();
////			if (resource instanceof IFile) {
////				IFile file = (IFile)resource;
////				DictionaryRename.rename(null, file);
////			}
////		}
//	}
}
