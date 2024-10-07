package org.cardanofoundation.cfexploreraggregator.txcount.model.repository;

import org.cardanofoundation.cfexploreraggregator.txcount.model.entity.AddressTxCountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressTxCountRepository extends JpaRepository<AddressTxCountEntity, Long> {

    List<AddressTxCountEntity> findByAddressOrderBySlotDesc(String address);

    void deleteBySlotGreaterThan(long slot);

}
