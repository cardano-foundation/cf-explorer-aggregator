package org.cardanofoundation.cfexploreraggregator.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.cardanofoundation.cfexploreraggregator.poolstatus.model.domain.PoolAggregationRecord;
import org.cardanofoundation.cfexploreraggregator.poolstatus.model.domain.PoolStatusRecord;
import org.cardanofoundation.cfexploreraggregator.poolstatus.model.entity.PoolAggregationEntity;
import org.cardanofoundation.cfexploreraggregator.poolstatus.model.entity.PoolStatusEntity;
import org.cardanofoundation.cfexploreraggregator.poolstatus.model.mapper.ActivePoolMapper;
import org.cardanofoundation.cfexploreraggregator.poolstatus.model.mapper.PoolStatusMapper;
import org.cardanofoundation.cfexploreraggregator.poolstatus.model.repository.PoolAggregationRepository;
import org.cardanofoundation.cfexploreraggregator.poolstatus.model.repository.PoolStatusRepository;
import org.cardanofoundation.cfexploreraggregator.poolstatus.service.PoolStatusService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PoolStatusServiceTest {

    @InjectMocks
    PoolStatusService poolStatusService;
    @Mock
    PoolStatusRepository poolStatusRepository;
    @Mock
    PoolAggregationRepository poolAggregationRepository;
    @Mock
    ActivePoolMapper activePoolMapper;
    @Mock
    PoolStatusMapper poolStatusMapper;

    @Test
    void getPoolStatus_shouldReturnEmpty() {
        Optional<PoolStatusRecord> poolStatus = poolStatusService.getPoolStatus("");

        assertEquals(Optional.empty(), poolStatus);
    }

    @Test
    void getPoolStatus_shouldReturnRecord() {

        PoolStatusEntity entity = PoolStatusEntity.builder()
                .poolId("poolId")
                .retired(true)
                .updatedSlot(1L)
                .build();

        when(poolStatusRepository.findByPoolId(anyString())).thenReturn(Optional.of(entity));
        when(poolStatusMapper.getPoolStatusRecord(entity)).thenReturn(new PoolStatusRecord(entity.getPoolId(), entity.isRetired(), entity.getUpdatedSlot()));

        Optional<PoolStatusRecord> poolStatus = poolStatusService.getPoolStatus(entity.getPoolId());

        assertTrue(poolStatus.isPresent());
        assertEquals(new PoolStatusRecord(entity.getPoolId(), entity.isRetired(), entity.getUpdatedSlot()), poolStatus.get());

        verify(poolStatusRepository).findByPoolId(entity.getPoolId());
        verifyNoMoreInteractions(poolStatusRepository);
        verify(poolStatusMapper).getPoolStatusRecord(entity);
        verifyNoMoreInteractions(poolStatusMapper);
        verifyNoInteractions(activePoolMapper);
    }

    @Test
    void getLatestPoolAggregation_shouldReturnRecord() {
        PoolAggregationEntity entity = PoolAggregationEntity.builder()
                .activePools(1)
                .registeredPools(1)
                .retiredPools(1)
                .epoch(1)
                .build();

        when(poolAggregationRepository.findTopByOrderByEpochDesc()).thenReturn(entity);
        when(activePoolMapper.getActivePoolAggregation(entity)).thenReturn(
                new PoolAggregationRecord(entity.getActivePools(),
                entity.getRegisteredPools(), entity.getRetiredPools(),
                        entity.getEpoch()));

        PoolAggregationRecord poolAggregation = poolStatusService.getLatestPoolAggregation();

        assertEquals(new PoolAggregationRecord(entity.getActivePools(),
                entity.getRegisteredPools(), entity.getRetiredPools(),
                        entity.getEpoch()), poolAggregation);
        verify(poolAggregationRepository).findTopByOrderByEpochDesc();
        verify(activePoolMapper).getActivePoolAggregation(entity);
        verifyNoMoreInteractions(poolAggregationRepository);
        verifyNoMoreInteractions(activePoolMapper);
        verifyNoInteractions(poolStatusMapper);
        verifyNoInteractions(poolStatusRepository);
    }

    @Test
    void getAllPoolAggregations_shouldReturnEmptyList() {
        when(poolAggregationRepository.findAll((Pageable) any())).thenReturn(Page.empty());

        List<PoolAggregationRecord> allPoolAggregations = poolStatusService.getAllPoolAggregations(null);

        assertTrue(allPoolAggregations.isEmpty());
    }

}
