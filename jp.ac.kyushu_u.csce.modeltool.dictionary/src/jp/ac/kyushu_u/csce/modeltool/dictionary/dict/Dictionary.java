package jp.ac.kyushu_u.csce.modeltool.dictionary.dict;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ListenerList;

/**
 * 辞書ドメインオブジェクト
 * @author KBK yoshimura
 */
public class Dictionary {

	/** 見出し語リスト */
	private List<Entry> entries;
	/** リスナーリスト */
	private ListenerList listeners;

	/** クラス */
	private DictionaryClass dictionaryClass;

	/** 設定 */
	private DictionarySetting setting;


	/** ソート／採番定数　{@link Entry#number}の値を使用 */
	public static final int NUMBER = 1;

	/** ソート／採番定数　{@link Entry#word}の値を使用 */
	public static final int WORD = 1 << 1;

	/** ソート／採番定数　{@link Entry#category}の値を使用 */
	public static final int CATEGORY = 1 << 2;

	/** ソート／採番定数　{@link Entry#informal}の値を使用 */
	public static final int INFORMAL = 1 << 3;

	/** ソート／採番定数　{@link Entry#formal}の値を使用 */
	public static final int FORMAL = 1 << 4;

	/** ソート／採番定数　{@link Entry#type}の値を使用 */
	public static final int TYPE = 1 << 5;

	/** ソート／採番定数　{@link Entry#seqNo}の値を使用 */
	public static final int SEQNO = 1 << 6;

	/** ソート／採番定数　{@link Entry#outNo}の値を使用 */
	public static final int OUTNO = 1 << 7;

	/** ソート／採番定数　{@link Entry#section}の値を使用 */
	public static final int SECTION = 1 << 12;

	/** ソート／採番定数 　ソートと同時に採番を行う */
	public static final int NUMBERING = 1 << 8;

	/**
	 * ソート／採番定数 　論理ソート<br>
	 * 物理的なデータの並び替えを行わない場合、このオプションを指定する。
	 */
	public static final int LOGICAL = 1 << 9;

	/** ソート／採番定数 　昇順 */
	public static final int ASC = 1 << 10;

	/** ソート／採番定数 　降順 */
	public static final int DESC = 1 << 11;


	/**
	 * コンストラクタ
	 */
	public Dictionary() {
		entries = new ArrayList<Entry>();
		listeners = new ListenerList();
		dictionaryClass = new DictionaryClass();
		setting = new DictionarySetting();
	}


	/**
	 * クリア
	 */
	public void clear() {
		if (entries == null) {
			entries = new ArrayList<Entry>();
		} else {
			entries.clear();
		}
		if (dictionaryClass == null) {
			dictionaryClass = new DictionaryClass();
		} else {
			dictionaryClass.clear();
		}
		if (setting == null) {
			setting = new DictionarySetting();
		} else {
			setting.clear();
		}
	}

	/**
	 * 見出し語リストの取得<br>
	 * 他のクラスから見出し語リストを直接編集するのはできる限り避けること。<br>
	 * リストのシャローコピーを取得する場合は、<code>getEntries(true)</code>を使用する。
	 * @return 見出し語リスト
	 */
	public List<Entry> getEntries() {
		return entries;
	}

	/**
	 * 見出し語リストの取得<br>
	 * copyにtrueを指定した場合、見出し語リストのシャローコピーを取得する。<br>
	 * 他のクラスから見出し語リスト本体を直接編集するのはできる限り避けること。<br>
	 * @param copy - true:シャローコピー／false:リスト本体
	 * @return 見出し語リスト
	 */
	public List<Entry> getEntries(boolean copy) {
		if (copy) {
			return new ArrayList<Entry>(entries);
		}
		return entries;
	}

	/**
	 * EntryのIteratorの取得
	 * @return Iterator
	 */
	public Iterator<Entry> getIterator() {
		return entries.iterator();
	}

	/**
	 * 辞書情報の取得
	 * @return 辞書情報
	 */
	public DictionaryClass getDictionaryClass() {
		return dictionaryClass;
	}

	/**
	 * 辞書情報の設定
	 * @param dictionaryClass 辞書情報
	 */
	public void setClass(DictionaryClass dictionaryClass) {
		// 入力言語の更新
		updateLangeuages(dictionaryClass.languages);
		this.dictionaryClass.set(dictionaryClass);
	}

