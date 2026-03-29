package com.vnu.uet.domain;

import static com.vnu.uet.domain.FlowTestSamples.*;
import static com.vnu.uet.domain.NodeTestSamples.*;
import static com.vnu.uet.domain.RelateNodeTestSamples.*;
import static com.vnu.uet.domain.SwitchNodeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.vnu.uet.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class FlowTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Flow.class);
        Flow flow1 = getFlowSample1();
        Flow flow2 = new Flow();
        assertThat(flow1).isNotEqualTo(flow2);

        flow2.setId(flow1.getId());
        assertThat(flow1).isEqualTo(flow2);

        flow2 = getFlowSample2();
        assertThat(flow1).isNotEqualTo(flow2);
    }

    @Test
    void nodeTest() {
        Flow flow = getFlowRandomSampleGenerator();
        Node nodeBack = getNodeRandomSampleGenerator();

        flow.addNode(nodeBack);
        assertThat(flow.getNodes()).containsOnly(nodeBack);
        assertThat(nodeBack.getFlow()).isEqualTo(flow);

        flow.removeNode(nodeBack);
        assertThat(flow.getNodes()).doesNotContain(nodeBack);
        assertThat(nodeBack.getFlow()).isNull();

        flow.nodes(new HashSet<>(Set.of(nodeBack)));
        assertThat(flow.getNodes()).containsOnly(nodeBack);
        assertThat(nodeBack.getFlow()).isEqualTo(flow);

        flow.setNodes(new HashSet<>());
        assertThat(flow.getNodes()).doesNotContain(nodeBack);
        assertThat(nodeBack.getFlow()).isNull();
    }

    @Test
    void relateNodeTest() {
        Flow flow = getFlowRandomSampleGenerator();
        RelateNode relateNodeBack = getRelateNodeRandomSampleGenerator();

        flow.addRelateNode(relateNodeBack);
        assertThat(flow.getRelateNodes()).containsOnly(relateNodeBack);
        assertThat(relateNodeBack.getFlow()).isEqualTo(flow);

        flow.removeRelateNode(relateNodeBack);
        assertThat(flow.getRelateNodes()).doesNotContain(relateNodeBack);
        assertThat(relateNodeBack.getFlow()).isNull();

        flow.relateNodes(new HashSet<>(Set.of(relateNodeBack)));
        assertThat(flow.getRelateNodes()).containsOnly(relateNodeBack);
        assertThat(relateNodeBack.getFlow()).isEqualTo(flow);

        flow.setRelateNodes(new HashSet<>());
        assertThat(flow.getRelateNodes()).doesNotContain(relateNodeBack);
        assertThat(relateNodeBack.getFlow()).isNull();
    }

    @Test
    void switchNodeTest() {
        Flow flow = getFlowRandomSampleGenerator();
        SwitchNode switchNodeBack = getSwitchNodeRandomSampleGenerator();

        flow.addSwitchNode(switchNodeBack);
        assertThat(flow.getSwitchNodes()).containsOnly(switchNodeBack);
        assertThat(switchNodeBack.getFlow()).isEqualTo(flow);

        flow.removeSwitchNode(switchNodeBack);
        assertThat(flow.getSwitchNodes()).doesNotContain(switchNodeBack);
        assertThat(switchNodeBack.getFlow()).isNull();

        flow.switchNodes(new HashSet<>(Set.of(switchNodeBack)));
        assertThat(flow.getSwitchNodes()).containsOnly(switchNodeBack);
        assertThat(switchNodeBack.getFlow()).isEqualTo(flow);

        flow.setSwitchNodes(new HashSet<>());
        assertThat(flow.getSwitchNodes()).doesNotContain(switchNodeBack);
        assertThat(switchNodeBack.getFlow()).isNull();
    }
}
