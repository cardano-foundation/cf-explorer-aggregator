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

import org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.domain.UniqueAccountRecord;
import org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.entity.UniqueAccountEntity;
import org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.mapper.UniqueAccountMapper;
import org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.repository.UniqueAccountRepository;
import org.cardanofoundation.cfexploreraggregator.uniqueaccount.service.UniqueAccountService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UniqueAccountServiceTest {

    @InjectMocks
    UniqueAccountService uniqueAccountService;
    @Mock
    UniqueAccountRepository uniqueAccountRepository;
    @Mock
    UniqueAccountMapper uniqueAccountMapper;

    @Test
    void getLatestUniqueAccount_shouldInteract() {
        uniqueAccountService.getLatestUniqueAccount();

        verify(uniqueAccountRepository).getLatestUniqueAccount();
        verify(uniqueAccountMapper).toUniqueAccountRecord(any());
        verifyNoMoreInteractions(uniqueAccountRepository);
        verifyNoMoreInteractions(uniqueAccountMapper);
    }

    @Test
    void getUniqueAccount_shouldNoInteraction() {
        when(uniqueAccountRepository.findByEpoch(1)).thenReturn(Optional.empty());

        uniqueAccountService.getUniqueAccount(1);

        verify(uniqueAccountRepository).findByEpoch(1);

        verifyNoMoreInteractions(uniqueAccountRepository);
        verifyNoInteractions(uniqueAccountMapper);
    }

    @Test
    void getUniqueAccount_shouldInteract() {
        when(uniqueAccountRepository.findByEpoch(1))
                .thenReturn(Optional.of(UniqueAccountEntity.builder().build()));
        when(uniqueAccountMapper.toUniqueAccountRecord(any()))
                .thenReturn(new UniqueAccountRecord(0,0));

        uniqueAccountService.getUniqueAccount(1);

        verify(uniqueAccountRepository).findByEpoch(1);
        verify(uniqueAccountMapper)
                .toUniqueAccountRecord(UniqueAccountEntity.builder().build());
        verifyNoMoreInteractions(uniqueAccountRepository);
        verifyNoMoreInteractions(uniqueAccountMapper);
    }

    @Test
    void getAllUniqueAccount_shouldReturnEmptyList() {
        when(uniqueAccountRepository.findAll((Pageable) any()))
                .thenReturn(Page.empty());

        List<UniqueAccountRecord> allUniqueAccount = uniqueAccountService.getAllUniqueAccount(null);

        assertTrue(allUniqueAccount.isEmpty());
        verify(uniqueAccountRepository).findAll((Pageable) any());
        verifyNoMoreInteractions(uniqueAccountRepository);
    }

}
