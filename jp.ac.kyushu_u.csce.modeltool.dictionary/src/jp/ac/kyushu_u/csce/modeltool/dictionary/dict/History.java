package jp.ac.kyushu_u.csce.modeltool.dictionary.dict;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryPreferenceConstants;


/**
 * 辞書編集履歴クラス
 *
 * @author yoshimura
 */
public class History {

	/** 編集区分：行追加 */
	public static final int TYPE_ADD			= 1;
	/** 編集区分：行削除 */
	public static final int TYPE_DELETE		= 2;
	/** 編集区分：行移動 */
	public static final int TYPE_MOVE		= 3;
	/** 編集区分：辞書編集ビュー */
	public static final int TYPE_EDIT		= 4;
	/** 編集区分：辞書情報ダイアログ */
	public static final int TYPE_INFO		= 5;
	/** 編集区分：セル */
	public static final int TYPE_CELL		= 6;
	/** 編集区分：エントリ */
	public static final int TYPE_ENTRY		= 7;

	/** 編集時のモード：検査 */
	public static final int MODE_INSPECT		= DictionaryPreferenceConstants.PV_DIC_DISP_INSPECT;
	/** 編集時のモード：出力 */
	public static final int MODE_OUTPUT		= DictionaryPreferenceConstants.PV_DIC_DISP_OUTPUT;

	/** 編集区分 */
	private int type;
	/** 編集カラムID */
	private int columnId;
	/** 編集時のモード */
	private int mode;
	/** 行番号 */
	private int index;
	/** 行番号リスト */
	private List<Integer> indexList = new ArrayList<Integer>();
	/** 編集前の値 */
	private Object oldValue;		// 汎用的に使いたいのでObject型で. newValueも同様.
	/** 編集後の値 */
	private Object newValue;
	/** 編集前の変更フラグ */
	private boolean oldDirty;
	/** 編集後の変更フラグ */
	private boolean newDirty;
	/** その他履歴情報 */
	private List<Object> others;

	/**
	 * コンストラクタ
	 * @param type 編集区分
	 * @param columnId カラムID
	 * @param mode 編集時のモード
	 * @param index 行番号
	 * @param indexList 行番号リスト
	 * @param oldValue 編集前の値
	 * @param newValue 編集後の値
	 * @param oldDirty 編集前の変更フラグ
	 * @param newDirty 編集後の変更フラグ
	 */
	public History(int type, int columnId, int mode, int index, List<Integer> indexList,
			Object oldValue, Object newValue, boolean oldDirty, boolean newDirty) {
		this.type = type;
		this.columnId = columnId;
		this.mode = mode;
		this.index = index;
		setIndexList(indexList);
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.oldDirty = oldDirty;
		this.newDirty = newDirty;
		this.others = new ArrayList<Object>();
	}

	/**
	 * 編集区分の取得
	 * @return 編集区分
	 */
	public int getType() {
		return type;
	}
	/**
	 * 編集区分の設定
	 * @param type 編集区分
	 */
	public void setType(int type) {
		this.type = type;
	}
	/**
	 * 編集カラムIDの取得
	 * @return 編集カラムID
	 */
	public int getColumnId() {
		return columnId;
	}
	/**
	 * 編集カラムIDの設定
	 * @param columnId 編集カラムID
	 */
	public void setColumnId(int columnId) {
		this.columnId = columnId;
	}
	/**
	 * 編集時のモードの取得
	 * @return 編集時のモード
	 */
	public int getMode() {
		return mode;
	}
	/**
	 * 編集時のモードの設定
	 * @param mode 編集時のモード
	 */
	public void setMode(int mode) {
		this.mode = mode;
	}
	/**
	 * 行番号の取得
	 * @return 行番号
	 */
	public int getIndex() {
		return index;
	}
	/**
	 * 行番号の設定
	 * @param inde 行番号x
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	/**
	 * 行番号リストの取得
	 * @return 行番号リスト
	 */
	public List<Integer> getIndexList() {
		return new ArrayList<Integer>(indexList);
	}
	/**
	 * 行番号リストの取得(昇順)
	 * @return 行番号リスト
	 */
	public List<Integer> getIndexListAsc() {
		List<Integer> list = getIndexList();
		Collections.sort(list);
		return list;
	}
	/**
	 * 行番号リストの取得(降順)
	 * @return 行番号リスト
	 */
	public List<Integer> getIndexListDesc() {
		List<Integer> list = getIndexList();
		Collections.reverse(list);
		return list;
	}
	/**
	 * 行番号リストの設定
	 * @param indexList 行番号リスト
	 */
	public void setIndexList(List<Integer> indexList) {
		this.indexList.clear();
		if (indexList != null) {
			this.indexList.addAll(indexList);
		}
	}
	/**
	 * 編集前の値の取得
	 * @return
	 */
	public Object getOldValue() {
		return oldValue;
	}
	/**
	 * 編集前の値の設定
	 * @param oldValue
	 */
	public void setOldValue(Object oldValue) {
		this.oldValue = oldValue;
	}
	/**
	 * 編集後の値の取得
	 * @return
	 */
	public Object getNewValue() {
		return newValue;
	}
	/**
	 * 編集後の値の設定
	 * @param newValue
	 */
	public void setNewValue(Object newValue) {
		this.newValue = newValue;
	}
	/**
	 * 編集前の変更フラグの取得
	 * @return
	 */
	public boolean isOldDirty() {
		return oldDirty;
	}
	/**
	 * 編集前の変更フラグの設定
	 * @param oldDirty
	 */
	public void setOldDirty(boolean oldDirty) {
		this.oldDirty = oldDirty;
	}
	/**
	 * 編集後の変更フラグの取得
	 * @return
	 */
	public boolean isNewDirty() {
		return newDirty;
	}
	/**
	 * 編集後の変更フラグの設定
	 * @param newDirty
	 */
	public void setNewDirty(boolean newDirty) {
		this.newDirty = newDirty;
	}
	/**
	 * その他履歴情報リストの取得
	 * @return
	 */
	public List<Object> getOthers() {
		return others;
	}
}
