package com.vnu.uet.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class RelateDemandTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static RelateDemand getRelateDemandSample1() {
        return new RelateDemand().id(1L).relateDemand("relateDemand1");
    }

    public static RelateDemand getRelateDemandSample2() {
        return new RelateDemand().id(2L).relateDemand("relateDemand2");
    }

    public static RelateDemand getRelateDemandRandomSampleGenerator() {
        return new RelateDemand().id(longCount.incrementAndGet()).relateDemand(UUID.randomUUID().toString());
    }
}
