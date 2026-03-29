package com.vnu.uet.service;

import com.vnu.uet.domain.Performer;
import com.vnu.uet.repository.PerformerRepository;
import com.vnu.uet.service.dto.PerformerDTO;
import com.vnu.uet.service.mapper.PerformerMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ExtendedPerformerService {

    private static final Logger LOG = LoggerFactory.getLogger(ExtendedPerformerService.class);

    private final PerformerRepository performerRepository;
    private final PerformerMapper performerMapper;

    public ExtendedPerformerService(PerformerRepository performerRepository, PerformerMapper performerMapper) {
        this.performerRepository = performerRepository;
        this.performerMapper = performerMapper;
    }

    /**
     * Get all performers associated with a particular Node ID.
     */
    @Transactional(readOnly = true)
    public List<PerformerDTO> getPerformersForNode(Long nodeId) {
        LOG.debug("Request to get all Performers for Node : {}", nodeId);
        List<Performer> performers = performerRepository.findAllByNodeId(nodeId);

        return performers.stream().map(performerMapper::toDto).collect(Collectors.toList());
        // FUTURE IMPLEMENTATION:
        // Here we could inject a RestTemplate or FeignClient to call GET /api/proxy/eaccount/users
        // using the userIds from `performers`. We would then map the email/profile info directly
        // into an enriched DTO returning both the eFlow configuration and eAccount personal details.
    }
}
