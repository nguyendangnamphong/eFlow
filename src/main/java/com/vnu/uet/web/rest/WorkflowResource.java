package com.vnu.uet.web.rest;

import com.vnu.uet.service.FlowService;
import com.vnu.uet.service.dto.FlowBasicDTO;
import com.vnu.uet.service.dto.FlowDTO;
import com.vnu.uet.service.dto.FlowGroupRequestDTO;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/workflow")
public class WorkflowResource {

    private final Logger log = LoggerFactory.getLogger(WorkflowResource.class);

    private static final String ENTITY_NAME = "eFlowWorkflow";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FlowService flowService;

    public WorkflowResource(FlowService flowService) {
        this.flowService = flowService;
    }

    /**
     * {@code GET /group} : Get distinct flow group names for launched flows (describe == "launch").
     * @return a list of distinct flow group names.
     */
    @GetMapping("/group")
    public ResponseEntity<List<String>> getLaunchedFlowGroups() {
        log.debug("REST request to get launched flow groups");
        return ResponseEntity.ok(flowService.findLaunchedFlowGroups());
    }

    /**
     * {@code POST /} : Get basic flows info by flow_group_name.
     * Request body example: {"flow_group_name":"A"}
     * @return list of {flowId, flowName}.
     */
    @PostMapping
    public ResponseEntity<List<FlowBasicDTO>> getFlowsByGroup(@RequestBody(required = false) FlowGroupRequestDTO request) {
        String groupName = request != null ? request.getFlowGroupName() : null;
        log.debug("REST request to get flows by group: {}", groupName);
        return ResponseEntity.ok(flowService.findFlowsByGroup(groupName));
    }

    /**
     * {@code POST  /init} : Initialize a new workflow record.
     * @param flowDTO the FlowDTO containing flowName, department, describe to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new flowDTO.
     */
    @PostMapping("/init")
    public ResponseEntity<FlowDTO> initWorkflow(@Valid @RequestBody FlowDTO flowDTO) {
        log.debug("REST request to init a new Workflow : {}", flowDTO);
        flowDTO.setId(null); // Ensure it creates a new record
        flowDTO.setFlowStartDate(Instant.now());
        flowDTO.setStatus("KhoiTao"); // Default status

        FlowDTO result = flowService.save(flowDTO);
        return ResponseEntity.status(201)
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /{flowId}/summary} : Get summary info of a workflow.
     * @param flowId the id of the workflow.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the flowDTO.
     */
    @GetMapping("/{flowId}/summary")
    public ResponseEntity<FlowDTO> getWorkflowSummary(@PathVariable("flowId") Long flowId) {
        log.debug("REST request to get Workflow Summary : {}", flowId);
        Optional<FlowDTO> flowDTO = flowService.findOne(flowId);
        return ResponseUtil.wrapOrNotFound(flowDTO);
    }

    /**
     * {@code PUT  /{flowId}/info} : Update general workflow info (name, owner, etc.).
     * @param flowId the id of the workflow.
     * @param flowDTO the FlowDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated flowDTO.
     */
    @PutMapping("/{flowId}/info")
    public ResponseEntity<FlowDTO> updateWorkflowInfo(
        @PathVariable("flowId") Long flowId,
        @Valid @RequestBody FlowDTO flowDTO
    ) {
        log.debug("REST request to update Workflow Info : {}", flowId);
        if (!flowId.equals(flowDTO.getId())) {
            return ResponseEntity.badRequest().build();
        }

        Optional<FlowDTO> result = flowService.partialUpdate(flowDTO);
        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, flowDTO.getId().toString())
        );
    }

    /**
     * {@code POST  /{flowId}/status} : Update the status of a workflow.
     * @param flowId the id of the workflow.
     * @param status string representation of the new status.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated flowDTO.
     */
    @PostMapping("/{flowId}/status")
    public ResponseEntity<FlowDTO> updateWorkflowStatus(
        @PathVariable("flowId") Long flowId,
        @RequestBody String status
    ) {
        log.debug("REST request to update Workflow Status : {} to {}", flowId, status);
        FlowDTO flowDTO = new FlowDTO();
        flowDTO.setId(flowId);
        // Ensure string quotation is handled (if passed as json string)
        String cleanStatus = status.replace("\"", "").trim();
        flowDTO.setStatus(cleanStatus);

        Optional<FlowDTO> result = flowService.partialUpdate(flowDTO);
        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, flowId.toString())
        );
    }

    /**
     * {@code DELETE  /{flowId}} : Delete a workflow and all its configuration.
     * @param flowId the id of the workflow.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{flowId}")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable("flowId") Long flowId) {
        log.debug("REST request to delete Workflow : {}", flowId);
        // TODO: In the future, verify if there are ongoing eRequests in this workflow before deleting.
        flowService.delete(flowId);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, flowId.toString()))
            .build();
    }
}
