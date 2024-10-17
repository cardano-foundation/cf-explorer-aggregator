package org.cardanofoundation.cfexploreraggregator.addresstxcount.model.mapper;

import org.mapstruct.Mapper;

import org.cardanofoundation.cfexploreraggregator.BaseMapper;
import org.cardanofoundation.cfexploreraggregator.addresstxcount.model.domain.AddressTxCountRecord;
import org.cardanofoundation.cfexploreraggregator.addresstxcount.model.entity.AddressTxCountEntity;

@Mapper(config = BaseMapper.class)
public interface AddressTxCountMapper {

    AddressTxCountRecord mapToRecord(AddressTxCountEntity entity);

}
