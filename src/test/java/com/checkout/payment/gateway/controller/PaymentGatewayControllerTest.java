package com.checkout.payment.gateway.controller;

import static org.springframework.test.web.client.ExpectedCount.between;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.enums.Currency;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.JsonObject;
import net.joshka.junit.json.params.JsonFileSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentGatewayControllerTest {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayControllerTest.class);

  @Autowired
  private MockMvc mvc;

  @Autowired
  private PaymentsRepository paymentsRepository;

  @Autowired
  private RestTemplate restTemplate;

  private MockRestServiceServer mockServer;
  private static final String BASE_URI = "http://localhost:8080/payments";
  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    mockServer = MockRestServiceServer.createServer(restTemplate);
  }

  @ParameterizedTest
  @JsonFileSource(resources = {"/payment-request.json"})
  void processPayments(JsonObject jsonObject) throws Exception {
    ParameterizedPaymentProcessRequest parameterizedPaymentProcessRequest = objectMapper.readValue(
        jsonObject.toString(),
        ParameterizedPaymentProcessRequest.class);

    LOG.info("Running test: {}", parameterizedPaymentProcessRequest.getTestTitle());

    mockServer.expect(between(1,5), requestTo(BASE_URI))
        .andExpect(method(HttpMethod.POST))
        .andRespond(
            request -> {
              if ("HttpServerErrorException".equals(parameterizedPaymentProcessRequest.getSimulateException())) {
                throw new HttpServerErrorException(HttpStatusCode.valueOf(500), "Internal server error");
              } else if ("ResourceAccessException".equals(parameterizedPaymentProcessRequest.getSimulateException())) {
                throw new ResourceAccessException("Connection refused");
              } else {
                return MockRestResponseCreators
                    .withRawStatus(parameterizedPaymentProcessRequest.getExpectBankPaymentResponseCode())
                    .body(objectMapper.writeValueAsString(parameterizedPaymentProcessRequest.getExpectBankPaymentResponseBody()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .createResponse(request);
              }
            }
        );

    ResultMatcher expectedStatus;
    if (parameterizedPaymentProcessRequest.getExpectPaymentException()) {
      expectedStatus = status().is5xxServerError();
    } else {
      expectedStatus = status().isOk();
    }

    ResultActions resultActions = mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(parameterizedPaymentProcessRequest.getPostPaymentRequest())))
        .andExpect(expectedStatus);

    if (!parameterizedPaymentProcessRequest.getExpectPaymentException()) {
      resultActions
          .andExpect(jsonPath("$.amount").value(
              parameterizedPaymentProcessRequest.getExpectedPaymentResponse().getAmount()))
          .andExpect(jsonPath("$.currency").value(
              parameterizedPaymentProcessRequest.getExpectedPaymentResponse().getCurrency().toString()))
          .andExpect(jsonPath("$.status").value(
              parameterizedPaymentProcessRequest.getExpectedPaymentResponse().getStatus().getName()))
          .andExpect(jsonPath("$.expiryMonth").value(
              parameterizedPaymentProcessRequest.getExpectedPaymentResponse().getExpiryMonth()))
          .andExpect(jsonPath("$.expiryYear").value(
              parameterizedPaymentProcessRequest.getExpectedPaymentResponse().getExpiryYear()))
          .andExpect(jsonPath("$.cardNumberLastFour").value(
              parameterizedPaymentProcessRequest.getExpectedPaymentResponse().getCardNumberLastFour()));
    } else {
      resultActions.andExpect(
          jsonPath("$.message").value(parameterizedPaymentProcessRequest.getExpectErrorMessage()));
    }
  }

  @Test
  void whenPaymentWithIdExistsThenCorrectPaymentIsReturned() throws Exception {
    PostPaymentResponse payment = new PostPaymentResponse();
    payment.setId(UUID.randomUUID());
    payment.setAmount(10);
    payment.setCurrency(Currency.USD);
    payment.setStatus(PaymentStatus.AUTHORIZED);
    payment.setExpiryMonth(12);
    payment.setExpiryYear(2024);
    payment.setCardNumberLastFour(4321);

    paymentsRepository.add(payment);

    mvc.perform(MockMvcRequestBuilders.get("/payment/" + payment.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(payment.getStatus().getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(payment.getCardNumberLastFour()))
        .andExpect(jsonPath("$.expiryMonth").value(payment.getExpiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(payment.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value(payment.getCurrency().toString()))
        .andExpect(jsonPath("$.amount").value(payment.getAmount()));
  }

  @Test
  void whenPaymentWithIdDoesNotExistThen404IsReturned() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/payment/" + UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Invalid ID"));
  }
}
