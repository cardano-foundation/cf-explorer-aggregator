package org.cardanofoundation.cfexploreraggregator.txcount.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.cardanofoundation.cfexploreraggregator.txcount.model.entity.AddressTxCountEntity;

public interface AddressTxCountRepository extends JpaRepository<AddressTxCountEntity, Long> {

    List<AddressTxCountEntity> findByAddressOrderBySlotDesc(String address);

    void deleteBySlotGreaterThan(long slot);

    @Query(nativeQuery = true, value = """
        DELETE FROM address_tx_count WHERE slot < :slot
        AND address IN (SELECT address FROM address_tx_count GROUP BY address HAVING COUNT(*) > 1)
        """)
    void cleanUpDB(@Param("slot") long slot);
}
