package com.iccom.topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;

import static javax.jms.Session.AUTO_ACKNOWLEDGE;

/**
 * 生产者
 * <p>
 * Create by CK on 2018/2/26 9:50
 */
public class Provider {

    private static final String URL = "tcp://127.0.0.1:61616";
    private static final String TOPIC_NAME = "testTopic";

    public void sendMsg() throws JMSException {
        // 获得连接工厂，由JMS规范提供
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(URL);

        // 获得连接
        Connection connection = connectionFactory.createConnection();

        // 启动
        connection.start();

        // 获得Session，不设置事务，为true时，第二个参数忽略，现在设置自动
        Session session = connection.createSession(false, AUTO_ACKNOWLEDGE);

        // 创建目标
        Destination destination = session.createTopic(TOPIC_NAME);

        // 生产者
        MessageProducer messageProducer = session.createProducer(destination);

        for (int i = 0; i < 50; i++) {
            TextMessage textMessage = new ActiveMQTextMessage();
            String str = "消息为：test" + i;
            textMessage.setText(str);
            System.out.println("发送的队列" + str);
            messageProducer.send(textMessage);
        }

        // 关闭连接
        connection.close();
    }

    public static void main(String[] args) throws JMSException {
        Provider provider = new Provider();
        provider.sendMsg();
    }

}
