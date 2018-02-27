package jp.ac.kyushu_u.csce.modeltool.spec.speceditor;

import jp.ac.kyushu_u.csce.modeltool.base.constant.ToolConstants;

import org.eclipse.swt.widgets.Composite;

/**
 * 仕様書エディターの内部テキストエディタ
 * @author KBK yoshimura
 */
public class InternalTextEditor extends AbstractInternalTextEditor {

	/** エンコーディング退避 */
	private String encoding;

	/** エンコーディング UTF-8 */
	private static final String ENCODING = ToolConstants.ENCODING_UTF_8;

	/**
	 * コンストラクタ
	 */
	public InternalTextEditor() {
		super();
	}

	/**
	 * エディターパート作成
	 */
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
	}

	@Override
	protected void installEncodingSupport() {
		super.installEncodingSupport();

		// 文字コードの設定
		// ※ 非同期スレッド処理にしないとエラーが発生
		getSite().getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				encoding = fEncodingSupport.getEncoding();
				fEncodingSupport.setEncoding(ENCODING);
			}
		});
	}

	@Override
	public void dispose() {

		// エンコーディングを元に戻す
		fEncodingSupport.setEncoding(encoding);

		super.dispose();
	}
}
