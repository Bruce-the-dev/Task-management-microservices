package com.frontend_service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(TaskNotFoundException.class)
    public String handleNoTaskFound(TaskNotFoundException ex, Model model) {
        logger.warn("Task not found: {}", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(StudentNotFoundException.class)
    public String handleStudentNotFound(StudentNotFoundException ex, Model model) {
        logger.warn("Student not found: {}", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public String handleServiceUnavailable(ServiceUnavailableException ex, Model model) {
        logger.error("Service unavailable: {}", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleBadRequest(IllegalArgumentException ex, Model model) {
        logger.warn("Validation error: {}", ex.getMessage());
        model.addAttribute("error", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFound() {
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneral(Exception ex, Model model) {
        logger.error("Unexpected error occurred", ex);
        model.addAttribute("error", "Something went wrong. Please try again.");
        return "error";
    }
}