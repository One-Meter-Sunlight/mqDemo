package com.iccom.requestResponse;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

import javax.jms.*;

import static javax.jms.Session.AUTO_ACKNOWLEDGE;

/**
 * 服务端
 * 流程：接收消息-发送消息到临时队列，并且一一对应请求
 * <p>
 * Create by CK on 2018/2/26 14:50
 */
public class Server {

    private static final String URL = "tcp://127.0.0.1:61616";
    private static final String ADMIN_QUEUE_NAME = "request-response-client-queue";

    private MessageProducer messageProducer = null;

    public void receiveMsg() throws JMSException {

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(URL);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        final Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);
        Destination adminQueue = session.createQueue(ADMIN_QUEUE_NAME);

        this.messageProducer = session.createProducer(null);
        messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        // 接收消息
        MessageConsumer messageConsumer = session.createConsumer(adminQueue);
        messageConsumer.setMessageListener(new MessageListener() {
            public void onMessage(Message message) {
                try {
                    // 创建返回消息
                    TextMessage response = session.createTextMessage();

                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        String receiveStr = textMessage.getText();
                        System.out.println("接收到的消息为==============" + receiveStr);

                        response.setText("成功接收，现在返回给你。。。");
                    }

                    response.setJMSCorrelationID(message.getJMSCorrelationID());

                    messageProducer.send(message.getJMSReplyTo(), response);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void main(String[] args) throws JMSException {
        Server server = new Server();
        server.receiveMsg();
    }
}
