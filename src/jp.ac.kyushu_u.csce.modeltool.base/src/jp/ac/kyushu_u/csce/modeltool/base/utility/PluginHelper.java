package jp.ac.kyushu_u.csce.modeltool.base.utility;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * 要求モデル検証ツール・ヘルパークラス
 *
 * @author KBK yoshimura
 */
public class PluginHelper {

	/** 空文字列 */
	public static final String EMPTY	= ""; //$NON-NLS-1$

	/**
	 * 配列の要素のインデックスを取得する<br>
	 * 指定した要素が配列に存在しない場合は負の値を返す
	 * @param array 配列
	 * @param element 要素
	 * @return インデックス
	 */
	public static int indexOf(Object[] array, Object element) {
		for (int i=0; i<array.length; i++) {
			if (array[i].equals(element)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 文字列がnull、空文字列、半角スペースのみの場合trueを返す
	 * @param input
	 * @return 空チェック結果
	 */
	public static boolean isEmpty(String input) {

		return (input == null || input.replaceAll(" ", "").equals(EMPTY)); //$NON-NLS-1$ //$NON-NLS-2$
	}


	/**
	 * 文字列配列に指定の文字列が存在するかどうかをチェックする<br>
	 * 入力文字列がnullの場合はfalseを返す
	 * @param input 文字列
	 * @param strings 文字列
	 * @return true:存在する／false:存在しない
	 */
	public static boolean in(String input, String... strings) {

		return in(input, true, strings);
	}

	/**
	 * 文字列配列に指定の文字列が存在するかどうかをチェックする<br>
	 * 入力文字列がnullの場合はfalseを返す
	 * @param input 文字列
	 * @param strings 文字列
	 * @param caseSensitive 大／小文字の区別
	 * @return true:存在する／false:存在しない
	 */
	public static boolean in(String input, boolean caseSensitive, String... strings) {

		return in(input, caseSensitive, new String[][] {strings});
	}

	/**
	 * 文字列配列に指定の文字列が存在するかどうかをチェックする<br>
	 * 入力文字列がnullの場合はfalseを返す
	 * @param input 文字列
	 * @param arrays 配列
	 * @return true:存在する／false:存在しない
	 */
	public static boolean in(String input, String[]... arrays) {

		return in(input, true, arrays);
	}

	/**
	 * 文字列配列に指定の文字列が存在するかどうかをチェックする<br>
	 * 入力文字列がnullの場合はfalseを返す
	 * @param input 文字列
	 * @param arrays 配列
	 * @param caseSensitive 大／小文字の区別
	 * @return true:存在する／false:存在しない
	 */
	public static boolean in(String input, boolean caseSensitive, String[]... arrays) {

		if (input == null) {
			return false;
		}

		for (String[] array : arrays) {
			for (String string : array) {
				if ((caseSensitive && string.equals(input)) ||		// caseSensitive = trueの場合、大／小文字区別あり
						(!caseSensitive && string.equalsIgnoreCase(input))) {	   // falseの場合、大／小文字区別なし
					return true;
				}
			}
		}

		return false;
	}

//	/**
//	 * 配列を結合する（配列１＋配列２）
//	 * @param cls 配列要素のクラス
//	 * @param array1 配列１
//	 * @param array2 配列２
//	 * @return 結合後の配列
//	 */
//	public static Object[] arrayConcat(Class<?> cls, Object[] array1, Object[] array2) {
//
//		Object[] ret = (Object[])Array.newInstance(cls, array1.length + array2.length);
//
//		System.arraycopy(array1, 0, ret, 0, array1.length);
//		System.arraycopy(array2, 0, ret, array1.length, array2.length);
//
//		return ret;
//	}

	/**
	 * 配列を結合する
	 * @param arrays 配列
	 * @return 結合後の配列
	 */
	public static Object[] arrayConcat(Class<?> cls, Object[]... arrays) {

		List<Object> list = new ArrayList<Object>();
		for (Object[] array : arrays) {
			list.addAll(Arrays.asList(array));
		}

		return list.toArray((Object[])Array.newInstance(cls, 0));
	}

	/**
	 * 指定したリソースのワークスペースからの相対パスを取得する。
	 * @param resource リソース
	 * @return 相対パス文字列
	 */
	public static String getRelativePath(IResource resource) {

		String path = resource.getFullPath().toString();
//		if (path.charAt(0) == '/') {
//			path = path.substring(1);
//		}
		return path;
	}

	/**
	 * 指定したりソースの絶対パスを取得する。
	 * @param resource リソース
	 * @return 絶対パス文字列
	 */
	public static String getAbsolutePath(IResource resource) {
		return resource.getLocation().toString();
	}

	/**
	 * 拡張子なしのファイル名を取得する。
	 * @param file ファイル
	 * @return 拡張子なしのファイル名
	 */
	public static String getFileNameWithoutExtension(IFile file) {

		// 拡張子付ファイル名、拡張子の取得
		String name = file.getName();
		String extension = file.getFileExtension();

		// 拡張子がない場合
		if (isEmpty(extension)) {
			return name;
		}

		// 拡張子を除去したファイル名を返す（'.'を除去するため-1しています）
		return name.substring(0, name.length() - extension.length() - 1);
	}

	/**
	 * 文字列中に改行を含むかチェックする
	 * @param object
	 * @return 改行を含む場合trueを返す
	 */
	public static boolean isMultiLine(String object) {

		boolean ret = false;

		if (object != null) {
//			ret = object.split("[\r\n|\r|\n]").length != 1;
//			ret = object.matches(".*[\r|\n].*");
//			ret = object.length() != object.replaceFirst("\r\n|\r|\n", "").length();
			ret = object.indexOf('\n') >= 0 || object.indexOf('\r') >= 0;
		}

		return ret;
	}

	/**
	 * コンテナハンドル、ファイル名からファイルハンドルを取得する
	 * @param container コンテナ
	 * @param fileName ファイル名
	 * @return ファイル
	 */
	public static IFile getFile(IContainer container, String fileName) {

		IPath containerPath = container.getFullPath();
		IPath newFilePath = containerPath.append(fileName);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(newFilePath);

		return file;
	}

	/**
	 *
	 * @param path ルートからの相対パス
	 * @return 指定パスのファイルハンドル
	 */
	public static IFile getFile(IPath path) {
		return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
	}

	/**
	 * 空、ホワイトスペース、nullの場合defaultStrを返す
	 * @param str 変換前文字列
	 * @param defaultStr 変換後文字列
	 * @return 変換結果
	 */
	public static String defaultIfBlank(String str, String defaultStr) {
		if (StringUtils.isBlank(str)) {
			return defaultStr;
		} else {
			return str;
		}
	}

//	/**
//	 *
//	 * @param location ローカルファイルシステム上の絶対パス
//	 * @return
//	 */
//	public static IFile getFileForLocation(IPath location) {
//		return ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(location);
//	}
//
//	/**
//	 *
//	 * @param location ローカルファイルシステム上の絶対パス
//	 * @return
//	 */
//	public static IContainer getContainerForLocation(IPath location) {
//		return ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(location);
//	}

	/**
	 * 文字列を改行コードで分割する
	 * @param str 対象文字列
	 * @return 分割結果の配列
	 */
	public static String[] splitByLine(String str) {
		if (StringUtils.isBlank(str)) return new String[]{};
		return str.replaceAll("\r\n", "\r").split("\r|\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * 数値がnullの場合0を返す
	 * @param num 数値
	 * @return 結果
	 */
	public static int nullToZero(Integer num) {
		if (num == null) return 0;
		return num.intValue();
	}

	/**
	 * フォルダの取得
	 * @param relativePath ルートからの相対パス
	 * @return フォルダ。指定パスがフォルダ（またはプロジェクト）でない場合nullを返す。
	 */
	public static IContainer getFolder(String relativePath) {

		// ワークスペースルートの取得
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		// フォルダの取得
		IContainer container = null;
		try {
			// 指定パスをフォルダとして取得
			container = root.getFolder(new Path(relativePath));
		} catch (Exception e) {
			// 指定パスがプロジェクトの場合、例外となるのでここでキャッチ
			try {
				// 指定パスをプロジェクトとして取得
				container = root.getProject(relativePath);
			} catch (Exception e2) {
				// ignore
			}
		}

		// フォルダが存在しない場合、nullを返す
		if (container != null) {
			if (!container.getLocation().toFile().exists()) {
				container = null;
			}
		}

		return container;
	}
}
