package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.handler;

import java.io.File;
import java.text.MessageFormat;

import jp.ac.kyushu_u.csce.modeltool.dictionary.Messages;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryView;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * 辞書ファイル(JSON)のエクスポートを行うハンドラクラス
 *
 * @author KBK yoshimura
 */
public class ExportHandler extends AbstractHandler implements IHandler {

	/** ファイル名（ワイルドカード） */
	private static final String WILD_CARD = "*."; //$NON-NLS-1$

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

		// アクティブなタブがなければ何もしない
		TableTab tab = view.getActiveTableTab(false);
		if (tab == null) {
			return null;
		}

		// ファイルダイアログ
		FileDialog dialog = new FileDialog(page.getWorkbenchWindow().getShell(), SWT.SAVE);
		String[] extensions = new String[]{
				WILD_CARD + DictionaryConstants.EXTENSION_JSON.toLowerCase(),
				WILD_CARD + DictionaryConstants.EXTENSION_CSV.toLowerCase()};
		dialog.setFilterExtensions(extensions);
		dialog.setFilterIndex(lastIndex);
		dialog.setFileName(tab.getDictionaryName());
		dialog.setText(Messages.ExportHandler_3);
		String fileNm = dialog.open();
		if (StringUtils.isBlank(fileNm)) {
			return null;
		}

		// 選択された拡張子のインデックスを退避　
		//   次回表示時に前回の拡張子を初期選択するため
		lastIndex = dialog.getFilterIndex();

		File file = new File(fileNm);
		if (file.exists()) {
			boolean overwrite = MessageDialog.openQuestion(
					view.getSite().getShell(),
					Messages.ExportHandler_0,
					MessageFormat.format(Messages.ExportHandler_1, new Object[]{file.getName()}));

			if (!overwrite) return null;
		}

		// 処理が完了した場合、メッセージを表示
		if(view.exportDictionary(file, tab)) {
			MessageDialog.openInformation(view.getSite().getShell(), Messages.ExportHandler_0, Messages.ExportHandler_2);
		};

		return null;
	}
}
