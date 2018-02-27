package jp.ac.kyushu_u.csce.modeltool.dictionary.dialog;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jp.ac.kyushu_u.csce.modeltool.base.ModelToolBasePlugin;
import jp.ac.kyushu_u.csce.modeltool.base.constant.BasePreferenceConstants;
import jp.ac.kyushu_u.csce.modeltool.base.dialog.CInputDialog;
import jp.ac.kyushu_u.csce.modeltool.base.utility.ColorName;
import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;
import jp.ac.kyushu_u.csce.modeltool.dictionary.Messages;
import jp.ac.kyushu_u.csce.modeltool.dictionary.ModelToolDictionaryPlugin;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryClass;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionarySetting;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionarySetting.Category;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.HandlerUpdater;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.ModelManager;
import jp.ac.kyushu_u.csce.modeltool.dictionary.utility.LanguageUtil;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * 辞書情報を表示するダイアログ
 *
 * @author KBK yoshimura
 */
public class DictionaryInformationDialog extends Dialog {

	private Text txtDomain;
	private Text txtProject;
	private Text txtLanguage;
//	private Text txtModel;
	private Combo cmbModel;

	private Table tblSubtotal;
	private int total;
	private String bkLanguage;

	Button chkSpecificCategory;
	TableViewer tvCategory;
	Button btnDefaultCategory;
//	ColorSelector csPrimary;
//	ColorSelector csSecondary;

	/** タブフォルダー */
	private TabFolder folder;
	/** 最後に開いたタブのインデックス */
	private static int lastTabIndex = 0;
	/** タブフォルダーのセレクションリスナー */
	SelectionListener tabListener;

//	/** 種別デフォルトフラグ */
//	private boolean isDefaultCategory;

	/**  */
	private static final int NUM_COLUMNS = 2;

	/** 言語情報タブのタブインデックス */
	private static final int TAB_IDX_LANGUAGE = 3;

	/** 辞書タブ */
	private TableTab tab;

	/** プリファレンスストア（base） */
	private IPreferenceStore storeBase;

	/**
	 * 種別設定
	 */
	List<CategoryItem> catItems;

	Map<CategoryItem, Image[]> colorImageMap;

	/** 変更可能フラグ */
	private boolean canEdit;

	/**
	 * コンストラクタ
	 * @param parentShell
	 */
	private DictionaryInformationDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * コンストラクタ
	 * @param parentShell
	 */
	private DictionaryInformationDialog(IShellProvider parentShell) {
		super(parentShell);
	}

