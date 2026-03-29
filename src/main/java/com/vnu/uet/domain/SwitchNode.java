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
 * A SwitchNode.
 */
@Entity
@Table(name = "switch_node")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SwitchNode implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 100)
    @Column(name = "form_id", length = 100)
    private String formId;

    @Size(max = 100)
    @Column(name = "variable_id", length = 100)
    private String variableId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "switchNode")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "relateNode", "switchNode" }, allowSetters = true)
    private Set<RelateDemand> relateDemands = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "nodes", "relateNodes", "switchNodes" }, allowSetters = true)
    private Flow flow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "switchNodes", "relateDemands", "flow", "node" }, allowSetters = true)
    private RelateNode relateNode;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public SwitchNode id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFormId() {
        return this.formId;
    }

    public SwitchNode formId(String formId) {
        this.setFormId(formId);
        return this;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getVariableId() {
        return this.variableId;
    }

    public SwitchNode variableId(String variableId) {
        this.setVariableId(variableId);
        return this;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }

    public Set<RelateDemand> getRelateDemands() {
        return this.relateDemands;
    }

    public void setRelateDemands(Set<RelateDemand> relateDemands) {
        if (this.relateDemands != null) {
            this.relateDemands.forEach(i -> i.setSwitchNode(null));
        }
        if (relateDemands != null) {
            relateDemands.forEach(i -> i.setSwitchNode(this));
        }
        this.relateDemands = relateDemands;
    }

    public SwitchNode relateDemands(Set<RelateDemand> relateDemands) {
        this.setRelateDemands(relateDemands);
        return this;
    }

    public SwitchNode addRelateDemand(RelateDemand relateDemand) {
        this.relateDemands.add(relateDemand);
        relateDemand.setSwitchNode(this);
        return this;
    }

    public SwitchNode removeRelateDemand(RelateDemand relateDemand) {
        this.relateDemands.remove(relateDemand);
        relateDemand.setSwitchNode(null);
        return this;
    }

    public Flow getFlow() {
        return this.flow;
    }

    public void setFlow(Flow flow) {
        this.flow = flow;
    }

    public SwitchNode flow(Flow flow) {
        this.setFlow(flow);
        return this;
    }

    public RelateNode getRelateNode() {
        return this.relateNode;
    }

    public void setRelateNode(RelateNode relateNode) {
        this.relateNode = relateNode;
    }

    public SwitchNode relateNode(RelateNode relateNode) {
        this.setRelateNode(relateNode);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SwitchNode)) {
            return false;
        }
        return getId() != null && getId().equals(((SwitchNode) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SwitchNode{" +
            "id=" + getId() +
            ", formId='" + getFormId() + "'" +
            ", variableId='" + getVariableId() + "'" +
            "}";
    }
}
