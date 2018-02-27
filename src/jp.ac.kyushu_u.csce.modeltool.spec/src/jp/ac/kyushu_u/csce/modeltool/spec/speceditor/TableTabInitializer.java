package jp.ac.kyushu_u.csce.modeltool.spec.speceditor;

import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.ITableTabInitializer;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;
import jp.ac.kyushu_u.csce.modeltool.spec.ModelToolSpecPlugin;
import jp.ac.kyushu_u.csce.modeltool.spec.constant.SpecPreferenceConstants;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

public class TableTabInitializer implements ITableTabInitializer {

	private IPropertyChangeListener listener = null;

	@Override
	public void initialize(TableTab _tab) {
		final TableTab tab = _tab;
		IPreferenceStore store = ModelToolSpecPlugin.getDefault().getPreferenceStore();

		listener = new IPropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent event) {

				IFile file = tab.getFile();

				// 既定辞書が変更された場合
				if (SpecPreferenceConstants.PK_REGISTER_FIXED_PATH.equals(event.getProperty())) {
					if (file != null && file.getFullPath().toString().equals(event.getNewValue())) {
						tab.setBold(true);
					} else {
						tab.setBold(false);
					}
				}

				// 正規表現検索の切替
				if (SpecPreferenceConstants.PK_USE_REGULAR_EXPRESSION.equals(event.getProperty())) {
					tab.getTableViewer().refresh();
				}
			}
		};

		store.addPropertyChangeListener(listener);
	}

	@Override
	public void finalize(TableTab _tab) {
		IPreferenceStore store = ModelToolSpecPlugin.getDefault().getPreferenceStore();
		store.removePropertyChangeListener(listener);
	}
}