	/**
	 * コンストラクタ
	 * @param parentShell
	 * @param tab
	 */
	public DictionaryInformationDialog(Shell parentShell, TableTab tab) {
		super(parentShell);
		Assert.isNotNull(tab);
		this.tab = tab;

		canEdit = ModelManager.getInstance().isResisteredModel(
				tab.getDictionary().getDictionaryClass().model);

		storeBase = ModelToolBasePlugin.getDefault().getPreferenceStore();

		catItems = new ArrayList<CategoryItem>();
		colorImageMap = new LinkedHashMap<CategoryItem, Image[]>();
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite composite = (Composite)super.createDialogArea(parent);
		GridLayout compLayout = (GridLayout)composite.getLayout();
		compLayout.numColumns = 2;
		compLayout.verticalSpacing = 4;

		// 辞書名
		Label lblDicName = new Label(composite, SWT.NONE);
		lblDicName.setText(Messages.DictionaryInformationDialog_0);
		GridData data = new GridData(SWT.NONE);
		lblDicName.setLayoutData(data);

		Text txtDicName = new Text(composite, SWT.READ_ONLY | SWT.WRAP);
		data = new GridData(GridData.GRAB_HORIZONTAL);
		txtDicName.setLayoutData(data);
		txtDicName.setText(tab.getDictionaryName());

		// ファイル名
		Label lblFileName = new Label(composite, SWT.NONE);
		lblFileName.setText(Messages.DictionaryInformationDialog_1);
		data = new GridData(SWT.NONE);
		lblFileName.setLayoutData(data);

		Text txtFileName = new Text(composite, SWT.READ_ONLY | SWT.WRAP);
		data = new GridData(GridData.GRAB_HORIZONTAL);
		txtFileName.setLayoutData(data);
		if (tab.getFile() != null) {
			txtFileName.setText(tab.getFile().getName());
		}

		// パス
		Label lblPath = new Label(composite, SWT.NONE);
		lblPath.setText(Messages.DictionaryInformationDialog_2);
		data = new GridData(SWT.NONE);
		lblPath.setLayoutData(data);

		Text txtPath = new Text(composite, SWT.READ_ONLY | SWT.WRAP);
		data = new GridData(GridData.GRAB_HORIZONTAL);
		txtPath.setLayoutData(data);
		if (tab.getFile() != null) {
			txtPath.setText(PluginHelper.getRelativePath(tab.getFile()));
		}

		// 空行
		Label lblEmpty = new Label(composite, SWT.NONE);
		lblEmpty.setText(""); //$NON-NLS-1$
		data = new GridData(SWT.NONE);
		data.horizontalSpan = 2;
		data.heightHint = 5;
		lblEmpty.setLayoutData(data);

		// タブフォルダ
		folder = new TabFolder(composite, SWT.NONE);
		data = new GridData(GridData.GRAB_HORIZONTAL
				| GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_FILL);
		data.horizontalSpan = 2;
		folder.setLayoutData(data);

		// 登録件数タブ
		/*TabItem itmTotal =*/ createTotalTab(folder);

		// 種別情報タブ
		createCategoryTab(folder);

		// 辞書情報タブ
		/*TabItem itmInfo =*/ createInfoTab(folder);

		// 入力言語タブ
		createLanguageTab(folder);

		// 最後に開いたタブを開く
		folder.setSelection(lastTabIndex);

		// リスナーの追加
		tabListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// 選択されたタブインデックスを退避
				lastTabIndex = folder.getSelectionIndex();
			}
		};
		folder.addSelectionListener(tabListener);

		// タブをフォーカス
		folder.setFocus();

		return composite;
	}

	/**
	 * 登録件数タブの作成
	 * @param folder
	 */
	protected TabItem createTotalTab(TabFolder folder) {

		// 登録件数タブ
		TabItem item = new TabItem(folder, SWT.BORDER);
		Composite cmpTotal = new Composite(folder, SWT.NONE);
		GridLayout cmpTotalLayout = new GridLayout(2, false);
		cmpTotal.setLayout(cmpTotalLayout);
		item.setControl(cmpTotal);
		item.setText(Messages.DictionaryInformationDialog_4);
		Composite composite = cmpTotal;

		// 登録件数
		Label lblTotal = new Label(composite, SWT.NONE);
		lblTotal.setText(Messages.DictionaryInformationDialog_5);
		GridData data = new GridData(SWT.NONE);
		lblTotal.setLayoutData(data);

		Text txtTotal = new Text(composite, SWT.READ_ONLY | SWT.WRAP);
		data = new GridData(GridData.GRAB_HORIZONTAL);
		txtTotal.setLayoutData(data);

		// 種別内訳
		Label lblSubtotal = new Label(composite, SWT.NONE);
		lblSubtotal.setText(Messages.DictionaryInformationDialog_6);
		data = new GridData(SWT.NONE);
		lblSubtotal.setLayoutData(data);

		Button btnCopy = new Button(composite, SWT.NONE);
		data = new GridData(SWT.END, SWT.CENTER, true, false);
		btnCopy.setLayoutData(data);
		btnCopy.setText(Messages.DictionaryInformationDialog_7);
		btnCopy.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				copy();
			}
		});

		// 登録件数の集計
		Map<Object, Integer> map = tab.getCategorySubtotal();
		int subtotalNone = map.containsKey(DictionaryConstants.CATEGORY_NO_NONE)?
				map.get(DictionaryConstants.CATEGORY_NO_NONE) : 0;
		int subtotalNoun = map.containsKey(DictionaryConstants.CATEGORY_NO_NOUN)?
				map.get(DictionaryConstants.CATEGORY_NO_NOUN) : 0;
		int subtotalVerb = map.containsKey(DictionaryConstants.CATEGORY_NO_VERB)?
				map.get(DictionaryConstants.CATEGORY_NO_VERB) : 0;
		int subtotalState = map.containsKey(DictionaryConstants.CATEGORY_NO_STATE)?
				map.get(DictionaryConstants.CATEGORY_NO_STATE) : 0;
		total = subtotalNone + subtotalNoun + subtotalVerb + subtotalState;

		tblSubtotal = new Table(composite, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
//		data = new GridData(GridData.GRAB_HORIZONTAL
//				| GridData.HORIZONTAL_ALIGN_FILL
//				| GridData.GRAB_VERTICAL
//				| GridData.VERTICAL_ALIGN_FILL);
//		data.horizontalSpan = 2;
		data = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		tblSubtotal.setLayoutData(data);

		TableLayout tblLayout = new TableLayout();
		tblSubtotal.setLayout(tblLayout);
		tblSubtotal.setHeaderVisible(true);
		tblSubtotal.setLinesVisible(true);

		TableColumn col1 = new TableColumn(tblSubtotal, SWT.NONE);
		col1.setText(Messages.DictionaryInformationDialog_8);
		col1.setWidth(100);

		TableColumn col2 = new TableColumn(tblSubtotal, SWT.RIGHT);
		col2.setText(Messages.DictionaryInformationDialog_9);
		col2.setWidth(100);

		TableItem itemNone = new TableItem(tblSubtotal, SWT.NONE);
		itemNone.setText(new String[]{DictionaryConstants.CATEGORY_NONE2, String.valueOf(subtotalNone)});
//		TableItem itemNoun = new TableItem(tblSubtotal, SWT.NONE);
//		itemNoun.setText(new String[]{DictionaryConstants.CATEGORY_NOUN, String.valueOf(subtotalNoun)});
//		TableItem itemVerb = new TableItem(tblSubtotal, SWT.NONE);
//		itemVerb.setText(new String[]{DictionaryConstants.CATEGORY_VERB, String.valueOf(subtotalVerb)});
//		TableItem itemState = new TableItem(tblSubtotal, SWT.NONE);
//		itemState.setText(new String[]{DictionaryConstants.CATEGORY_STATE, String.valueOf(subtotalState)});

		// 個別設定分の表示
		for (int i=1; i <= DictionaryConstants.MAX_CATEGORY_NO; i++) {
			Category category = tab.getDictionary().getSetting().getCategory(i);
			if (category != null) {
				TableItem itemCat = new TableItem(tblSubtotal, SWT.NONE);
				int count = PluginHelper.nullToZero(map.get(category.getNo()));
				itemCat.setText(new String[]{category.getName(), String.valueOf(count)});
				total += count;
			}
		}

		txtTotal.setText(String.valueOf(total) + Messages.DictionaryInformationDialog_10);

		return item;
	}

	/**
	 * 種別内訳をクリップボードにコピー
	 */
	private void copy() {
		Clipboard clipboard = new Clipboard(getShell().getDisplay());
		StringBuffer sb = new StringBuffer();

		TableItem[] items = tblSubtotal.getItems();
		for (int i=0; i<items.length; i++) {
			TableItem item = items[i];
			for (int j=0; j<NUM_COLUMNS; j++) {
				sb.append(item.getText(j));
				if (j != NUM_COLUMNS - 1) {
					sb.append("\t"); //$NON-NLS-1$
				}
			}
			sb.append("\n"); //$NON-NLS-1$
		}
		sb.append(Messages.DictionaryInformationDialog_13).append("\t").append(total); //$NON-NLS-1$

		String[] data = new String[]{sb.toString()};
		Transfer[] dataTypes = new Transfer[]{TextTransfer.getInstance()};
		clipboard.setContents(data, dataTypes);
		clipboard.dispose();
	}

	/**
	 * 種別情報タブの作成
	 * @param folder
	 * @return
	 */
	protected TabItem createCategoryTab(final TabFolder folder) {

		// ボタン配列
		final Button[] buttons = new Button[3];
		final int ADD = 0;		// 追加
		final int EDIT = 1;	// 編集
		final int REMOVE = 2;	// 除去

		// 変更可能フラグ
		final boolean isEditable = isCategoryEditable();

		// コントロールマネージャー
		final ControlUsageManager cuManager = new ControlUsageManager();

		// タブ作成
		TabItem item = new TabItem(folder, SWT.BORDER);
		Composite composite = new Composite(folder, SWT.NONE);
		GridLayout cmpInfoLayout = new GridLayout(1, false);
		composite.setLayout(cmpInfoLayout);
		item.setControl(composite);
		item.setText(Messages.DictionaryInformationDialog_48);

		// 固有設定有無チェックボックス
		chkSpecificCategory = new Button(composite, SWT.CHECK);
		chkSpecificCategory.setText(Messages.DictionaryInformationDialog_57);
		chkSpecificCategory.setSelection(!tab.getDictionary().getSetting().isDefaultCategory());

		// セパレーター
		Label separator = new Label(composite, SWT.SEPARATOR | SWT.SHADOW_OUT | SWT.HORIZONTAL | SWT.NO_FOCUS | SWT.LEFT_TO_RIGHT);
		separator.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL));

		// 種別設定グループ
		final Group grpCategory = new Group(composite, SWT.NONE);
		grpCategory.setText(Messages.DictionaryInformationDialog_21);
		GridData data = new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_VERTICAL
				| GridData.VERTICAL_ALIGN_FILL);
		grpCategory.setLayoutData(data);
		GridLayout grpLayout = new GridLayout(2, false);
		grpCategory.setLayout(grpLayout);

		// テーブル
		tvCategory = new TableViewer(grpCategory, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		Table tblCategory = tvCategory.getTable();
//		tblCategory = new Table(grpCategory, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		data = new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_VERTICAL
				| GridData.VERTICAL_ALIGN_FILL);
		data.horizontalSpan = 1;
		tblCategory.setLayoutData(data);

		TableLayout layout = new TableLayout();
		tblCategory.setLayout(layout);
		tblCategory.setHeaderVisible(true);
		tblCategory.setLinesVisible(true);

//		tblCategory.addListener(SWT.MeasureItem, new Listener() {
//			public void handleEvent(Event event) {
//				event.height = 25;
//			}
//		});

		// カラム設定
		// 1列目の表示位置が不正となる不具合対応（Windowsのみ？）
		// 1列目に非表示行を追加
		TableColumn col0 = new TableColumn(tblCategory, SWT.NONE);
		col0.setWidth(0);
		col0.setResizable(false);
		// No
		final TableColumn col1 = new TableColumn(tblCategory, SWT.NONE);
		col1.setText(Messages.DictionaryInformationDialog_22);
