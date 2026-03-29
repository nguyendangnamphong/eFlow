package com.vnu.uet.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.vnu.uet.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RelateNodeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RelateNodeDTO.class);
        RelateNodeDTO relateNodeDTO1 = new RelateNodeDTO();
        relateNodeDTO1.setId(1L);
        RelateNodeDTO relateNodeDTO2 = new RelateNodeDTO();
        assertThat(relateNodeDTO1).isNotEqualTo(relateNodeDTO2);
        relateNodeDTO2.setId(relateNodeDTO1.getId());
        assertThat(relateNodeDTO1).isEqualTo(relateNodeDTO2);
        relateNodeDTO2.setId(2L);
        assertThat(relateNodeDTO1).isNotEqualTo(relateNodeDTO2);
        relateNodeDTO1.setId(null);
        assertThat(relateNodeDTO1).isNotEqualTo(relateNodeDTO2);
    }
}
