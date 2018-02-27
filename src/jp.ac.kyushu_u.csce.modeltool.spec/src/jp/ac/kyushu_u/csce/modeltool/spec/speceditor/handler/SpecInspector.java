package jp.ac.kyushu_u.csce.modeltool.spec.speceditor.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import jp.ac.kyushu_u.csce.modeltool.base.ModelToolBasePlugin;
import jp.ac.kyushu_u.csce.modeltool.base.constant.BasePreferenceConstants;
import jp.ac.kyushu_u.csce.modeltool.base.utility.FileAccessException;
import jp.ac.kyushu_u.csce.modeltool.base.utility.FileAccessor;
import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;
import jp.ac.kyushu_u.csce.modeltool.base.utility.StringEscape;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Dictionary;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionarySetting;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry;
import jp.ac.kyushu_u.csce.modeltool.spec.ModelToolSpecPlugin;
import jp.ac.kyushu_u.csce.modeltool.spec.constant.SpecConstants;
import jp.ac.kyushu_u.csce.modeltool.spec.constant.SpecPreferenceConstants;
import jp.ac.kyushu_u.csce.modeltool.spec.speceditor.AbstractInternalTextEditor;
import jp.ac.kyushu_u.csce.modeltool.spec.speceditor.InternalExcelEditor;
import jp.ac.kyushu_u.csce.modeltool.spec.speceditor.InternalTextEditor;
import jp.ac.kyushu_u.csce.modeltool.spec.speceditor.SpecEditor;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IEditorPart;

/**
 * 要求仕様書の検査を行うクラス
 * @author KBK yoshimura
 */
public class SpecInspector {

	// HTMLタグ
	private static final String TAG_COMMENT_START	= "<!-- "; //$NON-NLS-1$
	private static final String TAG_COMMENT_END	= " -->\r\n"; //$NON-NLS-1$
	private static final String TAG_HTML_START	= "<HTML>\r\n"; //$NON-NLS-1$
	private static final String TAG_HTML_END		= "</HTML>\r\n"; //$NON-NLS-1$
	private static final String TAG_HEAD_START	= "<HEAD>"; //$NON-NLS-1$
	private static final String TAG_HEAD_END		= "</HEAD>\r\n"; //$NON-NLS-1$
	private static final String TAG_TITLE_START	= "<TITLE>"; //$NON-NLS-1$
	private static final String TAG_TITLE_END		= "</TITLE>"; //$NON-NLS-1$
	private static final String TAG_BODY_START	= "<BODY>\r\n"; //$NON-NLS-1$
	private static final String TAG_BODY_END		= "</BODY>\r\n"; //$NON-NLS-1$
	private static final String TAG_FONT_START1	= "<FONT COLOR="; //$NON-NLS-1$
	private static final String TAG_FONT_START2	= ">"; //$NON-NLS-1$
	private static final String TAG_FONT_END		= "</FONT>"; //$NON-NLS-1$
	private static final String TAG_BR			= "<BR>\r\n"; //$NON-NLS-1$

	private static final String REGEX_COLORCODE	= "#[0-9a-fA-F]{6}"; //$NON-NLS-1$
	private static final String REGEX_TAG_FONT	=
		TAG_FONT_START1 + REGEX_COLORCODE + TAG_FONT_START2 + "|" + TAG_FONT_END; //$NON-NLS-1$

	// 改行コード(CR+LF)
	private static final String LINE_SEPARATOR = "\r\n"; //$NON-NLS-1$
	private static final String REGEX_LINE_SEPARATOR = "\r\n|\r|\n"; //$NON-NLS-1$

	/** バッファマネージャ */
	private ITextFileBufferManager manager;

	/** 改行位置データ */
	private List<newLineData> newLineList;

	/** ファイルタイプ */
	private enum fileType {
		txt,
		html,
		xls,
	}

	/** 正規表現検索フラグ */
	private boolean regExSearch = true;	// 正規表現の場合true

	/** シート番号 */
	private Integer sheetNo = null;

	/**
	 * コンストラクタ
	 */
	public SpecInspector() {

		// バッファマネージャの取得
		manager = FileBuffers.getTextFileBufferManager();

		// プリファレンスストアから正規表現フラグを取得
		IPreferenceStore store = ModelToolSpecPlugin.getDefault().getPreferenceStore();
		regExSearch = store.getBoolean(SpecPreferenceConstants.PK_USE_REGULAR_EXPRESSION);
	}

