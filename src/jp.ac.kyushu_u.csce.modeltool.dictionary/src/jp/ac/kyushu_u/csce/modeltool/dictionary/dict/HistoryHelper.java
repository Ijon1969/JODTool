package jp.ac.kyushu_u.csce.modeltool.dictionary.dict;

import static jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants.DIC_COL_ID_NONE;

import java.util.ArrayList;
import java.util.List;

import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;

/**
 * Undo/Redo用履歴管理ヘルパークラス<br>
 * 当クラスの add○○History メソッドを使用する場合、
 * このメソッドを呼び出す前に TableTab の 変更フラグ(dirty)を変更しないでください。
 * 必ず add○○History ⇒ tab.setDirty(true) の順番で実行してください。
 *
 * @author yoshimura
 */
public class HistoryHelper extends HistoryManager {

	/**
	 * コンストラクタ
	 * @param tab 辞書タブ
	 */
	public HistoryHelper (TableTab tab) {
		super(tab);
	}

	/**
	 * 「追加」ヒストリの追加
	 * @param entry 追加したエントリ
	 * @param index 追加箇所のインデックス
	 */
	public void addAdditionHistory(List<Entry> entries, List<Integer> indexList) {
		History history = new History(
				History.TYPE_ADD,
				DictionaryConstants.DIC_COL_ID_NONE,
				DictionaryUtil.getMode(),
				indexList.get(0),
				indexList,
				null,
				getDeepCopy(entries),
				getTableTab().isDirty(),
				true);
		super.add(history);
	}

	/**
	 * 「削除」ヒストリの追加
	 * @param entry 削除したエントリ
	 * @param index 削除箇所のインデックス
	 */
	public void addDeletionHistory(List<Entry> entries, List<Integer> indexList) {
		History history = new History(
				History.TYPE_DELETE,
				DictionaryConstants.DIC_COL_ID_NONE,
				DictionaryUtil.getMode(),
				indexList.get(0),
				indexList,
				getDeepCopy(entries),
				null,
				getTableTab().isDirty(),
				true);
		super.add(history);
	}

	/**
	 * 「移動」ヒストリの追加<br>
	 * インデックスの考え方がちょっと解りづらいです。<br>
	 * 例えば、2～5を上に移動させた場合、実質的には1⇒5に移動したのと同じなので、
	 * oldIndexには1、newIndexには5をセットします。<br>
	 * 逆に、2～5を下に移動させた場合、実質的には6⇒2に移動したのと同じなので、
	 * oldIndexには6、newIndexには2をセットします。<br>
	 * indexListには選択範囲の移動前のインデックスである2～5をセットします。<br>
	 * とりあえず連続領域の選択しか考えていないのでこのやり方で実装しています。<br>
	 * 不連続領域とか考えると移動の履歴の持ち方を変えないといけません。
	 * @param oldIndex 移動前のインデックス
	 * @param newIndex 移動後のインデックス
	 */
	public void addMovementHistory(int oldIndex, int newIndex, List<Integer> indexList) {
		History history = new History(
				History.TYPE_MOVE,
				DictionaryConstants.DIC_COL_ID_NONE,
				DictionaryUtil.getMode(),
				oldIndex,
				indexList,
				oldIndex,
				newIndex,
				getTableTab().isDirty(),
				true);
		super.add(history);
	}

	/**
	 * 「編集」ヒストリの追加
	 * @param oldEntry 編集前のエントリ
	 * @param newEntry 編集後のエントリ
	 * @param index 編集箇所のインデックス
	 */
	public void addEditingHistory(Entry oldEntry, Entry newEntry, int index) {
		History history = new History(
				History.TYPE_EDIT,
				DIC_COL_ID_NONE,
				DictionaryUtil.isInspectMode()? History.MODE_INSPECT : History.MODE_OUTPUT,
				index,
				null,
				oldEntry.deepCopy(),
				newEntry.deepCopy(),
				getTableTab().isDirty(),
				true);
		super.add(history);
	}

	/**
	 * 「辞書情報」ヒストリの追加
	 * @param oldClass 変更前の辞書クラス情報
	 * @param newClass 変更後の辞書クラス情報
	 * @param oldSetting 変更前の辞書設定情報
	 * @param newSetting 変更後の辞書設定情報
	 * @param other その他履歴情報
	 */
	public void addInformationHistory(
			DictionaryClass oldClass, DictionaryClass newClass,
			DictionarySetting oldSetting, DictionarySetting newSetting, Object other) {

		History history = new History(
				History.TYPE_INFO,
				DictionaryConstants.DIC_COL_ID_NONE,
				DictionaryUtil.getMode(),
				0,
				null,
//				oldClass.deepCopy(),
//				newClass.deepCopy(),
				new DictionaryInfoHistory(oldClass, oldSetting),
				new DictionaryInfoHistory(newClass, newSetting),
				getTableTab().isDirty(),
				true);
		history.getOthers().add(other);
		super.add(history);
	}

	/**
	 * 「セル」履歴の追加
	 * @param index 変更箇所のインデックス
	 * @param columnId 変更対象カラムID
	 * @param oldValue 変更前のセルの値
	 * @param newValue 変更後のセルの値
	 */
	public void addCellHisotry(int index, int columnId, Object oldValue, Object newValue) {
		History history = new History(
				History.TYPE_CELL,
				columnId,
				DictionaryUtil.getMode(),
				index,
				null,
				oldValue,
				newValue,
				getTableTab().isDirty(),
				true);
		super.add(history);
	}

	/**
	 * 「エントリ」ヒストリの追加
	 * @param oldEntry 編集前のエントリ
	 * @param newEntry 編集後のエントリ
	 * @param index 編集箇所のインデックス
	 */
	public void addEntryHistory(Entry oldEntry, Entry newEntry, int index) {
		History history = new History(
				History.TYPE_ENTRY,
				DIC_COL_ID_NONE,
				DictionaryUtil.isInspectMode()? History.MODE_INSPECT : History.MODE_OUTPUT,
				index,
				null,
				oldEntry.deepCopy(),
				newEntry.deepCopy(),
				getTableTab().isDirty(),
				true);
		super.add(history);
	}

	/**
	 * ディープコピーの取得
	 * @param entries
	 * @return
	 */
	private List<Entry> getDeepCopy(List<Entry> entries) {
		List<Entry> ret = new ArrayList<Entry>(entries.size());
		for (Entry entry : entries) {
			ret.add(entry.deepCopy());
		}
		return ret;
	}
}
