package org.cardanofoundation.cfexploreraggregator.poolstatus.model.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PoolAggregationRecord(Integer epoch, Integer activePools, Integer registeredPools, Integer retiredPools) {
}
