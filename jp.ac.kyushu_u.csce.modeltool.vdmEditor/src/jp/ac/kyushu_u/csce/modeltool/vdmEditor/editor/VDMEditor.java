package jp.ac.kyushu_u.csce.modeltool.vdmEditor.editor;

import org.eclipse.ui.editors.text.TextEditor;

/**
 * VDMエディタークラス
 *
 * @author KBK yoshimura
 */
public class VDMEditor extends TextEditor {

	/** カラーマネージャ */
	private ColorManager colorManager;

	/**
	 * コンストラクタ
	 */
	public VDMEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new VDMConfiguration(colorManager));
		setDocumentProvider(new VDMDocumentProvider());
	}

	/**
	 * @see TextEditor#dispose()
	 */
	public void dispose() {
		colorManager.dispose();	// カラーマネージャのdispose
		super.dispose();
	}

	/**
	 * @see TextEditor#initializeKeyBindingScopes
	 */
	protected void initializeKeyBindingScopes() {
		setKeyBindingScopes(new String[] {VDMConstants.SCOPE_ID_VDMEDITOR});
	}
}
