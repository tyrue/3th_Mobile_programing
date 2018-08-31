/**
 * Copyright 2014-2017 Kakao Corp.
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
package com.kakao.network;

import com.kakao.network.exception.ResponseStatusError;
import com.kakao.util.helper.log.Logger;

import java.net.HttpURLConnection;


/**
 * Error에 대한 결과값을 저장하고 있는 객체.
 * error code, error message, http status를 저장한다.
 * @author leoshin, created at 15. 7. 28..
 */
public class ErrorResult {
    private final int CLIENT_ERROR_CODE = -777;
    private final int errorCode;
    protected final String errorMessage;
    private final int httpStatus;
    protected final Exception exception;

    /**
     * 클라이언트 사이드에서 에러가 발생한 경우에 사용하는 생성자
     * @param e 클라이언트 사이드에서 발생한 예외 객체
     */
    public ErrorResult(Exception e) {
        this.errorCode = CLIENT_ERROR_CODE;
        this.errorMessage = e.getMessage();
        this.httpStatus = HttpURLConnection.HTTP_INTERNAL_ERROR;
        this.exception = e;
    }

    /**
     * Http Status Code가 200이 아닌 경우에 사용하는 생성자
     * @param e 서버에서 내려온 에러를 표현하는 예외 객체
     */
    public ErrorResult(ResponseStatusError e) {
        this.errorCode = e.getErrorCode();
        this.errorMessage = e.getErrorMsg();
        this.httpStatus = e.getHttpStatusCode();
        this.exception = e;
    }

    /**
     * ErrorCode를 반환한다.
     * @return response 에러코드.
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * 에러발생 메세지
     * @return 에러발생 메세지
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * HttpResponse StatusCode를 반환
     * @return HttpResponse StatusCode
     */
    public int getHttpStatus() {
        return httpStatus;
    }

    public Exception getException() {
        return exception;
    }

    /**
     * 결과 객체를 String으로 표현
     * @return 요청 URL, 에러 코드, 에러 메시지를 포함한 string
     */
    @Override
    public String toString() {
        return  "ErrorResult{errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                ", exception='" + (exception != null ? exception.toString() : null) + '\'' +
                '}';
    }
}
