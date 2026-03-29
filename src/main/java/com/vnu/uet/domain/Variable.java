package com.vnu.uet.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Variable.
 */
@Entity
@Table(name = "variable")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Variable implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 100)
    @Column(name = "variable_source_form_id", length = 100)
    private String variableSourceFormId;

    @Size(max = 100)
    @Column(name = "variable_target_form_id", length = 100)
    private String variableTargetFormId;

    @Size(max = 500)
    @Column(name = "formula", length = 500)
    private String formula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "variables", "node" }, allowSetters = true)
    private MapForm mapForm;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Variable id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVariableSourceFormId() {
        return this.variableSourceFormId;
    }

    public Variable variableSourceFormId(String variableSourceFormId) {
        this.setVariableSourceFormId(variableSourceFormId);
        return this;
    }

    public void setVariableSourceFormId(String variableSourceFormId) {
        this.variableSourceFormId = variableSourceFormId;
    }

    public String getVariableTargetFormId() {
        return this.variableTargetFormId;
    }

    public Variable variableTargetFormId(String variableTargetFormId) {
        this.setVariableTargetFormId(variableTargetFormId);
        return this;
    }

    public void setVariableTargetFormId(String variableTargetFormId) {
        this.variableTargetFormId = variableTargetFormId;
    }

    public String getFormula() {
        return this.formula;
    }

    public Variable formula(String formula) {
        this.setFormula(formula);
        return this;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public MapForm getMapForm() {
        return this.mapForm;
    }

    public void setMapForm(MapForm mapForm) {
        this.mapForm = mapForm;
    }

    public Variable mapForm(MapForm mapForm) {
        this.setMapForm(mapForm);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Variable)) {
            return false;
        }
        return getId() != null && getId().equals(((Variable) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Variable{" +
            "id=" + getId() +
            ", variableSourceFormId='" + getVariableSourceFormId() + "'" +
            ", variableTargetFormId='" + getVariableTargetFormId() + "'" +
            ", formula='" + getFormula() + "'" +
            "}";
    }
}
