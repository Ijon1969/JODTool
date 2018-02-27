package jp.ac.kyushu_u.csce.modeltool.vdmslEditor.editor;

import jp.ac.kyushu_u.csce.modeltool.vdmslEditor.constants.VdmslConstants;

import org.eclipse.ui.editors.text.TextEditor;

/**
 * VDMエディタークラス
 *
 * @author KBK yoshimura
 */
public class VdmslEditor extends TextEditor {

	/** カラーマネージャ */
	private VdmslColorManager colorManager;

	/**
	 * コンストラクタ
	 */
	public VdmslEditor() {
		super();
		colorManager = new VdmslColorManager();
		setSourceViewerConfiguration(new VdmslConfiguration(colorManager));
		setDocumentProvider(new VdmslDocumentProvider());
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
		setKeyBindingScopes(new String[] {VdmslConstants.SCOPE_ID_VDMSL_EDITOR});
	}
}
