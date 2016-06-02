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

import org.ardulink.core.Link;
import org.ardulink.core.events.AnalogPinValueChangedEvent;
import org.ardulink.core.events.DigitalPinValueChangedEvent;
import org.ardulink.core.events.EventListener;
import org.ardulink.core.events.RawEvent;
import org.ardulink.core.events.RawListener;
import org.ardulink.core.events.RplyEvent;
import org.ardulink.core.events.RplyListener;

/**
 * [ardulinktitle] [ardulinkversion]
 * 
 * project Ardulink http://www.ardulink.org/
 * 
 * [adsense]
 *
 */
public class LinkMessageAdapter implements RawListener, RplyListener, EventListener {
	
	private final Link link;

	
	// TODO I'm here. I have to implements sendMessage and eventManagement...
	// implement methods for listener is actually wrong
	public LinkMessageAdapter(Link link) {
		this.link = link;
	}

	@Override
	public void stateChanged(AnalogPinValueChangedEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateChanged(DigitalPinValueChangedEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rplyReceived(RplyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rawReceived(RawEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	

}
