package jp.ac.kyushu_u.csce.modeltool.vdmsl.model;

import static jp.ac.kyushu_u.csce.modeltool.vdmsl.constants.VdmslConstants.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import jp.ac.kyushu_u.csce.modeltool.base.utility.ColorName;
import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Dictionary;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.Model;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.ModelElement;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.ModelError;
import jp.ac.kyushu_u.csce.modeltool.vdmsl.Messages;

import org.eclipse.core.runtime.Path;

/**
 * VDM-SLモデルクラス
 * @author KBK yoshimura
 * @since 2014/11/17
 */
public class VdmslModel extends Model {

	/** 形式的定義テンプレート：モジュール */
	private static final String TEMPLATE_MODULE = "module {0}"; //$NON-NLS-1$
	/** 形式的定義テンプレート：状態 */
	private static final String TEMPLATE_STATE_OF = "state {0} of\r\nend;"; //$NON-NLS-1$
	/** 形式的定義テンプレート：定数 */
	private static final String TEMPLATE_VALUE = "{0}: = ;"; //$NON-NLS-1$
	/** 形式的定義テンプレート：関数 */
	private static final String TEMPLATE_FUNCTION = "{0}: -> \r\n{0}() == ;"; //$NON-NLS-1$
	/** 形式的定義テンプレート：手続き */
	private static final String TEMPLATE_OPERATION = "{0}: ==> \r\n{0}() == ;"; //$NON-NLS-1$
	/** 形式的定義テンプレート：型 */
	private static final String TEMPLATE_TYPE = "{0} == ;"; //$NON-NLS-1$

	/**
	 * 形式的種別定義配列<br>
	 * ここの並びで辞書ビューのプルダウンに表示される
	 */
	private static final List<Model.Section> SECTION_DEFS = new ArrayList<Model.Section>();
	static {
		// module
		SECTION_DEFS.add(new Section(
				SECTION_NO_MODULE,
				SECTION_MODULE,
				TEMPLATE_MODULE,
				true,
				ColorName.RGB_AQUAMARINE));
		// state of
		SECTION_DEFS.add(new Section(
				SECTION_NO_STATE_OF,
				SECTION_STATE_OF,
				TEMPLATE_STATE_OF));
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
	}

	/** キーワード「module」 */
	private static final String KEYWORD_MODULE = "module"; //$NON-NLS-1$

	/** エラーコードマップ */
	private Map<Integer, String> errorMap = new HashMap<Integer, String>();
	{
		errorMap.put(E_NO_SECTION,			Messages.VdmslModel_0);
		errorMap.put(E_NO_DEFINITION,		Messages.VdmslModel_1);
//		errorMap.put(E_CLASS_UNKNOWN,		"Not belong to any class.");
		errorMap.put(E_WRONG_DEFINITION,	Messages.VdmslModel_2);
		errorMap.put(E_MODULE_WRONG,		Messages.VdmslModel_3);
		errorMap.put(E_STATE_WRONG, 		Messages.VdmslModel_4);
	}

	/** エラーコード 種別なし */
	private static final int E_NO_SECTION		= 1 << 0;
	/** エラーコード 定義なし */
	private static final int E_NO_DEFINITION		= 1 << 1;
//	/** エラーコード 所属クラス不明 */
//	private static final int E_CLASS_UNKNOWN		= 1 << 2;
	/** エラーコード モジュール定義不正 */
	private static final int E_WRONG_DEFINITION	= 1 << 3;
	/** エラーコード 所属モジュール定義不正 */
	private static final int E_MODULE_WRONG		= 1 << 4;
	/** エラーコード 状態定義不正 */
	private static final int E_STATE_WRONG		= 1 << 5;

	/** 改行コード */
	private static final String NEW_LINE = "\r\n"; //$NON-NLS-1$

	/** 状態 正規表現パターン */
//	private static final String REGEX_STATE_OF	= "^\\s*state \\S+ of\\s*($|--)"; //$NON-NLS-1$
	private static final String REGEX_STATE_OF	= "\\s*state \\S+ of\\s*"; //$NON-NLS-1$
	/** 状態 正規表現パターン */
//	private static final String REGEX_STATE_END	= "^\\s*end;\\s*($|--)"; //$NON-NLS-1$
	private static final String REGEX_STATE_END	= "\\s*end;\\s*"; //$NON-NLS-1$

