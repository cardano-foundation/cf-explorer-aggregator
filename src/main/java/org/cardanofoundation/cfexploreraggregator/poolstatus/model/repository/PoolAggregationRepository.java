package org.cardanofoundation.cfexploreraggregator.poolstatus.model.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import org.cardanofoundation.cfexploreraggregator.poolstatus.model.entity.PoolAggregationEntity;

public interface PoolAggregationRepository extends JpaRepository<PoolAggregationEntity, Long> {

    PoolAggregationEntity findTopByOrderByEpochDesc();
}
