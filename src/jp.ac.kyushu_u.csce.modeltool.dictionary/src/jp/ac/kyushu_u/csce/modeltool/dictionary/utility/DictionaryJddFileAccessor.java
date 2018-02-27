package jp.ac.kyushu_u.csce.modeltool.dictionary.utility;

import static jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryJddConstants.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jp.ac.kyushu_u.csce.modeltool.base.constant.ToolConstants;
import jp.ac.kyushu_u.csce.modeltool.base.utility.FileAccessException;
import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;
import jp.ac.kyushu_u.csce.modeltool.base.utility.XMLNode;
import jp.ac.kyushu_u.csce.modeltool.dictionary.Messages;
import jp.ac.kyushu_u.csce.modeltool.dictionary.ModelToolDictionaryPlugin;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Dictionary;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryClass;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionarySetting;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.ModelManager;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 辞書ファイルアクセスクラス（新フォーマット）
 *
 * @author KBK yoshimura
 */
public class DictionaryJddFileAccessor {

	/** 文字コード */
	private static final String ENCODING = ToolConstants.ENCODING_UTF_8;

	/**
	 * ファイルを読み込む<br>
	 * 指定ファイルを読み込み、文字列配列に変換する。<br>
	 * 配列の1要素＝ファイルの1行となる。<br>
	 * 初期にCSV辞書ファイルを使用していた際のメソッドのため現在は使用していません。
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
	 * 配列の1要素＝ファイルの1行となる。<br>
	 * 初期にCSV辞書ファイルを使用していた際のメソッドのため現在は使用していません。
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

	/**
	 * 新規ファイルを作成する
	 * 同名のファイルがすでに存在する場合、nullを返す
	 * @param container フォルダー
	 * @param fileName ファイル名
	 * @param dictionary 辞書
	 * @return 作成したファイルのハンドル
	 * @throws FileAccessException
	 */
	public static IFile createDictionaryFile(IContainer container, String fileName, Dictionary dictionary) throws FileAccessException {
		return createDictionaryFile(PluginHelper.getFile(container, fileName), dictionary);
	}

	/**
	 * 新規ファイルを作成する<br>
	 * 同名のファイルがすでに存在する場合、nullを返す
	 * @param file ファイル
	 * @param dictionary 辞書
	 * @return 作成したファイルのハンドル
	 * @throws FileAccessException
	 */
	public static IFile createDictionaryFile(IFile file, Dictionary dictionary) throws FileAccessException {

		Document document = convertDictionary2XmlDocument(dictionary);

		try {
			file.create(convertXmlDocument2InputStream(document), false, null);
			file.setCharset(ENCODING, null);

		} catch (Exception e) {
			throw new FileAccessException(e);
		}

		return file;
	}

	/**
	 * 既存ファイルを更新する
	 * @param container
	 * @param fileName
	 * @param dictionary
	 * @return 更新ファイルのインスタンス（失敗の場合、nullを返す）
	 * @throws FileAccessException
	 */
	public static IFile updateDictionaryFile(IContainer container, String fileName, Dictionary dictionary) throws FileAccessException {

		return updateDictionaryFile(PluginHelper.getFile(container, fileName), dictionary);
	}

