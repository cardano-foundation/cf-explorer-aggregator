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

import org.cardanofoundation.cfexploreraggregator.addresstxcount.model.domain.AddressTxCountRecord;
import org.cardanofoundation.cfexploreraggregator.addresstxcount.model.entity.AddressTxCountEntity;
import org.cardanofoundation.cfexploreraggregator.addresstxcount.model.mapper.AddressTxCountMapper;
import org.cardanofoundation.cfexploreraggregator.addresstxcount.model.repository.AddressTxCountRepository;
import org.cardanofoundation.cfexploreraggregator.addresstxcount.service.AddressTxCountService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddressTxCountServiceTest {

    @Mock
    AddressTxCountRepository addressTxCountRepository;
    @InjectMocks
    AddressTxCountService addressTxCountService;
    @Mock
    AddressTxCountMapper addressTxCountMapper;

    @Test
    void getTxCountForAddress_shouldReturnEmpty() {
        Optional<AddressTxCountRecord> txCountForAddress = addressTxCountService.getTxCountForAddress("");

        assertEquals(Optional.empty(), txCountForAddress);
    }

    @Test
    void getTxCountForAddress_shouldReturnRecord() {

        AddressTxCountEntity entity = AddressTxCountEntity.builder()
                .address("address")
                .txCount(1L)
                .slot(10L)
                .build();

        when(addressTxCountRepository.findTopByAddressOrderBySlotDesc(anyString())).thenReturn(Optional.of(entity));
        when(addressTxCountMapper.mapToRecord(any()))
                .thenReturn(new AddressTxCountRecord(entity.getAddress(), entity.getTxCount(), entity.getSlot()));

        Optional<AddressTxCountRecord> txCountForAddress = addressTxCountService.getTxCountForAddress(entity.getAddress());

        assertTrue(txCountForAddress.isPresent());
        assertEquals(new AddressTxCountRecord("address", 1L, 10L), txCountForAddress.get());
    }

    @Test
    void getAllTxCount_shouldReturnEmptyList() {
        when(addressTxCountRepository.findAll((Pageable) any()))
                .thenReturn(Page.empty());

        List<AddressTxCountRecord> allTxCount = addressTxCountService.getAllTxCount(null);

        assertTrue(allTxCount.isEmpty());
    }

}
