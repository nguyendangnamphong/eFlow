package com.vnu.uet.repository;

import com.vnu.uet.domain.RelateDemand;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RelateDemand entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RelateDemandRepository extends JpaRepository<RelateDemand, Long> {
    List<RelateDemand> findAllBySwitchNodeId(Long switchId);

    List<RelateDemand> findAllByRelateNodeId(Long relateNodeId);
}
