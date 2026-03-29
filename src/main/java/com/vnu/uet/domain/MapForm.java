package com.vnu.uet.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A MapForm.
 */
@Entity
@Table(name = "map_form")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MapForm implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 100)
    @Column(name = "target_form_id", length = 100)
    private String targetFormId;

    @Size(max = 100)
    @Column(name = "source_form_id", length = 100)
    private String sourceFormId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "mapForm")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "mapForm" }, allowSetters = true)
    private Set<Variable> variables = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "relateNodes", "performers", "mapForms", "flow" }, allowSetters = true)
    private Node node;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MapForm id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTargetFormId() {
        return this.targetFormId;
    }

    public MapForm targetFormId(String targetFormId) {
        this.setTargetFormId(targetFormId);
        return this;
    }

    public void setTargetFormId(String targetFormId) {
        this.targetFormId = targetFormId;
    }

    public String getSourceFormId() {
        return this.sourceFormId;
    }

    public MapForm sourceFormId(String sourceFormId) {
        this.setSourceFormId(sourceFormId);
        return this;
    }

    public void setSourceFormId(String sourceFormId) {
        this.sourceFormId = sourceFormId;
    }

    public Set<Variable> getVariables() {
        return this.variables;
    }

    public void setVariables(Set<Variable> variables) {
        if (this.variables != null) {
            this.variables.forEach(i -> i.setMapForm(null));
        }
        if (variables != null) {
            variables.forEach(i -> i.setMapForm(this));
        }
        this.variables = variables;
    }

    public MapForm variables(Set<Variable> variables) {
        this.setVariables(variables);
        return this;
    }

    public MapForm addVariable(Variable variable) {
        this.variables.add(variable);
        variable.setMapForm(this);
        return this;
    }

    public MapForm removeVariable(Variable variable) {
        this.variables.remove(variable);
        variable.setMapForm(null);
        return this;
    }

    public Node getNode() {
        return this.node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public MapForm node(Node node) {
        this.setNode(node);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MapForm)) {
            return false;
        }
        return getId() != null && getId().equals(((MapForm) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MapForm{" +
            "id=" + getId() +
            ", targetFormId='" + getTargetFormId() + "'" +
            ", sourceFormId='" + getSourceFormId() + "'" +
            "}";
    }
}
