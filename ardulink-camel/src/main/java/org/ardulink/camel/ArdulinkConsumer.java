package org.ardulink.camel;

import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.ardulink.core.messages.events.api.InMessageEvent;
import org.ardulink.core.messages.events.api.InMessageListener;

public class ArdulinkConsumer extends DefaultConsumer implements InMessageListener {

	public ArdulinkConsumer(ArdulinkEndpoint endpoint, Processor processor) {
		super(endpoint, processor);
	}

	@Override
	public void inMessageReceived(InMessageEvent e) {
		
		// TODO chiamare i metodi del default consumer studiare.... SONO QUI.
		
	}

}
