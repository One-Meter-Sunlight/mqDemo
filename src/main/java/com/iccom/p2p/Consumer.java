package com.iccom.p2p;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

import static javax.jms.Session.AUTO_ACKNOWLEDGE;

/**
 * 消费者
 * <p>
 * Create by CK on 2018/2/26 10:12
 */
public class Consumer {
    private static final String URL = "tcp://127.0.0.1:61616";
    private static final String QUEUE_NAME = "testQueue";

    public void reciveMsg() throws JMSException {
        // 获得连接工厂，由JMS规范提供
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(URL);

        // 获得连接
        Connection connection = connectionFactory.createConnection();

        // 启动
        connection.start();

        // 获得Session，不设置事务，为true时，第二个参数忽略，现在设置自动
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);

        // 创建目标
        Destination destination = session.createQueue(QUEUE_NAME);

        // 接收消息, 异步监听，这里不能关闭连接
        MessageConsumer messageConsumer = session.createConsumer(destination);
        messageConsumer.setMessageListener(new MessageListener() {
            public void onMessage(Message message) {
                TextMessage textMessage = (TextMessage) message;
                String receiveStr = null;
                try {
                    receiveStr = textMessage.getText();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
                System.out.println("接收到的消息为：" + receiveStr);
            }
        });

    }

    public static void main(String[] args) throws JMSException {
        Consumer consumer = new Consumer();
        consumer.reciveMsg();
    }
}
