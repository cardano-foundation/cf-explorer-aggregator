package org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.entity.UniqueAddressesInEpochEntity;

public interface UniqueAddressesInEpochRepository extends JpaRepository<UniqueAddressesInEpochEntity, String> {

    Optional<UniqueAddressesInEpochEntity> findByAddressAndEpoch(String address, int epoch);

    List<UniqueAddressesInEpochEntity> findAllByEpoch(int epoch);

    void deleteBySlotGreaterThan(long slot);

}
