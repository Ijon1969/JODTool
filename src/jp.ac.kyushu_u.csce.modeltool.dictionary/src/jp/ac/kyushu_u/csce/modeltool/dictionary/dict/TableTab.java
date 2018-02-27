package jp.ac.kyushu_u.csce.modeltool.dictionary.dict;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import jp.ac.kyushu_u.csce.modeltool.base.utility.FileAccessException;
import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;
import jp.ac.kyushu_u.csce.modeltool.dictionary.Messages;
import jp.ac.kyushu_u.csce.modeltool.dictionary.ModelToolDictionaryPlugin;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryPreferenceConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dialog.DictionaryInformationDialog;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.AbstractColumn;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.CategoryColumn;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.ConjugationColumn;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.EditColumn;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.FormalColumn;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.IColumn;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.InformalColumn;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.NumberColumn;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.OutputColumn;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.SectionColumn;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.SubwordColumn;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.WordColumn;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.edit.DictionaryEditView;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.EmptyModel;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.Model;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.ModelManager;
import jp.ac.kyushu_u.csce.modeltool.dictionary.utility.DictionaryCsvFileAccessor;
import jp.ac.kyushu_u.csce.modeltool.dictionary.utility.DictionaryFileAccessor;
import jp.ac.kyushu_u.csce.modeltool.dictionary.utility.DictionaryJddFileAccessor;
import jp.ac.kyushu_u.csce.modeltool.dictionary.utility.DictionaryJsonFileAccessor;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPage;

// TODO: メソッドが増えたのでうまいこと分割したい…

/**
 * 辞書テーブルタブ
 * @author KBK yoshimura
 */
public class TableTab {

	/** タブフォルダー */
	private CTabFolder folder;
	/** タブアイテム */
	private CTabItem item;
	/** テーブルビューアー */
	private TableViewer viewer;
	/** 辞書データ */
	private Dictionary dictionary;
	/** カラム定義配列 */
	private AbstractColumn[] columns;
	/** 辞書ファイル */
	private IFile file;
	/** 辞書ビュー */
	private DictionaryView view;

//	/** アクティブなカラムインデックス（ソートで使用） */
//	private int activeIndex = -1;

	/**
	 * テーブルの内容の変更フラグ<br>
	 * true:最終保存後の変更あり／false:最終保存後の変更なし
	 */
	private boolean isDirty;
	/** 初期状態フラグ */
	private boolean isInit;

	/** フォント */
	private Font localFont;
	/** プロパティリスナー */
	private PropertyChangeListener propListener;

	/** 拡張マネジャー */
	private TableTabExtensionManager tabManager;

	/** プリファレンスストア */
	private IPreferenceStore store;

	/** リソース変更リスナー */
	private ResourceChangeListener rcListener;

	/** 履歴ヘルパー */
	private HistoryHelper historyHelper;

	/** 出力 全選択フラグ */
	private boolean allOutput = true;

	/** 形式モデルマネジャー */
	private ModelManager modelManager;

	/**
	 * コンストラクタ
	 * @param folder タブフォルダー
	 * @param name タブ名
	 */
	public TableTab(DictionaryView view, CTabFolder folder, String name) {
		Assert.isNotNull(name);
		init(view, folder, name, name);
	}

//	/**
//	 * コンストラクタ
//	 * @param folder タブフォルダー
//	 * @param file 辞書ファイル
//	 */
//	public TableTab(DictionaryView view, CTabFolder folder, IFile file) {
//		Assert.isNotNull(file);
//		init(view, folder, PluginHelper.getFileNameWithoutExtension(file), PluginHelper.getRelativePath(file), file);
//	}

	/**
	 * 初期化
	 * @param view 辞書ビュー
	 * @param folder タブフォルダー
	 * @param text タブ名
	 * @param tooltip ツールチップ
	 */
	private void init(DictionaryView view, CTabFolder folder, String text, String tooltip) {

		Assert.isNotNull(folder);

		this.view = view;
		this.folder = folder;
		item = new CTabItem(folder, SWT.NONE);
//		viewer = new TableViewer(folder, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer = new TableViewer(folder, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		dictionary = new Dictionary();

		modelManager = ModelManager.getInstance();

		// 履歴ヘルパー
		historyHelper = new HistoryHelper(this);

		// 列の定義
		// TODO:複数列のカラムの自動設定とかできない？
		List<AbstractColumn> columnList = new ArrayList<AbstractColumn>();
		columnList.add(new NumberColumn(this));
		columnList.add(new OutputColumn(this));
		columnList.add(new EditColumn(this));
		columnList.add(new WordColumn(this));
		for (int i=0; i<DictionaryConstants.MAX_COL_SUBWORD; i++) {
			columnList.add(new SubwordColumn(this, i));
		}
		for (int i=0; i<DictionaryConstants.MAX_COL_CONJUGATION; i++) {
			columnList.add(new ConjugationColumn(this, i));
		}
		columnList.add(new CategoryColumn(this));
		for (int i=0; i<DictionaryConstants.MAX_COL_INFORMAL; i++) {
			columnList.add(new InformalColumn(this, i));
		}
		columnList.add(new SectionColumn(this));
		columnList.add(new FormalColumn(this));
//		columnList.add(new TypeColumn(this));			しばらく使用しないのでコメントアウト
		columns = columnList.toArray(new AbstractColumn[]{});

		// テーブルビューアの設定
		initTableViewer();

		// タブアイテムの設定
		item.setText(text);
		item.setToolTipText(tooltip);
		item.setControl(viewer.getControl());

		// プリファレンスリスナーの設定
		store = ModelToolDictionaryPlugin.getDefault().getPreferenceStore();
		propListener = new PropertyChangeListener();
		store.addPropertyChangeListener(propListener);

		// 拡張された初期化
		tabManager = new TableTabExtensionManager();
		tabManager.initialize(this);

		// フラグの設定
		setDirty(false);
		isInit = true;

		// 入力言語にロケールの言語をセットする
		String locLangCd = Locale.getDefault().getLanguage();
		if (ModelToolDictionaryPlugin.getLanguageUtil().getLanguageMap().containsKey(locLangCd)) {
			dictionary.getDictionaryClass().languages.add(locLangCd);
		}

		// カラム表示設定
		showColumns();
	}

	/**
	 * 指定したカラムのインデックスを取得する
	 * @param columnId カラムID
	 * @return インデックス
	 */
	private int getIndexOfColumn(int columnId) {
		for (int i=0; i<columns.length; i++) {
			if (columns[i].getId() == columnId) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * テーブルビューアーの初期化
	 */
	private void initTableViewer() {

		Table table = viewer.getTable();
		TableLayout layout = new TableLayout();
		table.setLayout(layout);
		table.setHeaderVisible(true);	// ヘッダ表示
		table.setLinesVisible(true);	// グリッド表示

		// カラムの設定
		for (int i=0; i<columns.length; i++) {

			final AbstractColumn column = columns[i];

			// カラム生成
			TableViewerColumn col = new TableViewerColumn(viewer, column.getStyle());
			col.setEditingSupport(column);		// EditingSupport
			col.getColumn().setText(column.getHeaderText());	// ヘッダテキスト
			col.getColumn().setWidth(column.getColumnWidth());	// カラム幅
			col.getColumn().setResizable(column.resizable());	// リサイズ
			// ヘッダークリックリスナー追加
			if (column.getHeaderClickListener() != null) {
				col.getColumn().addSelectionListener(column.getHeaderClickListener());
			}

//			// カラムクリックのリスナー追加
//			col.addSelectionListener(new ColumnClickListener(i));
		}

		// プロバイダー等の設定
		viewer.setLabelProvider(new TableLabelProvider(viewer, columns, dictionary));
		viewer.setContentProvider(new TableContentProvider());

		// リスナーの登録
		// 選択行が変更された場合の処理
		ISelectionChangedListener postSelectionChangedListener =
		new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				// ツールバーの状態更新
				new HandlerUpdater().update(TableTab.this);
			}
		};
		viewer.addPostSelectionChangedListener(postSelectionChangedListener);

		// ダブルクリックされた場合の処理
		table.addListener(SWT.MouseDoubleClick, new Listener() {

			public void handleEvent(Event event) {

//				// 選択行＝1件でない場合、処理なし
//				TableItem[] selection = viewer.getTable().getSelection();
//				if (selection.length != 1) {
//					return;
//				}

				// 選択行の取得
				TableItem item = viewer.getTable().getItem(new Point(event.x, event.y));
				if (item == null) {
					return;
				}

				for (int i = 0; i < viewer.getTable().getColumnCount(); i++) {
					if (item.getBounds(i).contains(event.x, event.y)) {

						// チェックボックスでない場合、編集可能状態にする
						if (columns[i].canActivate() != IColumn.ACTIVATE_CHECK) {
							columns[i].setEnabled(true);
							viewer.editElement(item.getData(), i);
							columns[i].setEnabled(false);
						}

						break;
					}
				}
			}
		});

		// キー押下時の処理
		table.addListener(SWT.KeyDown, new Listener() {

			public void handleEvent(Event event) {

				// 「F2」ボタン押下で、見出し語編集を行う
				TableItem[] selection = viewer.getTable().getSelection();
				if (selection.length != 1 || SWT.F2 != event.keyCode) {
					return;
				}

				// 見出し語カラムを編集可能状態にする
				int index = getIndexOfColumn(DictionaryConstants.DIC_COL_ID_WORD);
				columns[index].setEnabled(true);
				TableItem item = selection[0];
				viewer.editElement(item.getData(), index);
				columns[index].setEnabled(false);
			}
		});

		// ドメインオブジェクトのセット
		viewer.setInput(dictionary);

		// コンテキストメニューの生成
		createContextMenu();

		// ドラッグ・アンド・ドロップ設定
		installTextDragAndDrop();
	}

