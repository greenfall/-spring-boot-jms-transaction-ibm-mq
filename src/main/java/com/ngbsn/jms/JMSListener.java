package com.ngbsn.jms;

import com.ngbsn.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class JMSListener {
    private static final Logger logger
            = LoggerFactory.getLogger(JMSListener.class);

    @Autowired
    private TestService testService;

    @JmsListener(destination = "DEV.LOCAL.TEST_IN_QUEUE")
    @Transactional(rollbackFor = Exception.class)
    public void process(final String message) {
        try {
            testService.process(message);
        } catch (Exception e) {
            logger.error("An error occurred in the listener", e);
        }
    }
}
