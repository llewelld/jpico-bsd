/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

public class Preconditions {

	public static byte[] checkNotNullOrEmpty(byte[] b) {
		checkNotNull(b);
		checkArgument(b.length > 0);
		return b;
	}
	
	public static byte[] checkNotNullOrEmpty(byte[] b, String errorMessage) {
		checkNotNull(b, errorMessage);
		checkArgument(b.length > 0, errorMessage);
		return b;
	}
	
	public static byte[] checkNotNullOrEmpty(
			byte[] b,
			String errorMessageTemplate,
			Object... errorMessageArgs) {
		checkNotNull(b, errorMessageTemplate, errorMessageArgs);
		checkArgument(b.length > 0, errorMessageTemplate, errorMessageArgs);
		return b;
	}
	
	public static URI checkNotNullOrEmpty(URI uri) {
		checkNotNull(uri);
		checkArgument(uri.toString().length() > 0);
		return uri;
	}
	
	public static URI checkNotNullOrEmpty(URI uri, String errorMessage) {
		checkNotNull(uri, errorMessage);
		checkArgument(uri.toString().length() > 0, errorMessage);
		return uri;
	}
	
	public static URI checkNotNullOrEmpty(
			URI uri,
			String errorMessageTemplate,
			Object... errorMessageArgs) {
		checkNotNull(uri, errorMessageTemplate, errorMessageArgs);
		checkArgument(uri.toString().length() > 0, errorMessageTemplate, errorMessageArgs);
		return uri;
	}
	
	public static String checkNotNullOrEmpty(String s) {
		checkNotNull(s);
		checkArgument(s.length() > 0);
		return s;
	}
	
	public static String checkNotNullOrEmpty(String s, String errorMessage) {
		checkNotNull(s, errorMessage);
		checkArgument(s.length() > 0, errorMessage);
		return s;
	}
	
	public static String checkNotNullOrEmpty(
			String s,
			String errorMessageTemplate,
			Object... errorMessageArgs) {
		checkNotNull(s, errorMessageTemplate, errorMessageArgs);
		checkArgument(s.length() > 0, errorMessageTemplate, errorMessageArgs);
		return s;
	}
}
