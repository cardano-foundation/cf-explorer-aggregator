package org.cardanofoundation.cfexploreraggregator.poolstatus.model.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PoolStatusRecord(String poolId, boolean retired, Long updatedSlot) {
}
