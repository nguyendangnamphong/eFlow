package com.vnu.uet.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A RelateDemand.
 */
@Entity
@Table(name = "relate_demand")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RelateDemand implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 500)
    @Column(name = "relate_demand", length = 500)
    private String relateDemand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "switchNodes", "relateDemands", "flow", "node" }, allowSetters = true)
    private RelateNode relateNode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "relateDemands", "flow", "relateNode" }, allowSetters = true)
    private SwitchNode switchNode;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RelateDemand id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRelateDemand() {
        return this.relateDemand;
    }

    public RelateDemand relateDemand(String relateDemand) {
        this.setRelateDemand(relateDemand);
        return this;
    }

    public void setRelateDemand(String relateDemand) {
        this.relateDemand = relateDemand;
    }

    public RelateNode getRelateNode() {
        return this.relateNode;
    }

    public void setRelateNode(RelateNode relateNode) {
        this.relateNode = relateNode;
    }

    public RelateDemand relateNode(RelateNode relateNode) {
        this.setRelateNode(relateNode);
        return this;
    }

    public SwitchNode getSwitchNode() {
        return this.switchNode;
    }

    public void setSwitchNode(SwitchNode switchNode) {
        this.switchNode = switchNode;
    }

    public RelateDemand switchNode(SwitchNode switchNode) {
        this.setSwitchNode(switchNode);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RelateDemand)) {
            return false;
        }
        return getId() != null && getId().equals(((RelateDemand) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RelateDemand{" +
            "id=" + getId() +
            ", relateDemand='" + getRelateDemand() + "'" +
            "}";
    }
}
