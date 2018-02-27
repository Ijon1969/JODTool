package jp.ac.kyushu_u.csce.modeltool.spec.preference;

import static jp.ac.kyushu_u.csce.modeltool.spec.constant.SpecPreferenceConstants.*;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dialog.DictionaryViewerFilter;
import jp.ac.kyushu_u.csce.modeltool.spec.Messages;
import jp.ac.kyushu_u.csce.modeltool.spec.ModelToolSpecPlugin;
import jp.ac.kyushu_u.csce.modeltool.spec.constant.SpecConstants;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferencePage;
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
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * プリファレンスページ（仕様書エディタ設定）
 *
 * @author KBK yoshimura
 */
public class SpecPreferencePage
	extends PreferencePage
	implements IWorkbenchPreferencePage {

	// 登録辞書
	/** 既定辞書 ラジオボタン */
	private Button rdoFixedDictionary;
	/** 登録時選択 ラジオボタン */
	private Button rdoSelectedDictionary;
	/** アクティブ辞書 ラジオボタン */
	private Button rdoActiveDictionary;
	/** 既定辞書 入力フィールド */
	private Text txtFixedDictionary;
	/** 既定辞書 参照ボタン */
	private Button btnFixedDictionary;

	/** 折り返し */
	private Button btnWordwrap;

	/** 辞書ビューとのリンク */
	private Button btnLink;
	/** 辞書ビューとのリンク　背景色 */
	private ColorSelector cslBackColor;
	/** 辞書ビューとのリンク　前景色 */
	private ColorSelector cslForeColor;
	/** 辞書ビューとのリンク　背景色（副キーワード） */
	private ColorSelector cslSubBackColor;
	/** 辞書ビューとのリンク　前景色（副キーワード） */
	private ColorSelector cslSubForeColor;
	/** 辞書ビューとのリンク　背景色（活用形） */
	private ColorSelector cslConjBackColor;
	/** 辞書ビューとのリンク　前景色（活用形） */
	private ColorSelector cslConjForeColor;

	/**
	 * コンストラクタ
	 */
	public SpecPreferencePage() {
		setPreferenceStore(ModelToolSpecPlugin.getDefault().getPreferenceStore());
		setDescription(Messages.SpecPreferencePage_0);
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

		// 正規表現設定
		btnWordwrap = new Button(composite, SWT.CHECK | SWT.LEFT);
		btnWordwrap.setFont(parent.getFont());
		btnWordwrap.setText(Messages.SpecPreferencePage_1);

		// 辞書登録設定エリアの作成
		createRegistrationArea(composite);

		// 辞書ビューとのリンク
//		btnLink = new Button(composite, SWT.CHECK | SWT.LEFT);
//		btnLink.setFont(parent.getFont());
//		btnLink.setText("辞書ビューで選択したキーワードを強調表示する。");
		createLinkArea(composite);

		// リスナーの設定
		setListener();

		// 初期値の設定
		setInitialValue();

		return composite;
	}

	/**
	 * 辞書登録設定エリアの作成
	 * @param control
	 * @return
	 */
	protected Control createRegistrationArea(Composite parent) {

		// ラジオボタングループ（登録辞書の指定方法）
		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.SpecPreferencePage_2);
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridLayout layout= new GridLayout();
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 0;
		group.setLayout(layout);

		// ラジオボタン（既定辞書）
		rdoFixedDictionary = new Button(group, SWT.RADIO);
		rdoFixedDictionary.setText(Messages.SpecPreferencePage_3);
		rdoFixedDictionary.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

		// テキストボックス（既定辞書）
		txtFixedDictionary = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		txtFixedDictionary.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_LIST_BACKGROUND));
		txtFixedDictionary.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		// ボタン（既定辞書 参照）
		btnFixedDictionary = new Button(group, SWT.NONE);
		btnFixedDictionary.setText(Messages.SpecPreferencePage_4);
		btnFixedDictionary.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		Point buttonSize = btnFixedDictionary.computeSize(SWT.DEFAULT, SWT.DEFAULT);

		// ラジオボタン（登録時指定）
		rdoSelectedDictionary = new Button(group, SWT.RADIO);
		rdoSelectedDictionary.setText(Messages.SpecPreferencePage_5);
		GridData gd1 = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gd1.heightHint = buttonSize.y;
		rdoSelectedDictionary.setLayoutData(gd1);

		// ラジオボタン（アクティブな辞書に登録）
		rdoActiveDictionary = new Button(group, SWT.RADIO);
		rdoActiveDictionary.setText(Messages.SpecPreferencePage_6);
		GridData gd2 = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gd2.heightHint = buttonSize.y;
		rdoActiveDictionary.setLayoutData(gd2);

		return group;
	}

	/**
	 * 辞書ビューリンク設定エリアの作成
	 * @param control
	 * @return
	 */
	protected Control createLinkArea(Composite parent) {

		// ラジオボタングループ
		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.SpecPreferencePage_9);
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridLayout layout= new GridLayout();
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 0;
		group.setLayout(layout);

		// 辞書ビューとのリンク
		btnLink = new Button(group, SWT.CHECK | SWT.LEFT);
		btnLink.setFont(parent.getFont());
		btnLink.setText(Messages.SpecPreferencePage_10);
		GridData gdLink = new GridData();
		gdLink.horizontalSpan = 3;
		btnLink.setLayoutData(gdLink);

		new Label(group, SWT.NONE).setText(""); //$NON-NLS-1$
		new Label(group, SWT.NONE).setText(Messages.SpecPreferencePage_11);
		new Label(group, SWT.NONE).setText(Messages.SpecPreferencePage_13);

		// キーワード
		Label lblBackColor = new Label(group, SWT.NONE);
		lblBackColor.setText(Messages.SpecPreferencePage_14);
		cslBackColor = new ColorSelector(group);
		cslForeColor = new ColorSelector(group);

		// 副キーワード
		Label lblSubBackColor = new Label(group, SWT.NONE);
		lblSubBackColor.setText(Messages.SpecPreferencePage_15);
		cslSubBackColor = new ColorSelector(group);
		cslSubForeColor = new ColorSelector(group);

		// 活用形
		Label lblConjBackColor = new Label(group, SWT.NONE);
		lblConjBackColor.setText(Messages.SpecPreferencePage_16);
		cslConjBackColor = new ColorSelector(group);
		cslConjForeColor = new ColorSelector(group);

		return group;
	}

	/**
	 * リスナー設定
	 */
	protected void setListener() {

		// 既定辞書 参照ボタン
		btnFixedDictionary.addSelectionListener(new DictionaryButtonListener());

		// ラジオボタン
		SelectionAdapter radioButtonListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				txtFixedDictionary.setEnabled(rdoFixedDictionary.getSelection());
				btnFixedDictionary.setEnabled(rdoFixedDictionary.getSelection());

				// 妥当性チェック
				checkState();
			}
		};
		rdoFixedDictionary.addSelectionListener(radioButtonListener);
		rdoSelectedDictionary.addSelectionListener(radioButtonListener);
		rdoActiveDictionary.addSelectionListener(radioButtonListener);
	}

	/**
	 * 辞書選択ボタンリスナー
	 *
	 * @author KBK yoshimura
	 */
	private class DictionaryButtonListener extends SelectionAdapter {

		/**
		 * コントロールが選択されたときの処理
		 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			// 辞書選択ダイアログ
			ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
					getShell(), new WorkbenchLabelProvider(), new WorkbenchContentProvider());
			dialog.setTitle(Messages.SpecPreferencePage_7);
			dialog.setMessage(Messages.SpecPreferencePage_8);
			// フィルターの設定
			dialog.addFilter(new DictionaryViewerFilter());

			// 複数選択不可
			dialog.setAllowMultiple(false);
			// ヘルプ非表示
			dialog.setHelpAvailable(false);
			// ワークスペースのルートの配下を表示
			dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());

			// 初期フォーカスの設定
			if (txtFixedDictionary.getText().isEmpty() == false) {
				IPath path = new Path(txtFixedDictionary.getText());
				IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
//				IFile file = root.getFileForLocation(path);
				IFile file = root.getFile(path);
				dialog.setInitialSelection(file);
			}

			// バリデータの設定
			dialog.setValidator(new ISelectionStatusValidator() {
				public IStatus validate(Object[] selection) {
					if (selection.length > 0 && selection[0] instanceof IFile) {
						return new Status(IStatus.OK, ModelToolSpecPlugin.PLUGIN_ID, ""); //$NON-NLS-1$
					} else {
						return new Status(IStatus.ERROR, ModelToolSpecPlugin.PLUGIN_ID, ""); //$NON-NLS-1$
					}
				}
			});

			// ダイアログを開く
			if (dialog.open() == Dialog.OK) {
				// 結果をテキストボックスにセット
//				txtFixedDictionary.setText(((IResource)dialog.getFirstResult()).getLocation().toString());
				txtFixedDictionary.setText(((IResource)dialog.getFirstResult()).getFullPath().toString());
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

		// 折り返し設定
		btnWordwrap.setSelection(store.getBoolean(PK_SPECEDITOR_WORDWRAP));

		// 登録辞書の指定方法
		boolean fixed = false;
		switch (store.getInt(PK_REGISTER_DICTIONARY)) {
			case PV_REGISTER_FIXED:
				fixed = true;
				rdoFixedDictionary.setSelection(true);
				txtFixedDictionary.setText(store.getString(PK_REGISTER_FIXED_PATH));
				break;

			case PV_REGISTER_SELECT:
				rdoSelectedDictionary.setSelection(true);
				break;

			case PV_REGISTER_ACTIVE:
				rdoActiveDictionary.setSelection(true);
				break;

			default:
				break;
		}
		txtFixedDictionary.setEnabled(fixed);
		btnFixedDictionary.setEnabled(fixed);

		// 辞書ビューとのリンク
		btnLink.setSelection(store.getBoolean(PK_LINK_DICTIONARY));
		cslBackColor.setColorValue(
				PreferenceConverter.getColor(store, PK_SPEC_HIGHLIGHT_BACKCOLOR));
		cslForeColor.setColorValue(
				PreferenceConverter.getColor(store, PK_SPEC_HIGHLIGHT_FORECOLOR));
		cslSubBackColor.setColorValue(
				PreferenceConverter.getColor(store, PK_SPEC_SUB_HIGHLIGHT_BACKCOLOR));
		cslSubForeColor.setColorValue(
				PreferenceConverter.getColor(store, PK_SPEC_SUB_HIGHLIGHT_FORECOLOR));
		cslConjBackColor.setColorValue(
				PreferenceConverter.getColor(store, PK_SPEC_CONJ_HIGHLIGHT_BACKCOLOR));
		cslConjForeColor.setColorValue(
				PreferenceConverter.getColor(store, PK_SPEC_CONJ_HIGHLIGHT_FORECOLOR));

		// 妥当性チェック
		checkState();
	}

	/**
	 * 妥当性チェック
	 */
	protected void checkState() {

		boolean valid = true;
		String msg = null;

		if (rdoFixedDictionary.getSelection()) {
			if ("".equals(txtFixedDictionary.getText())) { //$NON-NLS-1$
				msg = Messages.SpecPreferencePage_12;
				valid = false;
			}
		}
		setErrorMessage(msg);
		setValid(valid);
	}

	/**
	 * 初期化
	 */
	public void init(IWorkbench workbench) {

		// プリファレンスストアのセット
		setPreferenceStore(ModelToolSpecPlugin.getDefault().getPreferenceStore());
	}

	/**
	 * デフォルトボタン押下時の処理
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults() {

		IPreferenceStore store = getPreferenceStore();

		// プリファレンスの値より初期状態を設定

		// 登録辞書の指定方法
		switch (store.getDefaultInt(PK_REGISTER_DICTIONARY)) {
			case PV_REGISTER_FIXED:
				rdoFixedDictionary.setSelection(true);
				txtFixedDictionary.setText(store.getDefaultString(PK_REGISTER_FIXED_PATH));
				rdoSelectedDictionary.setSelection(false);
				rdoActiveDictionary.setSelection(false);
				break;

			case PV_REGISTER_SELECT:
				rdoSelectedDictionary.setSelection(true);
				rdoFixedDictionary.setSelection(false);
				rdoActiveDictionary.setSelection(false);
				break;

			case PV_REGISTER_ACTIVE:
				rdoActiveDictionary.setSelection(true);
				rdoFixedDictionary.setSelection(false);
				rdoSelectedDictionary.setSelection(false);
				break;

			default:
				break;
		}

		// コントロールの制御
		txtFixedDictionary.setEnabled(rdoFixedDictionary.getSelection());
		btnFixedDictionary.setEnabled(rdoFixedDictionary.getSelection());

		// 辞書ビューとのリンク
		btnLink.setSelection(store.getDefaultBoolean(PK_LINK_DICTIONARY));
		cslBackColor.setColorValue(PreferenceConverter.getDefaultColor(store, PK_SPEC_HIGHLIGHT_BACKCOLOR));
		cslForeColor.setColorValue(PreferenceConverter.getDefaultColor(store, PK_SPEC_HIGHLIGHT_FORECOLOR));
		cslSubBackColor.setColorValue(PreferenceConverter.getDefaultColor(store, PK_SPEC_SUB_HIGHLIGHT_BACKCOLOR));
		cslSubForeColor.setColorValue(PreferenceConverter.getDefaultColor(store, PK_SPEC_SUB_HIGHLIGHT_FORECOLOR));
		cslConjBackColor.setColorValue(PreferenceConverter.getDefaultColor(store, PK_SPEC_CONJ_HIGHLIGHT_BACKCOLOR));
		cslConjForeColor.setColorValue(PreferenceConverter.getDefaultColor(store, PK_SPEC_CONJ_HIGHLIGHT_FORECOLOR));
	}

	/**
	 * OKボタン押下時の処理
	 */
	public boolean performOk() {

		IPreferenceStore store = getPreferenceStore();

		// 折り返し設定
		store.setValue(PK_SPECEDITOR_WORDWRAP, btnWordwrap.getSelection());
		// 折り返しトグルボタンへの設定値の反映
		ModelToolSpecPlugin.getHandlerActivationManager().changeToggleState(SpecConstants.COMMAND_ID_FOLDING,
				btnWordwrap.getSelection());

		// 登録辞書の指定方法
		if (rdoFixedDictionary.getSelection()) {
			store.setValue(PK_REGISTER_DICTIONARY, PV_REGISTER_FIXED);
			store.setValue(PK_REGISTER_FIXED_PATH, txtFixedDictionary.getText());

		} else if (rdoSelectedDictionary.getSelection()) {
			store.setValue(PK_REGISTER_DICTIONARY, PV_REGISTER_SELECT);
			store.setToDefault(PK_REGISTER_FIXED_PATH);

		} else if (rdoActiveDictionary.getSelection()) {
			store.setValue(PK_REGISTER_DICTIONARY, PV_REGISTER_ACTIVE);
			store.setToDefault(PK_REGISTER_FIXED_PATH);
		}

		// 辞書ビューとのリンク
		store.setValue(PK_LINK_DICTIONARY, btnLink.getSelection());
		ModelToolSpecPlugin.getHandlerActivationManager().changeToggleState(SpecConstants.COMMAND_ID_LINK,
				btnLink.getSelection());
		PreferenceConverter.setValue(store, PK_SPEC_HIGHLIGHT_BACKCOLOR, cslBackColor.getColorValue());
		PreferenceConverter.setValue(store, PK_SPEC_HIGHLIGHT_FORECOLOR, cslForeColor.getColorValue());
		PreferenceConverter.setValue(store, PK_SPEC_SUB_HIGHLIGHT_BACKCOLOR, cslSubBackColor.getColorValue());
		PreferenceConverter.setValue(store, PK_SPEC_SUB_HIGHLIGHT_FORECOLOR, cslSubForeColor.getColorValue());
		PreferenceConverter.setValue(store, PK_SPEC_CONJ_HIGHLIGHT_BACKCOLOR, cslConjBackColor.getColorValue());
		PreferenceConverter.setValue(store, PK_SPEC_CONJ_HIGHLIGHT_FORECOLOR, cslConjForeColor.getColorValue());

		return true;
	}
}