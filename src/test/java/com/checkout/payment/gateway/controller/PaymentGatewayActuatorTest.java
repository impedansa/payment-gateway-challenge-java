package com.checkout.payment.gateway.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentGatewayActuatorTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void whenGetHealthThenStatusIsUp() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/actuator/health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("UP"));
  }

  @Test
  void whenGetInfoThenStatusIsOk() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/actuator/info"))
        .andExpect(status().isOk());
  }
}
