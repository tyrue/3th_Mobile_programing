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
package com.kakao.network.callback;

import com.kakao.network.ErrorResult;

/**
 * Callback class that handles network success and errors.
 *
 * @author leoshin, created at 15. 8. 4..
 */
public abstract class ResponseCallback<T> {

    /**
     * 요청이 실패한경우 불린다.
     * @param errorResult 실패한 원인이 담긴 결과
     */
    public abstract void onFailure(final ErrorResult errorResult);

    /**
     * Called when errors are handled by the SDK and other UIs such as alert dialog are no longer needed.
     * @param errorResult Error result containing the cause of failure
     */
    @Deprecated
    public void onHandledFailure(final ErrorResult errorResult) {
    }

    /**
     * request에 대한 result.
     * @param result nullable value
     */
    public abstract void onSuccess(T result);

    /**
     * 해당 Job이 시작될때 불린다.
     * Appilcation 에서 프로그래스바를 보여줄때 사용가능합니다.
     */
    public void onDidStart() {
    }

    /**
     * 해당 Job이 끝날때 불린다.
     * Appilcation 에서 프로그래스바를 닫을때 사용가능합니다.
     */
    public void onDidEnd() {
    }

    /**
     * 요청한 Request가 실패했을때 불리는 callback.
     * 해당 Error를 통해서 Request별로 공통처리 및 onFailure Callback을 재정의 할 수 있습니다.
     * @param errorResult 실패결과.
     */
    public void onFailureForUiThread(ErrorResult errorResult) {
        onFailure(errorResult);
    }

    /**
     * 요청한 Request가 성공했을때 불리는 callback.
     * 해당 callback을 통해서 Request별로 공통처리 및 onSuccess Callback을 재정의 할 수 있습니다.
     * @param result 성공에 대한 response.
     */
    public void onSuccessForUiThread(T result) {
        onSuccess(result);
    }
}
