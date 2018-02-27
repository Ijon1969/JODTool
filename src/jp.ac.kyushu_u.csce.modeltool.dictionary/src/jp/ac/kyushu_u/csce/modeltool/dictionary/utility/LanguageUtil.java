package jp.ac.kyushu_u.csce.modeltool.dictionary.utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import jp.ac.kyushu_u.csce.modeltool.dictionary.Messages;
import jp.ac.kyushu_u.csce.modeltool.dictionary.ModelToolDictionaryPlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class LanguageUtil {

	/** インスタンス */
	private static LanguageUtil instance;

	private static final String PROP_FILE_NAME = "languages"; //$NON-NLS-1$
	private static final String PROP_EXTENSION = ".properties"; //$NON-NLS-1$

	/** 入力言語Map */
	private Map<String, String> languageMap;
	/** 逆引きMap */
	private Map<String, String> reverseMap;

	/** コンストラクタ */
	private LanguageUtil() {
		languageMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
		reverseMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
		// propertiesファイルの読込
		loadProperties();
	}

	/**
	 * インスタンスの取得
	 * @return インスタンス
	 */
	public static LanguageUtil getInstance() {
		if (instance == null) {
			instance = new LanguageUtil();
		}
		return instance;
	}

	/**
	 * propertiesファイル読み込み
	 */
	private void loadProperties() {

		InputStream in = null;
		try {
			// 入力言語をプロパティファイルから読み込む
			ClassLoader cl = getClass().getClassLoader();
			for (String suffix : getNlSuffixes()) {
				String fileName = PROP_FILE_NAME + suffix;
				in = cl.getResourceAsStream(fileName);
				if (in != null) break;
			}
			if (in == null) return;
			Properties prop = new Properties();
			prop.load(in);
			// 取得した内容をMapへコピー
			for (Map.Entry<Object, Object> p : prop.entrySet()) {
				languageMap.put((String)p.getKey(), (String)p.getValue());
				reverseMap.put((String)p.getValue(), (String)p.getKey());
			}
		} catch (IOException e1) {
			IStatus status = new Status(IStatus.ERROR, ModelToolDictionaryPlugin.PLUGIN_ID,
					Messages.LanguageUtil_0, e1);
			ModelToolDictionaryPlugin.getDefault().getLog().log(status);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	/**
	 * 入力言語Mapの取得
	 * @return 入力言語Map
	 */
	public Map<String, String> getLanguageMap() {
		return languageMap;
	}

	/**
	 * 逆引きMapの取得
	 * @return 逆引きMap
	 */
	public Map<String, String> getReverseMap() {
		return reverseMap;
	}

	/**
	 * ロケールの言語からプロパティファイルの接尾辞(拡張子含む)を取得する。<br>
	 * <br>
	 * 例．ロケール "ja_JP" の場合、{"_ja_JP.properties", "_ja.properties", ".properties"} を返却する。<br>
	 * 呼び出し元の {@link #loadProperties()}メソッド内で、languages_ja_JP.properties, languages_ja.properties, languages.properties の順に
	 * プロパティファイルを探し、最初に見つかったファイルをロードする。<br>
	 * 日本語化フラグメントプラグインには languages_ja.properties が存在するため、言語一覧が日本語化される。
	 * また、日本語化プラグインがインストールされない場合は、デフォルトの languages.properties が言語一覧に使用される。
	 *
	 * @return プロパティファイルのサフィックス配列
	 */
	private String[] getNlSuffixes() {
		String[] nlSuffixes = null;
		String nl = Locale.getDefault().toString();
		List<String> result = new ArrayList<String>(4);
		int lastSeparator;
		while (true) {
			result.add('_' + nl + PROP_EXTENSION);
			lastSeparator = nl.lastIndexOf('_');
			if (lastSeparator == -1)
				break;
			nl = nl.substring(0, lastSeparator);
		}
		result.add(PROP_EXTENSION);
		nlSuffixes = result.toArray(new String[result.size()]);
		return nlSuffixes;
	}
}
