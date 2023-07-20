package com.ngbsn.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class JMSProducer {
    private static final Logger logger
            = LoggerFactory.getLogger(JMSProducer.class);
    @Autowired
    JmsTemplate jmsTemplate;

    public void publish(final String message){
        jmsTemplate.convertAndSend("DEV.LOCAL.TEST_OUT_QUEUE", message);
        logger.info("Successfully published message {}", message);
    }
}
