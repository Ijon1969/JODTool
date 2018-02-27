package jp.ac.kyushu_u.csce.modeltool.base.preference;

import jp.ac.kyushu_u.csce.modeltool.base.Messages;
import jp.ac.kyushu_u.csce.modeltool.base.ModelToolBasePlugin;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * プリファレンスページ（ツール設定）
 *
 * @author yoshimura
 */
public class ToolPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	/**
	 * コンストラクタ
	 */
	public ToolPreferencePage() {
		super(GRID);
		setPreferenceStore(ModelToolBasePlugin.getDefault().getPreferenceStore());
		setDescription(Messages.ToolPreferencePage_0);
	}

	/**
	 * フィールドエディタ生成
	 */
	public void createFieldEditors() {

//		Composite parent = getFieldEditorParent();
//
//		Map<String, String> linkMap = new HashMap<String, String>();
//		linkMap.put("'検査'", ToolConstants.PREF_PAGE_ID_INSPECTION);
//		linkMap.put("'辞書'", ToolConstants.PREF_PAGE_ID_DICTIONARY);
//		linkMap.put("'要求仕様書'", ToolConstants.PREF_PAGE_ID_SPEC);
//		addField(new LinkFieldEditor(
//				"<a>'検査'</a>, <a>'辞書'</a>, <a>'要求仕様書'</a>の各設定を行う",
//				linkMap, (IWorkbenchPreferenceContainer)getContainer(), parent));
	}

	/**
	 * 初期化
	 */
	public void init(IWorkbench workbench) {}
}