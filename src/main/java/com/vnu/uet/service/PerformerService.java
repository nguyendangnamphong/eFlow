package com.vnu.uet.service;

import com.vnu.uet.domain.Performer;
import com.vnu.uet.repository.PerformerRepository;
import com.vnu.uet.service.dto.PerformerDTO;
import com.vnu.uet.service.mapper.PerformerMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.vnu.uet.domain.Performer}.
 */
@Service
@Transactional
public class PerformerService {

    private static final Logger LOG = LoggerFactory.getLogger(PerformerService.class);

    private final PerformerRepository performerRepository;

    private final PerformerMapper performerMapper;

    public PerformerService(PerformerRepository performerRepository, PerformerMapper performerMapper) {
        this.performerRepository = performerRepository;
        this.performerMapper = performerMapper;
    }

    /**
     * Save a performer.
     *
     * @param performerDTO the entity to save.
     * @return the persisted entity.
     */
    public PerformerDTO save(PerformerDTO performerDTO) {
        LOG.debug("Request to save Performer : {}", performerDTO);
        Performer performer = performerMapper.toEntity(performerDTO);
        performer = performerRepository.save(performer);
        return performerMapper.toDto(performer);
    }

    /**
     * Update a performer.
     *
     * @param performerDTO the entity to save.
     * @return the persisted entity.
     */
    public PerformerDTO update(PerformerDTO performerDTO) {
        LOG.debug("Request to update Performer : {}", performerDTO);
        Performer performer = performerMapper.toEntity(performerDTO);
        performer = performerRepository.save(performer);
        return performerMapper.toDto(performer);
    }

    /**
     * Partially update a performer.
     *
     * @param performerDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PerformerDTO> partialUpdate(PerformerDTO performerDTO) {
        LOG.debug("Request to partially update Performer : {}", performerDTO);

        return performerRepository
            .findById(performerDTO.getId())
            .map(existingPerformer -> {
                performerMapper.partialUpdate(existingPerformer, performerDTO);

                return existingPerformer;
            })
            .map(performerRepository::save)
            .map(performerMapper::toDto);
    }

    /**
     * Get all the performers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<PerformerDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Performers");
        return performerRepository.findAll(pageable).map(performerMapper::toDto);
    }

    /**
     * Get one performer by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PerformerDTO> findOne(Long id) {
        LOG.debug("Request to get Performer : {}", id);
        return performerRepository.findById(id).map(performerMapper::toDto);
    }

    /**
     * Delete the performer by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Performer : {}", id);
        performerRepository.deleteById(id);
    }
}
