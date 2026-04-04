package com.vnu.uet.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnu.uet.IntegrationTest;
import com.vnu.uet.domain.MapForm;
import com.vnu.uet.domain.Node;
import com.vnu.uet.domain.Variable;
import com.vnu.uet.repository.MapFormRepository;
import com.vnu.uet.repository.NodeRepository;
import com.vnu.uet.repository.VariableRepository;
import com.vnu.uet.service.dto.MapFormDTO;
import com.vnu.uet.service.dto.NodeDTO;
import com.vnu.uet.service.dto.VariableDTO;
import com.vnu.uet.service.mapper.MapFormMapper;
import com.vnu.uet.service.mapper.VariableMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ExtendedFormMappingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ExtendedFormMappingResourceIT {

    private static final String API_URL = "/api";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private MapFormRepository mapFormRepository;

    @Autowired
    private VariableRepository variableRepository;

    @Autowired
    private MapFormMapper mapFormMapper;

    @Autowired
    private VariableMapper variableMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFormMappingMockMvc;

    private Node node;

    @BeforeEach
    public void initTest() {
        node = new Node().nodeType("assign");
        node = nodeRepository.saveAndFlush(node);
    }

    @Test
    @Transactional
    void assignMapForm() throws Exception {
        MapForm mapForm = new MapForm().targetFormId("formA").sourceFormId("formB");
        MapFormDTO mapFormDTO = mapFormMapper.toDto(mapForm);
        mapFormDTO.setNode(new NodeDTO());
        mapFormDTO.getNode().setId(node.getId());

        restFormMappingMockMvc
            .perform(
                post(API_URL + "/node/{nodeId}/map-form", node.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(mapFormDTO))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @Transactional
    void mapVariable() throws Exception {
        MapForm mapForm = new MapForm().targetFormId("formA").sourceFormId("formB").node(node);
        mapForm = mapFormRepository.saveAndFlush(mapForm);

        Variable variable = new Variable().variableSourceFormId("v1").variableTargetFormId("v2");
        VariableDTO variableDTO = variableMapper.toDto(variable);
        variableDTO.setMapForm(new MapFormDTO());
        variableDTO.getMapForm().setId(mapForm.getId());

        restFormMappingMockMvc
            .perform(
                post(API_URL + "/map-form/{mapFormId}/variable", mapForm.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(variableDTO))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @Transactional
    void configVariableFormula() throws Exception {
        MapForm mapForm = new MapForm().targetFormId("formA").sourceFormId("formB").node(node);
        mapForm = mapFormRepository.saveAndFlush(mapForm);
        Variable variable = new Variable().variableSourceFormId("v1").variableTargetFormId("v2").mapForm(mapForm);
        variable = variableRepository.saveAndFlush(variable);

        String formula = "SUM(a,b)";
        restFormMappingMockMvc
            .perform(
                put(API_URL + "/variable/{variableId}/formula", variable.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("\"" + formula + "\"")
            )
            .andExpect(status().isOk());

        Variable testVar = variableRepository.findById(variable.getId()).orElseThrow();
        assertThat(testVar.getFormula()).isEqualTo(formula);
    }

    @Test
    @Transactional
    void getInheritanceBlueprint() throws Exception {
        MapForm mapForm = new MapForm().targetFormId("formA").sourceFormId("formB").node(node);
        mapForm = mapFormRepository.saveAndFlush(mapForm);
        Variable variable = new Variable().variableSourceFormId("v1").variableTargetFormId("v2").mapForm(mapForm);
        variableRepository.saveAndFlush(variable);

        restFormMappingMockMvc
            .perform(get(API_URL + "/node/{nodeId}/inheritance-blueprint", node.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mapForms").isArray())
            .andExpect(jsonPath("$.mapForms.[0].id").value(mapForm.getId().intValue()));
    }
}
