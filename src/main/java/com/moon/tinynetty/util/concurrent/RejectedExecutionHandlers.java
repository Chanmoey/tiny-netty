package com.moon.tinynetty.util.concurrent;

import java.util.concurrent.RejectedExecutionException;

/**
 * @author Chanmoey
 * Create at 2024/2/11
 */
public class RejectedExecutionHandlers {

    private static final RejectedExecutionHandler REJECT = (task, executor) -> {
        throw new RejectedExecutionException();
    };

    private RejectedExecutionHandlers() { }

    public static RejectedExecutionHandler reject() {
        return REJECT;
    }
}
