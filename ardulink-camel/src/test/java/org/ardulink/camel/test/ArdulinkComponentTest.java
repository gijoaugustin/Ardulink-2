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
package org.ardulink.camel.test;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.ardulink.camel.ArdulinkEndpoint;
import org.ardulink.camel.test.translate.DummyToArdulinkMessageProcessor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * [ardulinktitle] [ardulinkversion]
 * 
 * project Ardulink http://www.ardulink.org/
 * 
 * [adsense]
 *
 */
public class ArdulinkComponentTest {

	private static final String virtualLinkURI = "ardulink://virtual";

	@Before
	public void setUp() throws URISyntaxException {
	}
	
	@After
	public void tearDown() throws IOException {
	}

	@Test
	public void canGetEndpoint() throws Exception {
		CamelContext context = new DefaultCamelContext();
		
		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() {
				from("direct:in").to(virtualLinkURI);
			}
		});
		ArdulinkEndpoint endpoint = context.getEndpoint(virtualLinkURI, ArdulinkEndpoint.class);
		
		assertNotNull(endpoint);
	}
	
	@Test
	public void canProcessAMessage() throws Exception {
		
		CamelContext context = new DefaultCamelContext();
		ProducerTemplate template = context.createProducerTemplate();
		
		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() {
				from("direct:in").bean(new DummyToArdulinkMessageProcessor()).to(virtualLinkURI);
			}
		});

		context.start();
		template.sendBody("direct:in", "send Custom Message");
		template.sendBody("direct:in", "this should do nothing");
		context.stop();
		
		// TODO modify a little virtual link to save in a static array messages and the add assertThat statements for this test.
	}
}
