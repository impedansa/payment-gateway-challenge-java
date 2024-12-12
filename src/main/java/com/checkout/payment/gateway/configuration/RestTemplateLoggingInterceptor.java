package com.checkout.payment.gateway.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RestTemplateLoggingInterceptor implements ClientHttpRequestInterceptor {

  private static final Logger LOG = LoggerFactory.getLogger(RestTemplateLoggingInterceptor.class);

  @Override
  public ClientHttpResponse intercept(
      HttpRequest request,
      byte[] body,
      ClientHttpRequestExecution execution
  ) throws IOException {
    logRequest(request, body);
    ClientHttpResponse response = execution.execute(request, body);
    logResponse(response);
    return response;
  }

  private void logRequest(HttpRequest request, byte[] body) {
    LOG.info("Sending request: [{}] {} {}",
        request.getMethod(),
        request.getURI(),
        new String(body, StandardCharsets.UTF_8));
  }

  private void logResponse(ClientHttpResponse response) throws IOException {
    LOG.info("Received response: {} {}",
        response.getStatusCode(),
        response.getStatusText());
  }
}
