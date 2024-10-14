package org.cardanofoundation.cfexploreraggregator.poolstatus.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.cardanofoundation.cfexploreraggregator.poolstatus.model.entity.ActivePoolEntity;

public interface ActivePoolRepository extends JpaRepository<ActivePoolEntity, Long> {

    @Query(value = "SELECT a FROM ActivePoolEntity a ORDER BY a.epoch DESC LIMIT 1")
    Optional<ActivePoolEntity> getLatestActivePool();
}
