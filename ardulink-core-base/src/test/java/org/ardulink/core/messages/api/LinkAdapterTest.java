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

import org.ardulink.core.linkmanager.LinkManager;
import org.ardulink.core.linkmanager.LinkManager.Configurer;
import org.ardulink.core.messages.impl.DefaultOutMessageCustom;
import org.ardulink.util.URIs;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.assertEquals;

/**
 * [ardulinktitle] [ardulinkversion]
 * 
 * project Ardulink http://www.ardulink.org/
 * 
 * [adsense]
 *
 */
public class LinkAdapterTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	private LinkMessageAdapter linkMessageAdapter;

	@Before
	public void setUp() throws IOException {
		Configurer configurer = LinkManager.getInstance().getConfigurer(URIs.newURI("ardulink://dummyLink4LinkAdapter?proto=ardulink2"));
		linkMessageAdapter = new LinkMessageAdapter(configurer.newLink());
	}
	
	@Test
	public void sendingAnUnknownMessage() throws IOException {
		
		expectedException.expect(IllegalStateException.class);
		
		linkMessageAdapter.sendMessage(new OutMessage() {
		});

	}

	@Test
	public void sendingACustomMessage() throws IOException {
		
		linkMessageAdapter.sendMessage(new DefaultOutMessageCustom("one", "two"));
		
		String written = new String(DummyConnection4LinkAdapter.getWritten());

		assertEquals(written, "alp://cust/one/two\n");
	}

}
