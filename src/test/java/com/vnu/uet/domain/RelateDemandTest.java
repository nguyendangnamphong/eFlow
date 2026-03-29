package com.vnu.uet.domain;

import static com.vnu.uet.domain.RelateDemandTestSamples.*;
import static com.vnu.uet.domain.RelateNodeTestSamples.*;
import static com.vnu.uet.domain.SwitchNodeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.vnu.uet.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RelateDemandTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RelateDemand.class);
        RelateDemand relateDemand1 = getRelateDemandSample1();
        RelateDemand relateDemand2 = new RelateDemand();
        assertThat(relateDemand1).isNotEqualTo(relateDemand2);

        relateDemand2.setId(relateDemand1.getId());
        assertThat(relateDemand1).isEqualTo(relateDemand2);

        relateDemand2 = getRelateDemandSample2();
        assertThat(relateDemand1).isNotEqualTo(relateDemand2);
    }

    @Test
    void relateNodeTest() {
        RelateDemand relateDemand = getRelateDemandRandomSampleGenerator();
        RelateNode relateNodeBack = getRelateNodeRandomSampleGenerator();

        relateDemand.setRelateNode(relateNodeBack);
        assertThat(relateDemand.getRelateNode()).isEqualTo(relateNodeBack);

        relateDemand.relateNode(null);
        assertThat(relateDemand.getRelateNode()).isNull();
    }

    @Test
    void switchNodeTest() {
        RelateDemand relateDemand = getRelateDemandRandomSampleGenerator();
        SwitchNode switchNodeBack = getSwitchNodeRandomSampleGenerator();

        relateDemand.setSwitchNode(switchNodeBack);
        assertThat(relateDemand.getSwitchNode()).isEqualTo(switchNodeBack);

        relateDemand.switchNode(null);
        assertThat(relateDemand.getSwitchNode()).isNull();
    }
}
