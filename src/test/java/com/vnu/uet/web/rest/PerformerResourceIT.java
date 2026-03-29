package com.vnu.uet.web.rest;

import static com.vnu.uet.domain.PerformerAsserts.*;
import static com.vnu.uet.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnu.uet.IntegrationTest;
import com.vnu.uet.domain.Performer;
import com.vnu.uet.repository.PerformerRepository;
import com.vnu.uet.service.dto.PerformerDTO;
import com.vnu.uet.service.mapper.PerformerMapper;
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
 * Integration tests for the {@link PerformerResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PerformerResourceIT {

    private static final String DEFAULT_USER_ID = "AAAAAAAAAA";
    private static final String UPDATED_USER_ID = "BBBBBBBBBB";

    private static final Long DEFAULT_ORDER_EXECUTION = 1L;
    private static final Long UPDATED_ORDER_EXECUTION = 2L;

    private static final String ENTITY_API_URL = "/api/performers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PerformerRepository performerRepository;

    @Autowired
    private PerformerMapper performerMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPerformerMockMvc;

    private Performer performer;

    private Performer insertedPerformer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Performer createEntity() {
        return new Performer().userId(DEFAULT_USER_ID).orderExecution(DEFAULT_ORDER_EXECUTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Performer createUpdatedEntity() {
        return new Performer().userId(UPDATED_USER_ID).orderExecution(UPDATED_ORDER_EXECUTION);
    }

    @BeforeEach
    public void initTest() {
        performer = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedPerformer != null) {
            performerRepository.delete(insertedPerformer);
            insertedPerformer = null;
        }
    }

    @Test
    @Transactional
    void createPerformer() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Performer
        PerformerDTO performerDTO = performerMapper.toDto(performer);
        var returnedPerformerDTO = om.readValue(
            restPerformerMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(performerDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PerformerDTO.class
        );

        // Validate the Performer in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPerformer = performerMapper.toEntity(returnedPerformerDTO);
        assertPerformerUpdatableFieldsEquals(returnedPerformer, getPersistedPerformer(returnedPerformer));

        insertedPerformer = returnedPerformer;
    }

    @Test
    @Transactional
    void createPerformerWithExistingId() throws Exception {
        // Create the Performer with an existing ID
        performer.setId(1L);
        PerformerDTO performerDTO = performerMapper.toDto(performer);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPerformerMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(performerDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Performer in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPerformers() throws Exception {
        // Initialize the database
        insertedPerformer = performerRepository.saveAndFlush(performer);

        // Get all the performerList
        restPerformerMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(performer.getId().intValue())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID)))
            .andExpect(jsonPath("$.[*].orderExecution").value(hasItem(DEFAULT_ORDER_EXECUTION.intValue())));
    }

    @Test
    @Transactional
    void getPerformer() throws Exception {
        // Initialize the database
        insertedPerformer = performerRepository.saveAndFlush(performer);

        // Get the performer
        restPerformerMockMvc
            .perform(get(ENTITY_API_URL_ID, performer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(performer.getId().intValue()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID))
            .andExpect(jsonPath("$.orderExecution").value(DEFAULT_ORDER_EXECUTION.intValue()));
    }

    @Test
    @Transactional
    void getNonExistingPerformer() throws Exception {
        // Get the performer
        restPerformerMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPerformer() throws Exception {
        // Initialize the database
        insertedPerformer = performerRepository.saveAndFlush(performer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the performer
        Performer updatedPerformer = performerRepository.findById(performer.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPerformer are not directly saved in db
        em.detach(updatedPerformer);
        updatedPerformer.userId(UPDATED_USER_ID).orderExecution(UPDATED_ORDER_EXECUTION);
        PerformerDTO performerDTO = performerMapper.toDto(updatedPerformer);

        restPerformerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, performerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(performerDTO))
            )
            .andExpect(status().isOk());

        // Validate the Performer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPerformerToMatchAllProperties(updatedPerformer);
    }

    @Test
    @Transactional
    void putNonExistingPerformer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        performer.setId(longCount.incrementAndGet());

        // Create the Performer
        PerformerDTO performerDTO = performerMapper.toDto(performer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPerformerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, performerDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(performerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Performer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPerformer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        performer.setId(longCount.incrementAndGet());

        // Create the Performer
        PerformerDTO performerDTO = performerMapper.toDto(performer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPerformerMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(performerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Performer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPerformer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        performer.setId(longCount.incrementAndGet());

        // Create the Performer
        PerformerDTO performerDTO = performerMapper.toDto(performer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPerformerMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(performerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Performer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePerformerWithPatch() throws Exception {
        // Initialize the database
        insertedPerformer = performerRepository.saveAndFlush(performer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the performer using partial update
        Performer partialUpdatedPerformer = new Performer();
        partialUpdatedPerformer.setId(performer.getId());

        partialUpdatedPerformer.userId(UPDATED_USER_ID);

        restPerformerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPerformer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPerformer))
            )
            .andExpect(status().isOk());

        // Validate the Performer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPerformerUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPerformer, performer),
            getPersistedPerformer(performer)
        );
    }

    @Test
    @Transactional
    void fullUpdatePerformerWithPatch() throws Exception {
        // Initialize the database
        insertedPerformer = performerRepository.saveAndFlush(performer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the performer using partial update
        Performer partialUpdatedPerformer = new Performer();
        partialUpdatedPerformer.setId(performer.getId());

        partialUpdatedPerformer.userId(UPDATED_USER_ID).orderExecution(UPDATED_ORDER_EXECUTION);

        restPerformerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPerformer.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPerformer))
            )
            .andExpect(status().isOk());

        // Validate the Performer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPerformerUpdatableFieldsEquals(partialUpdatedPerformer, getPersistedPerformer(partialUpdatedPerformer));
    }

    @Test
    @Transactional
    void patchNonExistingPerformer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        performer.setId(longCount.incrementAndGet());

        // Create the Performer
        PerformerDTO performerDTO = performerMapper.toDto(performer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPerformerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, performerDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(performerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Performer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPerformer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        performer.setId(longCount.incrementAndGet());

        // Create the Performer
        PerformerDTO performerDTO = performerMapper.toDto(performer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPerformerMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(performerDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Performer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPerformer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        performer.setId(longCount.incrementAndGet());

        // Create the Performer
        PerformerDTO performerDTO = performerMapper.toDto(performer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPerformerMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(performerDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Performer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePerformer() throws Exception {
        // Initialize the database
        insertedPerformer = performerRepository.saveAndFlush(performer);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the performer
        restPerformerMockMvc
            .perform(delete(ENTITY_API_URL_ID, performer.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return performerRepository.count();
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

    protected Performer getPersistedPerformer(Performer performer) {
        return performerRepository.findById(performer.getId()).orElseThrow();
    }

    protected void assertPersistedPerformerToMatchAllProperties(Performer expectedPerformer) {
        assertPerformerAllPropertiesEquals(expectedPerformer, getPersistedPerformer(expectedPerformer));
    }

    protected void assertPersistedPerformerToMatchUpdatableProperties(Performer expectedPerformer) {
        assertPerformerAllUpdatablePropertiesEquals(expectedPerformer, getPersistedPerformer(expectedPerformer));
    }
}
