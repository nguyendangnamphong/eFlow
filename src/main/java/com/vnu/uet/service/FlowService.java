package com.vnu.uet.service;

import com.vnu.uet.domain.Flow;
import com.vnu.uet.repository.FlowRepository;
import com.vnu.uet.service.dto.FlowDTO;
import com.vnu.uet.service.mapper.FlowMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.vnu.uet.domain.Flow}.
 */
@Service
@Transactional
public class FlowService {

    private static final Logger LOG = LoggerFactory.getLogger(FlowService.class);

    private final FlowRepository flowRepository;

    private final FlowMapper flowMapper;

    public FlowService(FlowRepository flowRepository, FlowMapper flowMapper) {
        this.flowRepository = flowRepository;
        this.flowMapper = flowMapper;
    }

    /**
     * Save a flow.
     *
     * @param flowDTO the entity to save.
     * @return the persisted entity.
     */
    public FlowDTO save(FlowDTO flowDTO) {
        LOG.debug("Request to save Flow : {}", flowDTO);
        Flow flow = flowMapper.toEntity(flowDTO);
        flow = flowRepository.save(flow);
        return flowMapper.toDto(flow);
    }

    /**
     * Update a flow.
     *
     * @param flowDTO the entity to save.
     * @return the persisted entity.
     */
    public FlowDTO update(FlowDTO flowDTO) {
        LOG.debug("Request to update Flow : {}", flowDTO);
        Flow flow = flowMapper.toEntity(flowDTO);
        flow = flowRepository.save(flow);
        return flowMapper.toDto(flow);
    }

    /**
     * Partially update a flow.
     *
     * @param flowDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FlowDTO> partialUpdate(FlowDTO flowDTO) {
        LOG.debug("Request to partially update Flow : {}", flowDTO);

        return flowRepository
            .findById(flowDTO.getId())
            .map(existingFlow -> {
                flowMapper.partialUpdate(existingFlow, flowDTO);

                return existingFlow;
            })
            .map(flowRepository::save)
            .map(flowMapper::toDto);
    }

    /**
     * Get all the flows.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<FlowDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Flows");
        return flowRepository.findAll(pageable).map(flowMapper::toDto);
    }

    /**
     * Get one flow by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FlowDTO> findOne(Long id) {
        LOG.debug("Request to get Flow : {}", id);
        return flowRepository.findById(id).map(flowMapper::toDto);
    }

    /**
     * Delete the flow by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Flow : {}", id);
        flowRepository.deleteById(id);
    }
}
