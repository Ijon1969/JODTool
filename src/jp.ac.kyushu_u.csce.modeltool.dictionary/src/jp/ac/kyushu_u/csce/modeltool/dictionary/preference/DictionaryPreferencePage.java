package jp.ac.kyushu_u.csce.modeltool.dictionary.preference;

import static jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryPreferenceConstants.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;
import jp.ac.kyushu_u.csce.modeltool.dictionary.Messages;
import jp.ac.kyushu_u.csce.modeltool.dictionary.ModelToolDictionaryPlugin;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.Model;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.ModelManager;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
 * プリファレンスページ（辞書設定）
 *
 * @author yoshimura
 */
public class DictionaryPreferencePage
	extends PreferencePage
	implements IWorkbenchPreferencePage {

	// モデル出力フォルダ
	/** 既定フォルダ ラジオボタン */
	private Button rdoFixedFolder;
	/** 出力時選択 */
	private Button rdoSelectedFolder;
	/** 既定フォルダ 入力フィールド */
	private Text txtFixedFolder;
	/** 既定フォルダ 参照ボタン */
	private Button btnFixedFolder;
//	/** 検査フォルダ使用 ボタン */
//	private Button btnInspectFolder;

	/** 「元に戻す」ヒストリーのサイズ */
	private Text txtHistorySize;
//	/** クラスエントリの背景色 */
//	private ColorSelector cslClassBg;

	/** 種別によるエントリの背景色 */
	private List<ColorSelector[]> cslSectionBg = new ArrayList<ColorSelector[]>();
	/** 背景色プリファレンスキー */
	private List<String> lstBgPrefKey = new ArrayList<String>();

	/** 「形式的定義の補完を使用する」チェックボックス */
	private Button chkCompletion;

	/** 「モデル出力時にファイルを開く」チェックボックス（Overture用） */
	private Button chkOpenOutputModelFile;

	/**
	 * コンストラクタ
	 */
	public DictionaryPreferencePage() {
		setPreferenceStore(ModelToolDictionaryPlugin.getDefault().getPreferenceStore());
		setDescription(Messages.DictionaryPreferencePage_0);
	}

	/**
	 * コンテンツの作成
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {

		Composite composite= new Composite(parent, SWT.NONE);
		GridLayout layout= new GridLayout();
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		layout.numColumns = 1;
		composite.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gd);

		// モデル出力設定エリアの作成
		createOutputArea(composite);

		// 汎用設定エリアの作成
		createGeneralArea(composite);

		// リスナーの設定
		setListener();

		// 初期値の設定
		setInitialValue();

		return composite;
	}

	/**
	 * モデル出力設定エリアの作成
	 * @param control
	 * @return
	 */
	protected Control createOutputArea(Composite parent) {

		// ラジオボタングループ（モデルの出力先フォルダ）
		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.DictionaryPreferencePage_1);
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridLayout layout= new GridLayout();
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 0;
		group.setLayout(layout);

		// ラジオボタン（既定フォルダ）
		rdoFixedFolder = new Button(group, SWT.RADIO);
		rdoFixedFolder.setText(Messages.DictionaryPreferencePage_2);
		rdoFixedFolder.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

		// テキストボックス（既定フォルダ）
		txtFixedFolder = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		txtFixedFolder.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_LIST_BACKGROUND));
		txtFixedFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		// ボタン（既定フォルダ 参照）
		btnFixedFolder = new Button(group, SWT.NONE);
		btnFixedFolder.setText(Messages.DictionaryPreferencePage_3);
		btnFixedFolder.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		Point buttonSize = btnFixedFolder.computeSize(SWT.DEFAULT, SWT.DEFAULT);

