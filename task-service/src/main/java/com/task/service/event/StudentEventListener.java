package com.task.service.event;

import com.task.service.config.RabbitMQConfig;
import com.task.service.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentEventListener {

    private static final Logger logger = LoggerFactory.getLogger(StudentEventListener.class);
    private final TaskService taskService;

    @RabbitListener(queues = RabbitMQConfig.STUDENT_DELETED_QUEUE)
    public void handleStudentDeleted(StudentDeletedEvent event) {
        logger.info("Received student.deleted event for studentId: {}", event.getStudentId());
        taskService.nullifyTasksByStudent(event.getStudentId());
    }
}