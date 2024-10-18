package org.cardanofoundation.cfexploreraggregator.poolstatus.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.cardanofoundation.cfexploreraggregator.poolstatus.model.entity.PoolStatusEntity;

public interface PoolStatusRepository extends JpaRepository<PoolStatusEntity, Long> {

    Optional<PoolStatusEntity> findByPoolId(String poolId);

    Integer countByRetired(boolean retired);
}
