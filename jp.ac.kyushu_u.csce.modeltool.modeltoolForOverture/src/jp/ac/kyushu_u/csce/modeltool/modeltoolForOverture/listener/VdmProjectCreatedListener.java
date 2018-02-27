package jp.ac.kyushu_u.csce.modeltool.modeltoolForOverture.listener;

import java.util.ArrayList;
import java.util.List;

import jp.ac.kyushu_u.csce.modeltool.dictionary.constant.DictionaryConstants;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryClass;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.DictionaryView;
import jp.ac.kyushu_u.csce.modeltool.dictionary.dict.TableTab;
import jp.ac.kyushu_u.csce.modeltool.modeltoolForOverture.util.ModelToolForOvertureUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * VDMプロジェクトの作成を検知するリスナー
 */
public class VdmProjectCreatedListener implements IResourceChangeListener {

	/**
	 * VDMプロジェクト
	 */
	private IProject addedProject = null;

	/*
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	@Override
	public void resourceChanged(IResourceChangeEvent event) {

		// 変更の取得　変更なしの場合処理なし
		IResourceDelta delta = event.getDelta();
		if (delta == null) {
			return;
		}

		// 変更の解析
		analyseDelta(delta);
	}

	/**
	 * デルタを再帰的に解析する
	 * @param delta デルタ
	 */
	private void analyseDelta(IResourceDelta delta) {

		IResource resource = delta.getResource();
		if (resource != null) {
			// 対象リソースがプロジェクトの場合のみ処理を行う
			if (resource instanceof IProject) {
				IProject project = (IProject)resource;

				// VDMプロジェクトの作成は
				//   1.プロジェクトの追加
				//   2.プロジェクトの変更（ネイチャー(種別)の指定）
				// の2回イベントが発生するため、2のイベントの方をキャッチして処理を行う。

				// プロジェクト追加の場合
				if (delta.getKind() == IResourceDelta.ADDED) {
					// 追加されたプロジェクトの退避（変更イベントでのチェック用）
					addedProject = project;

				// プロジェクト追加以外の場合
				} else {

					// 退避プロジェクトの取得、クリア
					IProject vdmProject = addedProject;
					addedProject = null;

					// プロジェクト変更の場合
					if (delta.getKind() == IResourceDelta.CHANGED) {

						// VDMプロジェクト以外の場合処理なし
						if (!ModelToolForOvertureUtil.isVdmProject(project)) {
							return;
						}

						// 1.追加⇒2.変更の流れのみ処理するため、退避プロジェクトのnot nullチェック
						// および対象プロジェクトとの一致をチェックする。
						if (vdmProject == null || !project.equals(vdmProject)) {
							return;
						}

						// ネイチャー追加の場合、フラグはDESCRIPTION ⇒ それ以外であれば処理なし
						if ((delta.getFlags() & IResourceDelta.DESCRIPTION) == 0) {
							return;
						}

						try {
							IWorkbenchPage wbpage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

							// 辞書ビューのアクティブ化
							DictionaryView view = (DictionaryView)wbpage.findView(DictionaryConstants.PART_ID_DICTIONARY);

							TableTab tab = null;

							// 辞書ビューが開かれていない場合
							if (view == null) {
								// 辞書ビューのオープン
								view = (DictionaryView)wbpage.showView(DictionaryConstants.PART_ID_DICTIONARY);

								// アクティブなタブの取得
								// 　ビューを新規に開いた場合新規タブが作成されるはずなので。
								// 　念のため、アクティブなタブがない場合はタブを作成。
								tab = view.getActiveTableTab(true);

							// 辞書ビューが開かれている場合
							} else {
								// ビュー上の辞書名の取得
								List<String> tabList = new ArrayList<String>();
								String[][] tabArray = view.getArrayOfTabText();
								for (int i=0; i<tabArray.length; i++) {
									tabList.add(tabArray[i][0]);
								}

								// 辞書名重複対応
								// 　デフォルトの辞書名が既に存在する場合、後ろに (数字) を追加する
								String dicNameDefault = DictionaryConstants.DEFAULT_DIC_NAME;
								String dicName = dicNameDefault;
								int duplication = 1;
								while (true) {

									// 重複無しの場合
									if (tabList.contains(dicName) == false) {
										break;
									}

									// 重複有りの場合
									duplication++;
									dicName = dicNameDefault + "(" + duplication + ")"; //$NON-NLS-1$ //$NON-NLS-2$
								}

								// 新規タブの作成
								tab = view.createNewTab(dicName);
							}

							if (tab != null) {
								// プロジェクト種別によりモデルを設定
								DictionaryClass dicClass = tab.getDictionary().getDictionaryClass();
								dicClass.model = ModelToolForOvertureUtil.getVdmModelKey(project);
							}

						} catch (CoreException e) {
						}
					}

				}
			}
		}

		// 子要素を再帰的に確認する
		IResourceDelta[] children = delta.getAffectedChildren();
		for (IResourceDelta child : children) {
			analyseDelta(child);
		}
	}
}
