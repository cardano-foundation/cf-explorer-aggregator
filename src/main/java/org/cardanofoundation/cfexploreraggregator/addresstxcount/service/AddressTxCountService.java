package org.cardanofoundation.cfexploreraggregator.addresstxcount.service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import org.cardanofoundation.cfexploreraggregator.addresstxcount.model.domain.AddressTxCountRecord;
import org.cardanofoundation.cfexploreraggregator.addresstxcount.model.mapper.AddressTxCountMapper;
import org.cardanofoundation.cfexploreraggregator.addresstxcount.model.repository.AddressTxCountRepository;

@Component
@RequiredArgsConstructor
public class AddressTxCountService {

    private final AddressTxCountRepository addressTxCountRepository;
    private final AddressTxCountMapper addressTxCountMapper;

    public Optional<AddressTxCountRecord> getTxCountForAddress(String address) {
        return addressTxCountRepository.findTopByAddressOrderBySlotDesc(address).map(addressTxCountMapper::mapToRecord);
    }

    public List<AddressTxCountRecord> getAllTxCount(Pageable pageable) {
        return addressTxCountRepository.findAll(pageable).getContent().stream().map(addressTxCountMapper::mapToRecord).toList();
    }
}
