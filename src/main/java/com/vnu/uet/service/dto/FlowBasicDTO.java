package com.vnu.uet.service.dto;

public class FlowBasicDTO {

    private Long flowId;
    private String flowName;

    public FlowBasicDTO() {}

    public FlowBasicDTO(Long flowId, String flowName) {
        this.flowId = flowId;
        this.flowName = flowName;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }
}

