package org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.entity;

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
@Table(name = "unique_addresses_in_epoch")
@Slf4j
public class UniqueAddressesInEpochEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "address", nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(name = "epoch", nullable = false)
    private int epoch;

    @Column(name = "slot", nullable = false)
    private Long slot;

}
