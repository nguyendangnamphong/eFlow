package com.vnu.uet.web.rest;

import static com.vnu.uet.domain.SwitchNodeAsserts.*;
import static com.vnu.uet.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnu.uet.IntegrationTest;
import com.vnu.uet.domain.SwitchNode;
import com.vnu.uet.repository.SwitchNodeRepository;
import com.vnu.uet.service.dto.SwitchNodeDTO;
import com.vnu.uet.service.mapper.SwitchNodeMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link SwitchNodeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SwitchNodeResourceIT {

    private static final String DEFAULT_FORM_ID = "AAAAAAAAAA";
    private static final String UPDATED_FORM_ID = "BBBBBBBBBB";

    private static final String DEFAULT_VARIABLE_ID = "AAAAAAAAAA";
    private static final String UPDATED_VARIABLE_ID = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/switch-nodes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private SwitchNodeRepository switchNodeRepository;

    @Autowired
    private SwitchNodeMapper switchNodeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSwitchNodeMockMvc;

    private SwitchNode switchNode;

    private SwitchNode insertedSwitchNode;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SwitchNode createEntity() {
        return new SwitchNode().formId(DEFAULT_FORM_ID).variableId(DEFAULT_VARIABLE_ID);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SwitchNode createUpdatedEntity() {
        return new SwitchNode().formId(UPDATED_FORM_ID).variableId(UPDATED_VARIABLE_ID);
    }

    @BeforeEach
    public void initTest() {
        switchNode = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedSwitchNode != null) {
            switchNodeRepository.delete(insertedSwitchNode);
            insertedSwitchNode = null;
        }
    }

    @Test
    @Transactional
    void createSwitchNode() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the SwitchNode
        SwitchNodeDTO switchNodeDTO = switchNodeMapper.toDto(switchNode);
        var returnedSwitchNodeDTO = om.readValue(
            restSwitchNodeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(switchNodeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            SwitchNodeDTO.class
        );

        // Validate the SwitchNode in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedSwitchNode = switchNodeMapper.toEntity(returnedSwitchNodeDTO);
        assertSwitchNodeUpdatableFieldsEquals(returnedSwitchNode, getPersistedSwitchNode(returnedSwitchNode));

        insertedSwitchNode = returnedSwitchNode;
    }

    @Test
    @Transactional
    void createSwitchNodeWithExistingId() throws Exception {
        // Create the SwitchNode with an existing ID
        switchNode.setId(1L);
        SwitchNodeDTO switchNodeDTO = switchNodeMapper.toDto(switchNode);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSwitchNodeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(switchNodeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the SwitchNode in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSwitchNodes() throws Exception {
        // Initialize the database
        insertedSwitchNode = switchNodeRepository.saveAndFlush(switchNode);

        // Get all the switchNodeList
        restSwitchNodeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(switchNode.getId().intValue())))
            .andExpect(jsonPath("$.[*].formId").value(hasItem(DEFAULT_FORM_ID)))
            .andExpect(jsonPath("$.[*].variableId").value(hasItem(DEFAULT_VARIABLE_ID)));
    }

    @Test
    @Transactional
    void getSwitchNode() throws Exception {
        // Initialize the database
        insertedSwitchNode = switchNodeRepository.saveAndFlush(switchNode);

        // Get the switchNode
        restSwitchNodeMockMvc
            .perform(get(ENTITY_API_URL_ID, switchNode.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(switchNode.getId().intValue()))
            .andExpect(jsonPath("$.formId").value(DEFAULT_FORM_ID))
            .andExpect(jsonPath("$.variableId").value(DEFAULT_VARIABLE_ID));
    }

    @Test
    @Transactional
    void getNonExistingSwitchNode() throws Exception {
        // Get the switchNode
        restSwitchNodeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSwitchNode() throws Exception {
        // Initialize the database
        insertedSwitchNode = switchNodeRepository.saveAndFlush(switchNode);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the switchNode
        SwitchNode updatedSwitchNode = switchNodeRepository.findById(switchNode.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedSwitchNode are not directly saved in db
        em.detach(updatedSwitchNode);
        updatedSwitchNode.formId(UPDATED_FORM_ID).variableId(UPDATED_VARIABLE_ID);
        SwitchNodeDTO switchNodeDTO = switchNodeMapper.toDto(updatedSwitchNode);

        restSwitchNodeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, switchNodeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(switchNodeDTO))
            )
            .andExpect(status().isOk());

        // Validate the SwitchNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedSwitchNodeToMatchAllProperties(updatedSwitchNode);
    }

    @Test
    @Transactional
    void putNonExistingSwitchNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        switchNode.setId(longCount.incrementAndGet());

        // Create the SwitchNode
        SwitchNodeDTO switchNodeDTO = switchNodeMapper.toDto(switchNode);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSwitchNodeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, switchNodeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(switchNodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SwitchNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSwitchNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        switchNode.setId(longCount.incrementAndGet());

        // Create the SwitchNode
        SwitchNodeDTO switchNodeDTO = switchNodeMapper.toDto(switchNode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSwitchNodeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(switchNodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SwitchNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSwitchNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        switchNode.setId(longCount.incrementAndGet());

        // Create the SwitchNode
        SwitchNodeDTO switchNodeDTO = switchNodeMapper.toDto(switchNode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSwitchNodeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(switchNodeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SwitchNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSwitchNodeWithPatch() throws Exception {
        // Initialize the database
        insertedSwitchNode = switchNodeRepository.saveAndFlush(switchNode);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the switchNode using partial update
        SwitchNode partialUpdatedSwitchNode = new SwitchNode();
        partialUpdatedSwitchNode.setId(switchNode.getId());

        partialUpdatedSwitchNode.formId(UPDATED_FORM_ID);

        restSwitchNodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSwitchNode.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSwitchNode))
            )
            .andExpect(status().isOk());

        // Validate the SwitchNode in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSwitchNodeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedSwitchNode, switchNode),
            getPersistedSwitchNode(switchNode)
        );
    }

    @Test
    @Transactional
    void fullUpdateSwitchNodeWithPatch() throws Exception {
        // Initialize the database
        insertedSwitchNode = switchNodeRepository.saveAndFlush(switchNode);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the switchNode using partial update
        SwitchNode partialUpdatedSwitchNode = new SwitchNode();
        partialUpdatedSwitchNode.setId(switchNode.getId());

        partialUpdatedSwitchNode.formId(UPDATED_FORM_ID).variableId(UPDATED_VARIABLE_ID);

        restSwitchNodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSwitchNode.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedSwitchNode))
            )
            .andExpect(status().isOk());

        // Validate the SwitchNode in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertSwitchNodeUpdatableFieldsEquals(partialUpdatedSwitchNode, getPersistedSwitchNode(partialUpdatedSwitchNode));
    }

    @Test
    @Transactional
    void patchNonExistingSwitchNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        switchNode.setId(longCount.incrementAndGet());

        // Create the SwitchNode
        SwitchNodeDTO switchNodeDTO = switchNodeMapper.toDto(switchNode);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSwitchNodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, switchNodeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(switchNodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SwitchNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSwitchNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        switchNode.setId(longCount.incrementAndGet());

        // Create the SwitchNode
        SwitchNodeDTO switchNodeDTO = switchNodeMapper.toDto(switchNode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSwitchNodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(switchNodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the SwitchNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSwitchNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        switchNode.setId(longCount.incrementAndGet());

        // Create the SwitchNode
        SwitchNodeDTO switchNodeDTO = switchNodeMapper.toDto(switchNode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSwitchNodeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(switchNodeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SwitchNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSwitchNode() throws Exception {
        // Initialize the database
        insertedSwitchNode = switchNodeRepository.saveAndFlush(switchNode);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the switchNode
        restSwitchNodeMockMvc
            .perform(delete(ENTITY_API_URL_ID, switchNode.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return switchNodeRepository.count();
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

    protected SwitchNode getPersistedSwitchNode(SwitchNode switchNode) {
        return switchNodeRepository.findById(switchNode.getId()).orElseThrow();
    }

    protected void assertPersistedSwitchNodeToMatchAllProperties(SwitchNode expectedSwitchNode) {
        assertSwitchNodeAllPropertiesEquals(expectedSwitchNode, getPersistedSwitchNode(expectedSwitchNode));
    }

    protected void assertPersistedSwitchNodeToMatchUpdatableProperties(SwitchNode expectedSwitchNode) {
        assertSwitchNodeAllUpdatablePropertiesEquals(expectedSwitchNode, getPersistedSwitchNode(expectedSwitchNode));
    }
}
