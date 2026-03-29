package com.vnu.uet.service.mapper;

import static com.vnu.uet.domain.VariableAsserts.*;
import static com.vnu.uet.domain.VariableTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VariableMapperTest {

    private VariableMapper variableMapper;

    @BeforeEach
    void setUp() {
        variableMapper = new VariableMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getVariableSample1();
        var actual = variableMapper.toEntity(variableMapper.toDto(expected));
        assertVariableAllPropertiesEquals(expected, actual);
    }
}