	/**
	 * ファイル、辞書指定の検査
	 * 今回こちらのメソッドは使用しないので、inspectEditorを使用してください
	 * ※EXCEL未対応
	 * @param file
	 * @param dictionary
	 * @throws InspectException 検査処理例外
	 */
	public IFile inspectFile(IFile file, List<Dictionary> dictionaries, IContainer container) throws InspectException {

		// ドキュメントの取得
		IDocument document = getDocument(file);

		// マークデータリスト
//		List<MarkData> marks = getMarkDataList(document, dictionaries);
		List<InspectMarkData> inspectMarks = getInspectMarkDataList(document, dictionaries);
		List<MarkData> marks = getMarkDataList(document, inspectMarks);

		// マーク付き仕様書ファイルの出力
		IFile markedFile = outputMarkedSpecFile(document, marks,
				PluginHelper.getFileNameWithoutExtension(file), container);

		// ドキュメントの破棄
		disposeDocument(file);

		return markedFile;
	}

//	/**
//	 * エディター、複数辞書指定の検査
//	 * @param editor
//	 * @param dictionaries
//	 * @param container
//	 * @return
//	 * @throws InspectException
//	 */
//	public IFile inspectEditor(SpecEditor editor, List<Dictionary> dictionaries, IContainer container)
//	throws InspectException {
//
//		Dictionary dictionary = new Dictionary();
//
//		// 複数辞書のマージ
//		for (Iterator<Dictionary> itr = dictionaries.iterator(); itr.hasNext(); ) {
//			dictionary.getEntries().addAll(itr.next().getEntries());
//		}
//
//		return inspectEditor(editor, dictionary, container);
//	}

