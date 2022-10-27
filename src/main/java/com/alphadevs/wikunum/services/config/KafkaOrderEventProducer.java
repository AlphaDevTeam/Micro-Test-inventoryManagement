package com.alphadevs.wikunum.services.config;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface KafkaOrderEventProducer {
    String CHANNELNAME = "binding-out-order-event";

    @Output(CHANNELNAME)
    MessageChannel output();
}
