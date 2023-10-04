package com.acgustafson;

import org.apache.activemq.artemis.jms.client

        .ActiveMQConnectionFactory;

public class JMSConfiguration {
    public static ActiveMQConnectionFactory createFactory(String brokerHostname) {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://" + brokerHostname + "?connectionTTL=300000");
        connectionFactory.setCompressLargeMessage(false);

//        connectionFactory.setProducerWindowSize(-1);
        connectionFactory.setBlockOnDurableSend(false);
        connectionFactory.setReconnectAttempts(-1);
        connectionFactory.setRetryInterval(1000);
        connectionFactory.setRetryIntervalMultiplier(2.0);
        connectionFactory.setUseTopologyForLoadBalancing(false);
        return connectionFactory;
    }
}
