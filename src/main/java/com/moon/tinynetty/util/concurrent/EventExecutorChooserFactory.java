package com.moon.tinynetty.util.concurrent;

/**
 * @author Chanmoey
 * Create at 2024/2/11
 */
public interface EventExecutorChooserFactory {


    EventExecutorChooser newChooser(EventExecutor[] executors);


    interface EventExecutorChooser {

        /**
         * Returns the new {@link EventExecutor} to use.
         */
        EventExecutor next();
    }
}
