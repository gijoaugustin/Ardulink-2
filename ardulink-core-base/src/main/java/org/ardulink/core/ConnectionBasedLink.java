/**
Copyright 2013 project Ardulink http://www.ardulink.org/
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.ardulink.core;

import java.io.IOException;

import org.ardulink.core.Pin.AnalogPin;
import org.ardulink.core.Pin.DigitalPin;
import org.ardulink.core.messages.api.ToDeviceMessageStartListening;
import org.ardulink.core.messages.api.ToDeviceMessageStopListening;
import org.ardulink.core.messages.impl.DefaultToDeviceMessageCustom;
import org.ardulink.core.messages.impl.DefaultToDeviceMessageKeyPress;
import org.ardulink.core.messages.impl.DefaultToDeviceMessageNoTone;
import org.ardulink.core.messages.impl.DefaultToDeviceMessagePinStateChange;
import org.ardulink.core.messages.impl.DefaultToDeviceMessageStartListening;
import org.ardulink.core.messages.impl.DefaultToDeviceMessageStopListening;
import org.ardulink.core.messages.impl.DefaultToDeviceMessageTone;
import org.ardulink.core.proto.api.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [ardulinktitle] [ardulinkversion]
 * 
 * project Ardulink http://www.ardulink.org/
 * 
 * [adsense]
 *
 */
public class ConnectionBasedLink extends AbstractConnectionBasedLink {

	private static final Logger logger = LoggerFactory
			.getLogger(ConnectionBasedLink.class);

	public ConnectionBasedLink(Connection connection, Protocol protocol) {
		super(connection, protocol);
	}

	@Override
	public void startListening(Pin pin) throws IOException {
		logger.info("Starting listening on pin {}", pin);
		ToDeviceMessageStartListening startListening = addMessageIdIfNeeded(new DefaultToDeviceMessageStartListening(
				pin));
		send(getProtocol().toDevice(startListening));
	}

	@Override
	public void stopListening(Pin pin) throws IOException {
		ToDeviceMessageStopListening stopListening = addMessageIdIfNeeded(new DefaultToDeviceMessageStopListening(
				pin));
		send(getProtocol().toDevice(stopListening));
		logger.info("Stopped listening on pin {}", pin);
	}

	@Override
	public void switchAnalogPin(AnalogPin analogPin, int value)
			throws IOException {
		send(analogPin, value);
	}

	@Override
	public void switchDigitalPin(DigitalPin digitalPin, boolean value)
			throws IOException {
		send(digitalPin, value);
	}

	@Override
	public void sendKeyPressEvent(char keychar, int keycode, int keylocation,
			int keymodifiers, int keymodifiersex) throws IOException {
		send(getProtocol().toDevice(addMessageIdIfNeeded(
				new DefaultToDeviceMessageKeyPress(keychar, keycode,
						keylocation, keymodifiers, keymodifiersex))));
	}

	@Override
	public void sendTone(Tone tone) throws IOException {
		send(getProtocol().toDevice(addMessageIdIfNeeded(new DefaultToDeviceMessageTone(tone))));
	}

	@Override
	public void sendNoTone(AnalogPin analogPin) throws IOException {
		send(getProtocol().toDevice(addMessageIdIfNeeded(new DefaultToDeviceMessageNoTone(analogPin))));
	}

	@Override
	public void sendCustomMessage(String... messages) throws IOException {
		send(getProtocol().toDevice(
				addMessageIdIfNeeded(new DefaultToDeviceMessageCustom(messages))));
	}

	private void send(AnalogPin pin, int value) throws IOException {
		send(getProtocol().toDevice(addMessageIdIfNeeded(new DefaultToDeviceMessagePinStateChange(pin, value))));
	}

	private void send(DigitalPin pin, boolean value) throws IOException {
		send(getProtocol().toDevice(addMessageIdIfNeeded(new DefaultToDeviceMessagePinStateChange(pin, value))));
	}

	private void send(byte[] bytes) throws IOException {
		getConnection().write(bytes);
	}
	
}