	@Override
	protected void doConvert(Dictionary dictionary,
			List<ModelElement> elements, List<ModelError> errors) {

		// 先頭（有効）レコードフラグ
		boolean isFirst = true;

		// モジュールリスト
		List<VdmModule> moduleList = new ArrayList<VdmModule>();

		// エラーリスト
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

			// 先頭レコードの場合
			if (isFirst) {
				// 先頭レコードがモジュールでない場合
				if (entry.getSection() != SECTION_NO_MODULE) {
					// 警告を追加
					errors.add(new ModelError(entry.getOutNo(), entry.getWord(), entry.getFormal(),
							Messages.VdmslModel_5, ModelError.TYPE_WARNING));
				}
				isFirst = false;
			}

			// 行番号の取得
			int no = entry.getOutNo();
			// 要素名の取得
			String name = entry.getWord();
			// 要素定義の取得
			String definition = entry.getFormal();

			switch(entry.getSection()) {

				// モジュール
				case SECTION_NO_MODULE:
					addModule(moduleList, errorList, no, name, definition);
					break;

				// 状態
				case SECTION_NO_STATE_OF:
					addState(moduleList, errorList, no, name, definition);
					break;

				// 定数
				case SECTION_NO_VALUE:
					addValue(moduleList, errorList, no, name, definition);
					break;

				// 関数
				case SECTION_NO_FUNCTION:
					addFunction(moduleList, errorList, no, name, definition);
					break;

				// 手続き
				case SECTION_NO_OPERATION:
					addOperation(moduleList, errorList, no, name, definition);
					break;

				// 型
				case SECTION_NO_TYPE:
					addType(moduleList, errorList, no, name, definition);
					break;

				// 上記以外
				default:
					errorList.add(new Member(no, name, definition).setError(E_NO_SECTION));
					break;
			}
		}

		// エレメントリストの出力
		for (VdmModule vdmModule : moduleList) {
			// エラーがある場合、出力しない
			if (vdmModule.getError() > 0) {
				continue;
			}
			// リストに追加
			elements.add(new ModelElement(vdmModule.moduleName, getModuleElement(vdmModule)));
		}

