package org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.entity.AccountInEpochEntity;

public interface AddressInEpochRepository extends JpaRepository<AccountInEpochEntity, String> {

    Optional<AccountInEpochEntity> findByAddressAndEpoch(String address, int epoch);

    List<AccountInEpochEntity> findAllByEpoch(int epoch);

    void deleteBySlotGreaterThan(long slot);

}
