package com.vnu.uet.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Flow.
 */
@Entity
@Table(name = "flow")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Flow implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 100)
    @Column(name = "flow_name", length = 100)
    private String flowName;

    @Size(max = 100)
    @Column(name = "flow_group", length = 100)
    private String flowGroup;

    @Size(max = 100)
    @Column(name = "owner_name", length = 100)
    private String ownerName;

    @Size(max = 100)
    @Column(name = "superviser_name", length = 100)
    private String superviserName;

    @Size(max = 500)
    @Column(name = "department", length = 500)
    private String department;

    @Size(max = 500)
    @Column(name = "jhi_describe", length = 500)
    private String describe;

    @Size(max = 50)
    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "flow_start_date")
    private Instant flowStartDate;

    @Column(name = "flow_end_date")
    private Instant flowEndDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "flow")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "relateNodes", "performers", "mapForms", "flow" }, allowSetters = true)
    private Set<Node> nodes = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "flow")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "switchNodes", "relateDemands", "flow", "node" }, allowSetters = true)
    private Set<RelateNode> relateNodes = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "flow")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "relateDemands", "flow", "relateNode" }, allowSetters = true)
    private Set<SwitchNode> switchNodes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Flow id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFlowName() {
        return this.flowName;
    }

    public Flow flowName(String flowName) {
        this.setFlowName(flowName);
        return this;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getFlowGroup() {
        return this.flowGroup;
    }

    public Flow flowGroup(String flowGroup) {
        this.setFlowGroup(flowGroup);
        return this;
    }

    public void setFlowGroup(String flowGroup) {
        this.flowGroup = flowGroup;
    }

    public String getOwnerName() {
        return this.ownerName;
    }

    public Flow ownerName(String ownerName) {
        this.setOwnerName(ownerName);
        return this;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getSuperviserName() {
        return this.superviserName;
    }

    public Flow superviserName(String superviserName) {
        this.setSuperviserName(superviserName);
        return this;
    }

    public void setSuperviserName(String superviserName) {
        this.superviserName = superviserName;
    }

    public String getDepartment() {
        return this.department;
    }

    public Flow department(String department) {
        this.setDepartment(department);
        return this;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDescribe() {
        return this.describe;
    }

    public Flow describe(String describe) {
        this.setDescribe(describe);
        return this;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getStatus() {
        return this.status;
    }

    public Flow status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getFlowStartDate() {
        return this.flowStartDate;
    }

    public Flow flowStartDate(Instant flowStartDate) {
        this.setFlowStartDate(flowStartDate);
        return this;
    }

    public void setFlowStartDate(Instant flowStartDate) {
        this.flowStartDate = flowStartDate;
    }

    public Instant getFlowEndDate() {
        return this.flowEndDate;
    }

    public Flow flowEndDate(Instant flowEndDate) {
        this.setFlowEndDate(flowEndDate);
        return this;
    }

    public void setFlowEndDate(Instant flowEndDate) {
        this.flowEndDate = flowEndDate;
    }

    public Set<Node> getNodes() {
        return this.nodes;
    }

    public void setNodes(Set<Node> nodes) {
        if (this.nodes != null) {
            this.nodes.forEach(i -> i.setFlow(null));
        }
        if (nodes != null) {
            nodes.forEach(i -> i.setFlow(this));
        }
        this.nodes = nodes;
    }

    public Flow nodes(Set<Node> nodes) {
        this.setNodes(nodes);
        return this;
    }

    public Flow addNode(Node node) {
        this.nodes.add(node);
        node.setFlow(this);
        return this;
    }

    public Flow removeNode(Node node) {
        this.nodes.remove(node);
        node.setFlow(null);
        return this;
    }

    public Set<RelateNode> getRelateNodes() {
        return this.relateNodes;
    }

    public void setRelateNodes(Set<RelateNode> relateNodes) {
        if (this.relateNodes != null) {
            this.relateNodes.forEach(i -> i.setFlow(null));
        }
        if (relateNodes != null) {
            relateNodes.forEach(i -> i.setFlow(this));
        }
        this.relateNodes = relateNodes;
    }

    public Flow relateNodes(Set<RelateNode> relateNodes) {
        this.setRelateNodes(relateNodes);
        return this;
    }

    public Flow addRelateNode(RelateNode relateNode) {
        this.relateNodes.add(relateNode);
        relateNode.setFlow(this);
        return this;
    }

    public Flow removeRelateNode(RelateNode relateNode) {
        this.relateNodes.remove(relateNode);
        relateNode.setFlow(null);
        return this;
    }

    public Set<SwitchNode> getSwitchNodes() {
        return this.switchNodes;
    }

    public void setSwitchNodes(Set<SwitchNode> switchNodes) {
        if (this.switchNodes != null) {
            this.switchNodes.forEach(i -> i.setFlow(null));
        }
        if (switchNodes != null) {
            switchNodes.forEach(i -> i.setFlow(this));
        }
        this.switchNodes = switchNodes;
    }

    public Flow switchNodes(Set<SwitchNode> switchNodes) {
        this.setSwitchNodes(switchNodes);
        return this;
    }

    public Flow addSwitchNode(SwitchNode switchNode) {
        this.switchNodes.add(switchNode);
        switchNode.setFlow(this);
        return this;
    }

    public Flow removeSwitchNode(SwitchNode switchNode) {
        this.switchNodes.remove(switchNode);
        switchNode.setFlow(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Flow)) {
            return false;
        }
        return getId() != null && getId().equals(((Flow) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Flow{" +
            "id=" + getId() +
            ", flowName='" + getFlowName() + "'" +
            ", flowGroup='" + getFlowGroup() + "'" +
            ", ownerName='" + getOwnerName() + "'" +
            ", superviserName='" + getSuperviserName() + "'" +
            ", department='" + getDepartment() + "'" +
            ", describe='" + getDescribe() + "'" +
            ", status='" + getStatus() + "'" +
            ", flowStartDate='" + getFlowStartDate() + "'" +
            ", flowEndDate='" + getFlowEndDate() + "'" +
            "}";
    }
}
