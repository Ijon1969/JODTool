package jp.ac.kyushu_u.csce.modeltool.base.utility;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * 使用していません
 *
 * @author yoshimura
 *
 */
public class ColorSelectorCellEditor extends CellEditor {

	ColorSelector selector;

	private static final RGB DEFAULT_COLOR = new RGB(0, 0, 0);

	public ColorSelectorCellEditor() {
	}

	public ColorSelectorCellEditor(Composite parent) {
		super(parent);
	}

	public ColorSelectorCellEditor(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Control createControl(Composite parent) {

		selector = new ColorSelector(parent);

		return selector.getButton();
	}

	@Override
	protected Object doGetValue() {
		return selector.getColorValue();
	}

	@Override
	protected void doSetFocus() {
		selector.getButton().setFocus();
	}

	@Override
	protected void doSetValue(Object value) {
		if (value == null) {
			selector.setColorValue(DEFAULT_COLOR);
		} else {
			selector.setColorValue((RGB)value);
		}
	}

	public void deactivate() {
		// 処理なし（常に表示）
	}
}