	/**
	 * 既存ファイルを更新する
	 * @param file 更新対象ファイル
	 * @param dictionary 辞書
	 * @return 更新ファイルのインスタンス（失敗の場合、nullを返す）
	 * @throws FileAccessException
	 */
	public static IFile updateDictionaryFile(IFile file, Dictionary dictionary) throws FileAccessException {

		Document document = convertDictionary2XmlDocument(dictionary);

		try {
			file.setContents(convertXmlDocument2InputStream(document), false, false, null);
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
	 * @param dictionary
	 * @return 作成／更新ファイルのインスタンス（失敗の場合、nullを返す）
	 * @throws FileAccessException
	 */
	public static IFile writeFile(IContainer container, String fileName, Dictionary dictionary) throws FileAccessException {

		return writeDictionaryFile(PluginHelper.getFile(container, fileName), dictionary);
	}

	/**
	 * ファイルの新規作成／更新を行う<br>
	 * ファイルが存在する場合は更新、存在しない場合新規に作成する。
	 * @param file
	 * @param dictionary
	 * @return 作成／更新ファイルのインスタンス（失敗の場合、nullを返す）
	 * @throws FileAccessException
	 */
	public static IFile writeDictionaryFile(IFile file, Dictionary dictionary) throws FileAccessException {

		if (file.exists()) {
			return updateDictionaryFile(file, dictionary);
		} else {
			return createDictionaryFile(file, dictionary);
		}
	}

	/**
	 * 辞書をXMLドキュメントに変換する
	 * @param dictionary
	 * @return XMLドキュメント
	 * @throws FileAccessException
	 */
	private static Document convertDictionary2XmlDocument(Dictionary dictionary) throws FileAccessException {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = dbFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
		Document document = docBuilder.newDocument();

		// TODO:StringEscapeUtilsを使用すると、マルチバイト文字までエスケープされる。 ← DOMが勝手にエスケープしてる？

		Element elmRoot = createElement(document, document, TAG_DICTIONARY, null);
		elmRoot.setAttribute(ATTR_VERSION, ModelToolDictionaryPlugin.getDefault().getBundle().getVersion().toString());
		elmRoot.setAttribute(ATTR_DATE, Calendar.getInstance().getTime().toString());

		// 辞書クラス
		DictionaryClass dicClass = dictionary.getDictionaryClass();
		Element elmClass = createElement(document, elmRoot, TAG_CLASS, null);
		// 問題領域
		createElement(document, elmClass, TAG_DOMAIN, dicClass.domain);
		// プロジェクト名
		createElement(document, elmClass, TAG_PROJECT, dicClass.project);
		// 入力言語
		Element elmLanguages = createElement(document, elmClass, TAG_LANGUAGES, null);
		for (int i=0; i<dicClass.languages.size(); i++) {
			String strLang = dicClass.languages.get(i);
			Element elmLanguage = createElement(document, elmLanguages, TAG_LANGUAGE, strLang);
			elmLanguage.setAttribute(ATTR_ID, String.valueOf(i + 1));
		}
		// 出力モデル
		createElement(document, elmClass, TAG_MODEL, dicClass.model);
		// 拡張元辞書
		createElement(document, elmClass, TAG_EXTEND, dicClass.extend);

		// 設定
		DictionarySetting dicSetting = dictionary.getSetting();
		Element elmSetting = createElement(document, elmRoot, TAG_SETTING, null);

		// デフォルトの種別設定を用いない場合
		if (dicSetting.isDefaultCategory() == false) {
			// 種別設定
			Element elmCategories = createElement(document, elmSetting, TAG_CATEGORIES, null);
//			for (int i=0; i<=DictionarySetting.CATEGORY_COUNT; i++) {
			for (int i=0; i<=DictionaryConstants.MAX_CATEGORY_NO; i++) {
				DictionarySetting.Category dicCategory = dicSetting.getCategory(i);
				if (dicCategory == null) {
					continue;
				}
				Element elmCategory = createElement(document, elmCategories, TAG_CATEGORY, null);
				// No
				createElement(document, elmCategory, TAG_NO, String.valueOf(dicCategory.getNo()));
				// 種別名
				createElement(document, elmCategory, TAG_NAME, dicCategory.getName());
				// 初回色
				createElement(document, elmCategory, TAG_PRIMARY, StringConverter.asString(dicCategory.getPrimary()));
				// 2回目以降色
				createElement(document, elmCategory, TAG_SECONDARY, StringConverter.asString(dicCategory.getSecondary()));
			}
		}

		// 見出し語
		Element elmEntries = createElement(document, elmRoot, TAG_ENTRIES, null);
		for (int i=0; i<dictionary.getEntries().size(); i++) {

			Entry entry = dictionary.get(i);

			Element elmEntry = createElement(document, elmEntries, TAG_ENTRY, null);
			elmEntry.setAttribute("id", String.valueOf(i + 1)); //$NON-NLS-1$

			// 検査用行番号
			createElement(document, elmEntry, TAG_SEQ_NO, String.valueOf(entry.getSeqNo()));
			// 出力用行番号
			createElement(document, elmEntry, TAG_OUTPUT_NO, String.valueOf(entry.getOutNo()));
			// 見出し語
			Element elmWord = createElement(document, elmEntry, TAG_KEYWORD, null);
			createElement(document, elmWord, TAG_TEXT, entry.getWord());
			// 活用形
			if (!entry.getConjugations().isEmpty()) {
				Element elmConjugations = createElement(document, elmWord, TAG_CONJUGATIONS, null);
				for (String strConj : entry.getConjugations()) {
					if (StringUtils.isNotBlank(strConj)) {
						createElement(document, elmConjugations, TAG_CONJUGATION, strConj);
					}
				}
			}
			// 副キーワード
			if (!entry.getSubwords().isEmpty()) {
				Element elmSubwords = createElement(document, elmEntry, TAG_SUB_KEYWORDS, null);
				for (String strSub : entry.getSubwords()) {
					if (StringUtils.isNotBlank(strSub)) {
						Element elmSubword = createElement(document, elmSubwords, TAG_SUB_KEYWORD, null);
						createElement(document, elmSubword, TAG_TEXT, strSub);
					}
				}
			}
			// 種別
			if (dicSetting.containsCategory(entry.getCategoryNo())) {
				createElement(document, elmEntry, TAG_CATEGORY, String.valueOf(entry.getCategoryNo()));
			} else {
				createElement(document, elmEntry, TAG_CATEGORY, String.valueOf(0));
			}
			// 非形式的定義
			Element elmInformals = createElement(document, elmEntry, TAG_INFORMALS, null);
			for (int j=0; j<dicClass.languages.size(); j++) {
				String strInf = ""; //$NON-NLS-1$
				if (j < entry.getInformals().size()) {
					strInf = entry.getInformals().get(j);
				}
				Element elmInformal = createElement(document, elmInformals, TAG_INFORMAL, strInf);
				elmInformal.setAttribute(ATTR_LANG_ID, String.valueOf(j));
			}
			// 形式的定義
			createElement(document, elmEntry, TAG_FORMAL, entry.getFormal());
			// 形式的種別
			createElement(document, elmEntry, TAG_SECTION, String.valueOf(entry.getSection()));
//			// 型
//			createElement(document, elmEntry, TAG_TYPE, entry.getType());
			// 拡張元見出し語
			if (!entry.getOverride().isEmpty()) {
				Element elmOverride = createElement(document, elmEntry, TAG_OVERRIDE, null);
				for (String strTarget : entry.getOverride()) {
					if (StringUtils.isNotBlank(strTarget)) {
						createElement(document, elmOverride, TAG_TARGET, strTarget);
					}
				}
			}
		}

		return document;
	}

	/**
	 * XML要素の作成
	 * @param document XMLドキュメント
	 * @param parent 親要素
	 * @param tag タグ
	 * @param text テキスト
	 * @return 作成したXML要素
	 */
	private static Element createElement(Document document, Node parent, String tag, String text) {
		Element element = document.createElement(tag);
		if (StringUtils.isNotEmpty(text)) {
			element.setTextContent(text);
		}
		parent.appendChild(element);
		return element;
	}

	/**
	 * XMLドキュメントをInputStreamに変換する
	 * @param document
	 * @return XMLドキュメント
	 * @throws FileAccessException
	 */
	private static InputStream convertXmlDocument2InputStream(Document document) throws FileAccessException {

		TransformerFactory tfFactory = TransformerFactory.newInstance();
		tfFactory.setAttribute("indent-number", 2); //$NON-NLS-1$

		StringWriter writer = new StringWriter();

		try {
			Transformer transformer = tfFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, ENCODING);
			transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
			transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$

			transformer.transform(new DOMSource(document), new StreamResult(writer));
			writer.flush();

		} catch (Exception e) {
			throw new FileAccessException(e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					throw new FileAccessException(e);
				}
			}
		}

		InputStream input;
		try {
			input = new ByteArrayInputStream(writer.toString().getBytes(ENCODING));
		} catch (UnsupportedEncodingException e) {
			throw new FileAccessException(e);
		}
		return input;
	}

