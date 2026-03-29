package com.vnu.uet.domain;

import static com.vnu.uet.domain.MapFormTestSamples.*;
import static com.vnu.uet.domain.NodeTestSamples.*;
import static com.vnu.uet.domain.VariableTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.vnu.uet.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MapFormTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MapForm.class);
        MapForm mapForm1 = getMapFormSample1();
        MapForm mapForm2 = new MapForm();
        assertThat(mapForm1).isNotEqualTo(mapForm2);

        mapForm2.setId(mapForm1.getId());
        assertThat(mapForm1).isEqualTo(mapForm2);

        mapForm2 = getMapFormSample2();
        assertThat(mapForm1).isNotEqualTo(mapForm2);
    }

    @Test
    void variableTest() {
        MapForm mapForm = getMapFormRandomSampleGenerator();
        Variable variableBack = getVariableRandomSampleGenerator();

        mapForm.addVariable(variableBack);
        assertThat(mapForm.getVariables()).containsOnly(variableBack);
        assertThat(variableBack.getMapForm()).isEqualTo(mapForm);

        mapForm.removeVariable(variableBack);
        assertThat(mapForm.getVariables()).doesNotContain(variableBack);
        assertThat(variableBack.getMapForm()).isNull();

        mapForm.variables(new HashSet<>(Set.of(variableBack)));
        assertThat(mapForm.getVariables()).containsOnly(variableBack);
        assertThat(variableBack.getMapForm()).isEqualTo(mapForm);

        mapForm.setVariables(new HashSet<>());
        assertThat(mapForm.getVariables()).doesNotContain(variableBack);
        assertThat(variableBack.getMapForm()).isNull();
    }

    @Test
    void nodeTest() {
        MapForm mapForm = getMapFormRandomSampleGenerator();
        Node nodeBack = getNodeRandomSampleGenerator();

        mapForm.setNode(nodeBack);
        assertThat(mapForm.getNode()).isEqualTo(nodeBack);

        mapForm.node(null);
        assertThat(mapForm.getNode()).isNull();
    }
}
