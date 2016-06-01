package org.ardulink.camel;

import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;

public class ArdulinkConsumer extends DefaultConsumer {

	public ArdulinkConsumer(ArdulinkEndpoint endpoint, Processor processor) {
		super(endpoint, processor);
	}

}
