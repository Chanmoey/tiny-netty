package com.moon.tinynetty.util.concurrent;

import com.moon.tinynetty.util.internal.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @author Chanmoey
 * Create at 2024/2/7
 */
public abstract class SingleThreadEventExecutor implements EventExecutor {

    private static final Logger logger = LoggerFactory.getLogger(SingleThreadEventExecutor.class);

    /**
     * 执行器的初始化状态，未启动
     */
    private static final int ST_NOT_STARTED = 1;

    /**
     * 执行器启动后的状态
     */
    private static final int ST_STARTED = 2;

    /**
     * 执行器的状态
     */
    private volatile int state = ST_NOT_STARTED;

    /**
     * 执行器的状态更新器，也是一个原子类，通过cas来改变执行器的状态
     */
    private static final AtomicIntegerFieldUpdater<SingleThreadEventExecutor> STATE_UPDATED =
            AtomicIntegerFieldUpdater.newUpdater(SingleThreadEventExecutor.class, "state");

    /**
     * 任务队列的容量，默认是Integer的最大值
     */
    protected static final int DEFAULT_MAX_PENDING_EXECUTOR_TASKS = Integer.MAX_VALUE;

    private final Queue<Runnable> taskQueue;

    /**
     * 创建执行器的线程
     */
    private volatile Thread thread;

    /**
     * 创建线程的执行器
     */
    private Executor executor;

    private EventExecutorGroup parent;

    private boolean addTaskWakesUp;

    private volatile boolean interrupted;

    private final RejectedExecutionHandler rejectedExecutionHandler;

    protected SingleThreadEventExecutor(EventExecutorGroup parent, Executor executor,
                                        boolean addTaskWakesUp, Queue<Runnable> taskQueue,
                                        RejectedExecutionHandler rejectedHandler) {
        this.parent = parent;
        this.addTaskWakesUp = addTaskWakesUp;
        this.executor = executor;
        this.taskQueue = ObjectUtil.checkNotNull(taskQueue, "taskQueue");
        this.rejectedExecutionHandler = ObjectUtil.checkNotNull(rejectedHandler, "rejectedHandler");
    }

    protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
        return new LinkedBlockingQueue<>(maxPendingTasks);
    }

    /**
     * 执行器的工作方法，由NioEventLoop具体子类来实现
     */
    protected abstract void run();

    @Override
    public void execute(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task");
        }
        //把任务提交到任务队列中
        addTask(task);
        //启动单线程执行器中的线程
        startThread();
    }

    private void startThread() {
        if (state == ST_NOT_STARTED && (STATE_UPDATED.compareAndSet(this, ST_NOT_STARTED, ST_STARTED))) {
            boolean success = false;
            try {
                doStartThread();
                success = true;
            } finally {
                if (!success) {
                    STATE_UPDATED.compareAndSet(this, ST_STARTED, ST_NOT_STARTED);
                }
            }

        }
    }

    private void doStartThread() {
        executor.execute(() -> {
            thread = Thread.currentThread();
            if (interrupted) {
                thread.interrupt();
            }
            // 线程开始轮询处理IO事件，父类中的关键字this代表的是之类的对象，这里调用的是NioEventLoop中的run方法
            SingleThreadEventExecutor.this.run();
            logger.info("单线程执行器的线程错误结束了！");
        });
    }

    @Override
    public boolean inEventLoop(Thread thread) {
        return thread == this.thread;
    }

    /**
     * 判断任务队列中是否还有任务
     */
    protected boolean hasTasks() {
        return !taskQueue.isEmpty();
    }

    private void addTask(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task");
        }
        if (!offerTask(task)) {
            reject(task);
        }
    }

    final boolean offerTask(Runnable task) {
        return taskQueue.offer(task);
    }

    protected void runAllTasks() {
        runAllTasksFrom(taskQueue);
    }

    protected void runAllTasksFrom(Queue<Runnable> taskQueue) {
        // 从任务队列中拉取任务，如果第一次拉取就为null，说明任务队列中没有任务，直接返回即可
        Runnable task = pollTaskFrom(taskQueue);
        if (task == null) {
            return;
        }

        while (true) {
            // 秩序任务队列中的任务
            safeExecute(task);
            task = pollTaskFrom(taskQueue);
            if (task == null) {
                return;
            }
        }
    }

    private void safeExecute(Runnable task) {
        try {
            task.run();
        } catch (Throwable t) {
            logger.warn("A task raised an exception. Task: {}", task, t);
        }
    }

    protected static Runnable pollTaskFrom(Queue<Runnable> taskQueue) {
        return taskQueue.poll();
    }

    protected static void reject(Runnable task) {
        throw new RejectedExecutionException("event executor terminated");
    }

    /**
     * 中断单线程执行器中的线程
     */
    protected void interruptThread() {
        Thread currentThread = thread;
        if (currentThread == null) {
            interrupted = true;
        } else {
            // 将线程的中断标记位设置为true
            currentThread.interrupt();
        }
    }

    @Override
    public void shutdownGracefully() {

    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public void awaitTermination(Integer integer, TimeUnit timeUnit) throws InterruptedException{

    }
}









































