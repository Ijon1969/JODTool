package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.edit;

import static jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.ac.kyushu_u.csce.modeltool.base.utility.AbstractViewPart;
import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;
import jp.ac.kyushu_u.csce.modeltool.dictionary.Messages;
import jp.ac.kyushu_u.csce.modeltool.dictionary.ModelToolDictionaryPlugin;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryPreferenceConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionarySetting.Category;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryUtil;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.Model;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.ModelManager;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

/**
 * 辞書編集ビュー
 * @author KBK yoshimura
 */
public class DictionaryEditView extends AbstractViewPart {

	private CTabFolder folder;
	private List<EditTab> tabs;

	/**
	 * createPartControl
	 */
	public void createPartControl(Composite parent) {

		// タブフォルダー生成
		folder = new CTabFolder(parent, SWT.NONE);
		tabs = new ArrayList<EditTab>();
	}

	/**
	 * 新規タブ作成
	 * @param entry
	 * @param listener
	 */
	public void createNewTab(TableTab ttab, Entry entry, IDictionaryEditTabListener listener) {

		// タブ作成
		EditTab editTab = new EditTab(ttab);

//		// テーブルタブ設定（形式的種別コンボボックス設定）
//		editTab.setTableTab(ttab);
		// 形式的種別コンボボックス設定
		editTab.setCmbSection();

		// 見出し語データ設定
		editTab.setEntry(entry);

		// リスナー設定
		editTab.listeners.add(listener);

		// フォーカス
		folder.setSelection(editTab.item);

		// タブリスト追加
		tabs.add(editTab);
	}

	/**
	 * タブ削除
	 * @param entry
	 * @param isOk true:確定／false:キャンセル
	 */
	public void removeTab(Entry entry, boolean isOk) {
		EditTab tab = null;
		for (Iterator<EditTab> itr = tabs.iterator(); itr.hasNext(); ) {
			tab = itr.next();
			if (tab.entry.equals(entry)) {
				break;
			}
		}
		if (tab == null) {
			return;
		}

		int buttonId = isOk? EditTab.OK : EditTab.CANCEL;
		tab.buttonPressed(buttonId);
	}

	/**
	 * タブの更新（行番号）
	 * @param entries
	 */
	public void updateTab(List<Entry> entries) {

		// 指定された見出し語のタブの行番号を更新する。
		// TODO:ループで処理対象を探すよりも全タブ更新したほうが早いか？

		List<EditTab> cTabs = new ArrayList<EditTab>(tabs);

		for (Entry entry : entries) {
			for (Iterator<EditTab> tItr = cTabs.iterator(); tItr.hasNext(); ) {
				EditTab tab = tItr.next();
				if (entry.equals(tab.entry)) {
					tab.refreshNo();
					tItr.remove();
					break;
				}
			}
		}
	}

	/**
	 * タブの更新（行番号）※全タブ更新バージョン
	 * タブ数が少なければこちらのほうが早いと思われる。
	 */
	public void UpdateTab() {
		for (EditTab tab : tabs) {
			tab.refreshNo();
		}
	}

	/**
	 * dispose
	 */
	public void dispose() {
		for (int i=tabs.size()-1; i>-1; i--) {
			EditTab tab = tabs.get(i);
			tab.entry.setEdit(false);
			tab.update();
			tabs.remove(i);
		}
		super.dispose();
	}

	/**
	 * タブクラス
	 *
	 * @author KBK yoshimura
	 */
	private class EditTab {

		private CTabItem item;
		private Text txtNo;
//		private Label txtNo;
		private Text txtWord;
		private Combo cmbCategory;
		private List<Integer> categoryNoList = new ArrayList<Integer>();
		private Text[] txtInformal;
		private Combo cmbSection;
		private Text txtFormal;
//		private Text txtType;
		private TableViewer tvSubword;
		private TableViewer tvConjugation;

		private SelectionListener buttonListener;
		public static final int OK = 1;
		public static final int CANCEL = 2;

		TableTab ttab;
		Entry entry;

		ListenerList listeners;

		// 形式モデル
		private Model model;

