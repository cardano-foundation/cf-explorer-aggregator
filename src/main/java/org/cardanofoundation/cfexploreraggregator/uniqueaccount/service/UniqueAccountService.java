package org.cardanofoundation.cfexploreraggregator.uniqueaccount.service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.domain.UniqueAccountRecord;
import org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.entity.UniqueAccountEntity;
import org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.mapper.UniqueAccountMapper;
import org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.repository.UniqueAccountRepository;

@Component
@RequiredArgsConstructor
public class UniqueAccountService {

    private final UniqueAccountRepository uniqueAccountRepository;
    private final UniqueAccountMapper uniqueAccountMapper;

    public UniqueAccountRecord getLatestUniqueAccount() {
        UniqueAccountEntity uniqueAccountEntity = uniqueAccountRepository.getLatestUniqueAccount();
        return uniqueAccountMapper.toUniqueAccountRecord(uniqueAccountEntity);

    }

    public Optional<UniqueAccountRecord> getUniqueAccount(Integer epoch) {
        return uniqueAccountRepository.findByEpoch(epoch)
                .map(uniqueAccountMapper::toUniqueAccountRecord);
    }

    public List<UniqueAccountRecord> getAllUniqueAccount(Pageable pageable) {
        return uniqueAccountRepository.findAll(pageable).map(uniqueAccountMapper::toUniqueAccountRecord).toList();
    }
}
