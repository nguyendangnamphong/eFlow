package com.vnu.uet.web.rest;

import com.vnu.uet.repository.SwitchNodeRepository;
import com.vnu.uet.service.SwitchNodeService;
import com.vnu.uet.service.dto.SwitchNodeDTO;
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
 * REST controller for managing {@link com.vnu.uet.domain.SwitchNode}.
 */
@RestController
@RequestMapping("/api/switch-nodes")
public class SwitchNodeResource {

    private static final Logger LOG = LoggerFactory.getLogger(SwitchNodeResource.class);

    private static final String ENTITY_NAME = "eFlowSwitchNode";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SwitchNodeService switchNodeService;

    private final SwitchNodeRepository switchNodeRepository;

    public SwitchNodeResource(SwitchNodeService switchNodeService, SwitchNodeRepository switchNodeRepository) {
        this.switchNodeService = switchNodeService;
        this.switchNodeRepository = switchNodeRepository;
    }

    /**
     * {@code POST  /switch-nodes} : Create a new switchNode.
     *
     * @param switchNodeDTO the switchNodeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new switchNodeDTO, or with status {@code 400 (Bad Request)} if the switchNode has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<SwitchNodeDTO> createSwitchNode(@Valid @RequestBody SwitchNodeDTO switchNodeDTO) throws URISyntaxException {
        LOG.debug("REST request to save SwitchNode : {}", switchNodeDTO);
        if (switchNodeDTO.getId() != null) {
            throw new BadRequestAlertException("A new switchNode cannot already have an ID", ENTITY_NAME, "idexists");
        }
        switchNodeDTO = switchNodeService.save(switchNodeDTO);
        return ResponseEntity.created(new URI("/api/switch-nodes/" + switchNodeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, switchNodeDTO.getId().toString()))
            .body(switchNodeDTO);
    }

    /**
     * {@code PUT  /switch-nodes/:id} : Updates an existing switchNode.
     *
     * @param id the id of the switchNodeDTO to save.
     * @param switchNodeDTO the switchNodeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated switchNodeDTO,
     * or with status {@code 400 (Bad Request)} if the switchNodeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the switchNodeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SwitchNodeDTO> updateSwitchNode(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SwitchNodeDTO switchNodeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update SwitchNode : {}, {}", id, switchNodeDTO);
        if (switchNodeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, switchNodeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!switchNodeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        switchNodeDTO = switchNodeService.update(switchNodeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, switchNodeDTO.getId().toString()))
            .body(switchNodeDTO);
    }

    /**
     * {@code PATCH  /switch-nodes/:id} : Partial updates given fields of an existing switchNode, field will ignore if it is null
     *
     * @param id the id of the switchNodeDTO to save.
     * @param switchNodeDTO the switchNodeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated switchNodeDTO,
     * or with status {@code 400 (Bad Request)} if the switchNodeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the switchNodeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the switchNodeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SwitchNodeDTO> partialUpdateSwitchNode(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SwitchNodeDTO switchNodeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update SwitchNode partially : {}, {}", id, switchNodeDTO);
        if (switchNodeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, switchNodeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!switchNodeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SwitchNodeDTO> result = switchNodeService.partialUpdate(switchNodeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, switchNodeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /switch-nodes} : get all the switchNodes.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of switchNodes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<SwitchNodeDTO>> getAllSwitchNodes(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of SwitchNodes");
        Page<SwitchNodeDTO> page = switchNodeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /switch-nodes/:id} : get the "id" switchNode.
     *
     * @param id the id of the switchNodeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the switchNodeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SwitchNodeDTO> getSwitchNode(@PathVariable("id") Long id) {
        LOG.debug("REST request to get SwitchNode : {}", id);
        Optional<SwitchNodeDTO> switchNodeDTO = switchNodeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(switchNodeDTO);
    }

    /**
     * {@code DELETE  /switch-nodes/:id} : delete the "id" switchNode.
     *
     * @param id the id of the switchNodeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSwitchNode(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete SwitchNode : {}", id);
        switchNodeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
