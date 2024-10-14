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
@Table(name = "active_pools")
@Slf4j
public class ActivePoolEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "active_pools", nullable = false)
    private Integer activePools;

    @Column(name = "epoch", nullable = false)
    private Integer epoch;
}
