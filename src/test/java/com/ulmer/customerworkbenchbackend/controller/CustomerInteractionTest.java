package com.ulmer.customerworkbenchbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ulmer.customerworkbenchbackend.data.CustomerInteraction;
import com.ulmer.customerworkbenchbackend.data.CustomerInteractionRepository;
import com.ulmer.customerworkbenchbackend.data.InteractionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerInteractionTest {

  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

  @Autowired
  private CustomerInteractionRepository customerInteractionRepository;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    customerInteractionRepository.deleteAll();
  }

  @Test
  void persistCsvAndRetrieveCustomerInteractions() throws Exception {
    // Load MOCK_DATA.csv from test resources
    ClassPathResource csvResource = new ClassPathResource("MOCK_DATA.csv");
    MockMultipartFile file = new MockMultipartFile(
      "file",
      "MOCK_DATA.csv",
      "text/csv",
      csvResource.getInputStream()
    );

    // Upload the CSV file
    mockMvc.perform(multipart("/customer-interaction/upload")
        .file(file))
      .andExpect(status().isCreated());

    // Verify data was persisted
    long totalCount = customerInteractionRepository.count();
    assertThat(totalCount).isGreaterThan(0);

    // Retrieve data using the search endpoint
    CustomerInteractionRequest searchRequest = new CustomerInteractionRequest();
    searchRequest.setPageNumber(0);
    searchRequest.setPageSize(10);

    MvcResult result = mockMvc.perform(post("/customer-interaction/search")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(searchRequest)))
      .andExpect(status().isOk())
      .andReturn();

    // Parse the response
    String responseContent = result.getResponse().getContentAsString();
    CustomerInteractionSearchResponse response = objectMapper.readValue(
      responseContent,
      CustomerInteractionSearchResponse.class
    );

    // Verify the retrieved data
    assertThat(response.getTotalElements()).isEqualTo(totalCount);
    assertThat(response.getContent()).hasSize(10);
    assertThat(response.getContent().get(0).getProductId()).isNotNull();
    assertThat(response.getContent().get(0).getCustomerId()).isNotNull();
    assertThat(response.getContent().get(0).getInteractionType()).isIn(InteractionType.values());
  }

  @Test
  void searchByCustomerId() throws Exception {
    // Upload CSV data first
    ClassPathResource csvResource = new ClassPathResource("MOCK_DATA.csv");
    MockMultipartFile file = new MockMultipartFile(
      "file",
      "MOCK_DATA.csv",
      "text/csv",
      csvResource.getInputStream()
    );

    mockMvc.perform(multipart("/customer-interaction/upload")
        .file(file))
      .andExpect(status().isCreated());

    // Search for specific customer (customer_id = 1 from the CSV)
    CustomerInteractionRequest searchRequest = new CustomerInteractionRequest();
    searchRequest.setCustomerId(1L);
    searchRequest.setPageNumber(0);
    searchRequest.setPageSize(10);

    MvcResult result = mockMvc.perform(post("/customer-interaction/search")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(searchRequest)))
      .andExpect(status().isOk())
      .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    CustomerInteractionSearchResponse response = objectMapper.readValue(
      responseContent,
      CustomerInteractionSearchResponse.class
    );

    // Verify all results are for customer_id = 1
    assertThat(response.getContent()).isNotEmpty();
    assertThat(response.getContent()).allMatch(interaction -> interaction.getCustomerId().equals(1L));
  }

  @Test
  void searchByInteractionType() throws Exception {
    // Upload CSV data first
    ClassPathResource csvResource = new ClassPathResource("MOCK_DATA.csv");
    MockMultipartFile file = new MockMultipartFile(
      "file",
      "MOCK_DATA.csv",
      "text/csv",
      csvResource.getInputStream()
    );

    mockMvc.perform(multipart("/customer-interaction/upload")
        .file(file))
      .andExpect(status().isCreated());

    // Search for CHAT interactions
    CustomerInteractionRequest searchRequest = new CustomerInteractionRequest();
    searchRequest.setInteractionType(InteractionType.CHAT);
    searchRequest.setPageNumber(0);
    searchRequest.setPageSize(10);

    MvcResult result = mockMvc.perform(post("/customer-interaction/search")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(searchRequest)))
      .andExpect(status().isOk())
      .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    CustomerInteractionSearchResponse response = objectMapper.readValue(
      responseContent,
      CustomerInteractionSearchResponse.class
    );

    // Verify all results are CHAT type
    assertThat(response.getContent()).isNotEmpty();
    assertThat(response.getContent()).allMatch(interaction -> interaction.getInteractionType() == InteractionType.CHAT);
  }
}
