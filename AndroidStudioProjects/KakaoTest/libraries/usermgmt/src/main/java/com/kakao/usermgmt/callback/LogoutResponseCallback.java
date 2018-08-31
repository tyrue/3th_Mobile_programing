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
package com.kakao.usermgmt.callback;

import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;

/**
 * @author leoshin, created at 15. 8. 7..
 */
public abstract class LogoutResponseCallback extends ApiResponseCallback<Long> {
    public abstract void onCompleteLogout();

    @Override
    public void onSessionClosed(ErrorResult errorResult) {
    }

    @Override
    public void onNotSignedUp() {
    }

    @Override
    public void onSuccess(Long result) {
    }

    @Override
    public void onDidEnd() {
        Session.getCurrentSession().close();
        onCompleteLogout();
    }
}
