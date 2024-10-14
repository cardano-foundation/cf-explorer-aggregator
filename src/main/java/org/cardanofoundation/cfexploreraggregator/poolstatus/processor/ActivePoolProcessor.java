package org.cardanofoundation.cfexploreraggregator.poolstatus.processor;

import java.util.HashSet;
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
import com.bloxbean.cardano.yaci.store.events.BlockEvent;
import com.bloxbean.cardano.yaci.store.events.EpochChangeEvent;

import org.cardanofoundation.cfexploreraggregator.poolstatus.model.entity.ActivePoolEntity;
import org.cardanofoundation.cfexploreraggregator.poolstatus.model.repository.ActivePoolRepository;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        prefix = "explorer.aggregation",
        name = "poolstatus-enabled",
        havingValue = "true"
)
public class ActivePoolProcessor {

    private final ActivePoolRepository activePoolRepository;

    Map<Integer, Set<String>> activePools = new ConcurrentHashMap<>();

    @Value("${explorer.aggregation.poolstatus.active-pool-threshold}")
    private int activePoolThreshold;

    @Transactional
    @EventListener
    public void processBlockEvent(BlockEvent blockEvent) {
        String slotLeader = blockEvent.getMetadata().getSlotLeader();
        int epochNumber = blockEvent.getMetadata().getEpochNumber();
        activePools.merge(epochNumber, Set.of(slotLeader), (oldValue, newValue) -> {
            Set<String> newSet = new HashSet<>(oldValue);
            newSet.addAll(newValue);
            return newSet;
        });
    }

    @Transactional
    @EventListener
    public void processEpochEvent(EpochChangeEvent epochChangeEvent) {
        Integer epoch = epochChangeEvent.getEpoch();
        int activePoolsCount = 0;
        for(int i = 0; i < activePoolThreshold; i++) {
            activePoolsCount += this.activePools.getOrDefault(epoch - i, Set.of()).size();
        }
        activePoolRepository.save(ActivePoolEntity.builder()
                .activePools(activePoolsCount)
                .epoch(epoch)
                .build());
    }

}
