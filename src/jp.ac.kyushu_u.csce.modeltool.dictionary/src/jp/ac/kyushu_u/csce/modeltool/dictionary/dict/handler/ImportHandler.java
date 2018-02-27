package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.handler;

import java.io.File;

import jp.ac.kyushu_u.csce.modeltool.dictionary.Messages;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryView;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * 辞書ファイル(JSON, XML)のインポートを行うハンドラクラス
 *
 * @author KBK yoshimura
 */
public class ImportHandler extends AbstractHandler implements IHandler {

	/** ファイル名（ワイルドカード） */
	private static final String WILD_CARD = "*."; //$NON-NLS-1$
	/** 区切り文字 */
	private static final String DELIMITER = ";";  //$NON-NLS-1$

	/**
	 * ファイルダイアログで最後に選択した拡張子のインデックス
	 */
	private int lastIndex = 0;

	/**
	 * execute
	 * @see org.eclipse.core.commands.IHandler#execute(ExecutionEvent)
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchPage page = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();

		// 辞書ビューの取得（開いていない場合処理なし）
		DictionaryView view = (DictionaryView)page.findView(DictionaryConstants.PART_ID_DICTIONARY);
		if (view == null) {
			return null;
		}

		// ファイルダイアログ
		FileDialog dialog = new FileDialog(page.getWorkbenchWindow().getShell(), SWT.OPEN);
		String[] extensions = new String[]{
				WILD_CARD + DictionaryConstants.EXTENSION_JSON + DELIMITER
					+ WILD_CARD + DictionaryConstants.EXTENSION_XML + DELIMITER
					+ WILD_CARD + DictionaryConstants.EXTENSION_CSV,
				WILD_CARD + DictionaryConstants.EXTENSION_JSON,
				WILD_CARD + DictionaryConstants.EXTENSION_XML,
				WILD_CARD + DictionaryConstants.EXTENSION_CSV};
		dialog.setFilterExtensions(extensions);
		dialog.setFilterIndex(lastIndex);
		dialog.setText(Messages.ImportHandler_0);
		String fileNm = dialog.open();
		if (StringUtils.isBlank(fileNm)) {
			return null;
		}

		// 選択された拡張子のインデックスを退避　
		//   次回表示時に前回の拡張子を初期選択するため
		lastIndex = dialog.getFilterIndex();

		File file = new File(fileNm);

		view.importDictionary(file);

		return null;
	}
}