//		col1.setWidth(30);
		col1.setAlignment(SWT.RIGHT);
		col1.setResizable(false);
//		col1.pack();
		// 種別名
		TableColumn col2 = new TableColumn(tblCategory, SWT.NONE);
		col2.setText(Messages.DictionaryInformationDialog_23);
		col2.setWidth(100);
		// 初回色
		TableColumn col3 = new TableColumn(tblCategory, SWT.NONE);
		col3.setText(Messages.DictionaryInformationDialog_24);
		col3.setResizable(false);
		col3.setWidth(60);
		col3.setAlignment(SWT.CENTER);
		// 2回目以降色
		TableColumn col4 = new TableColumn(tblCategory, SWT.NONE);
		col4.setText(Messages.DictionaryInformationDialog_25);
		col4.setResizable(false);
		col4.setWidth(60);
		col4.setAlignment(SWT.CENTER);

		// コンテンツプロバイダー
		tvCategory.setContentProvider(new ArrayContentProvider());

		// ラベルプロバイダー
		tvCategory.setLabelProvider(new TableLabelProvider());

//		// カラムプロパティー
//		tvCategory.setColumnProperties(new String[]{
//				"blank",
//				"no", //$NON-NLS-1$
//				"name", //$NON-NLS-1$
//				"primary", //$NON-NLS-1$
//				"secondary", //$NON-NLS-1$
//		});
//
//		// セルエディター
//		tvCategory.setCellEditors(new CellEditor[]{
//				null,
//				null,
////				new TextCellEditor(tblCategory, SWT.READ_ONLY),   // TODO:ダイアログでの編集に切り替え
//				null,                                            //       セルエディターは使わない
//				null,
//				null,
//		});
//
//		// セルモディファイアー
//		tvCategory.setCellModifier(new ICellModifier() {
//
//			@Override
//			public void modify(Object element, String property, Object value) {
//				CategoryItem item = (CategoryItem)((TableItem)element).getData();
//				if (property.equals("name")) { //$NON-NLS-1$
//					item.name = (String)value;
//				}
//				tvCategory.update(item, new String[]{property});
//			}
//
//			@Override
//			public Object getValue(Object element, String property) {
//				if (property.equals("name")) { //$NON-NLS-1$
//					return ((CategoryItem)element).name;
//				}
//				return null;
//			}
//
//			@Override
//			public boolean canModify(Object element, String property) {
//				if (property.equals("name")) { //$NON-NLS-1$
//					return true;
//				} else {
//					return false;
//				}
//			}
//		});

		// セレクションリスナーの追加
		tvCategory.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (isEditable) {
					IStructuredSelection selection = (IStructuredSelection)event.getSelection();
					if (!selection.isEmpty()) {
						// 編集ボタン　使用可
						buttons[EDIT].setEnabled(true);
						// 選択されたインデックスの取得
						buttons[REMOVE].setEnabled(true);
					} else {
						buttons[EDIT].setEnabled(false);
						buttons[REMOVE].setEnabled(false);
					}
					if (tvCategory.getTable().getItemCount() >= DictionaryConstants.MAX_CATEGORY_NO) {
						buttons[ADD].setEnabled(false);
					} else {
						buttons[ADD].setEnabled(true);
				}
				}
			}
		});

		// XXX:ダイアログでの編集に切り替えるためコメントアウト
//		tblCategory.addMouseListener(new MouseAdapter() {
//			public void mouseDown(MouseEvent e) {
//				TableItem[] selection = tblCategory.getSelection();
//				if (selection.length != 1) {
//					return;
//				}
//				IStructuredSelection sSelection = (IStructuredSelection)tvCategory.getSelection();
//				CategoryItem cItem = (CategoryItem)sSelection.getFirstElement();
////				csPrimary.setColorValue(cItem.primary);
////				csSecondary.setColorValue(cItem.secondary);
////				setEnabledColorSelector(true);
//
//				TableItem item = selection[0];
//				for (int i = 0; i < tblCategory.getColumnCount(); i++) {
//					if (item.getBounds(i).contains(e.x, e.y)) {
//						if (i == 2) {
//							ColorDialog dialog = new ColorDialog(tblCategory.getShell());
//							dialog.setRGB(cItem.primary);
//							RGB rgb = dialog.open();
//							if (rgb != null) {
//								cItem.primary = rgb;
//								isDefaultCategory = false;
//								tvCategory.update(cItem, null);
//							}
//						}
//						if (i == 3) {
//							ColorDialog dialog = new ColorDialog(tblCategory.getShell());
//							dialog.setRGB(cItem.secondary);
//							RGB rgb = dialog.open();
//							if (rgb != null) {
//								cItem.secondary = rgb;
//								isDefaultCategory = false;
//								tvCategory.update(cItem, null);
//							}
//						}
//						break;
//					}
//				}
//			}
//		});

		// データの追加
		DictionarySetting setting = tab.getDictionary().getSetting();
