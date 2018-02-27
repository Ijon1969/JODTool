package jp.ac.kyushu_u.csce.modeltool.dictionary.dict;

import static jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryPreferenceConstants.*;

import jp.ac.kyushu_u.csce.modeltool.base.utility.ColorName;
import jp.ac.kyushu_u.csce.modeltool.dictionary.ModelToolDictionaryPlugin;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.column.IColumn;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.ModelManager;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

/**
 * 辞書テーブルのラベルプロバイダー・クラス
 * @author KBK yoshimura
 */
public class TableLabelProvider extends LabelProvider implements
		ITableLabelProvider, ITableColorProvider, ITableFontProvider {

	/** カラム定義配列 */
	private IColumn[] columns;

	/** 辞書 */
	private Dictionary dictionary;

	/**
	 * コンストラクター
	 * @param viewer テーブルビューアー
	 * @param columns カラム定義配列
	 */
	public TableLabelProvider(TableViewer viewer, IColumn[] columns, Dictionary dictionary) {
		this.columns = columns;
		this.dictionary = dictionary;
	}

	/**
	 * テーブルに出力する文字列を返す<br>
	 * 処理内容はカラム定義クラス(IColumn実装クラス)のgetColumnText()メソッドに記述する
	 */
	public String getColumnText(Object element, int index) {

		// IColumnのgetColumnText()メソッド呼び出し
		return columns[index].getColumnText(element);
	}

	/**
	 * テーブルに出力する画像を返す<br>
	 * 処理内容はカラム定義クラス(IColumn実装クラス)のgetColumnImage()メソッドに記述する
	 */
	public Image getColumnImage(Object element, int columnIndex) {

		// IColumnのgetColumnImage()メソッド呼び出し
		return columns[columnIndex].getColumnImage(element);
	}

	/**
	 * テーブルの背景色を返す
	 */
	public Color getBackground(Object element, int columnIndex) {
		if (DictionaryUtil.isOutputMode()) {
			IPreferenceStore store = ModelToolDictionaryPlugin.getDefault().getPreferenceStore();
			ModelManager manager = ModelManager.getInstance();
			int section = ((Entry)element).getSection();
			if (manager.isSectionBg(dictionary, section)) {
				RGB bgColor = PreferenceConverter.getColor(
						store,
						PK_DICTIONARY_SECTION_BG + manager.getModelKey(dictionary) + "_" + //$NON-NLS-1$
						String.valueOf(section));
				return ModelToolDictionaryPlugin.getColorManager().getColor(bgColor);
			}
		}
		return null;
	}

	/**
	 * テーブルの前景色を返す
	 */
	public Color getForeground(Object element, int columnIndex) {

		// 見出し語エラーチェック
		if (! new WordCheckerExtensionManager().check((Entry)element)) {
			return ModelToolDictionaryPlugin.getColorManager().getColor(ColorName.RGB_CRIMSON);
		}

		// 辞書編集ビューで開いている場合
		if (((Entry)element).isEdit()) {
			return ModelToolDictionaryPlugin.getColorManager().getColor(ColorName.RGB_DARKRED);
		} else {
			return null;
		}
	}

	/**
	 * フォントを返す
	 */
	public Font getFont(Object element, int columnIndex) {
		return null;
	}
}
