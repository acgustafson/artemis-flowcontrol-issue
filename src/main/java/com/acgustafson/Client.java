package com.acgustafson;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Client implements Runnable {
    private final Logger   logger = LoggerFactory.getLogger(getClass());
    private final String   HELLO_TOPIC_NAME = "HelloTopic";
    private final String   clientId;
    private final boolean  consumeFromWildcard;
    private final String   brokerHostname;
    private final boolean  printStats;
    private final Integer  pubSubTimer = 30000;

    private AtomicInteger messagesConsumed = new AtomicInteger(0);

    public Client(String id, Boolean consumeFromWildcard, String brokerHostname, boolean printStats) {
        this.clientId = id;
        this.consumeFromWildcard = consumeFromWildcard;
        this.brokerHostname = brokerHostname;
        this.printStats = printStats;
    }

    private Session getSession() throws JMSException {
        ActiveMQConnectionFactory connectionFactory = JMSConfiguration.createFactory(brokerHostname);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    private void consumeFromWildcard() {
        try {
            Session session = getSession();
            Topic wildcardHelloTopic = session.createTopic(HELLO_TOPIC_NAME + ".#");
            MessageConsumer messageConsumer = session.createConsumer(wildcardHelloTopic, null, true);
            messageConsumer.setMessageListener(new ClientMessageListenter("wildcardConsumer-" + clientId, this.printStats));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void startPublisher() {
        try {
            Session session = getSession();
            Topic helloTopic = session.createTopic(HELLO_TOPIC_NAME + "." + clientId);

            MessageProducer messageProducer = session.createProducer(null);
            messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            Timer timer = new Timer("PublisherTimer");
            timer.schedule(new TimerTask() {
                public void run() {
                    try {
                        ObjectMessage message = session.createObjectMessage(new HelloMessage());
                        messageProducer.send(helloTopic, message);
                        logger.debug("Sent message from publisherID {}", clientId);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }, 0, pubSubTimer);
        }
        catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        if (consumeFromWildcard) {
            consumeFromWildcard();
            if (printStats) {
                Timer statsTimer = new Timer("StatsTimer");
                statsTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        logger.info("Consumed {} messages in the last {} seconds", messagesConsumed, TimeUnit.MILLISECONDS.toSeconds(pubSubTimer));
                        messagesConsumed.set(0);
                    }
                }, 0, pubSubTimer);
            }
        }
        else {
            startPublisher();
        }
    }

    private class ClientMessageListenter implements MessageListener {
        String clientName;
        Boolean captureStats;
        public ClientMessageListenter(String clientName, boolean captureStats){
            this.clientName = clientName;
            this.captureStats = captureStats;
        }
        @Override
        public void onMessage(Message message) {
            ObjectMessage oMessage = (ObjectMessage)message;
            try {
                final Serializable baseMessage = oMessage.getObject();
                String test = ((HelloMessage)baseMessage).getTestString();
                if (captureStats) {
                    messagesConsumed.getAndIncrement();
                }
                else {
                    logger.debug("{} consumed message", clientName);
                }

            } catch (JMSException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
