package com.ulmer.customerworkbenchbackend.controller;


import com.ulmer.customerworkbenchbackend.data.InteractionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class CustomerInteractionRequest {
  private Long customerId;
  private InteractionType interactionType;
  private Instant timestampStart;
  private Instant timestampEnd;
  @NotNull
  private Integer pageNumber;
  @NotNull
  private Integer pageSize;
}
