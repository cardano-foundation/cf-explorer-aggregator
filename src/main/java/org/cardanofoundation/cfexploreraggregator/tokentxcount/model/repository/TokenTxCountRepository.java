package org.cardanofoundation.cfexploreraggregator.tokentxcount.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.cardanofoundation.cfexploreraggregator.tokentxcount.model.entity.TokenTxCountEntity;

public interface TokenTxCountRepository extends JpaRepository<TokenTxCountEntity, Long> {

    List<TokenTxCountEntity> findByUnitOrderBySlotDesc(String assetId);

    void deleteBySlotGreaterThan(long slot);
}
