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

import java.io.Closeable;
import java.io.IOException;

import org.ardulink.core.Pin.AnalogPin;
import org.ardulink.core.Pin.DigitalPin;
import org.ardulink.core.events.EventListener;
import org.ardulink.core.events.CustomListener;
import org.ardulink.core.events.RplyListener;

/**
 * [ardulinktitle] [ardulinkversion]
 * 
 * project Ardulink http://www.ardulink.org/
 * 
 * [adsense]
 *
 */
public interface Link extends Closeable {

	Link addListener(EventListener listener) throws IOException;

	Link removeListener(EventListener listener) throws IOException;

	Link addRplyListener(RplyListener listener) throws IOException;

	Link removeRplyListener(RplyListener listener) throws IOException;

	Link addCustomListener(CustomListener listener) throws IOException;

	Link removeCustomListener(CustomListener listener) throws IOException;

	void startListening(Pin pin) throws IOException;

	void stopListening(Pin pin) throws IOException;

	void switchAnalogPin(AnalogPin analogPin, int value) throws IOException;

	void switchDigitalPin(DigitalPin digitalPin, boolean value)
			throws IOException;

	void sendKeyPressEvent(char keychar, int keycode, int keylocation,
			int keymodifiers, int keymodifiersex) throws IOException;

	void sendTone(Tone tone) throws IOException;

	void sendNoTone(AnalogPin analogPin) throws IOException;

	void sendCustomMessage(String... messages) throws IOException;

}