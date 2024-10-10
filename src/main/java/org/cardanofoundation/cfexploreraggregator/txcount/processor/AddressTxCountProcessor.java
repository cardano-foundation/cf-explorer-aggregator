package org.cardanofoundation.cfexploreraggregator.txcount.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.bloxbean.cardano.client.util.Tuple;
import com.bloxbean.cardano.yaci.core.model.byron.ByronTx;
import com.bloxbean.cardano.yaci.core.model.byron.ByronTxOut;
import com.bloxbean.cardano.yaci.core.model.byron.payload.ByronTxPayload;
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
        name = "addressTxCount-enabled",
        havingValue = "true"
)
public class AddressTxCountProcessor {

    private final AddressTxCountRepository addressTxCountRepository;

    private final ConcurrentHashMap<String, Tuple<Long, Long>> hashCounts = new ConcurrentHashMap<>();

    private final ScheduledExecutorService dbWriteScheduler = Executors.newScheduledThreadPool(1);

    @Value("${explorer.aggregation.addressTxCount.Safe-Slot-Distance}")
    private long safeSlotDistance;

    @PostConstruct
    private void init() {
        // flushing every 10 seconds to the DB
        dbWriteScheduler.scheduleAtFixedRate(this::flushToDatabase, 10, 10, TimeUnit.SECONDS);
    }

    @EventListener
    @Transactional
    public void handleTransactionEvent(TransactionEvent event) {
        event.getTransactions().forEach(tx -> {
            Set<String> addresses = tx.getUtxos().stream().map(Utxo::getAddress).collect(Collectors.toSet());
            addresses.forEach(address -> processTransaction(event.getMetadata().getSlot(), address));
        });
    }

    @EventListener
    @Transactional
    public void handleByronEvent(ByronMainBlockEvent event) {
        List<ByronTx> byronTxList = event.getByronMainBlock().getBody().getTxPayload()
                .stream()
                .map(ByronTxPayload::getTransaction).toList();
        byronTxList.forEach(byronTx -> {
            List<ByronTxOut> outputs = byronTx.getOutputs();
            outputs.forEach(output -> processTransaction(event.getMetadata().getSlot(), output.getAddress().getBase58Raw()));
        });
    }

    private void processTransaction(long slot, String address) {
        hashCounts.merge(address, new Tuple<>(slot, 1L), (longLongTuple, longLongTuple2) -> {
            longLongTuple._1 = Math.max(longLongTuple._1, longLongTuple2._1);
            longLongTuple._2 += longLongTuple2._2;
            return longLongTuple;
        });
    }

    private void flushToDatabase() {
        try {
            Map<String, Tuple<Long, Long>> snapshot = new HashMap<>(hashCounts);
            hashCounts.clear();

            List<AddressTxCountEntity> entities = snapshot.entrySet().stream()
                    .map(entry -> {
                        List<AddressTxCountEntity> byAddressOrderByTxCountDesc = addressTxCountRepository.findByAddressOrderBySlotDesc(entry.getKey());
                        long txCount = 0L;
                        long slot = 0L;
                        if(!byAddressOrderByTxCountDesc.isEmpty()) {
                            txCount = byAddressOrderByTxCountDesc.getFirst().getTxCount();
                            slot = byAddressOrderByTxCountDesc.getFirst().getSlot();
                        }

                        return AddressTxCountEntity.builder()
                                .address(entry.getKey())
                                .txCount(txCount + entry.getValue()._2)
                                .slot(Math.max(slot, entry.getValue()._1))
                                .build();
                    })
                    .toList();
            addressTxCountRepository.saveAll(entities);
            log.info("Saved {} address tx count entities", entities.size());
            // getting the max slot from the snapshot and cleaning up the DB
            snapshot.entrySet().stream().max((o1, o2) -> (int) (o1.getValue()._1 - o2.getValue()._1))
                    .ifPresent(entry -> addressTxCountRepository.cleanUpDB(entry.getValue()._1 - safeSlotDistance));
        } catch (Exception e) {
            log.error("Error flushing to database");
        }
    }

    @EventListener
    @Transactional
    public void processRollbackEvent(RollbackEvent rollbackEvent) {
        log.info("Rollback event received. Rolling back transactions to slot {}", rollbackEvent.getRollbackTo().getSlot());
        addressTxCountRepository.deleteBySlotGreaterThan(rollbackEvent.getRollbackTo().getSlot());
    }
}
