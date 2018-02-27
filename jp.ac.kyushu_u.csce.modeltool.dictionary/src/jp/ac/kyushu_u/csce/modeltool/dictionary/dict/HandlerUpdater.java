package jp.ac.kyushu_u.csce.modeltool.dictionary.dict;

import static jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants.*;

import java.util.Iterator;

import jp.ac.kyushu_u.csce.modeltool.base.utility.HandlerActivationManager;
import jp.ac.kyushu_u.csce.modeltool.dictionary.ModelToolDictionaryPlugin;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.handler.AddHandler;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.handler.CopyHandler;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.handler.CutHandler;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.handler.DownHandler;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.handler.ExportHandler;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.handler.InformationHandler;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.handler.OutputHandler;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.handler.PasteHandler;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.handler.RedoHandler;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.handler.RemoveHandler;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.handler.SaveHandler;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.handler.UndoHandler;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.handler.UpHandler;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.ModelManager;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * 辞書ビューのハンドラの状態更新を行うクラス
 *
 * @author KBK yoshimura
 */
public class HandlerUpdater {

	/** ハンドラーマネージャー */
	private HandlerActivationManager manager;

	/**
	 * 選択状態
	 */
	private enum SelectionState {
		/** 未選択 */
		NONE,
		/** 単選択 */
		SINGLE,
		/** 複数選択 連続領域 */
		CONTINUOUS,
		/** 複数選択 非連続領域 */
		NOT_CONTINUOUS,
	}

	/**
	 * コンストラクタ
	 */
	public HandlerUpdater() {
		// ハンドラーマネージャーの取得
		manager = ModelToolDictionaryPlugin.getHandlerActivationManager();
	}

	/**
	 * 全ハンドラーの削除
	 */
	public void clear() {

		manager.clearActivation();
	}

	/**
	 * 更新
	 * @param tab
	 */
	public void update(TableTab tab) {

		// タブの有無によるハンドラー状態の更新
		if (tab == null) {
			// モデル出力
			manager.deactivate(COMMAND_ID_OUTPUT_MODEL);
			// 追加
			manager.deactivate(COMMAND_ID_ADD);
			// 貼り付け
			manager.deactivate(COMMAND_ID_PASTE);
			// 辞書情報
			manager.deactivate(COMMAND_ID_INFORMATION);
			// 保存
			manager.deactivate(COMMAND_ID_SAVE);

			// コピー
			manager.deactivate(COMMAND_ID_COPY);
			// 上へ移動
			manager.deactivate(COMMAND_ID_UP);
			// 下へ移動
			manager.deactivate(COMMAND_ID_DOWN);
			// 削除
			manager.deactivate(COMMAND_ID_REMOVE);
			// 切り取り
			manager.deactivate(COMMAND_ID_CUT);

			// エクスポート
			manager.deactivate(COMMAND_ID_EXPORT);

			// 元に戻す
			manager.deactivate(COMMAND_ID_UNDO);
			// やり直し
			manager.deactivate(COMMAND_ID_REDO);

		} else {
			// 辞書編集可否フラグ（モデルマネージャに登録済みかどうか）
			boolean canEdit = ModelManager.getInstance().isResisteredModel(
					tab.getDictionary().getDictionaryClass().model);

			// 選択状態の取得
			SelectionState state = getSelectionState(tab.getTableViewer());

			// 貼り付け
			if (tab.getView().isNoCopyData() || !canEdit ||
					state == SelectionState.CONTINUOUS || state == SelectionState.NOT_CONTINUOUS) {
				manager.deactivate(COMMAND_ID_PASTE);
			} else {
				manager.activate(COMMAND_ID_PASTE, PasteHandler.class);
			}
			// 辞書情報
			manager.activate(COMMAND_ID_INFORMATION, InformationHandler.class);
			// 保存
			if ((tab.isDirty() || tab.isInit()) && canEdit) {
				manager.activate(COMMAND_ID_SAVE, SaveHandler.class);
			} else {
				manager.deactivate(COMMAND_ID_SAVE);
			}

			// エクスポート
			if (canEdit) {
				manager.activate(COMMAND_ID_EXPORT, ExportHandler.class);
			} else {
				manager.deactivate(COMMAND_ID_EXPORT);
			}

			// 元に戻す
			if (tab.getHistoryHelper().canUndo() && canEdit) {
				manager.activate(COMMAND_ID_UNDO, UndoHandler.class);
			} else {
				manager.deactivate(COMMAND_ID_UNDO);
			}
			// やり直し
			if (tab.getHistoryHelper().canRedo() && canEdit) {
				manager.activate(COMMAND_ID_REDO, RedoHandler.class);
			} else {
				manager.deactivate(COMMAND_ID_REDO);
			}

			// テーブルの中身でハンドラー状態を更新
			update(tab.getTableViewer());
		}
	}

