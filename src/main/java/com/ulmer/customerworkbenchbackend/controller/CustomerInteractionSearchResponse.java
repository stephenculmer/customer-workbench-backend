package com.ulmer.customerworkbenchbackend.controller;

import com.ulmer.customerworkbenchbackend.data.CustomerInteraction;
import lombok.Data;

import java.util.List;

@Data
public class CustomerInteractionSearchResponse {
  private List<CustomerInteraction> content;
  private Long totalElements;
}
