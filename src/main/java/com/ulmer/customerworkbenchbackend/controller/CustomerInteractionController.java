package com.ulmer.customerworkbenchbackend.controller;

import com.ulmer.customerworkbenchbackend.data.CustomerInteraction;
import com.ulmer.customerworkbenchbackend.service.CustomerInteractionService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("customer-interaction")
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class CustomerInteractionController {

  private final CustomerInteractionService customerInteractionService;

  @PostMapping(
    value = "/upload",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public ResponseEntity<Void> uploadCsv(@RequestParam("file") MultipartFile file) {
    try {
      customerInteractionService.persistCustomerInteractions(file.getInputStream());
      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (IOException e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping(
    value = "upload",
    consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<Void> uploadJson(@RequestBody
                                            List<CustomerInteraction> customerInteractions
                                            ) {

    customerInteractionService.persistCustomerInteractions(customerInteractions);

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @PostMapping(value = "/search", consumes =  MediaType.APPLICATION_JSON_VALUE)
  public CustomerInteractionSearchResponse getCustomerInteractions(@RequestBody CustomerInteractionRequest request) {
    Page<CustomerInteraction> page = customerInteractionService.searchCustomerInteractions(
      request.getCustomerId(),
      request.getInteractionType(),
      request.getTimestampStart(),
      request.getTimestampEnd(),
      request.getPageNumber(),
      request.getPageSize()
    );

    CustomerInteractionSearchResponse searchResponse = new CustomerInteractionSearchResponse();
    searchResponse.setContent(page.getContent());
    searchResponse.setTotalElements(page.getTotalElements());

    return searchResponse;
  }
}
