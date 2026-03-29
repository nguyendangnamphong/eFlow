package com.vnu.uet.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.vnu.uet.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MapFormDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MapFormDTO.class);
        MapFormDTO mapFormDTO1 = new MapFormDTO();
        mapFormDTO1.setId(1L);
        MapFormDTO mapFormDTO2 = new MapFormDTO();
        assertThat(mapFormDTO1).isNotEqualTo(mapFormDTO2);
        mapFormDTO2.setId(mapFormDTO1.getId());
        assertThat(mapFormDTO1).isEqualTo(mapFormDTO2);
        mapFormDTO2.setId(2L);
        assertThat(mapFormDTO1).isNotEqualTo(mapFormDTO2);
        mapFormDTO1.setId(null);
        assertThat(mapFormDTO1).isNotEqualTo(mapFormDTO2);
    }
}
