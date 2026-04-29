package com.studentservice.event;

import com.studentservice.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(StudentEventPublisher.class);
    private static final String STUDENT_DELETED_ROUTING_KEY = "student.deleted";

    private final RabbitTemplate rabbitTemplate;

    public void publishStudentDeleted(String studentId) {
        StudentDeletedEvent event = new StudentDeletedEvent(studentId);
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.STUDENT_EVENTS_EXCHANGE,
            STUDENT_DELETED_ROUTING_KEY,
            event
        );
        logger.info("Published student.deleted event for studentId: {}", studentId);
    }
}