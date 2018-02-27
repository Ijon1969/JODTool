package jp.ac.kyushu_u.csce.modeltool.spec.dialog;

import static jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper.in;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;
import jp.ac.kyushu_u.csce.modeltool.spec.Messages;
import jp.ac.kyushu_u.csce.modeltool.spec.ModelToolSpecPlugin;
import jp.ac.kyushu_u.csce.modeltool.spec.constant.SpecConstants;
import jp.ac.kyushu_u.csce.modeltool.spec.constant.SpecPreferenceConstants;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * 辞書選択ダイアログ
 * @author KBK yoshimura
 */
public class DictionarySelectionDialog extends Dialog {

	/** 辞書一覧テーブル */
	private Table table;
	/** テーブルビューワー */
	private TableViewer viewer;
	/** チェックボックス */
	private Button checkDefault;

	/** 辞書タブリスト */
	private List<TableTab> tabs;
	/** レコードリスト */
	private List<Record> records;

	/** 選択結果リスト */
	private List<TableTab> result;

	/** 選択モード 1:抽出／2:検査 */
	private int mode;
	/** 選択モード 1:抽出 */
	public static final int MODE_PICKOUT = 1;
	/** 選択モード 2:検査 */
	public static final int MODE_INSPECT = 2;

	/** チェックボックス画像:OFF */
	private Image imgNoCheck;
	/** チェックボックス画像:ON */
	private Image imgCheck;

	/** 「選択」カラム */
	private static final String COLUMN_SELECT = "select"; //$NON-NLS-1$
	/** 「辞書名」カラム */
	private static final String COLUMN_NAME = "name"; //$NON-NLS-1$
	/** 「パス」カラム */
	private static final String COLUMN_PATH = "path"; //$NON-NLS-1$
	/** 「優先順位」カラム */
	private static final String COLUMN_PRIORITY = "priority"; //$NON-NLS-1$

	/** 全選択ボタン値 */
	private boolean allChecked;

	/**
	 * コンストラクタ
	 * @param parentShell
	 * @param mode MODE_PICKOUT(抽出) or MODE_INSPECT(検査)
	 */
	public DictionarySelectionDialog(Shell parentShell, int mode) {
		super(parentShell);
		this.mode = mode;

		records = new ArrayList<Record>();
		result = new ArrayList<TableTab>();

		// アイコンの作成
		ImageDescriptor descriptor =
			ModelToolSpecPlugin.imageDescriptorFromPlugin(ModelToolSpecPlugin.PLUGIN_ID, SpecConstants.IMG_DISABLED);
		imgNoCheck = descriptor.createImage();
		descriptor =
			ModelToolSpecPlugin.imageDescriptorFromPlugin(ModelToolSpecPlugin.PLUGIN_ID, SpecConstants.IMG_ENABLED);
		imgCheck = descriptor.createImage();
	}

