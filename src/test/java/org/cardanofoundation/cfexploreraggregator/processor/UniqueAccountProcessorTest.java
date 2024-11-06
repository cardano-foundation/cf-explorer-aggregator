package org.cardanofoundation.cfexploreraggregator.processor;

import java.util.List;
import java.util.Set;

import com.bloxbean.cardano.yaci.helper.model.Transaction;
import com.bloxbean.cardano.yaci.helper.model.Utxo;
import com.bloxbean.cardano.yaci.store.events.EventMetadata;
import com.bloxbean.cardano.yaci.store.events.TransactionEvent;
import com.bloxbean.cardano.yaci.store.events.internal.CommitEvent;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.repository.UniqueAccountRepository;
import org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.repository.UniqueAddressesInEpochRepository;
import org.cardanofoundation.cfexploreraggregator.uniqueaccount.processor.UniqueAccountProcessor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UniqueAccountProcessorTest {

    @Mock
    UniqueAccountProcessor uniqueAccountProcessor;
    @Mock
    UniqueAddressesInEpochRepository uniqueAddressesInEpochRepository;
    @Mock
    UniqueAccountRepository uniqueAccountRepository;

    @Test
    void handleTransactionEvent_ShouldNoInteract() {
        TransactionEvent event = new TransactionEvent();
        uniqueAccountProcessor.handleTransactionEvent(event);

        verify(uniqueAccountProcessor).handleTransactionEvent(event);
        verifyNoMoreInteractions(uniqueAccountProcessor);
        verifyNoInteractions(uniqueAddressesInEpochRepository);
        verifyNoInteractions(uniqueAccountRepository);
    }

    @Test
    void handleCommitEvent_AddingNewUniqueAccounts() {
        UniqueAccountProcessor processor = new UniqueAccountProcessor(uniqueAccountRepository, uniqueAddressesInEpochRepository);
        Set<String> address = Set.of("address1", "address2", "address3");
        // adding all addresses to TxEvent
        TransactionEvent txEvent = TransactionEvent.builder()
                .transactions(address.stream().map(this::getTransactionWithUtxoAddress).toList())
                .build();
        processor.handleTransactionEvent(txEvent);

        CommitEvent commitEvent = CommitEvent.builder()
                .metadata(EventMetadata.builder()
                            .epochNumber(1)
                            .slot(2L)
                        .build())
                .build();

        // No addresses found in DB
        when(uniqueAddressesInEpochRepository.findAllByEpochAndAddressIn(commitEvent.getMetadata().getEpochNumber(), address))
                .thenReturn(List.of());

        processor.handleCommitEvent(commitEvent);

        verify(uniqueAddressesInEpochRepository).findAllByEpochAndAddressIn(commitEvent.getMetadata().getEpochNumber(), address);
        verify(uniqueAddressesInEpochRepository).saveAll(any());
        verifyNoMoreInteractions(uniqueAddressesInEpochRepository);

    }

    Transaction getTransactionWithUtxoAddress(String address) {
        return Transaction.builder()
                .utxos(List.of(
                Utxo.builder()
                        .address(address)
                        .build()))
                .build();
    }
}
