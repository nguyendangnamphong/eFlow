package com.vnu.uet.service;

import com.vnu.uet.domain.MapForm;
import com.vnu.uet.domain.Variable;
import com.vnu.uet.repository.MapFormRepository;
import com.vnu.uet.repository.VariableRepository;
import com.vnu.uet.service.dto.MapFormDTO;
import com.vnu.uet.service.dto.VariableDTO;
import com.vnu.uet.service.mapper.MapFormMapper;
import com.vnu.uet.service.mapper.VariableMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ExtendedFormMappingService {

    private static final Logger LOG = LoggerFactory.getLogger(ExtendedFormMappingService.class);

    private final MapFormRepository mapFormRepository;
    private final VariableRepository variableRepository;
    private final MapFormMapper mapFormMapper;
    private final VariableMapper variableMapper;

    public ExtendedFormMappingService(
        MapFormRepository mapFormRepository,
        VariableRepository variableRepository,
        MapFormMapper mapFormMapper,
        VariableMapper variableMapper
    ) {
        this.mapFormRepository = mapFormRepository;
        this.variableRepository = variableRepository;
        this.mapFormMapper = mapFormMapper;
        this.variableMapper = variableMapper;
    }

    /**
     * Get inheritance blueprint containing mapped forms and their variables for a Node.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getInheritanceBlueprint(Long nodeId) {
        LOG.debug("Request to get Inheritance Blueprint for Node : {}", nodeId);

        List<MapForm> mapForms = mapFormRepository.findAllByNodeId(nodeId);

        List<Map<String, Object>> formsList = new ArrayList<>();

        for (MapForm mf : mapForms) {
            MapFormDTO mfDTO = mapFormMapper.toDto(mf);
            List<Variable> variables = variableRepository.findAllByMapFormId(mf.getId());
            List<VariableDTO> variableDTOs = variables.stream().map(variableMapper::toDto).collect(Collectors.toList());

            Map<String, Object> bpEntry = new HashMap<>();
            bpEntry.put("mapForm", mfDTO);
            bpEntry.put("variables", variableDTOs);
            formsList.add(bpEntry);
        }

        Map<String, Object> blueprint = new HashMap<>();
        blueprint.put("nodeId", nodeId);
        blueprint.put("forms", formsList);

        return blueprint;
    }
}
