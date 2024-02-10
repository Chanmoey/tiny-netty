package com.moon.tinynetty.channel;

/**
 * @author Chanmoey
 * Create at 2024/2/11
 */
public final class DefaultSelectStrategyFactory implements SelectStrategyFactory {
    public static final SelectStrategyFactory INSTANCE = new DefaultSelectStrategyFactory();

    private DefaultSelectStrategyFactory() { }

    @Override
    public SelectStrategy newSelectStrategy() {
        return DefaultSelectStrategy.INSTANCE;
    }
}
