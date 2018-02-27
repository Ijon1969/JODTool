package jp.ac.kyushu_u.csce.modeltool.spec.speceditor;

import java.util.Iterator;
import java.util.List;

import jp.ac.kyushu_u.csce.modeltool.base.utility.ColorName;
import jp.ac.kyushu_u.csce.modeltool.spec.ModelToolSpecPlugin;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

/**
 * 仕様書エディタの内部Excelエディタクラス
 *
 * @author KBK yoshimura
 */
public class InternalExcelEditor extends AbstractInternalTextEditor {

	/** シート番号 */
	private int sheetIndex;

	/** ドラッグ・アンド・ドロップの設定有無 */
	private boolean fIsTextDragAndDropInstalled= false;

	/**
	 * コンストラクタ
	 */
	private InternalExcelEditor() {
		super();
	}

	/**
	 * コンストラクタ
	 * @param sheetIndex シート番号
	 */
	public InternalExcelEditor(int sheetIndex) {

		this();

		this.sheetIndex = sheetIndex;
		setDocumentProvider(new ExcelDocumentProvider(this.sheetIndex));
	}

	/*
	 * コントロールの作成
	 */
	@Override
	public void createPartControl(Composite parent) {

		super.createPartControl(parent);

		// 行スタイル設定
		setLineStyle(getExcelItems(), 0);
	}

	/**
	 * 行のスタイル設定
	 * @param list
	 * @param lineIndex
	 * @return
	 */
	private int setLineStyle(List<ExcelEditorItem> list, int lineIndex) {

		int retIndex = lineIndex;
		StyledText styledText = getSourceViewer().getTextWidget();

		for (Iterator<ExcelEditorItem> itr = list.iterator(); itr.hasNext(); ) {
			ExcelEditorItem item = itr.next();
			if (item.isEmpty() == false) {

				// インデント
				styledText.setLineIndent(retIndex, 1, 25 * item.getLevel());

				switch (item.getLevel()) {

					case 0:		// A列
						StyleRange range0 = new StyleRange(
								styledText.getOffsetAtLine(retIndex),
								styledText.getLine(retIndex).length(),
								null,
								ModelToolSpecPlugin.getColorManager().getColor(ColorName.RGB_LIGHTCYAN),
								SWT.BOLD);
						styledText.setStyleRange(range0);
						break;

					case 1:		// B列
						StyleRange range1 = new StyleRange(
								styledText.getOffsetAtLine(retIndex),
								styledText.getLine(retIndex).length(),
								ModelToolSpecPlugin.getColorManager().getColor(ColorName.RGB_SADDLEBROWN),
								null,
								SWT.NORMAL);
						range1.underline = true;
						styledText.setStyleRange(range1);

					default:
						break;
				}

				retIndex++;
			}

			retIndex = setLineStyle(item.getChildren(), retIndex);
		}

		return retIndex;
	}

	private List<ExcelEditorItem> getExcelItems() {

		return ((ExcelDocumentProvider)getDocumentProvider()).items;
	}

	/**
	 * 書式のリセット
	 */
	public void resetTextStyle() {

		super.resetTextStyle();
		setLineStyle(getExcelItems(), 0);
	}

	/**
	 * 指定アイテム行にフォーカスする
	 * @param item
	 */
	public void selectAndReveal(ExcelEditorItem item) {

		// 行番号の取得
		int line = getLine(item);
		if (line < 0) {
			return;
		}

		// 行の先頭位置の取得
		int offset = 0;
		int length = 0;
		try {
			IDocument document = getDocument();

			// 改行文字列取得
			String newLine = document.getLineDelimiter(line);

			// 開始位置取得
			offset = document.getLineOffset(line);

			// 長さ取得
			if (newLine == null) {
				length = document.getLineLength(line);
			} else {
				length = document.getLineLength(line) - newLine.length();
			}
		} catch (BadLocationException e) {
			return;
		}

		// 指定行へ移動
		selectAndReveal(offset, length);
	}

