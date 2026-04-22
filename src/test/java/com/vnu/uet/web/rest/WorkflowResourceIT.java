package com.vnu.uet.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnu.uet.IntegrationTest;
import com.vnu.uet.domain.Flow;
import com.vnu.uet.repository.FlowRepository;
import com.vnu.uet.service.dto.FlowGroupRequestDTO;
import com.vnu.uet.service.dto.FlowDTO;
import com.vnu.uet.service.mapper.FlowMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link WorkflowResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WorkflowResourceIT {

    private static final String DEFAULT_FLOW_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FLOW_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DEPARTMENT = "AAAAAAAAAA";
    private static final String UPDATED_DEPARTMENT = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIBE = "AAAAAAAAAA";

    private static final String API_URL = "/api/workflow";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FlowRepository flowRepository;

    @Autowired
    private FlowMapper flowMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWorkflowMockMvc;

    private Flow flow;

    private final List<Flow> insertedFlows = new ArrayList<>();

    public static Flow createEntity() {
        return new Flow().flowName(DEFAULT_FLOW_NAME).department(DEFAULT_DEPARTMENT).describe(DEFAULT_DESCRIBE);
    }

    @BeforeEach
    public void initTest() {
        flow = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (!insertedFlows.isEmpty()) {
            flowRepository.deleteAll(insertedFlows);
            insertedFlows.clear();
        }
    }

    @Test
    @Transactional
    void initWorkflow() throws Exception {
        long databaseSizeBeforeCreate = flowRepository.count();
        FlowDTO flowDTO = flowMapper.toDto(flow);

        restWorkflowMockMvc
            .perform(post(API_URL + "/init").contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flowDTO)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.flowName").value(DEFAULT_FLOW_NAME))
            .andExpect(jsonPath("$.status").value("KhoiTao"));

        assertThat(flowRepository.count()).isEqualTo(databaseSizeBeforeCreate + 1);
    }

    @Test
    @Transactional
    void getWorkflowSummary() throws Exception {
        Flow insertedFlow = flowRepository.saveAndFlush(flow);
        insertedFlows.add(insertedFlow);

        restWorkflowMockMvc
            .perform(get(API_URL + "/{flowId}/summary", insertedFlow.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(insertedFlow.getId().intValue()))
            .andExpect(jsonPath("$.flowName").value(DEFAULT_FLOW_NAME));
    }

    @Test
    @Transactional
    void updateWorkflowInfo() throws Exception {
        Flow insertedFlow = flowRepository.saveAndFlush(flow);
        insertedFlows.add(insertedFlow);
        long databaseSizeBeforeUpdate = flowRepository.count();

        Flow updatedFlow = flowRepository.findById(insertedFlow.getId()).orElseThrow();
        em.detach(updatedFlow);
        updatedFlow.flowName(UPDATED_FLOW_NAME);
        FlowDTO flowDTO = flowMapper.toDto(updatedFlow);

        restWorkflowMockMvc
            .perform(
                put(API_URL + "/{flowId}/info", flowDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(flowDTO))
            )
            .andExpect(status().isOk());

        Flow testFlow = flowRepository.findById(insertedFlow.getId()).orElseThrow();
        assertThat(testFlow.getFlowName()).isEqualTo(UPDATED_FLOW_NAME);
        assertThat(flowRepository.count()).isEqualTo(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void updateWorkflowStatus() throws Exception {
        Flow insertedFlow = flowRepository.saveAndFlush(flow);
        insertedFlows.add(insertedFlow);

        String newStatus = "ApDung";
        restWorkflowMockMvc
            .perform(
                post(API_URL + "/{flowId}/status", insertedFlow.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("\"" + newStatus + "\"")
            )
            .andExpect(status().isOk());

        Flow testFlow = flowRepository.findById(insertedFlow.getId()).orElseThrow();
        assertThat(testFlow.getStatus()).isEqualTo(newStatus);
    }

    @Test
    @Transactional
    void deleteWorkflow() throws Exception {
        Flow insertedFlow = flowRepository.saveAndFlush(flow);
        long databaseSizeBeforeDelete = flowRepository.count();

        restWorkflowMockMvc
            .perform(delete(API_URL + "/{flowId}", insertedFlow.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        assertThat(flowRepository.count()).isEqualTo(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    void getLaunchedFlowGroups_returnsDistinctNonBlankOnly() throws Exception {
        Flow launchedA1 = new Flow().flowName("A1").flowGroup("A").describe("launch").department(DEFAULT_DEPARTMENT);
        Flow launchedA2 = new Flow().flowName("A2").flowGroup("A").describe("launch").department(DEFAULT_DEPARTMENT);
        Flow draftB = new Flow().flowName("B1").flowGroup("B").describe("draft").department(DEFAULT_DEPARTMENT);
        Flow launchedNull = new Flow().flowName("N1").flowGroup(null).describe("launch").department(DEFAULT_DEPARTMENT);
        Flow launchedBlank = new Flow().flowName("E1").flowGroup("   ").describe("launch").department(DEFAULT_DEPARTMENT);

        insertedFlows.add(flowRepository.saveAndFlush(launchedA1));
        insertedFlows.add(flowRepository.saveAndFlush(launchedA2));
        insertedFlows.add(flowRepository.saveAndFlush(draftB));
        insertedFlows.add(flowRepository.saveAndFlush(launchedNull));
        insertedFlows.add(flowRepository.saveAndFlush(launchedBlank));

        restWorkflowMockMvc
            .perform(get(API_URL + "/group"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0]").value("A"))
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @Transactional
    void postWorkflow_returnsFlowsByMatchingGroupName() throws Exception {
        Flow a1 = new Flow().flowName("Flow 1").flowGroup("A").describe("launch").department(DEFAULT_DEPARTMENT);
        Flow a2 = new Flow().flowName("Flow 2").flowGroup("A").describe("draft").department(DEFAULT_DEPARTMENT);
        Flow b1 = new Flow().flowName("Flow 3").flowGroup("B").describe("launch").department(DEFAULT_DEPARTMENT);

        insertedFlows.add(flowRepository.saveAndFlush(a1));
        insertedFlows.add(flowRepository.saveAndFlush(a2));
        insertedFlows.add(flowRepository.saveAndFlush(b1));

        FlowGroupRequestDTO request = new FlowGroupRequestDTO();
        request.setFlowGroupName("A");

        restWorkflowMockMvc
            .perform(post(API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].flowId").exists())
            .andExpect(jsonPath("$[0].flowName").exists());
    }

    @Test
    @Transactional
    void postWorkflow_blankGroup_returnsEmptyList() throws Exception {
        FlowGroupRequestDTO request = new FlowGroupRequestDTO();
        request.setFlowGroupName("   ");

        restWorkflowMockMvc
            .perform(post(API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }
}
