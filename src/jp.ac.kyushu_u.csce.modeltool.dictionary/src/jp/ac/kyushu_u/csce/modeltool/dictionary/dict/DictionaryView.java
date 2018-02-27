package jp.ac.kyushu_u.csce.modeltool.dictionary.dict;

import static jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants.*;
import static jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryPreferenceConstants.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.ac.kyushu_u.csce.modeltool.base.dialog.CustomMessageDialog;
import jp.ac.kyushu_u.csce.modeltool.base.utility.AbstractViewPart;
import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;
import jp.ac.kyushu_u.csce.modeltool.dictionary.Messages;
import jp.ac.kyushu_u.csce.modeltool.dictionary.ModelToolDictionaryPlugin;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dialog.DictionaryViewerFilter;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.ModelManager;
import jp.ac.kyushu_u.csce.modeltool.dictionary.utility.DictionaryRenameUtility;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * 辞書ビュー
 * @author KBK yoshimura
 */
public class DictionaryView extends AbstractViewPart {

	/** タブフォルダー */
	private CTabFolder folder;

	/** タブリスト */
	private List<TableTab> tabs;

	/** モード コンボボックス */
	private Combo cmbMode;
	/** モデル出力ボタン */
	private Button btnOutput;

	/** ファイルダイアログのフォルダ */
	private IContainer dialogFilterPath = null;

	/** アクティブなコンテキスト */
	private IContextActivation contextActivation = null;

	/** コピーレコード退避 */
	@Deprecated
	private Entry copyEntry;
	/** コピーレコード退避 */
	private List<Entry> copyEntries = new ArrayList<Entry>();
	/** コピーモード 1:コピー／2:切取り／0:データなし */
	private int copyMode;
	/** コピーモード定数：コピー */
	public static final int COPY_MODE_COPY	= 1;
	/** コピーモード定数：切取り */
	public static final int COPY_MODE_CUT	= 2;
	/** コピーモード定数：データなし */
	public static final int COPY_MODE_NODATA	= 0;
	/** コピー元辞書のモデルキー */
	private String srcModel;

	/**
	 * ハンドラー更新用
	 */
	private HandlerUpdater handlerUpdater = new HandlerUpdater();

