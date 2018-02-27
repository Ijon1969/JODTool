package jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column;

import jp.ac.kyushu_u.csce.modeltool.dictionary.ModelToolDictionaryPlugin;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.ModelManager;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;

/**
 * 辞書ビューのカラムを表す抽象クラス<br>
 *
 * @author KBK yoshimura
 */
public abstract class AbstractColumn extends EditingSupport implements IColumn {

	/** プリファレンスストア */
	protected IPreferenceStore store;

	/** モデルマネジャー */
	protected ModelManager manager;

	// セルの配置
	protected static final int LEFT		= SWT.LEFT;
	protected static final int RIGHT		= SWT.RIGHT;
	protected static final int CENTER	= SWT.CENTER;

	/** ヘッダテキスト */
	protected String text = "undefined"; //$NON-NLS-1$
	/** 幅 */
	protected int width = 100;
	/** セルエディタ */
	protected CellEditor editor = null;
	/** 辞書タブ */
	protected TableTab tab = null;
	/** アクティベート種別 */
	protected int activate = ACTIVATE_FALSE;
	/** 使用可否 */
	protected boolean enabled = false;

	/** 編集前の値 */
	protected Object preEditValue;

	/** フォーカスリスナ */
	protected FocusListener focusListener;

	/**
	 * コンストラクタ
	 */
	public AbstractColumn(TableTab tab) {

		super(tab.getTableViewer());

		this.tab = tab;
		this.store = ModelToolDictionaryPlugin.getDefault().getPreferenceStore();
		this.manager = ModelManager.getInstance();
	}

	/**
	 * TableViewerの取得
	 * @return TableViewer テーブルビューア
	 */
	public TableViewer getTableViewer() {
		return (TableViewer)getViewer();
	}

	/**
	 * ヘッダテキストの取得
	 * @return ヘッダテキスト
	 */
	public String getHeaderText() {
		return text;
	}

	/**
	 * カラム幅の取得
	 * @return 幅
	 */
	public int getColumnWidth() {
		return width;
	}

	/**
	 * カラム画像の取得
	 * @param element 行オブジェクト
	 * @return カラム画像
	 */
	public Image getColumnImage(Object element) {
		return null;
	}

	/**
	 * スタイルの取得
	 * @return スタイル
	 */
	public int getStyle() {
		return LEFT;
	}

	/**
	 * リサイズ可・不可の取得
	 */
	public boolean resizable() {
		return true;
	}

	/**
	 * 複数カラム
	 */
	public boolean multiple() {
		return false;
	}

	/**
	 * 使用可否の設定
	 * @param enabled true:可/false:不可
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * 編集可否の判定<br>
	 * <code>activate</code>および<code>enabled</code>の値より、
	 * 編集可能かを判定する。
	 * @param element 行オブジェクト
	 * @return true:可／false:不可
	 */
	protected boolean canEdit(Object element) {

		switch (activate) {
			case ACTIVATE_FALSE:
				return false;

			case ACTIVATE_TRUE:
			case ACTIVATE_CHECK:
				return true;

			case ACTIVATE_VARIABLE:
				return enabled;
		}

		return false;
	}

	/**
	 * 値の取得
	 * @param element 行オブジェクト
	 * @return セル値
	 */
	public Object getValue(Object element) {
		return null;
	}

	/**
	 * 値の設定および表示の更新
	 * @param element 行オブジェクト
	 * @param value セル値
	 */
	public void setValue(Object element, Object value) {

		// ダイアログ入力の場合、ここで変更前の値を取得
		if (editor instanceof DialogCellEditor) {
			preEditValue = getValue(element);
		}

		// 変更がある場合
		if (!value.equals(preEditValue)) {

			// 値の設定
			doSetValue(element, value);

			// 変更通知OKの場合
			if (notifyChange()) {
				// 変更フラグを立てる
				tab.setDirty(true);
			}

			// 行を再選択（SelectionChangeEventを発生させるため）
			tab.setSelection((Entry)element);

			// テーブルの表示を更新
			getViewer().update(element, null);
		}
	}

