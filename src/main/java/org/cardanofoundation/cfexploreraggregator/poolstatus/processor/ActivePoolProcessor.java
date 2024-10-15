package org.cardanofoundation.cfexploreraggregator.poolstatus.processor;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.bloxbean.cardano.yaci.core.model.certs.Certificate;
import com.bloxbean.cardano.yaci.core.model.certs.CertificateType;
import com.bloxbean.cardano.yaci.store.events.BlockEvent;
import com.bloxbean.cardano.yaci.store.events.CertificateEvent;
import com.bloxbean.cardano.yaci.store.events.EpochChangeEvent;

import org.cardanofoundation.cfexploreraggregator.poolstatus.model.entity.ActivePoolEntity;
import org.cardanofoundation.cfexploreraggregator.poolstatus.model.repository.ActivePoolRepository;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        prefix = "aggregation.modules",
        name = "poolstatus-enabled",
        havingValue = "true"
)
public class ActivePoolProcessor {

    private final ActivePoolRepository activePoolRepository;

    Map<Integer, Set<String>> activePools = new ConcurrentHashMap<>();

    @Value("${aggregation.configuration.poolstatus.active-pool-threshold}")
    private int activePoolThreshold;

    private final AtomicInteger registrations = new AtomicInteger(0);

    @Transactional
    @EventListener
    public void processCertificateEvent(CertificateEvent certificateEvent) {
        certificateEvent.getTxCertificatesList().forEach(txCertificates -> {
            List<Certificate> certificates = txCertificates.getCertificates();
            certificates.forEach(certificate -> {
                if(certificate.getType() == CertificateType.POOL_REGISTRATION) {
                    registrations.incrementAndGet();
                } else if(certificate.getType() == CertificateType.POOL_RETIREMENT) {
                    registrations.decrementAndGet();
                }
            });
        });
    }

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
        Set<String> activePoolsOverEpoch = new HashSet<>();
        for(int i = 0; i <= activePoolThreshold; i++) {
            activePoolsOverEpoch.addAll(this.activePools.getOrDefault(epoch - i, Set.of()));
        }
        activePools.remove(epoch - activePoolThreshold); // remove old epoch, this won't be taken into account anymore

        activePoolRepository.save(ActivePoolEntity.builder()
                .activePoolCount(activePoolsOverEpoch.size())
                .epoch(epoch)
                .build());
    }

}
