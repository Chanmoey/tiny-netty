package com.moon.tinynetty.channel;

/**
 * @author Chanmoey
 * Create at 2024/2/11
 */
public interface SelectStrategyFactory {
    SelectStrategy newSelectStrategy();
}
