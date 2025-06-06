package com.ahamo.common.exception;

import org.springframework.stereotype.Component;

public interface ErrorNotificationService {
    void notifyCriticalError(ErrorContext context);
}

@Component
class ErrorNotificationServiceImpl implements ErrorNotificationService {
    @Override
    public void notifyCriticalError(ErrorContext context) {
    }
}

class ErrorContext {
    private final String errorCode;
    private final String message;
    private final String requestId;
    
    public ErrorContext(String errorCode, String message, String requestId) {
        this.errorCode = errorCode;
        this.message = message;
        this.requestId = requestId;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getRequestId() {
        return requestId;
    }
}
