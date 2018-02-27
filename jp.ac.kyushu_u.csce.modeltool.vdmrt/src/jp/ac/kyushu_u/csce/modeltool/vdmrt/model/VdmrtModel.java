package jp.ac.kyushu_u.csce.modeltool.vdmrt.model;

import static jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants.*;
import static jp.ac.kyushu_u.csce.modeltool.vdmrt.constants.VdmrtConstants.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ac.kyushu_u.csce.modeltool.base.utility.ColorName;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Dictionary;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.Model;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.ModelElement;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.ModelError;

import org.eclipse.core.runtime.Path;

/**
 * VDM-RTモデルクラス
 * @author KBK yoshimura
 * @since 2016/01/04
 */
public class VdmrtModel extends Model {

	/** 形式的定義テンプレート：クラス */
	private static final String TEMPLATE_CLASS = "class {0}"; //$NON-NLS-1$
	/** 形式的定義テンプレート：インスタンス変数 */
	private static final String TEMPLATE_INSTANCE_VARIABLE = "{0}: ;"; //$NON-NLS-1$
	/** 形式的定義テンプレート：定数 */
	private static final String TEMPLATE_VALUE = "{0}: = ;"; //$NON-NLS-1$
	/** 形式的定義テンプレート：関数 */
	private static final String TEMPLATE_FUNCTION = "{0}: -> \r\n{0}() == ;"; //$NON-NLS-1$
	/** 形式的定義テンプレート：手続き */
	private static final String TEMPLATE_OPERATION = "{0}: ==> \r\n{0}() == ;"; //$NON-NLS-1$
	/** 形式的定義テンプレート：型 */
	private static final String TEMPLATE_TYPE = "{0} == ;"; //$NON-NLS-1$
	/** 形式的定義テンプレート：スレッド */
	private static final String TEMPLATE_THREAD = ""; //$NON-NLS-1$
	/** 形式的定義テンプレート：トレース */
	private static final String TEMPLATE_TRACE = ""; //$NON-NLS-1$
	/** 形式的定義テンプレート：システム */
	private static final String TEMPLATE_SYSTEM = "system {0}"; //$NON-NLS-1$

	/**
	 * 形式的種別定義配列<br>
	 * ここの並びで辞書ビューのプルダウンに表示される
	 */
	private static final List<Model.Section> SECTION_DEFS = new ArrayList<Model.Section>();
	static {
		// system
		SECTION_DEFS.add(new Section(
				SECTION_NO_SYSTEM,
				SECTION_SYSTEM,
				TEMPLATE_SYSTEM,
				true,
				ColorName.RGB_AQUAMARINE));
		// class
		SECTION_DEFS.add(new Section(
				SECTION_NO_CLASS,
				SECTION_CLASS,
				TEMPLATE_CLASS,
				true,
				ColorName.RGB_AQUAMARINE));
		// instance variable
		SECTION_DEFS.add(new Section(
				SECTION_NO_INSTANCE_VARIABLE,
				SECTION_INSTANCE_VARIABLE,
				TEMPLATE_INSTANCE_VARIABLE));
		// value
		SECTION_DEFS.add(new Section(
				SECTION_NO_VALUE,
				SECTION_VALUE,
				TEMPLATE_VALUE));
		// function
		SECTION_DEFS.add(new Section(
				SECTION_NO_FUNCTION,
				SECTION_FUNCTION,
				TEMPLATE_FUNCTION));
		// operation
		SECTION_DEFS.add(new Section(
				SECTION_NO_OPERATION,
				SECTION_OPERATION,
				TEMPLATE_OPERATION));
		// type
		SECTION_DEFS.add(new Section(
				SECTION_NO_TYPE,
				SECTION_TYPE,
				TEMPLATE_TYPE));
		// thread
		SECTION_DEFS.add(new Section(
				SECTION_NO_THREAD,
				SECTION_THREAD,
				TEMPLATE_THREAD));
		// trace
		SECTION_DEFS.add(new Section(
				SECTION_NO_TRACE,
				SECTION_TRACE,
				TEMPLATE_TRACE));
	}

	/** アクセス修飾子「private」 */
	private static final String KEYWORD_PRIVATE = "private"; //$NON-NLS-1$

	/** キーワード「class」 */
	private static final String KEYWORD_CLASS = "class"; //$NON-NLS-1$

	/** キーワード「system」 */
	private static final String KEYWORD_SYSTEM = "system"; //$NON-NLS-1$

