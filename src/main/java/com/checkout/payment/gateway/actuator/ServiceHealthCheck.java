package com.checkout.payment.gateway.actuator;

import com.checkout.payment.gateway.actuator.health.BankSimulatorHealthCheck;
import lombok.Data;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.stream.Stream;

import static org.springframework.boot.actuate.health.Status.DOWN;

@Data
@Component("service-health-check")
public class ServiceHealthCheck implements HealthIndicator {

  private final BankSimulatorHealthCheck bankSimulatorHealthCheck;

  @Override
  public Health getHealth(boolean includeDetails) {
    return HealthIndicator.super.getHealth(true);
  }

  @Override
  public Health health() {
    boolean isAnyDown = Stream.of(bankSimulatorHealthCheck.getHealth())
        .anyMatch(health -> DOWN.toString().equalsIgnoreCase(health.get("status").toString()));

    Map<String, Object> aggregatedHealthCheckDetails = Map.of(
        "BANK-SIMULATOR", bankSimulatorHealthCheck.getHealth()
    );

    return isAnyDown
        ? Health.down().withDetails(aggregatedHealthCheckDetails).build()
        : Health.up().withDetails(aggregatedHealthCheckDetails).build();
  }
}
