package jp.ac.kyushu_u.csce.modeltool.base.utility;

/**
 * ファイルアクセス例外クラス
 *
 * @author KBK yoshimura
 */
public class FileAccessException extends Exception {

	private static final long serialVersionUID = 1L;

	public FileAccessException() {
		super();
	}

	public FileAccessException(String message) {
		super(message);
	}

	public FileAccessException(Throwable cause) {
		super(cause);
//		setStackTrace(cause.getStackTrace());
	}

	public FileAccessException(String message, Throwable cause) {
		super(message, cause);
//		setStackTrace(cause.getStackTrace());
	}
}
