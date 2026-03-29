package com.vnu.uet.repository;

import com.vnu.uet.domain.RelateNode;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RelateNode entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RelateNodeRepository extends JpaRepository<RelateNode, Long> {}