	/**
	 * ファイルを読み込む<br>
	 * 指定ファイルを読み込み、辞書データに変換する。<br>
	 * @param container フォルダー
	 * @param fileName ファイル名
	 * @return 読込結果
	 * @throws FileAccessException
	 */
	public static IFile readDictionaryFile(IContainer container, String fileName, Dictionary dictionary) throws FileAccessException {

		return readDictionaryFile(PluginHelper.getFile(container, fileName), dictionary);
	}

	/**
	 * ファイルを読み込む<br>
	 * 指定ファイルを読み込み、辞書データに変換する。<br>
	 * @param file ファイル
	 * @return 読込結果
	 * @throws FileAccessException
	 */
	public static IFile readDictionaryFile(IFile file, Dictionary dictionary) throws FileAccessException {

		Document document = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = null;
			docBuilder = dbFactory.newDocumentBuilder();
			document = docBuilder.parse(file.getContents());
		} catch (Exception e) {
			throw new FileAccessException(e);
		}

		// 辞書オブジェクトのクリア
		dictionary.clear();

		XMLNode ndRoot = new XMLNode(document).n(TAG_DICTIONARY);
		XMLNode ndEntries = null;
		if (ndRoot != null) {
			ndEntries = ndRoot.n(TAG_ENTRIES);
		}
		if (ndEntries == null) {
			throw new FileAccessException(Messages.DictionaryJddFileAccessor_4);
		}