		/**
		 * コンストラクタ
		 */
		public EditTab(TableTab ttab) {

			this.ttab = ttab;
			// モデルの取得
			model = ModelManager.getInstance().getModel(ttab.getDictionary());

			// タブアイテムの生成
			item = new CTabItem(folder, SWT.NONE);

			Composite composite = new Composite(folder, SWT.NONE);
			composite.setLayout(new GridLayout());
			composite.setLayoutData(new GridData());

			// 編集エリア
			createEditArea(composite);

			// ボタンバー
			createButtonBar(composite);

			// タブとコントロールの紐付け
			item.setControl(composite);

			// リスナリスト
			listeners = new ListenerList();
		}

		/**
		 * 編集エリアの作成
		 * @param parent
		 * @return
		 */
		protected Control createEditArea(final Composite parent) {

			// provider
			TableProvider provider = new TableProvider();

			ScrolledComposite scrolled = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
			scrolled.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			scrolled.setMinSize(500, 600);
			scrolled.setExpandHorizontal(true);
			scrolled.setExpandVertical(true);

			Composite composite = new Composite(scrolled, SWT.NONE);
			composite.setLayoutData(new GridData());
			composite.setLayout(new GridLayout(2, false));
			scrolled.setContent(composite);

			// 行番号
			Label lblNo = new Label(composite, SWT.NONE);
			lblNo.setText(Messages.DictionaryEditView_0);
			lblNo.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

//			txtNo = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
			txtNo = new Text(composite, SWT.READ_ONLY);
//			txtNo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
			txtNo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

			// 見出し語
			Label lblWord = new Label(composite, SWT.NONE);
			lblWord.setText(Messages.DictionaryEditView_1);
			lblWord.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

			txtWord = new Text(composite, SWT.BORDER | SWT.SINGLE);
			GridData gdWord = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
			gdWord.widthHint = 200;
			txtWord.setLayoutData(gdWord);
			Point pWord = txtWord.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			int defaultHeight = pWord.y;

			// 変更時の処理
			final String[] words = new String[]{null};	// 退避用（finalにするため配列を使用）
			txtWord.addFocusListener(new FocusListener() {		// フォーカス・イン／アウト時の処理
				public void focusLost(FocusEvent e) {
					// 変更がない場合何もしない
					if (txtWord.getText().equals(words[0])) return;
					// 形式的定義を補完する
					complementFormal();
				}
				public void focusGained(FocusEvent e) {
					// フォーカス時の値を退避
					words[0] = txtWord.getText();
				}
			});

			// 副キーワード
			Label lblSubWord = new Label(composite, SWT.NONE);
			lblSubWord.setText(Messages.DictionaryEditView_9);
			lblSubWord.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, false, false));

			tvSubword = new TableViewer(composite, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
			Table tblSubword = tvSubword.getTable();
			GridData gdSubword = new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1);
			gdSubword.heightHint = tblSubword.getItemHeight() * 2;
			gdSubword.widthHint = 330;
			tblSubword.setLayoutData(gdSubword);
			TableLayout tlSubword = new TableLayout();
			tblSubword.setLayout(tlSubword);
			tblSubword.setHeaderVisible(false);
			tblSubword.setLinesVisible(true);
			// 番号
			TableViewerColumn colSubwordNum = new TableViewerColumn(tvSubword, SWT.NONE);
			colSubwordNum.getColumn().setWidth(30);
			colSubwordNum.setEditingSupport(new TableEditingSupport(tvSubword, 0));
			TableViewerColumn colSubword = new TableViewerColumn(tvSubword, SWT.NONE);
			// テキスト
			colSubword.getColumn().setWidth(300);
			colSubword.setEditingSupport(new TableEditingSupport(tvSubword, 1));
			tvSubword.setContentProvider(provider);
			tvSubword.setLabelProvider(provider);

