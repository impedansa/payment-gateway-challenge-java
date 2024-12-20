package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.enums.Currency;
import com.checkout.payment.gateway.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PostPaymentResponse {

  private UUID id;
  private PaymentStatus status;
  private Integer cardNumberLastFour;
  private Integer expiryMonth;
  private Integer expiryYear;
  private Currency currency;
  private Integer amount;
}