	/**
	 * SWTコントロールの生成
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(Composite)
	 */
	public void createPartControl(Composite parent) {

		final IPreferenceStore store = ModelToolDictionaryPlugin.getDefault().getPreferenceStore();

		// 基底領域
		Composite base = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		base.setLayout(gridLayout);

		// 表示モードコンボボックス等を配置する領域
		Composite field1 = new Composite(base, SWT.NONE);
		gridLayout = new GridLayout(5, false);
//		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		field1.setLayout(gridLayout);
		field1.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));

		Label lblLang = new Label(field1, SWT.NONE);
		lblLang.setText(Messages.DictionaryView_4);

		cmbMode = new Combo(field1, SWT.READ_ONLY);
		cmbMode.setLayoutData(new GridData(100, SWT.DEFAULT));
		// コンボボックスにセット
		cmbMode.add(DISPLAY_MODE_INSPECT);
		cmbMode.add(DISPLAY_MODE_OUTPUT);
		cmbMode.setData(DISPLAY_MODE_INSPECT, PV_DIC_DISP_INSPECT);
		cmbMode.setData(DISPLAY_MODE_OUTPUT, PV_DIC_DISP_OUTPUT);
		int mode = store.getInt(PK_DICTIONARY_DISPLAY_MODE);
		for (int i=0; i<cmbMode.getItemCount(); i++) {
			if (mode == (Integer)cmbMode.getData(cmbMode.getItem(i))) {
				cmbMode.select(i);
				break;
			}
		}
		cmbMode.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int mode = (Integer)cmbMode.getData(cmbMode.getItem(cmbMode.getSelectionIndex()));
				// 既に変更済みの場合は何もしない
				if (mode != store.getInt(PK_DICTIONARY_DISPLAY_MODE)) {
					store.setValue(PK_DICTIONARY_DISPLAY_MODE, mode);
				}
			}
		});

		btnOutput = new Button(field1, SWT.PUSH);
		btnOutput.setText(Messages.DictionaryView_5);
		btnOutput.setEnabled(DictionaryUtil.isOutputMode());
		btnOutput.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ICommandService cmdService = (ICommandService)getSite().getService(ICommandService.class);
				Command cmd = cmdService.getCommand(COMMAND_ID_OUTPUT_MODEL);
				IHandlerService service = (IHandlerService)getSite().getService(IHandlerService.class);
				try {
					if (cmd.isHandled()) {
						cmd.executeWithChecks(new ExecutionEvent(null, Collections.EMPTY_MAP, null, service.getCurrentState()));
					}
				} catch (Exception e1) {
					IStatus status = new Status(IStatus.ERROR, ModelToolDictionaryPlugin.PLUGIN_ID,
							Messages.DictionaryView_7, e1);
					ModelToolDictionaryPlugin.getDefault().getLog().log(status);
				}
			}
		});

		// タブフォルダーを配置する領域
		Composite field2 = new Composite(base, SWT.NONE);
		field2.setLayout(new FillLayout());
		field2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		// タブフォルダー生成
		folder = new CTabFolder(field2, SWT.CLOSE);
		folder.setSimple(false);

		// タブ配色の設定（Eclipse標準のタブと同色）
		Display display = Display.getCurrent();
		Color titleForeColor =
			display.getSystemColor(SWT.COLOR_TITLE_FOREGROUND);
		Color titleBackColor1 =
			display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND);
		Color titleBackColor2 =
			display.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT);

		folder.setSelectionForeground(titleForeColor);
		folder.setSelectionBackground(
				new Color[] {titleBackColor1, titleBackColor2},
				new int[] {100},
				true
		);

		// コピーモード初期設定
		copyMode = COPY_MODE_NODATA;

		// タブリスト初期化
		tabs = new ArrayList<TableTab>();

		// 初期表示タブの設定
		initFirstTab();

		// ツールバーコマンドの状態設定
		setCommandState();

		// フィルターパスの初期化（ワークスペースのルートフォルダ）
		dialogFilterPath = ResourcesPlugin.getWorkspace().getRoot();

		// リスナーの設定
		//  タブフォルダイベントリスナー
		folder.addCTabFolder2Listener(new CTabFolder2Listener() {

			public void showList(CTabFolderEvent event) {}

			public void restore(CTabFolderEvent event) {}

			public void minimize(CTabFolderEvent event) {}

			public void maximize(CTabFolderEvent event) {}

			// タブを閉じる
			public void close(CTabFolderEvent event) {

				// close対象のタブ取得
				CTabItem item = (CTabItem)event.item;
				int index = folder.indexOf(item);
				TableTab tab = tabs.get(index);

				// 編集中で未保存の場合
				if (tab.isDirty()) {
					int status = CustomMessageDialog.openQuestionWithCancel(
						DictionaryView.this.getSite().getShell(),
						Messages.DictionaryView_0,
						tab.getDictionaryName() + Messages.DictionaryView_1);

					switch (status) {
						case IDialogConstants.YES_ID:
							if (saveDictionary(tab, false) != 0) {
								event.doit = false;
								return;
							}
							break;

						case IDialogConstants.NO_ID:
							// 保存しないで閉じる
							break;

						case IDialogConstants.CANCEL_ID:
							// 閉じずに終了
							event.doit = false;
							return;

						default:
							// 内部エラー
							break;
					}
				}

				tabs.get(index).dispose();
				tabs.remove(index);
				// TableTabのdispose()メソッドでCTabItemをdisposeするため、doit=falseとする
				event.doit = false;

				if (tabs.size() == 0) {
					// ハンドラーの更新
					updateHandler();
				}
			}
		});

		//  セレクションリスナー
		folder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// ハンドラーの更新
				updateHandler();
			}
		});

		//  ダブルクリックリスナー
		folder.addListener(SWT.MouseDoubleClick, new Listener() {
			public void handleEvent(Event event) {

				// 対象タブの取得
				TableTab tab = getTabOfItem(folder.getItem(new Point(event.x, event.y)));
				if (tab == null) {
					return;
				}
				// タブ名変更
				DictionaryRenameUtility.rename(tab, tab.getFile());
			}
		});

		// プリファレンスリスナー
		store.addPropertyChangeListener(new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				// 辞書モードの切替
				if (PK_DICTIONARY_DISPLAY_MODE.equals(event.getProperty())) {
					// ハンドラの制御
					new HandlerUpdater().update(getActiveTableTab(false));
					// トグル切り替え
					ModelToolDictionaryPlugin.getHandlerActivationManager().changeToggleState(
							COMMAND_ID_CHANGE_MODE, DictionaryUtil.isOutputMode());
					// コンボボックス制御
					if (!cmbMode.isDisposed()) {
						int mode = (Integer)event.getNewValue();
						for (int i=0; i<cmbMode.getItemCount(); i++) {
							if (mode == (Integer)cmbMode.getData(cmbMode.getItem(i))) {
								// 既に選択されている場合は何もしない
								if (i != cmbMode.getSelectionIndex()) {
									cmbMode.select(i);
								}
								break;
							}
						}
					}
					// ボタン制御
					btnOutput.setEnabled(DictionaryUtil.isOutputMode());
				}
			}
		});

		// 自身をリスナーとして登録
		getSite().getWorkbenchWindow().getPartService().addPartListener(this);
		getSite().getWorkbenchWindow().addPerspectiveListener(this);
		getSite().getWorkbenchWindow().getWorkbench().addWorkbenchListener(this);

		// コンテキストメニューの作成
		MenuManager manager = new MenuManager();
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(final IMenuManager menu) {}
		} );
		Menu menu = manager.createContextMenu(folder);
		folder.setMenu(menu);
		getSite().registerContextMenu(
				CTX_ID_DICTIONARY_TAB,
				manager,
				getSite().getSelectionProvider());
	}

	/**
	 * ツールバーコマンドの状態設定
	 */
	private void setCommandState() {

		IPreferenceStore store = ModelToolDictionaryPlugin.getDefault().getPreferenceStore();

		// 追加位置の設定
		boolean addUnder = store.getBoolean(PK_ENTRY_ADD_UNDER);
		ModelToolDictionaryPlugin.getHandlerActivationManager().changeToggleState(
				COMMAND_ID_ADD_POSITION, addUnder);

		// 検査／出力モード
		boolean mode = DictionaryUtil.isOutputMode();
		ModelToolDictionaryPlugin.getHandlerActivationManager().changeToggleState(
				COMMAND_ID_CHANGE_MODE, mode);
	}

	/**
	 * dispose
	 */
	public void dispose() {

		// リスナーの削除
		getSite().getWorkbenchWindow().getPartService().removePartListener(this);
		getSite().getWorkbenchWindow().removePerspectiveListener(this);
		getSite().getWorkbenchWindow().getWorkbench().removeWorkbenchListener(this);

		// 辞書編集ビューのタブを閉じる
		for (TableTab tab : tabs) {
			tab.closeEditView();
		}

		super.dispose();
	}

	/**
	 * 初期表示タブの設定<br>
	 * デフォルト名でローカルファイルの存在しないタブを作成する
	 */
	private void initFirstTab() {
//		// プリファレンスストアの取得
//		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
//		int registerDictionary = store.getInt(PreferenceConstants.PK_REGISTER_DICTIONARY);
//
//		// 既定の辞書を使用する場合
//		if (registerDictionary == PreferenceConstants.PV_REGISTER_FIXED) {
//			String dictionaryPath = store.getString(PreferenceConstants.PK_REGISTER_FIXED_PATH);
//			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
//
////			IFile file = root.getFileForLocation(new Path(dictionaryPath));
//			IFile file = root.getFile(new Path(dictionaryPath));
//			if (file == null || file.exists() == false) {
//				createNewTab(ToolConstants.DEFAULT_DIC_NAME);
////				MessageDialog.openWarning(getSite().getShell(), "辞書ビュー",
////						"既定の辞書 \"" + dictionaryPath + "\" が存在しません。");
//				return;
//			}
//
//			loadDictionary(file);
//
//		// その他の場合
//		} else {
//			createNewTab(ToolConstants.DEFAULT_DIC_NAME);
//		}

		// デフォルトの辞書名で新規タブ作成
		createNewTab(DEFAULT_DIC_NAME);
	}

	/**
	 * ビューがアクティブになった際の処理
	 * @see jp.ac.kyushu_u.csce.modeltool.utility.AbstractViewPart#partActivated(IWorkbenchPartReference)
	 */
	public void partActivated(IWorkbenchPartReference partRef) {
		if (PART_ID_DICTIONARY.equals(partRef.getId())) {
			activateContext(true);
		}
	}

	/**
	 * ビューがアクティブでなくなった際の処理
	 * @see jp.ac.kyushu_u.csce.modeltool.utility.AbstractViewPart#partDeactivated(IWorkbenchPartReference)
	 */
	public void partDeactivated(IWorkbenchPartReference partRef) {

		if (PART_ID_DICTIONARY.equals(partRef.getId())) {
			activateContext(false);
		} else {
			return;
		}

		TableTab tab = getActiveTableTab(false);
		if (tab == null) {
			return;
		}
	}

	/**
	 * コンテキスト(キーバインドスコープ)のアクティブ／非アクティブ切り替え
	 * @param isActivate
	 */
	public void activateContext(boolean isActivate) {
		IContextService contextService = (IContextService)getSite().getService(IContextService.class);
		if (isActivate) {
			contextActivation = contextService.activateContext(KB_SCOPE_ID_DICTIONARY);
		} else {
			contextService.deactivateContext(contextActivation);
		}
	}

	/**
	 * ビューを閉じる際の処理
	 * @see org.eclipse.ui.IPerspectiveListener2#perspectiveChanged(IWorkbenchPage, IPerspectiveDescriptor, String)
	 */
	public void perspectiveChanged(IWorkbenchPage page,
			IPerspectiveDescriptor perspective,
			IWorkbenchPartReference partRef, String changeId) {

		// ビューが閉じられた場合
		if (IWorkbenchPage.CHANGE_VIEW_HIDE.equals(changeId)) {
			// 閉じられたビューが辞書ビューの場合
			if (partRef != null && PART_ID_DICTIONARY.equals(partRef.getId())) {
				// 編集されている場合
				if (isDirty()) {
					page.findView(partRef.getId());

					// 保存確認
					boolean save = MessageDialog.openQuestion(getSite().getShell(),
							Messages.DictionaryView_2, Messages.DictionaryView_3);

					if (save) {
						for (TableTab tab : tabs) {
							if (tab.isDirty()) {
								saveDictionary(tab, false);
							}
						}
					}
				}
				// ハンドラーの削除
				handlerUpdater.clear();

				// タブの削除
				for (TableTab tab : tabs) {
					tab.dispose();
				}
			}
		}
	}

	/**
	 * ワークベンチを閉じる際の処理
	 * @see org.eclipse.ui.IWorkbenchListener#preShutdown(IWorkbench, boolean)
	 */
	public boolean preShutdown(IWorkbench workbench, boolean forced) {

		if (isDirty() == false) {
			return true;
		}

		int status = CustomMessageDialog.openQuestionWithCancel(
			DictionaryView.this.getSite().getShell(),
			Messages.DictionaryView_2,
			Messages.DictionaryView_3);

		boolean close = true;

		// はい
		if (status == IDialogConstants.YES_ID) {
			for (TableTab tab : tabs) {
				if (tab.isDirty()) {
					saveDictionary(tab, false);
				}
			}

		// いいえ
		} else if (status == IDialogConstants.NO_ID) {

		// キャンセル
		} else {
			close = false;
		}

		return forced || close;
	}

	/**
	 * 新規タブの作成
	 * @param string タブ名
	 * @return 作成したタブ
	 * @throws Exception
	 */
	public TableTab createNewTab(String string) {

		// 空のタブを作成しアクティブにする
		TableTab tab = new TableTab(this, folder, string);
		tab.getDictionary().getDictionaryClass().model = ModelManager.DEFAULT_MODEL_KEY;
		tabs.add(tab);
		tab.activate();

		// ハンドラーの更新
		updateHandler();

		return tab;
	}

	/**
	 * アクティブなタブのインデックスを取得する
	 * @return インデックス
	 */
	public int getActiveTabIndex() {
		return folder.getSelectionIndex();
	}

	/**
	 * アクティブなタブのインデックスを設定する
	 * @return インデックス
	 */
	public TableTab setActiveTabIndex(int index) {

		if (index >= folder.getItemCount()) {
			return null;
		}
		folder.setSelection(index);
		return tabs.get(index);
	}

	/**
	 * アクティブなタブを取得する
	 * @param isCreate trueの場合、アクティブなタブがなければ新規作成する
	 * @return タブ
	 */
	public TableTab getActiveTableTab(boolean isCreate) {
		int index = getActiveTabIndex();
		if (index < 0) {
			if (isCreate) {
				TableTab tab = createNewTab(DEFAULT_DIC_NAME);
				return tab;
			}
			return null;
		}
		return tabs.get(index);
	}

	/**
	 * テーブルタブの内容をファイルに保存する
	 * @param tab テーブルタブ
	 * @param outputMessage
	 * @return 0:正常終了／1:キャンセル／2:タブ未選択
	 */
	public int saveDictionary(final TableTab tab, boolean outputMessage) {

		// タブが選択されていない／存在しない場合、処理なし
		if (tab == null) {
			return 2;
		}

		tab.activate();

		// ローカルファイルが存在する
		if (tab.isFileExist()) {
			tab.save(outputMessage);

		// ローカルファイルが存在しない
		} else {

			// ファイルダイアログ（保存用）　TODO:別クラスに分離する予定
			ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
					getSite().getShell(), new WorkbenchLabelProvider(), new WorkbenchContentProvider()) {

				/** ファイル名入力テキストボックス */
				private Text text;
				/** ファイルパス表示テキストボックス */
				private Text labelPath;
				/** ファイル */
				private IFile file;

				/**
				 * ダイアログエリアの作成
				 * @see org.eclipse.ui.dialogs.ElementTreeSelectionDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
				 */
				protected Control createDialogArea(Composite parent) {

					Composite composite = (Composite)super.createDialogArea(parent);

					Composite child = new Composite(composite, SWT.NONE);
					GridLayout layout = new GridLayout(2, false);
					child.setLayout(layout);
					child.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

					Label label = new Label(child, SWT.NONE);
					label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
					label.setText(Messages.DictionaryView_6);

					// ファイルパスを表示するラベル（テキストボックスを使用）
					labelPath = new Text(child, SWT.READ_ONLY);
					labelPath.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

					// ファイル名を入力するテキストボックス
					text = new Text(child, SWT.BORDER);
					GridData data = new GridData(SWT.FILL, SWT.TOP, true, false);
					data.horizontalSpan = 2;
					text.setLayoutData(data);
//					text.setText(new Path(tab.getDictionaryName()).addFileExtension(EXTENSION_XML).toString());
					text.setText(new Path(tab.getDictionaryName()).addFileExtension(EXTENSION_JDD).toString());

					text.addModifyListener(new ModifyListener() {
						public void modifyText(ModifyEvent e) {
							updateOKStatus();
						}
					});

					return composite;
				}

				/**
				 * バリデータの設定
				 * @see org.eclipse.ui.dialogs.ElementTreeSelectionDialog#setValidator(org.eclipse.ui.dialogs.ISelectionStatusValidator)
				 */
				public void setValidator(ISelectionStatusValidator validator) {

					if (validator != null) {
						super.setValidator(validator);
						return;
					}

					super.setValidator(new ISelectionStatusValidator() {
						public IStatus validate(Object[] selection) {

							file = null;

							// ファイル名（妥当性）チェック
							IWorkspace workspace = ResourcesPlugin.getWorkspace();
							IStatus status = workspace.validateName(text.getText(), IResource.FILE);
							if (!status.isOK()) {
								labelPath.setText(""); //$NON-NLS-1$
								return status;
							}

							// 選択チェック
							if (selection.length == 0) {
								labelPath.setText(""); //$NON-NLS-1$
								return new Status(Status.ERROR, ModelToolDictionaryPlugin.PLUGIN_ID, ""); //$NON-NLS-1$
							}

							// ファイル名（重複）チェック
							IContainer container;
							Object resource = selection[0];
							if (resource instanceof IContainer) {
								container = (IContainer)resource;
							} else {
								// ファイルを選択している場合、親フォルダを取得
								container = ((IFile)resource).getParent();
							}
							IPath filePath = container.getFullPath().append(text.getText());
							// 拡張子xmlが未入力の場合、拡張子を付加する
							String extension = filePath.getFileExtension();
							if (extension == null || !PluginHelper.in(extension, false, DICTIONARY_EXTENSIONS)) {
//								filePath = filePath.addFileExtension(EXTENSION_XML);
								filePath = filePath.addFileExtension(EXTENSION_JDD);
							}
							file = PluginHelper.getFile(filePath);
							labelPath.setText(file.getFullPath().toString());
							// 同名ファイルが存在
							if (file.exists()) {
								file = null;
								return new Status(Status.ERROR, ModelToolDictionaryPlugin.PLUGIN_ID, Messages.DictionaryView_10);
							}
							// 同名フォルダが存在
							if (ResourcesPlugin.getWorkspace().getRoot().getFolder(file.getFullPath()).exists()) {
								file = null;
								return new Status(Status.ERROR, ModelToolDictionaryPlugin.PLUGIN_ID, Messages.DictionaryView_11);
							}
							return new Status(Status.OK, ModelToolDictionaryPlugin.PLUGIN_ID, ""); //$NON-NLS-1$
						}
					});
				}

				/**
				 * TreeViewerの作成
				 * @see org.eclipse.ui.dialogs.ElementTreeSelectionDialog#createTreeViewer(org.eclipse.swt.widgets.Composite)
				 */
				protected TreeViewer createTreeViewer(Composite parent) {

					TreeViewer viewer = super.createTreeViewer(parent);

					// ビューワにリスナを追加
					viewer.addSelectionChangedListener(new ISelectionChangedListener() {
						// ファイルが選択された場合、テキストボックスにファイル名表示
						@Override
						public void selectionChanged(SelectionChangedEvent event) {
							if (event.getSelection().isEmpty()) {
								return;
							}
							IStructuredSelection selection = (IStructuredSelection)event.getSelection();
							IResource resource = (IResource)selection.getFirstElement();
							if (resource instanceof IFile) {
								IFile file = (IFile)resource;
								text.setText(file.getName());
							}
						}
					});
					return viewer;
				}

				/**
				 * ダイアログを開く
				 * @see org.eclipse.ui.dialogs.ElementTreeSelectionDialog#open()
				 */
				public int open() {
					// バリデータの設定
					setValidator(null);
					return super.open();
				}

				/**
				 * 複数行選択可否の設定
				 * @see org.eclipse.ui.dialogs.ElementTreeSelectionDialog#setAllowMultiple(boolean)
				 */
				public void setAllowMultiple(boolean allowMultiple) {
					// 複数行は選択させない
					super.setAllowMultiple(false);
				}

				/**
				 * 結果の計算
				 * @see org.eclipse.ui.dialogs.ElementTreeSelectionDialog#computeResult()
				 */
				protected void computeResult() {
					List<IFile> list = new ArrayList<IFile>();
					if (file != null) {
						list.add(file);
					}
					super.setResult(list);
				}
			};

			dialog.setTitle(Messages.DictionaryView_2);
			dialog.setMessage(Messages.DictionaryView_14);
			// フィルターの設定
//			dialog.addFilter(new DictionaryViewerFilter());
			dialog.addFilter(new ViewerFilter() {
				@Override
				public boolean select(Viewer viewer, Object parentElement, Object element) {
					// 辞書ファイル以外のファイルは表示しない
					if (element instanceof IFile &&
							!PluginHelper.in(((IFile)element).getFileExtension(), false, DICTIONARY_EXTENSIONS)) {
						return false;
					}
					return true;
				}
			});

			// 複数選択不可
			dialog.setAllowMultiple(false);
			// ヘルプ非表示
			dialog.setHelpAvailable(false);
			// ワークスペースのルートの配下を表示
			dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
			// 初期表示位置の設定
			dialog.setInitialSelection(dialogFilterPath);

			// ダイアログを開く
			if (dialog.open() != Dialog.OK) {
				return 1;
			}

			// 指定したファイル(ハンドル)を取得
			IFile file = (IFile)dialog.getFirstResult();

			// 選択した場所の退避
			dialogFilterPath = file.getParent();

			// ファイルを保存する
			tab.save(file, outputMessage);
		}

		return 0;
	}

	/**
	 * 辞書ファイルの読み込み<br>
	 * ダイアログからファイルを選択しビューに表示する
	 * @return テーブルタブ
	 */
	public TableTab loadDictionary() {

		// 辞書選択ダイアログ
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				getSite().getShell(), new WorkbenchLabelProvider(), new WorkbenchContentProvider());
		dialog.setTitle(Messages.DictionaryView_15);
		dialog.setMessage(Messages.DictionaryView_16);
		// フィルターの設定
		dialog.addFilter(new DictionaryViewerFilter());

		// 複数選択不可
		dialog.setAllowMultiple(false);
		// ヘルプ非表示
		dialog.setHelpAvailable(false);
		// ワークスペースのルートの配下を表示
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		// 初期表示位置の設定
		dialog.setInitialSelection(dialogFilterPath);

		// バリデータの設定
		dialog.setValidator(new ISelectionStatusValidator() {
			public IStatus validate(Object[] selection) {
				if (selection.length > 0 && selection[0] instanceof IFile) {
					return new Status(IStatus.OK, ModelToolDictionaryPlugin.PLUGIN_ID, ""); //$NON-NLS-1$
				} else {
					return new Status(IStatus.ERROR, ModelToolDictionaryPlugin.PLUGIN_ID, ""); //$NON-NLS-1$
				}
			}
		});

		// ダイアログを開く
		if (dialog.open() != Dialog.OK) {
			return null;
		}

		// 選択したファイルを取得
		IFile file = (IFile)dialog.getFirstResult();

		// 選択した場所の退避
		dialogFilterPath = file.getParent();

		// ファイルをロードする
		TableTab tab = loadDictionary(file);

		return tab;
	}

	/**
	 * 辞書ファイルの読込<br>
	 * 指定された辞書ファイルをビューに表示する
	 * @param file 辞書ファイル
	 * @return テーブルタブ
	 */
	public TableTab loadDictionary(IFile file) {

		TableTab fileTab = getTabOfFile(file);
		if (fileTab != null) {
			fileTab.activate();
			return fileTab;
		}

		TableTab tab = createNewTab(file.getName());
		tab.load(file);

		return tab;
	}

	/**
	 * 辞書ファイルの読込<br>
	 * 辞書ファイルを指定のタブに読み込む
	 * @param file
	 * @return
	 */
	public TableTab loadDictionaryToActiveTab(IFile file) {

		TableTab tab = getActiveTableTab(true);
		tab.load(file);

		return tab;
	}

	/**
	 * インポート
	 * @param file
	 * @return
	 */
	public TableTab importDictionary(File file) {

		// 拡張子なしのファイル名
		String fileNm = file.getName();
		int idx = fileNm.lastIndexOf('.');
		if (idx >= 0)
			fileNm = fileNm.substring(0, idx);

		TableTab tab = createNewTab(fileNm);
		tab.importFile(file);
		return tab;
	}

	/**
	 * エクスポート
	 * @param file
	 * @param tab
	 */
	public boolean exportDictionary(File file, TableTab tab) {
		return tab.exportFile(file);
	}

	/**
	 * 指定ファイルを開いているタブがあれば、そのタブを返す
	 * @param file
	 * @return
	 */
	public TableTab getTabOfFile(IFile file) {

		for (TableTab tab : tabs) {
			if (tab.equalFile(file)) {
				return tab;
			}
		}

		return null;
	}

	/**
	 * 指定タブアイテムに紐付くタブを返す
	 * @param item
	 * @return
	 */
	private TableTab getTabOfItem(CTabItem item) {

		if (item == null) {
			return null;
		}

		int index = folder.indexOf(item);
		if (index < 0 || index >= tabs.size()) {
			return null;
		}

		return tabs.get(index);
	}

	/**
	 * 指定ファイルが開かれているかを返す
	 * @param file
	 * @return
	 */
	public boolean isFileOpen(IFile file) {
		return getTabOfFile(file) != null;
	}

	/**
	 * 指定ファイルのタブを閉じる
	 * @param file
	 */
	public void closeTab(IFile file) {
		TableTab tab = getTabOfFile(file);
		if (tab == null) {
			return;
		}
		// タブを閉じる
		tab.dispose();
		tabs.remove(tab);

		// ハンドラーの更新
		updateHandler();
	}

	/**
	 * コピーレコードの取得
	 * @return コピーレコード
	 * @deprecated
	 * @see #getCopyEntries()
	 */
	public Entry getCopyEntry() {
		return copyEntry;
	}

	/**
	 * コピーレコードの取得
	 * @return コピーレコードのリスト
	 */
	public List<Entry> getCopyEntries() {
		return new ArrayList<Entry>(copyEntries);
	}

	/**
	 * コピーレコードの設定
	 * @param copyEntry
	 * @deprecated
	 * @see #setCopyEntries(List, String)
	 */
	public void setCopyEntry(Entry copyEntry, String model) {
		this.copyEntry = copyEntry;
		this.srcModel = model;
	}

	/**
	 * コピーレコードの設定
	 * @param copyEntries コピーレコード
	 * @param model モデル
	 */
	public void setCopyEntries(List<Entry> copyEntries, String model) {
		this.copyEntries.clear();
		this.copyEntries.addAll(copyEntries);
		this.srcModel = model;
	}

	/**
	 * コピー元、コピー先辞書のモデルが同じかどうか判定する。
	 * @param dstModel コピー先辞書のモデル
	 * @return 同じ場合trueを返す
	 */
	public boolean isSameCopyModel(String dstModel) {
		return dstModel.equals(srcModel);
	}

	/**
	 * 「コピーデータなし」かどうかのチェック
	 * @return
	 */
	public boolean isNoCopyData() {
		return copyMode == COPY_MODE_NODATA;
	}

	/**
	 * 「コピーデータあり」かどうかのチェック
	 * @return
	 */
	public boolean isCopyData() {
		return copyMode == COPY_MODE_COPY;
	}

	/**
	 * 「切り取りデータあり」かどうかのチェック
	 * @return
	 */
	public boolean isCutData() {
		return copyMode == COPY_MODE_CUT;
	}

	/**
	 * コピーモードの設定
	 * @param copyMode コピーモード
	 */
	public void setCopyMode(int copyMode) {
		this.copyMode = copyMode;
		handlerUpdater.updatePaste(getActiveTableTab(false), !isNoCopyData());
	}

	/**
	 * 編集状態フラグのチェック
	 * @return 編集状態フラグONのタブがいつでもあればtrueを返す
	 */
	public boolean isDirty() {
		for (TableTab tab : tabs) {
			if (tab.isDirty()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * タブ数の取得
	 * @return
	 */
	public int getNumberOfTabs() {
		return tabs.size();
	}

	/**
	 * タブ名、ツールチップの配列を取得する
	 * @return {{辞書名1, 相対パス1, 絶対パス1}, {辞書名2, 相対パス2, 絶対パス2}, …}形式の配列
	 */
	public String[][] getArrayOfTabText() {

		String[][] array = new String[tabs.size()][3];

		for (int i=0; i<tabs.size(); i++) {
			TableTab tab = tabs.get(i);
			if (tab.getFile() != null) {
				array[i][0] = PluginHelper.getFileNameWithoutExtension(tab.getFile());
				array[i][1] = PluginHelper.getRelativePath(tab.getFile());
				array[i][2] = PluginHelper.getAbsolutePath(tab.getFile());
			} else {
				array[i][0] = tab.getDictionaryName();
				array[i][1] = ""; //$NON-NLS-1$
				array[i][2] = ""; //$NON-NLS-1$
			}
		}

		return array;
	}

	/**
	 *  タブリストの取得
	 */
	public List<TableTab> getTabs() {
		return tabs;
	}

	/**
	 *  コマンドハンドラーの状態更新
	 */
	private void updateHandler() {
		handlerUpdater.update(getActiveTableTab(false));
	}
}
