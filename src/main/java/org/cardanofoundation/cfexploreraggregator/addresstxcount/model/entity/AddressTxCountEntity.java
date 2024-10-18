package org.cardanofoundation.cfexploreraggregator.addresstxcount.model.entity;

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
@Table(name = "address_tx_count")
@Slf4j
public class AddressTxCountEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "tx_count", nullable = false)
    private Long txCount;

    @Column(name = "slot", nullable = false)
    private Long slot;
}
