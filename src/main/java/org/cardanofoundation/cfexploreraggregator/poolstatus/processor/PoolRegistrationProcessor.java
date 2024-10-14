package org.cardanofoundation.cfexploreraggregator.poolstatus.processor;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.bloxbean.cardano.yaci.core.model.certs.Certificate;
import com.bloxbean.cardano.yaci.core.model.certs.CertificateType;
import com.bloxbean.cardano.yaci.store.events.CertificateEvent;
import com.bloxbean.cardano.yaci.store.events.RollbackEvent;
import com.bloxbean.cardano.yaci.store.events.internal.CommitEvent;

import org.cardanofoundation.cfexploreraggregator.poolstatus.model.entity.PoolRegistrationEntity;
import org.cardanofoundation.cfexploreraggregator.poolstatus.model.repository.PoolRegistrationRepository;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        prefix = "explorer.aggregation",
        name = "poolstatus-enabled",
        havingValue = "true"
)
public class PoolRegistrationProcessor {

    private final PoolRegistrationRepository poolRegistrationRepository;

    private AtomicInteger registrations = new AtomicInteger(0);
    private AtomicInteger retirements = new AtomicInteger(0);

    @Transactional
    @EventListener
    public void processCertificateEvent(CertificateEvent certificateEvent) {
        certificateEvent.getTxCertificatesList().forEach(txCertificates -> {
            List<Certificate> certificates = txCertificates.getCertificates();
            certificates.forEach(certificate -> {
                if(certificate.getType() == CertificateType.POOL_REGISTRATION) {
                    registrations.incrementAndGet();
                } else if(certificate.getType() == CertificateType.POOL_RETIREMENT) {
                    retirements.incrementAndGet();
                }
            });
        });
    }

    @Transactional
    @EventListener
    public void processCommitEvent(CommitEvent commitEvent) {
        int commitRegistrations = registrations.getAndSet(0);
        int commitRetirements = retirements.getAndSet(0);
        PoolRegistrationEntity currentPoolStatus = poolRegistrationRepository.getLatestPoolStatus().orElse(PoolRegistrationEntity.builder()
                .registeredPools(0L)
                .retiredPools(0L)
                .build());
        PoolRegistrationEntity poolRegistrationEntity = PoolRegistrationEntity.builder()
                .registeredPools(commitRegistrations + currentPoolStatus.getRegisteredPools())
                .retiredPools(commitRetirements + currentPoolStatus.getRetiredPools())
                .slot(commitEvent.getMetadata().getSlot())
                .build();
        poolRegistrationRepository.save(poolRegistrationEntity);
    }

    @EventListener
    @Transactional
    public void processRollbackEvent(RollbackEvent rollbackEvent) {
        log.info("Rollback event received. Rolling back transactions to slot {}", rollbackEvent.getRollbackTo().getSlot());
        poolRegistrationRepository.deleteBySlotGreaterThan(rollbackEvent.getRollbackTo().getSlot());
    }


}
