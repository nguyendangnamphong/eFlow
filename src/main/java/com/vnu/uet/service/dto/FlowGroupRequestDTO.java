package com.vnu.uet.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FlowGroupRequestDTO {

    @JsonProperty("flow_group_name")
    private String flowGroupName;

    public String getFlowGroupName() {
        return flowGroupName;
    }

    public void setFlowGroupName(String flowGroupName) {
        this.flowGroupName = flowGroupName;
    }
}