	/**
	 * 辞書設定の取得
	 * @return 辞書設定
	 */
	public DictionarySetting getSetting() {
		return setting;
	}

	/**
	 * 辞書設定の取得
	 * @return 辞書設定
	 */
	public void setSetting(DictionarySetting setting) {
		this.setting.set(setting);
	}

	/**
	 * リスナーの追加
	 * @param listener リスナー
	 */
	public void addDictionaryListener(IDictionaryListener listener) {
		listeners.add(listener);
	}

	/**
	 * リスナーの削除
	 * @param listener リスナー
	 */
	public void removeDictionaryListener(IDictionaryListener listener) {
		listeners.remove(listener);
	}

	/**
	 * 見出し語を追加する
	 * @param entry 見出し語
	 */
	public void add(Entry entry) {

		if (entry.getSeqNo() < 0) {
			entry.setSeqNo(getNewNumber());
		}
		if (entry.getOutNo() < 0) {
			entry.setOutNo(getNewNumber());
		}
		entries.add(entry);

		update();
	}

	/**
	 * 見出し語を指定した位置に追加する
	 * @param index 追加位置
	 * @param entry 見出し語
	 * @param option 表示順 {@link Dictionary#SEQNO} または {@link Dictionary#OUTNO}
	 */
	public void add(int index, Entry entry, int option) {

		if ((option & SEQNO) != 0) {
			entry.setOutNo(getNewNumber());
		}
		if ((option & OUTNO) != 0) {
			entry.setSeqNo(getNewNumber());
		}
		entries.add(index, entry);

		number(option);

		update();
	}

	/**
	 * 見出し語を削除する
	 * @param entry 見出し語
	 * @param option 表示順 {@link Dictionary#SEQNO} または {@link Dictionary#OUTNO}
	 */
	public void remove(Entry entry, int option) {

		entries.remove(entry);

		number(option);

		int reverse = ~option & (SEQNO | OUTNO);
		sort(reverse | ASC | NUMBERING | LOGICAL);

		update();
	}

	/**
	 * リスト内の指定された位置にある要素を返す
	 * @param index インデックス
	 * @return 指定位置の見出し語
	 */
	public Entry get(int index) {
		return entries.get(index);
	}

	/**
	 * 指定された見出し語がリスト内で最初に検出された位置のインデックスを返す
	 * @param entry 見出し語
	 * @return インデックス
	 */
	public int indexOf(Entry entry) {
		return entries.indexOf(entry);
	}

	/**
	 * 指定された位置にある要素をスワップする
	 * @param i インデックス1
	 * @param j インデックス2
	 */
	public void swap(int i, int j, int option) {
		Collections.swap(entries, i, j);
		number(option);
		update();
	}

	/**
	 * リストの要素をリスト内の別の場所に移動する
	 * @param org 移動対象の要素のインデックス
	 * @param dst 移動先のインデックス
	 * @param option オプション
	 */
	public void move(int org, int dst, int option) {
		Entry entry = entries.remove(org);
		entries.add(dst, entry);
		number(option);
		update();
	}

	/**
	 * 辞書の見出し語数を取得する
	 * @return 辞書に登録されている見出し語の数
	 */
	public int size() {
		return entries.size();
	}

	/**
	 * 見出し語の行番号をリストの表示順で採番する
	 *
	 * @param option SEQNO or OUTNO
	 *
	 * @see Dictionary#SEQNO
	 * @see Dictionary#OUTNO
	 */
	public void number(int option) {
		number(entries, option);
	}

	/**
	 * 見出し語の行番号をリストの表示順で採番する
	 *
	 * @param option SEQNO or OUTNO
	 * @param list リスト
	 *
	 * @see Dictionary#SEQNO
	 * @see Dictionary#OUTNO
	 */
	public void number(List<Entry> list, int option) {
		int column = option & (SEQNO | OUTNO);
		if (column == 0) return;
		switch(column) {
			case SEQNO:
				for (ListIterator<Entry> itr = list.listIterator(); itr.hasNext(); )
					itr.next().setSeqNo(itr.nextIndex());
				break;
			case OUTNO:
				for (ListIterator<Entry> itr = list.listIterator(); itr.hasNext(); )
					itr.next().setOutNo(itr.nextIndex());
				break;
			default:
				break;
		}
	}

