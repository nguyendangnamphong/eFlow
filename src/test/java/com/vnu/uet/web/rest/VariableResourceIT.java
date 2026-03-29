package com.vnu.uet.web.rest;

import static com.vnu.uet.domain.VariableAsserts.*;
import static com.vnu.uet.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vnu.uet.IntegrationTest;
import com.vnu.uet.domain.Variable;
import com.vnu.uet.repository.VariableRepository;
import com.vnu.uet.service.dto.VariableDTO;
import com.vnu.uet.service.mapper.VariableMapper;
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
 * Integration tests for the {@link VariableResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class VariableResourceIT {

    private static final String DEFAULT_VARIABLE_SOURCE_FORM_ID = "AAAAAAAAAA";
    private static final String UPDATED_VARIABLE_SOURCE_FORM_ID = "BBBBBBBBBB";

    private static final String DEFAULT_VARIABLE_TARGET_FORM_ID = "AAAAAAAAAA";
    private static final String UPDATED_VARIABLE_TARGET_FORM_ID = "BBBBBBBBBB";

    private static final String DEFAULT_FORMULA = "AAAAAAAAAA";
    private static final String UPDATED_FORMULA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/variables";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private VariableRepository variableRepository;

    @Autowired
    private VariableMapper variableMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVariableMockMvc;

    private Variable variable;

    private Variable insertedVariable;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Variable createEntity() {
        return new Variable()
            .variableSourceFormId(DEFAULT_VARIABLE_SOURCE_FORM_ID)
            .variableTargetFormId(DEFAULT_VARIABLE_TARGET_FORM_ID)
            .formula(DEFAULT_FORMULA);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Variable createUpdatedEntity() {
        return new Variable()
            .variableSourceFormId(UPDATED_VARIABLE_SOURCE_FORM_ID)
            .variableTargetFormId(UPDATED_VARIABLE_TARGET_FORM_ID)
            .formula(UPDATED_FORMULA);
    }

    @BeforeEach
    public void initTest() {
        variable = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedVariable != null) {
            variableRepository.delete(insertedVariable);
            insertedVariable = null;
        }
    }

    @Test
    @Transactional
    void createVariable() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Variable
        VariableDTO variableDTO = variableMapper.toDto(variable);
        var returnedVariableDTO = om.readValue(
            restVariableMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(variableDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            VariableDTO.class
        );

        // Validate the Variable in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedVariable = variableMapper.toEntity(returnedVariableDTO);
        assertVariableUpdatableFieldsEquals(returnedVariable, getPersistedVariable(returnedVariable));

        insertedVariable = returnedVariable;
    }

    @Test
    @Transactional
    void createVariableWithExistingId() throws Exception {
        // Create the Variable with an existing ID
        variable.setId(1L);
        VariableDTO variableDTO = variableMapper.toDto(variable);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVariableMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(variableDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Variable in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllVariables() throws Exception {
        // Initialize the database
        insertedVariable = variableRepository.saveAndFlush(variable);

        // Get all the variableList
        restVariableMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(variable.getId().intValue())))
            .andExpect(jsonPath("$.[*].variableSourceFormId").value(hasItem(DEFAULT_VARIABLE_SOURCE_FORM_ID)))
            .andExpect(jsonPath("$.[*].variableTargetFormId").value(hasItem(DEFAULT_VARIABLE_TARGET_FORM_ID)))
            .andExpect(jsonPath("$.[*].formula").value(hasItem(DEFAULT_FORMULA)));
    }

    @Test
    @Transactional
    void getVariable() throws Exception {
        // Initialize the database
        insertedVariable = variableRepository.saveAndFlush(variable);

        // Get the variable
        restVariableMockMvc
            .perform(get(ENTITY_API_URL_ID, variable.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(variable.getId().intValue()))
            .andExpect(jsonPath("$.variableSourceFormId").value(DEFAULT_VARIABLE_SOURCE_FORM_ID))
            .andExpect(jsonPath("$.variableTargetFormId").value(DEFAULT_VARIABLE_TARGET_FORM_ID))
            .andExpect(jsonPath("$.formula").value(DEFAULT_FORMULA));
    }

    @Test
    @Transactional
    void getNonExistingVariable() throws Exception {
        // Get the variable
        restVariableMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingVariable() throws Exception {
        // Initialize the database
        insertedVariable = variableRepository.saveAndFlush(variable);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the variable
        Variable updatedVariable = variableRepository.findById(variable.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedVariable are not directly saved in db
        em.detach(updatedVariable);
        updatedVariable
            .variableSourceFormId(UPDATED_VARIABLE_SOURCE_FORM_ID)
            .variableTargetFormId(UPDATED_VARIABLE_TARGET_FORM_ID)
            .formula(UPDATED_FORMULA);
        VariableDTO variableDTO = variableMapper.toDto(updatedVariable);

        restVariableMockMvc
            .perform(
                put(ENTITY_API_URL_ID, variableDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(variableDTO))
            )
            .andExpect(status().isOk());

        // Validate the Variable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedVariableToMatchAllProperties(updatedVariable);
    }

    @Test
    @Transactional
    void putNonExistingVariable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        variable.setId(longCount.incrementAndGet());

        // Create the Variable
        VariableDTO variableDTO = variableMapper.toDto(variable);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVariableMockMvc
            .perform(
                put(ENTITY_API_URL_ID, variableDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(variableDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Variable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVariable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        variable.setId(longCount.incrementAndGet());

        // Create the Variable
        VariableDTO variableDTO = variableMapper.toDto(variable);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVariableMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(variableDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Variable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVariable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        variable.setId(longCount.incrementAndGet());

        // Create the Variable
        VariableDTO variableDTO = variableMapper.toDto(variable);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVariableMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(variableDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Variable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVariableWithPatch() throws Exception {
        // Initialize the database
        insertedVariable = variableRepository.saveAndFlush(variable);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the variable using partial update
        Variable partialUpdatedVariable = new Variable();
        partialUpdatedVariable.setId(variable.getId());

        partialUpdatedVariable
            .variableSourceFormId(UPDATED_VARIABLE_SOURCE_FORM_ID)
            .variableTargetFormId(UPDATED_VARIABLE_TARGET_FORM_ID)
            .formula(UPDATED_FORMULA);

        restVariableMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVariable.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVariable))
            )
            .andExpect(status().isOk());

        // Validate the Variable in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVariableUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedVariable, variable), getPersistedVariable(variable));
    }

    @Test
    @Transactional
    void fullUpdateVariableWithPatch() throws Exception {
        // Initialize the database
        insertedVariable = variableRepository.saveAndFlush(variable);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the variable using partial update
        Variable partialUpdatedVariable = new Variable();
        partialUpdatedVariable.setId(variable.getId());

        partialUpdatedVariable
            .variableSourceFormId(UPDATED_VARIABLE_SOURCE_FORM_ID)
            .variableTargetFormId(UPDATED_VARIABLE_TARGET_FORM_ID)
            .formula(UPDATED_FORMULA);

        restVariableMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVariable.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVariable))
            )
            .andExpect(status().isOk());

        // Validate the Variable in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVariableUpdatableFieldsEquals(partialUpdatedVariable, getPersistedVariable(partialUpdatedVariable));
    }

    @Test
    @Transactional
    void patchNonExistingVariable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        variable.setId(longCount.incrementAndGet());

        // Create the Variable
        VariableDTO variableDTO = variableMapper.toDto(variable);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVariableMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, variableDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(variableDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Variable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVariable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        variable.setId(longCount.incrementAndGet());

        // Create the Variable
        VariableDTO variableDTO = variableMapper.toDto(variable);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVariableMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(variableDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Variable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVariable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        variable.setId(longCount.incrementAndGet());

        // Create the Variable
        VariableDTO variableDTO = variableMapper.toDto(variable);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVariableMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(variableDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Variable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVariable() throws Exception {
        // Initialize the database
        insertedVariable = variableRepository.saveAndFlush(variable);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the variable
        restVariableMockMvc
            .perform(delete(ENTITY_API_URL_ID, variable.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return variableRepository.count();
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

    protected Variable getPersistedVariable(Variable variable) {
        return variableRepository.findById(variable.getId()).orElseThrow();
    }

    protected void assertPersistedVariableToMatchAllProperties(Variable expectedVariable) {
        assertVariableAllPropertiesEquals(expectedVariable, getPersistedVariable(expectedVariable));
    }

    protected void assertPersistedVariableToMatchUpdatableProperties(Variable expectedVariable) {
        assertVariableAllUpdatablePropertiesEquals(expectedVariable, getPersistedVariable(expectedVariable));
    }
}
