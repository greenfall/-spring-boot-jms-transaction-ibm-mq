package com.ngbsn.service;

import com.ngbsn.jms.JMSProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    private static final Logger logger
            = LoggerFactory.getLogger(TestService.class);


    @Autowired
    JMSProducer jmsProducer;

    public void process(final String message){
        jmsProducer.publish(message + "-PROCESSED");
        logger.info("Successfully processed message {}", message);
    }
}
