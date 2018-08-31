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
package com.kakao.network.exception;

/**
 * @author leoshin
 * Created by leoshin on 15. 8. 20..
 */
public abstract class ResponseStatusError extends Exception {
    public ResponseStatusError(String errorMsg) {
        super(errorMsg);
    }

    public abstract int getErrorCode();
    public abstract String getErrorMsg();
    public abstract int getHttpStatusCode();
}