	/**
	 * 値の設定
	 * @param element 行オブジェクト
	 * @param value セル値
	 */
	protected void doSetValue(Object element, Object value) {
	}

	/**
	 * Undoヒストリの追加
	 * @param element 行オブジェクト
	 * @param value セル値
	 */
	protected void addUndoHistory(Object element, Object value) {
		tab.getHistoryHelper().addCellHisotry(
				tab.getDictionary().indexOf((Entry)element), getId(), preEditValue, value);
	}

	/**
	 * 行オブジェクトのUndoヒストリの追加
	 * @param element 変更前の行オブジェクト
	 * @param value 変更後行オブジェクト
	 */
	protected void addEntryUndoHistory(Entry before, Entry after) {
		tab.getHistoryHelper().addEntryHistory(
				before, after, tab.getDictionary().indexOf(after));
	}

	/**
	 * 編集の可否
	 * @return true:可／false:不可
	 */
	public int canActivate() {
		return activate;
	}

	/**
	 * 変更通知の可否
	 * @return 変更通知可否
	 */
	public boolean notifyChange() {
		return true;
	}

	/**
	 * カラムヘッダークリック時のリスナーを取得
	 * @return リスナー　リスナーを設定しない場合はnullを返す
	 */
	public SelectionListener getHeaderClickListener() {
		return null;
	}

	/**
	 * dispose
	 */
	public void dispose() {
	}

	/**
	 * セルエディターへリスナーを追加する。<br>
	 * DialogCellEditorでは動作しません。DilaogCellEditorを使用する場合、
	 * サブクラスで編集有無をチェックする必要があります。
	 * また、サブクラスで履歴登録（addHistory）してください。
	 * @param cellEditor セルエディタ
	 * @param element 行エレメント
	 */
	protected void setCellEditorListener(final CellEditor cellEditor, final Object element) {

		if (cellEditor == null || cellEditor.getControl() == null) {
			return;
		}

		// ダイアログ入力の場合、何もしない
		if (cellEditor instanceof DialogCellEditor) {
			return;
		}

		// コントロールの取得
		Control control = cellEditor.getControl();

		// リスナーの削除
		if (focusListener != null) {
			control.removeFocusListener(focusListener);
		}

		// フォーカスリスナーの追加
		focusListener = new FocusListener() {

			// フォーカスアウト
			public void focusLost(FocusEvent e) {
				// 辞書ビューのスコープを有効化
				tab.getView().activateContext(true);
//				// 値に変更があれば、dirtyにする  ⇒ doSetValue()で行うこと
//				if (editor.getValue().equals(preEditValue) == false) {
//					tab.setDirty(true);
//				}
			}

			// フォーカスイン
			public void focusGained(FocusEvent e) {
				// 辞書ビューのスコープ無効化
				// （セル編集中は辞書ビューのキーバインドを使用させない）
				tab.getView().activateContext(false);
				// 編集前の値の退避
				preEditValue = cellEditor.getValue();
			}
		};
		control.addFocusListener(focusListener);
	}

	/**
	 * リスナーの削除
	 * @param cellEditor
	 */
	protected void removeCellEditorListener(CellEditor cellEditor) {

		if (cellEditor == null || cellEditor.getControl() == null) {
			return;
		}

		// コントロールの取得
		Control control = cellEditor.getControl();

		// リスナーの削除
		if (focusListener != null) {
			control.removeFocusListener(focusListener);
		}
		focusListener = null;
	}

	/**
	 * 登録済みモデルかを判定する
	 * @return 登録済みモデルの場合trueを返す
	 */
	protected boolean isRegisteredModel() {
		return ModelManager.getInstance().isResisteredModel(
				tab.getDictionary().getDictionaryClass().model);
	}
}