			// 活用形
			Label lblConjugation = new Label(composite, SWT.NONE);
			lblConjugation.setText(Messages.DictionaryEditView_10);
			lblConjugation.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, false, false));

			tvConjugation = new TableViewer(composite, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.FULL_SELECTION);
			Table tblConjugation = tvConjugation.getTable();
			GridData gdConjugation = new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1);
			gdConjugation.heightHint = tblConjugation.getItemHeight() * 2;
			gdConjugation.widthHint = 330;
			tblConjugation.setLayoutData(gdConjugation);
			TableLayout tlConjugation = new TableLayout();
			tblConjugation.setLayout(tlConjugation);
			tblConjugation.setHeaderVisible(false);
			tblConjugation.setLinesVisible(true);
			// 番号
			TableViewerColumn colConjugationNum = new TableViewerColumn(tvConjugation, SWT.NONE);
			colConjugationNum.getColumn().setWidth(30);
			colConjugationNum.setEditingSupport(new TableEditingSupport(tvConjugation, 0));
			// テキスト
			TableViewerColumn colConjugation = new TableViewerColumn(tvConjugation, SWT.NONE);
			colConjugation.getColumn().setWidth(300);
			colConjugation.setEditingSupport(new TableEditingSupport(tvConjugation, 1));
			tvConjugation.setContentProvider(provider);
			tvConjugation.setLabelProvider(provider);

			// 種別
			Label lblCategory = new Label(composite, SWT.NONE);
			lblCategory.setText(Messages.DictionaryEditView_2);
			lblCategory.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

			cmbCategory = new Combo(composite, SWT.READ_ONLY);
			GridData gdCategory = new GridData();
			gdCategory.minimumWidth = 100;
			cmbCategory.setLayoutData(gdCategory);
			for (String category : CATEGORIES) {
				cmbCategory.add(category);
				categoryNoList.add(0);
			}
			for (int i = 1; i <= DictionaryConstants.MAX_CATEGORY_NO; i++) {
				Category category = ttab.getDictionary().getSetting().getCategory(i);
				if (category != null) {
					cmbCategory.add(category.getName());
					categoryNoList.add(category.getNo());
				}
			}

			// 非形式的定義
			List<String> languages = ttab.getDictionary().getDictionaryClass().languages;
			int infCnt = ttab.getShowColumnCount(DIC_COL_ID_INFORMAL);
			txtInformal = new Text[infCnt];
			for (int i=0; i<infCnt; i++) {
				Label lblInformal = new Label(composite, SWT.NONE);
				lblInformal.setText(MessageFormat.format(Messages.DictionaryEditView_3, languages.get(i)));
				lblInformal.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, false, false));

				txtInformal[i] = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
				GridData gdInformal = new GridData(SWT.FILL, SWT.FILL, true, true);
				gdInformal.minimumHeight = defaultHeight;
				txtInformal[i].setLayoutData(gdInformal);
			}

			// 形式的種別
			Label lblSection = new Label(composite, SWT.NONE);
			lblSection.setText(Messages.DictionaryEditView_4);
			lblSection.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));

			cmbSection = new Combo(composite, SWT.READ_ONLY);
			GridData gdSection = new GridData();
			gdSection.minimumWidth = 100;
			cmbSection.setLayoutData(gdSection);

			// 変更時の処理
			cmbSection.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					// 形式的定義を補完する
					complementFormal();
				}
			});

			// 形式的定義
			Label lblFormal = new Label(composite, SWT.NONE);
			lblFormal.setText(Messages.DictionaryEditView_5);
			lblFormal.setLayoutData(new GridData(SWT.END, SWT.BEGINNING, false, false));

			txtFormal = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
			GridData gdFormal = new GridData(SWT.FILL, SWT.FILL, true, true);
//			gdFormal.minimumHeight = tblSubword.getItemHeight();
			gdFormal.minimumHeight = defaultHeight * 4;
			txtFormal.setLayoutData(gdFormal);

