package com.vnu.uet.web.rest;

import com.vnu.uet.repository.RelateNodeRepository;
import com.vnu.uet.service.RelateNodeService;
import com.vnu.uet.service.dto.RelateNodeDTO;
import com.vnu.uet.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.vnu.uet.domain.RelateNode}.
 */
@RestController
@RequestMapping("/api/relate-nodes")
public class RelateNodeResource {

    private static final Logger LOG = LoggerFactory.getLogger(RelateNodeResource.class);

    private static final String ENTITY_NAME = "eFlowRelateNode";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RelateNodeService relateNodeService;

    private final RelateNodeRepository relateNodeRepository;

    public RelateNodeResource(RelateNodeService relateNodeService, RelateNodeRepository relateNodeRepository) {
        this.relateNodeService = relateNodeService;
        this.relateNodeRepository = relateNodeRepository;
    }

    /**
     * {@code POST  /relate-nodes} : Create a new relateNode.
     *
     * @param relateNodeDTO the relateNodeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new relateNodeDTO, or with status {@code 400 (Bad Request)} if the relateNode has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<RelateNodeDTO> createRelateNode(@RequestBody RelateNodeDTO relateNodeDTO) throws URISyntaxException {
        LOG.debug("REST request to save RelateNode : {}", relateNodeDTO);
        if (relateNodeDTO.getId() != null) {
            throw new BadRequestAlertException("A new relateNode cannot already have an ID", ENTITY_NAME, "idexists");
        }
        relateNodeDTO = relateNodeService.save(relateNodeDTO);
        return ResponseEntity.created(new URI("/api/relate-nodes/" + relateNodeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, relateNodeDTO.getId().toString()))
            .body(relateNodeDTO);
    }

    /**
     * {@code PUT  /relate-nodes/:id} : Updates an existing relateNode.
     *
     * @param id the id of the relateNodeDTO to save.
     * @param relateNodeDTO the relateNodeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated relateNodeDTO,
     * or with status {@code 400 (Bad Request)} if the relateNodeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the relateNodeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RelateNodeDTO> updateRelateNode(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RelateNodeDTO relateNodeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update RelateNode : {}, {}", id, relateNodeDTO);
        if (relateNodeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, relateNodeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!relateNodeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        relateNodeDTO = relateNodeService.update(relateNodeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, relateNodeDTO.getId().toString()))
            .body(relateNodeDTO);
    }

    /**
     * {@code PATCH  /relate-nodes/:id} : Partial updates given fields of an existing relateNode, field will ignore if it is null
     *
     * @param id the id of the relateNodeDTO to save.
     * @param relateNodeDTO the relateNodeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated relateNodeDTO,
     * or with status {@code 400 (Bad Request)} if the relateNodeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the relateNodeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the relateNodeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<RelateNodeDTO> partialUpdateRelateNode(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RelateNodeDTO relateNodeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update RelateNode partially : {}, {}", id, relateNodeDTO);
        if (relateNodeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, relateNodeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!relateNodeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RelateNodeDTO> result = relateNodeService.partialUpdate(relateNodeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, relateNodeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /relate-nodes} : get all the relateNodes.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of relateNodes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<RelateNodeDTO>> getAllRelateNodes(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of RelateNodes");
        Page<RelateNodeDTO> page = relateNodeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /relate-nodes/:id} : get the "id" relateNode.
     *
     * @param id the id of the relateNodeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the relateNodeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RelateNodeDTO> getRelateNode(@PathVariable("id") Long id) {
        LOG.debug("REST request to get RelateNode : {}", id);
        Optional<RelateNodeDTO> relateNodeDTO = relateNodeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(relateNodeDTO);
    }

    /**
     * {@code DELETE  /relate-nodes/:id} : delete the "id" relateNode.
     *
     * @param id the id of the relateNodeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRelateNode(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete RelateNode : {}", id);
        relateNodeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
