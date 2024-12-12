package com.checkout.payment.gateway.configuration;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "endpoints.payment")
public class PaymentEndpoints {

  private String acquiringBank;
}
