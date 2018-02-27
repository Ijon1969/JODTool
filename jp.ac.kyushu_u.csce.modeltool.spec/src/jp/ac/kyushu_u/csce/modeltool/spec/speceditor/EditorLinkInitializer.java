package jp.ac.kyushu_u.csce.modeltool.spec.speceditor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryView;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.ITableTabInitializer;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;
import jp.ac.kyushu_u.csce.modeltool.spec.Messages;
import jp.ac.kyushu_u.csce.modeltool.spec.ModelToolSpecPlugin;
import jp.ac.kyushu_u.csce.modeltool.spec.constant.SpecPreferenceConstants;
import jp.ac.kyushu_u.csce.modeltool.spec.speceditor.handler.SpecInspector;
import jp.ac.kyushu_u.csce.modeltool.spec.speceditor.handler.SpecInspector.InspectException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.texteditor.IDocumentProvider;

/**
 * 辞書ビュー⇔仕様書エディターのリンク制御クラス
 */
public class EditorLinkInitializer implements ITableTabInitializer {

	/** プリファレンスリスナー */
	private IPropertyChangeListener prefListener = null;
	/** テーブルリスナー */
	private ISelectionChangedListener tableListener = null;

	/** ハイライト時の再マーキング実施フラグ */
	// TODO:2.1.0.v20141216_betaではtrueとする
	private boolean isRemark = false;

	/** 処理対象のプリファレンスキー */
	private static final String[] PREF_KEYS = {
		SpecPreferenceConstants.PK_LINK_DICTIONARY,
		SpecPreferenceConstants.PK_SPEC_HIGHLIGHT_BACKCOLOR,
		SpecPreferenceConstants.PK_SPEC_HIGHLIGHT_FORECOLOR,
		SpecPreferenceConstants.PK_SPEC_SUB_HIGHLIGHT_BACKCOLOR,
		SpecPreferenceConstants.PK_SPEC_SUB_HIGHLIGHT_FORECOLOR,
		SpecPreferenceConstants.PK_SPEC_CONJ_HIGHLIGHT_BACKCOLOR,
		SpecPreferenceConstants.PK_SPEC_CONJ_HIGHLIGHT_FORECOLOR,
		SpecPreferenceConstants.PK_USE_REGULAR_EXPRESSION,
	};

	@Override
	public void initialize(TableTab tab) {
		IPreferenceStore store = ModelToolSpecPlugin.getDefault().getPreferenceStore();
		prefListener = new PropertyChangeListener();
		store.addPropertyChangeListener(prefListener);

		tableListener = new SelectionChangedListener();
		tab.getTableViewer().addSelectionChangedListener(tableListener);
	}

	@Override
	public void finalize(TableTab tab) {
		IPreferenceStore store = ModelToolSpecPlugin.getDefault().getPreferenceStore();
		store.removePropertyChangeListener(prefListener);
		tab.getTableViewer().removeSelectionChangedListener(tableListener);
	}