	/**
	 * コンテキストメニューの生成
	 */
	private void createContextMenu() {
		MenuManager manager = new MenuManager();
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(final IMenuManager menu) {}
		} );
		Menu menu = manager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		view.getSite().registerContextMenu(
				DictionaryConstants.CTX_ID_DICTIONARY_TABLE,
				manager,
				view.getSite().getSelectionProvider());
	}

	/**
	 * ドラッグ・アンド・ドロップを設定する
	 * TODO:抽出機能のため本来はspecパッケージで設定するのが望ましい。
	 */
	protected void installTextDragAndDrop() {

		// ドロップ対象のリスナ
		DropTargetListener dropTargetListener= new DropTargetAdapter() {

			public void dragEnter(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					if ((event.operations & DND.DROP_MOVE) != 0) {
						event.detail= DND.DROP_MOVE;
					} else if ((event.operations & DND.DROP_COPY) != 0) {
						event.detail= DND.DROP_COPY;
					} else {
						event.detail= DND.DROP_NONE;
					}
				} else {
					event.detail = event.operations;
				}
			}

			public void dragOperationChanged(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					if ((event.operations & DND.DROP_MOVE) != 0) {
						event.detail= DND.DROP_MOVE;
					} else if ((event.operations & DND.DROP_COPY) != 0) {
						event.detail= DND.DROP_COPY;
					} else {
						event.detail= DND.DROP_NONE;
					}
				} else {
					event.detail = event.operations;
				}
			}

			public void dragOver(DropTargetEvent event) {
				event.feedback |= (DND.FEEDBACK_SCROLL | DND.FEEDBACK_SELECT);
			}

			public void drop(DropTargetEvent event) {
				// TODO:可能であれば当メソッドに拡張ポイントを作成し、実際の処理をspecプラグインに移動させたい

				// 改行を含んでいる場合
				if (PluginHelper.isMultiLine((String)event.data)) {
					MessageDialog.openWarning(view.getSite().getShell(),
							Messages.TableTab_0, Messages.TableTab_1);
					return;
				}

				// 追加位置を求める
				int y = viewer.getTable().toControl(event.x, event.y).y;
				Entry baseEntry = null;	// マウスオーバー行
				boolean addUnder = true;	// 下追加フラグ
				if (y < viewer.getTable().getHeaderHeight()) {
					// ヘッダ上にマウスがある場合、先頭に追加
					addUnder = false;
				} else {
					// マウスオーバーしている行データを取得
					for (TableItem item : viewer.getTable().getItems()) {
						if (y < item.getBounds().y) {
							break;
						}
						baseEntry = (Entry)item.getData();
					}
				}

				// マウスオーバー行の下に追加
				Entry entry = new Entry((String)event.data);
				addEntry(baseEntry, entry, addUnder);	// 追加
				setSelection(entry);					// フォーカス
			}
		};

		// テーブルビューアにリスナを追加
		viewer.addDropSupport(DND.DROP_COPY, new Transfer[]{TextTransfer.getInstance()}, dropTargetListener);
	}

	/**
	 * 与えられたエントリを、辞書の指定位置に追加する
	 * @param index 追加位置
	 * @param entries 追加エントリ
	 */
	private void addEntries(int index, List<Entry> entries) {
		addEntries(index, entries, false);
	}
	/**
	 * 与えられたエントリを、辞書の指定位置に追加する
	 * @param index 追加位置
	 * @param entries 追加エントリ
	 * @param isUndo Undo/Redoかどうか
	 */
	private void addEntries(int index, List<Entry> entries, boolean isUndo) {
		// 表示モードの取得
		int mode = store.getInt(DictionaryPreferenceConstants.PK_DICTIONARY_DISPLAY_MODE);
		if (index < 0 || index >= dictionary.size()) {
			// indexが範囲外の場合は末尾に追加
			index = dictionary.size();
		}
		int delta = 0;
		List<Integer> indexList = new ArrayList<Integer>(entries.size());
		for (Entry entry : entries) {
			dictionary.add(index + delta, entry, mode);
			indexList.add(index + delta);
			delta++;
		}
		// 履歴の登録
		if (!isUndo) {
			historyHelper.addAdditionHistory(entries, indexList);

			// 編集状態フラグON
			setDirty(true);
		}
	}

	/**
	 * 指定エントリを、基準エントリの上(または下)に追加する
	 * @param baseEntry 基準エントリ
	 * @param entry 追加エントリ
	 * @param addUnder true:下に追加／false:上に追加
	 */
	private void addEntry(Entry baseEntry, Entry entry, boolean addUnder) {

		int index = addUnder? dictionary.size() : 0;
		if (baseEntry != null) {
			index = dictionary.indexOf(baseEntry);
			if (addUnder) {
				index++;
			}
		}
		List<Entry> entries = new ArrayList<Entry>();
		entries.add(entry);
		addEntries(index, entries);
	}

	/**
	 * 指定エントリを、基準エントリの上(または下)に追加する
	 * @param baseEntry 基準エントリ
	 * @param entries 追加エントリ
	 * @param addUnder true:下に追加／false:上に追加
	 */
	private void addEntries(Entry baseEntry, List<Entry> entries, boolean addUnder) {

		int index = addUnder? dictionary.size() : 0;
		if (baseEntry != null) {
			index = dictionary.indexOf(baseEntry);
			if (addUnder) {
				index++;
			}
		}
		addEntries(index, entries);
	}

	/**
	 * 指定エントリを、基準エントリの上(または下)に追加する<br>
	 * 上下どちらに追加するかはプリファレンスストアから取得
	 * @param baseEntry 基準エントリ
	 * @param entry 追加エントリ
	 */
	public void addEntry(Entry baseEntry, Entry entry) {
		// 追加位置設定を取得
		boolean addUnder = store.getBoolean(DictionaryPreferenceConstants.PK_ENTRY_ADD_UNDER);
		List<Entry> entries = new ArrayList<Entry>();
		entries.add(entry);
		addEntries(baseEntry, entries, addUnder);
	}

	/**
	 * 指定エントリを、基準エントリの上(または下)に追加する<br>
	 * 上下どちらに追加するかはプリファレンスストアから取得
	 * @param baseEntry 基準エントリ
	 * @param entries 追加エントリ
	 */
	public void addEntries(Entry baseEntry, List<Entry> entries) {
		// 追加位置設定を取得
		boolean addUnder = store.getBoolean(DictionaryPreferenceConstants.PK_ENTRY_ADD_UNDER);
		addEntries(baseEntry, entries, addUnder);
	}

	/**
	 * 指定エントリを削除し、削除したエントリの次のエントリを返す
	 * @param targetEntries 指定エントリ
	 * @return 指定エントリの次のエントリ
	 */
	public Entry removeEntries(List<Entry> targetEntries) {
		return removeEntries(targetEntries, false);
	}
	/**
	 * 指定エントリを削除し、削除したエントリの次のエントリを返す
	 * @param targetEntries 指定エントリ
	 * @param isUndo Undo/Redoかどうか
	 * @return 指定エントリの次のエントリ
	 */
	public Entry removeEntries(List<Entry> targetEntries, boolean isUndo) {
		if (targetEntries == null || targetEntries.isEmpty()) {
			return null;
		}
		// インデックスリスト
		List<Integer> indexList = new ArrayList<Integer>(targetEntries.size());
		// 編集中
		for (Entry entry : targetEntries) {
			if (entry.isEdit()) {
				if (!isUndo) {
					MessageDialog.openWarning(folder.getShell(), Messages.TableTab_2, Messages.TableTab_3);
					return null;
				} else {
					// EditColumn#getCellEditor(object)に同様のロジックあり
					// ここを修正する場合、上記も併せて修正すること
					// 辞書編集ビューの取得
					IWorkbenchPage page = ModelToolDictionaryPlugin.getDefault().getWorkbench()
							.getActiveWorkbenchWindow().getActivePage();
					DictionaryEditView editView = (DictionaryEditView)page.findView(DictionaryConstants.PART_ID_DICTIONARY_EDIT);
					if (editView == null) {
						return null;
					}
					// 辞書編集ビューから該当タブを削除
					editView.removeTab(entry, false);
					// 辞書編集ビューでの編集内容を辞書ビューに反映しない
					viewer.update(entry, null);
				}
			}
			indexList.add(dictionary.indexOf(entry));
		}
		// 削除エントリの次エントリの取得
		Entry nextEntry = null;
		// 全削除でない場合
		if (targetEntries.size() < dictionary.size()) {
			// 選択された最後のエントリ
			Entry lastEntry = targetEntries.get(targetEntries.size() - 1);
			int index = dictionary.indexOf(lastEntry);
			// 最終でなければ次のレコード
			if (index != dictionary.size() - 1) {
				nextEntry = dictionary.get(index + 1);
			}
			// 最終レコードが削除対象に含まれる場合、削除後にdictinaryの最後のエントリを返す
		}

		// 行削除
		for (Entry entry : targetEntries) {
			dictionary.remove(entry,
					store.getInt(DictionaryPreferenceConstants.PK_DICTIONARY_DISPLAY_MODE));
		}

		// 履歴の登録
		if (!isUndo) {
			historyHelper.addDeletionHistory(targetEntries, indexList);
		}

		// 変更状態フラグON
		setDirty(true);

		// 次レコードを返す
		if (nextEntry == null && dictionary.size() > 0) {
			nextEntry = dictionary.get(dictionary.size() - 1);
		}
		return nextEntry;
	}

	/**
	 * 指定レコードを上／下移動する
	 * @param targetEntries 指定レコード
	 * @param isMoveUp 上移動フラグ（true：上／false：下）
	 */
	public void moveEntries(List<Entry> targetEntries, boolean isMoveUp) {
		moveEntries(targetEntries, isMoveUp, false);
	}
	/**
	 * 指定レコードを上／下移動する
	 * @param targetEntries 指定レコード
	 * @param isMoveUp 上移動フラグ（true：上／false：下）
	 * @param isUndo Undo/Redoかどうか
	 */
	public void moveEntries(List<Entry> targetEntries, boolean isMoveUp, boolean isUndo) {
		if (targetEntries == null || targetEntries.isEmpty()) {
			return;
		}
//		// 指定レコードのインデックス
//		int index = dictionary.indexOf(targetEntries.get(0));
//		// 移動先のインデックス（1つ上または下）
//		int destination = isMoveUp? index-1 : index+1;
//		if (destination < 0 || destination >= dictionary.size()) {
//			return;
//		}
//
//		// 指定位置と移動先のレコードを入れ替え
//		dictionary.swap(index, destination,
//				store.getInt(DictionaryPreferenceConstants.PK_DICTIONARY_DISPLAY_MODE));

		// 選択されたエントリの個数
		int size = targetEntries.size();
		// 選択範囲の先頭のインデックス
		int start = dictionary.indexOf(targetEntries.get(0));
		// 選択範囲の末尾のインデックス
		int end = dictionary.indexOf(targetEntries.get(size - 1));
		// 選択範囲のインデックスのリスト
		List<Integer> indexList = new ArrayList<Integer>(targetEntries.size());
		for (int index = start; index <= end; index++) {
			indexList.add(index);
		}

		// 移動させるエントリのインデックス
		int origin = start;
		// 移動先のインデックス
		int destination = end;
		// 上移動の場合
		if (isMoveUp) {
			// 先頭の1個上のエントリを末尾の位置に持っていく
			origin = start - 1;
			destination = end;
		// 下移動の場合
		} else {
			// 末尾の1個下のエントリを先頭の位置に持っていく
			origin = end + 1;
			destination = start;
		}

		// エントリの移動
		moveEntries(origin, destination);

		// 履歴の登録
		if (!isUndo) {
			historyHelper.addMovementHistory(origin, destination, indexList);
		}

		// 編集状態フラグON
		setDirty(true);
	}

	/**
	 * エントリの移動
	 * @param origin 移動元インデックス
	 * @param destination 移動先インデックス
	 */
	private void moveEntries(int origin, int destination) {
		dictionary.move(origin, destination,
				store.getInt(DictionaryPreferenceConstants.PK_DICTIONARY_DISPLAY_MODE));

	}

	/**
	 * 登録数、内訳の表示
	 */
	public void openInfomation() {
		// 辞書情報ダイアログの表示
		DictionaryInformationDialog dialog =
				new DictionaryInformationDialog(view.getSite().getShell(), this);
		dialog.open();
	}

	/**
	 * 指定エントリをコピーする
	 * @param view 辞書ビュー
	 * @param targetEntries 指定エントリ
	 * @param model モデル
	 */
	public void copyEntries(DictionaryView view, List<Entry> targetEntries, String model) {

		if (targetEntries == null || targetEntries.isEmpty()) {
			return;
		}

		// エントリを新規作成し、内容をコピー
		List<Entry> copy = new ArrayList<Entry>();
		for (Entry entry : targetEntries) {
			copy.add(entry.deepCopy());
		}
		// コピーしたエントリの退避
		view.setCopyEntries(copy, model);
		// コピー状態の設定
		view.setCopyMode(DictionaryView.COPY_MODE_COPY);
	}

	/**
	 * 指定エントリを切り取る
	 * @param view 辞書ビュー
	 * @param targetEntries 指定エントリ
	 * @param model モデル
	 * @return 削除エントリの次のエントリ
	 */
	public Entry cutEntries(DictionaryView view, List<Entry> targetEntries, String model) {
		if (targetEntries == null || targetEntries.isEmpty()) {
			return null;
		}
		// 編集中の場合、切り取り不可
		for (Entry entry : targetEntries) {
			if (entry.isEdit()) {
				MessageDialog.openWarning(folder.getShell(), Messages.TableTab_5, Messages.TableTab_6);
				return null;
			}
		}
		// エントリのコピー
		copyEntries(view, targetEntries, model);
		// レコードの削除
		Entry entry = removeEntries(targetEntries);
		// コピー状態の設定
		view.setCopyMode(DictionaryView.COPY_MODE_CUT);

		return entry;
	}

	/**
	 * 見出し語を貼り付ける（追加＆貼付）
	 * @param view 辞書ビュー
	 * @param baseEntry 基準レコード
	 */
	public List<Entry> pasteEntry(DictionaryView view, Entry baseEntry, String model) {

		// コピーデータなし
		if (view.isNoCopyData()) {
			List<Entry> ret = new ArrayList<Entry>();
			ret.add(baseEntry);
			return ret;
		}

		// 退避コピーデータを新規レコードにコピー
//		Entry pasteEntry = view.getCopyEntry().deepCopy();
//
//		pasteEntry.clearNumber();	// ←この処理不要？
		List<Entry> pasteEntries = new ArrayList<Entry>();
		for (Entry copyEntry : view.getCopyEntries()) {
			Entry pasteEntry = copyEntry.deepCopy();
			pasteEntry.clearNumber();
			// コピー元とコピー先でモデルが異なる場合
			if (!view.isSameCopyModel(model)) {
				pasteEntry.clearFormalDefinition();
			}
			pasteEntries.add(pasteEntry);
		}

		// レコードの追加
//		addEntry(baseEntry, pasteEntry);
		addEntries(baseEntry, pasteEntries);
		// 切り取り時の動作 貼付後データ保持する？
		// 1回のみ貼付可にする場合、以下3行のコメント解除
//		if (view.isCutData()) {
//			view.setCopyMode(DictionaryView.COPY_MODE_NODATA);
//		}
		return pasteEntries;
	}

	/**
	 * タブの活性化
	 */
	public void activate() {
		// タブの選択
		folder.setSelection(item);
		// ツールバーの状態更新
		new HandlerUpdater().update(this);
	}

	/**
	 * dispose
	 */
	public void dispose() {

		// 辞書編集ビューを閉じる
		closeEditView();

		// ヘッダークリックリスナーの削除
		TableColumn[] tCols = viewer.getTable().getColumns();
		for (int i=0; i<tCols.length; i++) {
			if (columns[i].getHeaderClickListener() != null) {
				tCols[i].removeSelectionListener(columns[i].getHeaderClickListener());
			}
		}

		// タブ、テーブルをdispose
		item.dispose();
		viewer.getControl().dispose();
		for (IColumn column : columns) {
			column.dispose();
		}

		// フォントをdispose
		if (localFont != null && !localFont.isDisposed()) {
			localFont.dispose();
		}

		// プロパティー変更リスナーの削除
		store.removePropertyChangeListener(propListener);

		// 履歴ヘルパーをdispose
		historyHelper.dispose();

		// リソース変更リスナーの停止
		if (rcListener != null) {
			rcListener.uninstall();
		}

		// モデル管理マネジャーから辞書を削除
		modelManager.removeDictionary(dictionary);

		// 終了処理（拡張）
		tabManager.finalize(this);
	}

	/**
	 * 辞書編集ビューで開いているタブを閉じる
	 */
	public void closeEditView() {
		closeEditView(null);
	}

	/**
	 * 辞書編集ビューで開いているタブを閉じる
	 */
	public void closeEditView(Entry entry) {

		// 辞書編集ビューの取得
		IWorkbenchPage page = view.getViewSite().getPage();
		DictionaryEditView editView = null;
		editView = (DictionaryEditView)page.findView(DictionaryConstants.PART_ID_DICTIONARY_EDIT);
		if (editView == null) {
			return;
		}

		if (entry == null) {
			// 辞書編集ビューのタブを閉じる
			for (Entry closeEntry : dictionary.getEntries()) {
				if (closeEntry.isEdit()) {
					editView.removeTab(closeEntry, false);
				}
			}
		} else {
			if (entry.isEdit()) {
				editView.removeTab(entry, false);
			}
		}
	}

	/** 編集状態フラグの取得 */
	public boolean isDirty() {
		if (!ModelManager.getInstance().isResisteredModel(dictionary.getDictionaryClass().model)) {
			return false;
		}
		return isDirty;
	}

	/**
	 * 編集状態フラグ設定
	 * @param isDirty
	 */
	public void setDirty(boolean isDirty) {

		// 拡張子がxmlでない場合の処理　削除
//		if (file != null && ToolConstants.EXTENSION_XML.equalsIgnoreCase(file.getFileExtension()) == false) {
//			this.isDirty = false;
//			String text = item.getText();
//			if (text.length() > 0 && "*".equals(text.substring(0, 1))) {
//				item.setText(text.substring(1));
//			}
//			return;
//		}

		// 未定義モデルの場合は編集されてもフラグを立てない
		if (!ModelManager.getInstance().isResisteredModel(dictionary.getDictionaryClass().model)) {
			return;
		}

		// 現在の状態と変更後の状態が同じ場合処理なし
		if (this.isDirty == isDirty) {
			return;
		}

		// タブ表示名の取得
		String text = item.getText();

		// 変更あり(true)に変更する場合
		if (isDirty) {
			item.setText("*" + text); //$NON-NLS-1$

		// 変更なし(false)に変更する場合
		} else {
			if (text.length() > 0 && "*".equals(text.substring(0, 1))) { //$NON-NLS-1$
				item.setText(text.substring(1));
			}
			// 変更履歴のクリーン
			historyHelper.clean();
		}

		this.isDirty = isDirty;

		// 初期状態フラグの編集
		isInit = false;

		// ツールバーの状態更新
		new HandlerUpdater().updateSave(this);
	}

	/**
	 * 初期状態フラグの取得
	 * @return
	 */
	public boolean isInit() {
		return isInit;
	}

	/** ファイル存在フラグの取得 */
	public boolean isFileExist() {
		return (file != null) && file.exists();
	}

	/** タブ名の設定 */
	public void setText(String text) {
		if (isDirty) {
			item.setText("*" + text); //$NON-NLS-1$
		} else {
			item.setText(text);
		}
	}

	/**
	 * タブ名の取得<br>
	 * 編集中の場合は頭に"*"がついています。
	 * 辞書名を取得したい場合は<code>getDictionaryName()</code>を使用してください。
	 */
	public String getText() {
		return item.getText();
	}

	/**
	 * 辞書名の取得<br>
	 * 編集中のマーク"*"を除去した形で返します
	 * @return
	 */
	public String getDictionaryName() {
		if (isDirty) {
			return getText().substring(1);
		} else {
			return getText();
		}
	}

	/** ツールチップテキストの設定 */
	public void setToolTipText(String string) {
		item.setToolTipText(string);
	}

	/**
	 * 辞書ファイルを新規作成する
	 * @param saveFile 新規作成するファイル
	 * @param message infoメッセージの表示有無 - ※error, warningメッセージは常に表示されます
	 */
	public void save(IFile saveFile, boolean message) {

		// ファイルの作成
		try {
			file = DictionaryJddFileAccessor.createDictionaryFile(saveFile, dictionary);
			if (file != null) {
				setDirty(false);
				setText(PluginHelper.getFileNameWithoutExtension(file));
				setToolTipText(PluginHelper.getRelativePath(file));
				if (message)
					MessageDialog.openInformation(folder.getShell(), Messages.TableTab_11, Messages.TableTab_12);

				// リソース変更リスナー
				rcListener = new ResourceChangeListener(file);
				rcListener.install();
			} else {
				MessageDialog.openWarning(folder.getShell(), Messages.TableTab_11, Messages.TableTab_14);
			}
		} catch (FileAccessException e) {
			// メッセージダイアログ出力
			MessageDialog.openError(folder.getShell(),
					Messages.TableTab_11, Messages.TableTab_16);
			// エラーログ出力
			IStatus status = new Status(
					IStatus.ERROR, ModelToolDictionaryPlugin.PLUGIN_ID,
					"failed to create new file [path:" + saveFile.getLocation().toOSString() + "]", //$NON-NLS-1$ //$NON-NLS-2$
					e);
			ModelToolDictionaryPlugin.getDefault().getLog().log(status);
		}
	}

	/**
	 * 辞書ファイルを上書き保存する
	 * @param message infoメッセージの表示有無 - ※error, warningメッセージは常に表示されます
	 */
	public void save(boolean message) {

//		if (file != null && DictionaryConstants.EXTENSION_XML.equalsIgnoreCase(file.getFileExtension()) == false) {
		if (file != null && DictionaryConstants.EXTENSION_JDD.equalsIgnoreCase(file.getFileExtension()) == false) {
			return;
		}

		if (isFileExist() == false) {
			return;
		}

		// ファイルの更新
		IFile overwrite = null;
		try {
			overwrite = DictionaryJddFileAccessor.updateDictionaryFile(file, dictionary);
			if (overwrite != null) {
				setDirty(false);
				if (message)
					MessageDialog.openInformation(folder.getShell(), Messages.TableTab_19, Messages.TableTab_12);
			}
		} catch (FileAccessException e) {
			// メッセージダイアログ出力
			MessageDialog.openError(folder.getShell(),
					Messages.TableTab_19, Messages.TableTab_22);
			// エラーログ出力
			IStatus status = new Status(
					IStatus.ERROR, ModelToolDictionaryPlugin.PLUGIN_ID,
					"failed to overwrite file [path:" + file.getLocation().toOSString() + "]", //$NON-NLS-1$ //$NON-NLS-2$
					e);
			ModelToolDictionaryPlugin.getDefault().getLog().log(status);
		}
	}

	/**
	 * 辞書ファイルを開く
	 * @param loadFile 辞書ファイル
	 */
	public void load(IFile loadFile) {

		file = loadFile;

		// 編集状態フラグOFF
		setDirty(false);

		// タブタイトル、ツールチップの設定
		setText(PluginHelper.getFileNameWithoutExtension(loadFile));
		setToolTipText(PluginHelper.getRelativePath(loadFile));

		// 辞書ファイルの拡張子の場合、ファイルを開く
		if (PluginHelper.in(loadFile.getFileExtension(), false, DictionaryConstants.DICTIONARY_EXTENSIONS)) {
			loadXML(loadFile);
		}

		// カラム表示設定
		showColumns();
	}

	/**
	 * XMLファイルの読込み
	 * @param loadFile
	 */
	private void loadXML(IFile loadFile) {

		try {
			// ファイル読込
			DictionaryJddFileAccessor.readDictionaryFile(loadFile, dictionary);

			if (dictionary.size() == 0) {
				return;
			}

			// 表示モード
			if (DictionaryUtil.isInspectMode()) {	// 検査モード
				// 出力用行番号で昇順に論理ソート＋採番
				dictionary.sort(Dictionary.OUTNO | Dictionary.ASC | Dictionary.LOGICAL | Dictionary.NUMBERING);
				// 検査用行番号で昇順にソート
				dictionary.sort(Dictionary.SEQNO | Dictionary.ASC | Dictionary.NUMBERING);

			} else if (DictionaryUtil.isOutputMode()) {	// 出力モード
				// 検査用行番号で昇順に論理ソート＋採番
				dictionary.sort(Dictionary.SEQNO | Dictionary.ASC | Dictionary.LOGICAL | Dictionary.NUMBERING);
				// 出力用行番号で昇順にソート
				dictionary.sort(Dictionary.OUTNO | Dictionary.ASC | Dictionary.NUMBERING);
			}

			// 初期状態フラグの設定
			isInit = false;

			// 1行目を選択
			setSelection(dictionary.getIterator().next());

			// リソース変更リスナー
			rcListener = new ResourceChangeListener(file);
			rcListener.install();

		} catch (Exception e) {
			// メッセージダイアログ出力
			MessageDialog.openError(folder.getShell(),
					Messages.TableTab_25, Messages.TableTab_26);
			// エラーログ出力
			IStatus status = new Status(
					IStatus.ERROR, ModelToolDictionaryPlugin.PLUGIN_ID,
					"failed to open file [path:" + loadFile.getLocation().toOSString() + "]", //$NON-NLS-1$ //$NON-NLS-2$
					e);
			ModelToolDictionaryPlugin.getDefault().getLog().log(status);

			return;
		}

		// ロード後処理
		afterLoad();

		// ファイルロード後処理（拡張）
		new TableTabExtensionManager().afterLoad(this);
	}

	/**
	 * XMLファイルの読込み
	 * @param importFile インポートするファイル<br>
	 *              OSのファイルダイアログを使用するためIFileではなく、Fileで受け取る
	 */
	private boolean importXML(File importFile) {

		try {
			// ファイル読込
			DictionaryFileAccessor.importXmlFile(importFile, dictionary);

		} catch (Exception e) {
			IStatus status = new Status(
					Status.ERROR,
					ModelToolDictionaryPlugin.PLUGIN_ID,
					Messages.TableTab_7
					,e);
			ModelToolDictionaryPlugin.getDefault().getLog().log(status);
			MessageDialog.openError(view.getViewSite().getShell(), Messages.TableTab_8, Messages.TableTab_7);
			dispose();
			return false;
		}

// 共通処理のため、importFileメソッドへ移動
//		// 表示モード
//		if (DictionaryUtil.isInspectMode()) {	// 検査モード
//			// 出力用行番号で昇順に論理ソート＋採番
//			dictionary.sort(Dictionary.OUTNO | Dictionary.ASC | Dictionary.LOGICAL | Dictionary.NUMBERING);
//			// 検査用行番号で昇順にソート
//			dictionary.sort(Dictionary.SEQNO | Dictionary.ASC | Dictionary.NUMBERING);
//
//		} else if (DictionaryUtil.isOutputMode()) {	// 出力モード
//			// 検査用行番号で昇順に論理ソート＋採番
//			dictionary.sort(Dictionary.SEQNO | Dictionary.ASC | Dictionary.LOGICAL | Dictionary.NUMBERING);
//			// 出力用行番号で昇順にソート
//			dictionary.sort(Dictionary.OUTNO | Dictionary.ASC | Dictionary.NUMBERING);
//		}
//
//		// 初期状態フラグの設定
//		isInit = false;
//
//		// 1行目を選択
//		setSelection(dictionary.getIterator().next());
//
//		// ロード後処理
//		afterLoad();
//
//		// ファイルロード後処理（拡張）
//		new TableTabExtensionManager().afterLoad(this);

		return true;
	}

	/**
	 * JSONファイルの読込
	 * @param importFile インポートするファイル<br>
	 *              OSのファイルダイアログを使用するためIFileではなく、Fileで受け取る
	 */
	private boolean importJson(File importFile) {
		try {
			DictionaryJsonFileAccessor.importJsonFile(importFile, dictionary);
		} catch (Exception e) {
			IStatus status = new Status(
					Status.ERROR,
					ModelToolDictionaryPlugin.PLUGIN_ID,
					Messages.TableTab_7
					,e);
			ModelToolDictionaryPlugin.getDefault().getLog().log(status);
			MessageDialog.openError(view.getViewSite().getShell(), Messages.TableTab_8, Messages.TableTab_7);
			dispose();
			return false;
		}

// 共通処理のため、importFileメソッドへ移動
//		// 表示モード
//		if (DictionaryUtil.isInspectMode()) {	// 検査モード
//			// 出力用行番号で昇順に論理ソート＋採番
//			dictionary.sort(Dictionary.OUTNO | Dictionary.ASC | Dictionary.LOGICAL | Dictionary.NUMBERING);
//			// 検査用行番号で昇順にソート
//			dictionary.sort(Dictionary.SEQNO | Dictionary.ASC | Dictionary.NUMBERING);
//
//		} else if (DictionaryUtil.isOutputMode()) {	// 出力モード
//			// 検査用行番号で昇順に論理ソート＋採番
//			dictionary.sort(Dictionary.SEQNO | Dictionary.ASC | Dictionary.LOGICAL | Dictionary.NUMBERING);
//			// 出力用行番号で昇順にソート
//			dictionary.sort(Dictionary.OUTNO | Dictionary.ASC | Dictionary.NUMBERING);
//		}
//
//		// 1行目を選択
//		setSelection(dictionary.getIterator().next());

		return true;
	}

	/**
	 * CSVファイルの読込
	 * @param importFile インポートするファイル<br>
	 *              OSのファイルダイアログを使用するためIFileではなく、Fileで受け取る
	 */
	private boolean importCSV(File importFile) {
		try {
			DictionaryCsvFileAccessor.importCsvFile(importFile, dictionary);
		} catch (Exception e) {
			IStatus status = new Status(
					Status.ERROR,
					ModelToolDictionaryPlugin.PLUGIN_ID,
					Messages.TableTab_7
					,e);
			ModelToolDictionaryPlugin.getDefault().getLog().log(status);
			MessageDialog.openError(view.getViewSite().getShell(), Messages.TableTab_8, Messages.TableTab_7);
			dispose();
			return false;
		}

// 共通処理のため、importFileメソッドへ移動
//		// 表示モード
//		if (DictionaryUtil.isInspectMode()) {	// 検査モード
//			// 出力用行番号で昇順に論理ソート＋採番
//			dictionary.sort(Dictionary.OUTNO | Dictionary.ASC | Dictionary.LOGICAL | Dictionary.NUMBERING);
//			// 検査用行番号で昇順にソート
//			dictionary.sort(Dictionary.SEQNO | Dictionary.ASC | Dictionary.NUMBERING);
//
//		} else if (DictionaryUtil.isOutputMode()) {	// 出力モード
//			// 検査用行番号で昇順に論理ソート＋採番
//			dictionary.sort(Dictionary.SEQNO | Dictionary.ASC | Dictionary.LOGICAL | Dictionary.NUMBERING);
//			// 出力用行番号で昇順にソート
//			dictionary.sort(Dictionary.OUTNO | Dictionary.ASC | Dictionary.NUMBERING);
//		}
//
//		// 1行目を選択
//		setSelection(dictionary.getIterator().next());

		return true;
	}

	/**
	 * 辞書ファイルのインポート
	 * @param importFile ファイル名
	 */
	public boolean importFile(File importFile) {

		boolean ret = false;
		if (importFile.getName().endsWith(DictionaryConstants.EXTENSION_JSON)) {
			ret = importJson(importFile);
		} else if (importFile.getName().endsWith(DictionaryConstants.EXTENSION_XML)) {
			ret = importXML(importFile);
		} else if (importFile.getName().endsWith(DictionaryConstants.EXTENSION_CSV)) {
			ret = importCSV(importFile);
		} else {
			// JSON,XML以外は対象外
			dispose();
			return false;
		}

		if (!ret) return false;

		// 表示モード
		if (DictionaryUtil.isInspectMode()) {	// 検査モード
			// 出力用行番号で昇順に論理ソート＋採番
			dictionary.sort(Dictionary.OUTNO | Dictionary.ASC | Dictionary.LOGICAL | Dictionary.NUMBERING);
			// 検査用行番号で昇順にソート
			dictionary.sort(Dictionary.SEQNO | Dictionary.ASC | Dictionary.NUMBERING);

		} else if (DictionaryUtil.isOutputMode()) {	// 出力モード
			// 検査用行番号で昇順に論理ソート＋採番
			dictionary.sort(Dictionary.SEQNO | Dictionary.ASC | Dictionary.LOGICAL | Dictionary.NUMBERING);
			// 出力用行番号で昇順にソート
			dictionary.sort(Dictionary.OUTNO | Dictionary.ASC | Dictionary.NUMBERING);
		}

		// 初期状態フラグの設定
		isInit = false;

		// 1行目を選択
		setSelection(dictionary.getIterator().next());

		// ロード後処理
		afterLoad();

		// ファイルロード後処理（拡張）
		new TableTabExtensionManager().afterLoad(this);

		// 編集状態フラグON
		setDirty(true);
		// カラム表示設定
		showColumns();

		return true;
	}

	/**
	 * 辞書のエクスポート
	 * @param exportFile ファイル名
	 * @return true:成功/false:失敗
	 */
	public boolean exportFile(File exportFile) {
		try {
			String[] ary = exportFile.getName().split("\\."); //$NON-NLS-1$
			String extension = ary[ary.length - 1];

			if (DictionaryConstants.EXTENSION_JSON.equalsIgnoreCase(extension)) {
				DictionaryJsonFileAccessor.exportJsonFile(exportFile, dictionary);
			} else if (DictionaryConstants.EXTENSION_CSV.equalsIgnoreCase(extension)) {
				DictionaryCsvFileAccessor.exportCsvFile(exportFile, dictionary);
			}
		} catch (FileAccessException e) {
			IStatus status = new Status(
					Status.ERROR,
					ModelToolDictionaryPlugin.PLUGIN_ID,
					Messages.TableTab_17
					,e);
			ModelToolDictionaryPlugin.getDefault().getLog().log(status);
			MessageDialog.openError(view.getViewSite().getShell(), Messages.TableTab_18, Messages.TableTab_17);
			return false;
		}
		return true;
	}

	/**
	 * タブのフォント（太字）を切り替える
	 * @param bold true:太字／false:標準
	 */
	public void setBold(boolean bold) {
		Font font = item.getFont();
		int style = bold? SWT.BOLD : SWT.NORMAL;
		FontData[] fds = font.getFontData();
		for (FontData data : fds) {
			data.setStyle(style);
		}
		item.setFont(new Font(font.getDevice(), fds));
		if (localFont != null) {
			localFont.dispose();
		}
		localFont = item.getFont();
	}

	/**
	 * 辞書ビューの取得
	 * @return
	 */
	public DictionaryView getView() {
		return view;
	}

	/**
	 * テーブルビューアーの取得
	 * @return
	 */
	public TableViewer getTableViewer() {
		return viewer;
	}

	/**
	 * 辞書データの取得
	 * @return
	 */
	public Dictionary getDictionary() {
		return dictionary;
	}

	/**
	 * 辞書ファイルの取得
	 * @return
	 */
	public IFile getFile() {
		return file;
	}

	/**
	 * 指定ファイルがタブに表示されたファイルと同じか判定する
	 * @param inFile
	 * @return
	 */
	public boolean equalFile(IFile inFile) {
		if (inFile == null) {
			return false;
		}
		return inFile.equals(file);
	}

	// 検査・出力で並び順を使用するため、カラムクリックでのソートは行わない
