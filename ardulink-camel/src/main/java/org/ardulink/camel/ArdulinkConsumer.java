package org.ardulink.camel;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
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

        Exchange exchange = getEndpoint().createExchange(ExchangePattern.InOnly);
        Message message = exchange.getIn();

        message.setBody(e.getInMessage());
		try {
			getProcessor().process(exchange);
		} catch (Exception ex) {
			ex.printStackTrace();
			getExceptionHandler().handleException("Failed to process notification", ex);
		}
	}

	@Override
	protected void doStart() throws Exception {
		ArdulinkEndpoint ardulinkEndpoint = (ArdulinkEndpoint)getEndpoint();
		
		ardulinkEndpoint.getLinkMessageAdapter().addInMessageListener(this);
		super.doStart();
	}
	
	@Override
	protected void doStop() throws Exception {
		ArdulinkEndpoint ardulinkEndpoint = (ArdulinkEndpoint)getEndpoint();
		
		ardulinkEndpoint.getLinkMessageAdapter().removeInMessageListener(this);
		super.doStop();
	}

	

}
