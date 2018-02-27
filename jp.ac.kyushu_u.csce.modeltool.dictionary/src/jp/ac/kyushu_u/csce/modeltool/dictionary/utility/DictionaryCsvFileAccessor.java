package jp.ac.kyushu_u.csce.modeltool.dictionary.utility;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import jp.ac.kyushu_u.csce.modeltool.base.utility.ColorName;
import jp.ac.kyushu_u.csce.modeltool.base.utility.FileAccessException;
import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;
import jp.ac.kyushu_u.csce.modeltool.dictionary.Messages;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Dictionary;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionarySetting;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionarySetting.Category;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.Entry;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.Model.Section;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.ModelManager;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.graphics.RGB;

import com.orangesignal.csv.Csv;
import com.orangesignal.csv.CsvConfig;
import com.orangesignal.csv.handlers.StringArrayListHandler;

/**
 * 辞書ファイルアクセスクラス（CSVファイル）<br>
 * <br>
 * CSVフォーマットのルール<br>
 * ・CSVフォーマットのバージョン番号はプラグインのバージョンとは別に管理する。<br>
 * ・CSVフォーマットのバージョンが上がっても、以下のルールは変わらないものとする。<br>
 *  　ヘッダー開始行	#header<br>
 *  　ヘッダー1行目	version<br>
 *  　データ開始行 	#data<br>
 *  　データ1行目 	データ部のタイトル行<br>
 * </pre>
 *
 * @author KBK yoshimura
 */
public class DictionaryCsvFileAccessor {

	/**
	 * CSV設定
	 */
	private static final CsvConfig CSV_CONFIG;
	static {
		CSV_CONFIG = new CsvConfig(',', '"', '"');
		CSV_CONFIG.setIgnoreEmptyLines(true);	// 空行を無視
	}

	/** バージョン番号 1.0 */
	private static final String VERSION_1_0 = "1.0"; //$NON-NLS-1$	// v1.0 2015/02/16 release

	/**
	 * CSVフォーマット・バージョンのリスト<br>
	 * CSVのフォーマットを変更した場合、リストにバージョンを追加する。
	 */
	private static final List<String> VERSION_LIST;
	static {
		VERSION_LIST = new ArrayList<String>();
		VERSION_LIST.add(VERSION_1_0);
	}

	private static final String HEADER_MARK = "#header"; //$NON-NLS-1$
	private static final String DATA_MARK = "#data"; //$NON-NLS-1$

	private static final String HEADER_TITLE_LANGUAGE = Messages.DictionaryCsvFileAccessor_14;
	private static final String HEADER_TITLE_CATEGORY = Messages.DictionaryCsvFileAccessor_17;

	private static final String DATA_TITLE_SUB_KEYWORD = Messages.DictionaryCsvFileAccessor_24;
	private static final String DATA_TITLE_CONJUGATION = Messages.DictionaryCsvFileAccessor_25;
	private static final String DATA_TITLE_INFORMAL_DEFINITION = Messages.DictionaryCsvFileAccessor_28;

	/**
	 * 最新バージョン番号の取得
	 * @return 最新バージョン番号
	 */
	private static String getLatestVersion() {
		if (VERSION_LIST.isEmpty()) {
			return null;
		} else {
			return VERSION_LIST.get(VERSION_LIST.size() - 1);
		}
	}

	/**
	 * バージョンに対応したヘッダータイトルの配列を返す
	 * @param version バージョン番号
	 * @return ヘッダータイトル配列
	 */
	private static String[] getHeaderTitle(String version) {

		if (VERSION_1_0.equals(version)) {
			return new String[] {
					Messages.DictionaryCsvFileAccessor_11,
					Messages.DictionaryCsvFileAccessor_12,
					Messages.DictionaryCsvFileAccessor_13,
					MessageFormat.format(HEADER_TITLE_LANGUAGE, 1),
					MessageFormat.format(HEADER_TITLE_LANGUAGE, 2),
					MessageFormat.format(HEADER_TITLE_LANGUAGE, 3),
					MessageFormat.format(HEADER_TITLE_LANGUAGE, 4),
					MessageFormat.format(HEADER_TITLE_LANGUAGE, 5),
					Messages.DictionaryCsvFileAccessor_15,
					Messages.DictionaryCsvFileAccessor_16,
					MessageFormat.format(HEADER_TITLE_CATEGORY, 1),
					MessageFormat.format(HEADER_TITLE_CATEGORY, 2),
					MessageFormat.format(HEADER_TITLE_CATEGORY, 3),
					MessageFormat.format(HEADER_TITLE_CATEGORY, 4),
					MessageFormat.format(HEADER_TITLE_CATEGORY, 5),
					MessageFormat.format(HEADER_TITLE_CATEGORY, 6),
					MessageFormat.format(HEADER_TITLE_CATEGORY, 7),
					MessageFormat.format(HEADER_TITLE_CATEGORY, 8),
					MessageFormat.format(HEADER_TITLE_CATEGORY, 9),
					MessageFormat.format(HEADER_TITLE_CATEGORY, 10),
					MessageFormat.format(HEADER_TITLE_CATEGORY, 11),
					MessageFormat.format(HEADER_TITLE_CATEGORY, 12),
					MessageFormat.format(HEADER_TITLE_CATEGORY, 13),
					MessageFormat.format(HEADER_TITLE_CATEGORY, 14),
					MessageFormat.format(HEADER_TITLE_CATEGORY, 15),
					MessageFormat.format(HEADER_TITLE_CATEGORY, 16),
					MessageFormat.format(HEADER_TITLE_CATEGORY, 17),
					MessageFormat.format(HEADER_TITLE_CATEGORY, 18),
					MessageFormat.format(HEADER_TITLE_CATEGORY, 19),
					MessageFormat.format(HEADER_TITLE_CATEGORY, 20),
			};
		}
		return new String[0];
	}

