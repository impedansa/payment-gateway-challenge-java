package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.enums.Currency;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PostPaymentRequest implements Serializable {

  @JsonProperty("card_number")
  private String cardNumber;
  @JsonProperty("expiry_month")
  private Integer expiryMonth;
  @JsonProperty("expiry_year")
  private Integer expiryYear;
  private Currency currency;
  private Integer amount;
  private String cvv;
}
