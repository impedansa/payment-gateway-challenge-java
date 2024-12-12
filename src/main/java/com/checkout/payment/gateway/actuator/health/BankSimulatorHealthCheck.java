package com.checkout.payment.gateway.actuator.health;

import org.springframework.stereotype.Component;
import java.util.Map;

import static org.springframework.boot.actuate.health.Status.UP;

@Component
public class BankSimulatorHealthCheck {

  public Map<String, Object> getHealth() {
    /* Can add other info as required. Like connectivity errors, if any, etc. */
    return Map.of("status", UP);
  }
}
