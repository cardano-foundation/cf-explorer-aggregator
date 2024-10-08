package org.cardanofoundation.cfexploreraggregator.txcount.processor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.bloxbean.cardano.yaci.core.model.byron.ByronTx;
import com.bloxbean.cardano.yaci.core.model.byron.ByronTxOut;
import com.bloxbean.cardano.yaci.helper.model.Utxo;
import com.bloxbean.cardano.yaci.store.events.ByronMainBlockEvent;
import com.bloxbean.cardano.yaci.store.events.RollbackEvent;
import com.bloxbean.cardano.yaci.store.events.TransactionEvent;

import org.cardanofoundation.cfexploreraggregator.txcount.model.entity.AddressTxCountEntity;
import org.cardanofoundation.cfexploreraggregator.txcount.model.repository.AddressTxCountRepository;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        prefix = "explorer.aggregation",
        name = "ddressTxCount-enabled",
        havingValue = "true"
)
public class AddressTxCountProcessor {

    private final AddressTxCountRepository addressTxCountRepository;

    @EventListener
    public void handleTransactionEvent(TransactionEvent event) {
        event.getTransactions().forEach(tx -> {
            Set<String> addresses = tx.getUtxos().stream().map(Utxo::getAddress).collect(Collectors.toSet());
            addresses.forEach(address -> {
                processTransaction(event.getMetadata().getSlot(), address);
            });
        });

    }

    @EventListener
    public void handleByronEvent(ByronMainBlockEvent event) {
        List<ByronTx> byronTxList = event.getByronMainBlock().getBody().getTxPayload()
                .stream()
                .map(byronTxPayload -> byronTxPayload.getTransaction()).toList();
        byronTxList.forEach(byronTx -> {
            List<ByronTxOut> outputs = byronTx.getOutputs();
            outputs.forEach(output -> {
                processTransaction(event.getMetadata().getSlot(), output.getAddress().getBase58Raw());
            });
        });

    }

    private void processTransaction(long slot, String address) {
        List<AddressTxCountEntity> byAddressOrderBySlotDesc = addressTxCountRepository.findByAddressOrderBySlotDesc(address);
        if(byAddressOrderBySlotDesc.isEmpty()){
            AddressTxCountEntity addressTxCountEntity = AddressTxCountEntity.builder()
                    .address(address)
                    .txCount(0L)
                    .slot(slot)
                    .build();
            byAddressOrderBySlotDesc.add(addressTxCountEntity);
        }
        AddressTxCountEntity addressTxCountEntity = byAddressOrderBySlotDesc.getFirst();
        addressTxCountEntity.setSlot(slot);
        addressTxCountEntity.setTxCount(addressTxCountEntity.getTxCount() + 1);
        addressTxCountRepository.save(addressTxCountEntity);

        // clean up process
        if(slot - byAddressOrderBySlotDesc.getFirst().getSlot() > 10 ) {
            addressTxCountRepository.deleteAll(byAddressOrderBySlotDesc);
        }
    }


    @EventListener
    @Transactional
    public void processRollbackEvent(RollbackEvent rollbackEvent) {
        log.info("Rollback event received. Rolling back transactions to slot {}", rollbackEvent.getRollbackTo().getSlot());
        addressTxCountRepository.deleteBySlotGreaterThan(rollbackEvent.getRollbackTo().getSlot());
    }
}
