package com.sdu.irpc.framework.core.limiter;

public class TokenBucketRateLimiter implements Limiter {

    private int tokens;

    private final int capacity;

    private final int interval;

    private Long lastTokenTime;

    public TokenBucketRateLimiter(int capacity, int interval) {
        this.capacity = capacity;
        this.interval = interval;
        this.lastTokenTime = System.currentTimeMillis();
        this.tokens = capacity;
    }

    @Override
    public synchronized boolean allowRequest() {
        Long currentTime = System.currentTimeMillis();
        long timeInterval = currentTime - lastTokenTime;
        if (timeInterval > interval) {
            tokens = (int) Math.min(capacity, timeInterval * 10 + tokens);
            this.lastTokenTime = System.currentTimeMillis();
        }
        if (tokens > 0) {
            tokens --;
            return true;
        } else {
            System.out.println("请求被拦截---------------");
            return false;
        }
    }
}
