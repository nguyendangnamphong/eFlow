package com.vnu.uet.web.rest;

import com.vnu.uet.service.PerformerService;
import com.vnu.uet.service.dto.PerformerDTO;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import tech.jhipster.web.util.HeaderUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/node")
public class ExtendedPerformerResource {

    private final Logger log = LoggerFactory.getLogger(ExtendedPerformerResource.class);
    private static final String ENTITY_NAME = "eFlowPerformer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PerformerService performerService;

    public ExtendedPerformerResource(PerformerService performerService) {
        this.performerService = performerService;
    }

    /**
     * {@code POST  /{nodeId}/performer} : Add a performer to a node.
     */
    @PostMapping("/{nodeId}/performer")
    public ResponseEntity<PerformerDTO> addPerformerToNode(@PathVariable("nodeId") Long nodeId, @Valid @RequestBody PerformerDTO performerDTO) {
        log.debug("REST request to add Performer to Node : {}", nodeId);
        
        PerformerDTO result = performerService.save(performerDTO);
        return ResponseEntity.status(201)
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /{nodeId}/performers} : Get performers assigned to a node.
     */
    @GetMapping("/{nodeId}/performers")
    public ResponseEntity<List<PerformerDTO>> getPerformersForNode(@PathVariable("nodeId") Long nodeId) {
        log.debug("REST request to get Performers for Node : {}", nodeId);
        // FIXME: Replace with actual `findAllByNodeId(nodeId)` call locally when repository modified.
        List<PerformerDTO> performers = List.of(); 
        return ResponseEntity.ok(performers);
    }
}
