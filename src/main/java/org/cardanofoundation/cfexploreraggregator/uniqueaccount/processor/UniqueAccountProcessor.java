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
import com.bloxbean.cardano.yaci.store.events.EpochChangeEvent;
import com.bloxbean.cardano.yaci.store.events.RollbackEvent;
import com.bloxbean.cardano.yaci.store.events.TransactionEvent;
import com.bloxbean.cardano.yaci.store.events.internal.CommitEvent;

import org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.entity.AccountInEpochEntity;
import org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.entity.UniqueAccountEntity;
import org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.repository.AddressInEpochRepository;
import org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.repository.UniqueAccountRepository;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        prefix = "explorer.aggregation",
        name = "uniqueAccountCount-enabled",
        havingValue = "true"
)
public class UniqueAccountProcessor {

    private final UniqueAccountRepository uniqueAccountRepository;
    private final AddressInEpochRepository addressInEpochRepository;

    private final Set<String> uniqueAccounts = new ConcurrentSkipListSet<>();


    @EventListener
    @Transactional
    public void handleTransactionEvent(TransactionEvent event) {
        event.getTransactions().forEach(tx -> tx.getUtxos().forEach(utxo -> {
            try {
            Address addr = new Address(utxo.getAddress());
            if(addr.getDelegationCredential().isEmpty()) {
                uniqueAccounts.add(utxo.getAddress());
            } else {
                String address = AddressProvider.getStakeAddress(addr).getAddress();
                uniqueAccounts.add(address);
            }
            } catch (RuntimeException e) {
                log.error("Error processing utxo address");
            }
        }));
    }

    @EventListener
    @Transactional
    public void handleCommitEvent(CommitEvent commitEvent) {
        Set<String> snapshot = new HashSet<>(uniqueAccounts);
        uniqueAccounts.clear();
        snapshot.forEach(address -> {
            Optional<AccountInEpochEntity> byAddressAndEpoch = addressInEpochRepository.findByAddressAndEpoch(address, commitEvent.getMetadata().getEpochNumber());
            List<AccountInEpochEntity> accountsInEpoch = new ArrayList<>();
            if(byAddressAndEpoch.isEmpty()) {
                accountsInEpoch.add(AccountInEpochEntity.builder()
                        .address(address)
                        .epoch(commitEvent.getMetadata().getEpochNumber())
                        .slot(commitEvent.getMetadata().getSlot())
                        .build());
            }
            addressInEpochRepository.saveAll(accountsInEpoch);
        });
    }

    @EventListener
    @Transactional
    public void handleEpochChangeEvent(EpochChangeEvent epochChangeEvent) {

        if(epochChangeEvent.getEpoch() == 0) {
            return;
        }
        List<AccountInEpochEntity> allByEpoch = addressInEpochRepository.findAllByEpoch(epochChangeEvent.getPreviousEpoch());
        addressInEpochRepository.deleteAll(allByEpoch);
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
        addressInEpochRepository.deleteBySlotGreaterThan(rollbackEvent.getRollbackTo().getSlot());
    }

}
