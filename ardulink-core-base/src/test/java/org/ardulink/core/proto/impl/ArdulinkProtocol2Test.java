package org.ardulink.core.proto.impl;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;

import org.ardulink.core.messages.api.FromDeviceMessage;
import org.ardulink.core.messages.api.FromDeviceMessageCustom;
import org.ardulink.core.messages.api.FromDeviceMessageReply;
import org.ardulink.core.proto.api.Protocol;
import org.junit.Test;

public class ArdulinkProtocol2Test {

	private final Protocol sut = ArdulinkProtocol2.instance();

	@Test
	public void ardulinkProtocol2ReceiveCustomEvent() {
		String message = "alp://cevnt/foo=w/some=42";

		FromDeviceMessage fromDevice = sut.fromDevice(message.getBytes());

		assertThat(fromDevice, instanceOf(FromDeviceMessageCustom.class));
		assertEquals(((FromDeviceMessageCustom) fromDevice).getValue()
				.toString(), "foo=w/some=42");

	}

	@Test
	public void ardulinkProtocol2ReceiveRply() {
		String message = "alp://rply/ok?id=1&UniqueID=456-2342-2342&ciao=boo";

		FromDeviceMessage fromDevice = sut.fromDevice(message.getBytes());

		assertThat(fromDevice, instanceOf(FromDeviceMessageReply.class));
		FromDeviceMessageReply fromDeviceMessageReply = (FromDeviceMessageReply) fromDevice;
		assertTrue(fromDeviceMessageReply.isOk());
		assertEquals(fromDeviceMessageReply.getId(), 1);
		assertEquals(fromDeviceMessageReply.getParameters().get("UniqueID"),
				"456-2342-2342");
		assertEquals(fromDeviceMessageReply.getParameters().get("ciao"), "boo");
	}

}
