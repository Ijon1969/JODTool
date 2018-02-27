package jp.ac.kyushu_u.csce.modeltool.vdmEditor.editor;

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
public class VDMConfiguration extends SourceViewerConfiguration {
	private VDMDoubleClickStrategy doubleClickStrategy;
	private ColorManager colorManager;

	/**
	 * コンストラクタ
	 * @param colorManager
	 */
	public VDMConfiguration(ColorManager colorManager) {
		this.colorManager = colorManager;
	}

	/**
	 * @see SourceViewerConfiguration#getConfiguredContentTypes(ISourceViewer)
	 */
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {

		// パーティション定数の配列を返す
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,					// デフォルト
			VDMPartitionScanner.VDM_CHARACTER,				// 文字
			VDMPartitionScanner.VDM_STRING,					// 文字列
			VDMPartitionScanner.VDM_SINGLE_LINE_COMMENT,	// 1行コメント
			VDMPartitionScanner.VDM_MULTI_LINE_COMMENT,		// 複数行コメント
			VDMPartitionScanner.VDM_KEYWORD,				// キーワード
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
			doubleClickStrategy = new VDMDoubleClickStrategy();
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
		NonRuleBasedDamagerRepairer ndr = null;

		// デフォルト書式
		ndr = new NonRuleBasedDamagerRepairer(
					new TextAttribute(colorManager.getColor(IVDMColorConstants.DEFAULT)));
		reconciler.setDamager(ndr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(ndr, IDocument.DEFAULT_CONTENT_TYPE);

		// 文字列書式
        ndr = new NonRuleBasedDamagerRepairer(
        		new TextAttribute(colorManager.getColor(IVDMColorConstants.STRING)));
        reconciler.setDamager(ndr, VDMPartitionScanner.VDM_STRING);
        reconciler.setRepairer(ndr, VDMPartitionScanner.VDM_STRING);

        // 文字書式
        ndr = new NonRuleBasedDamagerRepairer(
        		new TextAttribute(colorManager.getColor(IVDMColorConstants.CHARACTER)));
        reconciler.setDamager(ndr, VDMPartitionScanner.VDM_CHARACTER);
        reconciler.setRepairer(ndr, VDMPartitionScanner.VDM_CHARACTER);

        // コメント書式
        ndr = new NonRuleBasedDamagerRepairer(
        		new TextAttribute(colorManager.getColor(IVDMColorConstants.VDM_COMMENT)));
        reconciler.setDamager(ndr, VDMPartitionScanner.VDM_MULTI_LINE_COMMENT);
        reconciler.setRepairer(ndr, VDMPartitionScanner.VDM_MULTI_LINE_COMMENT);
        reconciler.setDamager(ndr, VDMPartitionScanner.VDM_SINGLE_LINE_COMMENT);
        reconciler.setRepairer(ndr, VDMPartitionScanner.VDM_SINGLE_LINE_COMMENT);

        // キーワード書式
        ndr = new NonRuleBasedDamagerRepairer(
        		new TextAttribute(colorManager.getColor(IVDMColorConstants.RESERVED), null, 1));
        reconciler.setDamager(ndr, VDMPartitionScanner.VDM_KEYWORD);
        reconciler.setRepairer(ndr, VDMPartitionScanner.VDM_KEYWORD);

		return reconciler;
	}

}