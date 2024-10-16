package org.cardanofoundation.cfexploreraggregator.tokentxcount.model.entity;

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
@Table(name = "token_tx_count")
@Slf4j
public class TokenTxCountEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "unit", nullable = false)
    private String unit;

    @Column(name = "tx_count", nullable = false)
    private Long txCount;

    @Column(name = "slot", nullable = false)
    private Long slot;
}
