package com.vnu.uet.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.vnu.uet.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RelateDemandDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RelateDemandDTO.class);
        RelateDemandDTO relateDemandDTO1 = new RelateDemandDTO();
        relateDemandDTO1.setId(1L);
        RelateDemandDTO relateDemandDTO2 = new RelateDemandDTO();
        assertThat(relateDemandDTO1).isNotEqualTo(relateDemandDTO2);
        relateDemandDTO2.setId(relateDemandDTO1.getId());
        assertThat(relateDemandDTO1).isEqualTo(relateDemandDTO2);
        relateDemandDTO2.setId(2L);
        assertThat(relateDemandDTO1).isNotEqualTo(relateDemandDTO2);
        relateDemandDTO1.setId(null);
        assertThat(relateDemandDTO1).isNotEqualTo(relateDemandDTO2);
    }
}
