package org.ardulink.util;

import java.net.URI;
import java.net.URISyntaxException;

public final class URIs {

	private URIs() {
		super();
	}

	public static URI newURI(String asciiString) {
		try {
			return new URI(asciiString);
		} catch (URISyntaxException e) {
			throw Throwables.propagate(e);
		}
	}

}
