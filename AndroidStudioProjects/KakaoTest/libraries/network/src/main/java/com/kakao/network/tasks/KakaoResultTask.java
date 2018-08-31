/**
 * Copyright 2014-2015 Kakao Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kakao.network.tasks;

import android.os.Handler;
import android.os.Looper;

import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.network.exception.ResponseStatusError;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public abstract class KakaoResultTask<T> {
    private final static Handler mainHandler = new Handler(Looper.getMainLooper());
    final ResponseCallback<T> callback;

    public KakaoResultTask() {
        this.callback = null;
    }

    public KakaoResultTask(ResponseCallback<T> callback) {
        this.callback = callback;
    }

    private final Callable<T> task = new Callable<T>() {
        @Override
        public T call() throws Exception {
            T result = null;
            Exception ex = null;

            try {
                onDidStart();

                if (callback != null) {
                    callback.onDidStart();
                }
                result = KakaoResultTask.this.call();
            } catch (Exception e) {
                ex = e;
            }

            final T response = result;
            final Exception t = ex;

            final CountDownLatch lock = new CountDownLatch(1);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (callback == null) {
                            return;
                        }

                        if (t != null) {
                            ErrorResult errorResult;
                            if (t instanceof ResponseStatusError) {
                                ResponseStatusError err = (ResponseStatusError) t;
                                errorResult = new ErrorResult(err);
                            } else {
                                errorResult = new ErrorResult(t);
                            }
                            callback.onFailureForUiThread(errorResult);
                        } else {
                            callback.onSuccessForUiThread(response);
                        }
                    } finally {
                        lock.countDown();
                    }
                }
            });

            lock.await();

            if (callback != null) {
                callback.onDidEnd();
            }
            onDidEnd();

            return result;
        }
    };

    public abstract T call() throws Exception;

    final public Callable<T> getCallable() {
        return task;
    }

    public void onDidStart() {
    }

    public void onDidEnd() {
    }
}
