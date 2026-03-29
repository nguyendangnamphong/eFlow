package com.vnu.uet.service;

import com.vnu.uet.domain.RelateNode;
import com.vnu.uet.repository.RelateNodeRepository;
import com.vnu.uet.service.dto.RelateNodeDTO;
import com.vnu.uet.service.mapper.RelateNodeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.vnu.uet.domain.RelateNode}.
 */
@Service
@Transactional
public class RelateNodeService {

    private static final Logger LOG = LoggerFactory.getLogger(RelateNodeService.class);

    private final RelateNodeRepository relateNodeRepository;

    private final RelateNodeMapper relateNodeMapper;

    public RelateNodeService(RelateNodeRepository relateNodeRepository, RelateNodeMapper relateNodeMapper) {
        this.relateNodeRepository = relateNodeRepository;
        this.relateNodeMapper = relateNodeMapper;
    }

    /**
     * Save a relateNode.
     *
     * @param relateNodeDTO the entity to save.
     * @return the persisted entity.
     */
    public RelateNodeDTO save(RelateNodeDTO relateNodeDTO) {
        LOG.debug("Request to save RelateNode : {}", relateNodeDTO);
        RelateNode relateNode = relateNodeMapper.toEntity(relateNodeDTO);
        relateNode = relateNodeRepository.save(relateNode);
        return relateNodeMapper.toDto(relateNode);
    }

    /**
     * Update a relateNode.
     *
     * @param relateNodeDTO the entity to save.
     * @return the persisted entity.
     */
    public RelateNodeDTO update(RelateNodeDTO relateNodeDTO) {
        LOG.debug("Request to update RelateNode : {}", relateNodeDTO);
        RelateNode relateNode = relateNodeMapper.toEntity(relateNodeDTO);
        relateNode = relateNodeRepository.save(relateNode);
        return relateNodeMapper.toDto(relateNode);
    }

    /**
     * Partially update a relateNode.
     *
     * @param relateNodeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RelateNodeDTO> partialUpdate(RelateNodeDTO relateNodeDTO) {
        LOG.debug("Request to partially update RelateNode : {}", relateNodeDTO);

        return relateNodeRepository
            .findById(relateNodeDTO.getId())
            .map(existingRelateNode -> {
                relateNodeMapper.partialUpdate(existingRelateNode, relateNodeDTO);

                return existingRelateNode;
            })
            .map(relateNodeRepository::save)
            .map(relateNodeMapper::toDto);
    }

    /**
     * Get all the relateNodes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<RelateNodeDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all RelateNodes");
        return relateNodeRepository.findAll(pageable).map(relateNodeMapper::toDto);
    }

    /**
     * Get one relateNode by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RelateNodeDTO> findOne(Long id) {
        LOG.debug("Request to get RelateNode : {}", id);
        return relateNodeRepository.findById(id).map(relateNodeMapper::toDto);
    }

    /**
     * Delete the relateNode by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete RelateNode : {}", id);
        relateNodeRepository.deleteById(id);
    }
}
