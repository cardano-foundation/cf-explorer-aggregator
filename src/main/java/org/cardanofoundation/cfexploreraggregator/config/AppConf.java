package org.cardanofoundation.cfexploreraggregator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConf {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
