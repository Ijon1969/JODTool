package jp.ac.kyushu_u.csce.modeltool.base.dialog;

import jp.ac.kyushu_u.csce.modeltool.base.utility.ScopeUtility;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.WhitespaceCharacterPainter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.texteditor.AbstractTextEditor;

/**
 * 複数行入力ダイアログ
 *
 * @author yoshimura
 */
public class MultiInputDialog2 extends Dialog {
	/** タイトル */
	private String title;
	/** テキストの値 */
	private String value = "";//$NON-NLS-1$
//	/** テキストウィジェット */
//	private Text text;
	/** テキストビューアー */
	private ITextViewer viewer;
	/** 最小行数 */
	private int minimumLine;

	/**
	 * コンストラクタ
	 * @param parentShell シェル
	 * @param dialogTitle ダイアログタイトル
	 * @param initialValue 初期値
	 * @param minimumLine 最少行数
	 */
	public MultiInputDialog2(Shell parentShell, String dialogTitle,
			String initialValue, int minimumLine) {
		super(parentShell);
		this.title = dialogTitle;
		if (initialValue == null) {
			value = "";//$NON-NLS-1$
		} else {
			value = initialValue;
		}
		this.minimumLine = (minimumLine < 1)? 1 : minimumLine;
	}


	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
//			value = text.getText();
			value = viewer.getTextWidget().getText();
		} else {
			value = null;
		}
		super.buttonPressed(buttonId);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (title != null) {
			shell.setText(title);
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// OKボタン
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		// キャンセルボタン
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		// テキストにフォーカスし、全選択
//		text.setFocus();
//		if (value != null) {
//			text.setText(value);
//			text.selectAll();
//		}
		viewer.getTextWidget().setFocus();
		if (value != null) {
			viewer.getTextWidget().setText(value);
			viewer.getTextWidget().selectAll();
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
//		text = new Text(composite, getInputTextStyle());
//		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//			GridData gridData = new GridData(GridData.GRAB_HORIZONTAL
//					| GridData.HORIZONTAL_ALIGN_FILL);
//			gridData.verticalAlignment = SWT.FILL;
//			gridData.grabExcessVerticalSpace = true;
//			text.setLayoutData(gridData);

		viewer = new TextViewer(composite, getInputTextStyle());
		viewer.getTextWidget().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewer.setDocument(new Document(value));

		// 空白文字の表示設定
		IPreferenceStore store = new ScopedPreferenceStore(ScopeUtility.getInstanceScope(), EditorsUI.PLUGIN_ID);
		if (store.getBoolean(AbstractTextEditor.PREFERENCE_SHOW_WHITESPACE_CHARACTERS)) {
			IPainter painter = null;
			try {
				painter = new WhitespaceCharacterPainter(
						viewer,
						store.getBoolean(AbstractTextEditor.PREFERENCE_SHOW_LEADING_SPACES),
						store.getBoolean(AbstractTextEditor.PREFERENCE_SHOW_ENCLOSED_SPACES),
						store.getBoolean(AbstractTextEditor.PREFERENCE_SHOW_TRAILING_SPACES),
						store.getBoolean(AbstractTextEditor.PREFERENCE_SHOW_LEADING_IDEOGRAPHIC_SPACES),
						store.getBoolean(AbstractTextEditor.PREFERENCE_SHOW_ENCLOSED_IDEOGRAPHIC_SPACES),
						store.getBoolean(AbstractTextEditor.PREFERENCE_SHOW_TRAILING_IDEOGRAPHIC_SPACES),
						store.getBoolean(AbstractTextEditor.PREFERENCE_SHOW_LEADING_TABS),
						store.getBoolean(AbstractTextEditor.PREFERENCE_SHOW_ENCLOSED_TABS),
						store.getBoolean(AbstractTextEditor.PREFERENCE_SHOW_TRAILING_TABS),
						store.getBoolean(AbstractTextEditor.PREFERENCE_SHOW_CARRIAGE_RETURN),
						store.getBoolean(AbstractTextEditor.PREFERENCE_SHOW_LINE_FEED),
						store.getInt(AbstractTextEditor.PREFERENCE_WHITESPACE_CHARACTER_ALPHA_VALUE)
						);
			} catch (NoSuchFieldError e) {
				// 3.6以前だとAbstractTextEditorの各定数が未定義のため
				painter = new WhitespaceCharacterPainter(viewer);
			} catch (NoSuchMethodError e) {
				// 3.6以前だと上記コンストラクタ未定義のため
				painter = new WhitespaceCharacterPainter(viewer);
			}
			if (painter != null)
				((ITextViewerExtension2)viewer).addPainter(painter);
		}

		// ダイアログ・フォントの適用
		applyDialogFont(composite);
		// プリファレンスストアからテキスト・フォント設定を取得
		viewer.getTextWidget().setFont(JFaceResources.getTextFont());
		return composite;
	}

	/**
	 * 値の取得
	 * @return 入力された値
	 */
	public String getValue() {
		return value;
	}

	/**
	 * テキストのスタイルの取得
	 * @return テキストのスタイル
	 */
	protected int getInputTextStyle() {
		return SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected Point getInitialSize() {
		Point point = super.getInitialSize();
		int line = (getValue() == null)? 0 : getValue().split("\r|\n|\r\n").length; //$NON-NLS-1$
		if (line < minimumLine) {
			point.y += convertHeightInCharsToPixels(minimumLine - line);
		}
		return point;
	}
}
