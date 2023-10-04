Running without arguments creates two embedded brokers, 3000 producers, 
and 12 consumers on a wildcard that matches all the producers. 

Producers/consumers are load balanced between the two embedded brokers.  

After a minute of running this program the logs from the consumer printing statistics
logs the following:

```
[2023-10-04 14:39:23,240] INFO  Client [StatsTimer] Consumed 1500 messages in the last 30 seconds`
```

The embedded broker also shows it's blocked on flow control:

```text
[2023-10-04 14:36:53,208] DEBUG BridgeImpl [Thread-0 (ActiveMQ-server-org.apache.activemq.artemis.core.server.impl.ActiveMQServerImpl$6@3b4ef7)] Bridge $.artemis.internal.sf.bug-cluster.1215041a-62d4-11ee-a353-00be43bd5a32 is blocked on flow control, cannot receive Reference[45097183242]:NON-RELIABLE:CoreMessage[messageID=45097183242, durable=false, userID=5e49a605-62ed-11ee-87b3-00be43bd5a32, priority=4, timestamp=Wed Oct 04 14:36:52 CDT 2023, expiration=0, durable=false, address=HelloTopic.2005, size=558, properties=TypedProperties[__AMQ_CID=4c67db81-62ed-11ee-87b3-00be43bd5a32, _AMQ_ROUTE_TO$.artemis.internal.sf.bug-cluster.1215041a-62d4-11ee-a353-00be43bd5a32=[0000 000A 8000 295C 0000 000A 8000 294F 0000 000A 8000 2942 0000 000A 8000 2969 0000 000A 8000 2928 0000 000A 8000 2976 0000 000A 8000 2935], bytesAsLongs[45097167196, 45097167183, 45097167170, 45097167209, 45097167144, 45097167222, 45097167157], _AMQ_ROUTING_TYPE=0]]@561104729
```
 