	/**
	 * 更新
	 * @param viewer
	 */
	public void update(TableViewer viewer) {

		// 辞書の取得
		Dictionary dictionary = (Dictionary)viewer.getInput();
		// 辞書編集可否フラグ（モデルマネージャに登録済みかどうか）
		boolean canEdit = ModelManager.getInstance().isResisteredModel(
				dictionary.getDictionaryClass().model);

		// 選択状態の取得
		SelectionState state = getSelectionState(viewer);

		// テーブルアイテム、選択されたテーブルアイテムの取得
		Table table = viewer.getTable();
		TableItem[] tableItems = null;
		TableItem[] selections = null;
		if (table.isDisposed()) {
			tableItems = new TableItem[0];
			selections = new TableItem[0];
		} else {
			tableItems = viewer.getTable().getItems();
			selections = viewer.getTable().getSelection();
		}

		// アイテム数、アイテム選択のチェック
		boolean isEntryExist = (tableItems.length > 0);
		boolean isEntrySelected = isEntryExist && selections.length > 0;
		boolean isTopSelected = isEntrySelected && tableItems[0].equals(selections[0]);
		boolean isBottomSelected = isEntrySelected &&
									tableItems[tableItems.length - 1].equals(selections[selections.length - 1]);
		boolean isEditingSelected = false;
		for (TableItem item : selections) {
			if (((Entry)item.getData()).isEdit()) {
				isEditingSelected = true;
				break;
			}
		}
		boolean isOutputMode =	DictionaryUtil.isOutputMode();

		// 追加
		if (canEdit && (state == SelectionState.NONE || state == SelectionState.SINGLE)) {
			// 活性
			manager.activate(COMMAND_ID_ADD, AddHandler.class);
		} else {
			// 非活性
			manager.deactivate(COMMAND_ID_ADD);
		}

		// コピー
		// 見出し語が選択されている場合
		if (isEntrySelected && canEdit) {
			// 活性
			manager.activate(COMMAND_ID_COPY, CopyHandler.class);
		} else {
			// 非活性
			manager.deactivate(COMMAND_ID_COPY);
		}

		// 上へ移動
		// 先頭の見出し語が選択されている場合
		if (!isEntrySelected || isTopSelected || !canEdit ||
				state == SelectionState.NOT_CONTINUOUS) {
			// 非活性
			manager.deactivate(COMMAND_ID_UP);
		} else {
			// 活性
			manager.activate(COMMAND_ID_UP, UpHandler.class);
		}

		// 下へ移動
		// 末尾の見出し語が選択されている場合
		if (!isEntrySelected || isBottomSelected || !canEdit ||
				state == SelectionState.NOT_CONTINUOUS) {
			// 非活性
			manager.deactivate(COMMAND_ID_DOWN);
		} else {
			// 活性
			manager.activate(COMMAND_ID_DOWN, DownHandler.class);
		}

		// 削除・切り取り
		// 編集中の見出し語が選択されていない場合
		if (isEntrySelected && !isEditingSelected && canEdit) {
			// 活性
			manager.activate(COMMAND_ID_REMOVE, RemoveHandler.class);
			manager.activate(COMMAND_ID_CUT, CutHandler.class);
		} else {
			// 非活性
			manager.deactivate(COMMAND_ID_REMOVE);
			manager.deactivate(COMMAND_ID_CUT);
		}

		// 出力モードの場合
		if (isOutputMode && canEdit) {
			// モデル出力
			manager.activate(COMMAND_ID_OUTPUT_MODEL, OutputHandler.class);
		} else {
			manager.deactivate(COMMAND_ID_OUTPUT_MODEL);
		}
	}


	// 辞書の編集なしに状態が変わる場合、以下のメソッドを使用する。
	// 貼り付け：コピーを行った場合に実行可能
	// 保存　　：保存を行った場合に実行不可
	/**
	 * 「貼り付け」ハンドラー切替
	 * @param tab
	 * @param isActivate
	 */
	public void updatePaste(TableTab tab, boolean isActivate) {

		if (tab != null && isActivate) {
			manager.activate(COMMAND_ID_PASTE, PasteHandler.class);
		} else {
			manager.deactivate(COMMAND_ID_PASTE);
		}
	}

	/**
	 * 「保存」ハンドラー切替
	 * @param tab
	 */
	public void updateSave(TableTab tab) {

		if (tab != null && (tab.isInit() || tab.isDirty())) {
			manager.activate(COMMAND_ID_SAVE, SaveHandler.class);
		} else {
			manager.deactivate(COMMAND_ID_SAVE);
		}
	}

	/**
	 * 選択状態の判定
	 * @param viewer
	 * @return 選択状態
	 */
	private SelectionState getSelectionState(TableViewer viewer) {

		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();

		switch (selection.size()) {
			case 0:		// 未選択
				return SelectionState.NONE;

			case 1:		// 単選択
				return SelectionState.SINGLE;

			default:	// 複数選択
				Dictionary dictionary = (Dictionary)viewer.getInput();	// getInputで取得するのはあんまりスマートじゃないなあ…
				Iterator<?> itr = selection.iterator();
				int prvIndex = -1;
				while (itr.hasNext()) {
					Entry entry = (Entry)itr.next();
					int index = dictionary.indexOf(entry);
					if (prvIndex > 0) {
						// 直前の選択インデックスの次の要素でない場合
						if (index != prvIndex + 1) {
							return SelectionState.NOT_CONTINUOUS;		// 不連続選択
						}
					}
					prvIndex = index;
				}
				return SelectionState.CONTINUOUS;	// 連続選択
		}
	}
}
