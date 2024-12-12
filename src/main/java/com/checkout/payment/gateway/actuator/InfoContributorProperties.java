package com.checkout.payment.gateway.actuator;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = "spring.application")
public class InfoContributorProperties {
  private String name;
  private String version;
  private String contributors;
}
