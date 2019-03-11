package com.codingforhappy.sms.service;

import java.util.Random;

public abstract class VerificationCodeGenerator {
    private static Random r = new Random();

    public static String getRandNum(int charCount) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < charCount; i++) {
            char c = (char) (randomInt(48, 57));
            sb.append(c);
        }
        return sb.toString();
    }

    public static int randomInt(int from, int to) {
        return from + r.nextInt(to - from);
    }
}
