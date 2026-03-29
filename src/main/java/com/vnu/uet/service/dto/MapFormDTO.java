package com.vnu.uet.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.vnu.uet.domain.MapForm} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MapFormDTO implements Serializable {

    private Long id;

    @Size(max = 100)
    private String targetFormId;

    @Size(max = 100)
    private String sourceFormId;

    private NodeDTO node;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTargetFormId() {
        return targetFormId;
    }

    public void setTargetFormId(String targetFormId) {
        this.targetFormId = targetFormId;
    }

    public String getSourceFormId() {
        return sourceFormId;
    }

    public void setSourceFormId(String sourceFormId) {
        this.sourceFormId = sourceFormId;
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
        if (!(o instanceof MapFormDTO)) {
            return false;
        }

        MapFormDTO mapFormDTO = (MapFormDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, mapFormDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MapFormDTO{" +
            "id=" + getId() +
            ", targetFormId='" + getTargetFormId() + "'" +
            ", sourceFormId='" + getSourceFormId() + "'" +
            ", node=" + getNode() +
            "}";
    }
}
