package com.vnu.uet.domain;

import static com.vnu.uet.domain.MapFormTestSamples.*;
import static com.vnu.uet.domain.VariableTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.vnu.uet.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VariableTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Variable.class);
        Variable variable1 = getVariableSample1();
        Variable variable2 = new Variable();
        assertThat(variable1).isNotEqualTo(variable2);

        variable2.setId(variable1.getId());
        assertThat(variable1).isEqualTo(variable2);

        variable2 = getVariableSample2();
        assertThat(variable1).isNotEqualTo(variable2);
    }

    @Test
    void mapFormTest() {
        Variable variable = getVariableRandomSampleGenerator();
        MapForm mapFormBack = getMapFormRandomSampleGenerator();

        variable.setMapForm(mapFormBack);
        assertThat(variable.getMapForm()).isEqualTo(mapFormBack);

        variable.mapForm(null);
        assertThat(variable.getMapForm()).isNull();
    }
}
