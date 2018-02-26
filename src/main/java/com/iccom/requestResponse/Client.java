package com.iccom.requestResponse;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;

import static javax.jms.Session.AUTO_ACKNOWLEDGE;

/**
 * 客户端
 * <p>
 * Create by CK on 2018/2/26 14:08
 */
public class Client {

    private static final String URL = "tcp://127.0.0.1:61616";
    private static final String ADMIN_QUEUE_NAME = "request-response-client-queue";

    public void sendMsg() throws JMSException {
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
        messageConsumer.setMessageListener(new MessageListener() {
            public void onMessage(Message message) {
                TextMessage textMessage = (TextMessage) message;
                String receiveStr = null;
                try {
                    receiveStr = textMessage.getText();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
                System.out.println("接收到的消息为==============" + receiveStr);
            }
        });

        // 发送消息
        TextMessage textMessage = new ActiveMQTextMessage();
        String str = "消息为：test";
        textMessage.setText(str);
        System.out.println("发送的队列" + str);

        // 重点
        textMessage.setJMSReplyTo(tempQueue);

        // 设置返回消息知道是哪个请求
        textMessage.setJMSCorrelationID("1001");

        // 发送
        messageProducer.send(textMessage);

        // 关闭连接
        connection.close();
    }

    public static void main(String[] args) throws JMSException {
        Client client = new Client();
        client.sendMsg();
    }
}
