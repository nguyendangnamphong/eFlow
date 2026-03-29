package com.vnu.uet.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.vnu.uet.domain.RelateDemand} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RelateDemandDTO implements Serializable {

    private Long id;

    @Size(max = 500)
    private String relateDemand;

    private RelateNodeDTO relateNode;

    private SwitchNodeDTO switchNode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRelateDemand() {
        return relateDemand;
    }

    public void setRelateDemand(String relateDemand) {
        this.relateDemand = relateDemand;
    }

    public RelateNodeDTO getRelateNode() {
        return relateNode;
    }

    public void setRelateNode(RelateNodeDTO relateNode) {
        this.relateNode = relateNode;
    }

    public SwitchNodeDTO getSwitchNode() {
        return switchNode;
    }

    public void setSwitchNode(SwitchNodeDTO switchNode) {
        this.switchNode = switchNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RelateDemandDTO)) {
            return false;
        }

        RelateDemandDTO relateDemandDTO = (RelateDemandDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, relateDemandDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RelateDemandDTO{" +
            "id=" + getId() +
            ", relateDemand='" + getRelateDemand() + "'" +
            ", relateNode=" + getRelateNode() +
            ", switchNode=" + getSwitchNode() +
            "}";
    }
}
