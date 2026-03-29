package com.vnu.uet.domain;

import static com.vnu.uet.domain.FlowTestSamples.*;
import static com.vnu.uet.domain.NodeTestSamples.*;
import static com.vnu.uet.domain.RelateDemandTestSamples.*;
import static com.vnu.uet.domain.RelateNodeTestSamples.*;
import static com.vnu.uet.domain.SwitchNodeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.vnu.uet.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RelateNodeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RelateNode.class);
        RelateNode relateNode1 = getRelateNodeSample1();
        RelateNode relateNode2 = new RelateNode();
        assertThat(relateNode1).isNotEqualTo(relateNode2);

        relateNode2.setId(relateNode1.getId());
        assertThat(relateNode1).isEqualTo(relateNode2);

        relateNode2 = getRelateNodeSample2();
        assertThat(relateNode1).isNotEqualTo(relateNode2);
    }

    @Test
    void switchNodeTest() {
        RelateNode relateNode = getRelateNodeRandomSampleGenerator();
        SwitchNode switchNodeBack = getSwitchNodeRandomSampleGenerator();

        relateNode.addSwitchNode(switchNodeBack);
        assertThat(relateNode.getSwitchNodes()).containsOnly(switchNodeBack);
        assertThat(switchNodeBack.getRelateNode()).isEqualTo(relateNode);

        relateNode.removeSwitchNode(switchNodeBack);
        assertThat(relateNode.getSwitchNodes()).doesNotContain(switchNodeBack);
        assertThat(switchNodeBack.getRelateNode()).isNull();

        relateNode.switchNodes(new HashSet<>(Set.of(switchNodeBack)));
        assertThat(relateNode.getSwitchNodes()).containsOnly(switchNodeBack);
        assertThat(switchNodeBack.getRelateNode()).isEqualTo(relateNode);

        relateNode.setSwitchNodes(new HashSet<>());
        assertThat(relateNode.getSwitchNodes()).doesNotContain(switchNodeBack);
        assertThat(switchNodeBack.getRelateNode()).isNull();
    }

    @Test
    void relateDemandTest() {
        RelateNode relateNode = getRelateNodeRandomSampleGenerator();
        RelateDemand relateDemandBack = getRelateDemandRandomSampleGenerator();

        relateNode.addRelateDemand(relateDemandBack);
        assertThat(relateNode.getRelateDemands()).containsOnly(relateDemandBack);
        assertThat(relateDemandBack.getRelateNode()).isEqualTo(relateNode);

        relateNode.removeRelateDemand(relateDemandBack);
        assertThat(relateNode.getRelateDemands()).doesNotContain(relateDemandBack);
        assertThat(relateDemandBack.getRelateNode()).isNull();

        relateNode.relateDemands(new HashSet<>(Set.of(relateDemandBack)));
        assertThat(relateNode.getRelateDemands()).containsOnly(relateDemandBack);
        assertThat(relateDemandBack.getRelateNode()).isEqualTo(relateNode);

        relateNode.setRelateDemands(new HashSet<>());
        assertThat(relateNode.getRelateDemands()).doesNotContain(relateDemandBack);
        assertThat(relateDemandBack.getRelateNode()).isNull();
    }

    @Test
    void flowTest() {
        RelateNode relateNode = getRelateNodeRandomSampleGenerator();
        Flow flowBack = getFlowRandomSampleGenerator();

        relateNode.setFlow(flowBack);
        assertThat(relateNode.getFlow()).isEqualTo(flowBack);

        relateNode.flow(null);
        assertThat(relateNode.getFlow()).isNull();
    }

    @Test
    void nodeTest() {
        RelateNode relateNode = getRelateNodeRandomSampleGenerator();
        Node nodeBack = getNodeRandomSampleGenerator();

        relateNode.setNode(nodeBack);
        assertThat(relateNode.getNode()).isEqualTo(nodeBack);

        relateNode.node(null);
        assertThat(relateNode.getNode()).isNull();
    }
}
