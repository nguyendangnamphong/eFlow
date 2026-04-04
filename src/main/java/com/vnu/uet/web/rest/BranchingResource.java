package com.vnu.uet.web.rest;

import com.vnu.uet.service.RelateDemandService;
import com.vnu.uet.service.SwitchNodeService;
import com.vnu.uet.service.dto.RelateDemandDTO;
import com.vnu.uet.service.dto.SwitchNodeDTO;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api")
public class BranchingResource {

    private final Logger log = LoggerFactory.getLogger(BranchingResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SwitchNodeService switchNodeService;
    private final RelateDemandService relateDemandService;

    public BranchingResource(SwitchNodeService switchNodeService, RelateDemandService relateDemandService) {
        this.switchNodeService = switchNodeService;
        this.relateDemandService = relateDemandService;
    }

    /**
     * {@code POST  /switch/{switchId}/config} : Configure conditional node.
     */
    @PostMapping("/switch/{switchId}/config")
    public ResponseEntity<SwitchNodeDTO> configSwitchNode(
        @PathVariable("switchId") Long switchId,
        @Valid @RequestBody SwitchNodeDTO configDTO
    ) {
        log.debug("REST request to config SwitchNode : {}", switchId);
        if (!switchId.equals(configDTO.getId())) {
            return ResponseEntity.badRequest().build();
        }
        Optional<SwitchNodeDTO> result = switchNodeService.partialUpdate(configDTO);
        return ResponseUtil.wrapOrNotFound(result);
    }

    /**
     * {@code POST  /relate-demand} : Save branch conditioning for edges.
     */
    @PostMapping("/relate-demand")
    public ResponseEntity<RelateDemandDTO> saveRelateDemand(@Valid @RequestBody RelateDemandDTO dto) {
        log.debug("REST request to save RelateDemand : {}", dto);
        RelateDemandDTO result = relateDemandService.save(dto);
        return ResponseEntity.status(201).body(result);
    }

    /**
     * {@code GET  /switch/{switchId}/demands} : Get all branch conditions for a switch.
     */
    @GetMapping("/switch/{switchId}/demands")
    public ResponseEntity<List<RelateDemandDTO>> getDemandsForSwitch(@PathVariable("switchId") Long switchId) {
        log.debug("REST request to get RelateDemands for SwitchNode : {}", switchId);
        List<RelateDemandDTO> demands = relateDemandService.findAllBySwitchNodeId(switchId);
        return ResponseEntity.ok(demands);
    }
}
