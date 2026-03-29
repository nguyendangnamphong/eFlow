package com.vnu.uet.service.mapper;

import static com.vnu.uet.domain.RelateDemandAsserts.*;
import static com.vnu.uet.domain.RelateDemandTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RelateDemandMapperTest {

    private RelateDemandMapper relateDemandMapper;

    @BeforeEach
    void setUp() {
        relateDemandMapper = new RelateDemandMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRelateDemandSample1();
        var actual = relateDemandMapper.toEntity(relateDemandMapper.toDto(expected));
        assertRelateDemandAllPropertiesEquals(expected, actual);
    }
}
