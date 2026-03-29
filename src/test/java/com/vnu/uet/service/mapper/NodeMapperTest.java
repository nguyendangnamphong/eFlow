package com.vnu.uet.service.mapper;

import static com.vnu.uet.domain.NodeAsserts.*;
import static com.vnu.uet.domain.NodeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NodeMapperTest {

    private NodeMapper nodeMapper;

    @BeforeEach
    void setUp() {
        nodeMapper = new NodeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getNodeSample1();
        var actual = nodeMapper.toEntity(nodeMapper.toDto(expected));
        assertNodeAllPropertiesEquals(expected, actual);
    }
}
