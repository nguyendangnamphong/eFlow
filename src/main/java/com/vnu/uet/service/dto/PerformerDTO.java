package com.vnu.uet.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.vnu.uet.domain.Performer} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PerformerDTO implements Serializable {

    private Long id;

    @Size(max = 100)
    private String userId;

    private Long orderExecution;

    private NodeDTO node;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getOrderExecution() {
        return orderExecution;
    }

    public void setOrderExecution(Long orderExecution) {
        this.orderExecution = orderExecution;
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
        if (!(o instanceof PerformerDTO)) {
            return false;
        }

        PerformerDTO performerDTO = (PerformerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, performerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PerformerDTO{" +
            "id=" + getId() +
            ", userId='" + getUserId() + "'" +
            ", orderExecution=" + getOrderExecution() +
            ", node=" + getNode() +
            "}";
    }
}
