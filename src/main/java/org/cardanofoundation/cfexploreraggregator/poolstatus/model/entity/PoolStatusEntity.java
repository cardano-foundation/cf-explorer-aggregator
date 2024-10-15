package org.cardanofoundation.cfexploreraggregator.poolstatus.model.entity;

import jakarta.persistence.Entity;
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
@Table(name = "pool_status")
@Slf4j
public class PoolStatusEntity {

    @Id
    private String poolId;

    private boolean retired;
    private Long updatedSlot;
}
