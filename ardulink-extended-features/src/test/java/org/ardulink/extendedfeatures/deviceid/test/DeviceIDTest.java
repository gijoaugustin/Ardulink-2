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
package org.ardulink.extendedfeatures.deviceid.test;

import static org.ardulink.core.linkmanager.LinkConfig.NO_ATTRIBUTES;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.ardulink.core.AbstractListenerLink;
import org.ardulink.core.events.DefaultRplyEvent;
import org.ardulink.core.messages.api.ToDeviceMessageCustom;
import org.ardulink.core.messages.impl.DefaultToDeviceMessageCustom;
import org.ardulink.core.proto.api.MessageIdHolder;
import org.ardulink.core.virtual.VirtualLink;
import org.ardulink.extendedfeatures.deviceid.DeviceID;
import org.ardulink.util.MapBuilder;
import org.junit.Test;

/**
 * [ardulinktitle] [ardulinkversion]
 * 
 * project Ardulink http://www.ardulink.org/
 * 
 * [adsense]
 *
 */
public class DeviceIDTest {

	private final AbstractListenerLink link = new VirtualLink(NO_ATTRIBUTES) {

		private final Queue<DefaultRplyEvent> queue = new LinkedList<DefaultRplyEvent>();

		@Override
		public void sendCustomMessage(String... messages) throws IOException {
			if (messages.length == 2 && "getUniqueID".equals(messages[0])) {
				logger.info("custom message unique ID request");
				ToDeviceMessageCustom custom = addMessageIdIfNeeded(new DefaultToDeviceMessageCustom(
						messages));
				if (custom instanceof MessageIdHolder) {
					long requestUniqueID = ((MessageIdHolder) custom).getId();
					String uniqueIDSuggested = messages[1];
					// requested device ID see DeviceID class
					synchronized (queue) {
						queue.add(new DefaultRplyEvent(true, requestUniqueID,
								MapBuilder.<String, Object> newMapBuilder()
										.put("UniqueID", uniqueIDSuggested)
										.build()));
					}
				}

			}

		};

		@Override
		protected void sendRandomMessagesAndSleep() {
			synchronized (queue) {
				for (DefaultRplyEvent event : queue) {
					fireReplyReceived(event);
				}
			}
		};
	};

	@Test
	public void canCreateDeviceId() throws IOException {
		DeviceID deviceID = new DeviceID(link);
		assertThat(deviceID.getUniqueID(), notNullValue());
	}

	@Test
	public void deviceIdDoesNotChange() throws IOException {
		DeviceID deviceID = new DeviceID(link);
		assertEquals(deviceID.getUniqueID(), deviceID.getUniqueID());
	}

}
