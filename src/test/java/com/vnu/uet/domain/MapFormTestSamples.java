package com.vnu.uet.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MapFormTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static MapForm getMapFormSample1() {
        return new MapForm().id(1L).targetFormId("targetFormId1").sourceFormId("sourceFormId1");
    }

    public static MapForm getMapFormSample2() {
        return new MapForm().id(2L).targetFormId("targetFormId2").sourceFormId("sourceFormId2");
    }

    public static MapForm getMapFormRandomSampleGenerator() {
        return new MapForm()
            .id(longCount.incrementAndGet())
            .targetFormId(UUID.randomUUID().toString())
            .sourceFormId(UUID.randomUUID().toString());
    }
}
