package com.ulmer.customerworkbenchbackend.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CustomerInteraction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long productId;

  @Column(nullable = false)
  private Long customerId;

  private Integer customerRating;

  @Column(length = 1000)
  private String feedback;

  private Instant timestamp;

  @Column(length = 1000)
  private String responsesFromCustomerSupport;

  @Column(name = "interaction_type")
  @Enumerated(EnumType.STRING)
  private InteractionType interactionType;

  @Column(length = 1000)
  private String message;

}
