package com.vnu.uet.service;

import com.vnu.uet.domain.RelateDemand;
import com.vnu.uet.repository.RelateDemandRepository;
import com.vnu.uet.service.dto.RelateDemandDTO;
import com.vnu.uet.service.mapper.RelateDemandMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.vnu.uet.domain.RelateDemand}.
 */
@Service
@Transactional
public class RelateDemandService {

    private static final Logger LOG = LoggerFactory.getLogger(RelateDemandService.class);

    private final RelateDemandRepository relateDemandRepository;

    private final RelateDemandMapper relateDemandMapper;

    public RelateDemandService(RelateDemandRepository relateDemandRepository, RelateDemandMapper relateDemandMapper) {
        this.relateDemandRepository = relateDemandRepository;
        this.relateDemandMapper = relateDemandMapper;
    }

    /**
     * Save a relateDemand.
     *
     * @param relateDemandDTO the entity to save.
     * @return the persisted entity.
     */
    public RelateDemandDTO save(RelateDemandDTO relateDemandDTO) {
        LOG.debug("Request to save RelateDemand : {}", relateDemandDTO);
        RelateDemand relateDemand = relateDemandMapper.toEntity(relateDemandDTO);
        relateDemand = relateDemandRepository.save(relateDemand);
        return relateDemandMapper.toDto(relateDemand);
    }

    /**
     * Update a relateDemand.
     *
     * @param relateDemandDTO the entity to save.
     * @return the persisted entity.
     */
    public RelateDemandDTO update(RelateDemandDTO relateDemandDTO) {
        LOG.debug("Request to update RelateDemand : {}", relateDemandDTO);
        RelateDemand relateDemand = relateDemandMapper.toEntity(relateDemandDTO);
        relateDemand = relateDemandRepository.save(relateDemand);
        return relateDemandMapper.toDto(relateDemand);
    }

    /**
     * Partially update a relateDemand.
     *
     * @param relateDemandDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RelateDemandDTO> partialUpdate(RelateDemandDTO relateDemandDTO) {
        LOG.debug("Request to partially update RelateDemand : {}", relateDemandDTO);

        return relateDemandRepository
            .findById(relateDemandDTO.getId())
            .map(existingRelateDemand -> {
                relateDemandMapper.partialUpdate(existingRelateDemand, relateDemandDTO);

                return existingRelateDemand;
            })
            .map(relateDemandRepository::save)
            .map(relateDemandMapper::toDto);
    }

    /**
     * Get all the relateDemands.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<RelateDemandDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all RelateDemands");
        return relateDemandRepository.findAll(pageable).map(relateDemandMapper::toDto);
    }

    /**
     * Get one relateDemand by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RelateDemandDTO> findOne(Long id) {
        LOG.debug("Request to get RelateDemand : {}", id);
        return relateDemandRepository.findById(id).map(relateDemandMapper::toDto);
    }

    /**
     * Delete the relateDemand by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete RelateDemand : {}", id);
        relateDemandRepository.deleteById(id);
    }
}
