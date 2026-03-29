package com.vnu.uet.service.mapper;

import static com.vnu.uet.domain.FlowAsserts.*;
import static com.vnu.uet.domain.FlowTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FlowMapperTest {

    private FlowMapper flowMapper;

    @BeforeEach
    void setUp() {
        flowMapper = new FlowMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFlowSample1();
        var actual = flowMapper.toEntity(flowMapper.toDto(expected));
        assertFlowAllPropertiesEquals(expected, actual);
    }
}
