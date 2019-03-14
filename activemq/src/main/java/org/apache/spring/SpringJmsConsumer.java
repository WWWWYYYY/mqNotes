package org.apache.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * 消费者
 */
public class SpringJmsConsumer {
    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context =new ClassPathXmlApplicationContext("classpath:application-c.xml");

        System.in.read();
    }
}
