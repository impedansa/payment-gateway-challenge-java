package com.checkout.payment.gateway.controller;

import com.checkout.payment.gateway.model.BankPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;

public class ParameterizedPaymentProcessRequest {

  private String testTitle;
  private PostPaymentRequest postPaymentRequest;
  private Integer expectBankPaymentResponseCode;
  private BankPaymentResponse expectBankPaymentResponseBody;
  private PostPaymentResponse expectedPaymentResponse;
  private Boolean expectPaymentException;
  private String expectErrorMessage;
  private String simulateException;

  public String getTestTitle() {
    return testTitle;
  }

  public void setTestTitle(String testTitle) {
    this.testTitle = testTitle;
  }

  public PostPaymentRequest getPostPaymentRequest() {
    return postPaymentRequest;
  }

  public void setPostPaymentRequest(PostPaymentRequest postPaymentRequest) {
    this.postPaymentRequest = postPaymentRequest;
  }

  public Integer getExpectBankPaymentResponseCode() {
    return expectBankPaymentResponseCode;
  }

  public void setExpectBankPaymentResponseCode(Integer expectBankPaymentResponseCode) {
    this.expectBankPaymentResponseCode = expectBankPaymentResponseCode;
  }

  public BankPaymentResponse getExpectBankPaymentResponseBody() {
    return expectBankPaymentResponseBody;
  }

  public void setExpectBankPaymentResponseBody(BankPaymentResponse expectBankPaymentResponseBody) {
    this.expectBankPaymentResponseBody = expectBankPaymentResponseBody;
  }

  public PostPaymentResponse getExpectedPaymentResponse() {
    return expectedPaymentResponse;
  }

  public void setExpectedPaymentResponse(PostPaymentResponse expectedPaymentResponse) {
    this.expectedPaymentResponse = expectedPaymentResponse;
  }

  public Boolean getExpectPaymentException() {
    return expectPaymentException;
  }

  public void setExpectPaymentException(Boolean expectPaymentException) {
    this.expectPaymentException = expectPaymentException;
  }

  public String getExpectErrorMessage() {
    return expectErrorMessage;
  }

  public void setExpectErrorMessage(String expectErrorMessage) {
    this.expectErrorMessage = expectErrorMessage;
  }

  public String getSimulateException() {
    return simulateException;
  }

  public void setSimulateException(String simulateException) {
    this.simulateException = simulateException;
  }
}
