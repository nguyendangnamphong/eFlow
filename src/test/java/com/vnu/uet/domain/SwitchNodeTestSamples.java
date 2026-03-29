package com.vnu.uet.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SwitchNodeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static SwitchNode getSwitchNodeSample1() {
        return new SwitchNode().id(1L).formId("formId1").variableId("variableId1");
    }

    public static SwitchNode getSwitchNodeSample2() {
        return new SwitchNode().id(2L).formId("formId2").variableId("variableId2");
    }

    public static SwitchNode getSwitchNodeRandomSampleGenerator() {
        return new SwitchNode()
            .id(longCount.incrementAndGet())
            .formId(UUID.randomUUID().toString())
            .variableId(UUID.randomUUID().toString());
    }
}
