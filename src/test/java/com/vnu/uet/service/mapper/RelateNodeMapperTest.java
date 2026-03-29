package com.vnu.uet.service.mapper;

import static com.vnu.uet.domain.RelateNodeAsserts.*;
import static com.vnu.uet.domain.RelateNodeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RelateNodeMapperTest {

    private RelateNodeMapper relateNodeMapper;

    @BeforeEach
    void setUp() {
        relateNodeMapper = new RelateNodeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getRelateNodeSample1();
        var actual = relateNodeMapper.toEntity(relateNodeMapper.toDto(expected));
        assertRelateNodeAllPropertiesEquals(expected, actual);
    }
}
