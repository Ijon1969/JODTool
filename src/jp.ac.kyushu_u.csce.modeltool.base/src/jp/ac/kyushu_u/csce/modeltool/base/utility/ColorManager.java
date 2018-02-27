package jp.ac.kyushu_u.csce.modeltool.base.utility;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * カラーマネージャークラス
 * @author KBK yoshimura
 */
public class ColorManager {

	/** カラーテーブル key:RGB, value:Color */
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
	 * Colorオブジェクトの取得
	 * @param rgb
	 * @return
	 */
	public Color getColor(RGB rgb) {
		Color color = (Color) fColorTable.get(rgb);
		if (color == null) {
			color = new Color(Display.getCurrent(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}
}
