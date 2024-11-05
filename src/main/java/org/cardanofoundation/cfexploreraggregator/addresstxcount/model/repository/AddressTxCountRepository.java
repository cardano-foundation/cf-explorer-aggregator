package org.cardanofoundation.cfexploreraggregator.addresstxcount.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.cardanofoundation.cfexploreraggregator.addresstxcount.model.entity.AddressTxCountEntity;

public interface AddressTxCountRepository extends JpaRepository<AddressTxCountEntity, Long> {

    Optional<AddressTxCountEntity> findTopByAddressOrderBySlotDesc(String address);

    List<AddressTxCountEntity> findAllByAddressIn(List<String> addresses);

    void deleteBySlotGreaterThan(long slot);
}
