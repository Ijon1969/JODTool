package jp.ac.kyushu_u.csce.modeltool.spec.speceditor;

import jp.ac.kyushu_u.csce.modeltool.spec.ModelToolSpecPlugin;
import jp.ac.kyushu_u.csce.modeltool.spec.constant.SpecConstants;
import jp.ac.kyushu_u.csce.modeltool.spec.constant.SpecPreferenceConstants;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.TextEditor;

/**
 * 仕様書エディタの内部エディタ抽象クラス
 *
 * @author KBK yoshimura
 */
public abstract class AbstractInternalTextEditor extends TextEditor {

	/** ポップアップメニューの追加グループ名 */
	private static final String POPUP_TOP = "top"; //$NON-NLS-1$

	/** プリファレンスストア */
	private IPreferenceStore store;

	/** プロパティチェンジリスナー */
	protected IPropertyChangeListener propertyListener;


	/**
	 * コンストラクタ
	 */
	protected AbstractInternalTextEditor() {
		super();
	}

	/**
	 * エディターパート作成
	 */
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		// プリファレンスストアの取得
		store = ModelToolSpecPlugin.getDefault().getPreferenceStore();

		// リスナーをプリファレンスストアに登録
		propertyListener = new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				// 折り返し設定の変更
				if (SpecPreferenceConstants.PK_SPECEDITOR_WORDWRAP.equals(event.getProperty())) {
					StyledText styledText = getStyledText();
					boolean state = (Boolean)event.getNewValue();
					styledText.setWordWrap(state);
				}
			}
		};
		store.addPropertyChangeListener(propertyListener);

		// 折り返し設定（初期値：折り返し無し）
		StyledText styledText = getSourceViewer().getTextWidget();
		boolean state = store.getBoolean(SpecPreferenceConstants.PK_SPECEDITOR_WORDWRAP);
		styledText.setWordWrap(state);
	}

	/**
	 * StyledTextを取得する
	 * @return StyledText
	 */
	public StyledText getStyledText() {
		return getSourceViewer().getTextWidget();
	}

	/**
	 * IDocumentを取得する
	 * @return IDocument
	 */
	public IDocument getDocument() {
		return getSourceViewer().getDocument();
	}

	/**
	 * キーバインドスコープの設定
	 */
	protected void initializeKeyBindingScopes() {
		setKeyBindingScopes(new String[] {SpecConstants.KB_SCOPE_ID_SPECEDITOR});
	}

	/**
	 *
	 */
	protected void editorContextMenuAboutToShow(IMenuManager menu) {

		// コンテキストメニューの先頭にセパレータを追加
		menu.add(new Separator(POPUP_TOP));
		getSite().registerContextMenu((MenuManager)menu, getSite().getSelectionProvider());

		super.editorContextMenuAboutToShow(menu);
	}

	/**
	 * テキストスタイルのリセット
	 */
	public void resetTextStyle(){

		StyledText text = getStyledText();
		StyleRange range = new StyleRange(0, getDocument().getLength(), null, null);
		text.setStyleRange(range);
	}

	/**
	 * dispose
	 */
	public void dispose() {

		// リスナーの削除
		store.removePropertyChangeListener(propertyListener);

		super.dispose();
	}
}
