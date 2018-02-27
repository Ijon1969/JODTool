package jp.ac.kyushu_u.csce.modeltool.spec.speceditor;

import org.eclipse.jface.preference.IPreferenceStore;

import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.ITableTabFileLoader;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;
import jp.ac.kyushu_u.csce.modeltool.spec.ModelToolSpecPlugin;
import jp.ac.kyushu_u.csce.modeltool.spec.constant.SpecPreferenceConstants;

public class TableTabFileLoader implements ITableTabFileLoader {

	@Override
	public void afterLoadFile(TableTab _tab) {

		IPreferenceStore store = ModelToolSpecPlugin.getDefault().getPreferenceStore();
		// 登録辞書の指定方法をプリファレンスストアから取得
		int registerDictionary = store.getInt(SpecPreferenceConstants.PK_REGISTER_DICTIONARY);
		// 「規定の辞書」の場合、太字にする
		if (registerDictionary == SpecPreferenceConstants.PV_REGISTER_FIXED) {
			String dictionaryPath = store.getString(SpecPreferenceConstants.PK_REGISTER_FIXED_PATH);
			if (dictionaryPath.equals(_tab.getFile().getFullPath().toString())) {
				_tab.setBold(true);
			} else {
				_tab.setBold(false);
			}
		}
	}
}
