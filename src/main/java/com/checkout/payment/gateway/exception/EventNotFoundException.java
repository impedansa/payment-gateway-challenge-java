package com.checkout.payment.gateway.exception;

public class EventNotFoundException extends RuntimeException {

  public EventNotFoundException(String message) {
    super(message);
  }
}
