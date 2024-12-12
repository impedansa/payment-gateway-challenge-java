package com.checkout.payment.gateway.util;

import com.checkout.payment.gateway.exception.EventProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpUtils {

  private static final Logger LOG = LoggerFactory.getLogger(HttpUtils.class);

  private final RestTemplate restTemplate;

  public HttpUtils(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Retryable(
      retryFor = {EventProcessingException.class},
      maxAttempts = 5,
      backoff = @Backoff(delay = 1000)
  )
  public <T> T post(String url, Object request, MultiValueMap<String, String> headers,
      Class<T> responseType) {
    HttpEntity<Object> requestEntity = new HttpEntity<>(request, headers);

    ResponseEntity<T> responseEntity;

    try {
      responseEntity = restTemplate.postForEntity(url, requestEntity, responseType);
    } catch (HttpServerErrorException e) {
      LOG.error("Server error while invoking : {} | Status: {} - {}", url, e.getStatusCode(), e.getResponseBodyAsString());
      throw new EventProcessingException("Bank simulator server error occurred");
    } catch (ResourceAccessException e) {
      LOG.error("Connection error while invoking : {} | Reason: {}", url, e.getMessage());
      throw new EventProcessingException("Bank simulator connection error occurred");
    }
    return responseEntity.getBody();
  }
}