//		// ボタン（検査フォルダ使用 参照）
//		btnInspectFolder = new Button(group, SWT.NONE);
//		btnInspectFolder.setText("検査フォルダを使用");
//		GridData gd1 = new GridData(SWT.RIGHT, SWT.FILL, false, true, 3, 1);
//		btnInspectFolder.setLayoutData(gd1);

		// ラジオボタン（登録時指定）
		rdoSelectedFolder = new Button(group, SWT.RADIO);
		rdoSelectedFolder.setText(Messages.DictionaryPreferencePage_4);
		GridData gd2 = new GridData(SWT.LEFT, SWT.FILL, true, true, 3, 1);
		gd2.heightHint = buttonSize.y;
		rdoSelectedFolder.setLayoutData(gd2);

		return group;
	}

	/**
	 * 汎用設定エリアの作成
	 * @param control
	 * @return
	 */
	protected Control createGeneralArea(Composite parent) {

		// ラジオボタングループ（モデルの出力先フォルダ）
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridLayout layout= new GridLayout();
		layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		// 「元に戻す」ヒストリーのサイズ
		Label lblHistorySize = new Label(composite, SWT.NONE);
		lblHistorySize.setText(Messages.DictionaryPreferencePage_7);
		lblHistorySize.setLayoutData(new GridData());

		txtHistorySize = new Text(composite, SWT.BORDER);
		GridData gdHistorySize = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gdHistorySize.widthHint = 100;
		txtHistorySize.setLayoutData(gdHistorySize);
		txtHistorySize.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				checkState();
			}
		});

		// クラスエントリの背景色
//		Label lblClassBg = new Label(composite, SWT.NONE);
//		lblClassBg.setText(Messages.DictionaryPreferencePage_8);
//		cslClassBg = new ColorSelector(composite);
		ModelManager modelManager = ModelManager.getInstance();
		for (String modelKey : modelManager.getKeyList()) {
			Model model = modelManager.getModelByKey(modelKey);
			int bgCnt = modelManager.getSectionBgCount(model);
			if (bgCnt > 0) {
				Group group = new Group(composite, SWT.NONE);
				group.setText(modelManager.getModelName(modelKey));
				group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
				GridLayout loModel = new GridLayout();
				loModel.numColumns = 2;
				group.setLayout(loModel);
				ColorSelector[] cslArr = new ColorSelector[bgCnt];
				int idx = 0;
				for (Model.Section section : model.getSectionDefs()) {
					if (section.hasBg()) {
						String prefKey = PK_DICTIONARY_SECTION_BG + modelKey + "_" + //$NON-NLS-1$
											String.valueOf(section.getCd());
						new Label(group, SWT.NONE).setText(MessageFormat.format(Messages.DictionaryPreferencePage_13, section.getName()));
						cslArr[idx++] = new ColorSelector(group);
						lstBgPrefKey.add(prefKey);
					}
				}
				cslSectionBg.add(cslArr);
			}
		}

		// 形式的定義の補完を使用する
		chkCompletion = new Button(composite, SWT.CHECK);