//			// 型		XXX:しばらく使用しないのでコメントアウト
//			Label lblType = new Label(composite, SWT.NONE);
//			lblType.setText(Messages.DictionaryEditView_6);
//			lblType.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
//
//			txtType = new Text(composite, SWT.BORDER | SWT.SINGLE);
//			txtType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
//			txtType.setSize(100, SWT.DEFAULT);

			return composite;
		}

		/**
		 * ボタンバーの作成
		 * @param parent
		 * @return
		 */
		protected Control createButtonBar(Composite parent) {

			Composite composite = new Composite(parent, SWT.NONE);
			composite.setLayoutData(new GridData(SWT.END, SWT.END, true, false));
			composite.setLayout(new GridLayout(0, false));

			Button btnOk = createButton(composite, OK, Messages.DictionaryEditView_7, false);
			createButton(composite, CANCEL, Messages.DictionaryEditView_8, false);

			// 未登録モデルの場合、OKボタン不可
			if (!ModelManager.getInstance().isResisteredModel(ttab.getDictionary().getDictionaryClass().model)) {
				btnOk.setEnabled(false);
			}

			return composite;
		}

		/**
		 * ボタンの作成
		 * @param parent
		 * @param id
		 * @param label
		 * @param defaultButton
		 * @return
		 */
		protected Button createButton(Composite parent, int id, String label,
				boolean defaultButton) {
			((GridLayout)parent.getLayout()).numColumns++;
			Button button = new Button(parent, SWT.PUSH);
			button.setText(label);
			button.setData(new Integer(id));
			button.addSelectionListener(getButtonListener());
			if (defaultButton) {
				Shell shell = parent.getShell();
				if (shell != null) {
					shell.setDefaultButton(button);
				}
			}
			button.setLayoutData(new GridData());
			return button;
		}

		/**
		 * 選択リスナー
		 * @return
		 */
		protected SelectionListener getButtonListener() {
			if (buttonListener == null) {
				buttonListener = new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						buttonPressed(((Integer) event.widget.getData()).intValue());
					}
				};
			}
			return buttonListener;
		}

		/**
		 * ボタン押下処理<br>
		 * 編集内容の確定または破棄を行い、タブを閉じる。
		 * @param buttonId <code>OK</code>:確定／<code>CANCEL</code>:破棄
		 */
		protected void buttonPressed(int buttonId) {

			// 変更の有無
			boolean isModified = false;

			if (buttonId == OK) {
				// 編集前の状態を退避
				Entry copy = entry.deepCopy();

				// 編集内容確定
				// 見出し語
				if (entry.getWord().equals(txtWord.getText()) == false) {
					entry.setWord(txtWord.getText());
					isModified = true;
				}
				// 副キーワード
				List<String> copySubwords = copyList(entry.getSubwords(), MAX_COL_SUBWORD);
				entry.getSubwords().clear();
				List<?> subwords = (List<?>)tvSubword.getInput();
				for (int i=0; i<MAX_COL_SUBWORD; i++) {
					String subword = PluginHelper.defaultIfBlank(((RowItem)subwords.get(i)).text, ""); //$NON-NLS-1$
					if (!subword.equals(copySubwords.get(i))) {
						isModified = true;
					}
					if (StringUtils.isNotBlank(subword)) {
						entry.getSubwords().add(subword);
					}
				}
				// 活用形
				List<String> copyConjugations = copyList(entry.getConjugations(), MAX_COL_CONJUGATION);
				entry.getConjugations().clear();
				List<?> conjugations = (List<?>)tvConjugation.getInput();
				for (int i=0; i<MAX_COL_CONJUGATION; i++) {
					String conjugation = PluginHelper.defaultIfBlank(((RowItem)conjugations.get(i)).text, ""); //$NON-NLS-1$
					if (!conjugation.equals(copyConjugations.get(i))) {
						isModified = true;
					}
					if (StringUtils.isNotBlank(conjugation)) {
						entry.getConjugations().add(conjugation);
					}
				}
				// 種別
//				if (entry.getCategoryNo() != cmbCategory.getSelectionIndex()) {
//					entry.setCategoryNo(cmbCategory.getSelectionIndex());
//					entry.setCategory(cmbCategory.getText());
//					isModified = true;
//				}
				if (entry.getCategoryNo() != categoryNoList.get(cmbCategory.getSelectionIndex())) {
					entry.setCategoryNo(categoryNoList.get(cmbCategory.getSelectionIndex()));
					entry.setCategory(cmbCategory.getText());
					isModified = true;
				}
				// 非形式的定義
				for (int i=0; i<txtInformal.length; i++) {
					if (entry.getInformals().get(i).equals(txtInformal[i].getText()) == false) {
						entry.getInformals().set(i, txtInformal[i].getText());
						isModified = true;
					}
				}
				// 形式的種別
				Integer sectionCd = (Integer)cmbSection.getData(cmbSection.getText());
				if (sectionCd == null) sectionCd = 0;
				if (entry.getSection() != sectionCd) {
//					entry.setSection(cmbSection.getSelectionIndex());
					entry.setSection(sectionCd);
					isModified = true;
				}
				// 形式的定義
				if (entry.getFormal().equals(txtFormal.getText()) == false) {
					entry.setFormal(txtFormal.getText());
					isModified = true;
				}
//				// 型
//				if (entry.getType().equals(txtType.getText()) == false) {
//					entry.setType(txtType.getText());
//					isModified = true;
//				}

				// 変更されている場合履歴を残す
				if (isModified) {
//					History history = new History(
//							History.TYPE_EDIT,
//							DIC_COL_ID_NONE,
//							DictionaryUtil.isInspectMode()? History.MODE_INSPECT : History.MODE_OUTPUT,
//							Integer.parseInt(txtNo.getText()) - 1,
//							copy,
//							entry.deepCopy(),
//							ttab.isDirty(),
//							true);
//					ttab.getHistoryManager().add(history);
					ttab.getHistoryHelper().addEditingHistory(
							copy, entry.deepCopy(), Integer.parseInt(txtNo.getText()) - 1);
				}
			} else {
				// 編集内容破棄
			}
			entry.setEdit(false);
			entry.setModified(isModified);

			update();
			ttab.showColumns();

			item.getControl().dispose();
			item.dispose();
			tabs.remove(this);
		}

		/**
		 * 更新処理
		 */
		protected void update() {
			for (Object listener : listeners.getListeners()) {
				if (listener instanceof IDictionaryEditTabListener) {
					((IDictionaryEditTabListener)listener).update(entry);
				}
			}
		}

		/**
		 * タブタイトルの設定
		 * @param string
		 */
		protected void setText(String string) {
			item.setText(string);
		}

		/**
		 * タブデータの設定
		 * @param entry
		 */
		protected void setEntry(Entry entry) {
			this.entry = entry;

			setText(entry.getWord());

			// 行番号
			txtNo.setText(String.valueOf(
					DictionaryUtil.isInspectMode()? entry.getSeqNo() : entry.getOutNo()));
			// 見出し語
			txtWord.setText(entry.getWord());
			// 種別
			cmbCategory.select(0);
			for (int i = 0; i < cmbCategory.getItemCount(); i++) {
				if (categoryNoList.get(i) == entry.getCategoryNo()) {
					cmbCategory.select(i);
				}
			}
			for (int i=0; i<txtInformal.length; i++) {
				txtInformal[i].setText(entry.getInformals().get(i));
			}
			// 非形式的種別
//			cmbSection.select(entry.getSection());
			if (entry.getSection() <= 0) {
				cmbSection.select(0);
			} else {
				cmbSection.setText(ModelManager.getInstance().getSectionName(ttab.getDictionary(), entry.getSection()));
			}
			// 形式的定義
			txtFormal.setText(entry.getFormal());

			// 副キーワード
			List<RowItem> subwords = new ArrayList<RowItem>();
			for (int i=0; i<MAX_COL_SUBWORD; i++) {
				if (i < entry.getSubwords().size()) {
					subwords.add(new RowItem(i + 1, entry.getSubwords().get(i)));
				} else {
					subwords.add(new RowItem(i + 1, "")); //$NON-NLS-1$
				}
			}
			tvSubword.setInput(subwords);

			// 活用形
			List<RowItem> conjugations = new ArrayList<RowItem>();
			for (int i=0; i<MAX_COL_CONJUGATION; i++) {
				if (i < entry.getConjugations().size()) {
					conjugations.add(new RowItem(i + 1, entry.getConjugations().get(i)));
				} else {
					conjugations.add(new RowItem(i + 1, "")); //$NON-NLS-1$
				}
			}
			tvConjugation.setInput(conjugations);
		}

		/**
		 * 番号の更新（リフレッシュ）
		 */
		protected void refreshNo() {
			txtNo.setText(String.valueOf(
					DictionaryUtil.isInspectMode()? entry.getSeqNo() : entry.getOutNo()));
		}

