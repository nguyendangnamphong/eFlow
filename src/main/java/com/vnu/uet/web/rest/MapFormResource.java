package com.vnu.uet.web.rest;

import com.vnu.uet.repository.MapFormRepository;
import com.vnu.uet.service.MapFormService;
import com.vnu.uet.service.dto.MapFormDTO;
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
 * REST controller for managing {@link com.vnu.uet.domain.MapForm}.
 */
@RestController
@RequestMapping("/api/map-forms")
public class MapFormResource {

    private static final Logger LOG = LoggerFactory.getLogger(MapFormResource.class);

    private static final String ENTITY_NAME = "eFlowMapForm";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MapFormService mapFormService;

    private final MapFormRepository mapFormRepository;

    public MapFormResource(MapFormService mapFormService, MapFormRepository mapFormRepository) {
        this.mapFormService = mapFormService;
        this.mapFormRepository = mapFormRepository;
    }

    /**
     * {@code POST  /map-forms} : Create a new mapForm.
     *
     * @param mapFormDTO the mapFormDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new mapFormDTO, or with status {@code 400 (Bad Request)} if the mapForm has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MapFormDTO> createMapForm(@Valid @RequestBody MapFormDTO mapFormDTO) throws URISyntaxException {
        LOG.debug("REST request to save MapForm : {}", mapFormDTO);
        if (mapFormDTO.getId() != null) {
            throw new BadRequestAlertException("A new mapForm cannot already have an ID", ENTITY_NAME, "idexists");
        }
        mapFormDTO = mapFormService.save(mapFormDTO);
        return ResponseEntity.created(new URI("/api/map-forms/" + mapFormDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, mapFormDTO.getId().toString()))
            .body(mapFormDTO);
    }

    /**
     * {@code PUT  /map-forms/:id} : Updates an existing mapForm.
     *
     * @param id the id of the mapFormDTO to save.
     * @param mapFormDTO the mapFormDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mapFormDTO,
     * or with status {@code 400 (Bad Request)} if the mapFormDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the mapFormDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MapFormDTO> updateMapForm(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MapFormDTO mapFormDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MapForm : {}, {}", id, mapFormDTO);
        if (mapFormDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mapFormDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!mapFormRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        mapFormDTO = mapFormService.update(mapFormDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, mapFormDTO.getId().toString()))
            .body(mapFormDTO);
    }

    /**
     * {@code PATCH  /map-forms/:id} : Partial updates given fields of an existing mapForm, field will ignore if it is null
     *
     * @param id the id of the mapFormDTO to save.
     * @param mapFormDTO the mapFormDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated mapFormDTO,
     * or with status {@code 400 (Bad Request)} if the mapFormDTO is not valid,
     * or with status {@code 404 (Not Found)} if the mapFormDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the mapFormDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MapFormDTO> partialUpdateMapForm(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MapFormDTO mapFormDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MapForm partially : {}, {}", id, mapFormDTO);
        if (mapFormDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, mapFormDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!mapFormRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MapFormDTO> result = mapFormService.partialUpdate(mapFormDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, mapFormDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /map-forms} : get all the mapForms.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of mapForms in body.
     */
    @GetMapping("")
    public ResponseEntity<List<MapFormDTO>> getAllMapForms(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of MapForms");
        Page<MapFormDTO> page = mapFormService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /map-forms/:id} : get the "id" mapForm.
     *
     * @param id the id of the mapFormDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the mapFormDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MapFormDTO> getMapForm(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MapForm : {}", id);
        Optional<MapFormDTO> mapFormDTO = mapFormService.findOne(id);
        return ResponseUtil.wrapOrNotFound(mapFormDTO);
    }

    /**
     * {@code DELETE  /map-forms/:id} : delete the "id" mapForm.
     *
     * @param id the id of the mapFormDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMapForm(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MapForm : {}", id);
        mapFormService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
