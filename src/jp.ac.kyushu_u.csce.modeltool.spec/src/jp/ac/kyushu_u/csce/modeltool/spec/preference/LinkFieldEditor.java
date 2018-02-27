package jp.ac.kyushu_u.csce.modeltool.spec.preference;

import java.util.Map;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

/**
 * 別ページへのリンクを行うためのフィールドエディタ
 *
 * @author KBK yoshimura
 */
public class LinkFieldEditor extends FieldEditor {

	private static final String PREFERENCE_NAME = "link_preference_name"; //$NON-NLS-1$

	private Link link;

	/**
	 * コンストラクタ
	 * @param labelText 出力する文字列。リンクは&lt;a&gt;,&lt;/a&gt;タグで囲む
	 * @param mapping リンク文字列とリンク先IDのマッピング。
	 * リンク文字列にはlabelTextで&lt;a&gt;,&lt;/a&gt;タグで囲んだ文字列、リンク先IDにはプリファレンスページのIDを指定する
	 * @param container プリファレンスコンテナ。{@link PreferencePage#getContainer()}の結果を指定する
	 * @param parent 親コンポジット
	 */
	public LinkFieldEditor(String labelText, Map<String, String> mapping,
			IWorkbenchPreferenceContainer container, Composite parent) {

		link = new Link(parent, SWT.NONE);
		link.setText(labelText + " ");	// セットした文字列の最後の文字が表示されないため //$NON-NLS-1$
		link.addSelectionListener(
				new LinkSelectionAdapter(mapping, container));

		init(PREFERENCE_NAME, ""); //$NON-NLS-1$
		createControl(parent);
	}

	/**
	 * リンク選択時のリスナー
	 */
	private class LinkSelectionAdapter extends SelectionAdapter {

		private IWorkbenchPreferenceContainer container;

		private Map<String, String> mapping;

		public LinkSelectionAdapter(Map<String, String> mapping, IWorkbenchPreferenceContainer container) {
			this.mapping = mapping;
			this.container = container;
		}

		/**
		 * リンクが選択された場合の処理<br>
		 * 選択されたプリファレンスページへ移動する
		 */
		public void widgetSelected(SelectionEvent e) {
			if (mapping.containsKey(e.text)) {
				container.openPage(mapping.get(e.text), null);
			}
		}
	}

	protected void adjustForNumColumns(int numColumns) {
		GridData gd = (GridData)link.getLayoutData();
		gd.horizontalSpan = numColumns;
	}

	protected void doFillIntoGrid(Composite parent, int numColumns) {
		GridData gd = new GridData();
		gd.horizontalSpan = numColumns;
		link.setLayoutData(gd);
	}

	protected void doLoad() {
	}

	protected void doLoadDefault() {
	}

	protected void doStore() {
	}

	public int getNumberOfControls() {
		return 1;
	}

}
