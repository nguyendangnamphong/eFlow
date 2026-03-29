package com.vnu.uet.web.rest;

import com.vnu.uet.service.ExtendedFormMappingService;
import com.vnu.uet.service.MapFormService;
import com.vnu.uet.service.VariableService;
import com.vnu.uet.service.dto.MapFormDTO;
import com.vnu.uet.service.dto.VariableDTO;
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
@RequestMapping("/api")
public class ExtendedFormMappingResource {

    private final Logger log = LoggerFactory.getLogger(ExtendedFormMappingResource.class);

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MapFormService mapFormService;
    private final VariableService variableService;
    private final ExtendedFormMappingService extendedFormMappingService;

    public ExtendedFormMappingResource(
        MapFormService mapFormService,
        VariableService variableService,
        ExtendedFormMappingService extendedFormMappingService
    ) {
        this.mapFormService = mapFormService;
        this.variableService = variableService;
        this.extendedFormMappingService = extendedFormMappingService;
    }

    /**
     * {@code POST  /node/{nodeId}/map-form} : Assign map form to node.
     */
    @PostMapping("/node/{nodeId}/map-form")
    public ResponseEntity<MapFormDTO> assignMapForm(@PathVariable("nodeId") Long nodeId, @Valid @RequestBody MapFormDTO mapFormDTO) {
        log.debug("REST request to assign MapForm to Node : {}", nodeId);
        // Note: Needs MapFormDTO relation update for node
        MapFormDTO result = mapFormService.save(mapFormDTO);
        return ResponseEntity.status(201).body(result);
    }

    /**
     * {@code POST  /map-form/{mapFormId}/variable} : Map a variable.
     */
    @PostMapping("/map-form/{mapFormId}/variable")
    public ResponseEntity<VariableDTO> mapVariable(@PathVariable("mapFormId") Long mapFormId, @Valid @RequestBody VariableDTO variableDTO) {
        log.debug("REST request to map Variable for MapForm : {}", mapFormId);
        VariableDTO result = variableService.save(variableDTO);
        return ResponseEntity.status(201).body(result);
    }

    /**
     * {@code PUT  /variable/{variableId}/formula} : Config formula for variable inheritance.
     */
    @PutMapping("/variable/{variableId}/formula")
    public ResponseEntity<VariableDTO> configVariableFormula(@PathVariable("variableId") Long variableId, @RequestBody String formula) {
        log.debug("REST request to config Variable Formula : {}", variableId);
        VariableDTO dto = new VariableDTO();
        dto.setId(variableId);
        dto.setFormula(formula.replace("\"", ""));
        Optional<VariableDTO> result = variableService.partialUpdate(dto);
        return ResponseUtil.wrapOrNotFound(result);
    }

    /**
     * {@code GET  /node/{nodeId}/inheritance-blueprint}
     */
    @GetMapping("/node/{nodeId}/inheritance-blueprint")
    public ResponseEntity<Map<String, Object>> getInheritanceBlueprint(@PathVariable("nodeId") Long nodeId) {
        log.debug("REST request to get inheritance blueprint for Node : {}", nodeId);
        Map<String, Object> blueprint = extendedFormMappingService.getInheritanceBlueprint(nodeId);
        return ResponseEntity.ok(blueprint);
    }
}
