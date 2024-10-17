package org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UniqueAccountRecord(Integer epoch, Integer uniqueAccounts) {
}
