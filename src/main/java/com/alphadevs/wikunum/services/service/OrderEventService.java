package com.alphadevs.wikunum.services.service;

import com.alphadevs.wikunum.services.config.KafkaOrderEventProducer;
import com.alphadevs.wikunum.services.domain.Order;
import com.alphadevs.wikunum.services.web.rest.errors.EventServiceException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

@Service
public class OrderEventService {

    private final Logger log = LoggerFactory.getLogger(OrderEventService.class);
    private final MessageChannel output;

    public OrderEventService(@Qualifier(KafkaOrderEventProducer.CHANNELNAME) MessageChannel output) {
        this.output = output;
    }

    public void orderActivatedEvent(Order order) {
        try {
            log.debug("orderActivatedEvent : {} to send to order-events-topic ", order);

            Map<String, Object> map = new HashMap<>();
            map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);
            MessageHeaders headers = new MessageHeaders(map);
            output.send(new GenericMessage<>(order, headers));
        } catch (Exception ex) {
            log.error("Could not send event", ex);
            throw new EventServiceException(ex);
        }
    }
}
