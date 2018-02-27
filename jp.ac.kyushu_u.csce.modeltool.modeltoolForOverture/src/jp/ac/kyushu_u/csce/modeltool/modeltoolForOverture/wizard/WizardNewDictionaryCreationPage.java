package jp.ac.kyushu_u.csce.modeltool.modeltoolForOverture.wizard;

import jp.ac.kyushu_u.csce.modeltool.base.utility.PluginHelper;
import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.model.ModelManager;
import jp.ac.kyushu_u.csce.modeltool.modeltoolForOverture.Messages;
import jp.ac.kyushu_u.csce.modeltool.modeltoolForOverture.constants.OvertureConstants;
import jp.ac.kyushu_u.csce.modeltool.vdmrt.constants.VdmrtConstants;
import jp.ac.kyushu_u.csce.modeltool.vdmsl.constants.VdmslConstants;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class WizardNewDictionaryCreationPage extends WizardNewFileCreationPage {

	/** 現在の選択（コンテナ） */
	IStructuredSelection currentSelection;

	/** モデルキー */
	private String modelKey;

	/**
	 * コンストラクタ
	 * @param pageName ページ名
	 * @param selection 選択
	 */
	protected WizardNewDictionaryCreationPage(String pageName,
			IStructuredSelection selection) {
		super(pageName, selection);
		setPageComplete(false);
		this.currentSelection = selection;
		setFileExtension(DictionaryConstants.EXTENSION_JDD);
	}

	/*
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#createAdvancedControls(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createAdvancedControls(Composite parent) {
		// Advances Control 非表示
	}

	/*
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#validatePage()
	 */
	@Override
	protected boolean validatePage() {
		try {
			// 親クラスのバリデーション
			boolean ret = super.validatePage();
			if (!ret) {
				return false;
			}

			IProject project = null;
			// 選択されたフォルダの取得
			IContainer container = PluginHelper.getFolder(getContainerFullPath().segments()[0]);
			// フォルダがプロジェクトでない場合、プロジェクトを取得
			if (container instanceof IProject) {
				project = (IProject)container;
			} else {
				project = container.getProject();
			}

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
			// 上記以外
			else {
				setErrorMessage(Messages.WizardNewDictionaryCreationPage_0);
				return false;
			}

			// 次ページのモデル名を設定
			((WizardNewDictionaryCreationSettingPage)getNextPage()).setModelName(
					ModelManager.getInstance().getModelName(modelKey));

			return ret;

		} catch (CoreException e) {
			return false;
		}
	}

	/*
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#validateLinkedResource()
	 */
	@Override
	protected IStatus validateLinkedResource() {
		// Advances Control 非表示のため常にOKを返す
		return Status.OK_STATUS;
	}

	/*
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#createLinkTarget()
	 */
	@Override
	protected void createLinkTarget() {
		// Advances Control 非表示のため使用しない
	}

	/**
	 * モデルキーの取得
	 * @return モデルキー
	 */
	public String getModelKey() {
		return modelKey;
	}
}
