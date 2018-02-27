package jp.ac.kyushu_u.csce.modeltool.vdmslEditor.editor;

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
public class VdmslConfiguration extends SourceViewerConfiguration {
	private VdmslDoubleClickStrategy doubleClickStrategy;
	private VdmslColorManager colorManager;

	/**
	 * コンストラクタ
	 * @param colorManager
	 */
	public VdmslConfiguration(VdmslColorManager colorManager) {
		this.colorManager = colorManager;
	}

	/**
	 * @see SourceViewerConfiguration#getConfiguredContentTypes(ISourceViewer)
	 */
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {

		// パーティション定数の配列を返す
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,					// デフォルト
			VdmslPartitionScanner.VDM_CHARACTER,				// 文字
			VdmslPartitionScanner.VDM_STRING,					// 文字列
			VdmslPartitionScanner.VDM_SINGLE_LINE_COMMENT,	// 1行コメント
			VdmslPartitionScanner.VDM_MULTI_LINE_COMMENT,		// 複数行コメント
			VdmslPartitionScanner.VDM_KEYWORD,				// キーワード
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
			doubleClickStrategy = new VdmslDoubleClickStrategy();
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
		VdmslNonRuleBasedDamagerRepairer ndr = null;

		// デフォルト書式
		ndr = new VdmslNonRuleBasedDamagerRepairer(
					new TextAttribute(colorManager.getColor(IVdmslColorConstants.DEFAULT)));
		reconciler.setDamager(ndr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(ndr, IDocument.DEFAULT_CONTENT_TYPE);

		// 文字列書式
        ndr = new VdmslNonRuleBasedDamagerRepairer(
        		new TextAttribute(colorManager.getColor(IVdmslColorConstants.STRING)));
        reconciler.setDamager(ndr, VdmslPartitionScanner.VDM_STRING);
        reconciler.setRepairer(ndr, VdmslPartitionScanner.VDM_STRING);

        // 文字書式
        ndr = new VdmslNonRuleBasedDamagerRepairer(
        		new TextAttribute(colorManager.getColor(IVdmslColorConstants.CHARACTER)));
        reconciler.setDamager(ndr, VdmslPartitionScanner.VDM_CHARACTER);
        reconciler.setRepairer(ndr, VdmslPartitionScanner.VDM_CHARACTER);

        // コメント書式
        ndr = new VdmslNonRuleBasedDamagerRepairer(
        		new TextAttribute(colorManager.getColor(IVdmslColorConstants.VDM_COMMENT)));
        reconciler.setDamager(ndr, VdmslPartitionScanner.VDM_MULTI_LINE_COMMENT);
        reconciler.setRepairer(ndr, VdmslPartitionScanner.VDM_MULTI_LINE_COMMENT);
        reconciler.setDamager(ndr, VdmslPartitionScanner.VDM_SINGLE_LINE_COMMENT);
        reconciler.setRepairer(ndr, VdmslPartitionScanner.VDM_SINGLE_LINE_COMMENT);

        // キーワード書式
        ndr = new VdmslNonRuleBasedDamagerRepairer(
        		new TextAttribute(colorManager.getColor(IVdmslColorConstants.RESERVED), null, 1));
        reconciler.setDamager(ndr, VdmslPartitionScanner.VDM_KEYWORD);
        reconciler.setRepairer(ndr, VdmslPartitionScanner.VDM_KEYWORD);

		return reconciler;
	}

}