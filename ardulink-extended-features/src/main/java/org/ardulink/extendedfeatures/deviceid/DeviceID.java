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

import java.io.IOException;

import org.ardulink.core.Link;
import org.ardulink.extendedfeatures.XFeature;

/**
 * [ardulinktitle] [ardulinkversion]
 * 
 * project Ardulink http://www.ardulink.org/
 * 
 * [adsense]
 *
 */
public class DeviceID implements XFeature {
	
	private Link link;
	
	/**
	 * cached unique id obtained from the device (Arduino based board)
	 */
	private String uniqueID;

	public DeviceID(Link link) {
		this.link = link;
	}
	
	public String getUniqueID() throws IOException {
		
		if(uniqueID == null) {
			initUniqueID();
		}
		return uniqueID;
	}

	public void clearCachedID() {
		uniqueID = null;
	}
	
	private void initUniqueID() throws IOException {
		link.sendCustomMessage("getUniqueID");
	}

}
