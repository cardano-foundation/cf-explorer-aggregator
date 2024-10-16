package org.cardanofoundation.cfexploreraggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan({
        "org.cardanofoundation.cfexploreraggregator.addresstxcount.model",
        "org.cardanofoundation.cfexploreraggregator.uniqueaccount.model",
        "org.cardanofoundation.cfexploreraggregator.poolstatus.model"
})
@EnableJpaRepositories({
        "org.cardanofoundation.cfexploreraggregator.addresstxcount.model",
        "org.cardanofoundation.cfexploreraggregator.uniqueaccount.model",
        "org.cardanofoundation.cfexploreraggregator.poolstatus.model"
})
public class CfExplorerAggregatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CfExplorerAggregatorApplication.class, args);
    }

}