	/**
	 * バージョンに対応したデータタイトルの配列を返す
	 * @param version バージョン番号
	 * @return データタイトル配列
	 */
	private static String[] getDataTitle(String version) {

		if (VERSION_1_0.equals(version)) {
			return new String[]{
					Messages.DictionaryCsvFileAccessor_21,
					Messages.DictionaryCsvFileAccessor_22,
					Messages.DictionaryCsvFileAccessor_23,
					MessageFormat.format(DATA_TITLE_SUB_KEYWORD, 1),
					MessageFormat.format(DATA_TITLE_SUB_KEYWORD, 2),
					MessageFormat.format(DATA_TITLE_SUB_KEYWORD, 3),
					MessageFormat.format(DATA_TITLE_SUB_KEYWORD, 4),
					MessageFormat.format(DATA_TITLE_SUB_KEYWORD, 5),
					MessageFormat.format(DATA_TITLE_CONJUGATION, 1),
					MessageFormat.format(DATA_TITLE_CONJUGATION, 2),
					MessageFormat.format(DATA_TITLE_CONJUGATION, 3),
					MessageFormat.format(DATA_TITLE_CONJUGATION, 4),
					MessageFormat.format(DATA_TITLE_CONJUGATION, 5),
					Messages.DictionaryCsvFileAccessor_26,
					Messages.DictionaryCsvFileAccessor_27,
					MessageFormat.format(DATA_TITLE_INFORMAL_DEFINITION, 1),
					MessageFormat.format(DATA_TITLE_INFORMAL_DEFINITION, 2),
					MessageFormat.format(DATA_TITLE_INFORMAL_DEFINITION, 3),
					MessageFormat.format(DATA_TITLE_INFORMAL_DEFINITION, 4),
					MessageFormat.format(DATA_TITLE_INFORMAL_DEFINITION, 5),
					Messages.DictionaryCsvFileAccessor_29,
					Messages.DictionaryCsvFileAccessor_30,
					Messages.DictionaryCsvFileAccessor_31,
//					"Type",
			};
		}

		return new String[0];
	}

