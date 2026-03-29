package com.vnu.uet.web.rest;

import static com.vnu.uet.domain.MapFormAsserts.*;
import static com.vnu.uet.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnu.uet.IntegrationTest;
import com.vnu.uet.domain.MapForm;
import com.vnu.uet.repository.MapFormRepository;
import com.vnu.uet.service.dto.MapFormDTO;
import com.vnu.uet.service.mapper.MapFormMapper;
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
 * Integration tests for the {@link MapFormResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MapFormResourceIT {

    private static final String DEFAULT_TARGET_FORM_ID = "AAAAAAAAAA";
    private static final String UPDATED_TARGET_FORM_ID = "BBBBBBBBBB";

    private static final String DEFAULT_SOURCE_FORM_ID = "AAAAAAAAAA";
    private static final String UPDATED_SOURCE_FORM_ID = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/map-forms";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MapFormRepository mapFormRepository;

    @Autowired
    private MapFormMapper mapFormMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMapFormMockMvc;

    private MapForm mapForm;

    private MapForm insertedMapForm;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MapForm createEntity() {
        return new MapForm().targetFormId(DEFAULT_TARGET_FORM_ID).sourceFormId(DEFAULT_SOURCE_FORM_ID);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MapForm createUpdatedEntity() {
        return new MapForm().targetFormId(UPDATED_TARGET_FORM_ID).sourceFormId(UPDATED_SOURCE_FORM_ID);
    }

    @BeforeEach
    public void initTest() {
        mapForm = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedMapForm != null) {
            mapFormRepository.delete(insertedMapForm);
            insertedMapForm = null;
        }
    }

    @Test
    @Transactional
    void createMapForm() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MapForm
        MapFormDTO mapFormDTO = mapFormMapper.toDto(mapForm);
        var returnedMapFormDTO = om.readValue(
            restMapFormMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mapFormDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MapFormDTO.class
        );

        // Validate the MapForm in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMapForm = mapFormMapper.toEntity(returnedMapFormDTO);
        assertMapFormUpdatableFieldsEquals(returnedMapForm, getPersistedMapForm(returnedMapForm));

        insertedMapForm = returnedMapForm;
    }

    @Test
    @Transactional
    void createMapFormWithExistingId() throws Exception {
        // Create the MapForm with an existing ID
        mapForm.setId(1L);
        MapFormDTO mapFormDTO = mapFormMapper.toDto(mapForm);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMapFormMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mapFormDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MapForm in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllMapForms() throws Exception {
        // Initialize the database
        insertedMapForm = mapFormRepository.saveAndFlush(mapForm);

        // Get all the mapFormList
        restMapFormMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(mapForm.getId().intValue())))
            .andExpect(jsonPath("$.[*].targetFormId").value(hasItem(DEFAULT_TARGET_FORM_ID)))
            .andExpect(jsonPath("$.[*].sourceFormId").value(hasItem(DEFAULT_SOURCE_FORM_ID)));
    }

    @Test
    @Transactional
    void getMapForm() throws Exception {
        // Initialize the database
        insertedMapForm = mapFormRepository.saveAndFlush(mapForm);

        // Get the mapForm
        restMapFormMockMvc
            .perform(get(ENTITY_API_URL_ID, mapForm.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(mapForm.getId().intValue()))
            .andExpect(jsonPath("$.targetFormId").value(DEFAULT_TARGET_FORM_ID))
            .andExpect(jsonPath("$.sourceFormId").value(DEFAULT_SOURCE_FORM_ID));
    }

    @Test
    @Transactional
    void getNonExistingMapForm() throws Exception {
        // Get the mapForm
        restMapFormMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMapForm() throws Exception {
        // Initialize the database
        insertedMapForm = mapFormRepository.saveAndFlush(mapForm);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mapForm
        MapForm updatedMapForm = mapFormRepository.findById(mapForm.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMapForm are not directly saved in db
        em.detach(updatedMapForm);
        updatedMapForm.targetFormId(UPDATED_TARGET_FORM_ID).sourceFormId(UPDATED_SOURCE_FORM_ID);
        MapFormDTO mapFormDTO = mapFormMapper.toDto(updatedMapForm);

        restMapFormMockMvc
            .perform(
                put(ENTITY_API_URL_ID, mapFormDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mapFormDTO))
            )
            .andExpect(status().isOk());

        // Validate the MapForm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMapFormToMatchAllProperties(updatedMapForm);
    }

    @Test
    @Transactional
    void putNonExistingMapForm() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mapForm.setId(longCount.incrementAndGet());

        // Create the MapForm
        MapFormDTO mapFormDTO = mapFormMapper.toDto(mapForm);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMapFormMockMvc
            .perform(
                put(ENTITY_API_URL_ID, mapFormDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mapFormDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MapForm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMapForm() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mapForm.setId(longCount.incrementAndGet());

        // Create the MapForm
        MapFormDTO mapFormDTO = mapFormMapper.toDto(mapForm);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMapFormMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(mapFormDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MapForm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMapForm() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mapForm.setId(longCount.incrementAndGet());

        // Create the MapForm
        MapFormDTO mapFormDTO = mapFormMapper.toDto(mapForm);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMapFormMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(mapFormDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MapForm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMapFormWithPatch() throws Exception {
        // Initialize the database
        insertedMapForm = mapFormRepository.saveAndFlush(mapForm);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mapForm using partial update
        MapForm partialUpdatedMapForm = new MapForm();
        partialUpdatedMapForm.setId(mapForm.getId());

        partialUpdatedMapForm.targetFormId(UPDATED_TARGET_FORM_ID).sourceFormId(UPDATED_SOURCE_FORM_ID);

        restMapFormMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMapForm.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMapForm))
            )
            .andExpect(status().isOk());

        // Validate the MapForm in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMapFormUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedMapForm, mapForm), getPersistedMapForm(mapForm));
    }

    @Test
    @Transactional
    void fullUpdateMapFormWithPatch() throws Exception {
        // Initialize the database
        insertedMapForm = mapFormRepository.saveAndFlush(mapForm);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the mapForm using partial update
        MapForm partialUpdatedMapForm = new MapForm();
        partialUpdatedMapForm.setId(mapForm.getId());

        partialUpdatedMapForm.targetFormId(UPDATED_TARGET_FORM_ID).sourceFormId(UPDATED_SOURCE_FORM_ID);

        restMapFormMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMapForm.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMapForm))
            )
            .andExpect(status().isOk());

        // Validate the MapForm in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMapFormUpdatableFieldsEquals(partialUpdatedMapForm, getPersistedMapForm(partialUpdatedMapForm));
    }

    @Test
    @Transactional
    void patchNonExistingMapForm() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mapForm.setId(longCount.incrementAndGet());

        // Create the MapForm
        MapFormDTO mapFormDTO = mapFormMapper.toDto(mapForm);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMapFormMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, mapFormDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(mapFormDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MapForm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMapForm() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mapForm.setId(longCount.incrementAndGet());

        // Create the MapForm
        MapFormDTO mapFormDTO = mapFormMapper.toDto(mapForm);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMapFormMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(mapFormDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MapForm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMapForm() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        mapForm.setId(longCount.incrementAndGet());

        // Create the MapForm
        MapFormDTO mapFormDTO = mapFormMapper.toDto(mapForm);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMapFormMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(mapFormDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MapForm in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMapForm() throws Exception {
        // Initialize the database
        insertedMapForm = mapFormRepository.saveAndFlush(mapForm);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the mapForm
        restMapFormMockMvc
            .perform(delete(ENTITY_API_URL_ID, mapForm.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return mapFormRepository.count();
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

    protected MapForm getPersistedMapForm(MapForm mapForm) {
        return mapFormRepository.findById(mapForm.getId()).orElseThrow();
    }

    protected void assertPersistedMapFormToMatchAllProperties(MapForm expectedMapForm) {
        assertMapFormAllPropertiesEquals(expectedMapForm, getPersistedMapForm(expectedMapForm));
    }

    protected void assertPersistedMapFormToMatchUpdatableProperties(MapForm expectedMapForm) {
        assertMapFormAllUpdatablePropertiesEquals(expectedMapForm, getPersistedMapForm(expectedMapForm));
    }
}
