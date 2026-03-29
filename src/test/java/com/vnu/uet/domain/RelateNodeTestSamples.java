package com.vnu.uet.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class RelateNodeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static RelateNode getRelateNodeSample1() {
        return new RelateNode().id(1L).childNodeId(1L);
    }

    public static RelateNode getRelateNodeSample2() {
        return new RelateNode().id(2L).childNodeId(2L);
    }

    public static RelateNode getRelateNodeRandomSampleGenerator() {
        return new RelateNode().id(longCount.incrementAndGet()).childNodeId(longCount.incrementAndGet());
    }
}