//		isDefaultCategory = setting.isDefaultCategory();
		boolean isDefaultCategory = setting.isDefaultCategory();
		if (isDefaultCategory) {
			CategoryItem itmNoun = new CategoryItem();
			itmNoun.no = DictionaryConstants.CATEGORY_NO_NOUN;
			itmNoun.name = DictionaryConstants.CATEGORY_NOUN;
			itmNoun.primary = PreferenceConverter.getColor(
					storeBase, BasePreferenceConstants.PK_COLOR_NOUN_FIRST);
			itmNoun.secondary = PreferenceConverter.getColor(
					storeBase, BasePreferenceConstants.PK_COLOR_NOUN);
			catItems.add(itmNoun);

			CategoryItem itmVerb = new CategoryItem();
			itmVerb.no = DictionaryConstants.CATEGORY_NO_VERB;
			itmVerb.name = DictionaryConstants.CATEGORY_VERB;
			itmVerb.primary = PreferenceConverter.getColor(
					storeBase, BasePreferenceConstants.PK_COLOR_VERB_FIRST);
			itmVerb.secondary = PreferenceConverter.getColor(
					storeBase, BasePreferenceConstants.PK_COLOR_VERB);
			catItems.add(itmVerb);

			CategoryItem itmState = new CategoryItem();
			itmState.no = DictionaryConstants.CATEGORY_NO_STATE;
			itmState.name = DictionaryConstants.CATEGORY_STATE;
			itmState.primary = PreferenceConverter.getColor(
					storeBase, BasePreferenceConstants.PK_COLOR_STATE_FIRST);
			itmState.secondary = PreferenceConverter.getColor(
					storeBase, BasePreferenceConstants.PK_COLOR_STATE);
			catItems.add(itmState);

		} else {
//			for (int i=0; i<=DictionarySetting.CATEGORY_COUNT; i++) {
			for (int i=0; i<=DictionaryConstants.MAX_CATEGORY_NO; i++) {
				DictionarySetting.Category category = setting.getCategory(i);
				if (category == null) {
					continue;
				}
				CategoryItem cItem = new CategoryItem();
				cItem.no = category.getNo();
				cItem.name = category.getName();
				cItem.primary = category.getPrimary();
				cItem.secondary = category.getSecondary();

				catItems.add(cItem);
			}
		}

		// データを設定
		tvCategory.setInput(catItems);

		// Noカラム幅調整
		col1.pack();

		// ボタンエリア
		Composite buttonArea = new Composite(grpCategory, SWT.NONE);
		data = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, true);
		buttonArea.setLayoutData(data);
		buttonArea.setLayout(new GridLayout(1, false));

		// 追加ボタン
		Button btnAdd = new Button(buttonArea, SWT.PUSH);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		btnAdd.setLayoutData(data);
		btnAdd.setText(Messages.DictionaryInformationDialog_49);
		buttons[ADD] = btnAdd;

		// 編集ボタン
		Button btnEdit = new Button(buttonArea, SWT.PUSH);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		btnEdit.setLayoutData(data);
		btnEdit.setText(Messages.DictionaryInformationDialog_50);
		buttons[EDIT] = btnEdit;

		// 除去ボタン
		Button btnRemove = new Button(buttonArea, SWT.PUSH);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		btnRemove.setLayoutData(data);
		btnRemove.setText(Messages.DictionaryInformationDialog_51);
		buttons[REMOVE] = btnRemove;

		// 追加ボタン押下時の処理
		buttons[ADD].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				// ダイアログを開いて追加する
				CategoryDialog dialog = new CategoryDialog(getShell(), null);
				int ret = dialog.open();
				if (ret == Dialog.OK) {
					CategoryItem retItem = dialog.getData();
					catItems.add(retItem);
					Collections.sort(catItems, new Comparator<CategoryItem>() {
						public int compare(CategoryItem o1, CategoryItem o2) {
							return (o1.no - o2.no);
						}
					});
					tvCategory.refresh();
					tvCategory.setSelection(new StructuredSelection(retItem), true);
					col1.pack();
					// 最大数に達した場合ボタン押下不可
					if (catItems.size() >= DictionaryConstants.MAX_CATEGORY_NO) {
						buttons[ADD].setEnabled(false);
					}
				}
			}
		});

		// 編集ボタン押下時の処理
		buttons[EDIT].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int index = tvCategory.getTable().getSelectionIndex();
				if (index >= 0) {
					// ダイアログを開いて編集する
					CategoryItem item = (CategoryItem)tvCategory.getElementAt(index);
					CategoryDialog dialog = new CategoryDialog(getShell(), item);
					int ret = dialog.open();
					if (ret == Dialog.OK) {
						CategoryItem retItem = dialog.getData();
						tvCategory.update(retItem, null);
						tvCategory.setSelection(new StructuredSelection(retItem), true);
						col1.pack();
					}
				}
			}
		});

		// 除去ボタン押下時の処理
		buttons[REMOVE].addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int index = tvCategory.getTable().getSelectionIndex();
				if (index >= 0) {
					catItems.remove(index);
					tvCategory.refresh();
					if (tvCategory.getTable().getItemCount() > 0) {
						if (index >= tvCategory.getTable().getItemCount()) {
							index = tvCategory.getTable().getItemCount() - 1;
						}
						tvCategory.setSelection(new StructuredSelection(
								tvCategory.getElementAt(index)), true);
						col1.pack();
					}
					// 追加ボタン押下可能
					buttons[ADD].setEnabled(true);
				}
			}
		});

		// デフォルトボタン
		btnDefaultCategory = new Button(grpCategory, SWT.PUSH);
		data = new GridData(SWT.END, SWT.CENTER, true, false);//GridData.GRAB_VERTICAL | GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_END);
		data.verticalAlignment = SWT.END;
//		data.grabExcessVerticalSpace = true;
		data.horizontalSpan = 2;
		btnDefaultCategory.setLayoutData(data);
		btnDefaultCategory.setText(Messages.DictionaryInformationDialog_33);
		btnDefaultCategory.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setDefaultColor();
//				isDefaultCategory = true;
			}
		});

		// 初期状態では未選択のため、編集・除去ボタン使用不可
		buttons[EDIT].setEnabled(false);
		buttons[REMOVE].setEnabled(false);
		// 最大数に達した場合ボタン押下不可
		if (catItems.size() >= DictionaryConstants.MAX_CATEGORY_NO) {
			buttons[ADD].setEnabled(false);
		}

		// 固有の設定チェックボックスによる活性・非活性の設定
		cuManager.setControlEnabled(grpCategory, chkSpecificCategory.getSelection());

		// 種別設定変更不可の場合
		if (!isEditable) {
			chkSpecificCategory.setEnabled(false);
			if (chkSpecificCategory.getSelection()) {
				tblCategory.setEnabled(true);		// ﾃｰﾌﾞﾙまでdisabledにすると見にくいためここだけenabled
			}
			for (Button button : buttons) button.setEnabled(false);
			btnDefaultCategory.setEnabled(false);
		}

		// 固有の設定チェックボックスのリスナー追加
		chkSpecificCategory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// 活性・非活性の設定
				cuManager.setControlEnabled(grpCategory, chkSpecificCategory.getSelection());
			}
		});

		return item;
	}

	/**
	 * コントロールの活性・非活性を管理するクラス
	 */
	private class ControlUsageManager {

		/**
		 * 活性・非活性の状態をバックアップするリスト
		 */
		private List<Boolean> list = new ArrayList<Boolean>();

		/**
		 * 子コントロールを含むコントロールの活性・非活性を設定する
		 * @param control 対象コントロール
		 * @param enabled trueの場合活性、falseの場合非活性
		 */
		void setControlEnabled(Control control, boolean enabled) {
			if (enabled) {
				// 使用可能にする場合
				loadEnabled(list.iterator(), control);
			} else {
				// 使用不可にする場合
				list.clear();
				saveEnabled(list, control);
			}
		}

		/**
		 * 活性・非活性の状態をバックアップリストに格納する
		 * @param list バックアップリスト
		 * @param control 対象コントロール
		 */
		private void saveEnabled(List<Boolean> list, Control control) {
			// 自身の状態を退避
			list.add(control.getEnabled());
			// Compositeであれば子の状態も退避（再帰）
			if (control instanceof Composite) {
				for (Control child : ((Composite)control).getChildren()) {
					saveEnabled(list, child);
				}
			}
			// 状態を非活性にする
			control.setEnabled(false);
		}

		/**
		 * 活性・非活性の状態をバックアップリストから復元する
		 * @param iterator バックアップリストのイテレータ
		 * @param control 対象コントロール
		 */
		private void loadEnabled(Iterator<Boolean> iterator, Control control) {
			if (!iterator.hasNext()) return;
			// 自身の状態を復元
			control.setEnabled(iterator.next());
			// Compositeであれば子の状態も復元（再帰）
			if (control instanceof Composite) {
				for (Control child : ((Composite)control).getChildren()) {
					loadEnabled(iterator, child);
				}
			}
		}
	}

	/**
	 * 辞書情報タブの作成
	 * @param folder
	 * @return
	 */
	protected TabItem createInfoTab(final TabFolder folder) {

		TabItem item = new TabItem(folder, SWT.BORDER);
		Composite composite = new Composite(folder, SWT.NONE);
		GridLayout cmpInfoLayout = new GridLayout(1, false);
		composite.setLayout(cmpInfoLayout);
		item.setControl(composite);
		item.setText(Messages.DictionaryInformationDialog_15);

		DictionaryClass dicClass = tab.getDictionary().getDictionaryClass();

		// クラスグループ
		Group grpClass = new Group(composite, SWT.NONE);
		grpClass.setText(Messages.DictionaryInformationDialog_16);
		GridData data = new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL);
		grpClass.setLayoutData(data);
		GridLayout grpLayout = new GridLayout(3, false);
		grpClass.setLayout(grpLayout);

		// 問題領域
		Label lblDomain = new Label(grpClass, SWT.NONE);
		lblDomain.setText(Messages.DictionaryInformationDialog_17);
		data = new GridData();
		lblDomain.setLayoutData(data);

		txtDomain = new Text(grpClass, SWT.BORDER | SWT.SINGLE);
		data = new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 2;
		txtDomain.setLayoutData(data);
		txtDomain.setText(dicClass.domain);

		// プロジェクト名
		Label lblProject = new Label(grpClass, SWT.NONE);
		lblProject.setText(Messages.DictionaryInformationDialog_18);
		data = new GridData();
		lblProject.setLayoutData(data);

		txtProject = new Text(grpClass, SWT.BORDER | SWT.SINGLE);
		data = new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 2;
		txtProject.setLayoutData(data);
		txtProject.setText(dicClass.project);

		// 入力言語
		Label lblLanguage = new Label(grpClass, SWT.NONE);
		lblLanguage.setText(Messages.DictionaryInformationDialog_19);
		data = new GridData();
		lblLanguage.setLayoutData(data);

		txtLanguage = new Text(grpClass, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		data = new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL);
		txtLanguage.setLayoutData(data);
