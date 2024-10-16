package org.cardanofoundation.cfexploreraggregator.network.monitoring.processor;

import com.bloxbean.cardano.yaci.store.events.TransactionEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardanofoundation.cfexploreraggregator.network.monitoring.entity.TransactionAdoption;
import org.cardanofoundation.cfexploreraggregator.network.monitoring.model.TransactionAdoptionMetadata;
import org.cardanofoundation.cfexploreraggregator.network.monitoring.repository.TransactionAdoptionRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        prefix = "explorer.aggregation",
        name = "transaction-adoption.enabled",
        havingValue = "true"
)
public class TransactionAdoptionProcessor {

    private final ObjectMapper objectMapper;

    private final TransactionAdoptionRepository transactionAdoptionRepository;

    private static final List<String> CF_WALLETS = List.of(
            "addr1vy0zwnn5yj4h3s25xuere4h38np4z6gcng2mdgxg0mapxagl6x66d",
            "addr1vxpvhtj5vvcqmf9td3vlvv4vza9nnuqrmkc42cnd42dg7fsz0v99d",
            "addr1vx7gvyvy2r7mycya22f3x88wlgra2552uxm8xz2g0v3g6yccgyydv",
            "addr1vx2uvrm53dak4x3u0txy98r2jpg2nhy0n82vk8a6v9wmk4s8up888"
    );

    @PostConstruct
    public void init() {
        log.info("INIT - Starting");
    }

    @EventListener
    @Transactional
    public void handleTransactionEvent(TransactionEvent transactionEvent) {
        TypeReference<HashMap<String, TransactionAdoptionMetadata>> typeRef = new TypeReference<HashMap<String, TransactionAdoptionMetadata>>() {
        };

        try {
            transactionEvent.getTransactions()
                    .forEach(transaction -> {
                                var outputs = transaction.getBody()
                                        .getOutputs();

                                for (int i = 0; i < outputs.size(); i++) {
                                    var transactionOutput = outputs.get(i);
                                    if (isRelevantAddress(transactionOutput.getAddress())) {
                                        log.info("found utxo: {}", transactionOutput);
                                        var json = transaction.getAuxData().getMetadataJson();
                                        log.info("json: {}", json);
                                        try {
                                            var transactionAdoptionMetadataMap = objectMapper
                                                    .readValue(json, typeRef);

                                            var transactionAdoptionMetadata = transactionAdoptionMetadataMap.get("1");

                                            log.info("transactionAdoptionMetadata: {}", transactionAdoptionMetadataMap);

                                            var slot = transactionEvent.getMetadata().getSlot();
                                            var absoluteSlot = Long.parseLong(transactionAdoptionMetadata.absoluteSlot());
                                            var diff = transactionEvent.getMetadata().getSlot() - absoluteSlot;

                                            var transactionAdoption = TransactionAdoption.builder()
                                                    .txHash(transaction.getTxHash())
                                                    .outputIndex(i)
                                                    .absoluteSlot(absoluteSlot)
                                                    .slot(slot)
                                                    .slotDifference(diff)
                                                    .build();

                                            transactionAdoptionRepository.save(transactionAdoption);

                                        } catch (JsonProcessingException e) {
                                            log.warn("error", e);
                                        }
                                    }
                                }

                            }
                    );
        } catch (Exception e) {
            log.warn("error", e);
        }

    }


    private boolean isRelevantAddress(String address) {
        return address != null && CF_WALLETS.contains(address);
    }

}
