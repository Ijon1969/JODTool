package jp.ac.kyushu_u.csce.modeltool.spec.preference;

import static jp.ac.kyushu_u.csce.modeltool.base.constant.BasePreferenceConstants.*;
import static jp.ac.kyushu_u.csce.modeltool.spec.constant.SpecPreferenceConstants.*;
import jp.ac.kyushu_u.csce.modeltool.base.ModelToolBasePlugin;
import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;
import jp.ac.kyushu_u.csce.modeltool.spec.Messages;
import jp.ac.kyushu_u.csce.modeltool.spec.ModelToolSpecPlugin;
import jp.ac.kyushu_u.csce.modeltool.spec.constant.SpecConstants;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * プリファレンスページ（検査設定）
 *
 * @author KBK yoshimura
 */
public class InspectionPreferencePage
	extends PreferencePage implements IWorkbenchPreferencePage {

	/**
	 * 色設定コントロール配列<br>
	 * 0:名詞句(初回)、1:名詞句、2:動詞句(初回)、3:動詞句、4:状態(初回)、5:状態
	 */
	private ColorSelector[] selectors;

	/**
	 * 色設定プリファレンスキー配列<br>
	 * 0:名詞句(初回)、1:名詞句、2:動詞句(初回)、3:動詞句、4:状態(初回)、5:状態
	 */
	private static final String[] PK_COLORS = {
		PK_COLOR_NOUN_FIRST,
		PK_COLOR_NOUN,
		PK_COLOR_VERB_FIRST,
		PK_COLOR_VERB,
		PK_COLOR_STATE_FIRST,
		PK_COLOR_STATE,
	};

	// 出力先設定
	// モデル出力フォルダ
	/** 既定フォルダ ラジオボタン */
	private Button rdoFixedFolder;
	/** 出力時選択 */
	private Button rdoSelectedFolder;
	/** 既定フォルダ 入力フィールド */
	private Text txtFixedFolder;
	/** 既定フォルダ 参照ボタン */
	private Button btnFixedFolder;

	/** 正規表現 */
	private Button regExButton;

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {

		IPreferenceStore store = getPreferenceStore();

		Composite contents= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		contents.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		contents.setLayoutData(gd);

		// 先頭行
		Label topLabel = new Label(contents, SWT.NONE);
		topLabel.setText(Messages.InspectionPreferencePage_0);
		gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		topLabel.setLayoutData(gd);

		// 出力先フォルダ設定
		createFolderGroup(contents);

		// マーク色設定
		createColorGroup(contents);

		// 正規表現設定
		regExButton = new Button(contents, SWT.CHECK | SWT.LEFT);
		regExButton.setFont(parent.getFont());
		regExButton.setText(Messages.InspectionPreferencePage_1);
		regExButton.setSelection(store.getBoolean(PK_USE_REGULAR_EXPRESSION));

		// 初期値の設定
		setInitialValue();

		return null;
	}

	/**
	 * 出力先フォルダ設定エリアの作成
	 * @param parent
	 * @return
	 */
	protected Control createFolderGroup(Composite parent) {

		// 出力先フォルダの指定
		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.InspectionPreferencePage_2);
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridLayout layout= new GridLayout();
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 0;
		group.setLayout(layout);

		// ラジオボタン（既定フォルダ）
		rdoFixedFolder = new Button(group, SWT.RADIO);
		rdoFixedFolder.setText(Messages.InspectionPreferencePage_3);
		rdoFixedFolder.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		rdoFixedFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				btnFixedFolder.setEnabled(true);
				txtFixedFolder.setEnabled(true);
				checkState();
			}
		});

		// テキストボックス（既定フォルダ）
		txtFixedFolder = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		txtFixedFolder.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		txtFixedFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
