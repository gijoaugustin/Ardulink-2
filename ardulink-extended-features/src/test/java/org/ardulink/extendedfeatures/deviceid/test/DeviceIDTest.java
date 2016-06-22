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

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.ardulink.core.AbstractListenerLink;
import org.ardulink.core.Link;
import org.ardulink.core.linkmanager.LinkManager;
import org.ardulink.extendedfeatures.deviceid.DeviceID;
import org.ardulink.util.Throwables;
import org.ardulink.util.URIs;
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

	private Link link = LinkManager.getInstance().getConfigurer(URIs.newURI("ardulink://virtual")).newLink();


	@Test
	public void getDeviceIDFirstTime() {
		
		DeviceID deviceID = new DeviceID((AbstractListenerLink)link);
		String uid1 = null;
		String uid2 = null;
		String uid3 = null;
		try {
			
			uid1 = deviceID.getUniqueID();
			uid2 = deviceID.getUniqueID();
			deviceID.clearCachedID();
			uid3 = deviceID.getUniqueID();
			
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
		
		assertThat(uid1, notNullValue());
		assertThat(uid2, notNullValue());
		assertThat(uid3, notNullValue());
		
		assertEquals(uid1, uid2);
		assertEquals(uid2, uid3);
	}
	
}
