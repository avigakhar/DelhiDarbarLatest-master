package com.app.delhidarbar.helper;

import java.util.Random;

public class RandomUtils {
    private RandomUtils() {
    }
    public static boolean getRandomBoolean() {
        Random random = new Random();
        return random.nextBoolean();
    }
}
