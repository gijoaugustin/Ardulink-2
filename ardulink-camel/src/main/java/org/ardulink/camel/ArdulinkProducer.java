package org.ardulink.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultProducer;
import org.ardulink.core.Link;
import org.ardulink.core.Pin;
import org.ardulink.core.Pin.AnalogPin;
import org.ardulink.core.Pin.DigitalPin;
import org.ardulink.core.proto.api.ToArduinoCustomMessage;
import org.ardulink.core.proto.api.ToArduinoPinEvent;

public class ArdulinkProducer extends DefaultProducer {

	private Link link;
	
	public ArdulinkProducer(ArdulinkEndpoint endpoint) {
		super(endpoint);
		
		link = endpoint.getLink();
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		Object body = message.getBody();
		
		// TODO could be convenient define a ToArduino generic message interface
		// in this way we could use a type converter
		if(body instanceof ToArduinoPinEvent) {
			ToArduinoPinEvent event = (ToArduinoPinEvent)body;
			if(event.getPin().is(Pin.Type.DIGITAL)) {
				link.switchDigitalPin((DigitalPin)event.getPin(), (Boolean)event.getValue());
			} else {
				link.switchAnalogPin((AnalogPin)event.getPin(), (Integer)event.getValue());
			}
		} else if(body instanceof ToArduinoCustomMessage) {
			ToArduinoCustomMessage customMessage = (ToArduinoCustomMessage)body;
			link.sendCustomMessage(customMessage.getMessages());
		}
	}

}
