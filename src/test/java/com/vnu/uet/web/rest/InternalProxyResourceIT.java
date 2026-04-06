package com.vnu.uet.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnu.uet.IntegrationTest;
import com.vnu.uet.domain.*;
import com.vnu.uet.repository.*;
import jakarta.persistence.EntityManager;
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
    void getNextNode() throws Exception {
        SwitchNode switchNode = new SwitchNode().flow(flow);
        switchNode = switchNodeRepository.saveAndFlush(switchNode);

        RelateNode edgeToSwitch = new RelateNode().flow(flow).node(node).childNodeId(switchNode.getId()).hasDemand(true);
        edgeToSwitch = relateNodeRepository.saveAndFlush(edgeToSwitch);

        Node nextNode = new Node().nodeType("assign").flow(flow);
        nextNode = nodeRepository.saveAndFlush(nextNode);

        RelateDemand demand = new RelateDemand().relateDemand("#amount > 100").switchNode(switchNode).relateNode(new RelateNode().id(999L));
        // Note: The logic in InternalProxyService looks for relateNode in relateDemand
        // Let's create a real relateNode for the destination
        RelateNode edgeFromSwitch = new RelateNode().flow(flow).node(new Node().id(switchNode.getId())).childNodeId(nextNode.getId());
        edgeFromSwitch = relateNodeRepository.saveAndFlush(edgeFromSwitch);

        demand.setRelateNode(edgeFromSwitch);
        relateDemandRepository.saveAndFlush(demand);

        Map<String, Object> formData = new HashMap<>();
        formData.put("amount", 150);

        restInternalProxyMockMvc
            .perform(
                get(API_URL + "/flow/{flowId}/next-node", flow.getId())
                    .param("currentNodeId", node.getId().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(formData))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nextNodeId").value(nextNode.getId().intValue()));
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
            .andExpect(jsonPath("$.mapForms").isArray());
    }
}