//		chkCompletion.setText("Use formal-definition completion.");
		chkCompletion.setText(Messages.DictionaryPreferencePage_14);
		GridData gdCompletion = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
		chkCompletion.setLayoutData(gdCompletion);

		// モデル出力時にファイルを開く
		chkOpenOutputModelFile = new Button(composite, SWT.CHECK);
		chkOpenOutputModelFile.setText(Messages.DictionaryPreferencePage_12);
		GridData gdOpenOutputModelFile = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
		chkOpenOutputModelFile.setLayoutData(gdOpenOutputModelFile);
		if (!ModelManager.getInstance().canOpenModelEditor()) {
			chkOpenOutputModelFile.setVisible(false);
		}

		return composite;
	}

	/**
	 * リスナー設定
	 */
	protected void setListener() {

//		final IPreferenceStore store = getPreferenceStore();

		// 既定フォルダ 参照ボタン
		btnFixedFolder.addSelectionListener(new FolderButtonListener());

		// 検査フォルダ使用ボタン　　　XXX:依存関係逆のため使えません
//		btnInspectFolder.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				// 検査設定の出力先フォルダの内容をモデルの出力先フォルダにコピー
//				txtFixedFolder.setText(store.getString(PK_MARK_FIXED_PATH));
//				// 妥当性チェック
//				checkState();
//			}
//		});

		// ラジオボタン
		SelectionAdapter radioButtonListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				txtFixedFolder.setEnabled(rdoFixedFolder.getSelection());
				btnFixedFolder.setEnabled(rdoFixedFolder.getSelection());
//				btnInspectFolder.setEnabled(rdoFixedFolder.getSelection());

				// 妥当性チェック
				checkState();
			}
		};
		rdoFixedFolder.addSelectionListener(radioButtonListener);
		rdoSelectedFolder.addSelectionListener(radioButtonListener);
	}

	/**
	 * フォルダ選択ボタンリスナー
	 *
	 * @author KBK yoshimura
	 */
	private class FolderButtonListener extends SelectionAdapter {

		/**
		 * コントロールが選択されたときの処理
		 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			// フォルダ選択ダイアログ
			ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
					getShell(), new WorkbenchLabelProvider(), new WorkbenchContentProvider());
			dialog.setTitle(Messages.DictionaryPreferencePage_5);
			dialog.setMessage(Messages.DictionaryPreferencePage_6);
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
				IContainer container = PluginHelper.getFolder(txtFixedFolder.getText());
				dialog.setInitialSelection(container);
			}

			// ダイアログを開く
			if (dialog.open() == Dialog.OK) {
				// 結果をテキストボックスにセット
				txtFixedFolder.setText(((IResource)dialog.getFirstResult()).getFullPath().toString());
			}

			// 妥当性チェック
			checkState();
		}
	}

	/**
	 * 初期値の設定
	 */
	protected void setInitialValue() {

		IPreferenceStore store = getPreferenceStore();

		// プリファレンスの値より初期状態を設定
		if (PV_OUTPUT_FIXED == store.getInt(PK_OUTPUT_FOLDER_SETTING)) {
			rdoFixedFolder.setSelection(true);
			txtFixedFolder.setText(store.getString(PK_OUTPUT_FIXED_PATH));
			txtFixedFolder.setEnabled(true);
			btnFixedFolder.setEnabled(true);
//			btnInspectFolder.setEnabled(true);

		} else {
			rdoSelectedFolder.setSelection(true);
			txtFixedFolder.setText(""); //$NON-NLS-1$
			txtFixedFolder.setEnabled(false);
			btnFixedFolder.setEnabled(false);
//			btnInspectFolder.setEnabled(false);
		}

		// 「元に戻す」ヒストリーのサイズ
		txtHistorySize.setText(String.valueOf(store.getInt(PK_DICTIONARY_HISTORY_SIZE)));

		// クラスエントリの背景色
		Iterator<String> itr = lstBgPrefKey.iterator();
		for (ColorSelector[] cslArr : cslSectionBg) {
			for (ColorSelector cslBg : cslArr) {
				cslBg.setColorValue(PreferenceConverter.getColor(store, itr.next()));
			}
		}

		// 形式的定義の補完を使用する
		chkCompletion.setSelection(store.getBoolean(PK_FORMAL_DEFINITION_COMPLETION));

		// モデル出力時にファイルを開く
		if (chkOpenOutputModelFile.getVisible()) {
			chkOpenOutputModelFile.setSelection(store.getBoolean(PK_OPEN_OUTPUT_MODEL_FILE));
		} else {
			chkOpenOutputModelFile.setSelection(false);
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

		// 既定フォルダの存在チェック
		if (valid && rdoFixedFolder.getSelection()) {
			if ("".equals(txtFixedFolder.getText())) { //$NON-NLS-1$
				msg = Messages.DictionaryPreferencePage_9;
				valid = false;
			}
		}

		// 「元に戻す」ヒストリーのサイズ
		if (valid) {
			String historySize = StringUtils.defaultString(txtHistorySize.getText()).trim();
			if (StringUtils.isBlank(historySize)) {
				msg = Messages.DictionaryPreferencePage_10;
				valid = false;
			} else {
				try {
				int size = Integer.parseInt(historySize);
					if (size < 0 || size > PV_DICTIONARY_HISTORY_SIZE_MAX) {
						msg = MessageFormat.format(Messages.DictionaryPreferencePage_11, String.valueOf(size));
						valid = false;
					}
				} catch (NumberFormatException e) {
					msg = MessageFormat.format(Messages.DictionaryPreferencePage_11, String.valueOf(historySize));
					valid = false;
				}
			}
		}

		setErrorMessage(msg);
		setValid(valid);
	}

	/**
	 * 初期化処理
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {

		// プリファレンスストアのセット
		setPreferenceStore(ModelToolDictionaryPlugin.getDefault().getPreferenceStore());
	}

	/**
	 * デフォルトボタン押下時の処理
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults() {

		IPreferenceStore store = getPreferenceStore();

		// プリファレンスの値より初期状態を設定

		// モデルの出力先フォルダ
		switch (store.getDefaultInt(PK_OUTPUT_FOLDER_SETTING)) {
			case PV_OUTPUT_FIXED:
				rdoFixedFolder.setSelection(true);
				txtFixedFolder.setText(store.getDefaultString(PK_OUTPUT_FIXED_PATH));
				rdoSelectedFolder.setSelection(false);
				break;

			case PV_OUTPUT_SELECT:
				rdoFixedFolder.setSelection(false);
				txtFixedFolder.setText(store.getDefaultString(PK_OUTPUT_FIXED_PATH));
				rdoSelectedFolder.setSelection(true);
				break;

			default:
				break;
		}

		// コントロールの制御
		txtFixedFolder.setEnabled(rdoFixedFolder.getSelection());
		btnFixedFolder.setEnabled(rdoFixedFolder.getSelection());
//		btnInspectFolder.setEnabled(rdoFixedFolder.getSelection() &&
//				PV_REGISTER_FIXED == store.getInt(PK_REGISTER_DICTIONARY));

		// 「元に戻す」ヒストリーのサイズ
		txtHistorySize.setText(String.valueOf(store.getDefaultInt(PK_DICTIONARY_HISTORY_SIZE)));

		// クラスエントリの背景色
		Iterator<String> itr = lstBgPrefKey.iterator();
		for (ColorSelector[] cslArr : cslSectionBg) {
			for (ColorSelector cslBg : cslArr) {
				cslBg.setColorValue(PreferenceConverter.getDefaultColor(store, itr.next()));
			}
		}

		// 形式的定義の補完を使用する
		chkCompletion.setSelection(store.getDefaultBoolean(PK_FORMAL_DEFINITION_COMPLETION));

		// モデル出力時にファイルを開く
		if (chkOpenOutputModelFile.getVisible()) {
			chkOpenOutputModelFile.setSelection(store.getDefaultBoolean(PK_OPEN_OUTPUT_MODEL_FILE));
		}
	}

	/**
	 * OKボタン押下時の処理
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk() {

		IPreferenceStore store = getPreferenceStore();

		// モデルの出力先フォルダ
		if (rdoFixedFolder.getSelection()) {
			store.setValue(PK_OUTPUT_FOLDER_SETTING, PV_OUTPUT_FIXED);
			store.setValue(PK_OUTPUT_FIXED_PATH, txtFixedFolder.getText());

		} else if (rdoSelectedFolder.getSelection()) {
			store.setValue(PK_OUTPUT_FOLDER_SETTING, PV_OUTPUT_SELECT);
			store.setToDefault(PK_OUTPUT_FIXED_PATH);
		}

		// 「元に戻す」ヒストリーのサイズ
		store.setValue(PK_DICTIONARY_HISTORY_SIZE, Integer.parseInt(txtHistorySize.getText().trim()));

		// クラスエントリの背景色
		Iterator<String> itr = lstBgPrefKey.iterator();
		for (ColorSelector[] cslArr : cslSectionBg) {
			for (ColorSelector cslBg : cslArr) {
				PreferenceConverter.setValue(store, itr.next(), cslBg.getColorValue());
			}
		}

		// 形式定義の補完を使用する
		store.setValue(PK_FORMAL_DEFINITION_COMPLETION, chkCompletion.getSelection());

		// モデル出力時にファイルを開く
		if (chkOpenOutputModelFile.getVisible()) {
			store.setValue(PK_OPEN_OUTPUT_MODEL_FILE, chkOpenOutputModelFile.getSelection());
		} else {
			store.setValue(PK_OPEN_OUTPUT_MODEL_FILE, false);
		}

		return true;
	}
}