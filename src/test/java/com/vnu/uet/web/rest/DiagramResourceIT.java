package com.vnu.uet.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnu.uet.IntegrationTest;
import com.vnu.uet.domain.Flow;
import com.vnu.uet.domain.Node;
import com.vnu.uet.repository.FlowRepository;
import com.vnu.uet.repository.NodeRepository;
import com.vnu.uet.repository.RelateNodeRepository;
import com.vnu.uet.service.dto.NodeDTO;
import com.vnu.uet.service.dto.RelateNodeDTO;
import com.vnu.uet.service.mapper.NodeMapper;
import com.vnu.uet.service.mapper.RelateNodeMapper;
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
 * Integration tests for the {@link DiagramResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DiagramResourceIT {

    private static final String API_URL = "/api/workflow";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FlowRepository flowRepository;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private RelateNodeRepository relateNodeRepository;

    @Autowired
    private NodeMapper nodeMapper;

    @Autowired
    private RelateNodeMapper relateNodeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDiagramMockMvc;

    private Flow flow;

    @BeforeEach
    public void initTest() {
        flow = new Flow().flowName("Test Flow");
        flow = flowRepository.saveAndFlush(flow);
    }

    @Test
    @Transactional
    void getFlowDefinition() throws Exception {
        Node node = new Node().nodeType("assign").flow(flow);
        node = nodeRepository.saveAndFlush(node);

        restDiagramMockMvc
            .perform(get(API_URL + "/{flowId}/definition", flow.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nodes").isArray())
            .andExpect(jsonPath("$.nodes.[0].id").value(node.getId().intValue()));
    }

    @Test
    @Transactional
    void createNode() throws Exception {
        Node node = new Node().nodeType("assign");
        NodeDTO nodeDTO = nodeMapper.toDto(node);
        nodeDTO.setFlow(new com.vnu.uet.service.dto.FlowDTO());
        nodeDTO.getFlow().setId(flow.getId());

        restDiagramMockMvc
            .perform(
                post(API_URL + "/{flowId}/node", flow.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(nodeDTO))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @Transactional
    void createEdge() throws Exception {
        Node node1 = new Node().nodeType("start").flow(flow);
        node1 = nodeRepository.saveAndFlush(node1);
        Node node2 = new Node().nodeType("end").flow(flow);
        node2 = nodeRepository.saveAndFlush(node2);

        RelateNodeDTO relateNodeDTO = new RelateNodeDTO();
        relateNodeDTO.setNode(new NodeDTO());
        relateNodeDTO.getNode().setId(node1.getId());
        relateNodeDTO.setChildNodeId(node2.getId());
        relateNodeDTO.setFlow(new com.vnu.uet.service.dto.FlowDTO());
        relateNodeDTO.getFlow().setId(flow.getId());

        restDiagramMockMvc
            .perform(
                post(API_URL + "/{flowId}/edge", flow.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(relateNodeDTO))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @Transactional
    void deleteElements() throws Exception {
        Node node = new Node().nodeType("assign").flow(flow);
        node = nodeRepository.saveAndFlush(node);

        restDiagramMockMvc
            .perform(delete(API_URL + "/elements").param("nodeIds", node.getId().toString()))
            .andExpect(status().isNoContent());

        assertThat(nodeRepository.existsById(node.getId())).isFalse();
    }
}
