package com.ahamo.payment.exception;

public class PaymentTokenException extends RuntimeException {
    public PaymentTokenException(String message) {
        super(message);
    }
    
    public PaymentTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
