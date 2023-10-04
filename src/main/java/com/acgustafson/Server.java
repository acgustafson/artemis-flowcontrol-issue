package com.acgustafson;

import org.apache.activemq.artemis.core.security.CheckType;
import org.apache.activemq.artemis.core.security.Role;
import org.apache.activemq.artemis.core.server.ActiveMQServer;
import org.apache.activemq.artemis.core.server.ActiveMQServers;
import org.apache.activemq.artemis.spi.core.security.ActiveMQSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;

public class Server {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    String config;
    public Server(String configName) {
        this.config = configName;
    }

    private ActiveMQSecurityManager getSecurityManager() {
        return new ActiveMQSecurityManager() {
            @Override
            public boolean validateUser(String user, String password) {
                return true;
            }
            @Override
            public boolean validateUserAndRole(String user, String password, Set<Role> roles, CheckType checkType) {
                return true;
            }
        };
    }

    private void createBroker(String configName) throws Exception {
        ActiveMQServer server = ActiveMQServers.newActiveMQServer(configName, null, getSecurityManager());
        server.start();
        logger.info("Started Embedded Broker " + server.getNodeID());
    }

    public void start() {
        try {
            createBroker(this.config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
