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

package org.ardulink.core.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import org.ardulink.core.Pin;
import org.ardulink.core.events.PinValueChangedEvent;

/**
 * [ardulinktitle] [ardulinkversion]
 * 
 * project Ardulink http://www.ardulink.org/
 * 
 * [adsense]
 *
 */
public class EventMatchers {

	public static PinValueChangedEventMatcher eventFor(Pin pin) {
		return new PinValueChangedEventMatcher(pin);
	}

	public static class PinValueChangedEventMatcher extends
			TypeSafeMatcher<PinValueChangedEvent> {

		private final Pin pin;
		private Object value;

		public PinValueChangedEventMatcher(Pin pin) {
			this.pin = pin;
		}

		@Override
		public void describeTo(Description description) {
			pinState(description, pin, value);
		}

		@Override
		protected void describeMismatchSafely(PinValueChangedEvent event,
				Description description) {
			pinState(description.appendText(" was "), event.getPin(),
					event.getValue());
		}

		private Description pinState(Description description, Pin pin,
				Object value) {
			return description.appendText(pin.getClass().getSimpleName())
					.appendText("[").appendValue(pin.pinNum()).appendText("]=")
					.appendValue(value);
		}

		@Override
		protected boolean matchesSafely(PinValueChangedEvent event) {
			return pinsAreEqual(event) && valuesAreEqual(event);
		}

		private boolean valuesAreEqual(PinValueChangedEvent event) {
			return event.getValue().equals(value);
		}

		private boolean pinsAreEqual(PinValueChangedEvent event) {
			return event.getPin().pinNum() == pin.pinNum();
		}

		public PinValueChangedEventMatcher withValue(int value) {
			this.value = value;
			return this;
		}

		public PinValueChangedEventMatcher withValue(boolean value) {
			this.value = value;
			return this;
		}

	}

}
