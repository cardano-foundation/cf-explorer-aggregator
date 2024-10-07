package org.cardanofoundation.cfexploreraggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan({
        "org.cardanofoundation.cfexploreraggregator.txcount.model"
})
@EnableJpaRepositories({
        "org.cardanofoundation.cfexploreraggregator.txcount.model"
})
public class CfExplorerAggregatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CfExplorerAggregatorApplication.class, args);
    }

}
