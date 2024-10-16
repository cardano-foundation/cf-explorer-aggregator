package org.cardanofoundation.cfexploreraggregator.network.monitoring.repository;

import com.bloxbean.cardano.yaci.store.utxo.storage.impl.model.UtxoId;
import org.cardanofoundation.cfexploreraggregator.network.monitoring.entity.TransactionAdoption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionAdoptionRepository extends JpaRepository<TransactionAdoption, UtxoId> {

    void deleteBySlotGreaterThan(long slot);

}
