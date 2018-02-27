package jp.ac.kyushu_u.csce.modeltool.spec.speceditor;

import jp.ac.kyushu_u.csce.modeltool.spec.Messages;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

/**
 * Excel仕様書エディターのアウトラインビューを表示するクラス
 * @author KBK yoshimura
 */
public class ExcelEditorOutlinePage extends ContentOutlinePage {

	/** Excelエディター */
	private SpecEditor editor;

	/** コンストラクタ */
	private ExcelEditorOutlinePage() {
		super();
	}

	/** コンストラクタ */
	public ExcelEditorOutlinePage(SpecEditor editor) {
		this();

		this.editor = editor;
	}

	/**
	 * コントロール作成
	 * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);

		// ビューアの設定
		TreeViewer tv = getTreeViewer();
		tv.setLabelProvider(new TreeLabelProvider());
		tv.setContentProvider(new TreeContentProvider());

		// リスナーの追加
		// 選択アイテムが変更された場合の処理
		tv.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				// 選択アイテムに対応するエディター内の行へ移動
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object element = selection.getFirstElement();
				if (element instanceof ExcelEditorItem) {
					editor.selectAndReveal((ExcelEditorItem)element);
				}
			}
		});

		setContent();
	}

	/**
	 * Excelエディターのアクティブなページにモデルを設定する
	 */
	public void setContent() {

		InternalExcelEditor internalEditor = (InternalExcelEditor)editor.getSelectedPage();
		ExcelEditorItem root = new ExcelEditorItem();
		root.setChildren(((ExcelDocumentProvider)internalEditor.getDocumentProvider()).items);

		getTreeViewer().setInput(root);
	}

	/**
	 * コンテントプロバイダークラス
	 * @author KBK yoshimura
	 */
	class TreeContentProvider implements ITreeContentProvider {

		public Object[] getChildren(Object parentElement) {
			return ((ExcelEditorItem)parentElement).getChildren().toArray();
		}

		public Object getParent(Object element) {
			return ((ExcelEditorItem)element).getParent();
		}

		public boolean hasChildren(Object element) {
			return ! ((ExcelEditorItem)element).getChildren().isEmpty();
		}

		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	/**
	 * ラベルプロバイダークラス
	 * @author KBK yoshimura
	 */
	class TreeLabelProvider extends LabelProvider {

		public String getText(Object element) {

			ExcelEditorItem item = (ExcelEditorItem)element;

			// 空アイテムの場合
			if (item.isEmpty()) {
				return Messages.ExcelEditorOutlinePage_0;
			}
			return item.getContent();
		}
	}
}
