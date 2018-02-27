package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.edit;

import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry;

/**
 * 辞書変種タブのリスナ
 *
 * @author KBK yoshimura
 */
public interface IDictionaryEditTabListener {

	/**
	 * 更新処理
	 * @param entry 見出し語
	 */
	public void update(Entry entry);
}
