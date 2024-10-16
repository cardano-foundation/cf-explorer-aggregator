package org.cardanofoundation.cfexploreraggregator.network.monitoring.entity;

import com.bloxbean.cardano.yaci.store.utxo.storage.impl.model.UtxoId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transaction_adoption")
@IdClass(UtxoId.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionAdoption {

    @Id
    private String txHash;
    @Id
    private Integer outputIndex;

    private Long slotDifference;

    private Long timestamp;

    private Long absoluteSlot;

    private Long slot;

}
