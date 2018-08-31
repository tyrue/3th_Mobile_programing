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
package com.kakao.auth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.kakao.util.AppConfig;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.exception.KakaoException.ErrorType;
import com.kakao.util.helper.log.Logger;

/**
 * Application에서 구현을 해줘야 하며 Application에서 init method를 {@link KakaoAdapter}와 연결한다.
 * @author leoshin, created at 15. 7. 20..
 */
public class KakaoSDK {
    @SuppressLint("StaticFieldLeak")
    private static volatile KakaoAdapter adapter = null;

    @SuppressLint("StaticFieldLeak")
    private static volatile Activity currentActivity;

    public synchronized static void init(KakaoAdapter adapter) {
        if (KakaoSDK.adapter != null) {
            throw new AlreadyInitializedException();
        }

        if (adapter == null) {
            throw new KakaoException(ErrorType.MISS_CONFIGURATION, "adapter is null");
        }

        KakaoSDK.adapter = adapter;

        // Sessino initailize.
        final Context context = adapter.getApplicationConfig().getApplicationContext();
        final Application application = (Application) context;

        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            private int numActivities = 0;
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
                currentActivity = activity;
                if (numActivities == 0) {
//                    try {
//                        String accessToken = Session.getCurrentSession().getAccessToken();
//                        if (!Utility.isNullOrEmpty(accessToken)) {
//                            S2Service.getInstance().addEvent(new Event.Builder().setAction("APP_START").setTimestamp(new Date().getTime()).setFrom(accessToken).setTo(context.getPackageName()).build());
//                        } else {
//                            Logger.i("Access token is empty because the user is currently not logged in to KakaoTalk.");
//                        }
//                    } catch (Exception e) {
//                        Logger.w("Kakao Session is not initialized yet.");
//                    }
                    if (needsToResetSession(activity, Session.getCurrentSession())) {
                        Session.getCurrentSession().internalClose(new KakaoException(ErrorType.CANCELED_OPERATION, "App restarted during Kakao login procedure. Restarting from the start."), false);
                    }
                }
                numActivities++;
            }

            @Override
            public void onActivityPaused(Activity activity) {
                if (activity != null && activity.equals(currentActivity)) {
                    currentActivity = null;
                }
            }

            @Override
            public void onActivityStopped(Activity activity) {
                numActivities--;
                if (numActivities == 0) {
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });

//        S2Service.init(application, 1);

        ApprovalType approvalType = adapter.getSessionConfig().getApprovalType();
        AppConfig.getInstance(context);
        Session.initialize(context, approvalType);
    }

    public static KakaoAdapter getAdapter() {
        return adapter;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    // Checks if app hash been relaunched with single task launch mode, while session state is not reset.
    private static boolean needsToResetSession(final Activity activity, final Session session) {
        if (!Intent.ACTION_MAIN.equals(activity.getIntent().getAction())) return false;
        if (!session.isClosed()) return false;
        if (session.getRequestType() == null) return false;
        return true;
    }

    /**
     * 3rd party Application과 Kakao SDK를 연결하는 과정중 adapter를 중복으로 세팅하게 되었을경우 발생할 수 있다.
     */
    public static class AlreadyInitializedException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}
