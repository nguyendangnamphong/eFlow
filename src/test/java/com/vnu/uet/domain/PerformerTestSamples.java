package com.vnu.uet.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PerformerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Performer getPerformerSample1() {
        return new Performer().id(1L).userId("userId1").orderExecution(1L);
    }

    public static Performer getPerformerSample2() {
        return new Performer().id(2L).userId("userId2").orderExecution(2L);
    }

    public static Performer getPerformerRandomSampleGenerator() {
        return new Performer()
            .id(longCount.incrementAndGet())
            .userId(UUID.randomUUID().toString())
            .orderExecution(longCount.incrementAndGet());
    }
}
