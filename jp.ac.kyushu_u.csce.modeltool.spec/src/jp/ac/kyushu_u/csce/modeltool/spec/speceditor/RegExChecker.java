package jp.ac.kyushu_u.csce.modeltool.spec.speceditor;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.IWordChecker;
import jp.ac.kyushu_u.csce.modeltool.spec.ModelToolSpecPlugin;
import jp.ac.kyushu_u.csce.modeltool.spec.constant.SpecPreferenceConstants;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * 見出し語の正規表現チェック
 * @author yoshimura
 */
public class RegExChecker implements IWordChecker {

	@Override
	public boolean check(Entry _entry) {

		IPreferenceStore store = ModelToolSpecPlugin.getDefault().getPreferenceStore();

		// 正規表現検査チェックONの場合のみチェックする
		if (store.getBoolean(SpecPreferenceConstants.PK_USE_REGULAR_EXPRESSION)) {
			try {
				Pattern.compile(_entry.getWord());
				for (String subword : _entry.getSubwords()) {
					Pattern.compile(subword);
				}
			} catch (PatternSyntaxException e) {
				return false;
			}
		}

		return true;
	}
}
