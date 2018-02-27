package com.iccom.requestResponse;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;
import java.util.Random;

import static javax.jms.Session.AUTO_ACKNOWLEDGE;

/**
 * 客户端
 * 流程：发送消息-创建临时队列接收返回的消息
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

        // 生产
        MessageProducer messageProducer = session.createProducer(adminQueue);
        messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        // 消费，创建临时队列监听相应
        Destination tempQueue = session.createTemporaryQueue();
        MessageConsumer messageConsumer = session.createConsumer(tempQueue);
        messageConsumer.setMessageListener(new MessageListener() {
            public void onMessage(Message message) {
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String receiveStr = null;
                    try {
                        receiveStr = textMessage.getText();
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                    System.out.println("接收到的返回消息为==============" + receiveStr);
                }
            }
        });

        // 发送消息
        TextMessage textMessage = new ActiveMQTextMessage();
        String str = "test";
        textMessage.setText(str);
        System.out.println("发送的消息队列为：" + str);

        // 重点，将返回的消息目的地设置到发送的消息中
        textMessage.setJMSReplyTo(tempQueue);

        // 设置返回消息知道是哪个请求
        // textMessage.setJMSCorrelationID("1001");  // 这里可以是随机数
        textMessage.setJMSCorrelationID(createCorrelationId());

        // 发送
        messageProducer.send(textMessage);

    }

    private String createCorrelationId() {
        Random random = new Random(System.currentTimeMillis());
        Long num = random.nextLong();
        System.out.println(num);
        return Long.toHexString(num);
    }

    public static void main(String[] args) throws JMSException {
        Client client = new Client();
        client.sendMsg();
    }
}
