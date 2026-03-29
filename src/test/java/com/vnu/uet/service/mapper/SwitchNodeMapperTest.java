package com.vnu.uet.service.mapper;

import static com.vnu.uet.domain.SwitchNodeAsserts.*;
import static com.vnu.uet.domain.SwitchNodeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SwitchNodeMapperTest {

    private SwitchNodeMapper switchNodeMapper;

    @BeforeEach
    void setUp() {
        switchNodeMapper = new SwitchNodeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSwitchNodeSample1();
        var actual = switchNodeMapper.toEntity(switchNodeMapper.toDto(expected));
        assertSwitchNodeAllPropertiesEquals(expected, actual);
    }
}
