package org.cardanofoundation.cfexploreraggregator.poolstatus.model.mapper;

import org.mapstruct.Mapper;

import org.cardanofoundation.cfexploreraggregator.BaseMapper;
import org.cardanofoundation.cfexploreraggregator.poolstatus.model.domain.PoolStatusRecord;
import org.cardanofoundation.cfexploreraggregator.poolstatus.model.entity.PoolStatusEntity;

@Mapper(config = BaseMapper.class)
public interface PoolStatusMapper {

    PoolStatusRecord getPoolStatusRecord(PoolStatusEntity entity);
}
