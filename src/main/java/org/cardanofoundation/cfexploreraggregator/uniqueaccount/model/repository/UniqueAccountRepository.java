package org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.entity.UniqueAccountEntity;

public interface UniqueAccountRepository extends JpaRepository<UniqueAccountEntity, Long> {
}
