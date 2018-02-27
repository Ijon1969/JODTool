package jp.ac.kyushu_u.csce.modeltool.vdmrtEditor.editor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Colorオブジェクト管理クラス
 * @author KBK yoshimura
 */
public class VdmrtColorManager {

	/** カラーテーブル */
	protected Map<RGB, Color> fColorTable = new HashMap<RGB, Color>(10);

	/**
	 * dispose
	 */
	public void dispose() {
		Iterator<Color> e = fColorTable.values().iterator();
		while (e.hasNext())
			e.next().dispose();
	}

	/**
	 * 色の取得
	 * @param rgb RGB値
	 * @return Colorオブジェクト
	 */
	public Color getColor(RGB rgb) {
		Color color = fColorTable.get(rgb);
		if (color == null) {
			color = new Color(Display.getCurrent(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}
}
