package com.checkout.payment.gateway.exception;

public class EventValidationException extends RuntimeException {

  public EventValidationException(String message) {
    super(message);
  }
}