	/**
	 * エディター、辞書指定の検査
	 * @param editor
	 * @param dictionary
	 * @throws InspectException 検査処理例外
	 */
	public IFile inspectEditor(IEditorPart editor, List<Dictionary> dictionaries, IContainer container)
			throws InspectException {

		// 仕様書エディター、仕様書Excelエディター以外の場合処理なし
		if (editor instanceof SpecEditor == false) {
			return null;
		}

		// ファイルの取得
		IFile file = (IFile)editor.getEditorInput().getAdapter(IFile.class);
		// 拡張子からファイルタイプを取得
		String extension = file.getFileExtension();
		fileType type = null;
		if (extension.equalsIgnoreCase(SpecConstants.EXTENSION_TXT)) {
			type = fileType.txt;
		} else if (extension.equalsIgnoreCase(SpecConstants.EXTENSION_HTML)) {
			type = fileType.html;
		} else if (PluginHelper.in(extension, false, SpecConstants.EXCEL_EXTENSIONS)) {
			type = fileType.xls;
		} else {
			return null;
		}

		// ドキュメントの取得
		IDocument document = getDocument(editor);
		if (document == null) {
			return null;
		}

		IFile markedFile = null;

		// 拡張子“html”の場合
		if (type == fileType.html) {

			// ドキュメントからHTMLタグを除去
			document = getBodyDocument(document);

			// HTMLアンエスケープ
			document.set(StringEscape.unescapeHtml(document.get()));
		}

		// 拡張子“txt”、“html”の場合
		IDocument copy = new Document(document.get());
//		if (type == fileType.txt || type == fileType.html) {
		if (type == fileType.html) {	// txtは改行コードそのまま（正規表現不具合対応）

			// ドキュメントの改行位置解析
			newLineList = analyseDocument(document);

			// 改行コードの除去
			copy = new Document();
			copy.set(document.get().replaceAll(REGEX_LINE_SEPARATOR, "")); //$NON-NLS-1$
		}

		// マークデータリスト
//		List<MarkData> marks = getMarkDataList(copy, dictionaries);
		List<InspectMarkData> inspectMarks = getInspectMarkDataList(copy, dictionaries);
		((SpecEditor)editor).setInspectList(inspectMarks);
		List<MarkData> marks = getMarkDataList(copy, inspectMarks);

		// マーク付き仕様書ファイルの出力
		String fileName = PluginHelper.getFileNameWithoutExtension(file);
		if (sheetNo != null) {
			fileName += "(" + sheetNo + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		markedFile = outputMarkedSpecFile(document, marks,
				fileName, container);

		// 拡張子“txt”、“xls”の場合
		if (type == fileType.txt || type == fileType.xls) {

			// 画面表示のリセット
			resetMarking(editor, document);

			// 画面表示のマーク付け
			displayMarking(document, marks, ((SpecEditor)editor).getStyledText());
		}

		// ドキュメントの破棄
		disposeDocument(editor);

		return markedFile;
	}

	/**
	 * エディター、辞書指定の検査
	 * @param editor
	 * @param dictionary
	 * @throws InspectException 検査処理例外
	 */
	public void inspectEditorAgain(IEditorPart editor)
			throws InspectException {

		// 仕様書エディター、仕様書Excelエディター以外の場合処理なし
		if (editor instanceof SpecEditor == false) {
			return;
		}

		// ファイルの取得
		IFile file = (IFile)editor.getEditorInput().getAdapter(IFile.class);
		// 拡張子からファイルタイプを取得
		String extension = file.getFileExtension();
		fileType type = null;
		if (extension.equalsIgnoreCase(SpecConstants.EXTENSION_TXT)) {
			type = fileType.txt;
		} else if (extension.equalsIgnoreCase(SpecConstants.EXTENSION_HTML)) {
			type = fileType.html;
		} else if (PluginHelper.in(extension, false, SpecConstants.EXCEL_EXTENSIONS)) {
			type = fileType.xls;
		} else {
			return;
		}

		// ドキュメントの取得
		IDocument document = getDocument(editor);
		if (document == null) {
			return;
		}

		// 拡張子“html”の場合
		if (type == fileType.html) {

			// ドキュメントからHTMLタグを除去
			document = getBodyDocument(document);

			// HTMLアンエスケープ
			document.set(StringEscape.unescapeHtml(document.get()));
		}

		// 拡張子“txt”、“html”の場合
		IDocument copy = new Document(document.get());
//		if (type == fileType.txt || type == fileType.html) {
		if (type == fileType.html) {	// txtは改行コードそのまま（正規表現不具合対応）

			// ドキュメントの改行位置解析
			newLineList = analyseDocument(document);

			// 改行コードの除去
			copy = new Document();
			copy.set(document.get().replaceAll(REGEX_LINE_SEPARATOR, "")); //$NON-NLS-1$
		}

		// マークデータリスト
		List<MarkData> marks = getMarkDataList(copy, ((SpecEditor)editor).getInspectList());

		// 拡張子“txt”、“xls”の場合
		if (type == fileType.txt || type == fileType.xls) {

			// 画面表示のリセット
			resetMarking(editor, document);

			// 画面表示のマーク付け
			displayMarking(document, marks, ((SpecEditor)editor).getStyledText());
		}

		// ドキュメントの破棄
		disposeDocument(editor);
	}

	/**
	 * ドキュメントの取得<br>
	 * このメソッドで取得したドキュメントは、{@link #disposeDocument(Object)}で
	 * 破棄すること。
	 * @param object
	 * @return
	 * @throws InspectException 検査処理例外
	 */
	private IDocument getDocument(Object object) throws InspectException {

		// ファイルの場合
		if (object instanceof IFile) {

			IFile file = (IFile)object;

			IPath path = file.getFullPath();

			try {
				manager.connect(path, LocationKind.IFILE, null);
			} catch (CoreException e) {
				throw new InspectException(e);
			}

			ITextFileBuffer buffer = manager.getTextFileBuffer(path, LocationKind.IFILE);

			return buffer.getDocument();
		}

		// 仕様書内部エディターの場合
		if (object instanceof AbstractInternalTextEditor) {
			AbstractInternalTextEditor editor = (AbstractInternalTextEditor)object;
			return editor.getDocumentProvider().getDocument(editor.getEditorInput());
		}

		// 仕様書エディターの場合
		if (object instanceof SpecEditor) {
			// アクティブなシートのドキュメントを返す
			SpecEditor editor = (SpecEditor)object;

			if (editor.getSelectedPage() instanceof InternalExcelEditor) {
				// アクティブなシート番号を取得（1オリジンに修正）
				sheetNo = editor.getActivePage() + 1;
			}

			return editor.getDocument();
		}

		return null;
	}

	/**
	 * ドキュメントの破棄<br>
	 * {@link #getDocument(Object)}でドキュメントを取得した場合、
	 * 必ずこのメソッドを使用して破棄すること。<br>
	 * パラメータは{@link #getDocument(Object)}と同じものを使用する。
	 * @throws InspectException 検査処理例外
	 */
	private void disposeDocument(Object object) throws InspectException {

		if (object instanceof IFile) {

			IFile file = (IFile)object;

			IPath path = file.getFullPath();

			try {
				manager.disconnect(path, LocationKind.IFILE, null);
			} catch (CoreException e) {
				throw new InspectException(e);
			}
		}

		if (object instanceof InternalTextEditor) {
			// 処理なし
		}
	}

	/**
	 * コピードキュメント（改行除去）のオフセットから、
	 * 元ドキュメントのオフセットを求める
	 * @param copyOffset コピードキュメントのオフセット
	 * @return 元ドキュメントのオフセット
	 */
	private int getOriginalOffset(int copyOffset) {

		int offset = copyOffset;

		if (newLineList == null || newLineList.isEmpty()) {
			return offset;
		}

		int gap = 0;
		for (int i=newLineList.size()-1; i>=0; i--) {
			newLineData data = newLineList.get(i);
			gap = data.offset1 - data.offset2;
			if (copyOffset >= data.offset2) {
				offset = copyOffset + gap;
				break;
			}
		}

		return offset;
	}

	/**
	 * ドキュメントの改行位置解析
	 * @param document
	 * @return
	 */
	private List<newLineData> analyseDocument(IDocument document) {

		int numOfLines = document.getNumberOfLines();
		List<newLineData> newLineList = new ArrayList<newLineData>(numOfLines);
		try {
			int offset = 0;
			for (int i=0; i<numOfLines; i++) {
				String delimiter = document.getLineDelimiter(i);
				if (delimiter != null) {
					offset += (document.getLineLength(i) - delimiter.length());
					newLineList.add(new newLineData(
							document.getLineOffset(i) + document.getLineLength(i),
							offset));
				}
			}

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return newLineList;
	}

	/**
	 * 改行データクラス
	 */
	private class newLineData {
		/** 元ドキュメントの改行位置 */
		int offset1;
		/** 改行除去後の改行位置 */
		int offset2;

		public newLineData(int offset1, int offset2) {
			super();
			this.offset1 = offset1;
			this.offset2 = offset2;
		}
	}

	/**
	 * マーク付きファイルの出力
	 * @param document
	 * @param marks
	 * @param name
	 * @param container
	 * @throws InspectException 検査処理例外
	 */
	private IFile outputMarkedSpecFile(IDocument document, List<MarkData> marks,
			String name, IContainer container) throws InspectException {

		// 仕様書にHTMLタグを埋め込む
		StringBuffer sb = new StringBuffer();
		sb.append(TAG_COMMENT_START);
		sb.append("OUTPUT DATE : " + new GregorianCalendar().getTime().toString()); //$NON-NLS-1$
		sb.append(TAG_COMMENT_END);
		sb.append(TAG_HTML_START);

		// ヘッダ部
		sb.append(TAG_HEAD_START);
		sb.append(TAG_TITLE_START);
		sb.append(name);
		sb.append(TAG_TITLE_END);
		sb.append(TAG_HEAD_END);

		// ボディ部
		sb.append(TAG_BODY_START);

		int offset = 0;
		int length = 0;
		try {
			for (MarkData mark : marks) {

//				if (offset + length <= mark.offset) {
//					// HTMLのフォント設定
////					sb.append(document.get(
////							offset + length, mark.offset - (offset + length)));
//					sb.append(StringEscapeUtils.escapeHtml(document.get(
//							offset + length, mark.offset - (offset + length))).replaceAll(REGEX_LINE_SEPARATOR, TAG_BR));
//					sb.append(TAG_FONT_START1);
//					sb.append(mark.getColorString());
//					sb.append(TAG_FONT_START2);
////					sb.append(document.get(mark.offset, mark.length));
//					sb.append(StringEscapeUtils.escapeHtml(document.get(mark.offset, mark.length)));
//					offset = mark.offset;
//					length = mark.length;
//					sb.append(TAG_FONT_END);
//				}
				int markOffset = getOriginalOffset(mark.offset);
				int markLength =
					getOriginalOffset(mark.offset + mark.length - 1) - markOffset + 1;
				if (offset + length <= markOffset) {
					// HTMLのフォント設定
//					sb.append(document.get(
//							offset + length, markOffset - (offset + length)));
					sb.append(StringEscape.escapeHtml(document.get(
							offset + length, markOffset - (offset + length))).replaceAll(REGEX_LINE_SEPARATOR, TAG_BR));
					sb.append(TAG_FONT_START1);
					sb.append(mark.getColorString());
					sb.append(TAG_FONT_START2);
//					sb.append(document.get(markOffset, markLength));
					sb.append(StringEscape.escapeHtml(document.get(markOffset, markLength)).replaceAll(REGEX_LINE_SEPARATOR, TAG_BR));
					offset = markOffset;
					length = markLength;
					sb.append(TAG_FONT_END);
				}
			}
//			sb.append(document.get(offset + length, document.getLength() - (offset + length)));
			sb.append(StringEscape.escapeHtml(document.get(
					offset + length, document.getLength() - (offset + length))).replaceAll(REGEX_LINE_SEPARATOR, TAG_BR));

			// TODO:escapeHtml()を使うと、マルチバイト文字までエスケープされる

		} catch (BadLocationException e) {
			throw new InspectException(e);
		}

		sb.append(TAG_BODY_END);
		sb.append(TAG_HTML_END);
		// タグ埋め込み終了

		// ファイル出力
		try {
			return FileAccessor.writeFile(
					container, name + ".html", sb.toString()); //$NON-NLS-1$
		} catch (FileAccessException e) {
			throw new InspectException(e);
		}
	}

	/**
	 * マーキングを初期状態に戻す
	 * @param editor
	 * @param document
	 */
	private void resetMarking(IEditorPart editor, IDocument document) {

		if (editor instanceof AbstractInternalTextEditor) {

			AbstractInternalTextEditor inEditor = (AbstractInternalTextEditor)editor;
			inEditor.resetTextStyle();

		} else if (editor instanceof SpecEditor) {

			SpecEditor sEditor = (SpecEditor)editor;

			// 初期状態に戻す
			sEditor.resetPageStyle(sEditor.getActivePage());
		}
	}

	/**
	 * 画面出力のマーク付け
	 * @param document
	 * @param marks
	 * @param text
	 * @throws InspectException 検査処理例外
	 */
	private void displayMarking(IDocument document, List<MarkData> marks, StyledText text) throws InspectException {

		StyleRange range = new StyleRange();
//		range.start = 0;
//		range.length = document.getLength();
//		text.setStyleRange(range);

		int offset = 0;
		int length = 0;
		for (MarkData mark : marks) {

//			if (offset + length <= mark.offset) {
//				range = new StyleRange();
//				range.start = mark.offset;
//				range.length = mark.length;
//				range.foreground = mark.getColor();
////				range.underline = true;
//				range.fontStyle = SWT.BOLD;
//				text.setStyleRange(range);
//
//				offset = mark.offset;
//				length = mark.length;
//			}
			int markOffset = getOriginalOffset(mark.offset);
			int markLength =
				getOriginalOffset(mark.offset + mark.length - 1) - markOffset + 1;
			if (offset + length <= markOffset) {

				StyleRange[] ranges = text.getStyleRanges(markOffset, markLength);
				if (ranges.length > 0) {
					range = ranges[ranges.length - 1];
				} else {
					range = new StyleRange();
					range.start = markOffset;
					range.length = markLength;
				}
				range.foreground = mark.getColor();
				range.fontStyle = SWT.BOLD;
				text.setStyleRange(range);

				offset = markOffset;
				length = markLength;
			}
		}
	}

	/**
	 * マーキングクラス<br>
	 * 始点、長さ、色の設定を保持する
	 */
	private class MarkData {
		/** 始点 */
		int offset;
		/** 長さ */
		int length;
		/** 優先順位（辞書単位） */
		int priority;
		/** 色 */
		RGB color;

		/** コンストラクタ */
		public MarkData(int offset, int length, int priority, RGB color) {
			this.offset = offset;
			this.length = length;
			this.priority = priority;
			this.color = color;
		}

		/** カラーコードを取得する */
		public String getColorString() {
			return toHexRGBString(color);
		}

		/**
		 * Colorオブジェクトを取得する
		 */
		public Color getColor() {
			return ModelToolSpecPlugin.getColorManager().getColor(color);
		}
	}

	/**
	 *
	 */
	public class InspectMarkData {

		/** 単語 */
		List<String> words = new ArrayList<String>();
		/** 優先順位（辞書単位） */
		int priority;
		/** 色1 */
		RGB color1;
		/** 色2 */
		RGB color2;
		/** 正規表現検索の有無 */
		boolean isRegEx;
	}

	/**
	 * RGBオブジェクトを16進カラーコードに変換する<br>
	 * 例：RGB {252, 31, 108} ⇒ #fc1f6c
	 * @param rgb RGBオブジェクト
	 * @return 16進カラーコード
	 */
	private String toHexRGBString(RGB rgb) {

		String red = toHexString(rgb.red);
		String green = toHexString(rgb.green);
		String blue = toHexString(rgb.blue);

		return "#" + red + green + blue; //$NON-NLS-1$
	}

	/**
	 * 数値を2桁の16進数を表す文字列に変換する
	 * @param i 0～255(00～ff)の整数
	 * @return 16進数への変換結果
	 */
	private String toHexString(int i) {
		String ret = Integer.toHexString(i);
		if (ret.length() == 1) {
			ret = "0" + ret; //$NON-NLS-1$
		}
		return ret;
	}

	/**
	 * ドキュメントと辞書を指定して語句の検索を行い、マークデータのリストを取得する
	 * @param document
	 * @param dictionary
	 * @return
	 */
	private List<InspectMarkData> getInspectMarkDataList(IDocument document, List<Dictionary> dictionaries) throws InspectException {

		// プリファレンスストアからマーク色の設定を取得
		IPreferenceStore store = ModelToolBasePlugin.getDefault().getPreferenceStore();
		RGB noun_f  = StringConverter.asRGB(store.getString(BasePreferenceConstants.PK_COLOR_NOUN_FIRST));
		RGB noun    = StringConverter.asRGB(store.getString(BasePreferenceConstants.PK_COLOR_NOUN));
		RGB verb_f  = StringConverter.asRGB(store.getString(BasePreferenceConstants.PK_COLOR_VERB_FIRST));
		RGB verb    = StringConverter.asRGB(store.getString(BasePreferenceConstants.PK_COLOR_VERB));
		RGB state_f = StringConverter.asRGB(store.getString(BasePreferenceConstants.PK_COLOR_STATE_FIRST));
		RGB state   = StringConverter.asRGB(store.getString(BasePreferenceConstants.PK_COLOR_STATE));

		// マークデータリスト
		List<InspectMarkData> markDataList = new ArrayList<InspectMarkData>();

		int dicPriority = 0;
		for (Dictionary dictionary : dictionaries) {

			// 種別設定の取得
			DictionarySetting setting = dictionary.getSetting();

			// タブの見出し語リストを取得
			List<Entry> entryList = dictionary.getEntries();

			for (Entry entry : entryList) {
				String word = entry.getWord();

				// 副キーワード
				List<String> subwords = entry.getSubwords();

				// 活用形
				List<String> conjugations = entry.getConjugations();

				// 正規表現が正しいかチェック
				if (regExSearch) {
					try {
						Pattern.compile(word);
						for (String subword : subwords) {
							if (StringUtils.isNotBlank(subword)) {
								Pattern.compile(subword);
							}
						}
					} catch (PatternSyntaxException e) {
						continue;
					}
				}

				int category = entry.getCategoryNo();
				RGB color1 = null;
				RGB color2 = null;
				// デフォルト設定の場合
				if (setting.isDefaultCategory()) {
					switch (category) {
					case DictionaryConstants.CATEGORY_NO_NOUN:
						color1 = noun_f;
						color2 = noun;
						break;
					case DictionaryConstants.CATEGORY_NO_VERB:
						color1 = verb_f;
						color2 = verb;
						break;
					case DictionaryConstants.CATEGORY_NO_STATE:
						color1 = state_f;
						color2 = state;
						break;
					}

					// 種別設定ありの場合
				} else {
					if (category != DictionaryConstants.CATEGORY_NO_NONE) {
						DictionarySetting.Category categorySetting = setting.getCategory(category);
						color1 = categorySetting.getPrimary();
						color2 = categorySetting.getSecondary();
					}
				}

				if (color1 != null && color2 != null) {
					// マークデータの追加
					InspectMarkData data = new InspectMarkData();
					data.words.add(word);
					data.words.addAll(subwords);
					data.words.addAll(conjugations);
					data.priority = dicPriority;
					data.color1 = color1;
					data.color2 = color2;
					data.isRegEx = regExSearch;
					markDataList.add(data);
				}
			}
			dicPriority++;
		}

		return markDataList;
	}

	/**
	 * ドキュメントと辞書を指定して語句の検索を行い、マークデータのリストを取得する
	 * @param document
	 * @param dictionary
	 * @return
	 */
	private List<MarkData> getMarkDataList(IDocument document, List<InspectMarkData> inspectList) throws InspectException {

		// 検索アダプターの生成
		FindReplaceDocumentAdapter adapter = new FindReplaceDocumentAdapter(document);

		// マークデータリスト
		List<MarkData> markDataList = new ArrayList<MarkData>();

		for (InspectMarkData inspectData : inspectList) {

			List<IRegion> regionList = new ArrayList<IRegion>();
			for (String word : inspectData.words) {
				// 見出し語の出現箇所を検索
				regionList.addAll(getRegions(adapter, 0, word, inspectData.isRegEx));
			}

			int cnt = 0;
			for (IRegion region : regionList) {
				int offset = region.getOffset();
				int length = region.getLength();
				markDataList.add(new MarkData(offset, length, inspectData.priority, (cnt==0)? inspectData.color1 : inspectData.color2));
				cnt++;
			}
		}

		// マークリストの編集
		markDataList = editMarkDataList(markDataList, document);

		// オフセットの昇順にソート
		Collections.sort(markDataList, new Comparator<MarkData>() {
			public int compare(MarkData o1, MarkData o2) {
				return o1.offset - o2.offset;
			}
		});

		return markDataList;
	}

	/**
	 * HTMLファイルのBODY部をHTMLタグを除去し取り出す。
	 * @param document
	 * @return
	 * @throws InspectException 検査処理例外
	 */
	private IDocument getBodyDocument(IDocument document) throws InspectException {

		// 検索アダプターの生成
		FindReplaceDocumentAdapter adapter = new FindReplaceDocumentAdapter(document);

		// ドキュメントのBODY部の開始位置、長さを取得する
		IRegion start = find(adapter, 0, TAG_BODY_START, false);
		IRegion end = find(adapter, 0, TAG_BODY_END, false);
		if (start == null || end == null) {
			return null;
		}
		int bodyOffset = start.getOffset() + TAG_BODY_START.length();
		int bodyLength = end.getOffset() - bodyOffset;

		// BODY部を取り出す
		String body = null;
		try {
			body = document.get(bodyOffset, bodyLength);
		} catch (BadLocationException e) {
			throw new InspectException(e);
		}

		// FONTタグの除去
		body = body.replaceAll(REGEX_TAG_FONT, ""); //$NON-NLS-1$

		// BRタグの除去
		body = body.replaceAll(TAG_BR, LINE_SEPARATOR);

		return new Document(body);
	}

	/**
	 * 指定位置以降から文字列を検索し、位置のリストを返す。
	 * @param adapter
	 * @param offset
	 * @param findString
	 * @return
	 */
	private List<IRegion> getRegions(FindReplaceDocumentAdapter adapter, int offset,  String findString, boolean isRegEx) throws InspectException {

		// 領域リスト
		List<IRegion> list = new ArrayList<IRegion>();

		// 検索領域の取得
		IRegion region = find(adapter, offset, findString, isRegEx);

		while (region != null) {	// && region.getOffset() + region.getLength() <= adapter.length()) {

			list.add(region);

			if (region.getOffset() + region.getLength() >= adapter.length()) {
				break;
			}

			region = find(adapter, region.getOffset() + region.getLength(), findString, isRegEx);
		}

		return list;
	}

	/**
	 * 指定位置以降から文字列を検索する
	 * @param adapter
	 * @param offset
	 * @param findString
	 * @return
	 */
	private IRegion find(FindReplaceDocumentAdapter adapter, int offset, String findString, boolean isRegEx) throws InspectException {

		// 検索条件の設定
		boolean forwardSearch = true;	// 前方検索の場合true
		boolean caseSensitive = true;	// 大／小文字区別する場合true
		boolean wholeWord = false;		// 単語単位の場合true
//		boolean regExSearch = false;	// 正規表現の場合true ⇒ クラス変数へ移動

		// 指定位置以降から文字列を検索する
		IRegion region = null;
		try {
			region = adapter.find(offset, findString, forwardSearch, caseSensitive, wholeWord, isRegEx);
		} catch (BadLocationException e) {
			throw new InspectException(e);
		}

//		// 文字列が見つかった場合、オフセットを返し、見つからない場合負の値を返す。
//		if (region != null) {
//			return region.getOffset();
//		} else {
//			return -1;
//		}
		return region;
	}

	private List<MarkData> editMarkDataList(List<MarkData> list, IDocument document) {

		List<MarkData> copyList = new ArrayList<MarkData>(list);
		List<MarkData> markList = new ArrayList<MarkData>();

		// priority昇順＞length降順でソート
		Collections.sort(copyList, new Comparator<MarkData>() {
			public int compare(MarkData o1, MarkData o2) {
				if (o1.priority == o2.priority) {
					return (o1.length - o2.length) * -1;
				} else {
					return o1.priority - o2.priority;
				}
			}
		});

		boolean[] marked = new boolean[document.getLength()];
		Arrays.fill(marked, false);

		for (MarkData data : copyList) {
			markList.addAll(editMarkData(marked, data));
		}

		return markList;
	}

	/**
	 * 重複を考慮したマークデータの抽出・分割
	 * @param marked
	 * @param data
	 * @return
	 */
	private List<MarkData> editMarkData(boolean[] marked, MarkData data) {

		IPreferenceStore store = ModelToolSpecPlugin.getDefault().getPreferenceStore();
		int mode = store.getInt(SpecPreferenceConstants.PK_INSPECT_MODE);

		List<MarkData> markList = new ArrayList<MarkData>();
		List<Integer> checkList = checkMarked(marked, data.offset, data.length);

		if (mode == SpecPreferenceConstants.PV_INSPECT_MODE_1) {
			// 全文字未マークの場合
			if (checkList.size() == data.length) {
				setMarked(marked, checkList);
				markList.add(data);
			}
		}
		if (mode == SpecPreferenceConstants.PV_INSPECT_MODE_2) {
			int prev = Integer.MIN_VALUE;
			int offset = 0;
			int length = 0;
			for (int i=0; i<checkList.size(); i++) {
				int index = checkList.get(i);
				if (prev + 1 != index) {
					if (i != 0) {
						markList.add(new MarkData(offset, length, data.priority, data.color));
					}
					offset = index;
					length = 1;
				} else {
					length++;
				}
				if (i == checkList.size() - 1) {
					markList.add(new MarkData(offset, length, data.priority, data.color));
				}
				prev = index;
			}
			setMarked(marked, checkList);
		}

		return markList;
	}

	private void setMarked(boolean[] marked, List<Integer> checkList) {
		for (Integer i : checkList) {
			marked[i] = true;
		}
	}

	/**
	 * マーク可能なオフセットのリストを返す
	 * @param marked
	 * @param offset
	 * @param length
	 */
	private List<Integer> checkMarked(boolean[] marked, int offset, int length) {

		List<Integer> list = new ArrayList<Integer>();

		for (int i=offset; i<offset+length; i++) {
			if (marked[i] == false) {
				list.add(i);
			}
		}

		return list;
	}

	/**
	 * 検査処理例外クラス
	 */
	public class InspectException extends Exception {

		private static final long serialVersionUID = 1L;

		public InspectException() {
			super();
		}

		public InspectException(String message) {
			super(message);
		}

		public InspectException(Throwable cause) {
			super(cause);
		}

		public InspectException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
