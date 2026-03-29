package com.vnu.uet.repository;

import com.vnu.uet.domain.Variable;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Variable entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VariableRepository extends JpaRepository<Variable, Long> {
    List<Variable> findAllByMapFormId(Long mapFormId);
}
