package org.cardanofoundation.cfexploreraggregator.assets.repository;

import org.cardanofoundation.cfexploreraggregator.assets.entity.TokenInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenInfoRepository extends JpaRepository<TokenInfo, String> {

    void deleteBySlotGreaterThan(long slot);

}
