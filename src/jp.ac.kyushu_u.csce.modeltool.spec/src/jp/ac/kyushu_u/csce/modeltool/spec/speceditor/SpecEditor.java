package jp.ac.kyushu_u.csce.modeltool.spec.speceditor;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import jp.ac.kyushu_u.csce.modeltool.base.utility.InfoForm;
import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;
import jp.ac.kyushu_u.csce.modeltool.spec.Messages;
import jp.ac.kyushu_u.csce.modeltool.spec.constant.SpecConstants;
import jp.ac.kyushu_u.csce.modeltool.spec.speceditor.handler.SpecInspector.InspectMarkData;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

// TODO:Excelエディタとテキストエディタを別々にしたほうが操作しやすいか？
// TODO:テキストエディタもマルチページ上に表示させるため制限が出ている
/**
 * 仕様書エディター<br>
 * txt形式、Excel形式の仕様書ファイルを扱うエディター。<br>
 * 内部エディターとして<code>{@link InternalTextEditor}</code>、<code>{@link InternalExcelEditor}</code>を使用する。
 *
 * @author KBK yoshimura
 */
public class SpecEditor extends MultiPageEditorPart {

	/** アウトライン */
	private ExcelEditorOutlinePage outlinePage;

	/** ルートコントロール */
	private Composite fParent;
	/** 通常／エラーページ切替用のレイアウト */
	private StackLayout fStackLayout;
	/** 通常ページのルートコントロール */
	private Composite fDefaultComposite;
	/** エラーページ */
	private Control fStatusControl;

	/**
	 * 直前の検査内容を保持するリスト
	 * TODO: 本当はマネージャークラスを作って管理するのがよいと思うが暫定的にエディターに持たせる
	 */
	private List<InspectMarkData> inspectList = new ArrayList<InspectMarkData>();

	@Override
	protected Composite createPageContainer(Composite parent) {

		fParent= new Composite(parent, SWT.NONE);
		fStackLayout= new StackLayout();
		fParent.setLayout(fStackLayout);

		fDefaultComposite= new Composite(fParent, SWT.NONE);
		fDefaultComposite.setLayout(new FillLayout());

		fStackLayout.topControl = fDefaultComposite;
		fParent.layout();

		return super.createPageContainer(fDefaultComposite);
	}

	/**
	 * ページの作成
	 * @see org.eclipse.ui.part.MultiPageEditorPart#createPages()
	 */
	protected void createPages() {

		IEditorInput editorInput = getEditorInput();

		// タイトルの設定
		setPartName(editorInput.getName());

		if (editorInput instanceof IFileEditorInput) {
			IFile file= ((IFileEditorInput) editorInput).getFile();

			// ファイル拡張子の取得
			String extension = file.getFileExtension();

//			InputStream contentStream = null ;
			InputStream in = null;

			try {

				// txt、htmlの場合
				if (PluginHelper.in(extension, false, SpecConstants.SPEC_EXTENSIONS)) {

					// 内部テキストエディター
					addPage(new InternalTextEditor(), editorInput);

					// タブ非表示
					CTabFolder folder = (CTabFolder)getContainer();
					folder.setTabHeight(0);

				// xls、xlsxの場合
				} else if (PluginHelper.in(extension, false, SpecConstants.EXCEL_EXTENSIONS)) {

					boolean isSync = file.isSynchronized(IResource.DEPTH_ZERO);

					if (isSync) {
						// ファイルをPOIで開く
						in = file.getContents();

//						POIFSFileSystem fs = new POIFSFileSystem(in);
//						Workbook workbook = WorkbookFactory.create(fs);
						Workbook workbook = null;
						try {
							workbook = WorkbookFactory.create(in);
						} catch (InvalidFormatException e) {
							e.printStackTrace();
						}

						// シートの数だけページを作成
						for (int i=0; i<workbook.getNumberOfSheets(); i++) {

							// 内部Excelエディター
							IEditorPart editor = new InternalExcelEditor(i);
							int index = addPage(editor, getEditorInput());

							// ページ名にシート名を表示
							setPageText(index, workbook.getSheetAt(i).getSheetName());
						}

					} else {
						// 内部Excelエディター
//						IEditorPart editor = new InternalExcelEditor(0);
//						addPage(editor, getEditorInput());
						addPage(new Composite(getContainer(), SWT.NONE));

						if (fStatusControl != null) {
							fStatusControl.dispose();
							fStatusControl= null;
						}

						fStatusControl= createStatusControl(fParent, file.getFullPath().toString());
						Control front = fStatusControl;

						if (fStackLayout.topControl != front) {
							fStackLayout.topControl= front;
							fParent.layout();
						}
					}
				}

			} catch (IOException e) {
			} catch (PartInitException e) {
			} catch (CoreException e) {

//				// メッセージダイアログ出力
//				MessageDialog.openInformation(getSite().getShell(), "ファイルのオープン",
//						"ファイルのオープンに失敗しました。");
//				// エラーログ出力
//				IStatus status = new Status(
//						IStatus.ERROR, Activator.PLUGIN_ID,
//						"failed to open file ["
//							+ file.getLocation().toOSString()
//							+ "]", e);
//				Activator.getDefault().getLog().log(status);

			} finally {
				try {
//					if (contentStream != null)
//						contentStream.close();
					if (in != null)
						in.close();
				} catch (IOException e1) {
				}
			}
		}

//		CTabFolder folder = (CTabFolder)getContainer();
//		folder.setTabHeight(0);
	}

