package jp.ac.kyushu_u.csce.modeltool.dictionary.utility;

import static jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryJddConstants.*;

import java.io.File;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Iterator;

import jp.ac.kyushu_u.csce.modeltool.base.utility.FileAccessException;
import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;
import jp.ac.kyushu_u.csce.modeltool.dictionary.Messages;
import jp.ac.kyushu_u.csce.modeltool.dictionary.ModelToolDictionaryPlugin;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Dictionary;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionarySetting;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.ModelManager;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 辞書ファイルアクセスクラス（JSONファイル）
 *
 * @author KBK yoshimura
 */
public class DictionaryJsonFileAccessor {

	/**
	 * JSONファイルのインポート
	 * @param file
	 * @param dictionary
	 * @return
	 * @throws FileAccessException
	 */
	public static Dictionary importJsonFile(File file, Dictionary dictionary) throws FileAccessException {

		ObjectMapper om = new ObjectMapper();
		JsonNode root = null;
		try {
			root = om.readTree(file);
		} catch (Exception e) {
			throw new FileAccessException(Messages.DictionaryJsonFileAccessor_0, e);
		}

		dictionary.clear();

		// dictionary
		JsonNode jnDictionary = getJsonNode(root, TAG_DICTIONARY, true);

		// class
		JsonNode jnClass = getJsonNode(jnDictionary, TAG_CLASS, true);
		// domain
		dictionary.getDictionaryClass().domain = getJsonNodeText(jnClass, TAG_DOMAIN, true);
		// project
		dictionary.getDictionaryClass().project = getJsonNodeText(jnClass, TAG_PROJECT, true);
		// languages
		for (Iterator<JsonNode> itr = getJsonNode(jnClass, TAG_LANGUAGES, true).elements(); itr.hasNext(); ) {
			dictionary.getDictionaryClass().languages.add(itr.next().textValue());
		}
		// model
		dictionary.getDictionaryClass().model = getJsonNodeText(jnClass, TAG_MODEL, true);
		if (PluginHelper.isEmpty(dictionary.getDictionaryClass().model)) {
			// 未設定の場合デフォルトモデルを使用する
			dictionary.getDictionaryClass().model = ModelManager.DEFAULT_MODEL_KEY;
		}

		// setting
		dictionary.getSetting().setDefaultCategory(true);
		if (jnDictionary.has(TAG_SETTING) && jnDictionary.get(TAG_SETTING).has(TAG_CATEGORIES)) {
			// category
			JsonNode jnCategories = jnDictionary.get(TAG_SETTING).get(TAG_CATEGORIES);
			for (Iterator<JsonNode> itr = jnCategories.elements(); itr.hasNext(); ) {
				JsonNode jnCategory = itr.next();
				// no
				int no = getJsonNodeInt(jnCategory, TAG_NO, true);
				// name
				String name = getJsonNodeText(jnCategory, TAG_NAME, true);
				// primary
				RGB primary = StringConverter.asRGB(getJsonNodeText(jnCategory, TAG_PRIMARY, true));
				// secondary
				RGB secondary = StringConverter.asRGB(getJsonNodeText(jnCategory, TAG_SECONDARY, true));
				dictionary.getSetting().setCategory(no, name, primary, secondary);
			}
			dictionary.getSetting().setDefaultCategory(false);
		}

		// entries
		JsonNode jnEntries = getJsonNode(jnDictionary, TAG_ENTRIES, true);
		for (Iterator<JsonNode> itr = jnEntries.elements(); itr.hasNext(); ) {
			JsonNode jnEntry = itr.next();
			Entry entry = new Entry();
			// seqno
			entry.setSeqNo(getJsonNodeInt(jnEntry, TAG_SEQ_NO, true));
			// outno
			entry.setOutNo(getJsonNodeInt(jnEntry, TAG_OUTPUT_NO, true));
			// word
			JsonNode jnWord = getJsonNode(jnEntry, TAG_KEYWORD, true);
			entry.setWord(getJsonNodeText(jnWord, TAG_TEXT, true));
			// conjugation
			if (jnWord.has(TAG_CONJUGATIONS)) {
				for (Iterator<JsonNode> itr2 = jnWord.get(TAG_CONJUGATIONS).elements(); itr2.hasNext(); ) {
					entry.getConjugations().add(itr2.next().textValue());
				}
			}
			// subword
			if (jnEntry.has(TAG_SUB_KEYWORDS)) {
				for (Iterator<JsonNode> itr2 = jnEntry.get(TAG_SUB_KEYWORDS).elements(); itr2.hasNext(); ) {
					entry.getSubwords().add(itr2.next().textValue());
				}
			}
			// category
			entry.setCategoryNo(getJsonNodeInt(jnEntry, TAG_CATEGORY, true));
			// informal
			for (Iterator<JsonNode> itr2 = getJsonNode(jnEntry, TAG_INFORMALS, true).elements(); itr2.hasNext(); ) {
				entry.getInformals().add(itr2.next().textValue());
			}
			// section
			entry.setSection(getJsonNodeInt(jnEntry, TAG_SECTION, true));
			// formal
			entry.setFormal(getJsonNodeText(jnEntry, TAG_FORMAL, true));
//			// type
//			entry.setType(getJsonNodeText(jnEntry, TAG_TYPE, false));

			dictionary.getEntries().add(entry);
		}

		return dictionary;
	}

