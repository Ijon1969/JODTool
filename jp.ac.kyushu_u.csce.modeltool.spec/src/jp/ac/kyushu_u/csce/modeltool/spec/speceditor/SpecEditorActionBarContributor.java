package jp.ac.kyushu_u.csce.modeltool.spec.speceditor;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;

/**
 * アクションバーのコントリビュータ
 *
 * @author KBK yoshimura
 */
public class SpecEditorActionBarContributor extends
		MultiPageEditorActionBarContributor {

	/**
	 * コンストラクタ
	 */
	public SpecEditorActionBarContributor() {
		super();
	}

	@Override
	public void setActivePage(IEditorPart activeEditor) {}
}
