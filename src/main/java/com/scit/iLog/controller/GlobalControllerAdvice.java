package com.scit.iLog.controller;

import com.scit.iLog.exception.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalControllerAdvice {

    // 모든 예외 처리 (기본)
    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return "/error/500.html";
    }

    // EntityNotFoundException 처리 (예: 데이터베이스에서 엔티티를 찾지 못한 경우)
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return "/error/404.html";
    }

    @ExceptionHandler(AnalysisResultNotFoundException.class)
    public String handleAnalysisResultNotFoundException(
            AnalysisResultNotFoundException ex
    ) {
        return String.format("redirect:/children/%d/analysis/resultList", ex.childId());
    }

    @ExceptionHandler(ChildNotFoundException.class)
    public String handleChildNotFoundException(
            ChildNotFoundException ex
    ) {
        return "redirect:/dashboard";
    }

    @ExceptionHandler({FileDeleteFailException.class, HealthCheckFileDeleteFailException.class})
    public String handleFileDeleteFailException() {
        return "/error/404.html";
    }

    @ExceptionHandler({FileSaveFailException.class, HealthCheckFileSaveFailException.class})
    public String handleFileSaveFailException() {
        return "/error/500.html";
    }

    @ExceptionHandler({MemberNotFoundException.class, WrongSignInIdException.class})
    public String handleMemberNotFoundException() {
        return "/auth/signIn";
    }

    public static class ErrorResponse {
        private final LocalDateTime timestamp;
        private final String message;
        private final String details;

        public ErrorResponse(LocalDateTime timestamp, String message, String details) {
            this.timestamp = timestamp;
            this.message = message;
            this.details = details;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public String getMessage() {
            return message;
        }

        public String getDetails() {
            return details;
        }
    }
}