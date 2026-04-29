package com.frontend_service.feign;

import com.frontend_service.exception.ServiceUnavailableException;
import com.frontend_service.exception.StudentNotFoundException;
import com.frontend_service.exception.TaskNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

    private static final Logger logger = LoggerFactory.getLogger(FeignErrorDecoder.class);

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 404 -> {
                logger.warn("Resource not found when calling: {}", methodKey);

                if (methodKey.contains("TaskClient")) {
                    yield new TaskNotFoundException("Task not found");
                }
                yield new StudentNotFoundException("Student not found");
            }
            case 503 -> {
                logger.error("Service unavailable when calling: {}", methodKey);
                yield new ServiceUnavailableException("Student Service is currently unavailable");
            }
            default -> {
                logger.error("Unexpected error {} when calling: {}", response.status(), methodKey);
                yield new ServiceUnavailableException("An unexpected error occurred");
            }
        };
    }
}