		// エラーログ出力
		for (Member member : errorList) {
			errors.add(new ModelError(member.no, member.name, member.definition,
												errorMap.get(member.getError())));
		}
	}

	private String getModuleElement(VdmModule vdmModule) {

		StringBuffer sb = new StringBuffer();

		// モジュール宣言
		if (!vdmModule.isNotModule) {
			sb.append(vdmModule.definition).append(NEW_LINE).append(NEW_LINE);
		}

		// 状態
		if (!vdmModule.states.isEmpty()) {
			for (State state : vdmModule.states) {
				if (state.getError() == 0) {
//					sb.append(state.definition).append(NEW_LINE).append(NEW_LINE);
					sb.append(state.defStart).append(NEW_LINE);
					for (String content : state.contents) {
						sb.append(content).append(NEW_LINE);
					}
					sb.append(state.defEnd).append(NEW_LINE).append(NEW_LINE);
				}
			}
		}

		// 定数
		if (!vdmModule.values.isEmpty()) {
			if (isOutputableObject(vdmModule.values)) {
				sb.append("values").append(NEW_LINE); //$NON-NLS-1$
			}
			for (Member value : vdmModule.values) {
				if (value.getError() == 0) {
					sb.append(value.definition).append(NEW_LINE).append(NEW_LINE);
				}
			}
		}

		// 関数
		if (!vdmModule.functions.isEmpty()) {
			if (isOutputableObject(vdmModule.functions)) {
				sb.append("functions").append(NEW_LINE); //$NON-NLS-1$
			}
			for (Member function : vdmModule.functions) {
				if (function.getError() == 0) {
					sb.append(function.definition).append(NEW_LINE).append(NEW_LINE);
				}
			}
		}

		// 手続き
		if (!vdmModule.operations.isEmpty()) {
			if (isOutputableObject(vdmModule.operations)) {
				sb.append("operations").append(NEW_LINE); //$NON-NLS-1$
			}
			for (Member operation : vdmModule.operations) {
				if (operation.getError() == 0) {
					sb.append(operation.definition).append(NEW_LINE).append(NEW_LINE);
				}
			}
		}

		// 型
		if (!vdmModule.types.isEmpty()) {
			if (isOutputableObject(vdmModule.types)) {
				sb.append("types").append(NEW_LINE); //$NON-NLS-1$
			}
			for (Member type : vdmModule.types) {
				if (type.getError() == 0) {
					sb.append(type.definition).append(NEW_LINE).append(NEW_LINE);
				}
			}
		}

		// クラスend
		if (!vdmModule.isNotModule) {
			sb.append("end ").append(vdmModule.moduleName).append(NEW_LINE); //$NON-NLS-1$
		}

		return sb.toString();
	}

	/**
	 * カレントモジュールの取得
	 * @return カレントモジュール。カレントモジュールが存在しない場合null。
	 */
	private VdmModule getModule(List<VdmModule> moduleList) {

		// 空の場合
		if (moduleList.isEmpty()) {
			VdmModule vdmModule = new VdmModule();
			moduleList.add(vdmModule);
			return vdmModule;
		}

		// 最後のモジュールを返す
		return moduleList.get(moduleList.size() - 1);
	}

	/**
	 * モジュールの追加
	 * @param name モジュール名
	 * @param definition モジュール定義
	 */
	private void addModule(List<VdmModule> moduleList, List<Member> errorList,
			int no, String name, String definition) {

		// モジュール定義を空白・改行で分割
		String[] lines = definition.trim().split("\\s+"); //$NON-NLS-1$

		// モジュール名
		String moduleName = ""; //$NON-NLS-1$

		if (lines.length > 0) {

			// モジュール名（"module" の次の要素"）を取得
			int index = -1;
			for (int i = 0; i < lines.length; i++) {
				if (KEYWORD_MODULE.equals(lines[i])) {
					index = i;
				}
			}
			if (index >= 0 && index < lines.length - 1) {
				moduleName = lines[index + 1];
			}
		}

		List<VdmModule> list = moduleList;

		// モジュールの追加
		VdmModule newModule = new VdmModule(no, moduleName, name, definition);

		// 定義が空白文字のみの場合
		if (definition.matches("\\s*")) { //$NON-NLS-1$
			newModule.setError(E_NO_DEFINITION);
		}

		// クラス名が取得できない、またはファイル名に使用できない
		else if (moduleName.length() == 0 || new Path("").isValidSegment(moduleName) == false) { //$NON-NLS-1$
			newModule.setError(E_WRONG_DEFINITION);
		}

		// クラスの追加
		list.add(newModule);

		// エラーがある場合、エラーリストにも追加する
		if (newModule.getError() > 0) {
			errorList.add(newModule);
		}
	}

	/**
	 * 定数の追加。カレントクラスの定数配列に要素を追加する。
	 * @param name 定数名
	 * @param definition 定数定義
	 */
	private void addValue(List<VdmModule> moduleList, List<Member> errorList, int no, String name, String definition) {
		VdmModule vdmModule = getModule(moduleList);
		List<Member> values = (vdmModule == null)? null : vdmModule.values;
		addMember(values, errorList, no, name, definition, vdmModule);
	}

	/**
	 * 関数の追加。カレントクラスの関数配列に要素を追加する。
	 * @param name 関数名
	 * @param definition 関数定義
	 */
	private void addFunction(List<VdmModule> moduleList, List<Member> errorList, int no, String name, String definition) {
		VdmModule vdmModule = getModule(moduleList);
		List<Member> functions = (vdmModule == null)? null : vdmModule.functions;
		addMember(functions, errorList, no, name, definition, vdmModule);
	}

	/**
	 * 手続きの追加。カレントクラスの手続き配列に要素を追加する。
	 * @param name 手続き名
	 * @param definition 手続き定義
	 */
	private void addOperation(List<VdmModule> moduleList, List<Member> errorList, int no, String name, String definition) {
		VdmModule vdmModule = getModule(moduleList);
		List<Member> operations = (vdmModule == null)? null : vdmModule.operations;
		addMember(operations, errorList, no, name, definition, vdmModule);
	}

	/**
	 * 型の追加。カレントクラスの型配列に要素を追加する。
	 * @param name 型名
	 * @param definition 型定義
	 */
	private void addType(List<VdmModule> moduleList, List<Member> errorList, int no, String name, String definition) {
		VdmModule vdmModule = getModule(moduleList);
		List<Member> types = (vdmModule == null)? null : vdmModule.types;
		addMember(types, errorList, no, name, definition, vdmModule);
	}

	/**
	 * メンバーの追加
	 * @param list
	 * @param no
	 * @param name
	 * @param definition
	 * @return
	 */
	private Member addMember(List<Member> list, List<Member> errorList, int no,
			String name, String definition, VdmModule vdmModule) {

		Member member = new Member(no, name, definition);

		// 定義が空白文字のみの場合
		if (definition.matches("\\s*")) { //$NON-NLS-1$
			member.setError(E_NO_DEFINITION);
		}

		// モジュールの定義が不正の場合
		else if ((vdmModule.getError() & (E_WRONG_DEFINITION | E_MODULE_WRONG)) != 0) {
			member.setError(E_MODULE_WRONG);
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
	 * 状態の追加。カレントクラスの状態配列に要素を追加する。
	 * @param name 状態名
	 * @param definition 状態定義
	 */
	private void addState(List<VdmModule> moduleList, List<Member> errorList, int no, String name, String definition) {
		VdmModule vdmModule = getModule(moduleList);
		List<State> states = (vdmModule == null)? null : vdmModule.states;

		State state = new State(no, name, definition);

		// 定義が空白文字のみの場合
		if (definition.matches("\\s*")) { //$NON-NLS-1$
			state.setError(E_NO_DEFINITION);
		}

		// モジュールの定義が不正の場合
		else if ((vdmModule.getError() & (E_WRONG_DEFINITION | E_MODULE_WRONG)) != 0) {
			state.setError(E_MODULE_WRONG);
		}

//		// 状態の定義が不正の場合（state xxx of にマッチしない場合）
//		else if (!Pattern.compile(REGEX_STATE_OF, Pattern.MULTILINE).matcher(definition).find()) {
//			state.setError(E_STATE_WRONG);
//		}
		// 定義の内容を解析
		List<String> contents = new ArrayList<String>();
		if (state.getError() <= 0) {
			String[] lines = PluginHelper.splitByLine(definition);
			boolean isStart = false;
			boolean isEnd = false;
			Pattern ptnStart = Pattern.compile(REGEX_STATE_OF);
			Pattern ptnEnd = Pattern.compile(REGEX_STATE_END);
			for (String line : lines) {
				if (!isStart) {
					// state xxx of の行の場合
					if (ptnStart.matcher(line).matches()) {
						state.defStart = line;
						isStart = true;
					}
				} else if (!isEnd) {
					// end; の行の場合
					if (ptnEnd.matcher(line).matches()) {
						state.defEnd = line;
						isEnd = true;
						break;
					} else {
						// どちらでもない場合、state定義の本文
						contents.add(line);
					}
				}
			}
			// "state xxx of","end;"が含まれない場合エラー
			if (!isStart || !isEnd) {
				contents.clear();
				state.setError(E_STATE_WRONG);
			}
		}

		// エラーがある場合、エラーリストに追加
		if (state.getError() > 0) {
			errorList.add(state);
		}

		// 状態名（state ABC of のABCの部分）の取出し
		String stateVar = ""; //$NON-NLS-1$
		String[] arr = definition.split("\\s"); //$NON-NLS-1$
		for (int i=0; i<arr.length - 1; i++) {
			if ("state".equals(arr[i])) { //$NON-NLS-1$
				stateVar = arr[i + 1];
				break;
			}
		}

		// 同じ状態名のエントリがないかチェック
		for (State st : states) {
			// 同じものがあればそちらに追加する
			if (st.var.equals(stateVar)) {
				st.contents.addAll(contents);
				return;
			}
		}

		// 同じものがない場合、リストに追加
		state.var = stateVar;
		state.contents.addAll(contents);
		if (states != null) {
			states.add(state);
		}
	}

	/**
	 * 内部クラス - VDM-SLのモジュールを表すクラス
	 */
	private class VdmModule extends Member {

		/** モジュール名 */
		String moduleName;

		/** モジュールなしフラグ */
		boolean isNotModule;

		/** 定数 */
		List<Member> values;
		/** 関数 */
		List<Member> functions;
		/** 手続き */
		List<Member> operations;
		/** 型 */
		List<Member> types;
		/** 状態 */
		List<State> states;

		/**
		 * コンストラクタ
		 * @param name クラス名
		 * @param definition クラス定義
		 */
		public VdmModule(int no, String moduleName, String name, String definition) {
			super(no, name, definition);
			this.moduleName = moduleName;
			values = new ArrayList<Member>();
			functions = new ArrayList<Member>();
			operations = new ArrayList<Member>();
			types = new ArrayList<Member>();
			states = new ArrayList<State>();
			isNotModule = false;
		}

		public VdmModule() {
			super(0, "", ""); //$NON-NLS-1$ //$NON-NLS-2$
			this.moduleName = "Default"; //$NON-NLS-1$
			values = new ArrayList<Member>();
			functions = new ArrayList<Member>();
			operations = new ArrayList<Member>();
			types = new ArrayList<Member>();
			states = new ArrayList<State>();
			isNotModule = true;
		}

		public VdmModule setError(int error) {
			super.setError(error);
			return this;
		}
	}

	/**
	 * 状態を表すクラス
	 */
	private class State extends Member {

		/** 状態変数 */
		String var;
		/** 定義の内容 */
		List<String> contents = new ArrayList<String>();
		/** 開始 */
		String defStart;
		/** 終了 */
		String defEnd;

		/**
		 * コンストラクタ
		 * @param no
		 * @param name
		 * @param definition
		 */
		public State(int no, String name, String definition) {
			super(no, name, definition);
		}

	}

	/**
	 * メンバを表すクラス
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
		return EXTENSION_VDMSL;
	}
}