	/** エラーコードマップ */
	private Map<Integer, String> errorMap = new HashMap<Integer, String>();
	{
		// VDM++のエラーメッセージをそのまま使用するため、dictionaryプラグインのMessageクラスを使用する
		errorMap.put(E_NO_SECTION,			jp.ac.kyushu_u.csce.modeltool.dictionary.Messages.VDMPPModel_8);
		errorMap.put(E_NO_DEFINITION,		jp.ac.kyushu_u.csce.modeltool.dictionary.Messages.VDMPPModel_9);
		errorMap.put(E_CLASS_UNKNOWN,		jp.ac.kyushu_u.csce.modeltool.dictionary.Messages.VDMPPModel_10);
		errorMap.put(E_WRONG_DEFINITION,	jp.ac.kyushu_u.csce.modeltool.dictionary.Messages.VDMPPModel_11);
		errorMap.put(E_CLASS_WRONG,			jp.ac.kyushu_u.csce.modeltool.dictionary.Messages.VDMPPModel_12);
	}

	/** エラーコード 種別なし */
	private static final int E_NO_SECTION		= 1 << 0;
	/** エラーコード 定義なし */
	private static final int E_NO_DEFINITION		= 1 << 1;
	/** エラーコード 所属クラス不明 */
	private static final int E_CLASS_UNKNOWN		= 1 << 2;
	/** エラーコード クラス定義不正 */
	private static final int E_WRONG_DEFINITION	= 1 << 3;
	/** エラーコード 所属クラス定義不正 */
	private static final int E_CLASS_WRONG		= 1 << 4;

	private static final String NEW_LINE = "\r\n"; //$NON-NLS-1$

	@Override
	public void doConvert(Dictionary dictionary,
			List<ModelElement> elements, List<ModelError> errors) {

		/** クラスリスト */
		List<VdmClass> classList = new ArrayList<VdmClass>();

		/** エラーリスト */
		List<Member> errorList = new ArrayList<Member>();

		// 見出し語リスト（コピー）の取得
		List<Entry> list = dictionary.getEntries(true);

		// outNo順にソート
		Collections.sort(list, new Comparator<Entry>() {
			public int compare(Entry o1, Entry o2) {
				return o1.getOutNo() - o2.getOutNo();
			}
		});

		for (Entry entry : list) {

			// 出力チェックボックスOFFの場合は出力しない
			if (!entry.isOutput()) {
				continue;
			}

			// 行番号の取得
			int no = entry.getOutNo();
			// 要素名の取得
			String name = entry.getWord();
			// 要素定義の取得
			String definition = entry.getFormal();

			switch(entry.getSection()) {

				// クラス
				case SECTION_NO_CLASS:
					addClass(classList, errorList, no, name, definition);
					break;

				// インスタンス変数
				case SECTION_NO_INSTANCE_VARIABLE:
					addInstanceVariable(classList, errorList, no, name, definition);
					break;

				// 定数
				case SECTION_NO_VALUE:
					addValue(classList, errorList, no, name, definition);
					break;

				// 関数
				case SECTION_NO_FUNCTION:
					addFunction(classList, errorList, no, name, definition);
					break;

				// 手続き
				case SECTION_NO_OPERATION:
					addOperation(classList, errorList, no, name, definition);
					break;

				// 型
				case SECTION_NO_TYPE:
					addTypes(classList, errorList, no, name, definition);
					break;

				// スレッド
				case SECTION_NO_THREAD:
					addThreads(classList, errorList, no, name, definition);
					break;

				// トレース
				case SECTION_NO_TRACE:
					addTraces(classList, errorList, no, name, definition);
					break;

				// システム
				case SECTION_NO_SYSTEM:
					addSystem(classList, errorList, no, name, definition);
					break;

				// 上記以外
				default:
					errorList.add(new Member(no, name, definition).setError(E_NO_SECTION));
					break;
			}
		}

		// エレメントリストの出力
		for (VdmClass vdmClass : classList) {
			// エラーがある場合、出力しない
			if (vdmClass.getError() > 0) {
				continue;
			}
			// リストに追加
			elements.add(new ModelElement(vdmClass.className, getClassElement(vdmClass)));
		}

		// エラーログ出力
		for (Member member : errorList) {
			errors.add(new ModelError(member.no, member.name, member.definition,
												errorMap.get(member.getError())));
		}
	}

