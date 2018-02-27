package jp.ac.kyushu_u.csce.modeltool.base.utility;

import jp.ac.kyushu_u.csce.modeltool.base.ModelToolBasePlugin;

import org.eclipse.core.runtime.preferences.IScopeContext;

/**
 * スコープ・ユーティリティクラス
 */
public class ScopeUtility {

	/**
	 * インスタンス・スコープの取得
	 * @return インスタンス・スコープ
	 */
	public static IScopeContext getInstanceScope() {
		return ModelToolBasePlugin.getInstanceScope();
	}
}
