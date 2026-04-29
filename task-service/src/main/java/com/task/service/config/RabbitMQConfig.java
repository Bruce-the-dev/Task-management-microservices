package com.task.service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    public static final String STUDENT_EVENTS_EXCHANGE = "student.events";
    public static final String STUDENT_DELETED_QUEUE = "task-service.student-deleted";
    public static final String STUDENT_DELETED_ROUTING_KEY = "student.deleted";

    @Bean
    public TopicExchange studentEventsExchange() {
        return new TopicExchange(STUDENT_EVENTS_EXCHANGE);
    }

    @Bean
    public Queue studentDeletedQueue() {
        return QueueBuilder.durable(STUDENT_DELETED_QUEUE).build();
    }

    @Bean
    public Binding studentDeletedBinding(Queue studentDeletedQueue,
                                          TopicExchange studentEventsExchange) {
        return BindingBuilder
            .bind(studentDeletedQueue)
            .to(studentEventsExchange)
            .with(STUDENT_DELETED_ROUTING_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         JacksonJsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}