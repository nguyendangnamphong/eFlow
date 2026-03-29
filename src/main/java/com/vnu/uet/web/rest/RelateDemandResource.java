package com.vnu.uet.web.rest;

import com.vnu.uet.repository.RelateDemandRepository;
import com.vnu.uet.service.RelateDemandService;
import com.vnu.uet.service.dto.RelateDemandDTO;
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
 * REST controller for managing {@link com.vnu.uet.domain.RelateDemand}.
 */
@RestController
@RequestMapping("/api/relate-demands")
public class RelateDemandResource {

    private static final Logger LOG = LoggerFactory.getLogger(RelateDemandResource.class);

    private static final String ENTITY_NAME = "eFlowRelateDemand";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RelateDemandService relateDemandService;

    private final RelateDemandRepository relateDemandRepository;

    public RelateDemandResource(RelateDemandService relateDemandService, RelateDemandRepository relateDemandRepository) {
        this.relateDemandService = relateDemandService;
        this.relateDemandRepository = relateDemandRepository;
    }

    /**
     * {@code POST  /relate-demands} : Create a new relateDemand.
     *
     * @param relateDemandDTO the relateDemandDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new relateDemandDTO, or with status {@code 400 (Bad Request)} if the relateDemand has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<RelateDemandDTO> createRelateDemand(@Valid @RequestBody RelateDemandDTO relateDemandDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save RelateDemand : {}", relateDemandDTO);
        if (relateDemandDTO.getId() != null) {
            throw new BadRequestAlertException("A new relateDemand cannot already have an ID", ENTITY_NAME, "idexists");
        }
        relateDemandDTO = relateDemandService.save(relateDemandDTO);
        return ResponseEntity.created(new URI("/api/relate-demands/" + relateDemandDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, relateDemandDTO.getId().toString()))
            .body(relateDemandDTO);
    }

    /**
     * {@code PUT  /relate-demands/:id} : Updates an existing relateDemand.
     *
     * @param id the id of the relateDemandDTO to save.
     * @param relateDemandDTO the relateDemandDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated relateDemandDTO,
     * or with status {@code 400 (Bad Request)} if the relateDemandDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the relateDemandDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RelateDemandDTO> updateRelateDemand(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RelateDemandDTO relateDemandDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update RelateDemand : {}, {}", id, relateDemandDTO);
        if (relateDemandDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, relateDemandDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!relateDemandRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        relateDemandDTO = relateDemandService.update(relateDemandDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, relateDemandDTO.getId().toString()))
            .body(relateDemandDTO);
    }

    /**
     * {@code PATCH  /relate-demands/:id} : Partial updates given fields of an existing relateDemand, field will ignore if it is null
     *
     * @param id the id of the relateDemandDTO to save.
     * @param relateDemandDTO the relateDemandDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated relateDemandDTO,
     * or with status {@code 400 (Bad Request)} if the relateDemandDTO is not valid,
     * or with status {@code 404 (Not Found)} if the relateDemandDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the relateDemandDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RelateDemandDTO> partialUpdateRelateDemand(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RelateDemandDTO relateDemandDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update RelateDemand partially : {}, {}", id, relateDemandDTO);
        if (relateDemandDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, relateDemandDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!relateDemandRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RelateDemandDTO> result = relateDemandService.partialUpdate(relateDemandDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, relateDemandDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /relate-demands} : get all the relateDemands.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of relateDemands in body.
     */
    @GetMapping("")
    public ResponseEntity<List<RelateDemandDTO>> getAllRelateDemands(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of RelateDemands");
        Page<RelateDemandDTO> page = relateDemandService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /relate-demands/:id} : get the "id" relateDemand.
     *
     * @param id the id of the relateDemandDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the relateDemandDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RelateDemandDTO> getRelateDemand(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RelateDemand : {}", id);
        Optional<RelateDemandDTO> relateDemandDTO = relateDemandService.findOne(id);
        return ResponseUtil.wrapOrNotFound(relateDemandDTO);
    }

    /**
     * {@code DELETE  /relate-demands/:id} : delete the "id" relateDemand.
     *
     * @param id the id of the relateDemandDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRelateDemand(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RelateDemand : {}", id);
        relateDemandService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
