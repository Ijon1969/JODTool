package jp.ac.kyushu_u.csce.modeltool.base.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

/**
 * 「はい」「いいえ」「キャンセル」の３値質問用ダイアログ
 *
 * @author KBK yoshimura
 */
public class CustomMessageDialog extends MessageDialog {

	/**
	 * コンストラクタ
	 * @see org.eclipse.jface.dialogs.MessageDialog#MessageDialog(Shell, String, Image, String, int, String[], int)
	 */
	public CustomMessageDialog(Shell parentShell, String dialogTitle,
			Image dialogTitleImage, String dialogMessage, int dialogImageType,
			String[] dialogButtonLabels, int defaultIndex) {

		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage,
				dialogImageType, dialogButtonLabels, defaultIndex);
	}

	/**
	 * Convenience method to open a simple question with cancel (Yes/No/Cancel) dialog.
	 *
	 * @param parent
	 *            the parent shell of the dialog, or <code>null</code> if none
	 * @param title
	 *            the dialog's title, or <code>null</code> if none
	 * @param message
	 *            the message
	 * @return <code>2</code> if the user presses the YES button,
	 *         <code>3</code> if the user presses the NO button,
	 *         <code>1</code> if the user presses the CANCEL button
	 */
	public static int openQuestionWithCancel(Shell parent, String title, String message) {

		// スーパークラスのメソッドgetButtonLabels()がpackageメソッドのため、直接記述する
		String[] dialogButtonLabels = new String[] {
				IDialogConstants.YES_LABEL,
				IDialogConstants.NO_LABEL,
				IDialogConstants.CANCEL_LABEL };

		MessageDialog dialog = new MessageDialog(
				parent, title, null, message, QUESTION_WITH_CANCEL, dialogButtonLabels, 0);

		switch (dialog.open()) {
			case 0:
				return IDialogConstants.YES_ID;
			case 1:
				return IDialogConstants.NO_ID;
			case 2:
				return IDialogConstants.CANCEL_ID;
		}

		return 0;
	}
}