		// クラス
		XMLNode ndClass = ndRoot.n(TAG_CLASS);
		DictionaryClass dicClass = dictionary.getDictionaryClass();
		if (ndClass != null) {
			// 問題領域
			dicClass.domain = ndClass.n(TAG_DOMAIN).getTextContent();
			// プロジェクト名
			dicClass.project = ndClass.n(TAG_PROJECT).getTextContent();
			// 入力言語
//			dicClass.language = ndClass.n(TAG_LANGUAGE).getTextContent();
			XMLNode ndLanguages = ndClass.n(TAG_LANGUAGES);
			if (ndLanguages != null) {
				int langCnt = ndLanguages.count(TAG_LANGUAGE);
				for (int i=0; i<langCnt; i++) {
					dicClass.languages.add(ndLanguages.n(TAG_LANGUAGE, i).getTextContent());
				}
			}
			// 出力モデル
			dicClass.model = ndClass.n(TAG_MODEL).getTextContent();
			if (PluginHelper.isEmpty(dicClass.model)) {
				// 未設定の場合デフォルトモデルを使用する
				dicClass.model = ModelManager.DEFAULT_MODEL_KEY;
			}
			// 拡張元辞書
			XMLNode ndExtend = ndClass.n(TAG_EXTEND);
			if (ndExtend != null) {
				dicClass.extend = ndExtend.getTextContent();
			}
		} else {
			// 出力モデル
			dicClass.model = ModelManager.DEFAULT_MODEL_KEY;
		}

		// 設定
		XMLNode ndSetting = ndRoot.n(TAG_SETTING);
		DictionarySetting dicSetting = dictionary.getSetting();
		dicSetting.setDefaultCategory(true);
		if (ndSetting != null) {

			// 種別設定
			XMLNode ndCategories = ndSetting.n(TAG_CATEGORIES);
			if (ndCategories != null) {
				int categoryCount = ndCategories.count(TAG_CATEGORY);
				for (int i=0; i<categoryCount; i++) {
					XMLNode ndCategory = ndCategories.n(TAG_CATEGORY, i);
					// 種別No
					int no = Integer.parseInt(ndCategory.n(TAG_NO).getTextContent());
					// 種別名称
					String name = ndCategory.n(TAG_NAME).getTextContent();
					// 初回色
					RGB primary = StringConverter.asRGB(ndCategory.n(TAG_PRIMARY).getTextContent());
					// 2回目以降色
					RGB secondary = StringConverter.asRGB(ndCategory.n(TAG_SECONDARY).getTextContent());

					dicSetting.setCategory(no, name, primary, secondary);
				}
				dicSetting.setDefaultCategory(false);
			}
		}

