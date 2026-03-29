package com.vnu.uet.repository;

import com.vnu.uet.domain.SwitchNode;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SwitchNode entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SwitchNodeRepository extends JpaRepository<SwitchNode, Long> {}
