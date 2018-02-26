package com.iccom.requestResponse;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;

import static javax.jms.Session.AUTO_ACKNOWLEDGE;

/**
 * 服务端
 * <p>
 * Create by CK on 2018/2/26 14:50
 */
public class Server {

    private static final String URL = "tcp://127.0.0.1:61616";
    private static final String ADMIN_QUEUE_NAME = "request-response-client-queue";

    public void receiveMsg() throws JMSException {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(URL);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Destination adminQueue = session.createQueue(ADMIN_QUEUE_NAME);

        // 生产者
        MessageProducer messageProducer = session.createProducer(adminQueue);
        messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        // 消费者
        Destination tempQueue = session.createTemporaryQueue();
        MessageConsumer messageConsumer = session.createConsumer(tempQueue);
    }

    public static void main(String[] args) throws JMSException {
        Server server = new Server();
        server.receiveMsg();
    }
}
