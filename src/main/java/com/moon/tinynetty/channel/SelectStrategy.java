package com.moon.tinynetty.channel;

import java.util.function.IntSupplier;

/**
 * @author Chanmoey
 * @date @date 2024/2/11
 */
public interface SelectStrategy {

    int SELECT = -1;

    int CONTINUE = -2;

    int BUSY_WAIT = -3;

    int calculateStrategy(IntSupplier selectSupplier, boolean hasTasks) throws Exception;
}