//		/**
//		 * テーブルタブの設定
//		 * @param ttab
//		 */
//		protected void setTableTab(TableTab ttab) {
////			this.ttab = ttab;		// コンストラクタでセットするよう変更
//
//			// 形式的種別の初期化
//			setCmbSection();
//		}

		/**
		 * 形式的種別の設定
		 */
		protected void setCmbSection() {

			cmbSection.removeAll();
			cmbSection.add(""); //$NON-NLS-1$

			for (Model.Section section : model.getSectionDefs()) {
				cmbSection.add(section.getName());
				cmbSection.setData(section.getName(), section.getCd());
			}
		}

		/**
		 * 形式的定義を補完する
		 */
		private void complementFormal() {
			// 形式的定義の補完を使用する場合
			if (ModelToolDictionaryPlugin.getDefault().getPreferenceStore().getBoolean(
					DictionaryPreferenceConstants.PK_FORMAL_DEFINITION_COMPLETION)) {
				if (cmbSection.getSelectionIndex() <= 0) return;
				if (PluginHelper.isEmpty(txtWord.getText())) return;
				if (!PluginHelper.isEmpty(txtFormal.getText())) return;
				ModelManager manager = ModelManager.getInstance();
				String model = manager.getModelKey(ttab.getDictionary());
				Integer sectionCd = (Integer)cmbSection.getData(cmbSection.getText());
				String template = manager.getTemplate(model, sectionCd);
				if (template != null) {
					txtFormal.setText(MessageFormat.format(template, txtWord.getText()));
				}
			}
		}
	}

	/**
	 * 副キーワード、活用形テーブルの行アイテムクラス
	 */
	private class RowItem {
		public int num;
		public String text;
		public RowItem(int num, String text) {
			this.num = num;
			this.text = text;
		}
	}

	/**
	 * 副キーワード、活用形テーブル用プロバイダ
	 */
	private class TableProvider extends LabelProvider
			implements IStructuredContentProvider, ITableLabelProvider {
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
		public String getColumnText(Object element, int columnIndex) {
			switch (columnIndex) {
				case 0:
					return String.valueOf(((RowItem)element).num);
				case 1:
					return ((RowItem)element).text;
				default:
					break;
			}
			return null;
		}
		public Object[] getElements(Object inputElement) {
			return ((List<?>)inputElement).toArray();
		}
	}

	/**
	 * 副キーワード、活用形テーブル用EditingSupport
	 */
	private class TableEditingSupport extends EditingSupport {
		private int index;
		public TableEditingSupport(ColumnViewer viewer, int index) {
			super(viewer);
			this.index = index;
		}
		protected CellEditor getCellEditor(Object element) {
			if (index == 1) {
				return new TextCellEditor((Composite)getViewer().getControl());
			}
			return null;
		}
		protected boolean canEdit(Object element) {
			if (index == 1) {
				return true;
			}
			return false;
		}
		protected Object getValue(Object element) {
			if (index == 1) {
				return ((RowItem)element).text;
			}
			return null;
		}
		protected void setValue(Object element, Object value) {
			if (index == 1) {
				((RowItem)element).text = (String)value;
				getViewer().update(element, null);
			}
		}
	}

	/**
	 * リストのコピー<br>
	 * コピー元のサイズが指定サイズより小さい場合は空文字を追加する
	 * @param org コピー元リスト
	 * @param size リストサイズ
	 * @return コピー結果リスト
	 */
	private List<String> copyList(List<String> org, int size) {
		List<String> copy = new ArrayList<String>();
		for (int i=0; i<org.size(); i++) {
			copy.add(org.get(i));
		}
		for (int i=org.size(); i<size; i++) {
			copy.add(""); //$NON-NLS-1$
		}
		return copy;
	}
}
