package com.vnu.uet.web.rest;

import static com.vnu.uet.domain.RelateDemandAsserts.*;
import static com.vnu.uet.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnu.uet.IntegrationTest;
import com.vnu.uet.domain.RelateDemand;
import com.vnu.uet.repository.RelateDemandRepository;
import com.vnu.uet.service.dto.RelateDemandDTO;
import com.vnu.uet.service.mapper.RelateDemandMapper;
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
 * Integration tests for the {@link RelateDemandResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RelateDemandResourceIT {

    private static final String DEFAULT_RELATE_DEMAND = "AAAAAAAAAA";
    private static final String UPDATED_RELATE_DEMAND = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/relate-demands";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RelateDemandRepository relateDemandRepository;

    @Autowired
    private RelateDemandMapper relateDemandMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRelateDemandMockMvc;

    private RelateDemand relateDemand;

    private RelateDemand insertedRelateDemand;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RelateDemand createEntity() {
        return new RelateDemand().relateDemand(DEFAULT_RELATE_DEMAND);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RelateDemand createUpdatedEntity() {
        return new RelateDemand().relateDemand(UPDATED_RELATE_DEMAND);
    }

    @BeforeEach
    public void initTest() {
        relateDemand = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedRelateDemand != null) {
            relateDemandRepository.delete(insertedRelateDemand);
            insertedRelateDemand = null;
        }
    }

    @Test
    @Transactional
    void createRelateDemand() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the RelateDemand
        RelateDemandDTO relateDemandDTO = relateDemandMapper.toDto(relateDemand);
        var returnedRelateDemandDTO = om.readValue(
            restRelateDemandMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(relateDemandDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            RelateDemandDTO.class
        );

        // Validate the RelateDemand in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedRelateDemand = relateDemandMapper.toEntity(returnedRelateDemandDTO);
        assertRelateDemandUpdatableFieldsEquals(returnedRelateDemand, getPersistedRelateDemand(returnedRelateDemand));

        insertedRelateDemand = returnedRelateDemand;
    }

    @Test
    @Transactional
    void createRelateDemandWithExistingId() throws Exception {
        // Create the RelateDemand with an existing ID
        relateDemand.setId(1L);
        RelateDemandDTO relateDemandDTO = relateDemandMapper.toDto(relateDemand);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRelateDemandMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(relateDemandDTO)))
            .andExpect(status().isBadRequest());

        // Validate the RelateDemand in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllRelateDemands() throws Exception {
        // Initialize the database
        insertedRelateDemand = relateDemandRepository.saveAndFlush(relateDemand);

        // Get all the relateDemandList
        restRelateDemandMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(relateDemand.getId().intValue())))
            .andExpect(jsonPath("$.[*].relateDemand").value(hasItem(DEFAULT_RELATE_DEMAND)));
    }

    @Test
    @Transactional
    void getRelateDemand() throws Exception {
        // Initialize the database
        insertedRelateDemand = relateDemandRepository.saveAndFlush(relateDemand);

        // Get the relateDemand
        restRelateDemandMockMvc
            .perform(get(ENTITY_API_URL_ID, relateDemand.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(relateDemand.getId().intValue()))
            .andExpect(jsonPath("$.relateDemand").value(DEFAULT_RELATE_DEMAND));
    }

    @Test
    @Transactional
    void getNonExistingRelateDemand() throws Exception {
        // Get the relateDemand
        restRelateDemandMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRelateDemand() throws Exception {
        // Initialize the database
        insertedRelateDemand = relateDemandRepository.saveAndFlush(relateDemand);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the relateDemand
        RelateDemand updatedRelateDemand = relateDemandRepository.findById(relateDemand.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRelateDemand are not directly saved in db
        em.detach(updatedRelateDemand);
        updatedRelateDemand.relateDemand(UPDATED_RELATE_DEMAND);
        RelateDemandDTO relateDemandDTO = relateDemandMapper.toDto(updatedRelateDemand);

        restRelateDemandMockMvc
            .perform(
                put(ENTITY_API_URL_ID, relateDemandDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(relateDemandDTO))
            )
            .andExpect(status().isOk());

        // Validate the RelateDemand in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRelateDemandToMatchAllProperties(updatedRelateDemand);
    }

    @Test
    @Transactional
    void putNonExistingRelateDemand() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        relateDemand.setId(longCount.incrementAndGet());

        // Create the RelateDemand
        RelateDemandDTO relateDemandDTO = relateDemandMapper.toDto(relateDemand);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRelateDemandMockMvc
            .perform(
                put(ENTITY_API_URL_ID, relateDemandDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(relateDemandDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RelateDemand in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRelateDemand() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        relateDemand.setId(longCount.incrementAndGet());

        // Create the RelateDemand
        RelateDemandDTO relateDemandDTO = relateDemandMapper.toDto(relateDemand);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRelateDemandMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(relateDemandDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RelateDemand in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRelateDemand() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        relateDemand.setId(longCount.incrementAndGet());

        // Create the RelateDemand
        RelateDemandDTO relateDemandDTO = relateDemandMapper.toDto(relateDemand);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRelateDemandMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(relateDemandDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RelateDemand in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRelateDemandWithPatch() throws Exception {
        // Initialize the database
        insertedRelateDemand = relateDemandRepository.saveAndFlush(relateDemand);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the relateDemand using partial update
        RelateDemand partialUpdatedRelateDemand = new RelateDemand();
        partialUpdatedRelateDemand.setId(relateDemand.getId());

        partialUpdatedRelateDemand.relateDemand(UPDATED_RELATE_DEMAND);

        restRelateDemandMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRelateDemand.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRelateDemand))
            )
            .andExpect(status().isOk());

        // Validate the RelateDemand in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRelateDemandUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedRelateDemand, relateDemand),
            getPersistedRelateDemand(relateDemand)
        );
    }

    @Test
    @Transactional
    void fullUpdateRelateDemandWithPatch() throws Exception {
        // Initialize the database
        insertedRelateDemand = relateDemandRepository.saveAndFlush(relateDemand);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the relateDemand using partial update
        RelateDemand partialUpdatedRelateDemand = new RelateDemand();
        partialUpdatedRelateDemand.setId(relateDemand.getId());

        partialUpdatedRelateDemand.relateDemand(UPDATED_RELATE_DEMAND);

        restRelateDemandMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRelateDemand.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRelateDemand))
            )
            .andExpect(status().isOk());

        // Validate the RelateDemand in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRelateDemandUpdatableFieldsEquals(partialUpdatedRelateDemand, getPersistedRelateDemand(partialUpdatedRelateDemand));
    }

    @Test
    @Transactional
    void patchNonExistingRelateDemand() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        relateDemand.setId(longCount.incrementAndGet());

        // Create the RelateDemand
        RelateDemandDTO relateDemandDTO = relateDemandMapper.toDto(relateDemand);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRelateDemandMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, relateDemandDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(relateDemandDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RelateDemand in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRelateDemand() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        relateDemand.setId(longCount.incrementAndGet());

        // Create the RelateDemand
        RelateDemandDTO relateDemandDTO = relateDemandMapper.toDto(relateDemand);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRelateDemandMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(relateDemandDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the RelateDemand in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRelateDemand() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        relateDemand.setId(longCount.incrementAndGet());

        // Create the RelateDemand
        RelateDemandDTO relateDemandDTO = relateDemandMapper.toDto(relateDemand);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRelateDemandMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(relateDemandDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the RelateDemand in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRelateDemand() throws Exception {
        // Initialize the database
        insertedRelateDemand = relateDemandRepository.saveAndFlush(relateDemand);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the relateDemand
        restRelateDemandMockMvc
            .perform(delete(ENTITY_API_URL_ID, relateDemand.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return relateDemandRepository.count();
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

    protected RelateDemand getPersistedRelateDemand(RelateDemand relateDemand) {
        return relateDemandRepository.findById(relateDemand.getId()).orElseThrow();
    }

    protected void assertPersistedRelateDemandToMatchAllProperties(RelateDemand expectedRelateDemand) {
        assertRelateDemandAllPropertiesEquals(expectedRelateDemand, getPersistedRelateDemand(expectedRelateDemand));
    }

    protected void assertPersistedRelateDemandToMatchUpdatableProperties(RelateDemand expectedRelateDemand) {
        assertRelateDemandAllUpdatablePropertiesEquals(expectedRelateDemand, getPersistedRelateDemand(expectedRelateDemand));
    }
}
