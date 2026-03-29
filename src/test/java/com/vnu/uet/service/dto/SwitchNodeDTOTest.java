package com.vnu.uet.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.vnu.uet.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SwitchNodeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SwitchNodeDTO.class);
        SwitchNodeDTO switchNodeDTO1 = new SwitchNodeDTO();
        switchNodeDTO1.setId(1L);
        SwitchNodeDTO switchNodeDTO2 = new SwitchNodeDTO();
        assertThat(switchNodeDTO1).isNotEqualTo(switchNodeDTO2);
        switchNodeDTO2.setId(switchNodeDTO1.getId());
        assertThat(switchNodeDTO1).isEqualTo(switchNodeDTO2);
        switchNodeDTO2.setId(2L);
        assertThat(switchNodeDTO1).isNotEqualTo(switchNodeDTO2);
        switchNodeDTO1.setId(null);
        assertThat(switchNodeDTO1).isNotEqualTo(switchNodeDTO2);
    }
}
