package com.vnu.uet.service;

import com.vnu.uet.domain.SwitchNode;
import com.vnu.uet.repository.SwitchNodeRepository;
import com.vnu.uet.service.dto.SwitchNodeDTO;
import com.vnu.uet.service.mapper.SwitchNodeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.vnu.uet.domain.SwitchNode}.
 */
@Service
@Transactional
public class SwitchNodeService {

    private static final Logger LOG = LoggerFactory.getLogger(SwitchNodeService.class);

    private final SwitchNodeRepository switchNodeRepository;

    private final SwitchNodeMapper switchNodeMapper;

    public SwitchNodeService(SwitchNodeRepository switchNodeRepository, SwitchNodeMapper switchNodeMapper) {
        this.switchNodeRepository = switchNodeRepository;
        this.switchNodeMapper = switchNodeMapper;
    }

    /**
     * Save a switchNode.
     *
     * @param switchNodeDTO the entity to save.
     * @return the persisted entity.
     */
    public SwitchNodeDTO save(SwitchNodeDTO switchNodeDTO) {
        LOG.debug("Request to save SwitchNode : {}", switchNodeDTO);
        SwitchNode switchNode = switchNodeMapper.toEntity(switchNodeDTO);
        switchNode = switchNodeRepository.save(switchNode);
        return switchNodeMapper.toDto(switchNode);
    }

    /**
     * Update a switchNode.
     *
     * @param switchNodeDTO the entity to save.
     * @return the persisted entity.
     */
    public SwitchNodeDTO update(SwitchNodeDTO switchNodeDTO) {
        LOG.debug("Request to update SwitchNode : {}", switchNodeDTO);
        SwitchNode switchNode = switchNodeMapper.toEntity(switchNodeDTO);
        switchNode = switchNodeRepository.save(switchNode);
        return switchNodeMapper.toDto(switchNode);
    }

    /**
     * Partially update a switchNode.
     *
     * @param switchNodeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SwitchNodeDTO> partialUpdate(SwitchNodeDTO switchNodeDTO) {
        LOG.debug("Request to partially update SwitchNode : {}", switchNodeDTO);

        return switchNodeRepository
            .findById(switchNodeDTO.getId())
            .map(existingSwitchNode -> {
                switchNodeMapper.partialUpdate(existingSwitchNode, switchNodeDTO);

                return existingSwitchNode;
            })
            .map(switchNodeRepository::save)
            .map(switchNodeMapper::toDto);
    }

    /**
     * Get all the switchNodes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SwitchNodeDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all SwitchNodes");
        return switchNodeRepository.findAll(pageable).map(switchNodeMapper::toDto);
    }

    /**
     * Get one switchNode by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SwitchNodeDTO> findOne(Long id) {
        LOG.debug("Request to get SwitchNode : {}", id);
        return switchNodeRepository.findById(id).map(switchNodeMapper::toDto);
    }

    /**
     * Delete the switchNode by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete SwitchNode : {}", id);
        switchNodeRepository.deleteById(id);
    }
}
