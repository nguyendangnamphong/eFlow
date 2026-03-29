package com.vnu.uet.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.vnu.uet.domain.Node} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NodeDTO implements Serializable {

    private Long id;

    @Size(max = 500)
    private String nodeType;

    private FlowDTO flow;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public FlowDTO getFlow() {
        return flow;
    }

    public void setFlow(FlowDTO flow) {
        this.flow = flow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NodeDTO)) {
            return false;
        }

        NodeDTO nodeDTO = (NodeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, nodeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NodeDTO{" +
            "id=" + getId() +
            ", nodeType='" + getNodeType() + "'" +
            ", flow=" + getFlow() +
            "}";
    }
}
