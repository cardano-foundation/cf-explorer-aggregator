package org.cardanofoundation.cfexploreraggregator.network.monitoring.processor;

import com.bloxbean.cardano.yaci.store.events.TransactionEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardanofoundation.cfexploreraggregator.network.monitoring.model.TransactionAdoptionMetadata;
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
        name = "transaction-delay-6h.enabled",
        havingValue = "true"
)
public class TransactionDelay6hProcessor {

    private final ObjectMapper objectMapper;

    private static final List<String> CF_WALLETS = List.of("addr1vxwkdhv8jqtzvqzuype5v7kaf6wrue4vyxlnlrxqmtqvr5sy2hjy4");

    @PostConstruct
    public void init() {
        log.info("INIT - Starting");
    }

    @EventListener
    public void handleTransactionEvent(TransactionEvent transactionEvent) {
        TypeReference<HashMap<String, TransactionAdoptionMetadata>> typeRef = new TypeReference<HashMap<String, TransactionAdoptionMetadata>>() {
        };

        try {
            transactionEvent.getTransactions()
                    .forEach(transaction -> {
                                transaction.getBody()
                                        .getOutputs()
                                        .forEach(transactionOutput -> {
                                                    if (isRelevantAddress(transactionOutput.getAddress())) {
                                                        log.info("found utxo: {}", transactionOutput);
                                                        var json = transaction.getAuxData().getMetadataJson();
                                                        log.info("json: {}", json);
                                                        try {
                                                            var transactionAdoptionMetadata = objectMapper
                                                                    .readValue(json, typeRef);
                                                            log.info("transactionAdoptionMetadata: {}", transactionAdoptionMetadata);
                                                        } catch (JsonProcessingException e) {
                                                            log.warn("error", e);
                                                        }
                                                    }
                                                }

                                        );
                            }
                    );
        } catch (Exception e) {
            //
        }

    }


    private boolean isRelevantAddress(String address) {
        return address != null && CF_WALLETS.contains(address);
    }

}
