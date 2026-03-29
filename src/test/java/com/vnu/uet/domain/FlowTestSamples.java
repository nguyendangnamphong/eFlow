package com.vnu.uet.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class FlowTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Flow getFlowSample1() {
        return new Flow()
            .id(1L)
            .flowName("flowName1")
            .flowGroup("flowGroup1")
            .ownerName("ownerName1")
            .superviserName("superviserName1")
            .department("department1")
            .describe("describe1")
            .status("status1");
    }

    public static Flow getFlowSample2() {
        return new Flow()
            .id(2L)
            .flowName("flowName2")
            .flowGroup("flowGroup2")
            .ownerName("ownerName2")
            .superviserName("superviserName2")
            .department("department2")
            .describe("describe2")
            .status("status2");
    }

    public static Flow getFlowRandomSampleGenerator() {
        return new Flow()
            .id(longCount.incrementAndGet())
            .flowName(UUID.randomUUID().toString())
            .flowGroup(UUID.randomUUID().toString())
            .ownerName(UUID.randomUUID().toString())
            .superviserName(UUID.randomUUID().toString())
            .department(UUID.randomUUID().toString())
            .describe(UUID.randomUUID().toString())
            .status(UUID.randomUUID().toString());
    }
}