	/**
	 * 新規項番取得
	 * @return 新規項番
	 */
	public int getNewNumber() {

		return entries.size() + 1;
	}

	/**
	 * 辞書を更新する<br>
	 * 登録されたすべてのリスナーに対して、更新メッセージを発行する
	 * entriesの編集を行う場合、編集後に必ずこのメソッドを呼び出してください
	 */
	private void update() {
		for (Object obj : listeners.getListeners()) {
			IDictionaryListener listener = (IDictionaryListener)obj;
			listener.update();
		}
	}

//	/**
//	 * 見出し語リストのソート<br>
//	 * 見出し語リストを項番の昇順にソートした結果を返す<br>
//	 * <br>
//	 * isCopyがtrueの場合、見出し語リストのコピーをソートして返す。
//	 * この場合、見出し語リスト自体の順番は変わらない。<br>
//	 * isCopyがfalseの場合、見出し語リスト自体をソートする。
//	 * @param isCopy リストのコピーをソートするか
//	 * @return ソート結果
//	 */
//	@Deprecated
//	public List<Entry> sort(boolean isCopy) {
//
//		List<Entry> list = null;
//		if (isCopy) {
//			list = new ArrayList<Entry>(entries);
//		} else {
//			list = entries;
//		}
//
//		Collections.sort(list, new Comparator<Entry>() {
//			public int compare(Entry entry1, Entry entry2) {
//				return entry1.getNumber() - entry2.getNumber();
//			}
//		});
//
//		if (isCopy == false) {
//			update();
//		}
//
//		return list;
//	}

	/**
	 * ソート
	 * @param option ソートオプション<br>
	 *		{@link #SEQNO}と{@link #OUTNO}のどちらか一方のみ必ず指定すること。
	 *		{@link #SEQNO}：{@code seqNo}でソート／{@link #OUTNO}：{@code outNo}でソート<br>
	 *		{@link #ASC}と{@link #DESC}のどちらか一方のみ必ず指定すること。
	 *		{@link #ASC}：昇順／{@link #DESC}：降順<br>
	 *		{@link #NUMBERING}を指定すると、ソートと同時に採番を行う。
	 *		採番するのは{@link #SEQNO}/{@link #OUTNO}で指定した項目。<br>
	 *		{@link #LOGICAL}を指定すると、{@code entries}のコピーを作成しソートを行う。
	 *		entriesそのものの順番は変更されない。
	 *
	 *@return optionに{@link #LOGICAL}を指定した場合のみ、ソート結果のリストを返す。<br>
	 *		指定しない場合はnullを返す。
	 *
	 * @see Dictionary#SEQNO
	 * @see Dictionary#OUTNO
	 * @see Dictionary#ASC
	 * @see Dictionary#DESC
	 * @see Dictionary#NUMBERING
	 * @see Dictionary#LOGICAL
	 */
	public List<Entry> sort(int option) {

		// ソートカラム
		final int column = option & (SEQNO | OUTNO);
		Assert.isTrue(column != 0);

		// オーダー
		int orderOption = option & (ASC | DESC);
		Assert.isTrue(orderOption != 0);
		final int order;
		if (orderOption == ASC) {
			order = 1;
		} else {
			order = -1;
		}

		// 採番
		boolean numbering = ((option & NUMBERING) != 0);

		// 論理ソート
		boolean logical = ((option & LOGICAL) != 0);


		// ソート用リスト
		List<Entry> list;
		if (logical) {
			list = new ArrayList<Entry>(entries);
		} else {
			list = entries;
		}

		// ソートの実行
		Collections.sort(list, new Comparator<Entry>() {
			public int compare(Entry o1, Entry o2) {
				switch(column) {
					case SEQNO:
						return (o1.getSeqNo() - o2.getSeqNo()) * order;
					case OUTNO:
						return (o1.getOutNo() - o2.getOutNo()) * order;
					default:
						return 0;
				}
			}
		});

		// 採番
		if (numbering) {
			number(list, column);
		}

		if (! logical) {
			update();
		}

		// 直接entriesを触らせたくないので、論理ソートの場合のみ結果を返す。
		if (logical) {
			return list;
		} else {
			return null;
		}
	}

