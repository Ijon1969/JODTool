package jp.ac.kyushu_u.csce.modeltool.modeltoolForOverture.wizard;

import java.io.IOException;
import java.io.InputStream;

import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryClass;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryView;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;
import jp.ac.kyushu_u.csce.modeltool.modeltoolForOverture.Messages;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;

public class NewDictionaryWizard extends Wizard implements IWorkbenchWizard {

	private WizardNewDictionaryCreationPage _pageOne;
	private WizardNewDictionaryCreationSettingPage _pageTwo;
	private IStructuredSelection fStructuredSelection;

	/**
	 * コンストラクタ
	 */
	public NewDictionaryWizard() {
		super.setWindowTitle(Messages.NewDictionaryWizard_0);
	}

	/*
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {

		this.fStructuredSelection = selection;
	}

	/*
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {

		IFile file = _pageOne.createNewFile();
		if (file.exists()) {
			String fileName = file.getName();
			if (fileName.contains(".")) { //$NON-NLS-1$
				fileName = fileName.substring(0, fileName.indexOf(".")); //$NON-NLS-1$
			}

			boolean isClean = false;
			InputStream in =null;
			try {
				in= file.getContents();
				if (file.getContents().read() == -1) {
					isClean = true;
				}
			} catch (IOException e) {
			} catch (CoreException e) {
			} finally {
				if(in!=null) {
					try {
						in.close();
					} catch (IOException e) {
					}
				}
			}

			if (isClean) {
				try {
					// 辞書ビューのオープン
					IWorkbenchPage wbpage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					DictionaryView view = (DictionaryView)wbpage.showView(DictionaryConstants.PART_ID_DICTIONARY);

					// 新規タブの作成
					TableTab tab = view.createNewTab(fileName);
					tab.setFile(file);

					// プロジェクト種別によりモデルを設定
					DictionaryClass dicClass = tab.getDictionary().getDictionaryClass();
					dicClass.model = _pageOne.getModelKey();

					// 2ページ目(辞書設定)がカレントの場合
					if (getContainer().getCurrentPage().equals(_pageTwo)) {
						DictionaryClass newDicClass = _pageTwo.getDictionaryClass();
						dicClass.domain = newDicClass.domain;
						dicClass.project = newDicClass.project;
						dicClass.languages.clear();
						dicClass.languages.addAll(newDicClass.languages);
					}

					// 辞書の保存（モデル設定の保存）
					tab.save(false);

				} catch (CoreException e) {
				}
			}
		}

		return true;
	}

	/*
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		// 1ページ目　フォルダ・ファイル名入力
		_pageOne = new WizardNewDictionaryCreationPage("Dictionary File", this.fStructuredSelection); //$NON-NLS-1$
		_pageOne.setTitle(Messages.NewDictionaryWizard_1);
		_pageOne.setDescription(Messages.NewDictionaryWizard_2);

		addPage(_pageOne);

		// 2ページ目　辞書情報入力
		_pageTwo = new WizardNewDictionaryCreationSettingPage("Dictionary Setting"); //$NON-NLS-1$
		_pageTwo.setTitle(Messages.NewDictionaryWizard_1);
		_pageTwo.setDescription(Messages.NewDictionaryWizard_2);

		addPage(_pageTwo);
	}
}
