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

package org.ardulink.core.events;

/**
 * [ardulinktitle] [ardulinkversion]
 * 
 * project Ardulink http://www.ardulink.org/
 * 
 * [adsense]
 *
 */
public class DefaultRplyEvent implements RplyEvent {

	private boolean ok;
	private long id;

	public DefaultRplyEvent(boolean ok, long id) {
		this.ok = ok;
		this.id = id;
	}

	@Override
	public boolean isOk() {
		return ok;
	}

	@Override
	public long getId() {
		return id;
	}

}
