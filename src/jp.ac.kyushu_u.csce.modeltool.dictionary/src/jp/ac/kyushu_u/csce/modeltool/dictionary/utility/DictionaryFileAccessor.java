package jp.ac.kyushu_u.csce.modeltool.dictionary.utility;

import static jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryXMLConstants.*;

import java.io.File;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jp.ac.kyushu_u.csce.modeltool.base.utility.FileAccessException;
import jp.ac.kyushu_u.csce.modeltool.base.utility.XMLNode;
import jp.ac.kyushu_u.csce.modeltool.dictionary.Messages;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Dictionary;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryClass;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionarySetting;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.ModelManager;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;
import org.w3c.dom.Document;

/**
 * 辞書ファイル生成クラス
 *
 * @author KBK yoshimura
 */
public class DictionaryFileAccessor {

	/**
	 * ファイルを読み込む（旧バージョン互換）<br>
	 * 指定ファイルを読み込み、辞書データに変換する。<br>
	 * インポート時は org.eclipse.core.resources.IFile ではなく java.io.File が引数となる<br>
	 * 対象バージョン 1.x.x.x以下。バージョン2.0以上の辞書ファイルには使用しない。
	 * @param file ファイル
	 * @return 読込結果
	 * @throws FileAccessException
	 */
	public static void importXmlFile(File file, Dictionary dictionary) throws FileAccessException {

		Document document = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = null;
			docBuilder = dbFactory.newDocumentBuilder();
			document = docBuilder.parse(file);
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
			throw new FileAccessException(Messages.DictionaryFileAccessor_4);
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
			if (StringUtils.isNotBlank(ndClass.n(TAG_LANGUAGE).getTextContent())) {
				// 旧バージョンでは直接入力で「日本語」などの値が入っている可能性があるため
				// 入力言語が設定されている場合も実行環境の言語コードをセットする
//				dicClass.languages.add(ndClass.n(TAG_LANGUAGE).getTextContent());
				dicClass.languages.add(Locale.getDefault().getLanguage());
			} else {
				// 入力言語未設定の場合は実行環境の言語コードをセットする
				dicClass.languages.add(Locale.getDefault().getLanguage());
			}
			// 出力モデル
			if (StringUtils.isNotBlank(ndClass.n(TAG_MODEL).getTextContent())) {
				// 旧バージョンでは設定されていないので常にデフォルトモデルを使用する
//				dicClass.model = ndClass.n(TAG_MODEL).getTextContent();
				dicClass.model = ModelManager.DEFAULT_MODEL_KEY;
			} else {
				// 未指定の場合デフォルトモデルをセット
				dicClass.model = ModelManager.DEFAULT_MODEL_KEY;
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
				int categoryCount = ndCategories.count(TAG_CATEGORY_S);
				for (int i=0; i<categoryCount; i++) {
					XMLNode ndCategory = ndCategories.n(TAG_CATEGORY_S, i);
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
			Entry dicEntry = new Entry();
			XMLNode ndEntry = ndEntries.n(TAG_ENTRY, i);

			// 検査用行番号
			XMLNode ndSeqNo = ndEntry.n(TAG_SEQNO);
			dicEntry.setSeqNo(Integer.MAX_VALUE);
			if (ndSeqNo != null) {
				String strSeqNo = ndEntry.n(TAG_SEQNO).getTextContent();
				if (strSeqNo != null && strSeqNo.matches("[0-9]+")) { //$NON-NLS-1$
					dicEntry.setSeqNo(Integer.parseInt(strSeqNo));
				}
			}

			// 出力用行番号
			XMLNode ndOutNo = ndEntry.n(TAG_OUTNO);
			dicEntry.setOutNo(Integer.MAX_VALUE);
			if (ndOutNo != null) {
				String strOutNo = ndEntry.n(TAG_OUTNO).getTextContent();
				if (strOutNo != null && strOutNo.matches("[0-9]+")) { //$NON-NLS-1$
					dicEntry.setOutNo(Integer.parseInt(strOutNo));
				}
			}

			// 見出し語
			dicEntry.setWord(ndEntry.n(TAG_WORD).getTextContent());

			// 種別
			String categoryNo = ndEntry.n(TAG_CATEGORY).getTextContent();
			// TODO:種別コードの判定（暫定的に数値チェックのみ行う）
			if (categoryNo.matches("[0-9]+")) { //$NON-NLS-1$
				dicEntry.setCategoryNo(Integer.parseInt(categoryNo));
			} else {
				dicEntry.setCategoryNo(0);
			}

			// 非形式的定義
			dicEntry.getInformals().add((ndEntry.n(TAG_INFORMAL).getTextContent()));

			// 形式的種別
			XMLNode ndSection = ndEntry.n(TAG_SECTION);
			dicEntry.setSection(0);
			if (ndSection != null) {
				String strSection = ndSection.getTextContent();
				if (strSection.matches("[0-9]+")) { //$NON-NLS-1$
					dicEntry.setSection(Integer.parseInt(strSection));
				}
			}

			// 形式的定義
			dicEntry.setFormal(ndEntry.n(TAG_FORMAL).getTextContent());

			// 型
			dicEntry.setType(ndEntry.n(TAG_TYPE).getTextContent());

			dictionary.add(dicEntry);
		}
	}

}
