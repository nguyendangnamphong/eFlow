package com.vnu.uet.service;

import com.vnu.uet.domain.Variable;
import com.vnu.uet.repository.VariableRepository;
import com.vnu.uet.service.dto.VariableDTO;
import com.vnu.uet.service.mapper.VariableMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.vnu.uet.domain.Variable}.
 */
@Service
@Transactional
public class VariableService {

    private static final Logger LOG = LoggerFactory.getLogger(VariableService.class);

    private final VariableRepository variableRepository;

    private final VariableMapper variableMapper;

    public VariableService(VariableRepository variableRepository, VariableMapper variableMapper) {
        this.variableRepository = variableRepository;
        this.variableMapper = variableMapper;
    }

    /**
     * Save a variable.
     *
     * @param variableDTO the entity to save.
     * @return the persisted entity.
     */
    public VariableDTO save(VariableDTO variableDTO) {
        LOG.debug("Request to save Variable : {}", variableDTO);
        Variable variable = variableMapper.toEntity(variableDTO);
        variable = variableRepository.save(variable);
        return variableMapper.toDto(variable);
    }

    /**
     * Update a variable.
     *
     * @param variableDTO the entity to save.
     * @return the persisted entity.
     */
    public VariableDTO update(VariableDTO variableDTO) {
        LOG.debug("Request to update Variable : {}", variableDTO);
        Variable variable = variableMapper.toEntity(variableDTO);
        variable = variableRepository.save(variable);
        return variableMapper.toDto(variable);
    }

    /**
     * Partially update a variable.
     *
     * @param variableDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<VariableDTO> partialUpdate(VariableDTO variableDTO) {
        LOG.debug("Request to partially update Variable : {}", variableDTO);

        return variableRepository
            .findById(variableDTO.getId())
            .map(existingVariable -> {
                variableMapper.partialUpdate(existingVariable, variableDTO);

                return existingVariable;
            })
            .map(variableRepository::save)
            .map(variableMapper::toDto);
    }

    /**
     * Get all the variables.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<VariableDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Variables");
        return variableRepository.findAll(pageable).map(variableMapper::toDto);
    }

    /**
     * Get one variable by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<VariableDTO> findOne(Long id) {
        LOG.debug("Request to get Variable : {}", id);
        return variableRepository.findById(id).map(variableMapper::toDto);
    }

    /**
     * Delete the variable by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Variable : {}", id);
        variableRepository.deleteById(id);
    }
}
