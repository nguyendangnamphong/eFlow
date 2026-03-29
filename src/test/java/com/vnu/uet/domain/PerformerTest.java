package com.vnu.uet.domain;

import static com.vnu.uet.domain.NodeTestSamples.*;
import static com.vnu.uet.domain.PerformerTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.vnu.uet.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PerformerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Performer.class);
        Performer performer1 = getPerformerSample1();
        Performer performer2 = new Performer();
        assertThat(performer1).isNotEqualTo(performer2);

        performer2.setId(performer1.getId());
        assertThat(performer1).isEqualTo(performer2);

        performer2 = getPerformerSample2();
        assertThat(performer1).isNotEqualTo(performer2);
    }

    @Test
    void nodeTest() {
        Performer performer = getPerformerRandomSampleGenerator();
        Node nodeBack = getNodeRandomSampleGenerator();

        performer.setNode(nodeBack);
        assertThat(performer.getNode()).isEqualTo(nodeBack);

        performer.node(null);
        assertThat(performer.getNode()).isNull();
    }
}