	/**
	 * @param parent the parent control
	 * @param status the status
	 * @return the new status control
	 */
	protected Control createStatusControl(Composite parent, String fileName) {

		String info = MessageFormat.format(
						Messages.SpecEditor_0,
						fileName);

		InfoForm infoForm = new InfoForm(parent);
		infoForm.setHeaderText(""); //$NON-NLS-1$
		infoForm.setBannerText(""); //$NON-NLS-1$
		infoForm.setInfo(info);
		return infoForm.getControl();
	}

	/**
	 *
	 * @param start
	 * @param length
	 */
	public void selectAndReveal(int start, int length) {
		IEditorPart editor = getActiveEditor();

		// Excelエディターの場合
		if (editor instanceof InternalExcelEditor) {
			InternalExcelEditor eEditor = ((InternalExcelEditor)editor);
			eEditor.selectAndReveal(start, length);
		}
	}

	/**
	 *
	 * @param item
	 */
	public void selectAndReveal(ExcelEditorItem item) {
		IEditorPart editor = getActiveEditor();

		// Excelエディターの場合
		if (editor instanceof InternalExcelEditor) {
			InternalExcelEditor eEditor = ((InternalExcelEditor)editor);
			eEditor.selectAndReveal(item);

		}
	}

	/**
	 * 指定ページの書式をリセット
	 * @param pageIndex ページインデックス
	 */
	public void resetPageStyle(int pageIndex) {

		// ページインデックス不正
		if (pageIndex < 0 || pageIndex >= getPageCount()) {
			return;
		}

		AbstractInternalTextEditor editor = (AbstractInternalTextEditor)getEditor(pageIndex);
		editor.resetTextStyle();
	}

	/**
	 * StyledTextの取得
	 * @return StyledText
	 */
	public StyledText getStyledText() {
		Object page = getSelectedPage();
		if (page == null) {
			return null;
		}

		return ((AbstractInternalTextEditor)page).getStyledText();
	}

	/**
	 * StyledTextの取得
	 * @param pageIndex ページインデックス
	 * @return StyledText
	 */
	public StyledText getStyledText(int pageIndex) {

		// ページインデックス不正
		if (pageIndex < 0 || pageIndex >= getPageCount()) {
			return null;
		}

		return ((AbstractInternalTextEditor)getEditor(pageIndex)).getStyledText();
	}

	/**
	 * Documentの取得
	 * @return IDocument
	 */
	public IDocument getDocument() {
		Object page = getSelectedPage();
		if (page == null) {
			return null;
		}

		return ((AbstractInternalTextEditor)page).getDocument();
	}

	/**
	 * Documentの取得
	 * @param pageIndex ページインデックス
	 * @return IDocument
	 */
	public IDocument getDocument(int pageIndex) {

		// ページインデックス不正
		if (pageIndex < 0 || pageIndex >= getPageCount()) {
			return null;
		}

		return ((AbstractInternalTextEditor)getEditor(pageIndex)).getDocument();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.MultiPageEditorPart#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		if (getSelectedPage() instanceof InternalExcelEditor) {
			if (IContentOutlinePage.class.equals(adapter)) {
				if (outlinePage == null) {
					outlinePage = new ExcelEditorOutlinePage(this);
				}
				return outlinePage;
			}
		}
		return super.getAdapter(adapter);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.MultiPageEditorPart#pageChange(int)
	 */
	@Override
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);

		if (outlinePage != null) {
			((ExcelEditorOutlinePage)outlinePage).setContent();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		IEditorPart editor = getActiveEditor();
		if (editor != null) {
			editor.doSave(monitor);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		IEditorPart editor = getActiveEditor();
		if (editor != null) {
			editor.doSaveAs();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		IEditorPart editor = getActiveEditor();
		if (editor != null) {
			return editor.isSaveAsAllowed();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.MultiPageEditorPart#dispose()
	 */
	@Override
	public void dispose() {

		if (outlinePage != null) {
			outlinePage.dispose();
		}

		//		colorManager.dispose();
		super.dispose();
	}

	public List<InspectMarkData> getInspectList() {
		return inspectList;
	}

	public void setInspectList(List<InspectMarkData> inspectList) {
		this.inspectList = inspectList;
	}
}
