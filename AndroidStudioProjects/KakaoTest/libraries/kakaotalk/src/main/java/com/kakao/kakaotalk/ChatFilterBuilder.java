/**
 * Copyright 2014-2016 Kakao Corp.
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
package com.kakao.kakaotalk;

import java.util.ArrayList;
import java.util.List;

import com.kakao.util.helper.log.Logger;

/**
 * 권한이 있는 채팅방의 리스트중, 내가 얻어올 수 있는 채팅방의 목록을 filtering 하기위해 필요한 데이터를 만들어주는 wrapper class.
 * filter로 넘겨주었지만 권한이 없는경우, 채팅방이 내려가지 않을 수 있다.
 */
public class ChatFilterBuilder {
    public enum ChatFilter {
        /**
         * 카카오톡 OPEN 채팅방
         */
        OPEN("open"),

        /**
         * 카카오톡 일반 채팅방
         */
        REGULAR("regular"),

        /**
         * 그룹 채팅방
         */
        MULTI("multi"),

        /**
         * 1:1 방
         */
        DIRECT("direct");

        private final String value;

        ChatFilter(String value) {
            this.value = value;
        }
    }

    private final List<ChatFilter> filterList = new ArrayList<ChatFilter>();

    public ChatFilterBuilder addFilter(ChatFilter filter) {
        filterList.add(filter);
        return this;
    }

    public String build() {
        String filterString = "";

        int size = filterList.size();
        for (int i = 0; i < size; i++) {
            filterString += filterList.get(i).value;
            if (i < size) {
                filterString += ",";
            }
        }

        Logger.i("filter = " + filterString);
        return filterString;
    }
}
