package com.vnu.uet.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.vnu.uet.domain.Variable} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VariableDTO implements Serializable {

    private Long id;

    @Size(max = 100)
    private String variableSourceFormId;

    @Size(max = 100)
    private String variableTargetFormId;

    @Size(max = 500)
    private String formula;

    private MapFormDTO mapForm;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVariableSourceFormId() {
        return variableSourceFormId;
    }

    public void setVariableSourceFormId(String variableSourceFormId) {
        this.variableSourceFormId = variableSourceFormId;
    }

    public String getVariableTargetFormId() {
        return variableTargetFormId;
    }

    public void setVariableTargetFormId(String variableTargetFormId) {
        this.variableTargetFormId = variableTargetFormId;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public MapFormDTO getMapForm() {
        return mapForm;
    }

    public void setMapForm(MapFormDTO mapForm) {
        this.mapForm = mapForm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VariableDTO)) {
            return false;
        }

        VariableDTO variableDTO = (VariableDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, variableDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VariableDTO{" +
            "id=" + getId() +
            ", variableSourceFormId='" + getVariableSourceFormId() + "'" +
            ", variableTargetFormId='" + getVariableTargetFormId() + "'" +
            ", formula='" + getFormula() + "'" +
            ", mapForm=" + getMapForm() +
            "}";
    }
}
