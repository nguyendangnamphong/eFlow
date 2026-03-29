package com.vnu.uet.service.mapper;

import static com.vnu.uet.domain.PerformerAsserts.*;
import static com.vnu.uet.domain.PerformerTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PerformerMapperTest {

    private PerformerMapper performerMapper;

    @BeforeEach
    void setUp() {
        performerMapper = new PerformerMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPerformerSample1();
        var actual = performerMapper.toEntity(performerMapper.toDto(expected));
        assertPerformerAllPropertiesEquals(expected, actual);
    }
}
