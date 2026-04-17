package com.vnu.uet.repository;

import com.vnu.uet.domain.RelateNode;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the RelateNode entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RelateNodeRepository extends JpaRepository<RelateNode, Long> {
    List<RelateNode> findAllByFlowIdAndNodeId(Long flowId, Long nodeId);
}