	/**
	 * クラス要素の取得<br>
	 * VDM-RTクラスオブジェクトをコード文字列に変換する。
	 * @param vdmClass VDM-RTクラスオブジェクト
	 * @return VDM-RTコード
	 */
	private String getClassElement(VdmClass vdmClass) {

		StringBuffer sb = new StringBuffer();

		// クラス宣言 (class or system)
		sb.append(vdmClass.definition).append(NEW_LINE).append(NEW_LINE);

		// 定数
		sb.append(getMemberElement(vdmClass.values, "values")); //$NON-NLS-1$

		// 変数
		sb.append(getMemberElement(vdmClass.instanceVariables, "instance variables")); //$NON-NLS-1$

		// 関数
		sb.append(getMemberElement(vdmClass.functions, "functions")); //$NON-NLS-1$

		// 手続き
		sb.append(getMemberElement(vdmClass.operations, "operations")); //$NON-NLS-1$

		// 型
		sb.append(getMemberElement(vdmClass.types, "types")); //$NON-NLS-1$

		// スレッド
		sb.append(getMemberElement(vdmClass.threads, "thread")); //$NON-NLS-1$

		// トレース
		sb.append(getMemberElement(vdmClass.traces, "traces")); //$NON-NLS-1$

		// privateクラス
		for (VdmClass innerClass : vdmClass.innerClasses) {
			// エラーのないもののみ出力する
			if (innerClass.getError() == 0) {
				sb.append(getClassElement(innerClass));
				sb.append(NEW_LINE);
			}
		}

		// クラスend
		sb.append("end ").append(vdmClass.className).append(NEW_LINE); //$NON-NLS-1$

		return sb.toString();
	}

	/**
	 * メンバ要素の取得
	 * @param list メンバリスト
	 * @param blockName ブロック名
	 * @return メンバ要素
	 */
	private String getMemberElement(List<Member> list, String blockName) {

		StringBuffer sb = new StringBuffer();

		if (!list.isEmpty()) {
			if (isOutputableObject(list)) {
				sb.append(blockName).append(NEW_LINE);
			}
			for (Member member : list) {
				if (member.getError() == 0) {
					sb.append(member.definition).append(NEW_LINE).append(NEW_LINE);
				}
			}
		}

		return sb.toString();
	}

	/**
	 * カレントクラスの取得
	 * @return カレントクラス。カレントクラスが存在しない場合null。
	 */
	private VdmClass getClass(List<VdmClass> classList, boolean inner) {

		// 空の場合
		if (classList.isEmpty()) {
			return null;
		}

		// 内部クラスを見ない場合、最後の外部クラスを返す
		VdmClass vdmClass = classList.get(classList.size() - 1);
		if (! inner) {
			return vdmClass;
		}

		// 外部クラス、内部クラス含めて最後のクラスを返す
		List<VdmClass> innerClasses = vdmClass.innerClasses;
		return innerClasses.isEmpty()? vdmClass : innerClasses.get(innerClasses.size() - 1);
	}

	/**
	 * クラスの追加
	 * @param name クラス名
	 * @param definition クラス定義
	 */
	private void addClass(List<VdmClass> classList, List<Member> errorList,
			int no, String name, String definition) {

		// クラス定義を空白・改行で分割
		String[] lines = definition.trim().split("\\s+"); //$NON-NLS-1$

		// クラス名
		String className = ""; //$NON-NLS-1$

		// 内部クラスフラグ
		boolean isPrivate = false;

		if (lines.length > 0) {

			// 第1要素が "private" の場合内部クラス
			isPrivate = KEYWORD_PRIVATE.equals(lines[0]);

			// クラス名（"class" の次の要素"）を取得
			int index = -1;
			for (int i = 0; i < lines.length; i++) {
				if (KEYWORD_CLASS.equals(lines[i])) {
					index = i;
				}
			}
			if (index >= 0 && index < lines.length - 1) {
				className = lines[index + 1];
			}
		}

		boolean inner = false;
		VdmClass vdmClass = null;
		List<VdmClass> list = classList;
		if (isPrivate) {
			vdmClass = getClass(classList, true);
			if (vdmClass != null) {
				inner = true;
				if (vdmClass.inner) {
					vdmClass = getClass(classList, false);
				}
				list = vdmClass.innerClasses;
			}
		}

		// クラスの追加
		VdmClass newClass = new VdmClass(no, className, name, definition, inner);

		// 定義が空白文字のみの場合
		if (definition.matches("\\s*")) { //$NON-NLS-1$
			newClass.setError(E_NO_DEFINITION);
		}

		// 親クラスが存在しない場合、または親クラスが所属クラス不明の場合
		else if (isPrivate && (vdmClass == null || (vdmClass.getError() & E_CLASS_UNKNOWN) != 0)) {
			newClass.setError(E_CLASS_UNKNOWN);
		}

		// クラス名が取得できない、またはファイル名に使用できない
		else if (className.length() == 0 || new Path("").isValidSegment(className) == false) { //$NON-NLS-1$
			newClass.setError(E_WRONG_DEFINITION);
		}

		// 親クラスが定義エラーの場合
		else if (inner && (vdmClass.getError() & (E_WRONG_DEFINITION | E_CLASS_WRONG)) != 0 ) {
			newClass.setError(E_CLASS_WRONG);
		}

		// クラスの追加
		list.add(newClass);

		// エラーがある場合、エラーリストにも追加する
		if (newClass.getError() > 0) {
			errorList.add(newClass);
		}
	}

