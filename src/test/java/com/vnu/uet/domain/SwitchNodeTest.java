package com.vnu.uet.domain;

import static com.vnu.uet.domain.FlowTestSamples.*;
import static com.vnu.uet.domain.RelateDemandTestSamples.*;
import static com.vnu.uet.domain.RelateNodeTestSamples.*;
import static com.vnu.uet.domain.SwitchNodeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.vnu.uet.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SwitchNodeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SwitchNode.class);
        SwitchNode switchNode1 = getSwitchNodeSample1();
        SwitchNode switchNode2 = new SwitchNode();
        assertThat(switchNode1).isNotEqualTo(switchNode2);

        switchNode2.setId(switchNode1.getId());
        assertThat(switchNode1).isEqualTo(switchNode2);

        switchNode2 = getSwitchNodeSample2();
        assertThat(switchNode1).isNotEqualTo(switchNode2);
    }

    @Test
    void relateDemandTest() {
        SwitchNode switchNode = getSwitchNodeRandomSampleGenerator();
        RelateDemand relateDemandBack = getRelateDemandRandomSampleGenerator();

        switchNode.addRelateDemand(relateDemandBack);
        assertThat(switchNode.getRelateDemands()).containsOnly(relateDemandBack);
        assertThat(relateDemandBack.getSwitchNode()).isEqualTo(switchNode);

        switchNode.removeRelateDemand(relateDemandBack);
        assertThat(switchNode.getRelateDemands()).doesNotContain(relateDemandBack);
        assertThat(relateDemandBack.getSwitchNode()).isNull();

        switchNode.relateDemands(new HashSet<>(Set.of(relateDemandBack)));
        assertThat(switchNode.getRelateDemands()).containsOnly(relateDemandBack);
        assertThat(relateDemandBack.getSwitchNode()).isEqualTo(switchNode);

        switchNode.setRelateDemands(new HashSet<>());
        assertThat(switchNode.getRelateDemands()).doesNotContain(relateDemandBack);
        assertThat(relateDemandBack.getSwitchNode()).isNull();
    }

    @Test
    void flowTest() {
        SwitchNode switchNode = getSwitchNodeRandomSampleGenerator();
        Flow flowBack = getFlowRandomSampleGenerator();

        switchNode.setFlow(flowBack);
        assertThat(switchNode.getFlow()).isEqualTo(flowBack);

        switchNode.flow(null);
        assertThat(switchNode.getFlow()).isNull();
    }

    @Test
    void relateNodeTest() {
        SwitchNode switchNode = getSwitchNodeRandomSampleGenerator();
        RelateNode relateNodeBack = getRelateNodeRandomSampleGenerator();

        switchNode.setRelateNode(relateNodeBack);
        assertThat(switchNode.getRelateNode()).isEqualTo(relateNodeBack);

        switchNode.relateNode(null);
        assertThat(switchNode.getRelateNode()).isNull();
    }
}
