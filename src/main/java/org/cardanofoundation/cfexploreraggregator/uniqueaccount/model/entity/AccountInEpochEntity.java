package org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.entity;

import jakarta.persistence.Column;
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
@Table(name = "tmp_account_in_epoch")
@Slf4j
public class AccountInEpochEntity {

    @Id
    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "epoch", nullable = false)
    private int epoch;

    @Column(name = "slot", nullable = false)
    private Long slot;

}
