package com.vnu.uet.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A RelateNode.
 */
@Entity
@Table(name = "relate_node")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RelateNode implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "has_demand")
    private Boolean hasDemand;

    @Column(name = "child_node_id")
    private Long childNodeId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "relateNode")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "relateDemands", "flow", "relateNode" }, allowSetters = true)
    private Set<SwitchNode> switchNodes = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "relateNode")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "relateNode", "switchNode" }, allowSetters = true)
    private Set<RelateDemand> relateDemands = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "nodes", "relateNodes", "switchNodes" }, allowSetters = true)
    private Flow flow;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "relateNodes", "performers", "mapForms", "flow" }, allowSetters = true)
    private Node node;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public RelateNode id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getHasDemand() {
        return this.hasDemand;
    }

    public RelateNode hasDemand(Boolean hasDemand) {
        this.setHasDemand(hasDemand);
        return this;
    }

    public void setHasDemand(Boolean hasDemand) {
        this.hasDemand = hasDemand;
    }

    public Long getChildNodeId() {
        return this.childNodeId;
    }

    public RelateNode childNodeId(Long childNodeId) {
        this.setChildNodeId(childNodeId);
        return this;
    }

    public void setChildNodeId(Long childNodeId) {
        this.childNodeId = childNodeId;
    }

    public Set<SwitchNode> getSwitchNodes() {
        return this.switchNodes;
    }

    public void setSwitchNodes(Set<SwitchNode> switchNodes) {
        if (this.switchNodes != null) {
            this.switchNodes.forEach(i -> i.setRelateNode(null));
        }
        if (switchNodes != null) {
            switchNodes.forEach(i -> i.setRelateNode(this));
        }
        this.switchNodes = switchNodes;
    }

    public RelateNode switchNodes(Set<SwitchNode> switchNodes) {
        this.setSwitchNodes(switchNodes);
        return this;
    }

    public RelateNode addSwitchNode(SwitchNode switchNode) {
        this.switchNodes.add(switchNode);
        switchNode.setRelateNode(this);
        return this;
    }

    public RelateNode removeSwitchNode(SwitchNode switchNode) {
        this.switchNodes.remove(switchNode);
        switchNode.setRelateNode(null);
        return this;
    }

    public Set<RelateDemand> getRelateDemands() {
        return this.relateDemands;
    }

    public void setRelateDemands(Set<RelateDemand> relateDemands) {
        if (this.relateDemands != null) {
            this.relateDemands.forEach(i -> i.setRelateNode(null));
        }
        if (relateDemands != null) {
            relateDemands.forEach(i -> i.setRelateNode(this));
        }
        this.relateDemands = relateDemands;
    }

    public RelateNode relateDemands(Set<RelateDemand> relateDemands) {
        this.setRelateDemands(relateDemands);
        return this;
    }

    public RelateNode addRelateDemand(RelateDemand relateDemand) {
        this.relateDemands.add(relateDemand);
        relateDemand.setRelateNode(this);
        return this;
    }

    public RelateNode removeRelateDemand(RelateDemand relateDemand) {
        this.relateDemands.remove(relateDemand);
        relateDemand.setRelateNode(null);
        return this;
    }

    public Flow getFlow() {
        return this.flow;
    }

    public void setFlow(Flow flow) {
        this.flow = flow;
    }

    public RelateNode flow(Flow flow) {
        this.setFlow(flow);
        return this;
    }

    public Node getNode() {
        return this.node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public RelateNode node(Node node) {
        this.setNode(node);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RelateNode)) {
            return false;
        }
        return getId() != null && getId().equals(((RelateNode) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RelateNode{" +
            "id=" + getId() +
            ", hasDemand='" + getHasDemand() + "'" +
            ", childNodeId=" + getChildNodeId() +
            "}";
    }
}