//		txtLanguage.setText(dicClass.language);
		txtLanguage.setText(getCommaSeparatedString(dicClass.languages));
		bkLanguage = txtLanguage.getText();			// 初期値を退避しておく

		Button btnLanguage = new Button(grpClass, SWT.PUSH);
		data = new GridData();
		btnLanguage.setData(data);
		btnLanguage.setText(Messages.DictionaryInformationDialog_35);
		btnLanguage.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				folder.setSelection(TAB_IDX_LANGUAGE);
			}
		});

		// 出力モデル
		Label lblModel = new Label(grpClass, SWT.NONE);
		lblModel.setText(Messages.DictionaryInformationDialog_20);
		data = new GridData();
		lblModel.setLayoutData(data);

//		txtModel = new Text(grpClass, SWT.BORDER | SWT.SINGLE);
//		data = new GridData(GridData.GRAB_HORIZONTAL
//				| GridData.HORIZONTAL_ALIGN_FILL);
//		data.horizontalSpan = 2;
//		txtModel.setLayoutData(data);
//		txtModel.setText(dicClass.model);
		cmbModel = new Combo(grpClass, SWT.READ_ONLY);
		data = new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 2;
		cmbModel.setLayoutData(data);
		ModelManager modelManager = ModelManager.getInstance();
		// 未登録モデルの場合、先頭に表示
		if (!canEdit) {
			String unregModel = tab.getDictionary().getDictionaryClass().model;
			String unregModelName = Messages.DictionaryInformationDialog_46 + tab.getDictionary().getDictionaryClass().model;
			cmbModel.add(unregModelName);
			cmbModel.setData(unregModelName, unregModel);
			cmbModel.setText(unregModelName);
		}
		for (String modelKey : modelManager.getKeyList()) {
			cmbModel.add(modelManager.getModelName(modelKey));
			cmbModel.setData(modelManager.getModelName(modelKey), modelKey);
		}
		if (!PluginHelper.isEmpty(dicClass.model) && canEdit) {
			cmbModel.setText(modelManager.getModelName(dicClass.model));
		}
		cmbModel.setEnabled(isModelEditable());

		return item;
	}


