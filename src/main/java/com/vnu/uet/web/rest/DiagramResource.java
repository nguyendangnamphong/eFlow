package com.vnu.uet.web.rest;

import com.vnu.uet.service.*;
import com.vnu.uet.service.DiagramService;
import com.vnu.uet.service.dto.*;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api/workflow")
public class DiagramResource {

    private final Logger log = LoggerFactory.getLogger(DiagramResource.class);
    private static final String ENTITY_NAME = "eFlowDiagram";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NodeService nodeService;
    private final RelateNodeService relateNodeService;
    private final SwitchNodeService switchNodeService;
    private final DiagramService diagramService;

    public DiagramResource(
        NodeService nodeService,
        RelateNodeService relateNodeService,
        SwitchNodeService switchNodeService,
        DiagramService diagramService
    ) {
        this.nodeService = nodeService;
        this.relateNodeService = relateNodeService;
        this.switchNodeService = switchNodeService;
        this.diagramService = diagramService;
    }

    /**
     * {@code GET  /{flowId}/definition} : Get diagram definition for a flow.
     */
    @GetMapping("/{flowId}/definition")
    public ResponseEntity<Map<String, Object>> getFlowDefinition(@PathVariable("flowId") Long flowId) {
        log.debug("REST request to get Flow Definition : {}", flowId);
        Map<String, Object> response = diagramService.getFlowDefinition(flowId);
        return ResponseEntity.ok(response);
    }

    /**
     * {@code POST  /{flowId}/node} : Add a new node to the diagram.
     */
    @PostMapping("/{flowId}/node")
    public ResponseEntity<NodeDTO> createNode(@PathVariable("flowId") Long flowId, @Valid @RequestBody NodeDTO nodeDTO) {
        log.debug("REST request to create Node for Flow : {}", flowId);
        // Assign flowId inside the entity relation before save. This requires setting the Flow object inside NodeDTO which isn't populated here yet.
        NodeDTO result = nodeService.save(nodeDTO);
        return ResponseEntity.status(201)
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, "Node", result.getId().toString()))
            .body(result);
    }

    /**
     * {@code POST  /{flowId}/edge} : Create an edge (RelateNode) between nodes.
     */
    @PostMapping("/{flowId}/edge")
    public ResponseEntity<RelateNodeDTO> createEdge(@PathVariable("flowId") Long flowId, @Valid @RequestBody RelateNodeDTO relateNodeDTO) {
        log.debug("REST request to create Edge for Flow : {}", flowId);
        RelateNodeDTO result = relateNodeService.save(relateNodeDTO);
        return ResponseEntity.status(201)
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, "RelateNode", result.getId().toString()))
            .body(result);
    }

    /**
     * {@code DELETE  /elements} : Delete diagram elements.
     */
    @DeleteMapping("/elements")
    public ResponseEntity<Void> deleteElements(
        @RequestParam(required = false) List<Long> nodeIds,
        @RequestParam(required = false) List<Long> edgeIds,
        @RequestParam(required = false) List<Long> switchIds
    ) {
        log.debug("REST request to delete diagram elements");
        diagramService.deleteElements(nodeIds, edgeIds, switchIds);
        return ResponseEntity.noContent().build();
    }
}
