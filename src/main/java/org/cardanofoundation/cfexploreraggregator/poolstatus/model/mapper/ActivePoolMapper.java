package org.cardanofoundation.cfexploreraggregator.poolstatus.model.mapper;

import org.mapstruct.Mapper;

import org.cardanofoundation.cfexploreraggregator.BaseMapper;
import org.cardanofoundation.cfexploreraggregator.poolstatus.model.domain.PoolAggregationRecord;
import org.cardanofoundation.cfexploreraggregator.poolstatus.model.entity.PoolAggregationEntity;

@Mapper(config = BaseMapper.class)
public interface ActivePoolMapper {

    PoolAggregationRecord getActivePoolAggregation(PoolAggregationEntity entity);

}
