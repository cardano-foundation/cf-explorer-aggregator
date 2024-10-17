package org.cardanofoundation.cfexploreraggregator.poolstatus.model.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.cardanofoundation.cfexploreraggregator.poolstatus.model.entity.PoolAggregationEntity;

public interface PoolAggregationRepository extends JpaRepository<PoolAggregationEntity, Long> {

    @Query(value = "SELECT a FROM PoolAggregationEntity a ORDER BY a.epoch DESC LIMIT 1")
    PoolAggregationEntity getLatestPoolAggregation();
}
