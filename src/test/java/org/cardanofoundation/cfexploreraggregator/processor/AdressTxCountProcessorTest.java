package org.cardanofoundation.cfexploreraggregator.processor;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import com.bloxbean.cardano.yaci.helper.model.Transaction;
import com.bloxbean.cardano.yaci.helper.model.Utxo;
import com.bloxbean.cardano.yaci.store.events.EventMetadata;
import com.bloxbean.cardano.yaci.store.events.TransactionEvent;
import com.bloxbean.cardano.yaci.store.events.internal.CommitEvent;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.cardanofoundation.cfexploreraggregator.addresstxcount.model.entity.AddressTxCountEntity;
import org.cardanofoundation.cfexploreraggregator.addresstxcount.model.repository.AddressTxCountRepository;
import org.cardanofoundation.cfexploreraggregator.addresstxcount.processor.AddressTxCountProcessor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdressTxCountProcessorTest {

    @Mock
    AddressTxCountProcessor addressTxCountProcessor;
    @Mock
    AddressTxCountRepository addressTxCountRepository;

    @Value("${aggregation.configuration.addressTxCount.Safe-Slot-Distance}")
    private long safeSlotDistance;

    @Test
    void handleTransactionEvent_ShouldInteract() {
        TransactionEvent event = getTransactionEvent("address1", 1L);
        addressTxCountProcessor.handleTransactionEvent(event);

        verify(addressTxCountProcessor).handleTransactionEvent(event);
        verifyNoMoreInteractions(addressTxCountProcessor);
    }

    @Test
    void handleCommitEvent_ShouldSaveNewEntityToDb() {
        AddressTxCountProcessor processor = new AddressTxCountProcessor(addressTxCountRepository);

        TransactionEvent txEvent = getTransactionEvent("address1", 1L);
        // Address should be in Local Map now
        processor.handleTransactionEvent(txEvent);

        CommitEvent commitEvent = CommitEvent.builder()
                .metadata(EventMetadata.builder().slot(2L).build())
                .build();

        when(addressTxCountRepository.findAllByAddressInOrderBySlotDesc(any()))
                .thenReturn(List.of());

        processor.handleCommitEvent(commitEvent);

        verify(addressTxCountRepository).findAllByAddressInOrderBySlotDesc(Set.of("address1"));
        verify(addressTxCountRepository).deleteAll(List.of());
        verify(addressTxCountRepository).saveAll(List.of(AddressTxCountEntity.builder()
                        .slot(1L)
                        .address("address1")
                        .txCount(1L)
                .build()));
        verifyNoMoreInteractions(addressTxCountRepository);
    }

    @Test
    void handleCommitEvent_ShouldIncrementEntity() {
        AddressTxCountProcessor processor = new AddressTxCountProcessor(addressTxCountRepository);

        TransactionEvent txEvent = getTransactionEvent("address1", 1L);
        // Address should be in Local Map now
        processor.handleTransactionEvent(txEvent);


        CommitEvent commitEvent = CommitEvent.builder()
                .metadata(EventMetadata.builder().slot(1L).build())
                .build();
        AddressTxCountEntity addressTxCountEntity = AddressTxCountEntity.builder()
                .address("address1")
                .id(1L)
                .slot(1L)
                .txCount(1L)
                .build();
        when(addressTxCountRepository.findAllByAddressInOrderBySlotDesc(any()))
                .thenReturn(List.of(addressTxCountEntity));

        processor.handleCommitEvent(commitEvent);

        verify(addressTxCountRepository).findAllByAddressInOrderBySlotDesc(Set.of("address1"));
        verify(addressTxCountRepository).deleteAll(List.of());
        verify(addressTxCountRepository).saveAll(List.of(AddressTxCountEntity.builder()
                .slot(txEvent.getMetadata().getSlot())
                .address(addressTxCountEntity.getAddress())
                .txCount(addressTxCountEntity.getTxCount() + 1)
                .build()));
        verifyNoMoreInteractions(addressTxCountRepository);
    }

    @Test
    void handleCommitEvent_ShouldDeleteEntity() {
        AddressTxCountProcessor processor = new AddressTxCountProcessor(addressTxCountRepository);
        TransactionEvent txEvent = getTransactionEvent("address1", safeSlotDistance + 2L);
        // Address should be in Local Map now
        processor.handleTransactionEvent(txEvent);

        CommitEvent commitEvent = CommitEvent.builder()
                .metadata(EventMetadata.builder().slot(safeSlotDistance + 2).build())
                .build();
        AddressTxCountEntity addressTxCountEntity = AddressTxCountEntity.builder()
                .address("address1")
                .id(1L)
                .slot(1L)
                .txCount(1L)
                .build();
        when(addressTxCountRepository.findAllByAddressInOrderBySlotDesc(any()))
                .thenReturn(List.of(addressTxCountEntity));

        processor.handleCommitEvent(commitEvent);

        verify(addressTxCountRepository).findAllByAddressInOrderBySlotDesc(Set.of("address1"));
        // Verifying that the entity is going to be deleted
        verify(addressTxCountRepository).deleteAll(List.of(addressTxCountEntity));
        verify(addressTxCountRepository).saveAll(List.of(AddressTxCountEntity.builder()
                .slot(txEvent.getMetadata().getSlot())
                .address(addressTxCountEntity.getAddress())
                .txCount(addressTxCountEntity.getTxCount() + 1)
                .build()));
        verifyNoMoreInteractions(addressTxCountRepository);
    }

    private static TransactionEvent getTransactionEvent(String address, Long slot) {
        Transaction tx = Transaction.builder()
                .utxos(List.of(Utxo.builder().address(address).build()))
                .build();
        return TransactionEvent.builder()
                .metadata(EventMetadata.builder().slot(slot).build())
                .transactions(List.of(tx))
                .build();
    }


}
