package org.cardanofoundation.cfexploreraggregator.uniqueaccount.processor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.bloxbean.cardano.client.address.Address;
import com.bloxbean.cardano.client.address.AddressProvider;
import com.bloxbean.cardano.client.address.AddressType;
import com.bloxbean.cardano.yaci.core.model.byron.ByronTx;
import com.bloxbean.cardano.yaci.core.model.byron.ByronTxOut;
import com.bloxbean.cardano.yaci.core.model.byron.payload.ByronTxPayload;
import com.bloxbean.cardano.yaci.store.events.ByronMainBlockEvent;
import com.bloxbean.cardano.yaci.store.events.EpochChangeEvent;
import com.bloxbean.cardano.yaci.store.events.RollbackEvent;
import com.bloxbean.cardano.yaci.store.events.TransactionEvent;
import com.bloxbean.cardano.yaci.store.events.internal.CommitEvent;

import org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.entity.UniqueAccountEntity;
import org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.entity.UniqueAddressesInEpochEntity;
import org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.repository.UniqueAccountRepository;
import org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.repository.UniqueAddressesInEpochRepository;
import org.cardanofoundation.cfexploreraggregator.utility.AddressUtility;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        prefix = "aggregation.modules",
        name = "uniqueAccountCount-enabled",
        havingValue = "true"
)
public class UniqueAccountProcessor {

    private final UniqueAccountRepository uniqueAccountRepository;
    private final UniqueAddressesInEpochRepository uniqueAddressesInEpochRepository;

    private final Set<String> uniqueAccounts = new ConcurrentSkipListSet<>();


    @EventListener
    @Transactional
    public void handleTransactionEvent(TransactionEvent event) {
        event.getTransactions().forEach(tx -> tx.getUtxos().forEach(utxo -> processTransaction(utxo.getAddress())));
    }

    private void processTransaction(String address) {
        if(AddressUtility.isShelleyAddress(address)) {
            Address addr = new Address(address);
            if (addr.getAddressType() == AddressType.Base) {
                String stakeAddress = AddressProvider.getStakeAddress(addr).getAddress();
                uniqueAccounts.add(stakeAddress);
            } else {
                uniqueAccounts.add(address);
            }
        } else {
            // Byron Address
            uniqueAccounts.add(address);
        }
    }

    @EventListener
    @Transactional
    public void handleByronEvent(ByronMainBlockEvent event) {
        List<ByronTx> byronTxList = event.getByronMainBlock().getBody().getTxPayload()
                .stream()
                .map(ByronTxPayload::getTransaction).toList();
        byronTxList.forEach(byronTx -> {
            List<ByronTxOut> outputs = byronTx.getOutputs();
            outputs.forEach(output -> processTransaction(output.getAddress().getBase58Raw()));
        });
    }

    @EventListener
    @Transactional
    public void handleCommitEvent(CommitEvent commitEvent) {
        Set<String> snapshot = new HashSet<>(uniqueAccounts);
        uniqueAccounts.clear();
        // Getting all addresses in the current epoch and removing them from the snapshot
        uniqueAddressesInEpochRepository.findAllByEpochAndAddressIn(commitEvent.getMetadata().getEpochNumber(), snapshot)
                .forEach(uniqueAddressesInEpochEntity -> snapshot.remove(uniqueAddressesInEpochEntity.getAddress()));

        // add the remaining addresses from the snapshot to the current epoch
        List<UniqueAddressesInEpochEntity> accountsInEpoch = new ArrayList<>();
        snapshot.forEach(address -> accountsInEpoch.add(UniqueAddressesInEpochEntity.builder()
                .address(address)
                .epoch(commitEvent.getMetadata().getEpochNumber())
                .slot(commitEvent.getMetadata().getSlot())
                .build()));
        uniqueAddressesInEpochRepository.saveAll(accountsInEpoch);
    }

    @EventListener
    @Transactional
    public void handleEpochChangeEvent(EpochChangeEvent epochChangeEvent) {

        if(epochChangeEvent.getPreviousEpoch() == null) {
            return;
        }
        List<UniqueAddressesInEpochEntity> allByEpoch = uniqueAddressesInEpochRepository.findAllByEpoch(epochChangeEvent.getPreviousEpoch());
        uniqueAddressesInEpochRepository.deleteAll(allByEpoch);
        log.info("Epoch change - Saving {} unique addresses in epoch {}", allByEpoch.size(), epochChangeEvent.getPreviousEpoch());
        UniqueAccountEntity build = UniqueAccountEntity.builder()
                .epoch(epochChangeEvent.getPreviousEpoch())
                .uniqueAccounts(allByEpoch.size())
                .build();
        uniqueAccountRepository.save(build);
    }

    @EventListener
    @Transactional
    public void processRollbackEvent(RollbackEvent rollbackEvent) {
        log.info("Rollback event received. Rolling back transactions to slot {}", rollbackEvent.getRollbackTo().getSlot());
        uniqueAddressesInEpochRepository.deleteBySlotGreaterThan(rollbackEvent.getRollbackTo().getSlot());
    }

}
