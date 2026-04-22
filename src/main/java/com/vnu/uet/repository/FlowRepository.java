package com.vnu.uet.repository;

import com.vnu.uet.domain.Flow;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Flow entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FlowRepository extends JpaRepository<Flow, Long> {
    @Query(
        """
            select distinct f.flowGroup
            from Flow f
            where f.describe = 'launch'
              and f.flowGroup is not null
              and trim(f.flowGroup) <> ''
            order by f.flowGroup
        """
    )
    List<String> findDistinctLaunchedFlowGroups();

    interface FlowIdNameProjection {
        Long getId();

        String getFlowName();
    }

    List<FlowIdNameProjection> findAllByFlowGroupOrderByIdAsc(String flowGroup);
}
