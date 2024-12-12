package com.checkout.payment.gateway.validator;

import com.checkout.payment.gateway.enums.Currency;
import com.checkout.payment.gateway.exception.EventValidationException;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.service.PaymentGatewayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.time.YearMonth;
import java.util.Arrays;

@Component
public class PaymentValidator {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

  public boolean validate(PostPaymentRequest postPaymentRequest) {
    try {
      if (postPaymentRequest.getCardNumber() == null || !postPaymentRequest.getCardNumber().matches("\\d{14,19}")) {
        throw new EventValidationException("Invalid card number");
      }

      if (postPaymentRequest.getExpiryMonth() < 1 || postPaymentRequest.getExpiryMonth() > 12) {
        throw new EventValidationException("Invalid expiry month");
      }

      if (YearMonth.of(postPaymentRequest.getExpiryYear(), postPaymentRequest.getExpiryMonth()).isBefore(YearMonth.now())) {
        throw new EventValidationException("Card has expired");
      }

      if (!Arrays.stream(Currency.values()).toList().contains(postPaymentRequest.getCurrency())) {
        throw new EventValidationException("Unsupported currency");
      }

      if (postPaymentRequest.getAmount() <= 0) {
        throw new EventValidationException("Invalid amount");
      }

      if (postPaymentRequest.getCvv() == null || !postPaymentRequest.getCvv().matches("\\d{3,4}")) {
        throw new EventValidationException("Invalid CVV");
      }

      return true;
    } catch (EventValidationException e) {
      LOG.warn("Validation failure. Reason - {}", e.getMessage());
      return false;
    }
  }
}