//	/**
//	 * カラムクリックのリスナークラス
//	 * @author yoshimura
//	 *
//	 */
//	private class ColumnClickListener extends SelectionAdapter {
//
//		private int colIndex;
//		private int order = 1;
//
//		public ColumnClickListener(int index) {
//			colIndex = index;
//		}
//
//		public void widgetSelected(SelectionEvent e) {
//
//			IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
//			Entry entry = (Entry)selection.getFirstElement();
//
//			if (activeIndex == colIndex) {
//				order *= -1;
//			} else {
//				order = 1;
//			}
//			activeIndex = colIndex;
//
//			if (columns[colIndex] instanceof NumberColumn) {
////				dictionary.sort(Dictionary.SORT_MODE_BY_NUMBER, order);
//			} else if (columns[colIndex] instanceof WordColumn) {
//				dictionary.sort(Dictionary.SORT_MODE_BY_WORD, order);
//			} else if (columns[colIndex] instanceof CategoryColumn) {
//				dictionary.sort(Dictionary.SORT_MODE_BY_CATEGORY, order);
//			} else if (columns[colIndex] instanceof FormalColumn) {
//				dictionary.sort(Dictionary.SORT_MODE_BY_FORMAL, order);
//			} else if (columns[colIndex] instanceof InformalColumn) {
//				dictionary.sort(Dictionary.SORT_MODE_BY_INFORMAL, order);
//			} else if (columns[colIndex] instanceof TypeColumn) {
//				dictionary.sort(Dictionary.SORT_MODE_BY_TYPE, order);
//			}
//
//			if (entry != null) {
//				viewer.setSelection(new StructuredSelection(entry), true);
//			}
//		}
//	}

	/**
	 * 種別ごとに登録数を集計する
	 * @return
	 */
	public TreeMap<Object, Integer> getCategorySubtotal() {
		return dictionary.getCategorySubtotal();
	}

	/**
	 * タブに紐付くファイルを開放する<br>
	 * ファイル名の変更、削除などを行う際に使用する。
	 */
	public void clearFile() {
		file = null;
	}

	/**
	 * タブに紐付くファイルを設定する<br>
	 * ファイル名の変更時に、{@link #clearFile()}を実行後、
	 * ファイル名変更を行い、当メソッドで紐付け直す。
	 * @param file
	 */
	public void setFile(IFile file) {
		this.file = file;
	}

	/**
	 * プロパティーチェンジリスナー
	 */
	private class PropertyChangeListener implements IPropertyChangeListener {
		public void propertyChange(PropertyChangeEvent event) {

			// 辞書モードの切替
			if (DictionaryPreferenceConstants.PK_DICTIONARY_DISPLAY_MODE.equals(event.getProperty())) {
				// ハンドラ制御・トグル切り替えはDictionaryVoewで行う
//				// ハンドラの制御
//				new HandlerUpdater().update(TableTab.this);
//				// トグル切り替え
//				ModelToolDictionaryPlugin.getHandlerActivationManager().changeToggleState(
//						DictionaryConstants.COMMAND_ID_CHANGE_MODE, DictionaryUtil.isOutputMode());
				// ソート
				int mode = (Integer)event.getNewValue();
				dictionary.sort(mode | Dictionary.ASC);
//				showColumns(DictionaryConstants.DIC_COL_ID_OUTPUT);
			}

			// クラスエントリの背景色
//			if (DictionaryPreferenceConstants.PK_DICTIONARY_CLASS_BACKCOLOR.equals(event.getProperty())) {
			if (event.getProperty().startsWith(DictionaryPreferenceConstants.PK_DICTIONARY_SECTION_BG)) {
				viewer.refresh();
			}
		}
	}

	/**
	 * 指定行をフォーカスする
	 * @param entry
	 */
	public void setSelection(Entry entry) {

		if (entry == null) {
			viewer.setSelection(null);
		} else {
			// フォーカス
			viewer.setSelection(new StructuredSelection(entry), true);
		}

		// ツールバーの状態変更
		new HandlerUpdater().update(this);
	}

	/**
	 * 指定行をフォーカスする
	 * @param entries
	 */
	public void setSelection(List<Entry> entries) {

		if (entries == null || entries.isEmpty()) {
			viewer.setSelection(null);
		} else {
			// フォーカス
			viewer.setSelection(new StructuredSelection(entries), true);
		}

		// ツールバーの状態変更
		new HandlerUpdater().update(this);
	}

	/**
	 * フォーカスされた行の取得
	 * @return entry
	 */
	public Entry getSelection() {
		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
		return (Entry)selection.getFirstElement();
	}

	/**
	 * フォーカスされた行の取得(複数選択)
	 * @return entry
	 */
	public List<Entry> getSelections() {
		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
		List<Entry> list = new ArrayList<Entry>(selection.size());
		for (Iterator<?> itr = selection.iterator(); itr.hasNext(); ) {
			list.add((Entry)itr.next());
		}
		return list;
	}

	/**
	 * リソース変更リスナー
	 *
	 * @author yoshimura
	 */
	public class ResourceChangeListener implements IResourceChangeListener, IResourceDeltaVisitor {

		/** 監視対象ファイル */
		private IFile fFile;
		/** リスナー使用状況 */
		private boolean fIsInstalled = false;

		/**
		 * 監視対象ファイルの取得
		 * @return
		 */
		public IFile getFile() {
			return this.fFile;
		}

		/**
		 * リスナー使用開始
		 */
		public void install() {
			getFile().getWorkspace().addResourceChangeListener(this);
			this.fIsInstalled = true;
		}

		/**
		 * リスナー使用終了
		 */
		public void uninstall() {
			getFile().getWorkspace().removeResourceChangeListener(this);
			this.fIsInstalled = false;
		}

		/**
		 * コンストラクタ
		 * @param _file
		 */
		public ResourceChangeListener(IFile _file) {
			this.fFile = _file;
		}

		/**
		 * リソース変更時の処理
		 * @see IResourceChangeListener#resourceChanged(IResourceChangeEvent)
		 */
		@Override
		public void resourceChanged(IResourceChangeEvent e) {
			IResourceDelta delta = e.getDelta();
			try {
				if (delta != null && fIsInstalled)
					delta.accept(this);
			} catch (CoreException x) {
				x.printStackTrace();
			}
		}

		/**
		 * リソース差分処理
		 * @see IResourceDeltaVisitor#visit(IResourceDelta)
		 */
		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			if (delta == null)
				return false;

			switch (delta.getKind()) {
				// ファイル削除
				case IResourceDelta.REMOVED:
					switch (delta.getFlags()) {
						// ファイル削除
						case IResourceDelta.NO_CHANGE:
							// 削除対象＝監視対象の場合、辞書タブの破棄
							if (delta.getResource().equals(fFile)) {
								TableTab.this.getView().getSite().getWorkbenchWindow().getShell().getDisplay().asyncExec(new Runnable() {
									public void run() {
										TableTab.this.getView().closeTab(fFile);
									}
								});
							}
							break;
					}
					break;

				// ファイル追加
				case IResourceDelta.ADDED:
					switch (delta.getFlags()) {
						// ファイル移動後
						case IResourceDelta.MOVED_FROM:
							// 移動前パス＝監視対象パスの場合
							if (delta.getMovedFromPath().equals(fFile.getFullPath())) {
								final IFile movedFile = (IFile)delta.getResource();
								this.fFile = movedFile;
								TableTab.this.getView().getSite().getWorkbenchWindow().getShell().getDisplay().asyncExec(new Runnable() {
									public void run() {
										TableTab.this.setFile(movedFile);
										TableTab.this.setText(PluginHelper.getFileNameWithoutExtension(movedFile));
										TableTab.this.setToolTipText(PluginHelper.getRelativePath(movedFile));
									}
								});
							}
							break;
					}
					break;
			}

			return true;
		}
	}

	/**
	 * 指定IDのカラムの表示数を取得する
	 * @param columnId カラムID
	 * @return 表示カラム数
	 */
	public int getShowColumnCount(int columnId) {
		// 副キーワード
		if (columnId == DictionaryConstants.DIC_COL_ID_SUBWORD) {
			int size = 0;
			for (Entry entry : dictionary.getEntries()) {
				for (int i = 0; i< entry.getSubwords().size(); i++) {
					if (StringUtils.isNotBlank(entry.getSubwords().get(i))) {
						size = Math.max(size, i + 1);
						if (size == DictionaryConstants.MAX_COL_SUBWORD)
							break;
					} else {
						break;
					}
					if (size == DictionaryConstants.MAX_COL_SUBWORD)
						break;
				}
			}
			if (size < DictionaryConstants.MAX_COL_SUBWORD) {
				size++;
			}
			return size;
		}
		// 活用形
		if (columnId == DictionaryConstants.DIC_COL_ID_CONJUGATION) {
			int size = 0;
			for (Entry entry : dictionary.getEntries()) {
				for (int i = 0; i< entry.getConjugations().size(); i++) {
					if (StringUtils.isNotBlank(entry.getConjugations().get(i))) {
						size = Math.max(size, i + 1);
						if (size == DictionaryConstants.MAX_COL_CONJUGATION)
							break;
					} else {
						break;
					}
					if (size == DictionaryConstants.MAX_COL_CONJUGATION)
						break;
				}
			}
			if (size < DictionaryConstants.MAX_COL_CONJUGATION) {
				size++;
			}
			return size;
		}
		// 非形式的定義
		if (columnId == DictionaryConstants.DIC_COL_ID_INFORMAL) {
			List<String> languages = dictionary.getDictionaryClass().languages;
			int size = 0;
			for (String language : languages) {
				if (StringUtils.isBlank(language)) break;
				size++;
			}
			return size;
		}
//		// 出力
//		if (columnId == DictionaryConstants.DIC_COL_ID_OUTPUT) {
//			if (DictionaryUtil.isOutputMode()) {
//				return 1;
//			} else {
//				return 0;
//			}
//		}
		return -1;
	}

	/**
	 * 指定カラムの最大表示数を取得する
	 * @param columnId
	 * @return
	 */
	private int getMaxColumnCount(int columnId) {
		// 副キーワード
		if (columnId == DictionaryConstants.DIC_COL_ID_SUBWORD)
			return DictionaryConstants.MAX_COL_SUBWORD;
		// 活用形
		if (columnId == DictionaryConstants.DIC_COL_ID_CONJUGATION)
			return DictionaryConstants.MAX_COL_CONJUGATION;
		// 非形式的定義
		if (columnId == DictionaryConstants.DIC_COL_ID_INFORMAL)
			return DictionaryConstants.MAX_COL_INFORMAL;
		return 1;
	}

	public void showColumns(int columnId) {

		// 表示するカラム数を取得
		int size = getShowColumnCount(columnId);

		// 対象カラムの先頭インデックス取得
		int infIdx = -1;
		for (int i=0; i<columns.length; i++) {
			if(columnId == columns[i].getId()) {
				infIdx = i;
				break;
			}
		}

		// 対象カラムの最大表示数
		int maxCnt = getMaxColumnCount(columnId);

		if (infIdx < 0) return;

		if (viewer.getTable().isDisposed()) return;

		TableColumn[] tcols = viewer.getTable().getColumns();
		for (int i = 0; i < maxCnt; i++) {
			TableColumn tcol = tcols[infIdx + i];
			IColumn col = columns[infIdx + i];
			if (i < size) {
				if (!tcol.getResizable()) {
					tcol.setResizable(true);
					tcol.setWidth(col.getColumnWidth());
				}
			} else {
				if (tcol.getResizable()) {
					tcol.setResizable(false);
					tcol.setWidth(0);
				}
			}
			tcol.setText(col.getHeaderText());
		}
	}

	public void hideColumns(int columnId) {

		// 対象カラムの先頭インデックス取得
		int infIdx = -1;
		for (int i=0; i<columns.length; i++) {
			if(columnId == columns[i].getId()) {
				infIdx = i;
				break;
			}
		}

		// 対象カラムの最大表示数
		int maxCnt = getMaxColumnCount(columnId);

		if (infIdx < 0) return;

		TableColumn[] tcols = viewer.getTable().getColumns();
		for (int i = 0; i < maxCnt; i++) {
			TableColumn tcol = tcols[infIdx + i];
			if (tcol.getResizable()) {
				tcol.setResizable(false);
				tcol.setWidth(0);
			}
		}
	}

	/**
	 * 可変長カラムの表示制御
	 */
	public void showColumns() {
		for (IColumn col : columns) {
			if (col.multiple())
				showColumns(col.getId());
		}
	}

	/**
	 * 副キーワードの追加
	 * @param entry
	 * @param subword
	 * @return
	 */
	public boolean addSubword(Entry entry, String subword) {

		boolean ret = false;

		List<String> subwords = entry.getSubwords();
		for (int i=0; i<Math.min(subwords.size(), DictionaryConstants.MAX_COL_SUBWORD); i++) {
			if (i >= subwords.size()) {
				subwords.add(subword);
				ret = true;
				break;
			} else if (StringUtils.isBlank(subwords.get(i))) {
				subwords.set(i, subword);
				ret = true;
				break;
			}
		}

		if (ret) {
			// テーブル表示の更新
			viewer.update(entry, null);
			showColumns(DictionaryConstants.DIC_COL_ID_SUBWORD);
		}

		return ret;
	}

	/**
	 * 履歴ヘルパーの取得
	 * @return 履歴ヘルパー
	 */
	public HistoryHelper getHistoryHelper() {
		return historyHelper;
	}

