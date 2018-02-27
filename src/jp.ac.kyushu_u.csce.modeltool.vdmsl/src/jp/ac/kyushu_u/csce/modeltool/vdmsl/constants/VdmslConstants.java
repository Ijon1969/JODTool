package jp.ac.kyushu_u.csce.modeltool.vdmsl.constants;

import jp.ac.kyushu_u.csce.modeltool.vdmsl.Messages;


/**
 * 定数クラス
 * @author KBK yoshimura
 */
public class VdmslConstants {

	private VdmslConstants() {}

	/** 拡張子：vdmsl */
	public static final String EXTENSION_VDMSL	= "vdmsl"; //$NON-NLS-1$

	/** 形式モデルキー：VDM-SL */
	public static final String MODEL_KEY_VDMSL	= "vdmsl"; //$NON-NLS-1$

	// 形式的種別
	/** 形式的種別：定数 */
	public static final String SECTION_MODULE				= Messages.VdmslConstants_1;
	/** 形式的種別：定数 */
	public static final String SECTION_VALUE				= Messages.VdmslConstants_2;
	/** 形式的種別：関数 */
	public static final String SECTION_FUNCTION			= Messages.VdmslConstants_3;
	/** 形式的種別：手続き */
	public static final String SECTION_OPERATION			= Messages.VdmslConstants_4;
	/** 形式的種別：型 */
	public static final String SECTION_TYPE				= Messages.VdmslConstants_5;
	/** 形式的種別：状態 */
	public static final String SECTION_STATE_OF			= Messages.VdmslConstants_6;

	/** 形式的種別No：モジュール */
	public static final int SECTION_NO_MODULE				= 8;
	/** 形式的種別No：定数 */
	public static final int SECTION_NO_VALUE					= 3;
	/** 形式的種別No：関数 */
	public static final int SECTION_NO_FUNCTION				= 4;
	/** 形式的種別No：手続き */
	public static final int SECTION_NO_OPERATION				= 5;
	/** 形式的種別No：型 */
	public static final int SECTION_NO_TYPE					= 6;
	/** 形式的種別No：状態 */
	public static final int SECTION_NO_STATE_OF				= 7;

}
