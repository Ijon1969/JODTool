package jp.ac.kyushu_u.csce.modeltool.vdmEditor.editor;

import org.eclipse.jface.text.*;
import org.eclipse.jface.text.rules.*;

/**
 * 使用していません　削除予定
 * @author KBK yoshimura
 * @deprecated
 */
public class VDMTagScanner extends RuleBasedScanner {

	public VDMTagScanner(ColorManager manager) {
		IToken string =
			new Token(
				new TextAttribute(manager.getColor(IVDMColorConstants.STRING)));

		IRule[] rules = new IRule[3];

		// Add rule for double quotes
		rules[0] = new SingleLineRule("\"", "\"", string, '\\'); //$NON-NLS-1$ //$NON-NLS-2$
		// Add a rule for single quotes
		rules[1] = new SingleLineRule("'", "'", string, '\\'); //$NON-NLS-1$ //$NON-NLS-2$
		// Add generic whitespace rule.
//		rules[2] = new WhitespaceRule(new VDMWhitespaceDetector());

		setRules(rules);
	}
}
