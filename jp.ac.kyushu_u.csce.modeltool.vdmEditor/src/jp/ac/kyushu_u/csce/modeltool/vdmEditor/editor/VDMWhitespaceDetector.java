package jp.ac.kyushu_u.csce.modeltool.vdmEditor.editor;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

/**
 * 使用していません　削除予定
 * @author KBK yoshimura
 * @deprecated
 */
public class VDMWhitespaceDetector implements IWhitespaceDetector {

	public boolean isWhitespace(char c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}
}
