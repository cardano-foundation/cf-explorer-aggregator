package org.cardanofoundation.cfexploreraggregator.addresstxcount.model.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record AddressTxCountRecord(String address, Long txCount, Long slot) {
}
