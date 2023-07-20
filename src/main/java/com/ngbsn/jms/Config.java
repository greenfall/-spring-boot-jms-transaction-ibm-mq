package com.ngbsn.jms;

import com.ibm.mq.jms.MQQueueConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

import javax.jms.JMSException;

import static com.ibm.msg.client.jms.JmsConstants.PASSWORD;
import static com.ibm.msg.client.jms.JmsConstants.USERID;
import static com.ibm.msg.client.wmq.common.CommonConstants.*;

@Configuration
public class Config {

    private static final Logger logger
            = LoggerFactory.getLogger(Config.class);

    @Value("${ibm.hostName}")
    private String hostName;
    @Value("${ibm.queueManager}")
    private String queueManager;
    @Value("${ibm.channel}")
    private String channel;
    @Value("${ibm.port}")
    private Integer port;
    @Value("${ibm.userName}")
    private String userName;
    @Value("${ibm.password}")
    private String password;
    @Value("${ibm.clientId}")
    private String clientId;

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() throws JMSException {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setDestinationResolver(new DynamicDestinationResolver());
        factory.setSessionTransacted(true);
        factory.setConcurrency("2-10");
        factory.setErrorHandler(throwable -> logger.error(throwable.getMessage()));
        return factory;
    }

    @Bean
    public MQQueueConnectionFactory connectionFactory() throws JMSException {
        final MQQueueConnectionFactory connectionFactory = new MQQueueConnectionFactory();
        connectionFactory.setHostName(hostName);
        connectionFactory.setPort(port);
        connectionFactory.setChannel(channel);
        connectionFactory.setQueueManager(queueManager);
        connectionFactory.setAppName(clientId);
        connectionFactory.setStringProperty(USERID, userName);
        connectionFactory.setStringProperty(PASSWORD, password);
        connectionFactory.setTransportType(WMQ_CM_CLIENT);
        connectionFactory.setIntProperty(WMQ_TARGET_CLIENT, WMQ_TARGET_DEST_MQ);
        return connectionFactory;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() throws JMSException {
        final CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setTargetConnectionFactory(connectionFactory());
        cachingConnectionFactory.setCacheConsumers(false);
        cachingConnectionFactory.setCacheProducers(true);
        cachingConnectionFactory.setSessionCacheSize(10);
        cachingConnectionFactory.setClientId("test");
        cachingConnectionFactory.setReconnectOnException(true);
        return cachingConnectionFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate() throws JMSException {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(cachingConnectionFactory());
        jmsTemplate.setExplicitQosEnabled(true);
        jmsTemplate.setDeliveryPersistent(false);
        jmsTemplate.setSessionTransacted(true);
        return jmsTemplate;
    }

    @Bean
    public JmsTransactionManager transactionManager() throws JMSException {
        JmsTransactionManager jmsTransactionManager = new JmsTransactionManager();
        jmsTransactionManager.setConnectionFactory(cachingConnectionFactory());
        return jmsTransactionManager;
    }
}
