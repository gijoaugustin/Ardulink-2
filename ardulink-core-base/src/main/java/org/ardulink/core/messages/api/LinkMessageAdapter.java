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
package org.ardulink.core.messages.api;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.ardulink.core.Link;
import org.ardulink.core.Pin;
import org.ardulink.core.Pin.AnalogPin;
import org.ardulink.core.Pin.DigitalPin;
import org.ardulink.core.Tone;
import org.ardulink.core.events.AnalogPinValueChangedEvent;
import org.ardulink.core.events.DigitalPinValueChangedEvent;
import org.ardulink.core.events.EventListener;
import org.ardulink.core.events.PinValueChangedEvent;
import org.ardulink.core.events.RawEvent;
import org.ardulink.core.events.RawListener;
import org.ardulink.core.events.RplyEvent;
import org.ardulink.core.events.RplyListener;
import org.ardulink.core.messages.events.api.InMessageEvent;
import org.ardulink.core.messages.events.api.InMessageListener;
import org.ardulink.core.messages.events.impl.DefaultInMessageEvent;
import org.ardulink.core.messages.impl.DefaultInMessagePinStateChanged;
import org.ardulink.core.messages.impl.DefaultInMessageRaw;
import org.ardulink.core.messages.impl.DefaultInMessageReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.ardulink.util.Preconditions.checkNotNull;

/**
 * [ardulinktitle] [ardulinkversion]
 * 
 * project Ardulink http://www.ardulink.org/
 * 
 * [adsense]
 *
 */
public class LinkMessageAdapter {
	
	private final Link link;
	private final LinkListener linkListener;

	private static final Logger logger = LoggerFactory.getLogger(LinkMessageAdapter.class);
	
	public LinkMessageAdapter(Link link) throws IOException {
		this.link = link;
		this.linkListener = new LinkListener();
		link.addListener(linkListener);
		link.addRawListener(linkListener);
		link.addRplyListener(linkListener);
	}

	
	public LinkMessageAdapter addInMessageListener(InMessageListener listener) throws IOException {
		checkNotNull(listener, "listener must not be null");
		linkListener.addInMessageListener(listener);
		return this;
	}

	public LinkMessageAdapter removeInMessageListener(InMessageListener listener) throws IOException {
		checkNotNull(listener, "listener must not be null");
		linkListener.removeInMessageListener(listener);
		return this;
	}

	
	public void sendMessage(OutMessage message) throws IOException {
		
		checkNotNull(message, "OutMessage must not be null");
		
		if(message instanceof OutMessageCustom) {
			OutMessageCustom cMessage = (OutMessageCustom)message;
			sendCustomMessage(cMessage.getMessages());
		} else if(message instanceof OutMessageKeyPress) {
			OutMessageKeyPress cMessage = (OutMessageKeyPress)message;
			sendKeyPressEvent(cMessage.getKeychar(), cMessage.getKeycode(), cMessage.getKeylocation(), cMessage.getKeymodifiers(), cMessage.getKeymodifiersex());
		} else if(message instanceof OutMessagePinEvent) {
			OutMessagePinEvent cMessage = (OutMessagePinEvent)message;
			Pin pin = cMessage.getPin();
			if(pin.is(Pin.Type.ANALOG)) {
				switchAnalogPin((Pin.AnalogPin)pin, (Integer)cMessage.getValue());
			} else { // DIGITAL
				switchDigitalPin((Pin.DigitalPin)pin, (Boolean)cMessage.getValue());
			}
		} else if(message instanceof OutMessageStartListening) {
			OutMessageStartListening cMessage = (OutMessageStartListening)message;
			startListening(cMessage.getPin());
		} else if(message instanceof OutMessageStopListening) {
			OutMessageStopListening cMessage = (OutMessageStopListening)message;
			stopListening(cMessage.getPin());
		} else if(message instanceof OutMessageTone) {
			OutMessageTone cMessage = (OutMessageTone)message;
			sendTone(cMessage.getTone());
		} else if(message instanceof OutMessageNoTone) {
			OutMessageNoTone cMessage = (OutMessageNoTone)message;
			sendNoTone(cMessage.getAnalogPin());
		} else {
			throw new IllegalStateException(String.format("OutMessage type don't supported. %s", message.getClass().getCanonicalName()));
		}
		
	}

	
	private void sendCustomMessage(String... messages) throws IOException {
		link.sendCustomMessage(messages);
	}
	
	private void sendKeyPressEvent(char keychar, int keycode, int keylocation, int keymodifiers, int keymodifiersex)
			throws IOException {
		link.sendKeyPressEvent(keychar, keycode, keylocation, keymodifiers, keymodifiersex);
	}

	private void switchAnalogPin(AnalogPin analogPin, int value) throws IOException {
		link.switchAnalogPin(analogPin, value);
	}

	private void switchDigitalPin(DigitalPin digitalPin, boolean value) throws IOException {
		link.switchDigitalPin(digitalPin, value);
	}

	private void startListening(Pin pin) throws IOException {
		link.startListening(pin);
	}

	private void stopListening(Pin pin) throws IOException {
		link.stopListening(pin);
	}

	private void sendTone(Tone tone) throws IOException {
		link.sendTone(tone);
	}

	private void sendNoTone(AnalogPin analogPin) throws IOException {
		link.sendNoTone(analogPin);
	}


	private class LinkListener implements RawListener, RplyListener, EventListener {
		
		private final List<InMessageListener> inMessageListeners = new CopyOnWriteArrayList<InMessageListener>();
		

		@Override
		public void stateChanged(AnalogPinValueChangedEvent event) {
			stateChanged((PinValueChangedEvent)event);
		}

		@Override
		public void stateChanged(DigitalPinValueChangedEvent event) {
			stateChanged((PinValueChangedEvent)event);
		}

		private void stateChanged(PinValueChangedEvent event) {
			InMessage inMessage = new DefaultInMessagePinStateChanged(event.getPin(), event.getValue());
			InMessageEvent inMessageEvent = new DefaultInMessageEvent(inMessage);

			fireInMessageReceived(inMessageEvent);
		}

		@Override
		public void rplyReceived(RplyEvent event) {
			InMessage inMessage = new DefaultInMessageReply(event.isOk(), event.getId());
			InMessageEvent inMessageEvent = new DefaultInMessageEvent(inMessage);

			fireInMessageReceived(inMessageEvent);
		}

		@Override
		public void rawReceived(RawEvent event) {
			InMessage inMessage = new DefaultInMessageRaw(event.getValue());
			InMessageEvent inMessageEvent = new DefaultInMessageEvent(inMessage);

			fireInMessageReceived(inMessageEvent);
		}
		
		public void addInMessageListener(InMessageListener listener) throws IOException {
			this.inMessageListeners.add(listener);
		}

		public void removeInMessageListener(InMessageListener listener) throws IOException {
			this.inMessageListeners.remove(listener);
		}
		
		public void fireInMessageReceived(InMessageEvent event) {
			for (InMessageListener listener : this.inMessageListeners) {
				try {
					listener.inMessageReceived(event);
				} catch (Exception e) {
					logger.error("InMessageListener {} failure", listener, e);
				}
			}
		}
	}
	

}