	/**
	 * CSVファイルのインポート
	 * @param file ファイル
	 * @param dictionary 辞書
	 * @return 辞書
	 * @throws FileAccessException
	 */
	public static Dictionary importCsvFile(File file, Dictionary dictionary) throws FileAccessException {

		// CSVファイルの読込
		List<String[]> data = null;
		try {
			data = Csv.load(file, CSV_CONFIG, new StringArrayListHandler());
		} catch (IOException e) {
			throw new FileAccessException(Messages.DictionaryCsvFileAccessor_0, e);
		}

		// バージョン番号
		String version = ""; //$NON-NLS-1$

		dictionary.clear();

		boolean isHeader = false;
		boolean isData = false;

		int baseIndex = 0;
		for (int index = 0; index < data.size(); index++) {
			String[] line = data.get(index);
			int offset = index - baseIndex;

			// ヘッダー
			if (HEADER_MARK.equals(line[0])) {
				isHeader = true;
				isData = false;
				baseIndex = index;
				continue;
			}

			// データ
			if (DATA_MARK.equals(line[0])) {
				isData = true;
				isHeader = false;
				baseIndex = index;
				continue;
			}

			// ヘッダー部の場合
			if (isHeader) {
				String value = line[1];
				// version
				if (offset == 1) {
					version = value;
				}
				// versionによる処理の分岐
				if (VERSION_1_0.equals(version)) {
					if (offset == 2) {	// domain
						dictionary.getDictionaryClass().domain = value;
					} else if (offset == 3) {	// project
						dictionary.getDictionaryClass().project = value;
					} else if (offset >= 4 && offset <= 8) {	// language
						if (!PluginHelper.isEmpty(value)) {
							dictionary.getDictionaryClass().languages.add(value);
						}
					} else if (offset == 9) {	// model
						dictionary.getDictionaryClass().model = value;
					} else if (offset == 10) {	// specific category
						if (!PluginHelper.isEmpty(value)) {
							dictionary.getSetting().setDefaultCategory(!Boolean.parseBoolean(value));
						} else {
							dictionary.getSetting().setDefaultCategory(true);
						}
					} else if (offset >= 11 && offset <= 30) {
						if (!dictionary.getSetting().isDefaultCategory()) {
							if (!PluginHelper.isEmpty(value)) {
								dictionary.getSetting().setCategory(
										offset - 10,
										value,
										convertHexToRgb(line[2]),
										convertHexToRgb(line[3]));
							}
						}
					} else {
						continue;
					}
				}
			}

			// データ部の場合
			if (isData) {
				// versionによる処理の分岐
				if (VERSION_1_0.equals(version)) {
					if (offset <= 1) continue;		// データタイトル部は読み飛ばす
					int col = 0;

					Entry entry = new Entry();

					// seqno 0
					entry.setSeqNo(Integer.parseInt(line[col++]));
					// outno 1
					entry.setOutNo(Integer.parseInt(line[col++]));
					// word  2
					entry.setWord(line[col++]);
					// conjugation 3-7
//					for (int i=0; i<DictionaryConstants.MAX_COL_CONJUGATION; i++) {
					// 定数定義が変わった場合にカラムがずれるためマジックナンバーを使用
					for (int i=0; i<5; i++) {
						entry.getConjugations().add(line[col++]);
					}
					// subword 8-12
//					for (int i=0; i<DictionaryConstants.MAX_COL_SUBWORD; i++) {
					for (int i=0; i<5; i++) {
						entry.getSubwords().add(line[col++]);
					}
					// category 13
					String category = line[col++];
					if (StringUtils.isBlank(category) || !StringUtils.isNumeric(category)) {
						entry.setCategoryNo(0);
					} else {
						entry.setCategoryNo(Integer.parseInt(category));
					}
					// category name 14
					col++;
					// informal 15-19
//					for (int i=0; i<DictionaryConstants.MAX_COL_INFORMAL; i++) {
					for (int i=0; i<5; i++) {
						entry.getInformals().add(line[col++]);
					}
					// section 20
					String section = line[col++];
					if (StringUtils.isBlank(section) || !StringUtils.isNumeric(section)) {
						entry.setSection(0);
					} else {
						entry.setSection(Integer.parseInt(section));
					}
					// section name 21
					col++;
					// formal 22
					entry.setFormal(line[col++]);
//					// type 23
//					entry.setType(line[col++]);

					dictionary.getEntries().add(entry);
				}
			}
		}

		return dictionary;
	}