	/**
	 * システムの追加
	 * @param name クラス名
	 * @param definition クラス定義
	 */
	private void addSystem(List<VdmClass> classList, List<Member> errorList,
			int no, String name, String definition) {

		// クラス定義を空白・改行で分割
		String[] lines = definition.trim().split("\\s+"); //$NON-NLS-1$

		// クラス名
		String className = ""; //$NON-NLS-1$

		if (lines.length > 0) {

			// クラス名（"system" の次の要素"）を取得
			int index = -1;
			for (int i = 0; i < lines.length; i++) {
				if (KEYWORD_SYSTEM.equals(lines[i])) {
					index = i;
				}
			}
			if (index >= 0 && index < lines.length - 1) {
				className = lines[index + 1];
			}
		}

		// クラスの追加
		VdmClass newClass = new VdmClass(no, className, name, definition, false);

		// 定義が空白文字のみの場合
		if (definition.matches("\\s*")) { //$NON-NLS-1$
			newClass.setError(E_NO_DEFINITION);
		}

		// クラス名が取得できない、またはファイル名に使用できない
		else if (className.length() == 0 || new Path("").isValidSegment(className) == false) { //$NON-NLS-1$
			newClass.setError(E_WRONG_DEFINITION);
		}

		// クラスの追加
		classList.add(newClass);

		// エラーがある場合、エラーリストにも追加する
		if (newClass.getError() > 0) {
			errorList.add(newClass);
		}
	}

	/**
	 * 定数の追加。カレントクラスの定数配列に要素を追加する。
	 * @param name 定数名
	 * @param definition 定数定義
	 */
	private void addValue(List<VdmClass> classList, List<Member> errorList, int no, String name, String definition) {
		VdmClass vdmClass = getClass(classList, true);
		List<Member> values = (vdmClass == null)? null : vdmClass.values;
		addMember(values, errorList, no, name, definition, vdmClass);
	}

	/**
	 * インスタンス変数の追加。カレントクラスのインスタンス変数配列に要素を追加する。
	 * @param name インスタンス変数名
	 * @param definition インスタンス変数定義
	 */
	private void addInstanceVariable(List<VdmClass> classList, List<Member> errorList, int no, String name, String definition) {
		VdmClass vdmClass = getClass(classList, true);
		List<Member> instanceVariables = (vdmClass == null)? null : vdmClass.instanceVariables;
		addMember(instanceVariables, errorList, no, name, definition, vdmClass);
	}

	/**
	 * 関数の追加。カレントクラスの関数配列に要素を追加する。
	 * @param name 関数名
	 * @param definition 関数定義
	 */
	private void addFunction(List<VdmClass> classList, List<Member> errorList, int no, String name, String definition) {
		VdmClass vdmClass = getClass(classList, true);
		List<Member> functions = (vdmClass == null)? null : vdmClass.functions;
		addMember(functions, errorList, no, name, definition, vdmClass);
	}

	/**
	 * 手続きの追加。カレントクラスの手続き配列に要素を追加する。
	 * @param name 手続き名
	 * @param definition 手続き定義
	 */
	private void addOperation(List<VdmClass> classList, List<Member> errorList, int no, String name, String definition) {
		VdmClass vdmClass = getClass(classList, true);
		List<Member> operations = (vdmClass == null)? null : vdmClass.operations;
		addMember(operations, errorList, no, name, definition, vdmClass);
	}

	/**
	 * 型の追加。カレントクラスの型配列に要素を追加する。
	 * @param name 型名
	 * @param definition 型定義
	 */
	private void addTypes(List<VdmClass> classList, List<Member> errorList, int no, String name, String definition) {
		VdmClass vdmClass = getClass(classList, true);
		List<Member> types = (vdmClass == null)? null : vdmClass.types;
		addMember(types, errorList, no, name, definition, vdmClass);
	}

