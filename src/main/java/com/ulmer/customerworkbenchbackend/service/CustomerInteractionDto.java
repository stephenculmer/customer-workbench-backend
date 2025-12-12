package com.ulmer.customerworkbenchbackend.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ulmer.customerworkbenchbackend.data.InteractionType;
import lombok.Data;

import java.time.Instant;

@Data
public class CustomerInteractionDto {
  @JsonProperty("product_id")
  private Long productId;
  @JsonProperty("customer_id")
  private Long customerId;
  @JsonProperty("customer_rating")
  private Integer customerRating;
  private String feedback;
  private Instant timestamp;
  @JsonProperty("responses_from_customer_support")
  private String responsesFromCustomerSupport;
  @JsonProperty("interaction_type")
  private InteractionType interactionType;
  private String message;
}