	/**
	 * プリファレンスリスナー
	 */
	private class PropertyChangeListener implements IPropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent event) {
			IPreferenceStore store = ModelToolSpecPlugin.getDefault().getPreferenceStore();
			// 辞書ビューとのリンク有無
			if (PluginHelper.in(event.getProperty(), PREF_KEYS)) {
				// リンクなしの場合
				if (!store.getBoolean(SpecPreferenceConstants.PK_LINK_DICTIONARY)) {
					// アクティブページの取得
					IWorkbenchPage page = ModelToolSpecPlugin.getDefault().getWorkbench()
											.getActiveWorkbenchWindow().getActivePage();
					// すべての仕様書エディターのstyleをクリアする
					for (IEditorReference ref : page.getEditorReferences()) {
						IEditorPart editor = ref.getEditor(false);
						if (editor instanceof SpecEditor) {
							AbstractInternalTextEditor inEditor =
									(AbstractInternalTextEditor)((SpecEditor)editor).getSelectedPage();
							if (inEditor instanceof InternalTextEditor) {
								// styleのクリア
								inEditor.resetTextStyle();
							}
							// 直前のマーキングの復元
							if (isRemark) {
								try {
									new SpecInspector().inspectEditorAgain(editor);
								} catch (InspectException e) {
								}
							}
						};
					}
				// リンクありの場合
				} else {
					// アクティブページの取得
					IWorkbenchPage page = ModelToolSpecPlugin.getDefault().getWorkbench()
											.getActiveWorkbenchWindow().getActivePage();
					// 仕様書エディターの取得
					IEditorPart editor = (IEditorPart)page.getActiveEditor();
					if (editor == null || !(editor instanceof SpecEditor)) {
						return;
					}
					// 直前のマーキングの復元
					if (isRemark) {
						try {
							new SpecInspector().inspectEditorAgain(editor);
						} catch (InspectException e) {
						}
					}
					// 辞書ビューの取得
					DictionaryView view = (DictionaryView)page.findView(DictionaryConstants.PART_ID_DICTIONARY);
					if (view == null) return;
					// 選択したキーワードの強調表示
					highlightKeyword((SpecEditor)editor, view, null);
				}
			}
		}
	}

	/**
	 * 選択リスナー
	 */
	private class SelectionChangedListener implements ISelectionChangedListener {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {

//			// XXX:性能調査用コード
//			long start = System.nanoTime();

			// プリファレンスの取得
			IPreferenceStore store = ModelToolSpecPlugin.getDefault().getPreferenceStore();
			if (!store.getBoolean(SpecPreferenceConstants.PK_LINK_DICTIONARY)) return;

			// アクティブページの取得
			IWorkbenchPage page = ModelToolSpecPlugin.getDefault().getWorkbench()
									.getActiveWorkbenchWindow().getActivePage();
			// アクティブエディターの取得
			IEditorPart editor = (IEditorPart)page.getActiveEditor();
			if (editor != null && editor instanceof SpecEditor) {
				// 直前のマーキングの復元
				if (isRemark) {
					try {
						new SpecInspector().inspectEditorAgain(editor);
					} catch (InspectException e) {
					}
				}
				// キーワードの強調表示
				highlightKeyword((SpecEditor)editor, null, event);
			}

//			// XXX:性能調査用コード
//			long end = System.nanoTime();
//			System.out.println((end - start)/1000000f);
		}
	}

	/**
	 * キーワードの強調表示
	 * @param editor 仕様書エディター
	 * @param view 辞書ビュー
	 * @param event 選択変更イベント
	 */
	private void highlightKeyword(SpecEditor editor, DictionaryView view, SelectionChangedEvent event) {

		// 選択
		IStructuredSelection selection = null;

		// 辞書ビュー、イベントの両方未指定の場合処理なし
		if (view == null && event == null) return;

		// 辞書ビューが指定された場合
		if (view != null) {
			TableTab tab = view.getActiveTableTab(false);
			if (tab == null) return;		// タブ未選択の場合処理なし
			selection = (IStructuredSelection)tab.getTableViewer().getSelection();
		}
		// イベントが指定された場合
		if (event != null) {
			selection = (IStructuredSelection)event.getSelection();
		}

		// VDMエディターからドキュメントを取得
		AbstractInternalTextEditor inEditor = (AbstractInternalTextEditor)editor.getSelectedPage();
		if (!(inEditor instanceof InternalTextEditor)) return;		// Excelの場合は処理なし
		IDocumentProvider prov =inEditor.getDocumentProvider();
		IDocument document = prov.getDocument(editor.getEditorInput());
		FindReplaceDocumentAdapter adapter = new FindReplaceDocumentAdapter(document);
		StyledText styledText = inEditor.getStyledText();

		// styleのクリア
		if (!isRemark) {
			styledText.setStyleRanges(new StyleRange[]{});
		}

		// 選択された見出し語を取得
		if (selection == null) return;		// 行未選択の場合処理なし
		Entry entry = (Entry)selection.getFirstElement();
		if (entry == null) return;

		// キーワード、副キーワード、活用形を一つのリストに纏める
		List<Keyword> words = new ArrayList<Keyword>();
		if (StringUtils.isNotBlank(entry.getWord())) {
			words.add(new Keyword(entry.getWord(), KeywordKind.WORD));
		}
		for (String subword : entry.getSubwords()) {
			if (StringUtils.isNotBlank(subword)) {
				words.add(new Keyword(subword, KeywordKind.SUBWORD));
			}
		}
		for (String conjugation : entry.getConjugations()) {
			if (StringUtils.isNotBlank(conjugation)) {
				words.add(new Keyword(conjugation, KeywordKind.CONJUGATION));
			}
		}
		if (words.isEmpty()) return;

		// プリファレンスストアの取得
		IPreferenceStore store = ModelToolSpecPlugin.getDefault().getPreferenceStore();
		// キーワードの色設定
		RGB backColorKey = PreferenceConverter.getColor(store, SpecPreferenceConstants.PK_SPEC_HIGHLIGHT_BACKCOLOR);
		RGB foreColorKey = PreferenceConverter.getColor(store, SpecPreferenceConstants.PK_SPEC_HIGHLIGHT_FORECOLOR);
		// 副キーワードの色設定
		RGB backColorSub = PreferenceConverter.getColor(store, SpecPreferenceConstants.PK_SPEC_SUB_HIGHLIGHT_BACKCOLOR);
		RGB foreColorSub = PreferenceConverter.getColor(store, SpecPreferenceConstants.PK_SPEC_SUB_HIGHLIGHT_FORECOLOR);
		// 活用形の色設定
		RGB backColorCon = PreferenceConverter.getColor(store, SpecPreferenceConstants.PK_SPEC_CONJ_HIGHLIGHT_BACKCOLOR);
		RGB foreColorCon = PreferenceConverter.getColor(store, SpecPreferenceConstants.PK_SPEC_CONJ_HIGHLIGHT_FORECOLOR);

		// プリファレンスストアから正規表現フラグを取得
		boolean regExSearch = store.getBoolean(SpecPreferenceConstants.PK_USE_REGULAR_EXPRESSION);

		// 優先度を活用形＞キーワード＞副キーワードとするため、逆順で検索・強調表示する
		for (int i=words.size()-1; i>=0; i--) {

			Keyword hlKeyeord = words.get(i);
			String word = hlKeyeord.keyword;

			// 正規表現ONの場合、正規表現チェックエラーのキーワードはスキップ
			if (regExSearch) {
				try {
					Pattern.compile(word);
				} catch (PatternSyntaxException pse) {
					continue;
				}
			}

			// キーワードと副キーワードで別色を使用する
			RGB foreColor = null;
			RGB backColor = null;
			switch (hlKeyeord.kind) {
				case WORD:
					foreColor = foreColorKey;
					backColor = backColorKey;
					break;
				case SUBWORD:
					foreColor = foreColorSub;
					backColor = backColorSub;
					break;
				case CONJUGATION:
					foreColor = foreColorCon;
					backColor = backColorCon;
					break;
				default:
					continue;
			}

			// 現在の選択位置
			int offset = 0;

			try {
				// 検索処理実行
				IRegion region = adapter.find(offset, word, true, true, false, regExSearch);
				while (region != null) {

					// 検索結果を強調表示
					StyleRange range = new StyleRange(
							region.getOffset(),
							region.getLength(),
							ModelToolSpecPlugin.getColorManager().getColor(foreColor),
							ModelToolSpecPlugin.getColorManager().getColor(backColor),
							SWT.BOLD);
					styledText.setStyleRange(range);

					// 次を検索
					offset = region.getOffset() + region.getLength();
					region = adapter.find(offset, word, true, true, false, regExSearch);
				}
			} catch(BadLocationException e) {
				IStatus status = new Status(IStatus.ERROR, ModelToolSpecPlugin.PLUGIN_ID,
						Messages.EditorLinkInitializer_0, e);
				ModelToolSpecPlugin.getDefault().getLog().log(status);
			}
		}
	}

	/** キーワード種別 */
	private enum KeywordKind {
		/** 見出し語 */
		WORD,
		/** 副キーワード */
		SUBWORD,
		/** 活用形 */
		CONJUGATION,
	}

	/**
	 * キーワードハイライト用クラス
	 */
	private class Keyword {
		/** キーワード */
		public String keyword;
		/** キーワード種別 */
		public KeywordKind kind;
		/**
		 * コンストラクタ
		 * @param keyword キーワード
		 * @param kind キーワード種別
		 */
		public Keyword(String keyword, KeywordKind kind) {
			this.keyword = keyword;
			this.kind = kind;
		}
	}
}
