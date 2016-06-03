package org.ardulink.mail.camel;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.ardulink.core.Pin.analogPin;
import static org.ardulink.util.MapBuilder.newMapBuilder;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.ws.rs.core.UriBuilder;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.ardulink.core.Link;
import org.ardulink.core.convenience.LinkDelegate;
import org.ardulink.core.convenience.Links;
import org.ardulink.util.Joiner;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class RestIntegrationTest {

	private static final String mockURI = "ardulink://mock";

	private Link link;

	@Before
	public void setup() throws URISyntaxException, Exception {
		link = Links.getLink(new URI(mockURI));
	}

	@After
	public void tearDown() throws IOException {
		link.close();
	}

	@Test
	public void setValue() throws Exception {
		CamelContext context = new DefaultCamelContext();
		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() {
				restConfiguration().component("restlet").host("localhost")
						.port(12345);
				from("rest:get:ardulink0/analog/{pin}/{value}")
						.setBody()
						// .simple("select * from user where id = ${header.userId}")
						.simple("usedScenario")
						.to(makeURI(
								mockURI,
								newMapBuilder().put("scenario.usedScenario",
										"A12=42").build()));

			}
		});
		context.start();

		int pin = 12;
		int value = 42;
		ClientResponse resp = setValueViaRest(pin, value);
		assertThat(resp.getStatus(), is(200));
		assertThat(resp.getEntity(String.class),
				is("SwitchAnalogPinCommand [pin=" + pin + ", value=" + value
						+ "]=OK"));
		
		context.stop();

		Link mock = getMock();
		verify(mock).switchAnalogPin(analogPin(pin), value);
		verify(mock).close();
		verifyNoMoreInteractions(mock);
	}

	private String makeURI(String uri,
			Map<? extends Object, ? extends Object> kv) {
		return uri + "?" + Joiner.on("&").withKeyValueSeparator("=").join(kv);
	}

	@Test
	@Ignore
	public void queryValue() throws Exception {
		CamelContext context = new DefaultCamelContext();
		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() {
				restConfiguration().component("restlet").host("localhost")
						.port(12345);
				from("rest:put:ardulink0/{pin}").setBody()
						.constant("It works!").to(mockURI);
			}
		});
		context.start();

		ClientResponse resp = getValueViaRest();
		assertThat(resp.getStatus(), is(200));
		assertThat(resp.getEntity(String.class), is("It works!"));
	}

	private ClientResponse getValueViaRest() {
		Client client = Client.create(new DefaultClientConfig());
		WebResource service = client.resource(getBaseURI());
		String pinNum = "12";
		return service.path("ardulink0").path(pinNum).accept(APPLICATION_JSON)
				.get(ClientResponse.class);
	}

	private ClientResponse setValueViaRest(int pin, int value) {
		Client client = Client.create(new DefaultClientConfig());
		WebResource service = client.resource(getBaseURI());
		return service.path("ardulink0").path("analog")
				.path(String.valueOf(pin)).path(String.valueOf(value))
				.accept(APPLICATION_JSON).get(ClientResponse.class);
	}

	private URI getBaseURI() {
		return UriBuilder.fromUri("http://localhost/").port(12345).build();
	}

	private Link getMock() {
		return getMock(link);
	}

	private Link getMock(Link link) {
		return ((LinkDelegate) link).getDelegate();
	}

}
