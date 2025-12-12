package com.ulmer.customerworkbenchbackend.service;

import com.ulmer.customerworkbenchbackend.data.CustomerInteraction;
import com.ulmer.customerworkbenchbackend.data.CustomerInteractionRepository;
import com.ulmer.customerworkbenchbackend.data.InteractionType;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import tools.jackson.databind.MappingIterator;
import tools.jackson.dataformat.csv.CsvMapper;
import tools.jackson.dataformat.csv.CsvSchema;

import java.io.InputStream;
import java.time.Instant;
import java.util.List;

@Component
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class CustomerInteractionService {

  private final CustomerInteractionRepository customerInteractionRepository;

  public void persistCustomerInteractions(InputStream inputStream) {

    List<CustomerInteraction> customerInteractions = parseCsv(inputStream).stream()
      .map(dto -> CustomerInteraction.builder()
        .productId(dto.getProductId())
        .customerId(dto.getCustomerId())
        .customerRating(dto.getCustomerRating())
        .feedback(dto.getFeedback())
        .timestamp(dto.getTimestamp())
        .responsesFromCustomerSupport(dto.getResponsesFromCustomerSupport())
        .interactionType(dto.getInteractionType())
        .message(dto.getMessage())
        .build())
      .toList();

    customerInteractionRepository.saveAll(customerInteractions);

  }

  public void persistCustomerInteractions(List<CustomerInteraction> customerInteractions) {
    customerInteractionRepository.saveAll(customerInteractions);
  }

  public Page<CustomerInteraction> searchCustomerInteractions(
    Long customerId,
    InteractionType interactionType,
    Instant timestampStart,
    Instant timestampEnd,
    Integer pageNumber,
    Integer pageSize
  ) {

    Page<CustomerInteraction> customerInteractions = customerInteractionRepository.find(customerId, interactionType, timestampStart, timestampEnd,
                                              PageRequest.of(pageNumber, pageSize)
    );

    return customerInteractions;
  }

  private List<CustomerInteractionDto> parseCsv(InputStream inputStream) {
    CsvMapper mapper = new CsvMapper();

    CsvSchema schema =  CsvSchema.emptySchema().withHeader();

    MappingIterator<CustomerInteractionDto> iterator = mapper
      .readerFor(CustomerInteractionDto.class)
      .with(schema)
      .readValues(inputStream);

    return iterator.readAll();
  }
}
