package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;

/**
 *
 * @author KBK yoshimura
 */
public interface IColumn {

	// セル・ダブルクリック時の動作モード
	/** 編集不可 */
	public static final int ACTIVATE_FALSE		= 0;
	/** 編集可 */
	public static final int ACTIVATE_TRUE			= 1;
	/** 条件による */
	public static final int ACTIVATE_VARIABLE		= 1 << 1;
//	public static final int ACTIVATE_DIALOG		= 1 << 2;
	/** チェックボックス */
	public static final int ACTIVATE_CHECK		= 1 << 3;

	/**
	 * カラムタイトルの取得
	 */
	public String getHeaderText();

	/**
	 * カラムIDの取得
	 */
	public int getId();

	/**
	 * カラム幅の取得
	 */
	public int getColumnWidth();

	/**
	 * カラムスタイルの取得
	 */
	public int getStyle();

	/**
	 * リサイズ可・不可の取得
	 */
	public boolean resizable();

	/**
	 * 複数カラム
	 */
	public boolean multiple();

//	/**
//	 * カラムプロパティーIDの取得
//	 */
//	public String getColumnId();

//	/**
//	 * セルエディターの取得
//	 * @param parent
//	 */
//	public CellEditor getCellEditor(Composite parent);

	///////////////////////////
	// ラベルプロバイダ関連
	///////////////////////////
	/**
	 * セルの値を取得
	 * @param element 行オブジェクト
	 * @return セル値
	 */
	public String getColumnText(Object element);
	/**
	 * セルの画像を取得
	 * @param element 行オブジェクト
	 * @return セル画像
	 */
	public Image getColumnImage(Object element);

//	///////////////////////////
//	// セルモディファイア関連
//	///////////////////////////
//	public Object getValue(Object element);
//	public void modify(Object element, Object value);
//	public int getEnabled();

	/**
	 * ダブルクリックによる編集の可否
	 * @return 編集可否
	 */
	public int canActivate();

	/**
	 * 変更通知の可否
	 * @return 変更通知可否
	 */
	public boolean notifyChange();

	/**
	 * カラムヘッダークリック時のリスナーを取得
	 * @return リスナー　リスナーを設定しない場合はnullを返す
	 */
	public SelectionListener getHeaderClickListener();

	/**
	 * dispose
	 */
	public void dispose();
}
