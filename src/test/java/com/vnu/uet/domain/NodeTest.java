package com.vnu.uet.domain;

import static com.vnu.uet.domain.FlowTestSamples.*;
import static com.vnu.uet.domain.MapFormTestSamples.*;
import static com.vnu.uet.domain.NodeTestSamples.*;
import static com.vnu.uet.domain.PerformerTestSamples.*;
import static com.vnu.uet.domain.RelateNodeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.vnu.uet.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class NodeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Node.class);
        Node node1 = getNodeSample1();
        Node node2 = new Node();
        assertThat(node1).isNotEqualTo(node2);

        node2.setId(node1.getId());
        assertThat(node1).isEqualTo(node2);

        node2 = getNodeSample2();
        assertThat(node1).isNotEqualTo(node2);
    }

    @Test
    void relateNodeTest() {
        Node node = getNodeRandomSampleGenerator();
        RelateNode relateNodeBack = getRelateNodeRandomSampleGenerator();

        node.addRelateNode(relateNodeBack);
        assertThat(node.getRelateNodes()).containsOnly(relateNodeBack);
        assertThat(relateNodeBack.getNode()).isEqualTo(node);

        node.removeRelateNode(relateNodeBack);
        assertThat(node.getRelateNodes()).doesNotContain(relateNodeBack);
        assertThat(relateNodeBack.getNode()).isNull();

        node.relateNodes(new HashSet<>(Set.of(relateNodeBack)));
        assertThat(node.getRelateNodes()).containsOnly(relateNodeBack);
        assertThat(relateNodeBack.getNode()).isEqualTo(node);

        node.setRelateNodes(new HashSet<>());
        assertThat(node.getRelateNodes()).doesNotContain(relateNodeBack);
        assertThat(relateNodeBack.getNode()).isNull();
    }

    @Test
    void performerTest() {
        Node node = getNodeRandomSampleGenerator();
        Performer performerBack = getPerformerRandomSampleGenerator();

        node.addPerformer(performerBack);
        assertThat(node.getPerformers()).containsOnly(performerBack);
        assertThat(performerBack.getNode()).isEqualTo(node);

        node.removePerformer(performerBack);
        assertThat(node.getPerformers()).doesNotContain(performerBack);
        assertThat(performerBack.getNode()).isNull();

        node.performers(new HashSet<>(Set.of(performerBack)));
        assertThat(node.getPerformers()).containsOnly(performerBack);
        assertThat(performerBack.getNode()).isEqualTo(node);

        node.setPerformers(new HashSet<>());
        assertThat(node.getPerformers()).doesNotContain(performerBack);
        assertThat(performerBack.getNode()).isNull();
    }

    @Test
    void mapFormTest() {
        Node node = getNodeRandomSampleGenerator();
        MapForm mapFormBack = getMapFormRandomSampleGenerator();

        node.addMapForm(mapFormBack);
        assertThat(node.getMapForms()).containsOnly(mapFormBack);
        assertThat(mapFormBack.getNode()).isEqualTo(node);

        node.removeMapForm(mapFormBack);
        assertThat(node.getMapForms()).doesNotContain(mapFormBack);
        assertThat(mapFormBack.getNode()).isNull();

        node.mapForms(new HashSet<>(Set.of(mapFormBack)));
        assertThat(node.getMapForms()).containsOnly(mapFormBack);
        assertThat(mapFormBack.getNode()).isEqualTo(node);

        node.setMapForms(new HashSet<>());
        assertThat(node.getMapForms()).doesNotContain(mapFormBack);
        assertThat(mapFormBack.getNode()).isNull();
    }

    @Test
    void flowTest() {
        Node node = getNodeRandomSampleGenerator();
        Flow flowBack = getFlowRandomSampleGenerator();

        node.setFlow(flowBack);
        assertThat(node.getFlow()).isEqualTo(flowBack);

        node.flow(null);
        assertThat(node.getFlow()).isNull();
    }
}
