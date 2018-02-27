package jp.ac.kyushu_u.csce.modeltool.dictionary.dialog;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.swt.widgets.Shell;

public class DictionaryPreferenceDialog extends PreferenceDialog {

	private DictionaryPreferenceDialog(Shell parentShell,
			PreferenceManager manager) {
		super(parentShell, manager);
	}

}