//	/**
//	 *
//	 */
//	private class ColorChangeListener implements IPropertyChangeListener {
//
//		private int row;
//		private int col;
//
//		public ColorChangeListener(int row, int col) {
//			this.row = row;
//			this.col = col;
//		}
//
//		public void propertyChange(PropertyChangeEvent event) {
//
//			if (event.getNewValue().equals(event.getOldValue())) {
//				return;
//			}
//
//			isDefaultCategory = false;
//
//			if (col == 2) {
//				catItems.get(row).primary = (RGB)event.getNewValue();
//			}
//			if (col == 3) {
//				catItems.get(row).secondary = (RGB)event.getNewValue();
//			}
//		}
//	}

	/**
	 * 種別設定ダイアログ・クラス
	 */
	private class CategoryDialog extends Dialog {

		/** No */
		Text txtNo;
		/** 辞書名 */
		Text txtName;
		/** マーキング色　初回 */
		ColorSelector csPrimary;
		/** マーキング色　2回目以降 */
		ColorSelector csSecondary;

		/** 編集対象アイテム */
		CategoryItem item;

		/**
		 * コンストラクタ
		 * @param shell シェル
		 * @param item アイテム
		 */
		public CategoryDialog(Shell shell, CategoryItem item) {
			super(shell);
			this.item = item;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
		 */
		protected Control createDialogArea(Composite parent) {
			// スーパークラスのメソッド呼び出し
			Composite composite = (Composite)super.createDialogArea(parent);

			// ダイアログエリアの作成
			Composite area = new Composite(composite, SWT.NONE);
			GridLayout layout = new GridLayout(3, false);
			area.setLayout(layout);
			area.setLayoutData(new GridData(GridData.FILL_BOTH));

			// No
			Label lblNo = new Label(area, SWT.NONE);
			lblNo.setText(Messages.DictionaryInformationDialog_52);

			txtNo = new Text(area, SWT.BORDER | SWT.RIGHT);
			GridData gdNo = new GridData();
			gdNo.widthHint = 50;
			txtNo.setLayoutData(gdNo);
			txtNo.setTextLimit(String.valueOf(DictionaryConstants.MAX_CATEGORY_NO).length());

			new Label(area, SWT.NONE).setText(MessageFormat.format(Messages.DictionaryInformationDialog_58,
					String.valueOf(DictionaryConstants.MAX_CATEGORY_NO)));

			// 種別名
			Label lblName = new Label(area, SWT.NONE);
			lblName.setText(Messages.DictionaryInformationDialog_53);

			txtName = new Text(area, SWT.BORDER);
			GridData gdName = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
			gdName.horizontalSpan = 2;
			txtName.setLayoutData(gdName);
			txtName.setTextLimit(50);

			// セパレーター
			Label separator = new Label(area, SWT.SEPARATOR | SWT.SHADOW_OUT | SWT.HORIZONTAL | SWT.NO_FOCUS | SWT.LEFT_TO_RIGHT);
			GridData gdSeparator = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
			gdSeparator.horizontalSpan = 3;
			separator.setLayoutData(gdSeparator);

			// マーキング色設定
			Label lblMarking = new Label(area, SWT.NONE);
			lblMarking.setText(Messages.DictionaryInformationDialog_54);
			GridData gdMarking = new GridData();
			gdMarking.horizontalSpan = 3;
			lblMarking.setLayoutData(gdMarking);

			Composite color = new Composite(area, SWT.NONE);
			GridLayout glColor = new GridLayout();
			glColor.numColumns = 4;
			color.setLayout(glColor);
			GridData gdColor = new GridData();
			gdColor.horizontalSpan = 3;
			color.setLayoutData(gdColor);

			Label lblPrimary = new Label(color, SWT.NONE);
			lblPrimary.setText(Messages.DictionaryInformationDialog_55);

			csPrimary = new ColorSelector(color);

			Label lblSecondary = new Label(color, SWT.NONE);
			lblSecondary.setText(Messages.DictionaryInformationDialog_56);

			csSecondary = new ColorSelector(color);

			txtNo.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					validateInput();
				}
			});

			txtName.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					validateInput();
				}
			});

			return composite;
		}

		protected Control createContents(Composite parent) {
			Composite composite = (Composite)super.createContents(parent);
			if (item != null) {
				txtNo.setText(String.valueOf(item.no));
				txtName.setText(item.name);
				csPrimary.setColorValue(item.primary);
				csSecondary.setColorValue(item.secondary);
				txtNo.setEditable(false);		// Noは変更不可
				txtName.setEditable(true);		// 名前は変更可
				txtName.setFocus();		// 種別名にフォーカス
				txtName.setSelection(0, item.name.length());
			} else {
				csPrimary.setColorValue(ColorName.RGB_BLACK);
				csSecondary.setColorValue(ColorName.RGB_BLACK);
				txtNo.setFocus();		// Noにフォーカス
			}
			validateInput();
			return composite;
		}

		protected void validateInput() {
			// No
			if (StringUtils.isBlank(txtNo.getText()) ||
					!StringUtils.isNumeric(txtNo.getText())) {
				// 数字でない場合エラー
				getButton(Dialog.OK).setEnabled(false);
				return;
			} else {
				int no = Integer.parseInt(txtNo.getText());
				// 0以下、MAXを超える場合エラー
				if (no <= 0 || no > DictionaryConstants.MAX_CATEGORY_NO) {
					getButton(Dialog.OK).setEnabled(false);
					return;
				}
				// 追加の場合、または編集でNo変更した場合
				if (item == null || (item != null && item.no != no)) {
					int itmCnt = tvCategory.getTable().getItemCount();
					for (int i = 0; i< itmCnt; i++) {
						CategoryItem catItm = (CategoryItem)tvCategory.getElementAt(i);
						if (no == catItm.no) {
							// 登録済みの数字の場合エラー
							getButton(Dialog.OK).setEnabled(false);
							return;
						}
					}
				}
			}
			// 種別名
			if (PluginHelper.isEmpty(txtName.getText())) {
				// 未入力の場合エラー
				getButton(Dialog.OK).setEnabled(false);
				return;
			}
			getButton(Dialog.OK).setEnabled(true);
		}

		protected void okPressed() {
			if (item == null) {
				item = new CategoryItem();
			}
			item.no = Integer.parseInt(txtNo.getText());
			item.name = txtName.getText();
			item.primary = csPrimary.getColorValue();
			item.secondary = csSecondary.getColorValue();

			super.okPressed();
		}

		public CategoryItem getData() {
			return this.item;
		}
	}

	/**
	 *
	 */
	class CategoryItem {

		int no;
		String name;
		RGB primary;
		RGB secondary;
	}

	/**
	 * 色選択ボタンのデフォルト色設定
	 */
	private void setDefaultColor() {

		catItems.clear();

		CategoryItem itmNoun = new CategoryItem();
		itmNoun.no = DictionaryConstants.CATEGORY_NO_NOUN;
		itmNoun.name = DictionaryConstants.CATEGORY_NOUN;
		itmNoun.primary = PreferenceConverter.getColor(
				storeBase, BasePreferenceConstants.PK_COLOR_NOUN_FIRST);
		itmNoun.secondary = PreferenceConverter.getColor(
				storeBase, BasePreferenceConstants.PK_COLOR_NOUN);
		catItems.add(itmNoun);

		CategoryItem itmVerb = new CategoryItem();
		itmVerb.no = DictionaryConstants.CATEGORY_NO_VERB;
		itmVerb.name = DictionaryConstants.CATEGORY_VERB;
		itmVerb.primary = PreferenceConverter.getColor(
				storeBase, BasePreferenceConstants.PK_COLOR_VERB_FIRST);
		itmVerb.secondary = PreferenceConverter.getColor(
				storeBase, BasePreferenceConstants.PK_COLOR_VERB);
		catItems.add(itmVerb);

		CategoryItem itmState = new CategoryItem();
		itmState.no = DictionaryConstants.CATEGORY_NO_STATE;
		itmState.name = DictionaryConstants.CATEGORY_STATE;
		itmState.primary = PreferenceConverter.getColor(
				storeBase, BasePreferenceConstants.PK_COLOR_STATE_FIRST);
		itmState.secondary = PreferenceConverter.getColor(
				storeBase, BasePreferenceConstants.PK_COLOR_STATE);
		catItems.add(itmState);

		tvCategory.setInput(catItems);

//		setEnabledColorSelector(false);
	}

