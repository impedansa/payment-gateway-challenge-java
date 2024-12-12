package com.checkout.payment.gateway.service;

import static com.checkout.payment.gateway.enums.PaymentStatus.AUTHORIZED;
import static com.checkout.payment.gateway.enums.PaymentStatus.DECLINED;
import static com.checkout.payment.gateway.enums.PaymentStatus.REJECTED;
import com.checkout.payment.gateway.configuration.PaymentEndpoints;
import com.checkout.payment.gateway.exception.EventNotFoundException;
import com.checkout.payment.gateway.model.BankPaymentRequest;
import com.checkout.payment.gateway.model.BankPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import com.checkout.payment.gateway.util.HttpUtils;
import com.checkout.payment.gateway.validator.PaymentValidator;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final HttpUtils httpUtils;
  private final PaymentEndpoints paymentEndpoints;
  private final PaymentsRepository paymentsRepository;

  private final PaymentValidator paymentValidator;

  public PaymentGatewayService(
      HttpUtils httpUtils,
      PaymentEndpoints paymentEndpoints,
      PaymentsRepository paymentsRepository,
      PaymentValidator paymentValidator
  ) {
    this.httpUtils = httpUtils;
    this.paymentEndpoints = paymentEndpoints;
    this.paymentsRepository = paymentsRepository;
    this.paymentValidator = paymentValidator;
  }

  public PostPaymentResponse getPaymentById(UUID id) {
    LOG.debug("Requesting access to payment with ID {}", id);
    return paymentsRepository.findByIdAndStatusIn(id, List.of(AUTHORIZED, DECLINED))
        .orElseThrow(() -> new EventNotFoundException("Invalid ID"));
  }

  public PostPaymentResponse processPayment(PostPaymentRequest postPaymentRequest) {
    PostPaymentResponse postPaymentResponse;
    boolean isPaymentRequestValid = paymentValidator.validate(postPaymentRequest);

    if(!isPaymentRequestValid) {
      postPaymentResponse = createPostPaymentValidationResponse(postPaymentRequest);
    } else {
      /* Create the bank payment request */
      BankPaymentRequest bankPaymentRequest = createBankPaymentRequest(postPaymentRequest);

      /* Simulate the call to the acquiring bank */
      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth("token");
      /* Any additional headers if required */

      BankPaymentResponse bankPaymentResponse = httpUtils.post(
          paymentEndpoints.getAcquiringBank(),
          bankPaymentRequest,
          headers,
          BankPaymentResponse.class
      );

      postPaymentResponse = createPostPaymentProcessingResponse(
          bankPaymentRequest,
          bankPaymentResponse
      );
    }

    paymentsRepository.add(postPaymentResponse);
    return postPaymentResponse;
  }

  private PostPaymentResponse createPostPaymentProcessingResponse(
      BankPaymentRequest bankPaymentRequest,
      BankPaymentResponse bankPaymentResponse
  ) {
    /* Get the last 4 digits of the card-number */
    int cardNumberLastFourDigits = Integer.parseInt(
        bankPaymentRequest.getCardNumber()
            .substring(bankPaymentRequest.getCardNumber().length() - 4));

    return PostPaymentResponse.builder()
        .id(UUID.randomUUID())
        .status(bankPaymentResponse.getAuthorized() ? AUTHORIZED : DECLINED)
        .cardNumberLastFour(cardNumberLastFourDigits)
        .expiryMonth(Integer.parseInt(bankPaymentRequest.getExpiryDate().split("/")[0]))
        .expiryYear(Integer.parseInt(bankPaymentRequest.getExpiryDate().split("/")[1]))
        .currency(bankPaymentRequest.getCurrency())
        .amount(bankPaymentRequest.getAmount())
        .build();
  }

  private PostPaymentResponse createPostPaymentValidationResponse(
      PostPaymentRequest postPaymentRequest
  ) {
    /* Get the last 4 digits of the card-number */
    int cardNumberLastFourDigits = Integer.parseInt(
        postPaymentRequest.getCardNumber()
            .substring(postPaymentRequest.getCardNumber().length() - 4));

    return PostPaymentResponse.builder()
        .id(UUID.randomUUID())
        .status(REJECTED)
        .cardNumberLastFour(cardNumberLastFourDigits)
        .expiryMonth(postPaymentRequest.getExpiryMonth())
        .expiryYear(postPaymentRequest.getExpiryYear())
        .currency(postPaymentRequest.getCurrency())
        .amount(postPaymentRequest.getAmount())
        .build();
  }

  private BankPaymentRequest createBankPaymentRequest(PostPaymentRequest postPaymentRequest) {
    DecimalFormat monthFormatter = new DecimalFormat("00");
    String formattedMonth = monthFormatter.format(postPaymentRequest.getExpiryMonth());
    String expiryDate = formattedMonth + "/" + postPaymentRequest.getExpiryYear();

    return BankPaymentRequest.builder()
        .cardNumber(postPaymentRequest.getCardNumber())
        .expiryDate(expiryDate)
        .currency(postPaymentRequest.getCurrency())
        .amount(postPaymentRequest.getAmount())
        .cvv(postPaymentRequest.getCvv())
        .build();
  }
}
