package org.cardanofoundation.cfexploreraggregator.poolstatus.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "active_pools_agg")
@Slf4j
public class ActivePoolEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "active_pools_count", nullable = false)
    private Integer activePoolCount;

    @Column(name = "registered_pools", nullable = false)
    private Integer registeredPools;

    @Column(name = "retired_pools", nullable = false)
    private Integer retiredPools;

    @Column(name = "epoch", nullable = false)
    private Integer epoch;
}
