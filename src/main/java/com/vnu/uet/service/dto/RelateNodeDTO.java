package com.vnu.uet.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.vnu.uet.domain.RelateNode} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RelateNodeDTO implements Serializable {

    private Long id;

    private Boolean hasDemand;

    private Long childNodeId;

    private FlowDTO flow;

    private NodeDTO node;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getHasDemand() {
        return hasDemand;
    }

    public void setHasDemand(Boolean hasDemand) {
        this.hasDemand = hasDemand;
    }

    public Long getChildNodeId() {
        return childNodeId;
    }

    public void setChildNodeId(Long childNodeId) {
        this.childNodeId = childNodeId;
    }

    public FlowDTO getFlow() {
        return flow;
    }

    public void setFlow(FlowDTO flow) {
        this.flow = flow;
    }

    public NodeDTO getNode() {
        return node;
    }

    public void setNode(NodeDTO node) {
        this.node = node;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RelateNodeDTO)) {
            return false;
        }

        RelateNodeDTO relateNodeDTO = (RelateNodeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, relateNodeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RelateNodeDTO{" +
            "id=" + getId() +
            ", hasDemand='" + getHasDemand() + "'" +
            ", childNodeId=" + getChildNodeId() +
            ", flow=" + getFlow() +
            ", node=" + getNode() +
            "}";
    }
}
