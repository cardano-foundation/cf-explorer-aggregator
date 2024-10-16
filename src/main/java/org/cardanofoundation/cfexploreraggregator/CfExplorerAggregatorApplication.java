package org.cardanofoundation.cfexploreraggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan({
        "org.cardanofoundation.cfexploreraggregator.txcount.model",
        "org.cardanofoundation.cfexploreraggregator.assets.entity",
        "org.cardanofoundation.cfexploreraggregator.network.monitoring.entity"
})
@EnableJpaRepositories({
        "org.cardanofoundation.cfexploreraggregator.txcount.model",
        "org.cardanofoundation.cfexploreraggregator.assets.repository",
        "org.cardanofoundation.cfexploreraggregator.network.monitoring.repository"
})
public class CfExplorerAggregatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CfExplorerAggregatorApplication.class, args);
    }

}
