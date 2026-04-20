package com.vnu.uet.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnu.uet.IntegrationTest;
import com.vnu.uet.domain.*;
import com.vnu.uet.repository.*;
import jakarta.persistence.EntityManager;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link InternalProxyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InternalProxyResourceIT {

    private static final String API_URL = "/api/internal";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FlowRepository flowRepository;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private SwitchNodeRepository switchNodeRepository;

    @Autowired
    private RelateNodeRepository relateNodeRepository;

    @Autowired
    private RelateDemandRepository relateDemandRepository;

    @Autowired
    private PerformerRepository performerRepository;

    @Autowired
    private MapFormRepository mapFormRepository;

    @Autowired
    private VariableRepository variableRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInternalProxyMockMvc;

    private Flow flow;
    private Node node;

    @BeforeEach
    public void initTest() {
        flow = new Flow().flowName("Internal Test Flow");
        flow = flowRepository.saveAndFlush(flow);
        node = new Node().nodeType("assign").flow(flow);
        node = nodeRepository.saveAndFlush(node);
    }

    @Test
    @Transactional
    void getNextNode_supportsDemandAttachedOnlyToSwitchNode_andBodyFormData() throws Exception {
        Node targetNode = new Node().nodeType("assign").flow(flow);
        targetNode = nodeRepository.saveAndFlush(targetNode);

        RelateNode edge = new RelateNode().flow(flow).node(node).childNodeId(targetNode.getId()).hasDemand(true);
        edge = relateNodeRepository.saveAndFlush(edge);

        SwitchNode switchNode = new SwitchNode().flow(flow).relateNode(edge);
        switchNode = switchNodeRepository.saveAndFlush(switchNode);

        RelateDemand demand = new RelateDemand().relateDemand("amount > 1000").switchNode(switchNode);
        relateDemandRepository.saveAndFlush(demand);

        Map<String, Object> formData = new HashMap<>();
        formData.put("amount", 1500);

        restInternalProxyMockMvc
            .perform(
                get(API_URL + "/flow/{flowId}/next-node", flow.getId())
                    .param("currentNodeId", node.getId().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(formData))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nextNodeId").value(targetNode.getId().intValue()));
    }

    @Test
    @Transactional
    void getNextNode_supportsHashVariableSyntax_andQueryFormData() throws Exception {
        Node targetNode = new Node().nodeType("assign").flow(flow);
        targetNode = nodeRepository.saveAndFlush(targetNode);

        RelateNode edge = new RelateNode().flow(flow).node(node).childNodeId(targetNode.getId()).hasDemand(true);
        edge = relateNodeRepository.saveAndFlush(edge);

        RelateDemand demand = new RelateDemand().relateDemand("#amount > 1000").relateNode(edge);
        relateDemandRepository.saveAndFlush(demand);

        restInternalProxyMockMvc
            .perform(
                get(API_URL + "/flow/{flowId}/next-node", flow.getId())
                    .param("currentNodeId", node.getId().toString())
                    .param("formData", "{\"amount\":1500}")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nextNodeId").value(targetNode.getId().intValue()));
    }

    @Test
    @Transactional
    void getNextNode_supportsUrlEncodedQueryFormData() throws Exception {
        Node targetNode = new Node().nodeType("assign").flow(flow);
        targetNode = nodeRepository.saveAndFlush(targetNode);

        RelateNode edge = new RelateNode().flow(flow).node(node).childNodeId(targetNode.getId()).hasDemand(true);
        edge = relateNodeRepository.saveAndFlush(edge);

        RelateDemand demand = new RelateDemand().relateDemand("#amount > 1000").relateNode(edge);
        relateDemandRepository.saveAndFlush(demand);

        String encoded = URLEncoder.encode("{\"amount\":1500}", StandardCharsets.UTF_8);

        restInternalProxyMockMvc
            .perform(
                get(API_URL + "/flow/{flowId}/next-node", flow.getId())
                    .param("currentNodeId", node.getId().toString())
                    .param("formData", encoded)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nextNodeId").value(targetNode.getId().intValue()));
    }

    @Test
    @Transactional
    void getNextNode_usesDefaultBranch_whenNoDemandMatches() throws Exception {
        Node conditionalTarget = new Node().nodeType("assign").flow(flow);
        conditionalTarget = nodeRepository.saveAndFlush(conditionalTarget);

        Node defaultTarget = new Node().nodeType("assign").flow(flow);
        defaultTarget = nodeRepository.saveAndFlush(defaultTarget);

        RelateNode conditionalEdge = new RelateNode().flow(flow).node(node).childNodeId(conditionalTarget.getId()).hasDemand(true);
        conditionalEdge = relateNodeRepository.saveAndFlush(conditionalEdge);

        RelateDemand demand = new RelateDemand().relateDemand("amount > 1000").relateNode(conditionalEdge);
        relateDemandRepository.saveAndFlush(demand);

        RelateNode defaultEdge = new RelateNode().flow(flow).node(node).childNodeId(defaultTarget.getId()).hasDemand(false);
        relateNodeRepository.saveAndFlush(defaultEdge);

        Map<String, Object> formData = new HashMap<>();
        formData.put("amount", 10);

        restInternalProxyMockMvc
            .perform(
                get(API_URL + "/flow/{flowId}/next-node", flow.getId())
                    .param("currentNodeId", node.getId().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(formData))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nextNodeId").value(defaultTarget.getId().intValue()));
    }

    @Test
    @Transactional
    void getActionPlan() throws Exception {
        Performer performer = new Performer().userId("user1").orderExecution(1L).node(node);
        performerRepository.saveAndFlush(performer);

        MapForm mapForm = new MapForm().targetFormId("tf").sourceFormId("sf").node(node);
        mapForm = mapFormRepository.saveAndFlush(mapForm);

        Variable variable = new Variable().variableSourceFormId("vs").variableTargetFormId("vt").mapForm(mapForm);
        variableRepository.saveAndFlush(variable);

        restInternalProxyMockMvc
            .perform(get(API_URL + "/node/{nodeId}/action-plan", node.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.performers").isArray())
            .andExpect(jsonPath("$.forms").isArray())
            .andExpect(jsonPath("$.forms[0].variables").isArray());
    }
}
