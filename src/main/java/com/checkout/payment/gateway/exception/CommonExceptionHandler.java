package com.checkout.payment.gateway.exception;

import com.checkout.payment.gateway.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(CommonExceptionHandler.class);

  @ExceptionHandler(EventProcessingException.class)
  public ResponseEntity<ErrorResponse> handleException(EventProcessingException ex) {
    ErrorResponse errorResponse = generateErrorResponse(ex);
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(EventNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleException(EventNotFoundException ex) {
    ErrorResponse errorResponse = generateErrorResponse(ex);
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  private ErrorResponse generateErrorResponse(Exception ex) {
    ErrorResponse errorResponse = new ErrorResponse(ex.getMessage());
    LOG.error("Exception happened - {}", errorResponse);
    return errorResponse;
  }
}
