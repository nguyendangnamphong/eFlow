package com.vnu.uet.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.vnu.uet.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VariableDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(VariableDTO.class);
        VariableDTO variableDTO1 = new VariableDTO();
        variableDTO1.setId(1L);
        VariableDTO variableDTO2 = new VariableDTO();
        assertThat(variableDTO1).isNotEqualTo(variableDTO2);
        variableDTO2.setId(variableDTO1.getId());
        assertThat(variableDTO1).isEqualTo(variableDTO2);
        variableDTO2.setId(2L);
        assertThat(variableDTO1).isNotEqualTo(variableDTO2);
        variableDTO1.setId(null);
        assertThat(variableDTO1).isNotEqualTo(variableDTO2);
    }
}
