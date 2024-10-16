package org.cardanofoundation.cfexploreraggregator.assets.processor;

import com.bloxbean.cardano.yaci.store.common.domain.Amt;
import com.bloxbean.cardano.yaci.store.events.TransactionEvent;
import com.bloxbean.cardano.yaci.store.events.internal.CommitEvent;
import com.bloxbean.cardano.yaci.store.utxo.storage.impl.model.AddressUtxoEntity;
import com.bloxbean.cardano.yaci.store.utxo.storage.impl.model.UtxoId;
import com.bloxbean.cardano.yaci.store.utxo.storage.impl.repository.UtxoRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardanofoundation.cfexploreraggregator.assets.entity.TokenInfo;
import org.cardanofoundation.cfexploreraggregator.assets.repository.TokenInfoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        prefix = "explorer.aggregation",
        name = "token-info.enabled",
        havingValue = "true"
)
public class TokenInfoProcessor {

    @Value("${explorer.aggregation.token-info.include-zero-balance}")
    private boolean includeTokenZeroBalanceTx;

    private final Set<TransactionEvent> cache = new HashSet<>();

    private final UtxoRepository utxoRepository;

    private final TokenInfoRepository tokenInfoRepository;

    @PostConstruct
    public void init() {
        log.info("INIT - Starting");
        log.info("INIT - includeTokenZeroBalanceTx is: {}", includeTokenZeroBalanceTx);
    }

    @EventListener
    public void handleTransactionEvent(TransactionEvent event) {
        cache.add(event);
    }

    @EventListener
    public void handleCommitEvent(CommitEvent commitEvent) {

        // TODO 24 hours is easy if indexed by slot and sum volume at slot where slot > 24 hours before

        cache
                .stream()
                .sorted(Comparator.comparingLong(transactionEvent -> transactionEvent.getMetadata().getBlock()))
                .forEach(event -> {
                    try {
                        event.getTransactions()
                                .forEach(transaction -> {

                                    var slot = event.getMetadata().getSlot();

                                    var inputUtxos = transaction.getBody()
                                            .getInputs()
                                            .stream()
                                            .map(transactionInput -> utxoRepository
                                                    .findById(UtxoId.builder()
                                                            .txHash(transactionInput.getTransactionId())
                                                            .outputIndex(transactionInput.getIndex())
                                                            .build()))
                                            .flatMap(Optional::stream)
                                            .toList();

                                    if (includeTokenZeroBalanceTx) {
                                        var result = inputUtxos.stream()
                                                .reduce((Map<String, Long>) new HashMap<String, Long>(), (accumulator, current) -> {
                                                    var amounts = current.getAmounts()
                                                            .stream()
                                                            .collect(Collectors.toMap(Amt::getUnit, amount -> amount.getQuantity().longValue()));
                                                    amounts.forEach((k, v) -> accumulator.merge(k, v, Long::sum));
                                                    return amounts;
                                                }, (a, b) -> {
                                                    b.forEach((k, v) -> a.merge(k, v, Long::sum));
                                                    return a;
                                                });

                                        result.forEach((unit, amount) -> {
                                            var tokenInfoOpt = tokenInfoRepository.findById(unit);

                                            var tokenInfo = tokenInfoOpt.map(existingTokenInfo ->
                                                            existingTokenInfo.toBuilder()
                                                                    .txCount(existingTokenInfo.getTxCount() + 1)
                                                                    .slot(slot)
                                                                    .volumeAtSlot(amount)
                                                                    .totalVolume(existingTokenInfo.getTotalVolume() + amount)
                                                                    .build())
                                                    .orElse(TokenInfo.builder()
                                                            .unit(unit)
                                                            .txCount(1L)
                                                            .slot(slot)
                                                            .volumeAtSlot(amount)
                                                            .totalVolume(amount)
                                                            .build());

                                            tokenInfoRepository.save(tokenInfo);

                                        });


                                    } else {


                                        inputUtxos.stream()
                                                .collect(Collectors.groupingBy(AddressUtxoEntity::getOwnerAddr, Collectors.mapping(AddressUtxoEntity::getAmounts, Collectors.toList())))
                                                .entrySet()
                                                .stream()
                                                .map(entry -> {
                                                    var key = entry.getKey();
                                                    entry.getValue().stream().map(AddressUtxoEntity::getAmounts).re
                                                })


                                    }


                                });
                    } catch (Exception e) {
                        log.warn("error", e);
                    }
                });


    }

}
