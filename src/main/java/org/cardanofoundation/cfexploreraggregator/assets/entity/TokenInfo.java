package org.cardanofoundation.cfexploreraggregator.assets.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "token_info")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfo {

    @Id
    private String unit;

    private Long numberOfHolders;

    private Long volumeAtSlot;

    private Long totalVolume;

    private Long txCount;

    private Long slot;

}
