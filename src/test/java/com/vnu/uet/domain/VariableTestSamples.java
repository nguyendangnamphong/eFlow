package com.vnu.uet.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class VariableTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Variable getVariableSample1() {
        return new Variable()
            .id(1L)
            .variableSourceFormId("variableSourceFormId1")
            .variableTargetFormId("variableTargetFormId1")
            .formula("formula1");
    }

    public static Variable getVariableSample2() {
        return new Variable()
            .id(2L)
            .variableSourceFormId("variableSourceFormId2")
            .variableTargetFormId("variableTargetFormId2")
            .formula("formula2");
    }

    public static Variable getVariableRandomSampleGenerator() {
        return new Variable()
            .id(longCount.incrementAndGet())
            .variableSourceFormId(UUID.randomUUID().toString())
            .variableTargetFormId(UUID.randomUUID().toString())
            .formula(UUID.randomUUID().toString());
    }
}