	/**
	 * ダイアログエリア生成
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		Composite composite = (Composite)super.createDialogArea(parent);

		// メッセージ
		Label label = new Label(composite, SWT.NONE);
		switch (mode) {
			case MODE_PICKOUT:
				label.setText(Messages.DictionarySelectionDialog_4);
				break;
			case MODE_INSPECT:
				label.setText(Messages.DictionarySelectionDialog_5);
				break;
		}
		GridData data = new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL);
		label.setLayoutData(data);

		// テーブル
		viewer = new TableViewer(composite, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		table = viewer.getTable();
		data = new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_VERTICAL
				| GridData.VERTICAL_ALIGN_FILL);
		table.setLayoutData(data);

		TableLayout layout = new TableLayout();
		table.setLayout(layout);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// カラム設定
		List<String> columnPropertyList = new ArrayList<String>();
		//  選択チェックボックス
		TableColumn colSelect = null;
		if (mode == MODE_INSPECT) {
			colSelect = new TableColumn(table, SWT.NONE);
//			colSelect.setText("");
			allChecked = true;
			colSelect.setImage(imgCheck);
			colSelect.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					Image imgChecked = allChecked? imgNoCheck : imgCheck;
					((TableColumn)e.getSource()).setImage(imgChecked);
					allChecked = !allChecked;
					for (Record record : records) {
						if (record.select && !allChecked) {
							record.priority = 0;
						}
						if (!record.select && allChecked) {
							updatePriority(record, records.size());
						}
						record.select = allChecked;
					}
					viewer.refresh();
					getButton(IDialogConstants.OK_ID).setEnabled(isValid());
				}
			});
			columnPropertyList.add(COLUMN_SELECT);
		}
		//  辞書名
		TableColumn colName = new TableColumn(table, SWT.NONE);
		colName.setText(Messages.DictionarySelectionDialog_6);
		columnPropertyList.add(COLUMN_NAME);
		//  パス
		TableColumn colPath = new TableColumn(table, SWT.NONE);
		colPath.setText(Messages.DictionarySelectionDialog_7);
		columnPropertyList.add(COLUMN_PATH);
		//  優先順位
		if (mode == MODE_INSPECT) {
			TableColumn colPriority = new TableColumn(table, SWT.NONE);
			colPriority.setText(Messages.DictionarySelectionDialog_8);
			columnPropertyList.add(COLUMN_PRIORITY);
		}

		// コンテンツプロバイダー
		viewer.setContentProvider(new ArrayContentProvider());

		// ラベルプロバイダー
		viewer.setLabelProvider(new TableLabelProvider());

		// カラムプロパティー
		viewer.setColumnProperties(columnPropertyList.toArray(new String[0]));

		// 検査モード時
		if (mode == MODE_INSPECT) {

			// セルエディター
			String[] comboItems = new String[records.size() + 1];
			comboItems[0] = ""; //$NON-NLS-1$
			for (int i=1; i<comboItems.length; i++) {
				comboItems[i] = String.valueOf(i);
			}
			CellEditor priorityEditor = new ComboBoxCellEditor(table, comboItems, SWT.READ_ONLY );
			priorityEditor.addListener(new ICellEditorListener() {
				public void editorValueChanged(boolean oldValidState, boolean newValidState) {
//					getButton(IDialogConstants.OK_ID).setEnabled(isValid());
				}
				public void cancelEditor() {
				}
				public void applyEditorValue() {
//					getButton(IDialogConstants.OK_ID).setEnabled(isValid());
				}

			});
			priorityEditor.getControl().setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_LIST_BACKGROUND));

			viewer.setCellEditors(new CellEditor[]{
					new CheckboxCellEditor(table),
					null,
					null,
					priorityEditor});

			// セルモディファイアー
			final TableColumn col = colSelect;
			viewer.setCellModifier(new ICellModifier() {
				public void modify(Object element, String property, Object value) {
					Record record = (Record)((Item)element).getData();
					if (property.equals(COLUMN_SELECT)) {
						boolean selected = (Boolean)value;

						if (selected) {
							updatePriority(record, records.size());
						} else {
							updatePriority(record, 0);
						}

						record.select = selected;

						// 全選択or無選択の場合
						if (allSelected(selected)) {
							allChecked = selected;
							col.setImage(selected? imgCheck : imgNoCheck);
						}
//						viewer.update(record, new String[]{property});
						viewer.update(records.toArray(), new String[]{COLUMN_SELECT, COLUMN_PRIORITY});

					}
					if (property.equals(COLUMN_PRIORITY)) {
//						record.priority = (Integer)value;

						// 優先順位の更新
						updatePriority(record, (Integer)value);

						viewer.update(records.toArray(), new String[]{COLUMN_SELECT, COLUMN_PRIORITY});
					}

					// OKボタン状態の更新
					getButton(IDialogConstants.OK_ID).setEnabled(isValid());
				}
				public Object getValue(Object element, String property) {
					Object obj = null;
					Record record = (Record)element;
					if (property.equals(COLUMN_SELECT)) {
						obj = record.select;
					}
					if (property.equals(COLUMN_PRIORITY)) {
						obj = record.priority;
					}
					return obj;
				}
				public boolean canModify(Object element, String property) {
//					if (PluginHelper.in(property, COLUMN_NAME, COLUMN_PATH)) {
//						return false;
//					}
//					return true;
					return in(property, COLUMN_SELECT, COLUMN_PRIORITY);
				}
			});
		}

		// データのセット
		for (int i=0; i<records.size(); i++) {
			records.get(i).priority = i + 1;
		}
		viewer.setInput(records);

		// カラム幅の設定
		for (int i=0; i<table.getColumnCount(); i++) {
			TableColumn col = table.getColumn(i);
			if (viewer.getColumnProperties()[i].equals(COLUMN_SELECT) == false) {
				col.pack();
				col.setResizable(true);
			} else {
				col.setWidth(27);
				col.setResizable(false);
			}
			if (viewer.getColumnProperties()[i].equals(COLUMN_PRIORITY)) {
				int minimumWidth = viewer.getCellEditors()[i].getLayoutData().minimumWidth;
				if (minimumWidth > col.getWidth()) {
					col.setWidth(minimumWidth);
				}
			}
		}

		// セレクションリスナの追加
		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// 抽出モード時
				if (mode == MODE_PICKOUT) {
					TableItem item = (TableItem)e.item;
					if (item == null) {
//						getButton(IDialogConstants.OK_ID).setEnabled(false);
						checkDefault.setEnabled(false);
						checkDefault.setSelection(false);
					} else {
//						getButton(IDialogConstants.OK_ID).setEnabled(true);
						checkDefault.setEnabled(! "".equals(item.getText(1))); //$NON-NLS-1$
					}
				}

//				// 検査モード時
//				if (mode == MODE_INSPECT) {
//					boolean selected = false;
//					for (Record record : records) {
//						selected = selected || (record.select && record.priority != 0);
//					}
//					getButton(IDialogConstants.OK_ID).setEnabled(selected);
//				}

				// ボタン状態更新
				getButton(IDialogConstants.OK_ID).setEnabled(isValid());
			}
		});

		// 抽出モード時
		if (mode == MODE_PICKOUT) {
			// チェックボックス
			Composite child = new Composite(composite, SWT.NONE);
			data = new GridData(GridData.GRAB_HORIZONTAL
					| GridData.HORIZONTAL_ALIGN_FILL);
			child.setLayoutData(data);
			child.setLayout(new GridLayout(2, false));

			checkDefault = new Button(child, SWT.CHECK);
			checkDefault.setEnabled(false);
			checkDefault.setSelection(false);
			checkDefault.setText(Messages.DictionarySelectionDialog_11);
		}

		return composite;
	}

	/**
	 * 優先順位の更新
	 * @param record 対象レコード
	 * @param value 更新する値
	 */
	protected void updatePriority(Record record, int value) {

		// 変更前後の値を取得
		int oldValue = record.priority;
		int newValue = value;

		// 値の変化がない場合処理なし
		if (oldValue == newValue) {
			return;
		}

		// パターン１：変更後＝０
		if (newValue == 0) {
			for (Record rec : records) {
				if (rec.priority > oldValue) {
					rec.priority = rec.priority - 1;
				}
			}
			record.select = false;
		}

		// パターン２：変更前＝０
		else if (oldValue == 0) {
			int max = 0;
			for (Record rec : records) {
				if (rec.priority >= newValue) {
					rec.priority = rec.priority + 1;
				}
				if (max < rec.priority) {
					max = rec.priority;
				}
			}
			if (newValue > max + 1) {
				newValue = max + 1;
			}
			record.select = true;
		}

		// パターン３：変更後＞変更前
		else if (newValue > oldValue) {
			int max = 0;
			for (Record rec : records) {
				if (rec.priority > oldValue && rec.priority <= newValue) {
					rec.priority = rec.priority - 1;
				}
				if (max < rec.priority) {
					max = rec.priority;
				}
			}
			if (oldValue == max) {
				newValue = max;
			} else if (newValue > max + 1) {
				newValue = max + 1;
			}
		}

		// パターン４：変更後＜変更前
		else if (newValue < oldValue) {
			for (Record rec : records) {
				if (rec.priority >= newValue && rec.priority < oldValue) {
					rec.priority = rec.priority + 1;
				}
			}
		}

		// 変更後の値をレコードにセット
		record.priority = newValue;
	}

