package jp.ac.kyushu_u.csce.modeltool.vdmrtEditor.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

/**
 * VDMエディターのドキュメントプロバイダ
 * @author KBK yoshimura
 */
public class VdmrtDocumentProvider extends FileDocumentProvider {

	/**
	 * ドキュメントの作成
	 * @see FileDocumentProvider#createDocument(Object)
	 */
	protected IDocument createDocument(Object element) throws CoreException {

		IDocument document = super.createDocument(element);
		if (document != null) {

			// パーティショナの作成
			IDocumentPartitioner partitioner =
				new FastPartitioner(
					new VdmrtPartitionScanner(),
					new String[] {
						VdmrtPartitionScanner.VDM_STRING,
						VdmrtPartitionScanner.VDM_CHARACTER,
						VdmrtPartitionScanner.VDM_SINGLE_LINE_COMMENT,
						VdmrtPartitionScanner.VDM_MULTI_LINE_COMMENT,
						VdmrtPartitionScanner.VDM_KEYWORD,
					});
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}
}