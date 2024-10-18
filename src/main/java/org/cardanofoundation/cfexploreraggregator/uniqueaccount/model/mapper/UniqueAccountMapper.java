package org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.mapper;

import org.mapstruct.Mapper;

import org.cardanofoundation.cfexploreraggregator.BaseMapper;
import org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.domain.UniqueAccountRecord;
import org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.entity.UniqueAccountEntity;

@Mapper(config = BaseMapper.class)
public interface UniqueAccountMapper {

    UniqueAccountRecord toUniqueAccountRecord(UniqueAccountEntity uniqueAccountEntity);
}
