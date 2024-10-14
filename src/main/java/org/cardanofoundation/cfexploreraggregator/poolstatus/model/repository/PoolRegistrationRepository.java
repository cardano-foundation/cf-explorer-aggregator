package org.cardanofoundation.cfexploreraggregator.poolstatus.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.cardanofoundation.cfexploreraggregator.poolstatus.model.entity.PoolRegistrationEntity;

public interface PoolRegistrationRepository extends JpaRepository<PoolRegistrationEntity, Long> {

    @Query(value = "SELECT p FROM PoolRegistrationEntity p ORDER BY p.slot DESC LIMIT 1")
    Optional<PoolRegistrationEntity> getLatestPoolStatus();

    void deleteBySlotGreaterThan(Long slot);

}
