package org.cardanofoundation.cfexploreraggregator.poolstatus.service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import org.cardanofoundation.cfexploreraggregator.poolstatus.model.domain.PoolAggregationRecord;
import org.cardanofoundation.cfexploreraggregator.poolstatus.model.domain.PoolStatusRecord;
import org.cardanofoundation.cfexploreraggregator.poolstatus.model.entity.PoolAggregationEntity;
import org.cardanofoundation.cfexploreraggregator.poolstatus.model.entity.PoolStatusEntity;
import org.cardanofoundation.cfexploreraggregator.poolstatus.model.mapper.ActivePoolMapper;
import org.cardanofoundation.cfexploreraggregator.poolstatus.model.mapper.PoolStatusMapper;
import org.cardanofoundation.cfexploreraggregator.poolstatus.model.repository.PoolAggregationRepository;
import org.cardanofoundation.cfexploreraggregator.poolstatus.model.repository.PoolStatusRepository;

@Component
@RequiredArgsConstructor
public class PoolStatusService {

    private final PoolStatusRepository poolStatusRepository;
    private final PoolAggregationRepository poolAggregationRepository;
    private final PoolStatusMapper poolStatusMapper;
    private final ActivePoolMapper activePoolMapper;

    public Optional<PoolStatusRecord> getPoolStatus(String poolId) {
        Optional<PoolStatusEntity> byPoolId = poolStatusRepository.findByPoolId(poolId);
        return byPoolId.map(poolStatusMapper::getPoolStatusRecord);
    }

    public Optional<PoolAggregationRecord> getPoolAggregationByEpoch(int epoch) {
        Optional<PoolAggregationEntity> poolAggregationByEpoch = poolAggregationRepository.findByEpoch(epoch);
        return poolAggregationByEpoch.map(activePoolMapper::getActivePoolAggregation);
    }

    public PoolAggregationRecord getLatestPoolAggregation() {
        PoolAggregationEntity latestPoolAggregation = poolAggregationRepository.findTopByOrderByEpochDesc();
        return activePoolMapper.getActivePoolAggregation(latestPoolAggregation);
    }

    public List<PoolAggregationRecord> getAllPoolAggregations(Pageable pageable) {
        return poolAggregationRepository.findAll(pageable).stream()
                .map(activePoolMapper::getActivePoolAggregation)
                .toList();
    }
}
