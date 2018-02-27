package jp.ac.kyushu_u.csce.modeltool.base.dialog;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * 複数行入力ダイアログ
 *
 * @author KBK yoshimura
 */
public class MultiInputDialog extends InputDialog {

	/** 最小行数 */
	private int minimumLine;

	/**
	 * コンストラクタ
	 * @param parentShell
	 * @param dialogTitle
	 * @param dialogMessage
	 * @param initialValue
	 * @param validator
	 * @param minimumLine
	 */
	public MultiInputDialog(Shell parentShell, String dialogTitle,
			String dialogMessage, String initialValue, IInputValidator validator,
			int minimumLine) {
		super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
		this.minimumLine = (minimumLine < 1)? 1 : minimumLine;
	}

	/**
	 * ダイアログエリア生成
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		Control control = super.createDialogArea(parent);
		Text text = getText();
		Object data = text.getLayoutData();
		if (data instanceof GridData) {
			GridData gridData = (GridData)data;
			gridData.verticalAlignment = SWT.FILL;
			gridData.grabExcessVerticalSpace = true;
		}
		return control;
	}

	/**
	 * 入力テキストフィールドのスタイル
	 * @see org.eclipse.jface.dialogs.InputDialog#getInputTextStyle()
	 */
	protected int getInputTextStyle() {
		return SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL;
	}

	/**
	 * サイズ変更可否
	 * @see org.eclipse.jface.dialogs.Dialog#isResizable()
	 */
	protected boolean isResizable() {
		return true;
	}

	/**
	 * 初期サイズ
	 * @see org.eclipse.jface.dialogs.Dialog#getInitialSize()
	 */
	protected Point getInitialSize() {
		Point point = super.getInitialSize();
		int line = (getValue() == null)? 0 : getValue().split("\r|\n|\r\n").length; //$NON-NLS-1$
		if (line < minimumLine) {
			point.y += convertHeightInCharsToPixels(minimumLine - line);
		}
		return point;
	}
}
