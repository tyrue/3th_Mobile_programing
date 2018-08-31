package com.kakao.auth.network;

import com.kakao.network.tasks.AbstractTaskQueue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author kevin.kang. Created on 2017. 5. 8..
 */

public class KakaoSingleTaskQueue extends AbstractTaskQueue {
    private static volatile KakaoSingleTaskQueue instance;

    public static KakaoSingleTaskQueue getInstance() {
        if (instance == null) {
            synchronized (KakaoSingleTaskQueue.class) {
                if (instance == null) {
                    instance = new KakaoSingleTaskQueue(Executors.newCachedThreadPool());
                }
            }
        }
        return instance;
    }

    public KakaoSingleTaskQueue(ExecutorService e) {
        super(e);
    }
}
