package com.vnu.uet.web.rest;

import com.vnu.uet.service.InternalProxyService;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal")
public class InternalProxyResource {

    private final Logger log = LoggerFactory.getLogger(InternalProxyResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InternalProxyService internalProxyService;

    public InternalProxyResource(InternalProxyService internalProxyService) {
        this.internalProxyService = internalProxyService;
    }

    /**
     * {@code GET  /flow/{flowId}/next-node} : Find next node based on form evaluation.
     */
    @GetMapping("/flow/{flowId}/next-node")
    public ResponseEntity<Map<String, Object>> getNextNode(
        @PathVariable("flowId") Long flowId,
        @RequestParam(name = "currentNodeId", required = true) Long currentNodeId,
        @RequestBody(required = false) Map<String, Object> currentFormData
    ) {
        log.debug("REST request to get next node for Flow : {}", flowId);

        Map<String, Object> result = internalProxyService.calculateNextNode(flowId, currentNodeId, currentFormData);
        return ResponseEntity.ok(result);
    }

    /**
     * {@code GET  /node/{nodeId}/action-plan} : Get action plan for a node.
     */
    @GetMapping("/node/{nodeId}/action-plan")
    public ResponseEntity<Map<String, Object>> getActionPlan(@PathVariable("nodeId") Long nodeId) {
        log.debug("REST request to get Action Plan for Node : {}", nodeId);

        Map<String, Object> result = internalProxyService.getActionPlan(nodeId);
        return ResponseEntity.ok(result);
    }
}
