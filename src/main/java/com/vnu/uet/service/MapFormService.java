package com.vnu.uet.service;

import com.vnu.uet.domain.MapForm;
import com.vnu.uet.repository.MapFormRepository;
import com.vnu.uet.service.dto.MapFormDTO;
import com.vnu.uet.service.mapper.MapFormMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.vnu.uet.domain.MapForm}.
 */
@Service
@Transactional
public class MapFormService {

    private static final Logger LOG = LoggerFactory.getLogger(MapFormService.class);

    private final MapFormRepository mapFormRepository;

    private final MapFormMapper mapFormMapper;

    public MapFormService(MapFormRepository mapFormRepository, MapFormMapper mapFormMapper) {
        this.mapFormRepository = mapFormRepository;
        this.mapFormMapper = mapFormMapper;
    }

    /**
     * Save a mapForm.
     *
     * @param mapFormDTO the entity to save.
     * @return the persisted entity.
     */
    public MapFormDTO save(MapFormDTO mapFormDTO) {
        LOG.debug("Request to save MapForm : {}", mapFormDTO);
        MapForm mapForm = mapFormMapper.toEntity(mapFormDTO);
        mapForm = mapFormRepository.save(mapForm);
        return mapFormMapper.toDto(mapForm);
    }

    /**
     * Update a mapForm.
     *
     * @param mapFormDTO the entity to save.
     * @return the persisted entity.
     */
    public MapFormDTO update(MapFormDTO mapFormDTO) {
        LOG.debug("Request to update MapForm : {}", mapFormDTO);
        MapForm mapForm = mapFormMapper.toEntity(mapFormDTO);
        mapForm = mapFormRepository.save(mapForm);
        return mapFormMapper.toDto(mapForm);
    }

    /**
     * Partially update a mapForm.
     *
     * @param mapFormDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MapFormDTO> partialUpdate(MapFormDTO mapFormDTO) {
        LOG.debug("Request to partially update MapForm : {}", mapFormDTO);

        return mapFormRepository
            .findById(mapFormDTO.getId())
            .map(existingMapForm -> {
                mapFormMapper.partialUpdate(existingMapForm, mapFormDTO);

                return existingMapForm;
            })
            .map(mapFormRepository::save)
            .map(mapFormMapper::toDto);
    }

    /**
     * Get all the mapForms.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<MapFormDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all MapForms");
        return mapFormRepository.findAll(pageable).map(mapFormMapper::toDto);
    }

    /**
     * Get one mapForm by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MapFormDTO> findOne(Long id) {
        LOG.debug("Request to get MapForm : {}", id);
        return mapFormRepository.findById(id).map(mapFormMapper::toDto);
    }

    /**
     * Delete the mapForm by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete MapForm : {}", id);
        mapFormRepository.deleteById(id);
    }
}
