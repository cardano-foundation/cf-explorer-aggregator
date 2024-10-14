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
@Table(name = "pool_registration_agg")
@Slf4j
public class PoolRegistrationEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "registered_pools", nullable = false)
    private Long registeredPools;

    @Column(name = "retired_pools", nullable = false)
    private Long retiredPools;

    @Column(name = "slot", nullable = false)
    private Long slot;
}
