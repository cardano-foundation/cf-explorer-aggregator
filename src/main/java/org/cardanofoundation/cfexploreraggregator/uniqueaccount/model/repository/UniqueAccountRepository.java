package org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.entity.UniqueAccountEntity;

public interface UniqueAccountRepository extends JpaRepository<UniqueAccountEntity, Long> {

    @Query(value = "SELECT u FROM UniqueAccountEntity u ORDER BY epoch DESC LIMIT 1")
    UniqueAccountEntity getLatestUniqueAccount();

    Optional<UniqueAccountEntity> findByEpoch(Integer epoch);
}
