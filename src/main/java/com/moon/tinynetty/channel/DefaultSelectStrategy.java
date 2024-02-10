package com.moon.tinynetty.channel;

import java.util.function.IntSupplier;

/**
 * @author Chanmoey
 * Create at 2024/2/11
 */
public class DefaultSelectStrategy implements SelectStrategy{

    static final SelectStrategy INSTANCE = new DefaultSelectStrategy();

    private DefaultSelectStrategy() { }

    @Override
    public int calculateStrategy(IntSupplier selectSupplier, boolean hasTasks) throws Exception {
        return hasTasks ? selectSupplier.getAsInt() : SelectStrategy.SELECT;
    }
}
