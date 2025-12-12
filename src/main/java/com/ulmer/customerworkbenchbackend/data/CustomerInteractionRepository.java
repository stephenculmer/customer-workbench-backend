package com.ulmer.customerworkbenchbackend.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.Instant;

public interface CustomerInteractionRepository extends PagingAndSortingRepository<CustomerInteraction, Long>, CrudRepository<CustomerInteraction, Long> {

  @Query("""
  select ci from CustomerInteraction ci
  where (:customerId is null or ci.customerId = :customerId)
    and (:interactionType is null or ci.interactionType = :interactionType)
    and ci.timestamp >= coalesce(:timestampAfter, ci.timestamp)
    and ci.timestamp <= coalesce(:timestampBefore, ci.timestamp)
  """)
  Page<CustomerInteraction> find(
    Long customerId,
    InteractionType interactionType,
    Instant timestampAfter,
    Instant timestampBefore,
    Pageable pageable);

}