		for (int i=0; i<ndEntries.count(TAG_ENTRY); i++) {
			Entry entry = new Entry();
			XMLNode ndEntry = ndEntries.n(TAG_ENTRY, i);

			// 検査用行番号
			XMLNode ndSeqNo = ndEntry.n(TAG_SEQ_NO);
			entry.setSeqNo(Integer.MAX_VALUE);
			if (ndSeqNo != null) {
				String strSeqNo = ndEntry.n(TAG_SEQ_NO).getTextContent();
				if (strSeqNo != null && strSeqNo.matches("[0-9]+")) { //$NON-NLS-1$
					entry.setSeqNo(Integer.parseInt(strSeqNo));
				}
			}

			// 出力用行番号
			XMLNode ndOutNo = ndEntry.n(TAG_OUTPUT_NO);
			entry.setOutNo(Integer.MAX_VALUE);
			if (ndOutNo != null) {
				String strOutNo = ndEntry.n(TAG_OUTPUT_NO).getTextContent();
				if (strOutNo != null && strOutNo.matches("[0-9]+")) { //$NON-NLS-1$
					entry.setOutNo(Integer.parseInt(strOutNo));
				}
			}

			// 見出し語
			if (ndEntry.n(TAG_KEYWORD) != null) {
				XMLNode ndWord = ndEntry.n(TAG_KEYWORD);
				XMLNode ndText = ndWord.n(TAG_TEXT);
				if (ndText != null) {
					entry.setWord(ndText.getTextContent());
				}

				// 活用形
				XMLNode ndConjugations = ndWord.n(TAG_CONJUGATIONS);
				if (ndConjugations != null) {
					int conjCnt = ndConjugations.count(TAG_CONJUGATION);
					for (int j=0; j<conjCnt; j++) {
						String conjugation = ndConjugations.n(TAG_CONJUGATION, j).getTextContent();
						entry.getConjugations().add(conjugation);
					}
				}
			}

			// 副キーワード
			if (ndEntry.n(TAG_SUB_KEYWORDS) != null) {
				XMLNode ndSubwords = ndEntry.n(TAG_SUB_KEYWORDS);
				int subCnt = ndSubwords.count(TAG_SUB_KEYWORD);
				for (int j=0; j<subCnt; j++) {
					if (ndSubwords.n(TAG_SUB_KEYWORD, j).n(TAG_TEXT) != null) {
						entry.getSubwords().add(ndSubwords.n(TAG_SUB_KEYWORD, j).n(TAG_TEXT).getTextContent());
					} else {
						entry.getSubwords().add(""); //$NON-NLS-1$
					}
				}
			}

			// 種別
			entry.setCategory(ndEntry.n(TAG_CATEGORY).getTextContent());
			String categoryNo = ndEntry.n(TAG_CATEGORY).getTextContent();
			// TODO:種別コードの判定（暫定的に数値チェックのみ行う）
			if (categoryNo.matches("[0-9]+")) { //$NON-NLS-1$
				entry.setCategoryNo(Integer.parseInt(categoryNo));
			}

			// 非形式的定義
			XMLNode ndInformals = ndEntry.n(TAG_INFORMALS);
			if (ndInformals != null) {
				int infCnt = ndInformals.count(TAG_INFORMAL);
				for (int j=0; j<infCnt; j++) {
					String informal = ndInformals.n(TAG_INFORMAL, j).getTextContent();
					entry.getInformals().add(informal);
				}
			}

			// 形式的種別
			XMLNode ndSection = ndEntry.n(TAG_SECTION);
			entry.setSection(0);
			if (ndSection != null) {
				String strSection = ndSection.getTextContent();
				if (strSection.matches("[0-9]+")) { //$NON-NLS-1$
					entry.setSection(Integer.parseInt(strSection));
				}
			}

			// 形式的定義
			entry.setFormal(ndEntry.n(TAG_FORMAL).getTextContent());

//			// 型
//			entry.setType(ndEntry.n(TAG_TYPE).getTextContent());

			// 拡張元見出し語
			XMLNode ndOverride = ndEntry.n(TAG_OVERRIDE);
			if (ndOverride != null) {
				int targetCnt = ndOverride.count(TAG_TARGET);
				for (int j=0; j<targetCnt; j++) {
					String target = ndOverride.n(TAG_TARGET, j).getTextContent();
					entry.getOverride().add(target);
				}
			}

			dictionary.add(entry);
		}

		return file;
	}
}