//	private void setEnabledColorSelector(boolean enabled) {
//		csPrimary.setEnabled(enabled);
//		csSecondary.setEnabled(enabled);
//	}

	/**
	 * 言語設定タブの作成
	 * @param folder
	 */
	protected TabItem createLanguageTab(final TabFolder folder) {

		DictionaryClass dicClass = tab.getDictionary().getDictionaryClass();
		LanguageUtil util = ModelToolDictionaryPlugin.getLanguageUtil();
		Map<String, String> map = util.getLanguageMap();
		boolean isEditable = isLanguagesEditable();

		// 言語設定タブ
		TabItem item = new TabItem(folder, SWT.BORDER);
		Composite cmpLang = new Composite(folder, SWT.NONE);
		GridLayout cmpLangLayout = new GridLayout(3, false);
		cmpLang.setLayout(cmpLangLayout);
		item.setControl(cmpLang);
		item.setText(Messages.DictionaryInformationDialog_36);
		Composite composite = cmpLang;

		final Table tblLanguage = new Table(composite, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		tblLanguage.setLayoutData(data);

		TableLayout tblLayout = new TableLayout();
		tblLanguage.setLayout(tblLayout);
		tblLanguage.setHeaderVisible(true);
		tblLanguage.setLinesVisible(true);

		TableColumn col1 = new TableColumn(tblLanguage, SWT.NONE);
		col1.setText(Messages.DictionaryInformationDialog_37);

		TableColumn col2 = new TableColumn(tblLanguage, SWT.NONE);
		col2.setText(Messages.DictionaryInformationDialog_38);

		// 入力言語テーブルにデータを追加
		int langcnt = 0;
		for (String cd : dicClass.languages) {
			String name = map.get(cd);
			new TableItem(tblLanguage, SWT.NONE).setText(new String[]{cd, name});
			langcnt++;
			if (langcnt >= DictionaryConstants.MAX_COL_INFORMAL) {
				break;
			}
		}

		col1.pack();
		col2.pack();
		int col1Width = col1.getWidth();
		int col2Width = col2.getWidth();
		tblLayout.addColumnData(new ColumnWeightData(20, col1Width));
		tblLayout.addColumnData(new ColumnWeightData(80, col2Width));

		Label lblEmpty = new Label(composite, SWT.NONE);
		data = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		lblEmpty.setLayoutData(data);

		final Button btnLangDel = new Button(cmpLang, SWT.NONE);
		btnLangDel.setText(Messages.DictionaryInformationDialog_39);
		data = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		data.widthHint = 100;
		btnLangDel.setLayoutData(data);
		btnLangDel.setEnabled(tblLanguage.getItemCount() > 0);
		btnLangDel.setEnabled(isEditable);

		Label lblLang = new Label(cmpLang, SWT.NONE);
		lblLang.setText(Messages.DictionaryInformationDialog_40);
		lblLang.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

		final Combo cmbLang = new Combo(cmpLang, SWT.READ_ONLY);
		cmbLang.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		LanguageUtil languageUtil = ModelToolDictionaryPlugin.getLanguageUtil();
		// コンボボックスにセット
		for (Map.Entry<String, String> p : languageUtil.getReverseMap().entrySet()) {
			// 既に選択済みの言語は除外する
			if (!dicClass.languages.contains(p.getValue())) {
				cmbLang.add(p.getKey());
			}
			cmbLang.setData(p.getKey(), p.getValue());
		}
		if (cmbLang.getItemCount() > 0) {
			cmbLang.select(0);
		}

		final Button btnLangAdd = new Button(cmpLang, SWT.NONE);
		btnLangAdd.setText(Messages.DictionaryInformationDialog_41);
		data = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		data.widthHint = 100;
		btnLangAdd.setLayoutData(data);
		btnLangAdd.setEnabled(isEditable);

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
					} else if (index == tblLanguage.getItemCount()) {
						tblLanguage.setSelection(index - 1);
					} else {
						tblLanguage.setSelection(index);
					}

					// コンボボックスへの追加位置を探す
					// TODO:とりあえず線形探索　時間があれば二分探索に変更する
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

					// 辞書情報タブの入力言語TextBox更新
					txtLanguage.setText(getCommaSeparatedString(tblLanguage.getItems()));
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

					// 最大数を超えた入力は不可
					if (tblLanguage.getItemCount() >= DictionaryConstants.MAX_LANGUAGES_COUNT) {
						btnLangAdd.setEnabled(false);
					}

					// 辞書情報タブの入力言語TextBox更新
					txtLanguage.setText(getCommaSeparatedString(tblLanguage.getItems()));
				}
			}
		});

		return item;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Control control = super.createButtonBar(parent);
		// OKボタン制御
		getButton(IDialogConstants.OK_ID).setEnabled(canEdit);
		return control;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(Messages.DictionaryInformationDialog_34);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected Point getInitialSize() {
		Point point = super.getInitialSize();
		point.x = 500;
		return point;
	}

	@Override
	protected void okPressed() {

		// 編集不可の場合、処理なし
		if (!canEdit) return;

		// 入力チェック
		if (!validate()) return;

		// 形式モデルが変更された場合
		if (!tab.getDictionary().getDictionaryClass().model.equals((String)cmbModel.getData(cmbModel.getText()))) {
			if (tab.isFileExist()) {
				boolean saveFlg = MessageDialog.openConfirm(getShell(), Messages.DictionaryInformationDialog_42,
						Messages.DictionaryInformationDialog_43);
				if (!saveFlg) return;

				// 辞書名設定
				CInputDialog ciDlg = new CInputDialog(getShell(),
						Messages.DictionaryInformationDialog_44,
						MessageFormat.format(Messages.DictionaryInformationDialog_45, tab.getDictionaryName()),
						tab.getDictionaryName(),
						new IInputValidator() {
							@Override
							public String isValid(String newText) {
								if (newText.equals(tab.getDictionaryName())) {
									return Messages.DictionaryInformationDialog_47;
								}
								IWorkspace workspace = ResourcesPlugin.getWorkspace();
								IStatus status = workspace.validateName(newText, IResource.FILE);
								if (status.isOK() == false) {
									return status.getMessage();
								}
								return null;
							}
						});
				if (ciDlg.open() == CANCEL) return;

				// 辞書の保存
				if (tab.isDirty()) {
					tab.save(false);
				}

				// 辞書ファイルとの紐づけを解除
				tab.clearFile();
				// 辞書名設定
				tab.setText(ciDlg.getValue());
				tab.setToolTipText(ciDlg.getValue());
			}
			// Undoヒストリのクリア
			tab.getHistoryHelper().clear();
			// 辞書情報の更新
			updateClassInfo();
			// 種別設定の更新
			updateCategorySetting();
			// 辞書モデルの更新
			ModelManager.getInstance().updateModel(tab.getDictionary());
			// 形式的種別・定義のクリア
			tab.getDictionary().clearFormalDefinition();
			// 編集フラグのクリア
			tab.setDirty(true);
			// 再描画
			tab.getTableViewer().refresh();
			new HandlerUpdater().update(tab);

		// モデルが変更されていない場合
		} else {
			boolean classDirtyFlg = isClassDirty();
			boolean categoryDirtyFlg = isCategoryDirty();
			// クラス情報または種別設定が変更された場合
			if (classDirtyFlg || categoryDirtyFlg) {

				// 不必要にメモリを使いたくないんで変更があったほうだけ履歴に残す

				// クラス情報
				DictionaryClass oldClass = null;
				Object informalList = null;
				if (classDirtyFlg) {
					// 変更前のクラスの退避
					oldClass = tab.getDictionary().getDictionaryClass().deepCopy();
					// 変更前の非形式的定義の退避
					informalList = getInformalsList();
				}

				// 種別設定
				DictionarySetting oldSetting = null;
				if (categoryDirtyFlg) {
					// 変更前の設定の退避
					oldSetting = tab.getDictionary().getSetting().deepCopy();
				}

				// 辞書情報の更新
				updateClassInfo();
				// 種別設定の更新
				updateCategorySetting();
				// 変更後のクラスの取得
				DictionaryClass newClass = tab.getDictionary().getDictionaryClass();
				// 変更後の設定の退避
				DictionarySetting newSetting = tab.getDictionary().getSetting();
				// Undoヒストリの追加
				tab.getHistoryHelper().addInformationHistory(oldClass, newClass, oldSetting, newSetting, informalList);

				// 辞書モデルの更新
				ModelManager.getInstance().updateModel(tab.getDictionary());
				tab.setDirty(true);
				tab.getTableViewer().refresh();
				new HandlerUpdater().update(tab);
			}
		}

		super.okPressed();
	}

	/**
	 * クラス情報が変更されたか判定する
	 * @return
	 */
	protected boolean isClassDirty() {

		boolean isClassDirty = false;
		DictionaryClass dicClass = tab.getDictionary().getDictionaryClass();

		if (dicClass.domain.equals(txtDomain.getText()) == false) {
			isClassDirty = true;
		}
		if (dicClass.project.equals(txtProject.getText()) == false) {
			isClassDirty = true;
		}
		if (bkLanguage.equals(txtLanguage.getText()) == false) {
			isClassDirty = true;
		}
//		if (dicClass.model.equals(txtModel.getText()) == false) {
//			isClassDirty = true;
//		}
		String newModel = StringUtils.defaultString((String)cmbModel.getData(cmbModel.getText()));
		if (dicClass.model.equals(newModel) == false) {
			isClassDirty = true;
		}
		return isClassDirty;
	}

	/**
	 * クラス情報の変更内容を辞書オブジェクトへ反映する
	 */
	protected void updateClassInfo() {
		DictionaryClass dicClass = tab.getDictionary().getDictionaryClass();
		if (dicClass.domain.equals(txtDomain.getText()) == false) {
			dicClass.domain = txtDomain.getText();
		}
		if (dicClass.project.equals(txtProject.getText()) == false) {
			dicClass.project = txtProject.getText();
		}
		if (bkLanguage.equals(txtLanguage.getText()) == false) {
			// 入力言語変更内容の反映
			updateLangeuages();
		}
//		if (dicClass.model.equals(txtModel.getText()) == false) {
//			dicClass.model = txtModel.getText();
//		}
		String newModel = StringUtils.defaultString((String)cmbModel.getData(cmbModel.getText()));
		if (dicClass.model.equals(newModel) == false) {
			dicClass.model = newModel;
		}
	}

	/**
	 * 種別設定が変更されたか判定する
	 * @return
	 */
	protected boolean isCategoryDirty() {

		boolean ret = false;

		// 種別設定
		DictionarySetting setting = tab.getDictionary().getSetting();

		// 固有設定の有無が変更された場合
		if (setting.isDefaultCategory() == chkSpecificCategory.getSelection()) {
			return true;
		}

		// 固有設定ありの場合
		if (chkSpecificCategory.getSelection()) {
			// 種別数が異なる場合
			if (catItems.size() != setting.categorySize()) {
				return true;
			}
			for (CategoryItem item : catItems) {
				// 設定済みの種別を取得
				Category category = setting.getCategory(item.no);
				// 種別Noが存在しない場合
				if (category == null) {
					return true;
				}
				// 設定内容が異なる場合
				if (!item.name.equals(category.getName()) ||
						!item.primary.equals(category.getPrimary()) ||
						!item.secondary.equals(category.getSecondary())) {
					return true;
				}
			}
		}

		return ret;
	}

	/**
	 * 種別設定の変更内容を辞書オブジェクトへ反映する
	 */
	protected void updateCategorySetting() {

		// 種別設定
		DictionarySetting setting = tab.getDictionary().getSetting();
		setting.clearCategory();
		// 固有設定ありの場合
		if (chkSpecificCategory.getSelection()) {
			setting.setDefaultCategory(false);
			for (int i=0; i<catItems.size(); i++) {
				CategoryItem catItem = catItems.get(i);
				setting.setCategory(catItem.no, catItem.name, catItem.primary, catItem.secondary);
			}
		// 固有設定なしの場合
		} else {
			setting.setDefaultCategory(true);
		}
	}

	/**
	 * 入力チェック
	 * @return チェック結果（true = OK/false = NG)
	 */
	protected boolean validate() {

		if (StringUtils.isBlank(txtLanguage.getText())) {
			MessageDialog.openWarning(getShell(), Messages.DictionaryInformationDialog_3, Messages.DictionaryInformationDialog_11);
			return false;
		}

		return true;
	}

	/**
	 * 非形式的定義のリストを取得する
	 * @return 非形式的定義リスト
	 */
	private List<List<String>> getInformalsList() {
		List<List<String>> list = new ArrayList<List<String>>(tab.getDictionary().size());
		for (Entry entry : tab.getDictionary().getEntries()) {
			list.add(entry.copyInformals());
		}
		return list;
	}

	/**
	 * 入力言語の変更内容を辞書オブジェクトに反映する
	 */
	private void updateLangeuages() {

		// 変更後の内容を反映
		String[] after = txtLanguage.getText().split(","); //$NON-NLS-1$
		List<String> newLanguages = new ArrayList<String>(after.length);
		for (String lang : after) {
			newLanguages.add(lang.trim());
		}

		tab.getDictionary().updateLangeuages(newLanguages);

		// カラムの表示/非表示制御
		tab.showColumns(DictionaryConstants.DIC_COL_ID_INFORMAL);
	}

	@Override
	public boolean close() {
		folder.removeSelectionListener(tabListener);
		boolean ret = super.close();
		if (ret) {
			for (CategoryItem item : colorImageMap.keySet()) {
				Image[] images = colorImageMap.get(item);
				for (Image image : images) {
					if (image != null && !image.isDisposed()) {
						image.dispose();
					}
				}
			}
		}
		return ret;
	}


	/**
	 * ラベルプロバイダー
	 */
	private class TableLabelProvider extends LabelProvider
			implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {

			if (columnIndex < 3) {
				return null;
			}

			CategoryItem item = (CategoryItem)element;
			int mapIndex = columnIndex - 3;

			int x = 0;
			int y = 0;
			int width = 47;
			int height = 20;

			Image image = null;
			Image[] images = null;
			if (colorImageMap.containsKey(item)) {
				images = colorImageMap.get(item);
				image = images[mapIndex];
				if (image != null && !image.isDisposed()) {
					image.dispose();
				}
			} else {
				images = new Image[]{null, null};
				colorImageMap.put(item, images);
			}
			image = new Image(tvCategory.getTable().getDisplay(), new Rectangle(x, y, width, height));
			images[mapIndex] = image;
			GC gc = new GC(image);
			if (columnIndex == 3) {
				gc.setBackground(ModelToolDictionaryPlugin.getColorManager().getColor(item.primary));
			} else {
				gc.setBackground(ModelToolDictionaryPlugin.getColorManager().getColor(item.secondary));
			}
			gc.fillRectangle(2, 1, width-3, height-2);
			gc.setForeground(tvCategory.getTable().getDisplay().getSystemColor(SWT.COLOR_BLACK));
			gc.drawRectangle(1, 0, width-2, height-1);
			gc.dispose();
			return image;
		}

		public String getColumnText(Object element, int columnIndex) {
			switch (columnIndex) {
				case 1:
					return String.valueOf(((CategoryItem)element).no);
				case 2:
					return ((CategoryItem)element).name;
//				case 2:
//					return ((CategoryItem)element).primary.toString();
//				case 3:
//					return ((CategoryItem)element).secondary.toString();
			}
			return null;
		}
	}

	/** List⇒カンマ区切り文字列変換 */
	private String getCommaSeparatedString(List<String> list) {

		if (list == null || list.isEmpty()) {
			return ""; //$NON-NLS-1$
		}

		StringBuilder sb = new StringBuilder();
		for (int i=0; i<list.size()-1; i++) {
			String str = list.get(i);
			sb.append(str).append(","); //$NON-NLS-1$
		}
		return sb.append(list.get(list.size() - 1)).toString();
	}

	/** TableItem[]⇒カンマ区切り文字列変換 */
	private String getCommaSeparatedString(TableItem[] items) {

		if (items == null || items.length == 0) {
			return ""; //$NON-NLS-1$
		}

		StringBuilder sb = new StringBuilder();
		for (int i=0; i<items.length-1; i++) {
			String str = items[i].getText(0);
			sb.append(str).append(","); //$NON-NLS-1$
		}
		return sb.append(items[items.length - 1].getText(0)).toString();
	}

	/**
	 * 種別設定が変更可能かを判定する
	 * @return エントリを辞書編集ビューで開いている場合は変更不可
	 */
	private boolean isCategoryEditable() {
		return isLanguagesEditable();
	}

	/**
	 * 入力言語が変更可能かを判定する
	 * @return エントリを辞書編集ビューで開いている場合は変更不可
	 */
	private boolean isLanguagesEditable() {
		for (Entry entry : tab.getDictionary().getEntries()) {
			if (entry.isEdit()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 出力モデルが変更可能かを判定する
	 * @return 入力言語と同条件
	 */
	private boolean isModelEditable() {
		return isLanguagesEditable();
	}
}
