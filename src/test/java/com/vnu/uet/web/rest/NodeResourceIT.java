package com.vnu.uet.web.rest;

import static com.vnu.uet.domain.NodeAsserts.*;
import static com.vnu.uet.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnu.uet.IntegrationTest;
import com.vnu.uet.domain.Node;
import com.vnu.uet.repository.NodeRepository;
import com.vnu.uet.service.dto.NodeDTO;
import com.vnu.uet.service.mapper.NodeMapper;
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
 * Integration tests for the {@link NodeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NodeResourceIT {

    private static final String DEFAULT_NODE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_NODE_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/nodes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private NodeMapper nodeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNodeMockMvc;

    private Node node;

    private Node insertedNode;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Node createEntity() {
        return new Node().nodeType(DEFAULT_NODE_TYPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Node createUpdatedEntity() {
        return new Node().nodeType(UPDATED_NODE_TYPE);
    }

    @BeforeEach
    public void initTest() {
        node = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedNode != null) {
            nodeRepository.delete(insertedNode);
            insertedNode = null;
        }
    }

    @Test
    @Transactional
    void createNode() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Node
        NodeDTO nodeDTO = nodeMapper.toDto(node);
        var returnedNodeDTO = om.readValue(
            restNodeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(nodeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            NodeDTO.class
        );

        // Validate the Node in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedNode = nodeMapper.toEntity(returnedNodeDTO);
        assertNodeUpdatableFieldsEquals(returnedNode, getPersistedNode(returnedNode));

        insertedNode = returnedNode;
    }

    @Test
    @Transactional
    void createNodeWithExistingId() throws Exception {
        // Create the Node with an existing ID
        node.setId(1L);
        NodeDTO nodeDTO = nodeMapper.toDto(node);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNodeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(nodeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Node in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllNodes() throws Exception {
        // Initialize the database
        insertedNode = nodeRepository.saveAndFlush(node);

        // Get all the nodeList
        restNodeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(node.getId().intValue())))
            .andExpect(jsonPath("$.[*].nodeType").value(hasItem(DEFAULT_NODE_TYPE)));
    }

    @Test
    @Transactional
    void getNode() throws Exception {
        // Initialize the database
        insertedNode = nodeRepository.saveAndFlush(node);

        // Get the node
        restNodeMockMvc
            .perform(get(ENTITY_API_URL_ID, node.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(node.getId().intValue()))
            .andExpect(jsonPath("$.nodeType").value(DEFAULT_NODE_TYPE));
    }

    @Test
    @Transactional
    void getNonExistingNode() throws Exception {
        // Get the node
        restNodeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingNode() throws Exception {
        // Initialize the database
        insertedNode = nodeRepository.saveAndFlush(node);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the node
        Node updatedNode = nodeRepository.findById(node.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedNode are not directly saved in db
        em.detach(updatedNode);
        updatedNode.nodeType(UPDATED_NODE_TYPE);
        NodeDTO nodeDTO = nodeMapper.toDto(updatedNode);

        restNodeMockMvc
            .perform(put(ENTITY_API_URL_ID, nodeDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(nodeDTO)))
            .andExpect(status().isOk());

        // Validate the Node in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedNodeToMatchAllProperties(updatedNode);
    }

    @Test
    @Transactional
    void putNonExistingNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        node.setId(longCount.incrementAndGet());

        // Create the Node
        NodeDTO nodeDTO = nodeMapper.toDto(node);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNodeMockMvc
            .perform(put(ENTITY_API_URL_ID, nodeDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(nodeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Node in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        node.setId(longCount.incrementAndGet());

        // Create the Node
        NodeDTO nodeDTO = nodeMapper.toDto(node);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNodeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(nodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Node in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        node.setId(longCount.incrementAndGet());

        // Create the Node
        NodeDTO nodeDTO = nodeMapper.toDto(node);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNodeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(nodeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Node in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNodeWithPatch() throws Exception {
        // Initialize the database
        insertedNode = nodeRepository.saveAndFlush(node);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the node using partial update
        Node partialUpdatedNode = new Node();
        partialUpdatedNode.setId(node.getId());

        partialUpdatedNode.nodeType(UPDATED_NODE_TYPE);

        restNodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNode.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNode))
            )
            .andExpect(status().isOk());

        // Validate the Node in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNodeUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedNode, node), getPersistedNode(node));
    }

    @Test
    @Transactional
    void fullUpdateNodeWithPatch() throws Exception {
        // Initialize the database
        insertedNode = nodeRepository.saveAndFlush(node);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the node using partial update
        Node partialUpdatedNode = new Node();
        partialUpdatedNode.setId(node.getId());

        partialUpdatedNode.nodeType(UPDATED_NODE_TYPE);

        restNodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNode.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNode))
            )
            .andExpect(status().isOk());

        // Validate the Node in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNodeUpdatableFieldsEquals(partialUpdatedNode, getPersistedNode(partialUpdatedNode));
    }

    @Test
    @Transactional
    void patchNonExistingNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        node.setId(longCount.incrementAndGet());

        // Create the Node
        NodeDTO nodeDTO = nodeMapper.toDto(node);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, nodeDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(nodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Node in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        node.setId(longCount.incrementAndGet());

        // Create the Node
        NodeDTO nodeDTO = nodeMapper.toDto(node);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNodeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(nodeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Node in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNode() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        node.setId(longCount.incrementAndGet());

        // Create the Node
        NodeDTO nodeDTO = nodeMapper.toDto(node);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNodeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(nodeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Node in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNode() throws Exception {
        // Initialize the database
        insertedNode = nodeRepository.saveAndFlush(node);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the node
        restNodeMockMvc
            .perform(delete(ENTITY_API_URL_ID, node.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return nodeRepository.count();
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

    protected Node getPersistedNode(Node node) {
        return nodeRepository.findById(node.getId()).orElseThrow();
    }

    protected void assertPersistedNodeToMatchAllProperties(Node expectedNode) {
        assertNodeAllPropertiesEquals(expectedNode, getPersistedNode(expectedNode));
    }

    protected void assertPersistedNodeToMatchUpdatableProperties(Node expectedNode) {
        assertNodeAllUpdatablePropertiesEquals(expectedNode, getPersistedNode(expectedNode));
    }
}
