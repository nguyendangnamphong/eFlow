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
 * A Node.
 */
@Entity
@Table(name = "node")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Node implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Size(max = 500)
    @Column(name = "node_type", length = 500)
    private String nodeType;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "node")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "switchNodes", "relateDemands", "flow", "node" }, allowSetters = true)
    private Set<RelateNode> relateNodes = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "node")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "node" }, allowSetters = true)
    private Set<Performer> performers = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "node")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "variables", "node" }, allowSetters = true)
    private Set<MapForm> mapForms = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "nodes", "relateNodes", "switchNodes" }, allowSetters = true)
    private Flow flow;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Node id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNodeType() {
        return this.nodeType;
    }

    public Node nodeType(String nodeType) {
        this.setNodeType(nodeType);
        return this;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public Set<RelateNode> getRelateNodes() {
        return this.relateNodes;
    }

    public void setRelateNodes(Set<RelateNode> relateNodes) {
        if (this.relateNodes != null) {
            this.relateNodes.forEach(i -> i.setNode(null));
        }
        if (relateNodes != null) {
            relateNodes.forEach(i -> i.setNode(this));
        }
        this.relateNodes = relateNodes;
    }

    public Node relateNodes(Set<RelateNode> relateNodes) {
        this.setRelateNodes(relateNodes);
        return this;
    }

    public Node addRelateNode(RelateNode relateNode) {
        this.relateNodes.add(relateNode);
        relateNode.setNode(this);
        return this;
    }

    public Node removeRelateNode(RelateNode relateNode) {
        this.relateNodes.remove(relateNode);
        relateNode.setNode(null);
        return this;
    }

    public Set<Performer> getPerformers() {
        return this.performers;
    }

    public void setPerformers(Set<Performer> performers) {
        if (this.performers != null) {
            this.performers.forEach(i -> i.setNode(null));
        }
        if (performers != null) {
            performers.forEach(i -> i.setNode(this));
        }
        this.performers = performers;
    }

    public Node performers(Set<Performer> performers) {
        this.setPerformers(performers);
        return this;
    }

    public Node addPerformer(Performer performer) {
        this.performers.add(performer);
        performer.setNode(this);
        return this;
    }

    public Node removePerformer(Performer performer) {
        this.performers.remove(performer);
        performer.setNode(null);
        return this;
    }

    public Set<MapForm> getMapForms() {
        return this.mapForms;
    }

    public void setMapForms(Set<MapForm> mapForms) {
        if (this.mapForms != null) {
            this.mapForms.forEach(i -> i.setNode(null));
        }
        if (mapForms != null) {
            mapForms.forEach(i -> i.setNode(this));
        }
        this.mapForms = mapForms;
    }

    public Node mapForms(Set<MapForm> mapForms) {
        this.setMapForms(mapForms);
        return this;
    }

    public Node addMapForm(MapForm mapForm) {
        this.mapForms.add(mapForm);
        mapForm.setNode(this);
        return this;
    }

    public Node removeMapForm(MapForm mapForm) {
        this.mapForms.remove(mapForm);
        mapForm.setNode(null);
        return this;
    }

    public Flow getFlow() {
        return this.flow;
    }

    public void setFlow(Flow flow) {
        this.flow = flow;
    }

    public Node flow(Flow flow) {
        this.setFlow(flow);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Node)) {
            return false;
        }
        return getId() != null && getId().equals(((Node) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Node{" +
            "id=" + getId() +
            ", nodeType='" + getNodeType() + "'" +
            "}";
    }
}
