package com.vnu.uet.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.vnu.uet.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PerformerDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PerformerDTO.class);
        PerformerDTO performerDTO1 = new PerformerDTO();
        performerDTO1.setId(1L);
        PerformerDTO performerDTO2 = new PerformerDTO();
        assertThat(performerDTO1).isNotEqualTo(performerDTO2);
        performerDTO2.setId(performerDTO1.getId());
        assertThat(performerDTO1).isEqualTo(performerDTO2);
        performerDTO2.setId(2L);
        assertThat(performerDTO1).isNotEqualTo(performerDTO2);
        performerDTO1.setId(null);
        assertThat(performerDTO1).isNotEqualTo(performerDTO2);
    }
}
