package jp.ac.kyushu_u.csce.modeltool.vdmrtEditor.editor;

import jp.ac.kyushu_u.csce.modeltool.vdmrtEditor.constants.VdmrteditorConstants;
import jp.ac.kyushu_u.csce.modeltool.vdmrtEditor.editor.VdmrtColorManager;

import org.eclipse.ui.editors.text.TextEditor;

/**
 * VDMエディタークラス
 *
 * @author KBK yoshimura
 */
public class VdmrtEditor extends TextEditor {

	/** カラーマネージャ */
	private VdmrtColorManager colorManager;

	/**
	 * コンストラクタ
	 */
	public VdmrtEditor() {
		super();
		colorManager = new VdmrtColorManager();
		setSourceViewerConfiguration(new VdmrtConfiguration(colorManager));
		setDocumentProvider(new VdmrtDocumentProvider());
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
		setKeyBindingScopes(new String[] {VdmrteditorConstants.SCOPE_ID_VDMRT_EDITOR});
	}
}