	/**
	 * ソート<br>
	 * SEQNO, OUTNO 以外でのソートは原則的に行いません。<br>
	 * {@link #sort(int)}の方を使用するのでこちらのメソッドは使いません。
	 * @param mode ソートカラム　複数指定不可. SEQNO, OUTNOのどちらかを指定すること
	 * @param order 1:昇順／-1:降順
	 * @deprecated
	 */
	public void sort(int mode, final int order) {
		switch (mode) {
//			case NUMBER:
//				Collections.sort(entries, new Comparator<Entry>() {
//					public int compare(Entry o1, Entry o2) {
//						return (o1.getNumber() - o2.getNumber()) * order;
//					}
//				});
//				break;

			case WORD:
				Collections.sort(entries, new Comparator<Entry>() {
					public int compare(Entry o1, Entry o2) {
						return o1.getWord().compareTo(o2.getWord()) * order;
					}
				});
				break;

			case CATEGORY:
				Collections.sort(entries, new Comparator<Entry>() {
					public int compare(Entry o1, Entry o2) {
						return (o1.getCategoryNo() - o2.getCategoryNo()) * order;
					}
				});
				break;

//			case INFORMAL:
//				Collections.sort(entries, new Comparator<Entry>() {
//					public int compare(Entry o1, Entry o2) {
//						return o1.getInformal().compareTo(o2.getInformal()) * order;
//					}
//				});
//				break;

			case FORMAL:
				Collections.sort(entries, new Comparator<Entry>() {
					public int compare(Entry o1, Entry o2) {
						return o1.getFormal().compareTo(o2.getFormal()) * order;
					}
				});
				break;

			case TYPE:
				Collections.sort(entries, new Comparator<Entry>() {
					public int compare(Entry o1, Entry o2) {
						return o1.getType().compareTo(o2.getType()) * order;
					}
				});
				break;

			case SEQNO:
				Collections.sort(entries, new Comparator<Entry>() {
					public int compare(Entry o1, Entry o2) {
						return (o1.getSeqNo() - o2.getSeqNo()) * order;
					}
				});
				break;

			case OUTNO:
				Collections.sort(entries, new Comparator<Entry>() {
					public int compare(Entry o1, Entry o2) {
						return (o1.getOutNo() - o2.getOutNo()) * order;
					}
				});
				break;

			default:
				return;
		}
		number(mode);
		update();
	}

	/**
	 * 種別ごと登録数を集計する
	 * @return
	 */
	public TreeMap<Object, Integer> getCategorySubtotal() {

		TreeMap<Object, Integer> map = new TreeMap<Object, Integer>();

		for (Entry entry : entries) {
			int key = entry.getCategoryNo();
			if (map.containsKey(key)) {
				map.put(key, map.get(key) + 1);
			} else {
				map.put(key, 1);
			}
		}

		return map;
	}

	/**
	 * 辞書編集ビューで編集中のエントリがあるか判定する
	 * @return true：編集中エントリが存在する／false：編集中エントリが存在しない
	 */
	public boolean isEditing() {
		for (Entry entry : entries) {
			if (entry.isEdit()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 入力言語の変更内容を辞書オブジェクトに反映する
	 */
	public void updateLangeuages(List<String> newlanguages) {

		// 入力言語リスト
		List<String> languages = dictionaryClass.languages;
		// 変更前の状態を退避しておく
		List<String> bkLanguages = new ArrayList<String>(languages);

		// クリア
		languages.clear();

		// 変更後の内容を反映
		languages.addAll(newlanguages);
		int size = languages.size();

		// 旧⇒新のインデックスマッピング
		Map<Integer, Integer> indexMap = new HashMap<Integer, Integer>();
		for (int i=0; i<bkLanguages.size(); i++) {
			indexMap.put(i, languages.indexOf(bkLanguages.get(i)));
		}

		// 非形式的定義の内容を変更
		for (Entry entry : entries) {
			List<String> informals = entry.getInformals();
			List<String> bkInformals = new ArrayList<String>(informals);
			informals.clear();
			String[] newInformals = new String[size];
			Arrays.fill(newInformals, ""); //$NON-NLS-1$
			for (int i=0; i<bkLanguages.size(); i++) {
				Integer j = indexMap.get(i);
				if (j != null && j >= 0) {
					newInformals[j] = bkInformals.get(i);
				}
			}
			informals.addAll(Arrays.asList(newInformals));
		}
	}

	/**
	 * 形式的定義のクリア
	 */
	public void clearFormalDefinition() {
		for (Entry entry : entries) {
			entry.clearFormalDefinition();
		}
	}
}
