package com.vnu.uet.repository;

import com.vnu.uet.domain.Performer;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Performer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PerformerRepository extends JpaRepository<Performer, Long> {}
