package com.vnu.uet.repository;

import com.vnu.uet.domain.RelateDemand;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RelateDemand entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RelateDemandRepository extends JpaRepository<RelateDemand, Long> {
    java.util.List<com.vnu.uet.domain.RelateDemand> findAllBySwitchNodeId(Long s