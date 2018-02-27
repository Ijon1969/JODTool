package jp.ac.kyushu_u.csce.modeltool.dictionary.dict;

import java.util.ArrayList;
import java.util.List;

/**
 * 見出し語クラス
 * @author KBK yoshimura
 */
public class Entry {

	/** 行番号 　※{@link #seqNo}(検査用行番号)、{@link #outNo}(出力用行番号)を使用すること*/
	@Deprecated
	private int number;
	/** 検査用行番号 */
	private int seqNo;
	/** 出力用行番号 */
	private int outNo;
	/** 見出し語 */
	private String word;
	/** 種別 */
	private String category;
	/** 種別No */
	private int categoryNo;
	/** 非形式的定義 ※多言語対応 {@link #informals}を使用 */
	@Deprecated
	private String informal;
	/** 形式的定義 */
	private String formal;
	/** 形式的種別 */
	private int section;
	/** 型 ※{@link #section}に"type(型)"を追加したため、こちらは未使用 */
	@Deprecated
	private String type;

	/** 副キーワードリスト */
	private List<String> subwords;
	/** 非形式的定義リスト */
	private List<String> informals;
	/** 活用形リスト */
	private List<String> conjugations;
	/** 拡張元見出し語リスト */
	private List<String> override;

	// 以下の項目はビュー表示用のため、SIZEには含めない
	/** 編集状態 */
	private boolean edit;
	/** 変更の有無 */
	private boolean modified;
	/** 出力対象 */
	private boolean output;

	/**
	 * フィールド変数の数<br>
	 * フィールドの追加・削除等を行う場合はこの定数の値も修正してください
	 * @deprecated そのうち削除します
	 */
	private static final int SIZE = 7;

	/**
	 * コンストラクタ
	 */
	public Entry() {
		clear();
	}

	/**
	 * コンストラクタ
	 * @param entry 見出し語
	 */
	public Entry(String entry) {
		clear();
		setWord(entry);
	}

	/**
	 * 行番号の取得
	 * @return 行番号
	 */
	@Deprecated
	public int getNumber() {
		return number;
	}
	/**
	 * 行番号の設定
	 * @param number 行番号
	 */
	@Deprecated
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * 見出し語の取得
	 * @return 見出し語
	 */
	public String getWord() {
		return word;
	}
	/**
	 * 見出し語の設定
	 * @param word 見出し語
	 */
	public void setWord(String word) {
		this.word = word;
	}

	/**
	 * 種別の取得
	 * @return 種別
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * 種別の設定
	 * @param category 種別
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * 種別Noの取得
	 * @return 種別No
	 */
	public int getCategoryNo() {
		return categoryNo;
	}
	/**
	 * 種別Noの設定
	 * @param categoryNo 種別No
	 */
	public void setCategoryNo(int categoryNo) {
		this.categoryNo = categoryNo;
	}

	/**
	 * 非形式的定義の取得
	 * @return 非形式的定義
	 */
	@Deprecated
	public String getInformal() {
		return informal;
	}
	/**
	 * 非形式的定義の設定
	 * @param informal 非形式的定義
	 */
	@Deprecated
	public void setInformal(String informal) {
		this.informal = informal;
	}

	/**
	 * 形式定期定義の取得
	 * @return 形式的定義
	 */
	public String getFormal() {
		return formal;
	}
	/**
	 * 形式的定義の設定
	 * @param formal 形式的定義
	 */
	public void setFormal(String formal) {
		this.formal = formal;
	}

	/**
	 * 型の取得
	 * @return 型
	 */
	public String getType() {
		return type;
	}
	/**
	 * 型の設定
	 * @param type 型
	 */
	public void setType(String type) {
		this.type = type;
	}

//	/**
//	 * 表示順の取得
//	 * @return 表示順
//	 */
//	public int getSequence() {
//		return sequence;
//	}
//	/**
//	 * 表示順の設定
//	 * @param sequence 表示順
//	 */
//	public void setSequence(int sequence) {
//		this.sequence = sequence;
//	}


	/**
	 * 検査用行番号の取得
	 * @return 検査用行番号
	 */
	public int getSeqNo() {
		return seqNo;
	}

	/**
	 * 検査用行番号の設定
	 * @param seqNo 検査用行番号
	 */
	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	/**
	 * 出力用行番号の取得
	 * @return 出力用行番号
	 */
	public int getOutNo() {
		return outNo;
	}

	/**
	 * 出力用行番号の設定
	 * @param outNo 出力用行番号
	 */
	public void setOutNo(int outNo) {
		this.outNo = outNo;
	}

	/**
	 * 形式的種別の取得
	 * @return 形式的種別
	 */
	public int getSection() {
		return section;
	}

	/**
	 * 形式的種別の設定
	 * @param section 形式的種別
	 */
	public void setSection(int section) {
		this.section = section;
	}

