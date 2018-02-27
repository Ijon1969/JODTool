package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model;

import jp.ac.kyushu_u.csce.modeltool.dictionary.Messages;

/**
 * モデル変換時エラークラス
 * TODO:VDMに依存した形になっているため、より一般的な作りにする必要がある
 *
 * @author KBK yoshimura
 */
public class ModelError {

	/** 行番号 */
	private Integer no;
	/** 見出し語 */
	private String word;
	/** 形式的定義 */
	private String formal;
	/** エラー内容 */
	private String error;
	/** エラー種別 */
	private int errorType = TYPE_ERROR;

	/** エラー種別：エラー */
	public static final int TYPE_ERROR = 1;
	/** エラー種別：警告 */
	public static final int TYPE_WARNING = 2;

	/**
	 * コンストラクタ<br>
	 * エラー種別 = 1:エラー
	 * @param no
	 * @param word
	 * @param formal
	 * @param error
	 */
	public ModelError(Integer no, String word, String formal, String error) {
		this.no = no;
		this.word = word;
		this.formal = formal;
		this.error = error;
	}

	/**
	 * コンストラクタ
	 * @param no
	 * @param word
	 * @param formal
	 * @param error
	 * @param errorType {@link ModelError#TYPE_ERROR} or {@link ModelError#TYPE_WARNING}
	 */
	public ModelError(Integer no, String word, String formal, String error, int errorType) {
		this.no = no;
		this.word = word;
		this.formal = formal;
		this.error = error;
		this.errorType = errorType;
	}

	/**
	 * 行番号の取得
	 * @return
	 */
	public Integer getNo() {
		return no;
	}

	/**
	 * 見出し語の取得
	 * @return
	 */
	public String getWord() {
		return word;
	}

	/**
	 * 形式的定義の取得
	 * @return
	 */
	public String getFormal() {
		return formal;
	}

	/**
	 * エラー内容の取得
	 * @return
	 */
	public String getError() {
		return error;
	}

	/**
	 * エラー種別の取得
	 * @return
	 */
	public int getErrorType() {
		return errorType;
	}

	/**
	 * エラーメッセージの取得<br>
	 * 出力結果ダイアログを修正したので多分このメソッドは使わない…
	 * @return
	 */
	public String getErrorMessage() {

		StringBuffer sb = new StringBuffer();

		if (no != null) {
			sb.append(Messages.ModelError_0).append(no).append("："); //$NON-NLS-1$
			sb.append(word.replaceAll("[\r\n]", "")).append("\t"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			sb.append("(").append(error).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			sb.append(error);
		}

		return sb.toString();
	}
}
