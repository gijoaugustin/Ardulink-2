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
package org.ardulink.extendedfeatures.deviceid;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.ardulink.util.Preconditions.checkNotNull;
import static org.ardulink.util.Preconditions.checkState;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.ardulink.core.AbstractListenerLink;
import org.ardulink.core.events.RplyEvent;
import org.ardulink.core.events.RplyListener;
import org.ardulink.extendedfeatures.XFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

;

/**
 * [ardulinktitle] [ardulinkversion]
 * 
 * project Ardulink http://www.ardulink.org/ This XFeature is able to retrieve
 * the Unique ID from an Arduino based board. If arduino doesn't have an unique
 * id this class suggests an id. Then in the reply message between parameters
 * DeviceID searches for a UniqueID parameter.
 * 
 * [adsense]
 *
 */
public class DeviceID implements XFeature, RplyListener {

	private static final Logger logger = LoggerFactory
			.getLogger(DeviceID.class);

	private final AbstractListenerLink link;
	private final long timeout;
	private final TimeUnit timeUnit;

	/**
	 * Cached unique ID obtained from the device (Arduino based board)
	 */
	private String uniqueID;

	/**
	 * Message ID this class waits for from the device
	 */
	private Long messageReplyID;

	private final Lock lock = new ReentrantLock(false);
	private final Condition condition = lock.newCondition();

	private RplyEvent event;

	public DeviceID(AbstractListenerLink link) {
		this(link, 5, SECONDS);
	}

	public DeviceID(AbstractListenerLink link, long timeout, TimeUnit timeUnit) {
		checkNotNull(link, "DeviceID class needs for a not null Link instance");
		checkState(timeout > 0, "Timeout has to be greater than 0. It is: %s",
				timeout);

		this.link = link;
		this.timeout = timeout;
		this.timeUnit = timeUnit;
	}

	public String getUniqueID() throws IOException {
		if (uniqueID == null) {
			initUniqueID();
		}
		return uniqueID;
	}

	private void initUniqueID() throws IOException {
		if (messageReplyID == null) {
			messageReplyID = link.nextLocalId();
			link.addRplyListener(this);

			link.sendCustomMessage("getUniqueID", UUID.randomUUID().toString());
			waitForRplyEvent();
			link.removeRplyListener(this);

			checkState(event.hasParameters(),
					"Returned message hasn't parameters");

			uniqueID = (String) event.getParameters().get("UniqueID");
			checkNotNull(uniqueID,
					"Returned message hasn't an UniqueID parameter");
		}
	}

	private void waitForRplyEvent() {
		lock.lock();
		try {
			logger.debug("Wait for {}", messageReplyID);
			try {
				checkState(
						condition.await(timeout, timeUnit),
						"No response received while waiting for messageId %s within %s %s",
						messageReplyID, timeout, timeUnit);
				long idReceived = checkNotNull(event,
						"No event received but condition signalled").getId();
				checkState(messageReplyID == event.getId(),
						"Waited for %s but got %s", messageReplyID,
						event.getId());
				checkState(event.isOk(), "Response status is not ok");
				logger.debug("Condition wait {}", idReceived);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void rplyReceived(RplyEvent e) {

		if (e.getId() == messageReplyID) {

			this.event = e;

			lock.lock();
			try {
				condition.signal();
			} finally {
				lock.unlock();
			}
		}
	}

}
