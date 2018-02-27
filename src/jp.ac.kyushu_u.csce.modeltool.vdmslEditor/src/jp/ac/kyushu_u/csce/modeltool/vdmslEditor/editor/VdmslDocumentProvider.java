package jp.ac.kyushu_u.csce.modeltool.vdmslEditor.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

/**
 * VDMエディターのドキュメントプロバイダ
 * @author KBK yoshimura
 */
public class VdmslDocumentProvider extends FileDocumentProvider {

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
					new VdmslPartitionScanner(),
					new String[] {
						VdmslPartitionScanner.VDM_STRING,
						VdmslPartitionScanner.VDM_CHARACTER,
						VdmslPartitionScanner.VDM_SINGLE_LINE_COMMENT,
						VdmslPartitionScanner.VDM_MULTI_LINE_COMMENT,
						VdmslPartitionScanner.VDM_KEYWORD,
					});
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}
}