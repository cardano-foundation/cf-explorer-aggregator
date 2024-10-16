package org.cardanofoundation.cfexploreraggregator.network.monitoring.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TransactionAdoptionMetadata(String timestamp, String absoluteSlot) {
}
