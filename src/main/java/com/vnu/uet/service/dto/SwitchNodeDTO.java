package com.vnu.uet.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.vnu.uet.domain.SwitchNode} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class SwitchNodeDTO implements Serializable {

    private Long id;

    @Size(max = 100)
    private String formId;

    @Size(max = 100)
    private String variableId;

    private FlowDTO flow;

    private RelateNodeDTO relateNode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }

    public FlowDTO getFlow() {
        return flow;
    }

    public void setFlow(FlowDTO flow) {
        this.flow = flow;
    }

    public RelateNodeDTO getRelateNode() {
        return relateNode;
    }

    public void setRelateNode(RelateNodeDTO relateNode) {
        this.relateNode = relateNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SwitchNodeDTO)) {
            return false;
        }

        SwitchNodeDTO switchNodeDTO = (SwitchNodeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, switchNodeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SwitchNodeDTO{" +
            "id=" + getId() +
            ", formId='" + getFormId() + "'" +
            ", variableId='" + getVariableId() + "'" +
            ", flow=" + getFlow() +
            ", relateNode=" + getRelateNode() +
            "}";
    }
}
