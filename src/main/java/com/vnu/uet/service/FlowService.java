package com.vnu.uet.service;

import com.vnu.uet.domain.*;
import com.vnu.uet.domain.Flow;
import com.vnu.uet.repository.*;
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

    // Injected repositories for cascade delete
    private final NodeRepository nodeRepository;
    private final RelateNodeRepository relateNodeRepository;
    private final SwitchNodeRepository switchNodeRepository;
    private final RelateDemandRepository relateDemandRepository;
    private final PerformerRepository performerRepository;
    private final MapFormRepository mapFormRepository;
    private final VariableRepository variableRepository;

    public FlowService(
        FlowRepository flowRepository,
        FlowMapper flowMapper,
        NodeRepository nodeRepository,
        RelateNodeRepository relateNodeRepository,
        SwitchNodeRepository switchNodeRepository,
        RelateDemandRepository relateDemandRepository,
        PerformerRepository performerRepository,
        MapFormRepository mapFormRepository,
        VariableRepository variableRepository
    ) {
        this.flowRepository = flowRepository;
        this.flowMapper = flowMapper;
        this.nodeRepository = nodeRepository;
        this.relateNodeRepository = relateNodeRepository;
        this.switchNodeRepository = switchNodeRepository;
        this.relateDemandRepository = relateDemandRepository;
        this.performerRepository = performerRepository;
        this.mapFormRepository = mapFormRepository;
        this.variableRepository = variableRepository;
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
        LOG.debug("Request to delete Flow (Cascade) : {}", id);

        Optional<Flow> flowOpt = flowRepository.findById(id);
        if (flowOpt.isPresent()) {
            Flow flow = flowOpt.orElseThrow();

            // Delete SwitchNode related stuff
            for (SwitchNode switchNode : flow.getSwitchNodes()) {
                relateDemandRepository.deleteAll(switchNode.getRelateDemands());
                switchNodeRepository.delete(switchNode);
            }

            // Delete RelateNode related stuff
            for (RelateNode relateNode : flow.getRelateNodes()) {
                relateDemandRepository.deleteAll(relateNode.getRelateDemands());
                relateNodeRepository.delete(relateNode);
            }

            // Delete Node related stuff
            for (Node node : flow.getNodes()) {
                performerRepository.deleteAll(node.getPerformers());
                for (MapForm mf : node.getMapForms()) {
                    variableRepository.deleteAll(mf.getVariables());
                    mapFormRepository.delete(mf);
                }
                nodeRepository.delete(node);
            }

            flowRepository.delete(flow);
        }
    }
}
