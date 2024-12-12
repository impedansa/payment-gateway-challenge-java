package com.checkout.payment.gateway.actuator;

import lombok.Data;
import org.springframework.boot.actuate.info.Info.Builder;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;
import java.util.Map;

@Data
@Component
public class ServiceInfoCheck implements InfoContributor {

  private final InfoContributorProperties infoContributorProperties;

  @Override
  public void contribute(Builder builder) {
    Map<String, Object> details = Map.of(
        "name", infoContributorProperties.getName(),
        "version", infoContributorProperties.getVersion(),
        "contributors", infoContributorProperties.getContributors()
    );

    builder.withDetails(details);
  }
}
