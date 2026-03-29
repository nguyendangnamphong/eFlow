package com.vnu.uet.service.mapper;

import static com.vnu.uet.domain.MapFormAsserts.*;
import static com.vnu.uet.domain.MapFormTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MapFormMapperTest {

    private MapFormMapper mapFormMapper;

    @BeforeEach
    void setUp() {
        mapFormMapper = new MapFormMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMapFormSample1();
        var actual = mapFormMapper.toEntity(mapFormMapper.toDto(expected));
        assertMapFormAllPropertiesEquals(expected, actual);
    }
}
