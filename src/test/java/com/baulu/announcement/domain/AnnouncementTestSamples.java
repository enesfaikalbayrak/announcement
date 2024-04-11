package com.baulu.announcement.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class AnnouncementTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Announcement getAnnouncementSample1() {
        return new Announcement().id(1L);
    }

    public static Announcement getAnnouncementSample2() {
        return new Announcement().id(2L);
    }

    public static Announcement getAnnouncementRandomSampleGenerator() {
        return new Announcement().id(longCount.incrementAndGet());
    }
}
