package jp.ac.kyushu_u.csce.modeltool.modeltoolForOverture.wizard;

import java.util.Locale;
import java.util.Map;

import jp.ac.kyushu_u.csce.modeltool.dictionary.ModelToolDictionaryPlugin;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryClass;
import jp.ac.kyushu_u.csce.modeltool.dictionary.utility.LanguageUtil;
import jp.ac.kyushu_u.csce.modeltool.modeltoolForOverture.Messages;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class WizardNewDictionaryCreationSettingPage extends WizardPage {

	/** 問題領域テキスト */
	private Text txtDomain;
	/** プロジェクト名テキスト */
	private Text txtProject;
	/** 出力モデルテキスト(read-only) */
	private Text txtModel;
	/** 入力言語テーブル */
	private Table tblLanguage;

	/**
	 * コンストラクタ
	 * @param pageName ページ名
	 */
	protected WizardNewDictionaryCreationSettingPage(String pageName) {
		super(pageName);
	}

	/*
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		// top level group
		Composite topLevel = new Composite(parent, SWT.NONE);
		topLevel.setLayout(new GridLayout());
		topLevel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL));
		topLevel.setFont(parent.getFont());

		Composite composite = new Composite(topLevel, SWT.NONE);
		GridLayout cmpInfoLayout = new GridLayout(2, false);
		composite.setLayout(cmpInfoLayout);
		composite.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL));

		// 問題領域
		Label lblDomain = new Label(composite, SWT.NONE);
		lblDomain.setText(Messages.WizardNewDictionaryCreationSettingPage_0);
		GridData data = new GridData();
		lblDomain.setLayoutData(data);

		txtDomain = new Text(composite, SWT.BORDER | SWT.SINGLE);
		data = new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL);
		txtDomain.setLayoutData(data);

		// プロジェクト名
		Label lblProject = new Label(composite, SWT.NONE);
		lblProject.setText(Messages.WizardNewDictionaryCreationSettingPage_1);
		data = new GridData();
		lblProject.setLayoutData(data);

		txtProject = new Text(composite, SWT.BORDER | SWT.SINGLE);
		data = new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL);
		txtProject.setLayoutData(data);

		// 出力モデル
		Label lblModel = new Label(composite, SWT.NONE);
		lblModel.setText(Messages.WizardNewDictionaryCreationSettingPage_2);
		data = new GridData();
		lblModel.setLayoutData(data);

		txtModel = new Text(composite, SWT.NONE | SWT.READ_ONLY);
		data = new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL);
		txtModel.setLayoutData(data);

		// 出力言語
		createLanguageArea(composite);

		setErrorMessage(null);
		setMessage(null);
		setControl(topLevel);
	}

	/**
	 * 入力言語設定エリアの作成
	 * @param composite コンポジット
	 */
	public void createLanguageArea(Composite composite) {
		LanguageUtil languageUtil = ModelToolDictionaryPlugin.getLanguageUtil();

		Group grpLanguages = new Group(composite, SWT.NONE);
		GridLayout grpInfoLayout = new GridLayout(1, false);
		grpLanguages.setLayout(grpInfoLayout);
		GridData data = new GridData(GridData.GRAB_HORIZONTAL
				| GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 2;
		grpLanguages.setLayoutData(data);
		grpLanguages.setText(Messages.WizardNewDictionaryCreationSettingPage_3);

		tblLanguage = new Table(grpLanguages,
				SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		data = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		data.minimumHeight = 200;
		tblLanguage.setLayoutData(data);

		TableLayout tblLayout = new TableLayout();
		tblLanguage.setLayout(tblLayout);
		tblLanguage.setHeaderVisible(true);
		tblLanguage.setLinesVisible(true);

		TableColumn col1 = new TableColumn(tblLanguage, SWT.NONE);
		col1.setText(Messages.WizardNewDictionaryCreationSettingPage_4);

		TableColumn col2 = new TableColumn(tblLanguage, SWT.NONE);
		col2.setText(Messages.WizardNewDictionaryCreationSettingPage_5);

		// 入力言語にロケールの言語をセットする
		String locLangCd = Locale.getDefault().getLanguage();
		if (ModelToolDictionaryPlugin.getLanguageUtil().getLanguageMap().containsKey(locLangCd)) {
			String name = languageUtil.getLanguageMap().get(locLangCd);
			new TableItem(tblLanguage, SWT.NONE).setText(new String[]{locLangCd, name});
			tblLanguage.setSelection(0);
		}

		col1.pack();
		col2.pack();
		int col1Width = col1.getWidth();
		int col2Width = col2.getWidth();
		tblLayout.addColumnData(new ColumnWeightData(20, col1Width));
		tblLayout.addColumnData(new ColumnWeightData(80, col2Width));

		Composite cmpLanguageMenu = new Composite(grpLanguages, SWT.NONE);
		GridLayout cmpLanguageMenuLayout = new GridLayout(3, false);
		cmpLanguageMenu.setLayout(cmpLanguageMenuLayout);
		data = new GridData(GridData.VERTICAL_ALIGN_FILL
				| GridData.HORIZONTAL_ALIGN_FILL);
		cmpLanguageMenu.setLayoutData(data);

		Label lblEmpty = new Label(cmpLanguageMenu, SWT.NONE);
		data = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		lblEmpty.setLayoutData(data);

		final Button btnLangDel = new Button(cmpLanguageMenu, SWT.NONE);
		btnLangDel.setText(Messages.WizardNewDictionaryCreationSettingPage_6);
		data = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		data.widthHint = 100;
		btnLangDel.setLayoutData(data);
		btnLangDel.setEnabled(tblLanguage.getItemCount() > 0);

		Label lblLang = new Label(cmpLanguageMenu, SWT.NONE);
		lblLang.setText(Messages.WizardNewDictionaryCreationSettingPage_7);
		lblLang.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

		final Combo cmbLang = new Combo(cmpLanguageMenu, SWT.READ_ONLY);
		cmbLang.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		// コンボボックスにセット
		for (Map.Entry<String, String> p : languageUtil.getReverseMap().entrySet()) {
			// ロケールの言語は初期状態で追加済みのためコンボボックスから除外
			if (!p.getValue().equals(locLangCd)) {
				cmbLang.add(p.getKey());
			}
			cmbLang.setData(p.getKey(), p.getValue());
		}
		if (cmbLang.getItemCount() > 0) {
			cmbLang.select(0);
		}

		final Button btnLangAdd = new Button(cmpLanguageMenu, SWT.NONE);
		btnLangAdd.setText(Messages.WizardNewDictionaryCreationSettingPage_8);
		data = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		data.widthHint = 100;
		btnLangAdd.setLayoutData(data);
		btnLangAdd.setEnabled(true);

		// 削除ボタン押下処理
		btnLangDel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int index = tblLanguage.getSelectionIndex();
				if (index > -1) {
					TableItem item =tblLanguage.getItem(index);
					String name = item.getText(1);
					tblLanguage.remove(index);
					if (tblLanguage.getItemCount() == 0) {
						btnLangDel.setEnabled(false);
						// Finishボタン 非活性
						setPageComplete(false);
					} else if (index == tblLanguage.getItemCount()) {
						tblLanguage.setSelection(index - 1);
					} else {
						tblLanguage.setSelection(index);
					}

					// コンボボックスへの追加位置を探す
					int cmbCnt = cmbLang.getItemCount();
					for (int i=0; i< cmbCnt; i++) {
						if (name.compareToIgnoreCase(cmbLang.getItem(i)) < 0) {
							cmbLang.add(name, i);
							break;
						}
					}
					if (cmbCnt == cmbLang.getItemCount()) {
						cmbLang.add(name);
					}

					// 追加ボタンの有効化
					if (tblLanguage.getItemCount() < DictionaryConstants.MAX_LANGUAGES_COUNT) {
						btnLangAdd.setEnabled(true);
					}
				}
			}
		});

		// 追加ボタン押下処理
		btnLangAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int index = cmbLang.getSelectionIndex();
				if (index > -1) {
					String name = cmbLang.getItem(index);
					String cd = (String)cmbLang.getData(name);
					new TableItem(tblLanguage, SWT.NONE).setText(new String[]{cd, name});
					cmbLang.remove(index);
					if (index != cmbLang.getItemCount()) {
						cmbLang.select(index);
					} else {
						cmbLang.select(index - 1);
					}
					tblLanguage.select(tblLanguage.getItemCount() - 1);
					tblLanguage.showItem(tblLanguage.getItem(tblLanguage.getItemCount() - 1));
					btnLangDel.setEnabled(true);

					// Finishボタン 活性
					setPageComplete(true);

					// 最大数を超えた入力は不可
					if (tblLanguage.getItemCount() >= DictionaryConstants.MAX_LANGUAGES_COUNT) {
						btnLangAdd.setEnabled(false);
					}
				}
			}
		});

	}

	/**
	 * 出力モデル名の設定
	 * @param modelName
	 */
	void setModelName(String modelName) {
		txtModel.setText(modelName);
	}

	/**
	 * 辞書クラスの取得
	 * @return 辞書クラス(モデル以外)
	 */
	public DictionaryClass getDictionaryClass() {

		DictionaryClass dicClass = new DictionaryClass();

		// 問題領域
		dicClass.domain = txtDomain.getText();
		// プロジェクト名
		dicClass.project = txtProject.getText();
		// 入力言語
		for (TableItem item : tblLanguage.getItems()) {
			dicClass.languages.add(item.getText(0));
		}

		return dicClass;
	}

}
