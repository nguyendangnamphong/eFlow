package com.vnu.uet.web.rest;

import com.vnu.uet.repository.PerformerRepository;
import com.vnu.uet.service.PerformerService;
import com.vnu.uet.service.dto.PerformerDTO;
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
 * REST controller for managing {@link com.vnu.uet.domain.Performer}.
 */
@RestController
@RequestMapping("/api/performers")
public class PerformerResource {

    private static final Logger LOG = LoggerFactory.getLogger(PerformerResource.class);

    private static final String ENTITY_NAME = "eFlowPerformer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PerformerService performerService;

    private final PerformerRepository performerRepository;

    public PerformerResource(PerformerService performerService, PerformerRepository performerRepository) {
        this.performerService = performerService;
        this.performerRepository = performerRepository;
    }

    /**
     * {@code POST  /performers} : Create a new performer.
     *
     * @param performerDTO the performerDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new performerDTO, or with status {@code 400 (Bad Request)} if the performer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PerformerDTO> createPerformer(@Valid @RequestBody PerformerDTO performerDTO) throws URISyntaxException {
        LOG.debug("REST request to save Performer : {}", performerDTO);
        if (performerDTO.getId() != null) {
            throw new BadRequestAlertException("A new performer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        performerDTO = performerService.save(performerDTO);
        return ResponseEntity.created(new URI("/api/performers/" + performerDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, performerDTO.getId().toString()))
            .body(performerDTO);
    }

    /**
     * {@code PUT  /performers/:id} : Updates an existing performer.
     *
     * @param id the id of the performerDTO to save.
     * @param performerDTO the performerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated performerDTO,
     * or with status {@code 400 (Bad Request)} if the performerDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the performerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PerformerDTO> updatePerformer(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PerformerDTO performerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Performer : {}, {}", id, performerDTO);
        if (performerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, performerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!performerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        performerDTO = performerService.update(performerDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, performerDTO.getId().toString()))
            .body(performerDTO);
    }

    /**
     * {@code PATCH  /performers/:id} : Partial updates given fields of an existing performer, field will ignore if it is null
     *
     * @param id the id of the performerDTO to save.
     * @param performerDTO the performerDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated performerDTO,
     * or with status {@code 400 (Bad Request)} if the performerDTO is not valid,
     * or with status {@code 404 (Not Found)} if the performerDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the performerDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PerformerDTO> partialUpdatePerformer(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PerformerDTO performerDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Performer partially : {}, {}", id, performerDTO);
        if (performerDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, performerDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!performerRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PerformerDTO> result = performerService.partialUpdate(performerDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, performerDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /performers} : get all the performers.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of performers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PerformerDTO>> getAllPerformers(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Performers");
        Page<PerformerDTO> page = performerService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /performers/:id} : get the "id" performer.
     *
     * @param id the id of the performerDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the performerDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PerformerDTO> getPerformer(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Performer : {}", id);
        Optional<PerformerDTO> performerDTO = performerService.findOne(id);
        return ResponseUtil.wrapOrNotFound(performerDTO);
    }

    /**
     * {@code DELETE  /performers/:id} : delete the "id" performer.
     *
     * @param id the id of the performerDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerformer(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Performer : {}", id);
        performerService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