	/**
	 * 副キーワードリストの取得
	 * @return 副キーワードリスト
	 */
	public List<String> getSubwords() {
		return subwords;
	}

	/**
	 * 非形式的定義リストの取得
	 * @return 非形式的定義リスト
	 */
	public List<String> getInformals() {
		return informals;
	}

	/**
	 * 活用形リストの取得
	 * @return 活用形リスト
	 */
	public List<String> getConjugations() {
		return conjugations;
	}

	/**
	 * 拡張元見出し語リストの取得
	 * @return 拡張元見出し語リスト
	 */
	public List<String> getOverride() {
		return override;
	}

	/**
	 * 編集状態の取得
	 * @return 編集状態
	 */
	public boolean isEdit() {
		return edit;
	}
	/**
	 * 編集状態の設定
	 * @param edit 編集状態
	 */
	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	/**
	 * 変更有無の取得
	 * @return 変更有無
	 */
	public boolean isModified() {
		return modified;
	}
	/**
	 * 変更有無の設定
	 * @param modified 変更有無
	 */
	public void setModified(boolean modified) {
		this.modified = modified;
	}

	/**
	 * 出力対象の取得
	 * @return 出力対象
	 */
	public boolean isOutput() {
		return output;
	}
	/**
	 * 出力対象の設定
	 * @param output 出力対象
	 */
	public void setOutput(boolean output) {
		this.output = output;
	}

	/**
	 * フィールド変数のクリア
	 */
	public void clear() {
		number		= -1;
		seqNo		= -1;
		outNo		= -1;
		word		= ""; //$NON-NLS-1$
		category	= ""; //$NON-NLS-1$
		categoryNo	= 0;
		informal	= ""; //$NON-NLS-1$
		formal		= ""; //$NON-NLS-1$
		section		= 0;
		type		= ""; //$NON-NLS-1$
		edit		= false;
		output		= true;

		subwords = clearList(subwords);
		informals = clearList(informals);
		conjugations = clearList(conjugations);
		override = clearList(override);
	}

	/** 行番号のクリア */
	public void clearNumber() {
		number = -1;
	}

	/**
	 * サイズの取得
	 * @return サイズ
	 * @deprecated 使ってないです、そのうち削除予定
	 */
	public static int size() {
		return SIZE;
	}

	/**
	 * リストのクリア<br>
	 * 引数がnullの場合、新規ArrayListを返す
	 * @param list リスト
	 * @return クリア後のリスト
	 */
	private <T> List<T> clearList(List<T> list) {
		if (list == null) {
			return new ArrayList<T>();
		} else {
			list.clear();
			return list;
		}
	}

	/**
	 * オブジェクトのディープコピーを作成<br>
	 * フィールドを増やしたらこのメソッドに追加すること
	 * @return ディープコピー
	 */
	public Entry deepCopy() {
		Entry entry = new Entry();

		entry.setSeqNo(seqNo);
		entry.setOutNo(outNo);
		entry.setWord(word);
		entry.setCategory(category);
		entry.setCategoryNo(categoryNo);
		entry.setFormal(formal);
		entry.setSection(section);
		entry.setType(type);

		entry.getSubwords().addAll(subwords);
		entry.getInformals().addAll(informals);
		entry.getConjugations().addAll(conjugations);

		entry.output = output;

		return entry;
	}

	/**
	 * 副キーワード配列のコピーを取得する
	 * @return 副キーワード配列のコピー
	 */
	public List<String> copySubwords() {
		return new ArrayList<String>(subwords);
	}

	/**
	 * 活用形配列のコピーを取得する
	 * @return 活用形配列のコピー
	 */
	public List<String> copyConjugations() {
		return new ArrayList<String>(conjugations);
	}

	/**
	 * 非形式的定義配列のコピーを取得する
	 * @return 非形式的定義配列のコピー
	 */
	public List<String> copyInformals() {
		return new ArrayList<String>(informals);
	}

	/**
	 * 内容を上書きする
	 * @param entry
	 */
	public void overwrite(Entry entry) {
		seqNo = entry.getSeqNo();
		outNo = entry.getOutNo();
		word = entry.getWord();
		category = entry.getCategory();
		categoryNo = entry.getCategoryNo();
		formal = entry.getFormal();
		section = entry.getSection();
		type = entry.getType();

		subwords.clear();
		subwords.addAll(entry.getSubwords());
		conjugations.clear();
		conjugations.addAll(entry.getConjugations());
		informals.clear();
		informals.addAll(entry.getInformals());
		override.clear();
		override.addAll(entry.getOverride());

		output = entry.output;
	}

	/**
	 * 形式的定義のクリア
	 */
	public void clearFormalDefinition() {
		formal = ""; //$NON-NLS-1$
		section = 0;
		type = ""; //$NON-NLS-1$
	}
}
