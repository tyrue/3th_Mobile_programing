package com.kakao.sdk.s2.sample;

import android.app.Application;
import android.widget.Toast;

import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.s2.EventsLogResponse;
import com.kakao.s2.S2Service;
import com.kakao.util.helper.log.Logger;

/**
 * @author kevin.kang
 * Created by kevin.kang on 2016. 9. 13..
 */

public class S2Application extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        S2Service.init(this, 20);
        final S2Application self = this;
        S2Service.getInstance().setPublishCallback(new ResponseCallback<EventsLogResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e("There was an error publishing batched events.");
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(EventsLogResponse result) {
                Toast.makeText(self.getApplicationContext(), "Successfully published " + result.getSuccessCount() + " event(s).", Toast.LENGTH_SHORT).show();
                Logger.e("Successfully published " + result.getSuccessCount() + " event(s).");
            }
        });
        S2Service.getInstance().setAddEventCallback(new ResponseCallback<Integer>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e("There was an error adding an event to batch.");
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(Integer result) {
                Logger.e("Successfully added an event to batch. Current batch size is " + result);
            }
        });
    }
}
