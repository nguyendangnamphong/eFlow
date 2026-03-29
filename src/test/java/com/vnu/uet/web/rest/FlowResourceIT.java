package com.vnu.uet.web.rest;

import static com.vnu.uet.domain.FlowAsserts.*;
import static com.vnu.uet.web.rest.TestUtil.createUpdateProxyForBean;
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
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link FlowResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FlowResourceIT {

    private static final String DEFAULT_FLOW_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FLOW_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FLOW_GROUP = "AAAAAAAAAA";
    private static final String UPDATED_FLOW_GROUP = "BBBBBBBBBB";

    private static final String DEFAULT_OWNER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_OWNER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SUPERVISER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SUPERVISER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DEPARTMENT = "AAAAAAAAAA";
    private static final String UPDATED_DEPARTMENT = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIBE = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIBE = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Instant DEFAULT_FLOW_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FLOW_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_FLOW_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FLOW_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/flows";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FlowRepository flowRepository;

    @Autowired
    private FlowMapper flowMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFlowMockMvc;

    private Flow flow;

    private Flow insertedFlow;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Flow createEntity() {
        return new Flow()
            .flowName(DEFAULT_FLOW_NAME)
            .flowGroup(DEFAULT_FLOW_GROUP)
            .ownerName(DEFAULT_OWNER_NAME)
            .superviserName(DEFAULT_SUPERVISER_NAME)
            .department(DEFAULT_DEPARTMENT)
            .describe(DEFAULT_DESCRIBE)
            .status(DEFAULT_STATUS)
            .flowStartDate(DEFAULT_FLOW_START_DATE)
            .flowEndDate(DEFAULT_FLOW_END_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Flow createUpdatedEntity() {
        return new Flow()
            .flowName(UPDATED_FLOW_NAME)
            .flowGroup(UPDATED_FLOW_GROUP)
            .ownerName(UPDATED_OWNER_NAME)
            .superviserName(UPDATED_SUPERVISER_NAME)
            .department(UPDATED_DEPARTMENT)
            .describe(UPDATED_DESCRIBE)
            .status(UPDATED_STATUS)
            .flowStartDate(UPDATED_FLOW_START_DATE)
            .flowEndDate(UPDATED_FLOW_END_DATE);
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
    void createFlow() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Flow
        FlowDTO flowDTO = flowMapper.toDto(flow);
        var returnedFlowDTO = om.readValue(
            restFlowMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flowDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FlowDTO.class
        );

        // Validate the Flow in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFlow = flowMapper.toEntity(returnedFlowDTO);
        assertFlowUpdatableFieldsEquals(returnedFlow, getPersistedFlow(returnedFlow));

        insertedFlow = returnedFlow;
    }

    @Test
    @Transactional
    void createFlowWithExistingId() throws Exception {
        // Create the Flow with an existing ID
        flow.setId(1L);
        FlowDTO flowDTO = flowMapper.toDto(flow);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFlowMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flowDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Flow in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllFlows() throws Exception {
        // Initialize the database
        insertedFlow = flowRepository.saveAndFlush(flow);

        // Get all the flowList
        restFlowMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(flow.getId().intValue())))
            .andExpect(jsonPath("$.[*].flowName").value(hasItem(DEFAULT_FLOW_NAME)))
            .andExpect(jsonPath("$.[*].flowGroup").value(hasItem(DEFAULT_FLOW_GROUP)))
            .andExpect(jsonPath("$.[*].ownerName").value(hasItem(DEFAULT_OWNER_NAME)))
            .andExpect(jsonPath("$.[*].superviserName").value(hasItem(DEFAULT_SUPERVISER_NAME)))
            .andExpect(jsonPath("$.[*].department").value(hasItem(DEFAULT_DEPARTMENT)))
            .andExpect(jsonPath("$.[*].describe").value(hasItem(DEFAULT_DESCRIBE)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].flowStartDate").value(hasItem(DEFAULT_FLOW_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].flowEndDate").value(hasItem(DEFAULT_FLOW_END_DATE.toString())));
    }

    @Test
    @Transactional
    void getFlow() throws Exception {
        // Initialize the database
        insertedFlow = flowRepository.saveAndFlush(flow);

        // Get the flow
        restFlowMockMvc
            .perform(get(ENTITY_API_URL_ID, flow.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(flow.getId().intValue()))
            .andExpect(jsonPath("$.flowName").value(DEFAULT_FLOW_NAME))
            .andExpect(jsonPath("$.flowGroup").value(DEFAULT_FLOW_GROUP))
            .andExpect(jsonPath("$.ownerName").value(DEFAULT_OWNER_NAME))
            .andExpect(jsonPath("$.superviserName").value(DEFAULT_SUPERVISER_NAME))
            .andExpect(jsonPath("$.department").value(DEFAULT_DEPARTMENT))
            .andExpect(jsonPath("$.describe").value(DEFAULT_DESCRIBE))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.flowStartDate").value(DEFAULT_FLOW_START_DATE.toString()))
            .andExpect(jsonPath("$.flowEndDate").value(DEFAULT_FLOW_END_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingFlow() throws Exception {
        // Get the flow
        restFlowMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFlow() throws Exception {
        // Initialize the database
        insertedFlow = flowRepository.saveAndFlush(flow);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the flow
        Flow updatedFlow = flowRepository.findById(flow.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFlow are not directly saved in db
        em.detach(updatedFlow);
        updatedFlow
            .flowName(UPDATED_FLOW_NAME)
            .flowGroup(UPDATED_FLOW_GROUP)
            .ownerName(UPDATED_OWNER_NAME)
            .superviserName(UPDATED_SUPERVISER_NAME)
            .department(UPDATED_DEPARTMENT)
            .describe(UPDATED_DESCRIBE)
            .status(UPDATED_STATUS)
            .flowStartDate(UPDATED_FLOW_START_DATE)
            .flowEndDate(UPDATED_FLOW_END_DATE);
        FlowDTO flowDTO = flowMapper.toDto(updatedFlow);

        restFlowMockMvc
            .perform(put(ENTITY_API_URL_ID, flowDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flowDTO)))
            .andExpect(status().isOk());

        // Validate the Flow in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFlowToMatchAllProperties(updatedFlow);
    }

    @Test
    @Transactional
    void putNonExistingFlow() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flow.setId(longCount.incrementAndGet());

        // Create the Flow
        FlowDTO flowDTO = flowMapper.toDto(flow);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFlowMockMvc
            .perform(put(ENTITY_API_URL_ID, flowDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flowDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Flow in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFlow() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flow.setId(longCount.incrementAndGet());

        // Create the Flow
        FlowDTO flowDTO = flowMapper.toDto(flow);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlowMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(flowDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Flow in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFlow() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flow.setId(longCount.incrementAndGet());

        // Create the Flow
        FlowDTO flowDTO = flowMapper.toDto(flow);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlowMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flowDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Flow in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFlowWithPatch() throws Exception {
        // Initialize the database
        insertedFlow = flowRepository.saveAndFlush(flow);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the flow using partial update
        Flow partialUpdatedFlow = new Flow();
        partialUpdatedFlow.setId(flow.getId());

        partialUpdatedFlow.department(UPDATED_DEPARTMENT).describe(UPDATED_DESCRIBE).flowEndDate(UPDATED_FLOW_END_DATE);

        restFlowMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFlow.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFlow))
            )
            .andExpect(status().isOk());

        // Validate the Flow in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFlowUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedFlow, flow), getPersistedFlow(flow));
    }

    @Test
    @Transactional
    void fullUpdateFlowWithPatch() throws Exception {
        // Initialize the database
        insertedFlow = flowRepository.saveAndFlush(flow);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the flow using partial update
        Flow partialUpdatedFlow = new Flow();
        partialUpdatedFlow.setId(flow.getId());

        partialUpdatedFlow
            .flowName(UPDATED_FLOW_NAME)
            .flowGroup(UPDATED_FLOW_GROUP)
            .ownerName(UPDATED_OWNER_NAME)
            .superviserName(UPDATED_SUPERVISER_NAME)
            .department(UPDATED_DEPARTMENT)
            .describe(UPDATED_DESCRIBE)
            .status(UPDATED_STATUS)
            .flowStartDate(UPDATED_FLOW_START_DATE)
            .flowEndDate(UPDATED_FLOW_END_DATE);

        restFlowMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFlow.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFlow))
            )
            .andExpect(status().isOk());

        // Validate the Flow in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFlowUpdatableFieldsEquals(partialUpdatedFlow, getPersistedFlow(partialUpdatedFlow));
    }

    @Test
    @Transactional
    void patchNonExistingFlow() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flow.setId(longCount.incrementAndGet());

        // Create the Flow
        FlowDTO flowDTO = flowMapper.toDto(flow);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFlowMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, flowDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(flowDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Flow in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFlow() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flow.setId(longCount.incrementAndGet());

        // Create the Flow
        FlowDTO flowDTO = flowMapper.toDto(flow);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlowMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(flowDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Flow in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFlow() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flow.setId(longCount.incrementAndGet());

        // Create the Flow
        FlowDTO flowDTO = flowMapper.toDto(flow);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlowMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(flowDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Flow in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFlow() throws Exception {
        // Initialize the database
        insertedFlow = flowRepository.saveAndFlush(flow);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the flow
        restFlowMockMvc
            .perform(delete(ENTITY_API_URL_ID, flow.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return flowRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Flow getPersistedFlow(Flow flow) {
        return flowRepository.findById(flow.getId()).orElseThrow();
    }

    protected void assertPersistedFlowToMatchAllProperties(Flow expectedFlow) {
        assertFlowAllPropertiesEquals(expectedFlow, getPersistedFlow(expectedFlow));
    }

    protected void assertPersistedFlowToMatchUpdatableProperties(Flow expectedFlow) {
        assertFlowAllUpdatablePropertiesEquals(expectedFlow, getPersistedFlow(expectedFlow));
    }
}
