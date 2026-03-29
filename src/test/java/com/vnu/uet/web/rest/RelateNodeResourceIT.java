package com.vnu.uet.web.rest;

import static com.vnu.uet.domain.RelateNodeAsserts.*;
import static com.vnu.uet.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnu.uet.IntegrationTest;
import com.vnu.uet.domain.RelateNode;
import com.vnu.uet.repository.RelateNodeRepository;
import com.vnu.uet.service.dto.RelateNodeDTO;
import com.vnu.uet.service.mapper.RelateNodeMapper;
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
 * Integration tests for the {@link RelateNodeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RelateNodeResourceIT {

    private static final Boolean DEFAULT_HAS_DEMAND = false;
    private static final Boolean UPDATED_HAS_DEMAND = true;

    private static final Long DEFAULT_CHILD_NODE_ID = 1L;
    private static final Long UPDATED_CHILD_NODE_ID = 2L;

    private static final String ENTITY_API_URL = "/api/relate-nodes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RelateNodeRepository relateNodeRepository;

    @Autowired
    private RelateNodeMapper relateNodeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRelateNodeMockMvc;

    private RelateNode relateNode;

    private RelateNode insertedRelateNode;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RelateNode createEntity() {
        return new RelateNode().hasDemand(DEFAULT_HAS_DEMAND).childNodeId(DEFAULT_CHILD_NODE_ID);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RelateNode createUpdatedEntity() {
        return new RelateNode().hasDemand(UPDATED_HAS_DEMAND).childNodeId(UPDATED_CHILD_NODE_ID);
    }

    @BeforeEach
    public void initTest() {
        relateNode = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedRelateNode != null) {
            relateNodeRepository.delete(insertedRelateNode);
            insertedRelateNode = null;
        }
    }

    @Test
    @Transactional
    void createRelateNode() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the RelateNode
        RelateNodeDTO relateNodeDTO = relateNodeMapper.toDto(relateNode);
        var returnedRelateNodeDTO = om.readValue(
            restRelateNodeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(relateNodeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RelateNodeDTO.class
        );

        // Validate the RelateNode in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRelateNode = relateNodeMapper.toEntity(returnedRelateNodeDTO);
        assertRelateNodeUpdatableFieldsEquals(returnedRelateNode, getPersistedRelateNode(returnedRelateNode));

        insertedRelateNode = returnedRelateNode;
    }

    @Test
    @Transactional
    void createRelateNodeWithExistingId() throws Exception {
        // Create the RelateNode with an existing ID
        relateNode.setId(1L);
        RelateNodeDTO relateNodeDTO = relateNodeMapper.toDto(relateNode);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRelateNodeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(relateNodeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the RelateNode in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllRelateNodes() throws Exception {
        // Initialize the database
        insertedRelateNode = relateNodeRepository.saveAndFlush(relateNode);

        // Get all the relateNodeList
        restRelateNodeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(relateNode.getId().intValue())))
            .andExpect(jsonPath("$.[*].hasDemand").value(hasItem(DEFAULT_HAS_DEMAND.booleanValue())))
            .andExpect(jsonPath("$.[*].childNodeId").value(hasItem(DEFAULT_CHILD_NODE_ID.intValue())));
    }

    @Test
    @Transactional
    void getRelateNode() throws Exception {
        // Initialize the database
        insertedRelateNode = relateNodeRepository.saveAndFlush(relateNode);

        // Get the relateNode
        restRelateNodeMockMvc
            .perform(get(ENTITY_API_URL_ID, relateNode.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(relateNode.getId().intValue()))
            .andExpect(jsonPath("$.hasDemand").value(DEFAULT_HAS_DEMAND.booleanValue()))
            .andExpect(jsonPath("$.childNodeId").value(DEFAULT_CHILD_NODE_ID.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingRelateNode() throws Exception {
        // Get the relateNode
        restRelateNodeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRelateNode() throws Exception {
        // Initialize the database
        insertedRelateNode = relateNodeRepository.saveAndFlush(relateNode);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the relateNode
        RelateNode updatedRelateNode = relateNodeRepository.findById(relateNode.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRelateNode are not directly saved in db
        em.detach(updatedRelateNode);
        updatedRelateNode.hasDemand(UPDATED_HAS_DEMAND).childNodeId(UPDATED_CHILD_NODE_ID);
        RelateNodeDTO relateNodeDTO = relateNodeMapper.toDto(updatedRelateNode);

        restRelateNodeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, relateNodeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(relateNodeDTO))
            )
            .andExpect(status().isOk());

        // Validate the RelateNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRelateNodeToMatchAllProperties(updatedRelateNode);
    }

    @Test
    @Transactional
    void putNonExistingRelateNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        relateNode.setId(longCount.incrementAndGet());

        // Create the RelateNode
        RelateNodeDTO relateNodeDTO = relateNodeMapper.toDto(relateNode);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRelateNodeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, relateNodeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(relateNodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RelateNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRelateNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        relateNode.setId(longCount.incrementAndGet());

        // Create the RelateNode
        RelateNodeDTO relateNodeDTO = relateNodeMapper.toDto(relateNode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRelateNodeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(relateNodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RelateNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRelateNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        relateNode.setId(longCount.incrementAndGet());

        // Create the RelateNode
        RelateNodeDTO relateNodeDTO = relateNodeMapper.toDto(relateNode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRelateNodeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(relateNodeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RelateNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRelateNodeWithPatch() throws Exception {
        // Initialize the database
        insertedRelateNode = relateNodeRepository.saveAndFlush(relateNode);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the relateNode using partial update
        RelateNode partialUpdatedRelateNode = new RelateNode();
        partialUpdatedRelateNode.setId(relateNode.getId());

        partialUpdatedRelateNode.hasDemand(UPDATED_HAS_DEMAND).childNodeId(UPDATED_CHILD_NODE_ID);

        restRelateNodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRelateNode.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRelateNode))
            )
            .andExpect(status().isOk());

        // Validate the RelateNode in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRelateNodeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRelateNode, relateNode),
            getPersistedRelateNode(relateNode)
        );
    }

    @Test
    @Transactional
    void fullUpdateRelateNodeWithPatch() throws Exception {
        // Initialize the database
        insertedRelateNode = relateNodeRepository.saveAndFlush(relateNode);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the relateNode using partial update
        RelateNode partialUpdatedRelateNode = new RelateNode();
        partialUpdatedRelateNode.setId(relateNode.getId());

        partialUpdatedRelateNode.hasDemand(UPDATED_HAS_DEMAND).childNodeId(UPDATED_CHILD_NODE_ID);

        restRelateNodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRelateNode.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRelateNode))
            )
            .andExpect(status().isOk());

        // Validate the RelateNode in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRelateNodeUpdatableFieldsEquals(partialUpdatedRelateNode, getPersistedRelateNode(partialUpdatedRelateNode));
    }

    @Test
    @Transactional
    void patchNonExistingRelateNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        relateNode.setId(longCount.incrementAndGet());

        // Create the RelateNode
        RelateNodeDTO relateNodeDTO = relateNodeMapper.toDto(relateNode);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRelateNodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, relateNodeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(relateNodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RelateNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRelateNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        relateNode.setId(longCount.incrementAndGet());

        // Create the RelateNode
        RelateNodeDTO relateNodeDTO = relateNodeMapper.toDto(relateNode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRelateNodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(relateNodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RelateNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRelateNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        relateNode.setId(longCount.incrementAndGet());

        // Create the RelateNode
        RelateNodeDTO relateNodeDTO = relateNodeMapper.toDto(relateNode);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRelateNodeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(relateNodeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RelateNode in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRelateNode() throws Exception {
        // Initialize the database
        insertedRelateNode = relateNodeRepository.saveAndFlush(relateNode);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the relateNode
        restRelateNodeMockMvc
            .perform(delete(ENTITY_API_URL_ID, relateNode.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return relateNodeRepository.count();
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

    protected RelateNode getPersistedRelateNode(RelateNode relateNode) {
        return relateNodeRepository.findById(relateNode.getId()).orElseThrow();
    }

    protected void assertPersistedRelateNodeToMatchAllProperties(RelateNode expectedRelateNode) {
        assertRelateNodeAllPropertiesEquals(expectedRelateNode, getPersistedRelateNode(expectedRelateNode));
    }

    protected void assertPersistedRelateNodeToMatchUpdatableProperties(RelateNode expectedRelateNode) {
        assertRelateNodeAllUpdatablePropertiesEquals(expectedRelateNode, getPersistedRelateNode(expectedRelateNode));
    }
}
