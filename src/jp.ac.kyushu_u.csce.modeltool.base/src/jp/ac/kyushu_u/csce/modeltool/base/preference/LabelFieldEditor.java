package jp.ac.kyushu_u.csce.modeltool.base.preference;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 *
 * @author KBK yoshimura
 *
 * 使ってないです
 */
public class LabelFieldEditor extends FieldEditor {

	private static final String PREFERENCE_NAME = "label_preference_name"; //$NON-NLS-1$

	public LabelFieldEditor(String labelText, Composite parent) {
		init(PREFERENCE_NAME, labelText);
		createControl(parent);
	}

	protected void adjustForNumColumns(int numColumns) {
		Label label = getLabelControl();
		GridData gd = (GridData)label.getLayoutData();
		gd.horizontalSpan = numColumns;
	}

	protected void doFillIntoGrid(Composite parent, int numColumns) {
		Label label = getLabelControl(parent);
		GridData gd = new GridData();
		gd.horizontalSpan = numColumns;
		label.setLayoutData(gd);
	}

	protected void doLoad() {
	}

	protected void doLoadDefault() {
	}

	protected void doStore() {
	}

	public int getNumberOfControls() {
		return 1;
	}

}
