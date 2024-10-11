package org.cardanofoundation.cfexploreraggregator.txcount.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.cardanofoundation.cfexploreraggregator.txcount.model.entity.AddressTxCountEntity;

public interface AddressTxCountRepository extends JpaRepository<AddressTxCountEntity, Long> {

    List<AddressTxCountEntity> findByAddressOrderBySlotDesc(String address);

    void deleteBySlotGreaterThan(long slot);
}
