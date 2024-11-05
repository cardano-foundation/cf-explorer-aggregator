package org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.entity.UniqueAddressesInEpochEntity;

public interface UniqueAddressesInEpochRepository extends JpaRepository<UniqueAddressesInEpochEntity, String> {

    Optional<UniqueAddressesInEpochEntity> findByAddressAndEpoch(String address, int epoch);

    List<UniqueAddressesInEpochEntity> findAllByEpoch(int epoch);
    List<UniqueAddressesInEpochEntity> findAllByEpochAndAddressIn(int epoch, Set<String> addresses);

    void deleteBySlotGreaterThan(long slot);

}
