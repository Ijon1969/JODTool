package jp.ac.kyushu_u.csce.modeltool.dictionary.dict;

import static jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryPreferenceConstants.PK_DICTIONARY_HISTORY_SIZE;

import java.util.LinkedList;

import jp.ac.kyushu_u.csce.modeltool.dictionary.ModelToolDictionaryPlugin;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * Undo/Redo用履歴管理クラス
 *
 * @author yoshimura
 */
public class HistoryManager {

	/** 辞書タブ */
	private TableTab tab;

	/** Undoリスト */
	private LinkedList<History> undoHistory = new LinkedList<History>();
	/** Redoリスト */
	private LinkedList<History> redoHistory = new LinkedList<History>();

	/** プリファレンスリスナー */
	private IPropertyChangeListener listener;

	/**
	 * コンストラクタ<br>
	 * タブを閉じる際は必ずHistoryManagerのdisposeを行ってください
	 * @param tab 辞書タブ
	 */
	public HistoryManager(TableTab tab) {
		this.tab = tab;

		// リスナーの追加
		IPreferenceStore store = ModelToolDictionaryPlugin.getDefault().getPreferenceStore();
		listener = new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				// ヒストリーサイズの変更時
				if (PK_DICTIONARY_HISTORY_SIZE.equals(event.getProperty())) {
					int size = (Integer)event.getNewValue();
					// サイズをはみ出る分は削除
					resizeUndoHistory(size);
					resizeRedoHistory(size);
				}
			}
		};
		store.addPropertyChangeListener(listener);
	}

	/**
	 * 辞書タブの取得
	 * @return 辞書タブの取得
	 */
	protected TableTab getTableTab() {
		return this.tab;
	}

	/**
	 * 履歴の追加
	 * @param history 履歴
	 */
	public void add(History history) {
		// undoキューに追加
		undoHistory.addLast(history);
		// サイズオーバーの場合先頭を削除
		resizeUndoHistory(getHistorySize());
		// redoキューをクリア
		redoHistory.clear();
	}

	/**
	 * 元に戻す
	 * @return 履歴
	 */
	public History undo() {
		if (undoHistory.isEmpty()) return null;
		// undoキューの末尾要素をredoキューの先頭へ移動
		History history = undoHistory.removeLast();
		redoHistory.addFirst(history);
		// サイズオーバーの場合末尾を削除
		resizeRedoHistory(getHistorySize());
		return history;
	}

	/**
	 * 直近のUndo履歴を取得
	 * @return 履歴
	 */
	public History getLastUndo() {
		if (undoHistory.isEmpty()) return null;
		return undoHistory.getLast();
	}

	/**
	 * やり直し
	 * @return 履歴
	 */
	public History redo() {
		if (redoHistory.isEmpty()) return null;
		// redoキューの先頭要素をundoキューの末尾へ移動
		History history = redoHistory.removeFirst();
		undoHistory.addLast(history);
		// サイズオーバーの場合先頭を削除
		resizeUndoHistory(getHistorySize());
		return history;
	}

	/**
	 * 直近のRedo履歴を取得
	 * @return 履歴
	 */
	public History getFirstRedo() {
		if (redoHistory.isEmpty()) return null;
		return redoHistory.getFirst();
	}

	/**
	 * クリア<br>
	 * 履歴をクリアする
	 */
	public void clear() {
		undoHistory.clear();
		redoHistory.clear();
	}

	/**
	 * クリーン<br>
	 * 現時点の変更フラグをOFF、それ以前／以降の履歴の変更フラグをONにする<br>
	 * 辞書ファイルの保存（新規・上書き両方）の場合のみ呼び出される
	 */
	public void clean() {
		if (!undoHistory.isEmpty()) {
			for (History history : undoHistory) {
				history.setOldDirty(true);
				history.setNewDirty(true);
			}
			undoHistory.getLast().setNewDirty(false);
		}
		if (!redoHistory.isEmpty()) {
			for (History history : redoHistory) {
				history.setOldDirty(true);
				history.setNewDirty(true);
			}
			redoHistory.getFirst().setOldDirty(false);
		}
	}

	/**
	 * Undo可能か
	 * @return
	 */
	public boolean canUndo() {
		return undoHistory.size() > 0;
	}

	/**
	 * Redo可能か
	 * @return
	 */
	public boolean canRedo() {
		return redoHistory.size() > 0;
	}

	/**
	 * ヒストリーのサイズの取得
	 * @return ヒストリーのサイズ
	 */
	private int getHistorySize() {
		IPreferenceStore store = ModelToolDictionaryPlugin.getDefault().getPreferenceStore();
		return store.getInt(PK_DICTIONARY_HISTORY_SIZE);
	}

	/**
	 * Undoヒストリーのリサイズ
	 * @param size
	 */
	protected void resizeUndoHistory(int size) {
		// 指定サイズになるまで先頭要素を削除
		while(!undoHistory.isEmpty() && undoHistory.size() > size) {
			undoHistory.removeFirst();
		}
	}

	/**
	 * Redoヒストリーのリサイズ
	 * @param size
	 */
	protected void resizeRedoHistory(int size) {
		// 指定サイズになるまで末尾要素を削除
		while (!redoHistory.isEmpty() && redoHistory.size() > size) {
			redoHistory.removeLast();
		}
	}

	/**
	 * dispose
	 */
	public void dispose() {
		IPreferenceStore store = ModelToolDictionaryPlugin.getDefault().getPreferenceStore();
		store.removePropertyChangeListener(listener);
	}
}