	/**
	 * 指定アイテムの行番号を取得
	 * @param item 行番号
	 * @return
	 */
	public int getLine(ExcelEditorItem item) {

		if (item.isEmpty()) {
			return -1;
		}

		List<ExcelEditorItem> items = getExcelItems();

		// 1オリジンから0オリジンに変換
		int line = -1;

		line += searchItem(items, item, new SearchItemResult());

		return line;
	}

	/**
	 * 行数取得用内部クラス
	 */
	private class SearchItemResult {
		public boolean isSucceed;
	}

	/**
	 * 行数取得用再帰メソッド
	 * @param list
	 * @param item
	 * @return
	 */
	private int searchItem(
			List<ExcelEditorItem> list,
			ExcelEditorItem item,
			SearchItemResult result) {

		int line = 0;

		for (ExcelEditorItem eItem : list) {

			// 行数＋1
			if (eItem.isEmpty() == false) {
				line++;
			}

			// 対象アイテムが見つかった場合
			if (eItem.equals(item)) {
				// その時点で探索終了
				result.isSucceed = true;
				break;
			}

			if (eItem.getChildren().isEmpty() == false) {
				// 再帰呼び出し
				line += searchItem(eItem.getChildren(), item, result);
				// 見つかった場合処理終了
				if (result.isSucceed) {
					break;
				}
			}
		}

		return line;
	}


	// XXX:現状では編集不可とするため以下のメソッドをオーバーライド
	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public boolean isEditorInputReadOnly() {
		return false;
	}

	@Override
	public boolean isEditorInputModifiable() {
		return false;
	}

	@Override
	protected boolean isBlockSelectionModeSupported() {
		return false;
	}

	/**
	 * SourceViewerにドラッグ・アンド・ドロップを設定する
	 * @param viewer
	 */
	protected void installTextDragAndDrop(final ISourceViewer viewer) {
		if (viewer == null || fIsTextDragAndDropInstalled)
			return;

		final StyledText st= viewer.getTextWidget();

		// Install drag source
		final ISelectionProvider selectionProvider= viewer.getSelectionProvider();
		final DragSource source= new DragSource(st, DND.DROP_COPY);
		source.setTransfer(new Transfer[] {TextTransfer.getInstance()});
		source.addDragListener(new DragSourceAdapter() {
			String fSelectedText;
			Point fSelection;
			public void dragStart(DragSourceEvent event) {
				try {
					fSelection= st.getSelection();
					event.doit= isLocationSelected(new Point(event.x, event.y));

					ISelection selection= selectionProvider.getSelection();
					if (selection instanceof ITextSelection)
						fSelectedText = ((ITextSelection)selection).getText();
					else // fallback to widget
						fSelectedText = st.getSelectionText();

					// 改行が含まれている場合、ドラッグ不可
					if (fSelectedText == null || fSelectedText.contains("\r") || //$NON-NLS-1$
							fSelectedText.contains("\n")) { //$NON-NLS-1$
						event.doit = false;
					}
				} catch (IllegalArgumentException ex) {
					event.doit= false;
				}
			}

			private boolean isLocationSelected(Point point) {
				if (isBlockSelectionModeEnabled())
					return false;

				int offset= st.getOffsetAtLocation(point);
				Point p= st.getLocationAtOffset(offset);
				if (p.x > point.x)
					offset--;
				return offset >= fSelection.x && offset < fSelection.y;
			}

			public void dragSetData(DragSourceEvent event) {
				event.data= fSelectedText;
			}
		});

		fIsTextDragAndDropInstalled= true;
	}

	/**
	 * SourceViewerからドラッグ・アンド・ドロップを取り除く
	 * @param viewer
	 */
	protected void uninstallTextDragAndDrop(ISourceViewer viewer) {
		if (viewer == null || !fIsTextDragAndDropInstalled)
			return;

		StyledText st= viewer.getTextWidget();

		DragSource dragSource= (DragSource)st.getData(DND.DRAG_SOURCE_KEY);
		if (dragSource != null) {
			dragSource.dispose();
			st.setData(DND.DRAG_SOURCE_KEY, null);
		}

		fIsTextDragAndDropInstalled= false;
	}
}
