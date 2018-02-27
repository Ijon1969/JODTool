package jp.ac.kyushu_u.csce.modeltool.vdmrt.constants;

import jp.ac.kyushu_u.csce.modeltool.vdmrt.Messages;

/**
 * 定数クラス
 * @author KBK yoshimura
 */
public class VdmrtConstants {

	private VdmrtConstants() {}

	/** 拡張子：vdmrt */
	public static final String EXTENSION_VDMRT	= "vdmrt"; //$NON-NLS-1$

	/** 形式モデルキー：VDM-RT */
	public static final String MODEL_KEY_VDMRT	= "vdmrt"; //$NON-NLS-1$

	// 形式的種別
	// VDM++と重複するものはdictionaryプロジェクトのDictionaryConstantsにて定義
	/** 形式的種別：システム */
	public static final String SECTION_SYSTEM				= Messages.VdmrtConstants_0;

	/** 形式的種別No：システム */
	public static final int SECTION_NO_SYSTEM				= 11;

}
