package org.cardanofoundation.cfexploreraggregator.tokentxcount.processor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.bloxbean.cardano.client.util.Tuple;
import com.bloxbean.cardano.yaci.store.events.RollbackEvent;
import com.bloxbean.cardano.yaci.store.events.TransactionEvent;
import com.bloxbean.cardano.yaci.store.events.internal.CommitEvent;

import org.cardanofoundation.cfexploreraggregator.tokentxcount.model.entity.TokenTxCountEntity;
import org.cardanofoundation.cfexploreraggregator.tokentxcount.model.repository.TokenTxCountRepository;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        prefix = "explorer.aggregation",
        name = "tokenTxCount-enabled",
        havingValue = "true"
)
public class TokenTxCountProcessor {

    private final TokenTxCountRepository tokenTxCountRepository;

    private final ConcurrentHashMap<String, Tuple<Long, Long>> hashCounts = new ConcurrentHashMap<>();

    @Value("${explorer.aggregation.addressTxCount.Safe-Slot-Distance}")
    private long safeSlotDistance;

    @EventListener
    @Transactional
    public void handleTransactionEvent(TransactionEvent event) {
        event.getTransactions().forEach(tx -> {
            Set<String> tokenNames = new HashSet<>();
            tx.getBody().getOutputs().forEach(transactionOutput ->
                    transactionOutput.getAmounts().forEach(amount ->
                            tokenNames.add(amount.getUnit())));
            tokenNames.forEach(tokenName -> processTransaction(event.getMetadata().getSlot(), tokenName));

        });
    }

    private void processTransaction(long slot, String tokenName) {
        hashCounts.merge(tokenName, new Tuple<>(slot, 1L), (oldValue, newValue) -> {
            oldValue._1 = Math.max(oldValue._1, newValue._1);
            oldValue._2 += newValue._2;
            return oldValue;
        });
    }

    @EventListener
    @Transactional
    public void handleCommitEvent (CommitEvent commitEvent) {
        Map<String, Tuple<Long, Long>> snapshot = new HashMap<>(hashCounts);
        hashCounts.clear();
        List<TokenTxCountEntity> list = snapshot.entrySet().stream().map(entry -> {
            List<TokenTxCountEntity> byTokenOrderByTxCountDesc = tokenTxCountRepository.findByUnitOrderBySlotDesc(entry.getKey());
            long txCount = 0L;
            long slot = 0L;
            if (!byTokenOrderByTxCountDesc.isEmpty()) {
                txCount = byTokenOrderByTxCountDesc.getFirst().getTxCount();
                slot = byTokenOrderByTxCountDesc.getFirst().getSlot();
            }

            // Deleting old values from DB
            List<TokenTxCountEntity> oldValues = byTokenOrderByTxCountDesc.stream().filter(entity -> entity.getSlot() < commitEvent.getMetadata().getSlot() - safeSlotDistance).toList();
            tokenTxCountRepository.deleteAll(oldValues);

            return TokenTxCountEntity.builder()
                    .unit(entry.getKey())
                    .slot(Math.max(slot, entry.getValue()._1))
                    .txCount(txCount + entry.getValue()._2)
                    .build();
        }).toList();
        tokenTxCountRepository.saveAll(list);
        log.info("Saved {} token tx count entities", list.size());
    }

    @EventListener
    @Transactional
    public void processRollbackEvent(RollbackEvent rollbackEvent) {
        log.info("Rollback event received. Rolling back transactions to slot {}", rollbackEvent.getRollbackTo().getSlot());
        tokenTxCountRepository.deleteBySlotGreaterThan(rollbackEvent.getRollbackTo().getSlot());
    }
}
