package com.kakao.network.tasks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * This is a base abstract task queue used across Kakao SDK. Various executor services could be used.
 * @author kevin.kang. Created on 2017. 5. 8..
 */

public abstract class AbstractTaskQueue {
    private ExecutorService executor;

    public AbstractTaskQueue(final ExecutorService e) {
        executor = e;
    }

    public <T> Future<T> addTask(KakaoResultTask<T> task) {
        return executor.submit(task.getCallable());
    }
}
