package jp.ac.kyushu_u.csce.modeltool.dictionary.dict;

/**
 * 辞書情報の履歴を保持するクラス
 */
public class DictionaryInfoHistory {

	/** インデックス：クラス情報 */
	private static final int DICTIONARY_HISTORY_CLASS = 0;
	/** インデックス：設定情報 */
	private static final int DICTIONARY_HISTORY_SETTING = 1;

	/**
	 * 辞書情報配列
	 */
	private DictionaryInfo[] arrayInfo;

	/**
	 * コンストラクタ<br>
	 * 引数のクラス情報、設定情報をインスタンスに格納する。<br>
	 * 格納されるのはディープコピーのため元の辞書情報には影響しない。
	 * @param _dictionaryClass クラス情報
	 * @param _setting 設定情報
	 */
	public DictionaryInfoHistory(DictionaryClass _dictionaryClass, DictionarySetting _setting) {

		// 配列の作成
		arrayInfo = new DictionaryInfo[]{null, null};

		// 辞書情報を配列に格納
		if (_dictionaryClass != null) {
			arrayInfo[DICTIONARY_HISTORY_CLASS] = _dictionaryClass.deepCopy();
		}

		// 設定情報を配列に格納
		if (_setting != null) {
			arrayInfo[DICTIONARY_HISTORY_SETTING] = _setting.deepCopy();
		}
	}

	/**
	 * 辞書情報の取得
	 * @return 辞書情報のディープコピー
	 */
	public DictionaryClass getDictionaryClass() {
		if (arrayInfo[DICTIONARY_HISTORY_CLASS] == null) return null;
		return (DictionaryClass)arrayInfo[DICTIONARY_HISTORY_CLASS].deepCopy();
	}

	/**
	 * 設定情報の取得
	 * @return 設定情報のディープコピー
	 */
	public DictionarySetting getDictinarySetting() {
		if (arrayInfo[DICTIONARY_HISTORY_SETTING] == null) return null;
		return (DictionarySetting)arrayInfo[DICTIONARY_HISTORY_SETTING].deepCopy();
	}
}