	/**
	 * ボタンバーへボタン追加
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		Button ok = createButton(
				parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		ok.setEnabled(isValid());
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * 選択内容の検証
	 * @return
	 */
	protected boolean isValid() {

		// 抽出モード時
		if (mode == MODE_PICKOUT) {
			// 選択ありの場合true
			return table.getSelectionCount() > 0;
		}

		// 検査モード時
		if (mode == MODE_INSPECT) {
			// 選択ありで優先順位指定ありの場合true
			boolean selected = false;
			for (Record record : records) {
				selected = selected || (record.select && record.priority != 0);
			}
			return selected;
		}

		return false;
	}

	/**
	 * テーブルの全レコードが選択or非選択かどうかチェックする
	 * @param selected true:選択／false:非選択
	 * @return 全レコードが引数の選択状態の場合のみtrueを返す
	 */
	private boolean allSelected(boolean selected) {

		for (Record record : records) {
			if (record.select != selected) {
				return false;
			}
		}
		return true;
	}

	/**
	 * シェル設定
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell shell) {
		// ダイアログタイトル設定
		super.configureShell(shell);
		shell.setText(Messages.DictionarySelectionDialog_12);
	}

	/**
	 * サイズ変更可／不可判定
	 * @see org.eclipse.jface.dialogs.Dialog#isResizable()
	 */
	protected boolean isResizable() {
		// サイズ変更可
		return true;
	}

