package jp.ac.kyushu_u.csce.modeltool.vdmrtEditor.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

/**
 * テキストエディター設定クラス
 * @author KBK yoshimura
 */
public class VdmrtConfiguration extends SourceViewerConfiguration {
	private VdmrtDoubleClickStrategy doubleClickStrategy;
	private VdmrtColorManager colorManager;

	/**
	 * コンストラクタ
	 * @param colorManager
	 */
	public VdmrtConfiguration(VdmrtColorManager colorManager) {
		this.colorManager = colorManager;
	}

	/**
	 * @see SourceViewerConfiguration#getConfiguredContentTypes(ISourceViewer)
	 */
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {

		// パーティション定数の配列を返す
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,					// デフォルト
			VdmrtPartitionScanner.VDM_CHARACTER,				// 文字
			VdmrtPartitionScanner.VDM_STRING,					// 文字列
			VdmrtPartitionScanner.VDM_SINGLE_LINE_COMMENT,	// 1行コメント
			VdmrtPartitionScanner.VDM_MULTI_LINE_COMMENT,		// 複数行コメント
			VdmrtPartitionScanner.VDM_KEYWORD,				// キーワード
		};
	}

	/**
	 * ダブルクリックの動作設定
	 * @see SourceViewerConfiguration#getDoubleClickStrategy(ISourceViewer, String)
	 */
	public ITextDoubleClickStrategy getDoubleClickStrategy(
			ISourceViewer sourceViewer,
			String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new VdmrtDoubleClickStrategy();
		return doubleClickStrategy;
	}

	/**
	 * パーティションごとの書式を設定する
	 * @see SourceViewerConfiguration#getPresentationReconciler(ISourceViewer)
	 */
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {

		// リコンサイラ（戻り値）
		PresentationReconciler reconciler = new PresentationReconciler();

		// ダメージャ・リペアラ
		VdmrtNonRuleBasedDamagerRepairer ndr = null;

		// デフォルト書式
		ndr = new VdmrtNonRuleBasedDamagerRepairer(
					new TextAttribute(colorManager.getColor(IVdmrtColorConstants.DEFAULT)));
		reconciler.setDamager(ndr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(ndr, IDocument.DEFAULT_CONTENT_TYPE);

		// 文字列書式
		ndr = new VdmrtNonRuleBasedDamagerRepairer(
				new TextAttribute(colorManager.getColor(IVdmrtColorConstants.STRING)));
		reconciler.setDamager(ndr, VdmrtPartitionScanner.VDM_STRING);
		reconciler.setRepairer(ndr, VdmrtPartitionScanner.VDM_STRING);

		// 文字書式
		ndr = new VdmrtNonRuleBasedDamagerRepairer(
				new TextAttribute(colorManager.getColor(IVdmrtColorConstants.CHARACTER)));
		reconciler.setDamager(ndr, VdmrtPartitionScanner.VDM_CHARACTER);
		reconciler.setRepairer(ndr, VdmrtPartitionScanner.VDM_CHARACTER);

		// コメント書式
		ndr = new VdmrtNonRuleBasedDamagerRepairer(
				new TextAttribute(colorManager.getColor(IVdmrtColorConstants.VDM_COMMENT)));
		reconciler.setDamager(ndr, VdmrtPartitionScanner.VDM_MULTI_LINE_COMMENT);
		reconciler.setRepairer(ndr, VdmrtPartitionScanner.VDM_MULTI_LINE_COMMENT);
		reconciler.setDamager(ndr, VdmrtPartitionScanner.VDM_SINGLE_LINE_COMMENT);
		reconciler.setRepairer(ndr, VdmrtPartitionScanner.VDM_SINGLE_LINE_COMMENT);

		// キーワード書式
		ndr = new VdmrtNonRuleBasedDamagerRepairer(
				new TextAttribute(colorManager.getColor(IVdmrtColorConstants.RESERVED), null, 1));
		reconciler.setDamager(ndr, VdmrtPartitionScanner.VDM_KEYWORD);
		reconciler.setRepairer(ndr, VdmrtPartitionScanner.VDM_KEYWORD);

		return reconciler;
	}

}