//	/**
//	 * 履歴マネジャーの取得
//	 * @return 履歴マネジャー
//	 */
//	public HistoryManager getHistoryManager() {
//		return historyManager;
//	}

	/**
	 * 元に戻す
	 */
	public void undo() {
		// 直前の履歴を取得
		History history = historyHelper.getLastUndo();
		if (history == null) return;

		// 表示モードの切り替え
		if (history.getMode() == History.MODE_INSPECT) {
			DictionaryUtil.setInspectMode();
		} else {
			DictionaryUtil.setOutputMode();
		}

		Entry entry = null;
		List<Entry> targetEntries = new ArrayList<Entry>();		// 処理対象のエントリのリスト
		List<Entry> selectEntries = new ArrayList<Entry>();		// 処理後フォーカスするエントリのリスト
		switch (history.getType()) {
			case History.TYPE_ADD:
//				entry = dictionary.get(history.getIndex());
//				entry = removeEntry(entry, true);
				List<Integer> indexList = history.getIndexListDesc();
				for (int index : indexList) {
					targetEntries.add(dictionary.get(index));
				}
				entry = removeEntries(targetEntries, true);
				selectEntries.add(entry);
				break;

			case History.TYPE_DELETE:
//				entry = ((Entry)history.getOldValue()).deepCopy();
//				addEntry(history.getIndex(), entry, true);
//				setSelection(entry);
				Iterator<Integer> indexIterator = history.getIndexListAsc().iterator();
				for (Object object : (List<?>)history.getOldValue()) {
					Entry delEntry = ((Entry)object).deepCopy();
					selectEntries.add(delEntry);
					List<Entry> delEntries = new ArrayList<Entry>(1);
					delEntries.add(delEntry);
					addEntries(indexIterator.next(), delEntries, true);
				}
				break;

			case History.TYPE_MOVE:
//				entry = dictionary.get((Integer)history.getNewValue());
//				moveEntry(entry,
//						(Integer)history.getNewValue() > (Integer)history.getOldValue(), true);
//				selectEntries.add(entry);
				int origin = (Integer)history.getOldValue();
				int destination = (Integer)history.getNewValue();
				moveEntries(destination, origin);
				// indexListには移動前のインデックスが入っているため元に戻した後にエントリを取得する
				for (int index : history.getIndexListAsc()) {
					selectEntries.add(dictionary.get(index));
				}
				break;

			case History.TYPE_EDIT:
			case History.TYPE_ENTRY:
				entry = dictionary.get(history.getIndex());
				entry.overwrite((Entry)history.getOldValue());
				showColumns();
				selectEntries.add(entry);
				break;

			case History.TYPE_INFO:
				boolean destroy = MessageDialog.openQuestion(view.getSite().getShell(), Messages.TableTab_9,
						Messages.TableTab_10);
				if (!destroy) {
					return;
				}
				// 辞書編集ビューで編集中の場合
				if (dictionary.isEditing()) {
					// 辞書編集タブを閉じる
					closeEditView();
				}

				// 辞書情報の取得
				DictionaryInfoHistory infoHist = (DictionaryInfoHistory)history.getOldValue();

				// クラス
//				dictionary.setClass((DictionaryClass)history.getOldValue());
				DictionaryClass oldClass = infoHist.getDictionaryClass();
				// ↑ ※ディープコピーを返すメソッドなので変数に格納して使用する
				if (oldClass != null) {
					dictionary.setClass(oldClass);
					List<List<String>> informalList = automaticCast(history.getOthers().get(0));
					for (int i=0; i<informalList.size(); i++) {
						entry = dictionary.getEntries().get(i);
						entry.getInformals().clear();
						entry.getInformals().addAll(informalList.get(i));
					}
					showColumns(DictionaryConstants.DIC_COL_ID_INFORMAL);
				}

				// 設定
				DictionarySetting oldSetting = infoHist.getDictinarySetting();
				// ↑ ※ディープコピーを返すメソッドなので変数に格納して使用する
				if (oldSetting != null) {
					dictionary.setSetting(oldSetting);
				}

				selectEntries.addAll(getSelections());
				break;

			case History.TYPE_CELL:
				entry = dictionary.get(history.getIndex());
				setEntryColumnValue(entry, history.getColumnId(), history.getOldValue());
				showColumns();
				selectEntries.add(entry);
				break;

			default:
				break;
		}

		// 履歴のUndo
		historyHelper.undo();

		// 再描画
		viewer.refresh();

		// 編集フラグ
		setDirty(history.isOldDirty());

		// 行のフォーカス
//		setSelection(entry);
		setSelection(selectEntries);

		// ツールバーの状態変更
		new HandlerUpdater().update(this);
	}

	/**
	 * やり直し
	 */
	public void redo() {
		// 直後の履歴を取得
		History history = historyHelper.getFirstRedo();
		if (history == null) return;

		// 表示モードの切り替え
		if (history.getMode() == History.MODE_INSPECT) {
			DictionaryUtil.setInspectMode();
		} else {
			DictionaryUtil.setOutputMode();
		}

		Entry entry = null;
		List<Entry> targetEntries = new ArrayList<Entry>();		// 処理対象のエントリのリスト
		List<Entry> selectEntries = new ArrayList<Entry>();		// 処理後フォーカスするエントリのリスト
		switch (history.getType()) {
			case History.TYPE_ADD:
//				entry = ((Entry)history.getNewValue()).deepCopy();
//				addEntry(history.getIndex(), entry, true);
				for (Object object : (List<?>)history.getNewValue()) {
					selectEntries.add(((Entry)object).deepCopy());
				}
				addEntries(history.getIndex(), selectEntries, true);
				break;

			case History.TYPE_DELETE:
//				entry = dictionary.get(history.getIndex());
//				entry = removeEntry(entry, true);
				for (int index : history.getIndexList()) {
					targetEntries.add(dictionary.get(index));
				}
				entry = removeEntries(targetEntries, true);
				selectEntries.add(entry);
				break;

			case History.TYPE_MOVE:
//				entry = dictionary.get((Integer)history.getOldValue());
//				moveEntry(entry,
//						(Integer)history.getOldValue() > (Integer)history.getNewValue(), true);
//				selectEntries.add(entry);
				// indexListには移動前のインデックスが入っているため移動前にエントリを取得しておく
				for (int index : history.getIndexListAsc()) {
					selectEntries.add(dictionary.get(index));
				}
				int origin = (Integer)history.getOldValue();
				int destination = (Integer)history.getNewValue();
				moveEntries(origin, destination);
				break;

			case History.TYPE_EDIT:
			case History.TYPE_ENTRY:
				entry = dictionary.get(history.getIndex());
				entry.overwrite((Entry)history.getNewValue());
				showColumns();
				selectEntries.add(entry);
				break;

			case History.TYPE_INFO:
				boolean destroy = MessageDialog.openQuestion(view.getSite().getShell(), Messages.TableTab_13,
						Messages.TableTab_15);
				if (!destroy) {
					return;
				}
				// 辞書編集ビューで編集中の場合
				if (dictionary.isEditing()) {
					// 辞書編集タブを閉じる
					closeEditView();
				}

				// 辞書情報の取得
				DictionaryInfoHistory infoHist = (DictionaryInfoHistory)history.getNewValue();

				// クラス
//				dictionary.setClass((DictionaryClass)history.getNewValue());
				DictionaryClass newClass = infoHist.getDictionaryClass();
				// ↑ ※ディープコピーを返すメソッドなので変数に格納して使用する
				if (newClass != null) {
					dictionary.setClass(newClass);
					showColumns(DictionaryConstants.DIC_COL_ID_INFORMAL);
				}

				// 設定
				DictionarySetting newSetting = infoHist.getDictinarySetting();
				// ↑ ※ディープコピーを返すメソッドなので変数に格納して使用する
				if (newSetting != null) {
					dictionary.setSetting(newSetting);
				}

				selectEntries.addAll(getSelections());
				break;

			case History.TYPE_CELL:
				entry = dictionary.get(history.getIndex());
				setEntryColumnValue(entry, history.getColumnId(), history.getNewValue());
				showColumns();
				selectEntries.add(entry);
				break;

			default:
				break;
		}

		// 履歴のRedo
		historyHelper.redo();

		// 再描画
		viewer.refresh();

		// 編集フラグ
		setDirty(history.isNewDirty());

		// 行のフォーカス
//		setSelection(entry);
		setSelection(selectEntries);

		// ツールバーの状態変更
		new HandlerUpdater().update(this);
	}

	/**
	 * 履歴の消去
	 */
	public void clearHistory() {
		historyHelper.clear();
	}

	/**
	 * カラムの値を設定する
	 * @param entry
	 * @param columnId
	 * @param value
	 */
	private void setEntryColumnValue(Entry entry, int columnId, Object value) {

		switch (columnId) {
			case DictionaryConstants.DIC_COL_ID_WORD:
				entry.setWord((String)value);
				break;

			case DictionaryConstants.DIC_COL_ID_SUBWORD:
				List<String> subwords = automaticCast(value);
				entry.getSubwords().clear();
				entry.getSubwords().addAll(subwords);
				break;

			case DictionaryConstants.DIC_COL_ID_CONJUGATION:
				List<String> conjugations = automaticCast(value);
				entry.getConjugations().clear();
				entry.getConjugations().addAll(conjugations);
				break;

			case DictionaryConstants.DIC_COL_ID_CATEGORY:
				entry.setCategoryNo((Integer)value);
				break;

			case DictionaryConstants.DIC_COL_ID_INFORMAL:
				List<String> informals = automaticCast(value);
				entry.getInformals().clear();
				entry.getInformals().addAll(informals);
				break;

			case DictionaryConstants.DIC_COL_ID_SECTION:
				entry.setSection((Integer)value);
				break;

			case DictionaryConstants.DIC_COL_ID_FORMAL:
				entry.setFormal((String)value);
				break;

			case DictionaryConstants.DIC_COL_ID_TYPE:
				entry.setType((String)value);
				break;

			default:
				break;
		}
	}

	/**
	 * キャスト
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T> T automaticCast(Object obj) {
		return (T)obj;
	}

	/**
	 * 出力フラグの全選択／全解除
	 */
	public void switchAllOutput() {
		// 全出力フラグ 反転
		allOutput = !allOutput;
		// 反転カウント
		int cnt = 0;
		for (Entry entry : dictionary.getEntries()) {
			if (entry.isOutput() != allOutput) {
				cnt++;
				entry.setOutput(allOutput);
			}
		}
		// １つも反転しなかった場合、全出力フラグを反転し
		// すべてのエントリのフラグも反転する
		if (cnt == 0) {
			allOutput = !allOutput;
			for (Entry entry : dictionary.getEntries()) {
				entry.setOutput(allOutput);
			}
		}
		// 再表示
		viewer.refresh();
	}

	/**
	 * 辞書ロード 後処理
	 */
	private void afterLoad() {
		// 形式モデルマネジャーへ登録
		Model model = modelManager.getModel(dictionary);
		if (model.getClass() == EmptyModel.class) {
			MessageDialog.openWarning(folder.getShell(), Messages.TableTab_27,
					MessageFormat.format(Messages.TableTab_28, dictionary.getDictionaryClass().model));
		}
	}
}
