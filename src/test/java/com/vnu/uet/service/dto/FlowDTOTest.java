package com.vnu.uet.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.vnu.uet.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FlowDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FlowDTO.class);
        FlowDTO flowDTO1 = new FlowDTO();
        flowDTO1.setId(1L);
        FlowDTO flowDTO2 = new FlowDTO();
        assertThat(flowDTO1).isNotEqualTo(flowDTO2);
        flowDTO2.setId(flowDTO1.getId());
        assertThat(flowDTO1).isEqualTo(flowDTO2);
        flowDTO2.setId(2L);
        assertThat(flowDTO1).isNotEqualTo(flowDTO2);
        flowDTO1.setId(null);
        assertThat(flowDTO1).isNotEqualTo(flowDTO2);
    }
}
