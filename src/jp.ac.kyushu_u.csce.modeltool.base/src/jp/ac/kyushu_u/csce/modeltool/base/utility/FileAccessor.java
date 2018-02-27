package jp.ac.kyushu_u.csce.modeltool.base.utility;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import jp.ac.kyushu_u.csce.modeltool.base.constant.ToolConstants;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;

/**
 * ファイル生成クラス
 *
 * @author KBK yoshimura
 */
public class FileAccessor {

	/** 文字コード */
	private static final String ENCODING = ToolConstants.ENCODING_UTF_8;

	/**
	 * 新規ファイルを作成する（空ファイル）
	 * 同名のファイルがすでに存在する場合、nullを返す
	 * @param container フォルダー
	 * @param fileName ファイル名
	 * @return 作成したファイルのハンドル
	 * @throws FileAccessException
	 */
	public static IFile createFile(IContainer container, String fileName) throws FileAccessException {

		return createFile(container, fileName, ""); //$NON-NLS-1$
	}

	/**
	 * 新規ファイルを作成する
	 * 同名のファイルがすでに存在する場合、nullを返す
	 * @param container フォルダー
	 * @param fileName ファイル名
	 * @param string ファイルに書き込む文字列
	 * @return 作成したファイルのハンドル
	 * @throws FileAccessException
	 */
	public static IFile createFile(IContainer container, String fileName, String string) throws FileAccessException {

		return createFile(PluginHelper.getFile(container, fileName), string);
	}

	/**
	 * 新規ファイルを作成する<br>
	 * 同名のファイルがすでに存在する場合、nullを返す
	 * @param file ファイル
	 * @param string ファイルに書き込む文字列
	 * @return 作成したファイルのハンドル
	 * @throws FileAccessException
	 */
	public static IFile createFile(IFile file, String string) throws FileAccessException {

		// 指定ファイルが存在する場合（ローカルではなくEFS上でのチェック）
		if (file.exists()) {
			return null;

		// 存在しない場合、作成する
		} else {
			try {
				file.create(new ByteArrayInputStream(string.getBytes(ENCODING)), false, null);
				file.setCharset(ENCODING, null);
			} catch (Exception e) {
				throw new FileAccessException(e);
			}
		}

		return file;
	}

	/**
	 * 既存ファイルを更新する
	 * @param container
	 * @param fileName
	 * @param string
	 * @return 更新ファイルのインスタンス（失敗の場合、nullを返す）
	 * @throws FileAccessException
	 */
	public static IFile updateFile(IContainer container, String fileName, String string) throws FileAccessException {

		return updateFile(PluginHelper.getFile(container, fileName), string);
	}

	/**
	 * 既存ファイルを更新する
	 * @param file 更新対象ファイル
	 * @param string ファイルに書き込む文字列
	 * @return 更新ファイルのインスタンス（失敗の場合、nullを返す）
	 * @throws FileAccessException
	 */
	public static IFile updateFile(IFile file, String string) throws FileAccessException {

		try {
			file.setContents(new ByteArrayInputStream(string.getBytes(ENCODING)), true, false, null);
			file.setCharset(ENCODING, null);
		} catch (Exception e) {
			throw new FileAccessException(e);
		}

		return file;
	}


	/**
	 * ファイルの新規作成／更新を行う<br>
	 * ファイルが存在する場合は更新、存在しない場合新規に作成する。
	 * @param container
	 * @param fileName
	 * @param string
	 * @return 更新ファイルのインスタンス（失敗の場合、nullを返す）
	 * @throws FileAccessException
	 */
	public static IFile writeFile(IContainer container, String fileName, String string) throws FileAccessException {

		return writeFile(PluginHelper.getFile(container, fileName), string);
	}

	/**
	 * ファイルの新規作成／更新を行う<br>
	 * ファイルが存在する場合は更新、存在しない場合新規に作成する。
	 * @param file
	 * @param string
	 * @return 更新ファイルのインスタンス（失敗の場合、nullを返す）
	 * @throws FileAccessException
	 */
	public static IFile writeFile(IFile file, String string) throws FileAccessException {

		if (file.exists()) {
			return updateFile(file, string);
		} else {
			return createFile(file, string);
		}
	}

	/**
	 * ファイルを読み込む<br>
	 * 指定ファイルを読み込み、文字列配列に変換する。<br>
	 * 配列の1要素＝ファイルの1行となる。
	 * @param container フォルダー
	 * @param fileName ファイル名
	 * @return 読込結果
	 * @throws FileAccessException
	 */
	public static String[] readFile(IContainer container, String fileName) throws FileAccessException {

		return readFile(PluginHelper.getFile(container, fileName));
	}

	/**
	 * ファイルを読み込む<br>
	 * 指定ファイルを読み込み、文字列配列に変換する。<br>
	 * 配列の1要素＝ファイルの1行となる。
	 * @param file ファイル
	 * @return 読込結果
	 * @throws FileAccessException
	 */
	public static String[] readFile(IFile file) throws FileAccessException {

		try {
			InputStream stream = file.getContents();
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, ENCODING));

			List<String> list = new ArrayList<String>();
			String line = null;

			// 1行ずつ読込む
			while ((line = reader.readLine()) != null) {
				list.add(line);
			}

			String[] texts = list.toArray(new String[]{});

			return texts;

		} catch (Exception e) {

			throw new FileAccessException(e);
		}
	}
}
