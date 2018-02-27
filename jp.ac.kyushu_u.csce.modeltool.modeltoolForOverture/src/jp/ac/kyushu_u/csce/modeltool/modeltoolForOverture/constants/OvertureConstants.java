package jp.ac.kyushu_u.csce.modeltool.modeltoolForOverture.constants;

/**
 * Overture向け拡張 定数クラス
 */
public class OvertureConstants {

	private OvertureConstants() {}

	// ウィザードID
	/** 新規辞書ウィザードID */
	public static final String WIZARD_ID_NEW_DICTIONARY =
			"jp.ac.kyushu_u.csce.modeltool.modeltoolForOverture.wizard.dictionary"; //$NON-NLS-1$

	// ネイチャーID
	// 　※Overtureプラグイン側で定数定義されてないため、こちらで定数定義した
	// 　　OvertureプラグインでネイチャーIDの変更があった場合（ないとは思うが）はこちらの定数定義も必要
	// 　　エディターID、ビューIDについても同様
	/** (Overture) NATURE_ID VDM++ */
	public static final String NATURE_ID_VDMPP = "org.overture.ide.vdmpp.core.nature"; //$NON-NLS-1$
	/** (Overture) NATURE_ID VDM-SL */
	public static final String NATURE_ID_VDMSL = "org.overture.ide.vdmsl.core.nature"; //$NON-NLS-1$
	/** (Overture) NATURE_ID VDM-RT */
	public static final String NATURE_ID_VDMRT = "org.overture.ide.vdmrt.core.nature"; //$NON-NLS-1$

	// エディターID
	/** (Overture) EDITOR_ID VDM++ */
	public static final String EDITOR_ID_VDMPP = "org.overture.ide.vdmpp.ui.VdmPpEditor"; //$NON-NLS-1$
	/** (Overture) EDITOR_ID VDM-SL */
	public static final String EDITOR_ID_VDMSL = "org.overture.ide.vdmsl.ui.VdmSlEditor"; //$NON-NLS-1$
	/** (Overture) EDITOR_ID VDM-RT */
	public static final String EDITOR_ID_VDMRT = "org.overture.ide.vdmrt.ui.VdmRtEditor"; //$NON-NLS-1$

	// ビューID
	/** (Overture) VIEW_ID VDM Explorer */
	public static final String VIEW_ID_VDM_EXPLORER = "org.overture.ide.ui.VdmExplorer"; //$NON-NLS-1$
}
