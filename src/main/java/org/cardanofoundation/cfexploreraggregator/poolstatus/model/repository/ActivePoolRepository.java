package org.cardanofoundation.cfexploreraggregator.poolstatus.model.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import org.cardanofoundation.cfexploreraggregator.poolstatus.model.entity.ActivePoolEntity;

public interface ActivePoolRepository extends JpaRepository<ActivePoolEntity, Long> {

}