	/**
	 * スレッドの追加。カレントクラスのスレッド配列に要素を追加する。
	 * @param name スレッド名
	 * @param definition スレッド定義
	 */
	private void addThreads(List<VdmClass> classList, List<Member> errorList, int no, String name, String definition) {
		VdmClass vdmClass = getClass(classList, true);
		List<Member> threads = (vdmClass == null)? null : vdmClass.threads;
		addMember(threads, errorList, no, name, definition, vdmClass);
	}

	/**
	 * トレースの追加。カレントクラスのトレース配列に要素を追加する。
	 * @param name トレース名
	 * @param definition トレース定義
	 */
	private void addTraces(List<VdmClass> classList, List<Member> errorList, int no, String name, String definition) {
		VdmClass vdmClass = getClass(classList, true);
		List<Member> traces = (vdmClass == null)? null : vdmClass.traces;
		addMember(traces, errorList, no, name, definition, vdmClass);
	}

	/**
	 * メンバーの追加
	 * @param list
	 * @param no
	 * @param name
	 * @param definition
	 * @return
	 */
	private Member addMember(List<Member> list, List<Member> errorList, int no, String name, String definition, VdmClass vdmClass) {

		Member member = new Member(no, name, definition);

		// 定義が空白文字のみの場合
		if (definition.matches("\\s*")) { //$NON-NLS-1$
			member.setError(E_NO_DEFINITION);
		}

		// リストが空、または親クラスが所属不明エラーの場合
		else if (vdmClass == null || (vdmClass.getError() & (E_CLASS_UNKNOWN)) != 0) {
			member.setError(E_CLASS_UNKNOWN);
		}

		// クラスの定義が不正の場合
		else if ((vdmClass.getError() & (E_WRONG_DEFINITION | E_CLASS_WRONG)) != 0) {
			member.setError(E_CLASS_WRONG);
		}

		// エラーがある場合、エラーリストに追加
		if (member.getError() > 0) {
			errorList.add(member);
		}

		// リストに追加
		if (list != null) {
			list.add(member);
		}

		return member;
	}

	/**
	 * 内部クラス - VDM-RTのクラスを表すクラス
	 *
	 * @author KBK yoshimura
	 */
	private class VdmClass extends Member {

		/** クラス名 */
		String className;

		/** 定数 */
		List<Member> values;
		/** インスタンス変数 */
		List<Member> instanceVariables;
		/** 関数 */
		List<Member> functions;
		/** 手続き */
		List<Member> operations;
		/** 型 */
		List<Member> types;
		/** スレッド */
		List<Member> threads;
		/** トレース */
		List<Member> traces;

		/** 内部クラス */
		List<VdmClass> innerClasses;
		/** 内部クラスフラグ */
		boolean inner;

		/**
		 * コンストラクタ
		 * @param no セクション番号
		 * @param className クラス名
		 * @param name 名称
		 * @param definition クラス定義
		 * @param inner 内部クラスフラグ
		 */
		public VdmClass(int no, String className, String name, String definition, boolean inner) {
			super(no, name, definition);
			this.className = className;
			values = new ArrayList<Member>();
			instanceVariables = new ArrayList<Member>();
			functions = new ArrayList<Member>();
			operations = new ArrayList<Member>();
			types = new ArrayList<Member>();
			threads = new ArrayList<Member>();
			traces = new ArrayList<Member>();
			this.inner = inner;
			innerClasses = new ArrayList<VdmClass>();
		}

		public VdmClass setError(int error) {
			super.setError(error);
			return this;
		}
	}

	/**
	 * メンバを表すクラス
	 *
	 * @author KBK yoshimura
	 */
	private class Member {

		/** 行番号 */
		int no;
		/** 名称 */
		String name;
		/** 定義 */
		String definition;
		/** エラーコード */
		private int error;

		public Member(int no, String name, String definition) {
			this.no = no;
			this.name = name;
			this.definition = definition;
			this.error = 0;
		}

		public Member setError(int error) {
			this.error = error;
			return this;
		}

		public int getError() {
			return error;
		}
	}

	/**
	 * 出力可能オブジェクトの判定
	 * @param list オブジェクト
	 * @return 結果 true:可能/false:不可
	 */
	private boolean isOutputableObject(List<Member> list) {
		if (list == null || list.isEmpty()) return false;
		for (Member member : list) {
			if (member.getError() == 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<Section> getSectionDefs() {
		return SECTION_DEFS;
	}

	@Override
	public String getExtension() {
		return EXTENSION_VDMRT;
	}
}
