package org.ardulink.camel;

import java.net.URI;
import java.util.Map;

import org.apache.camel.Consumer;
import org.apache.camel.MultipleConsumersSupport;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.ardulink.core.Link;
import org.ardulink.core.linkmanager.LinkManager;
import org.ardulink.util.URIs;

public class ArdulinkEndpoint extends DefaultEndpoint implements MultipleConsumersSupport {

	private Link link;
	
	public ArdulinkEndpoint(String uri, String remaining, Map<String, Object> parameters) {
		link = LinkManager.getInstance().getConfigurer(URIs.newURI(uri)).newLink();
	}

	@Override
	public Producer createProducer() throws Exception {
		ArdulinkProducer producer = new ArdulinkProducer(this);

		return producer;
	}

	@Override
	public Consumer createConsumer(Processor processor) throws Exception {
		ArdulinkConsumer consumer = new ArdulinkConsumer(this, processor);

		return consumer;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	@Override
	public boolean isMultipleConsumersSupported() {
		return true;
	}

	public Link getLink() {
		return link;
	}
}