	/**
	 * JSONノードの取得
	 * @param parent
	 * @param key
	 * @param isRequired
	 * @return
	 * @throws FileAccessException
	 */
	private static JsonNode getJsonNode(JsonNode parent, String key, boolean isRequired) throws FileAccessException {
		if (!parent.has(key)) {
			if (isRequired) {
				throw new FileAccessException(MessageFormat.format(Messages.DictionaryJsonFileAccessor_1, key));
			} else {
				return null;
			}
		}
		JsonNode node = parent.get(key);
		return node;
	}

	/**
	 * JSONノード値(文字列)の取得
	 * @param parent
	 * @param key
	 * @param isRequired
	 * @return
	 * @throws FileAccessException
	 */
	private static String getJsonNodeText(JsonNode parent, String key, boolean isRequired) throws FileAccessException {
		JsonNode node = getJsonNode(parent, key, isRequired);
		if (node == null) {
			return ""; //$NON-NLS-1$
		} else {
			return node.textValue();
		}
	}

	/**
	 * JSONノード値(整数)の取得
	 * @param parent
	 * @param key
	 * @param isRequired
	 * @return
	 * @throws FileAccessException
	 */
	private static int getJsonNodeInt(JsonNode parent, String key, boolean isRequired) throws FileAccessException {
		JsonNode node = getJsonNode(parent, key, isRequired);
		if (node == null) {
			return 0;
		} else {
			return node.intValue();
		}
	}

	/**
	 * JSONファイルのエクスポート
	 * @param file
	 * @param dictionary
	 * @throws FileAccessException
	 */
	public static void exportJsonFile(File file, Dictionary dictionary) throws FileAccessException {

		ObjectMapper om = new ObjectMapper();
		om.enable(SerializationFeature.INDENT_OUTPUT);

		ObjectNode root = om.createObjectNode();

		// dictionary
		ObjectNode onDictionary = root.putObject(TAG_DICTIONARY);
		onDictionary.put(ATTR_VERSION, ModelToolDictionaryPlugin.getDefault().getBundle().getVersion().toString());
		onDictionary.put(ATTR_DATE, Calendar.getInstance().getTime().toString());

		// class
		ObjectNode onClass = onDictionary.putObject(TAG_CLASS);
		// domain
		onClass.put(TAG_DOMAIN, dictionary.getDictionaryClass().domain);
		// project
		onClass.put(TAG_PROJECT, dictionary.getDictionaryClass().project);
		// language
		ArrayNode onLanguages = onClass.putArray(TAG_LANGUAGES);
		for (String language : dictionary.getDictionaryClass().languages) {
			onLanguages.add(language);
		}
		// model
		onClass.put(TAG_MODEL, dictionary.getDictionaryClass().model);

		// setting
		if (!dictionary.getSetting().isDefaultCategory()) {
			ObjectNode onSetting = onDictionary.putObject(TAG_SETTING);
			ArrayNode onCategories = onSetting.putArray(TAG_CATEGORIES);
//			for (int i=0; i<=DictionarySetting.CATEGORY_COUNT; i++) {
			for (int i=0; i<=DictionaryConstants.MAX_CATEGORY_NO; i++) {
				DictionarySetting.Category category = dictionary.getSetting().getCategory(i);
				if (category == null) {
					continue;
				}
				ObjectNode onCategory = om.createObjectNode();
				onCategory.put(TAG_NO, category.getNo());
				onCategory.put(TAG_NAME, category.getName());
				onCategory.put(TAG_PRIMARY, StringConverter.asString(category.getPrimary()));
				onCategory.put(TAG_SECONDARY, StringConverter.asString(category.getSecondary()));
				onCategories.add(onCategory);
			}
		}

		// entries
		ArrayNode onEntries = onDictionary.putArray(TAG_ENTRIES);
		for (Entry entry : dictionary.getEntries()) {
			ObjectNode onEntry = om.createObjectNode();
			// seqNo
			onEntry.put(TAG_SEQ_NO, entry.getSeqNo());
			// outNo
			onEntry.put(TAG_OUTPUT_NO, entry.getOutNo());
			// word
			ObjectNode onWord = onEntry.putObject(TAG_KEYWORD);
			onWord.put(TAG_TEXT, entry.getWord());
			// conjugation
			ArrayNode onConjugations = onWord.putArray(TAG_CONJUGATIONS);
			for (String conjugation : entry.getConjugations()) {
				if (StringUtils.isNotBlank(conjugation)) {
					onConjugations.add(conjugation);
				}
			}
			// subword
			ArrayNode onSubwords = onEntry.putArray(TAG_SUB_KEYWORDS);
			for (String subword : entry.getSubwords()) {
				if (StringUtils.isNotBlank(subword)) {
					onSubwords.add(subword);
				}
			}
			// category
			if (dictionary.getSetting().containsCategory(entry.getCategoryNo())) {
				onEntry.put(TAG_CATEGORY, entry.getCategoryNo());
			} else {
				onEntry.put(TAG_CATEGORY, 0);
			}
			// informal
			ArrayNode onInformals = onEntry.putArray(TAG_INFORMALS);
			for (int i=0; i<dictionary.getDictionaryClass().languages.size(); i++) {
				if (i < entry.getInformals().size()) {
					onInformals.add(entry.getInformals().get(i));
				} else {
					onInformals.add(""); //$NON-NLS-1$
				}
			}
			// section
			onEntry.put(TAG_SECTION, entry.getSection());
			// formal
			onEntry.put(TAG_FORMAL, entry.getFormal());
//			// type
//			onEntry.put(TAG_TYPE, entry.getType());
			onEntries.add(onEntry);
		}

		try {
			om.writeValue(file, root);
		} catch (Exception e) {
			throw new FileAccessException(Messages.DictionaryJsonFileAccessor_2, e);
		}
	}

}
