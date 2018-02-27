package jp.ac.kyushu_u.csce.modeltool.dictionary.listener;

import org.eclipse.core.resources.IFile;

/**
 * 辞書名称変更リスナー
 */
public interface IDictionaryRenameListener {

	/**
	 * 辞書の名前が変更されたときに呼び出されるメソッド
	 * @param file 名称変更された辞書ファイル
	 */
	public void dictionaryRenamed (IFile file);
}
