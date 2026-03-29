package com.vnu.uet.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.vnu.uet.domain.Flow} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FlowDTO implements Serializable {

    private Long id;

    @Size(max = 100)
    private String flowName;

    @Size(max = 100)
    private String flowGroup;

    @Size(max = 100)
    private String ownerName;

    @Size(max = 100)
    private String superviserName;

    @Size(max = 500)
    private String department;

    @Size(max = 500)
    private String describe;

    @Size(max = 50)
    private String status;

    private Instant flowStartDate;

    private Instant flowEndDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getFlowGroup() {
        return flowGroup;
    }

    public void setFlowGroup(String flowGroup) {
        this.flowGroup = flowGroup;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getSuperviserName() {
        return superviserName;
    }

    public void setSuperviserName(String superviserName) {
        this.superviserName = superviserName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getFlowStartDate() {
        return flowStartDate;
    }

    public void setFlowStartDate(Instant flowStartDate) {
        this.flowStartDate = flowStartDate;
    }

    public Instant getFlowEndDate() {
        return flowEndDate;
    }

    public void setFlowEndDate(Instant flowEndDate) {
        this.flowEndDate = flowEndDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FlowDTO)) {
            return false;
        }

        FlowDTO flowDTO = (FlowDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, flowDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FlowDTO{" +
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
