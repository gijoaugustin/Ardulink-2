package com.github.pfichtner.ardulink.core.proto.impl;

import static com.github.pfichtner.ardulink.core.Pin.analogPin;
import static com.github.pfichtner.ardulink.core.Pin.digitalPin;
import static com.github.pfichtner.ardulink.core.Pin.Type.ANALOG;
import static com.github.pfichtner.ardulink.core.Pin.Type.DIGITAL;
import static com.github.pfichtner.ardulink.core.proto.impl.ALProtoBuilder.alpProtocolMessage;
import static com.github.pfichtner.ardulink.core.proto.impl.ALProtoBuilder.ALPProtocolKey.ANALOG_PIN_READ;
import static com.github.pfichtner.ardulink.core.proto.impl.ALProtoBuilder.ALPProtocolKey.CHAR_PRESSED;
import static com.github.pfichtner.ardulink.core.proto.impl.ALProtoBuilder.ALPProtocolKey.DIGITAL_PIN_READ;
import static com.github.pfichtner.ardulink.core.proto.impl.ALProtoBuilder.ALPProtocolKey.POWER_PIN_INTENSITY;
import static com.github.pfichtner.ardulink.core.proto.impl.ALProtoBuilder.ALPProtocolKey.POWER_PIN_SWITCH;
import static com.github.pfichtner.ardulink.core.proto.impl.ALProtoBuilder.ALPProtocolKey.START_LISTENING_ANALOG;
import static com.github.pfichtner.ardulink.core.proto.impl.ALProtoBuilder.ALPProtocolKey.START_LISTENING_DIGITAL;
import static com.github.pfichtner.ardulink.core.proto.impl.ALProtoBuilder.ALPProtocolKey.STOP_LISTENING_ANALOG;
import static com.github.pfichtner.ardulink.core.proto.impl.ALProtoBuilder.ALPProtocolKey.STOP_LISTENING_DIGITAL;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.System.arraycopy;
import static org.zu.ardulink.util.Integers.tryParse;
import static org.zu.ardulink.util.Preconditions.checkState;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.pfichtner.ardulink.core.Pin;
import com.github.pfichtner.ardulink.core.proto.api.Protocol;
import com.github.pfichtner.ardulink.core.proto.api.ToArduinoCharEvent;
import com.github.pfichtner.ardulink.core.proto.api.ToArduinoPinEvent;
import com.github.pfichtner.ardulink.core.proto.api.ToArduinoStartListening;
import com.github.pfichtner.ardulink.core.proto.api.ToArduinoStopListening;
import com.github.pfichtner.ardulink.core.proto.impl.ALProtoBuilder.ALPProtocolKey;

public class ArdulinkProtocolN implements Protocol {

	private static final ArdulinkProtocolN instance = new ArdulinkProtocolN();

	public static final byte[] READ_SEPARATOR = "\n".getBytes();

	private static final Pattern pattern = Pattern
			.compile("alp:\\/\\/([a-z]+)/([\\d]+)/([\\d]+)");

	private static final String NAME = "ardulink";

	public String getName() {
		return NAME;
	};

	@Override
	public byte[] getSeparator() {
		return READ_SEPARATOR;
	}

	public static Protocol instance() {
		return instance;
	}

	@Override
	public byte[] toArduino(ToArduinoStartListening startListeningEvent) {
		Pin pin = startListeningEvent.pin;
		if (startListeningEvent.pin.is(ANALOG)) {
			return toBytes(alpProtocolMessage(START_LISTENING_ANALOG).forPin(
					pin.pinNum()).withoutValue());
		}
		if (startListeningEvent.pin.is(DIGITAL)) {
			return toBytes(alpProtocolMessage(START_LISTENING_DIGITAL).forPin(
					pin.pinNum()).withoutValue());
		}
		throw new IllegalStateException("Illegal Pin type "
				+ startListeningEvent.pin);
	}

	@Override
	public byte[] toArduino(ToArduinoStopListening stopListeningEvent) {
		Pin pin = stopListeningEvent.pin;
		if (stopListeningEvent.pin.is(ANALOG)) {
			return toBytes(alpProtocolMessage(STOP_LISTENING_ANALOG).forPin(
					pin.pinNum()).withoutValue());
		}
		if (stopListeningEvent.pin.is(DIGITAL)) {
			return toBytes(alpProtocolMessage(STOP_LISTENING_DIGITAL).forPin(
					pin.pinNum()).withoutValue());
		}
		throw new IllegalStateException("Illegal Pin type "
				+ stopListeningEvent.pin);
	}

	@Override
	public byte[] toArduino(ToArduinoPinEvent pinEvent) {
		if (pinEvent.pin.is(ANALOG)) {
			return toBytes(alpProtocolMessage(POWER_PIN_INTENSITY).forPin(
					pinEvent.pin.pinNum()).withValue((Integer) pinEvent.value));
		}
		if (pinEvent.pin.is(DIGITAL)) {
			return toBytes(alpProtocolMessage(POWER_PIN_SWITCH).forPin(
					pinEvent.pin.pinNum()).withState((Boolean) pinEvent.value));
		}
		throw new IllegalStateException("Illegal Pin type " + pinEvent.pin);
	}

	@Override
	public byte[] toArduino(ToArduinoCharEvent charEvent) {
		return toBytes(alpProtocolMessage(CHAR_PRESSED).withValue(
				"chr" + charEvent.keychar + "cod" + charEvent.keycode + "loc"
						+ charEvent.keylocation + "mod"
						+ charEvent.keymodifiers + "mex"
						+ charEvent.keymodifiersex));
	}

	@Override
	public FromArduino fromArduino(byte[] bytes) {
		Matcher matcher = pattern.matcher(new String(bytes));
		checkState(matcher.matches() && matcher.groupCount() == 3, "%s",
				new String(bytes));
		ALPProtocolKey key = ALPProtocolKey.fromString(matcher.group(1));
		Integer pin = tryParse(matcher.group(2));
		Integer value = tryParse(matcher.group(3));
		checkState(key != null && pin != null && value != null,
				"key %s pin %s value %s", key, pin, value);
		if (key == ANALOG_PIN_READ) {
			return new FromArduinoPinStateChanged(analogPin(pin), value);
		} else if (key == DIGITAL_PIN_READ) {
			return new FromArduinoPinStateChanged(digitalPin(pin),
					toBoolean(value));
		} else if (key == POWER_PIN_SWITCH) {
			return new ToArduinoChangePinState(digitalPin(pin),
					toBoolean(value));
		} else if (key == POWER_PIN_INTENSITY) {
			return new ToArduinoChangePinState(analogPin(pin), value);
		}
		throw new IllegalStateException(key + " " + new String(bytes));
	}

	private static Boolean toBoolean(Integer value) {
		return value.intValue() == 1 ? TRUE : FALSE;
	}

	private static byte[] toBytes(String message) {
		byte[] bytes = new byte[message.length() + READ_SEPARATOR.length];
		byte[] msgBytes = message.getBytes();
		arraycopy(msgBytes, 0, bytes, 0, msgBytes.length);
		arraycopy(READ_SEPARATOR, 0, bytes, msgBytes.length,
				READ_SEPARATOR.length);
		return bytes;
	}

}