	/**
	 * CSVファイルのエクスポート<br>
	 * エクスポート時は最新バージョンのフォーマットでファイルを出力する。
	 * @param file ファイル
	 * @param dictionary 辞書
	 * @throws FileAccessException
	 */
	public static void exportCsvFile(File file, Dictionary dictionary) throws FileAccessException {

		// バージョン番号（最新）
		String version = getLatestVersion();
		// ヘッダータイトル
		String[] headerTitle = getHeaderTitle(version);
		// データタイトル
		String[] dataTitle = getDataTitle(version);

		// 出力用リスト
		List<String[]> data = new ArrayList<String[]>();

		// ヘッダー部のヘッダー出力
		addLine(data, HEADER_MARK);

		// version
		addLine(data, headerTitle[0], version, null, null);

		// domain
		addLine(data, headerTitle[1], dictionary.getDictionaryClass().domain, null, null);
		// project
		addLine(data, headerTitle[2], dictionary.getDictionaryClass().project, null, null);
		// language
		List<String> languages = dictionary.getDictionaryClass().languages;
		for (int i = 0; i < DictionaryConstants.MAX_LANGUAGES_COUNT; i++) {
			if (i < languages.size()) {
				addLine(data, headerTitle[3 + i], languages.get(i), null, null);
			} else {
				addLine(data, headerTitle[3 + i], null, null, null);
			}
		}
		// model
		addLine(data, headerTitle[8], dictionary.getDictionaryClass().model, null, null);

		DictionarySetting setting = dictionary.getSetting();
		// use specific categories
		addLine(data, headerTitle[9], String.valueOf(!setting.isDefaultCategory()));
		// category
		for (int i = 1; i <= DictionaryConstants.MAX_CATEGORY_NO; i++) {
			Category category = setting.getCategory(i);
			if (category != null) {
				addLine(data, headerTitle[9 + i], category.getName(),
						convertRgbToHex(category.getPrimary()),
						convertRgbToHex(category.getSecondary()));
			} else {
				addLine(data, headerTitle[9 + i], null, null, null);
			}
		}

		// データ部のヘッダー出力
		addLine(data, DATA_MARK);
		addLine(data, dataTitle);

		// データ部出力
		// entries
		List<Entry> entries = dictionary.sort(Dictionary.SEQNO | Dictionary.ASC | Dictionary.LOGICAL);
		for (Entry entry : entries) {

			List<String> line = new ArrayList<String>();

			// seqNo
			line.add(String.valueOf(entry.getSeqNo()));
			// outNo
			line.add(String.valueOf(entry.getOutNo()));
			// word
			line.add(entry.getWord());
			// conjugation
			for (int i=0; i<DictionaryConstants.MAX_COL_CONJUGATION; i++) {
				if (i < entry.getConjugations().size()) {
					line.add(entry.getConjugations().get(i));
				} else {
					line.add(null);
				}
			}
			// subword
			for (int i=0; i<DictionaryConstants.MAX_COL_SUBWORD; i++) {
				if (i < entry.getSubwords().size()) {
					line.add(entry.getSubwords().get(i));
				} else {
					line.add(null);
				}
			}
			// category
			if (dictionary.getSetting().containsCategory(entry.getCategoryNo())) {
				line.add(String.valueOf(entry.getCategoryNo()));
				line.add(dictionary.getSetting().getCategory(entry.getCategoryNo()).getName());
			} else {
				line.add(String.valueOf(0));
				line.add(null);
			}
			// informal
			for (int i=0; i<DictionaryConstants.MAX_COL_INFORMAL; i++) {
				if (i < entry.getInformals().size()) {
					line.add(entry.getInformals().get(i));
				} else {
					line.add(null);
				}
			}
			// section
			line.add(String.valueOf(entry.getSection()));
			if (entry.getSection() == 0) {
				line.add(null);
			} else {
				List<Section> sections = ModelManager.getInstance().getModel(dictionary).getSectionDefs();
				String sectionName = null;
				for (Section section : sections) {
					if (section.getCd() == entry.getSection()) {
						sectionName = section.getName();
						break;
					}
				}
				line.add(sectionName);
			}
			// formal
			line.add(entry.getFormal());
//			// type
//			line.add(entry.getType());

			addLine(data, line.toArray(new String[0]));
		}

		try {
			Csv.save(data, file, CSV_CONFIG,new StringArrayListHandler());
		} catch (Exception e) {
			throw new FileAccessException(Messages.DictionaryCsvFileAccessor_1, e);
		}
	}

	/**
	 * 出力データに行を追加する。
	 * @param data 出力データ（文字列配列のリスト）
	 * @param line 行データ（文字列配列）…可変長引数
	 */
	private static void addLine(List<String[]> data, String... line) {
		data.add(line);
	}

	/**
	 * RGBを16進数(ffffff)形式に変換する
	 * @param rgb RGB
	 * @return 16進形式
	 */
	private static String convertRgbToHex(RGB rgb) {
		return getHexString(rgb.red) + getHexString(rgb.green) + getHexString(rgb.blue);
	}

	/**
	 * 16進数(ffffff)形式RGBをに変換する
	 * @param hex 16進形式
	 * @return RGB
	 */
	private static RGB convertHexToRgb(String hex) {
		// 引数が0文字または7文字以上の場合、黒を返す
		if (PluginHelper.isEmpty(hex) || hex.length() > 6) {
			return ColorName.RGB_BLACK;
		}

		int colorNum = 0;
		try {
			// 16進 ⇒ 10進
			colorNum = Integer.decode("0x" + hex); //$NON-NLS-1$
		} catch (NumberFormatException e) {
			return ColorName.RGB_BLACK;
		}

		int red = colorNum / (256 * 256);
		int green = (colorNum % (256 * 256)) / 256;
		int blue = colorNum % 256;

		return new RGB(red, green, blue);
	}

	/**
	 * 整数を2桁の16進数文字列に変換する
	 * @param num 整数
	 * @return 16進数文字列
	 */
	private static String getHexString(int num) {
		String ret = Integer.toHexString(num);
		if (ret.length() < 2) {
			ret = "0" + ret; //$NON-NLS-1$
		}
		return ret;
	}
}
