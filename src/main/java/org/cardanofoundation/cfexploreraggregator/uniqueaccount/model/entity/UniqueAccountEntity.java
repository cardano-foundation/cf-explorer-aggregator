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
@Table(name = "unique_account_agg")
@Slf4j
public class UniqueAccountEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "unique_accounts", nullable = false)
    private int uniqueAccounts;

    @Column(name = "epoch", nullable = false)
    private int epoch;
}
