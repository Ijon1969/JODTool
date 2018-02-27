package jp.ac.kyushu_u.csce.modeltool.modeltoolForOverture.util;

import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.modeltoolForOverture.constants.OvertureConstants;
import jp.ac.kyushu_u.csce.modeltool.vdmrt.constants.VdmrtConstants;
import jp.ac.kyushu_u.csce.modeltool.vdmsl.constants.VdmslConstants;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

/**
 * Overture拡張プラグイン ユーティリティクラス
 */
public class ModelToolForOvertureUtil {

	/**
	 * VDMプロジェクト(VDMPP, VDMSL, VDMRT)かどうか判定する
	 * @param project プロジェクト
	 * @return VDMプロジェクトの場合true、それ以外の場合falseを返す
	 */
	public static boolean isVdmProject(IProject project) {

		try {
			// VDM++の場合true
			if (project.hasNature(OvertureConstants.NATURE_ID_VDMPP)) {
				return true;
			}
			// VDM-SLの場合true
			if (project.hasNature(OvertureConstants.NATURE_ID_VDMSL)) {
				return true;
			}
			// VDM-RTの場合true
			if (project.hasNature(OvertureConstants.NATURE_ID_VDMRT)) {
				return true;
			}
		} catch (CoreException e) {
			return false;
		}

		return false;
	}

	/**
	 * VDMプロジェクトから形式モデルのキーを取得する
	 * @param project プロジェクト
	 * @return モデルキー
	 */
	public static String getVdmModelKey(IProject project) {

		String modelKey = ""; //$NON-NLS-1$

		try {
			// VDM++プロジェクト
			if (project.hasNature(OvertureConstants.NATURE_ID_VDMPP)) {
				modelKey = DictionaryConstants.MODEL_KEY_VDMPP;
			}
			// VDM-SLプロジェクト
			else if (project.hasNature(OvertureConstants.NATURE_ID_VDMSL)) {
				modelKey = VdmslConstants.MODEL_KEY_VDMSL;
			}
			// VDM-RTプロジェクト
			else if (project.hasNature(OvertureConstants.NATURE_ID_VDMRT)) {
				modelKey = VdmrtConstants.MODEL_KEY_VDMRT;
			}
		} catch (CoreException e) {
		}

		return modelKey;
	}
}
