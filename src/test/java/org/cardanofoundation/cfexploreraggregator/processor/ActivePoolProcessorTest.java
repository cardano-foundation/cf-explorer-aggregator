package org.cardanofoundation.cfexploreraggregator.processor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.bloxbean.cardano.yaci.core.model.PoolParams;
import com.bloxbean.cardano.yaci.core.model.certs.PoolRegistration;
import com.bloxbean.cardano.yaci.store.events.BlockEvent;
import com.bloxbean.cardano.yaci.store.events.CertificateEvent;
import com.bloxbean.cardano.yaci.store.events.EpochChangeEvent;
import com.bloxbean.cardano.yaci.store.events.EventMetadata;
import com.bloxbean.cardano.yaci.store.events.domain.TxCertificates;
import com.bloxbean.cardano.yaci.store.events.internal.CommitEvent;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.cardanofoundation.cfexploreraggregator.poolstatus.model.entity.PoolAggregationEntity;
import org.cardanofoundation.cfexploreraggregator.poolstatus.model.entity.PoolStatusEntity;
import org.cardanofoundation.cfexploreraggregator.poolstatus.model.repository.PoolAggregationRepository;
import org.cardanofoundation.cfexploreraggregator.poolstatus.model.repository.PoolStatusRepository;
import org.cardanofoundation.cfexploreraggregator.poolstatus.processor.ActivePoolProcessor;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivePoolProcessorTest {

    @Mock
    ActivePoolProcessor activePoolProcessor;
    @Mock
    PoolAggregationRepository poolAggregationRepository;
    @Mock
    PoolStatusRepository poolStatusRepository;

    @Test
    void processCertificateEvent_shouldInteractEmptyList() {
        CertificateEvent certificateEvent = CertificateEvent.builder()
                .txCertificatesList(List.of())
                .build();
        activePoolProcessor.processCertificateEvent(certificateEvent);

        verify(activePoolProcessor).processCertificateEvent(certificateEvent);
        verifyNoMoreInteractions(activePoolProcessor);
    }

    @Test
    void processCommitEvent_RegisteringNewPools() {
        ActivePoolProcessor processor = new ActivePoolProcessor(poolAggregationRepository, poolStatusRepository);
        Set<String> poolOwner = Set.of("owner1", "owner2", "owner3");

        CertificateEvent certificateEvent = getCertificateEvent(poolOwner);
        processor.processCertificateEvent(certificateEvent);

        CommitEvent commitEvent = CommitEvent.builder()
                .metadata(EventMetadata.builder().slot(1L).build())
                .build();

        when(poolStatusRepository.findByPoolId(anyString())).thenReturn(Optional.empty());

        processor.processCommitEvent(commitEvent);
        poolOwner.forEach(owner -> {
            verify(poolStatusRepository).findByPoolId(owner);
            verify(poolStatusRepository).save(PoolStatusEntity.builder()
                    .poolId(owner)
                    .retired(false)
                    .updatedSlot(commitEvent.getMetadata().getSlot())
                    .build());
        });
        verifyNoMoreInteractions(poolStatusRepository);
    }

    @Test
    void processEpochEvent_ActivePools() {
        ActivePoolProcessor processor = new ActivePoolProcessor(poolAggregationRepository, poolStatusRepository);


        processBlockEvent(processor, 1, "leader1");
        processBlockEvent(processor, 1, "leader2");

        processor.processEpochEvent(EpochChangeEvent.builder()
                .epoch(1)
                .build());

        verify(poolAggregationRepository).save(PoolAggregationEntity.builder()
                        .activePools(2)
                        .registeredPools(0)
                        .retiredPools(0)
                        .epoch(1)
                .build()
        );
        verifyNoMoreInteractions(poolAggregationRepository);
        verify(poolStatusRepository).countByRetired(false);
        verify(poolStatusRepository).countByRetired(true);
        verifyNoMoreInteractions(poolStatusRepository);
    }

    private static void processBlockEvent(ActivePoolProcessor processor, int epoch, String slotleader) {
        processor.processBlockEvent(BlockEvent.builder()
            .metadata(EventMetadata.builder()
                    .epochNumber(epoch)
                    .slotLeader(slotleader)
                    .build())
            .build());
    }

    private static CertificateEvent getCertificateEvent(Set<String> poolOwner) {
        return CertificateEvent.builder()
                .txCertificatesList(List.of(
                        TxCertificates.builder()
                                .certificates(List.of(
                                        PoolRegistration.builder()
                                            .poolParams(PoolParams.builder()
                                                    .poolOwners(poolOwner)
                                                    .build())
                                            .build()
                                ))
                                .build()
                ))
                .build();
    }

}
