package com.vnu.uet.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnu.uet.IntegrationTest;
import com.vnu.uet.domain.Flow;
import com.vnu.uet.repository.FlowRepository;
import com.vnu.uet.service.dto.FlowDTO;
import com.vnu.uet.service.mapper.FlowMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
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

    private Flow insertedFlow;

    public static Flow createEntity() {
        return new Flow().flowName(DEFAULT_FLOW_NAME).department(DEFAULT_DEPARTMENT).describe(DEFAULT_DESCRIBE);
    }

    @BeforeEach
    public void initTest() {
        flow = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedFlow != null) {
            flowRepository.delete(insertedFlow);
            insertedFlow = null;
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
        insertedFlow = flowRepository.saveAndFlush(flow);

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
        insertedFlow = flowRepository.saveAndFlush(flow);
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
        insertedFlow = flowRepository.saveAndFlush(flow);

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
        insertedFlow = flowRepository.saveAndFlush(flow);
        long databaseSizeBeforeDelete = flowRepository.count();

        restWorkflowMockMvc
            .perform(delete(API_URL + "/{flowId}", insertedFlow.getId()).accept(MediaType.A