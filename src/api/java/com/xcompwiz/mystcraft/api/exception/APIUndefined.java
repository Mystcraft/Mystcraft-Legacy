package com.xcompwiz.mystcraft.api.exception;

/**
 * Thrown in the case of an API name being unrecognized (ex. requesting 'zymbol-1')
 * @author xcompwiz
 */
public class APIUndefined extends Exception {

	/**
	 * Generated Serial UID
	 */
	private static final long serialVersionUID = 7033833326135545759L;

	public APIUndefined(String string) {
		super(string);
	}
}
