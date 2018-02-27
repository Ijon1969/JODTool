package jp.ac.kyushu_u.csce.modeltool.spec.speceditor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.FileDocumentProvider;

/**
 * 仕様書エディター（Excel）用ドキュメントプロバイダ
 *
 * @author KBK yoshimura
 */
public class ExcelDocumentProvider extends FileDocumentProvider {

	/** ページインデックス */
	private int index;
	/** Excelアイテム */
	public List<ExcelEditorItem> items;

	/** 最大読込列数 */
	private static final int EXCEL_MAX_COL = 3;

	/**
	 * コンストラクタ
	 */
	private ExcelDocumentProvider() {
		super();
	}

	/**
	 * コンストラクタ
	 * @param pageIndex ページインデックス
	 */
	public ExcelDocumentProvider(int pageIndex) {
		this();
		index = pageIndex;
		items = new ArrayList<ExcelEditorItem>();
	}

	/**
	 * @see org.eclipse.ui.editors.text.StorageDocumentProvider#setDocumentContent(IDocument, IEditorInput, String)
	 */
	protected boolean setDocumentContent(IDocument document,
			IEditorInput editorInput, String encoding) throws CoreException {

		if (editorInput instanceof IFileEditorInput) {
			IFile file= ((IFileEditorInput)editorInput).getFile();
			InputStream contentStream = null ;
			InputStream in = null;

			try {
				StringBuffer sb = new StringBuffer();
				in = file.getContents();

				// Excelワークブックを開く
				Workbook workbook = null;
				try {
					workbook = WorkbookFactory.create(in);
				} catch (InvalidFormatException e) {
					String message= (e.getMessage() != null ? e.getMessage() : ""); //$NON-NLS-1$
					IStatus s= new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, IStatus.OK, message, e);
					throw new CoreException(s);
				}

				// 各シートを読込む
				for (int i=0; i<workbook.getNumberOfSheets(); i++) {

					// 指定インデックス以外のシートは処理しない
					if (i != index) {
						continue;
					}

					// シート取得
					Sheet sheet = workbook.getSheetAt(i);

					// 上の行から走査
					for (Iterator<Row> rowItr = sheet.rowIterator(); rowItr.hasNext(); ) {

						Row row = (Row)rowItr.next();

						// 左の列から走査
                        for (Iterator<Cell> cellItr = row.cellIterator(); cellItr.hasNext(); ) {

                            Cell cell = (Cell)cellItr.next();

                            String cellString = ""; //$NON-NLS-1$

                            // cellの型で場合分け
                            switch (cell.getCellType()) {

	                        	case Cell.CELL_TYPE_BLANK:
	                        		break;

	                        	case Cell.CELL_TYPE_BOOLEAN:
	                        		cellString = String.valueOf(cell.getBooleanCellValue());
	                        		break;

	                        	case Cell.CELL_TYPE_ERROR:
	                        		cellString = String.valueOf(cell.getErrorCellValue());
	                        		break;

	                        	case Cell.CELL_TYPE_FORMULA:
	                        		cellString = cell.getCellFormula();
	                        		break;

	                        	case Cell.CELL_TYPE_NUMERIC:
	                        		if(DateUtil.isCellDateFormatted(cell)) {
	                        			cellString = String.valueOf(cell.getDateCellValue());
	                        		} else {
	                        			cellString = String.valueOf(new DecimalFormat("#").format(cell.getNumericCellValue())); //$NON-NLS-1$
	                        		}
	                        		break;

	                        	case Cell.CELL_TYPE_STRING:
	                                cellString = cell.getStringCellValue();
	                        		break;
                            }

                            // A,B,C の3列のみ読込む
    						if (cell.getColumnIndex() < EXCEL_MAX_COL && !cellString.replaceAll("[ |　|\\r|\\n]", "").isEmpty()) { //$NON-NLS-1$ //$NON-NLS-2$

    							// 改行コード除去
                                cellString = cellString.replaceAll("[\\n|\\r]+", " "); //$NON-NLS-1$ //$NON-NLS-2$

    							// アイテムのインスタンス作成
    							ExcelEditorItem item = new ExcelEditorItem();
    							item.setLevel(cell.getColumnIndex());
    							item.setContent(cellString);

    							switch (cell.getColumnIndex()) {
    							case 0:		// A列
    								items.add(item);
    								break;

    							case 1:		// B列
    								ExcelEditorItem parent = null;
    								if (items.size() == 0) {
    									// A列アイテムがない場合、空アイテムを作成
    									parent = new ExcelEditorItem();
    									parent.setLevel(0);
    									parent.setEmpty(true);
    									items.add(parent);
    								} else {
    									parent = items.get(items.size() - 1);
    								}
    								parent.getChildren().add(item);
    								item.setParent(parent);
    								break;

    							case 2:		// C列
    								ExcelEditorItem gp = null;
    								if (items.size() == 0) {
    									// A列アイテムがない場合、空アイテム作成
    									gp = new ExcelEditorItem();
    									gp.setLevel(0);
    									gp.setEmpty(true);
    									items.add(gp);
    								} else {
    									gp = items.get(items.size() - 1);
    								}

    								List<ExcelEditorItem> list = gp.getChildren();
    								parent = null;
    								if (list.size() == 0) {
    									// B列アイテムがない場合、空アイテム作成
    									parent = new ExcelEditorItem();
    									parent.setLevel(1);
    									parent.setEmpty(true);
    									list.add(parent);
    								} else {
    									parent = list.get(list.size() - 1);
    								}
    								parent.getChildren().add(item);
    								item.setParent(parent);

    								break;
    							}

                                sb.append(cellString);
                                sb.append("\r\n"); //$NON-NLS-1$
    						}
                        }
                    }

				}

				contentStream = new ByteArrayInputStream(sb.toString().getBytes());

				setDocumentContent(document, contentStream, encoding);

			} catch (IOException ex) {
				String message= (ex.getMessage() != null ? ex.getMessage() : ""); //$NON-NLS-1$
				IStatus s= new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, IStatus.OK, message, ex);
				throw new CoreException(s);
			} finally {
				try {
					if (contentStream != null)
						contentStream.close();
					if (in != null)
						in.close();
				} catch (IOException e1) {
				}
			}
			return true;
		}
		return super.setDocumentContent(document, editorInput, encoding);
	}


}