package com.checkout.payment.gateway.controller;

import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.service.PaymentGatewayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("api")
@RequestMapping("/payment")
public class PaymentGatewayController {

  private final PaymentGatewayService paymentGatewayService;

  public PaymentGatewayController(PaymentGatewayService paymentGatewayService) {
    this.paymentGatewayService = paymentGatewayService;
  }

  @Operation(summary = "Get payment by ID")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Successful operation.",
          content = {
              @Content(mediaType = "application/json",
                  schema = @Schema(implementation = PostPaymentResponse.class),
                  examples = @ExampleObject(value = "{\"id\": \"63181713-c68f-4ae6-92ef-1a601884a11e\", \"status\": \"Authorized\", \"cardNumberLastFour\": 8877, \"expiryMonth\": 4, \"expiryYear\": 2025, \"currency\": \"GBP\", \"amount\": 100}")
              )
          }
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Payment info not found.",
          content = {
              @Content(mediaType = "application/json",
                  schema = @Schema(implementation = PostPaymentResponse.class),
                  examples = @ExampleObject(value = "{\"message\": \"Invalid ID\"}")
              )
          }
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Processing failure.",
          content = {
              @Content(mediaType = "application/json",
                  schema = @Schema(implementation = EventProcessingException.class),
                  examples = @ExampleObject(value = "{ \"message\": \"Operation failed. Please try again later.\" }")
              )
          }
      )
  })
  @GetMapping("/{id}")
  public ResponseEntity<PostPaymentResponse> getPostPaymentEventById(@PathVariable UUID id) {
    return new ResponseEntity<>(paymentGatewayService.getPaymentById(id), HttpStatus.OK);
  }

  @Operation(summary = "Process payment")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Successful operation.",
          content = {
              @Content(mediaType = "application/json",
                  schema = @Schema(implementation = PostPaymentResponse.class),
                  examples = @ExampleObject(value = "{\"id\": \"63181713-c68f-4ae6-92ef-1a601884a11e\", \"status\": \"Authorized\", \"cardNumberLastFour\": 8877, \"expiryMonth\": 4, \"expiryYear\": 2025, \"currency\": \"GBP\", \"amount\": 100}")
              )
          }
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Validation failure. Payment request is rejected.",
          content = {
              @Content(mediaType = "application/json",
                  schema = @Schema(implementation = PostPaymentResponse.class),
                  examples = @ExampleObject(value = "{\"id\": \"63181713-c68f-4ae6-92ef-1a601884a11e\", \"status\": \"Rejected\", \"cardNumberLastFour\": 8877, \"expiryMonth\": 4, \"expiryYear\": 2020, \"currency\": \"ABC\", \"amount\": 100}")
              )
          }
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Processing failure.",
          content = {
              @Content(mediaType = "application/json",
                  schema = @Schema(implementation = EventProcessingException.class),
                  examples = @ExampleObject(value = "{ \"message\": \"Bank simulator server error occurred.\" }")
              )
          }
      )
  })
  @PostMapping
  public ResponseEntity<PostPaymentResponse> processPayment(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Payment processing request", required = true,
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = PostPaymentRequest.class),
              examples = @ExampleObject(value = "{ \"card_number\": \"2222405343248877\", \"expiry_month\": 4, \"expiry_year\": 2025, \"currency\": \"GBP\", \"amount\": 100, \"cvv\": \"123\" }")
          )
      )
      @RequestBody PostPaymentRequest postPaymentRequest
  ) {
    PostPaymentResponse postPaymentResponse = paymentGatewayService.processPayment(
        postPaymentRequest);
    return new ResponseEntity<>(postPaymentResponse, HttpStatus.OK);
  }
}