//		txtFixedFolder.setText(getPreferenceStore().getString(PK_MARK_FIXED_PATH));

		// 参照ボタン
		btnFixedFolder = new Button(group, SWT.NONE);
		btnFixedFolder.setText(Messages.InspectionPreferencePage_4);
		btnFixedFolder.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		Point buttonSize = btnFixedFolder.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		btnFixedFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// フォルダ選択ダイアログ
				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
						getShell(), new WorkbenchLabelProvider(), new WorkbenchContentProvider());
				dialog.setTitle(Messages.InspectionPreferencePage_5);
				dialog.setMessage(Messages.InspectionPreferencePage_6);
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

				// 初期フォーカスの設定
				if (txtFixedFolder.getText().isEmpty() == false) {
//					IPath path = new Path(txtFixedFolder.getText());
//					IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
////					IContainer container = root.getContainerForLocation(path);
//					IContainer container = root.getFolder(path);
					IContainer container = PluginHelper.getFolder(txtFixedFolder.getText());
					dialog.setInitialSelection(container);
				}

				// ダイアログを開く
				if (dialog.open() == Dialog.OK) {
					// 結果をテキストボックスにセット
//					txtFixedFolder.setText(((IResource)dialog.getFirstResult()).getLocation().toString());
					txtFixedFolder.setText(((IResource)dialog.getFirstResult()).getFullPath().toString());

					// 妥当性チェック
					checkState();
				}
			}
		});

		// ラジオボタン（登録時指定）
		rdoSelectedFolder = new Button(group, SWT.RADIO);
		rdoSelectedFolder.setText(Messages.InspectionPreferencePage_7);
		GridData gd2 = new GridData(SWT.LEFT, SWT.FILL, true, true, 3, 1);
		gd2.heightHint = buttonSize.y;
		rdoSelectedFolder.setLayoutData(gd2);
		rdoSelectedFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				txtFixedFolder.setEnabled(false);
				btnFixedFolder.setEnabled(false);
				checkState();
			}
		});

		return group;
	}

	/**
	 * マーク色設定グループの作成
	 * @param parent
	 * @return
	 */
	protected Control createColorGroup(Composite parent) {

		IPreferenceStore storeBase = ModelToolBasePlugin.getDefault().getPreferenceStore();

		// マークの設定
		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.InspectionPreferencePage_8);
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		group.setLayout(layout);

		// 1行目
		Label emptyLabel = new Label(group, SWT.NONE);
		emptyLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

		Label firstLabel = new Label(group, SWT.NONE);
		firstLabel.setText(Messages.InspectionPreferencePage_9);
		firstLabel.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));

		Label nextLabel = new Label(group, SWT.NONE);
		nextLabel.setText(Messages.InspectionPreferencePage_10);
		nextLabel.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));

		selectors = new ColorSelector[6];

		// 名詞句
		Label label1 = new Label(group, SWT.NONE);
		label1.setText(Messages.InspectionPreferencePage_11);
		selectors[0] = getSelector(group);
		selectors[0].setColorValue(PreferenceConverter.getColor(storeBase, PK_COLOR_NOUN_FIRST));
		selectors[1] = getSelector(group);
		selectors[1].setColorValue(PreferenceConverter.getColor(storeBase, PK_COLOR_NOUN));

		// 動詞句
		Label label2 = new Label(group, SWT.NONE);
		label2.setText(Messages.InspectionPreferencePage_12);
		selectors[2] = getSelector(group);
		selectors[2].setColorValue(PreferenceConverter.getColor(storeBase, PK_COLOR_VERB_FIRST));
		selectors[3] = getSelector(group);
		selectors[3].setColorValue(PreferenceConverter.getColor(storeBase, PK_COLOR_VERB));

		// 状態
		Label label3 = new Label(group, SWT.NONE);
		label3.setText(Messages.InspectionPreferencePage_13);
		selectors[4] = getSelector(group);
		selectors[4].setColorValue(PreferenceConverter.getColor(storeBase, PK_COLOR_STATE_FIRST));
		selectors[5] = getSelector(group);
		selectors[5].setColorValue(PreferenceConverter.getColor(storeBase, PK_COLOR_STATE));

		return group;
	}

	/**
	 * カラーセレクターの生成
	 * @param parent 親コントロール
	 * @return カラーセレクター
	 */
	private ColorSelector getSelector(Composite parent) {
		ColorSelector selector = new ColorSelector(parent);
		return selector;
	}

	/**
	 * 初期化処理
	 */
	public void init(IWorkbench workbench) {

		// プリファレンスストアのセット
		setPreferenceStore(ModelToolSpecPlugin.getDefault().getPreferenceStore());
	}

	/**
	 * OKボタン処理
	 */
	public boolean performOk() {

		IPreferenceStore store = getPreferenceStore();

		if (rdoFixedFolder.getSelection()) {
			store.setValue(PK_MARK_FOLDER_SETTING, PV_MARK_FIXED);
			store.setValue(PK_MARK_FIXED_PATH, txtFixedFolder.getText());
		} else {
			store.setValue(PK_MARK_FOLDER_SETTING, PV_MARK_SELECT);
			store.setValue(PK_MARK_FIXED_PATH, ""); //$NON-NLS-1$
		}

		for (int i=0; i<selectors.length; i++) {
//			PreferenceConverter.setValue(store, PK_COLORS[i], selectors[i].getColorValue());
			PreferenceConverter.setValue(ModelToolBasePlugin.getDefault().getPreferenceStore(),
					PK_COLORS[i], selectors[i].getColorValue());
		}

		store.setValue(PK_USE_REGULAR_EXPRESSION, regExButton.getSelection());
		// トグルボタンへの設定値の反映
		ModelToolSpecPlugin.getHandlerActivationManager().changeToggleState(SpecConstants.COMMAND_ID_REG_EX, regExButton.getSelection());

		return true;
	}

	/**
	 * デフォルトに戻すボタン処理
	 */
	protected void performDefaults() {

		// プリファレンスストア
		IPreferenceStore store = getPreferenceStore();
		// プリファレンスストア（Baseプラグイン）
		IPreferenceStore storeBase = ModelToolBasePlugin.getDefault().getPreferenceStore();

		txtFixedFolder.setText(store.getDefaultString(PK_MARK_FIXED_PATH));
		for (int i=0; i<selectors.length; i++) {
			// マーク色設定はBaseプラグインのプリファレンスストアで設定
			selectors[i].setColorValue(StringConverter.asRGB(storeBase.getDefaultString(PK_COLORS[i])));
		}

		regExButton.setSelection(store.getDefaultBoolean(PK_USE_REGULAR_EXPRESSION));

		super.performDefaults();
	}

	/**
	 * 初期値の設定
	 */
	protected void setInitialValue() {

		IPreferenceStore store = getPreferenceStore();

		// プリファレンスの値より初期状態を設定
		if (PV_MARK_FIXED == store.getInt(PK_MARK_FOLDER_SETTING)) {
			rdoFixedFolder.setSelection(true);
			txtFixedFolder.setText(store.getString(PK_MARK_FIXED_PATH));
			txtFixedFolder.setEnabled(true);
			btnFixedFolder.setEnabled(true);

		} else {
			rdoSelectedFolder.setSelection(true);
			txtFixedFolder.setText(""); //$NON-NLS-1$
			txtFixedFolder.setEnabled(false);
			btnFixedFolder.setEnabled(false);
		}

		// 妥当性チェック
		checkState();
	}

	/**
	 * 妥当性チェック
	 */
	protected void checkState() {

		boolean valid = true;
		String msg = null;

		if (rdoFixedFolder.getSelection()) {
			if ("".equals(txtFixedFolder.getText())) { //$NON-NLS-1$
				msg = Messages.InspectionPreferencePage_17;
				valid = false;
			}
		}
		setErrorMessage(msg);
		setValid(valid);
	}
}