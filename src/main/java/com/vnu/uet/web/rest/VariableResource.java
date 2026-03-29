package com.vnu.uet.web.rest;

import com.vnu.uet.repository.VariableRepository;
import com.vnu.uet.service.VariableService;
import com.vnu.uet.service.dto.VariableDTO;
import com.vnu.uet.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.vnu.uet.domain.Variable}.
 */
@RestController
@RequestMapping("/api/variables")
public class VariableResource {

    private static final Logger LOG = LoggerFactory.getLogger(VariableResource.class);

    private static final String ENTITY_NAME = "eFlowVariable";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VariableService variableService;

    private final VariableRepository variableRepository;

    public VariableResource(VariableService variableService, VariableRepository variableRepository) {
        this.variableService = variableService;
        this.variableRepository = variableRepository;
    }

    /**
     * {@code POST  /variables} : Create a new variable.
     *
     * @param variableDTO the variableDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new variableDTO, or with status {@code 400 (Bad Request)} if the variable has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<VariableDTO> createVariable(@Valid @RequestBody VariableDTO variableDTO) throws URISyntaxException {
        LOG.debug("REST request to save Variable : {}", variableDTO);
        if (variableDTO.getId() != null) {
            throw new BadRequestAlertException("A new variable cannot already have an ID", ENTITY_NAME, "idexists");
        }
        variableDTO = variableService.save(variableDTO);
        return ResponseEntity.created(new URI("/api/variables/" + variableDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, variableDTO.getId().toString()))
            .body(variableDTO);
    }

    /**
     * {@code PUT  /variables/:id} : Updates an existing variable.
     *
     * @param id the id of the variableDTO to save.
     * @param variableDTO the variableDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated variableDTO,
     * or with status {@code 400 (Bad Request)} if the variableDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the variableDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VariableDTO> updateVariable(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody VariableDTO variableDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Variable : {}, {}", id, variableDTO);
        if (variableDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, variableDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!variableRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        variableDTO = variableService.update(variableDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, variableDTO.getId().toString()))
            .body(variableDTO);
    }

    /**
     * {@code PATCH  /variables/:id} : Partial updates given fields of an existing variable, field will ignore if it is null
     *
     * @param id the id of the variableDTO to save.
     * @param variableDTO the variableDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated variableDTO,
     * or with status {@code 400 (Bad Request)} if the variableDTO is not valid,
     * or with status {@code 404 (Not Found)} if the variableDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the variableDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<VariableDTO> partialUpdateVariable(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody VariableDTO variableDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Variable partially : {}, {}", id, variableDTO);
        if (variableDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, variableDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!variableRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<VariableDTO> result = variableService.partialUpdate(variableDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, variableDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /variables} : get all the variables.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of variables in body.
     */
    @GetMapping("")
    public ResponseEntity<List<VariableDTO>> getAllVariables(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Variables");
        Page<VariableDTO> page = variableService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /variables/:id} : get the "id" variable.
     *
     * @param id the id of the variableDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the variableDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VariableDTO> getVariable(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Variable : {}", id);
        Optional<VariableDTO> variableDTO = variableService.findOne(id);
        return ResponseUtil.wrapOrNotFound(variableDTO);
    }

    /**
     * {@code DELETE  /variables/:id} : delete the "id" variable.
     *
     * @param id the id of the variableDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVariable(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Variable : {}", id);
        variableService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