	/**
	 * OKボタン押下処理
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {

		// 抽出モード時
		if (mode == MODE_PICKOUT) {
			if (table.getSelectionCount() < 1) {
				MessageDialog.openWarning(getShell(), Messages.DictionarySelectionDialog_13, Messages.DictionarySelectionDialog_14);
				return;
			}
			if (checkDefault.getSelection() && checkDefault.getEnabled()) {
				// プリファレンスストアの取得
				IPreferenceStore store = ModelToolSpecPlugin.getDefault().getPreferenceStore();
				store.setValue(SpecPreferenceConstants.PK_REGISTER_DICTIONARY,
						SpecPreferenceConstants.PV_REGISTER_FIXED);
//				store.setValue(PreferenceConstants.PK_RGSTR_FIXED_DIC_PATH, dictionaries[table.getSelectionIndex()][2]);
				Record record = (Record)((IStructuredSelection)viewer.getSelection()).getFirstElement();
				store.setValue(SpecPreferenceConstants.PK_REGISTER_FIXED_PATH,
//						record.tab.getFile().getLocation().toString());
						record.tab.getFile().getFullPath().toString());
			}
		}

		// 選択結果の設定
		setResult();

		super.okPressed();
	}

	/**
	 * 初期サイズ取得
	 * @see org.eclipse.jface.dialogs.Dialog#getInitialSize()
	 */
	@Override
	protected Point getInitialSize() {
		// 初期ｻｲｽﾞ設定
		Point point = super.getInitialSize();
		point.y = 400;
		return point;
	}

	/**
	 * 辞書一覧の設定
	 * @param dictionaries
	 */
	public void setDictionaries(List<TableTab> tabs) {
		this.tabs = tabs;
		convertTableTab2Record();
	}

	/**
	 * 辞書タブを一覧レコードに変換
	 */
	private void convertTableTab2Record() {
		records.clear();
		for (TableTab tab : tabs) {
			Record record = new Record();
			record.name = tab.getDictionaryName();
			if (tab.getFile() != null) {
				record.path = tab.getFile().getFullPath().toString();
			}
			record.tab = tab;
			records.add(record);
		}
	}

	/**
	 * 選択結果の設定
	 */
	private void setResult() {

		result.clear();

		// 抽出モード時
		if (mode == MODE_PICKOUT) {
			// 選択された一つのタブを返す
			Record record = (Record)((IStructuredSelection)viewer.getSelection()).getFirstElement();
			result.add(record.tab);
		}

		// 検査モード時
		if (mode == MODE_INSPECT) {
			// 選択されたタブを優先順位でソートして返す
			Collections.sort(records, new Comparator<Record>() {
				public int compare(Record o1, Record o2) {
					return o1.priority - o2.priority;
				}
			});
			for (Record record : records) {
				if (record.select && record.priority != 0) {
					result.add(record.tab);
				}
			}
		}
	}

	/**
	 * 選択結果の取得
	 * @return 選択結果タブリスト
	 */
	public List<TableTab> getResult() {
		return result;
	}

	/**
	 * 内部クラス
	 * @author yoshimura
	 */
	private class Record {
		/** 選択 */
		boolean select = true;
		/** 辞書名 */
		String name = ""; //$NON-NLS-1$
		/** パス（相対パス） */
		String path = ""; //$NON-NLS-1$
		/** 優先順位 */
		int priority = 0;
		/** 辞書タブ */
		TableTab tab;
	}

	/**
	 * 内部クラス テーブルラベルプロバイダー
	 * @author yoshimura
	 */
	private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
		/** カラム画像 */
		public Image getColumnImage(Object element, int columnIndex) {
			Record record = (Record)element;
			Image img = null;
			// 検査モード時
			if (mode == MODE_INSPECT) {
				if (columnIndex == 0) {
					img = record.select? imgCheck : imgNoCheck;
				}
			}
			return img;
		}
		/** カラムテキスト */
		public String getColumnText(Object element, int columnIndex) {
			Record record = (Record)element;
			String text = ""; //$NON-NLS-1$

//			// 抽出モード時
//			if (mode == MODE_PICKOUT) {
//				switch (columnIndex) {
//					case 0:
//						text = record.name;
//						break;
//					case 1:
//						text = record.path;
//						break;
//				}
//			}
//
//			// 検査モード時
//			if (mode == MODE_INSPECT) {
//				switch (columnIndex) {
//					case 1:
//						text = record.name;
//						break;
//					case 2:
//						text = record.path;
//						break;
//					case 3:
//						if (record.priority != 0) {
//							text = String.valueOf(record.priority);
//						}
//				}
//			}

			String property = (String)viewer.getColumnProperties()[columnIndex];
			if (COLUMN_SELECT.equals(property)) {
				// 処理なし
			}
			if (COLUMN_NAME.equals(property)) {
				text = record.name;
			}
			if (COLUMN_PATH.equals(property)) {
				text = record.path;
			}
			if (COLUMN_PRIORITY.equals(property)) {
				if (record.priority != 0) {
					text = String.valueOf(record.priority);
				}
			}

			return text;
		}
	}
}
