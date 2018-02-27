package jp.ac.kyushu_u.csce.modeltool.vdmslEditor.editor;

import org.eclipse.swt.graphics.RGB;

/**
 * 色設定定数クラス
 * @author KBK yoshimura
 */
public interface IVdmslColorConstants {

	/** コメント */
    public static final RGB VDM_COMMENT = new RGB(63, 127, 95);
    /** 文字列 */
    public static final RGB STRING = new RGB(42, 0, 255);
    /** 文字 */
    public static final RGB CHARACTER = new RGB(42, 0, 255);
    /** デフォルト */
    public static final RGB DEFAULT = new RGB(0, 0, 0);
    /** キーワード */
    public static final RGB RESERVED = new RGB(127, 0, 85);
}
