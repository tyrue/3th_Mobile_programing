package com.kakao.usermgmt.response.model;

import android.os.Parcelable;

/**
 * @author leoshin on 15. 9. 17.
 */
public interface User extends Parcelable {
    /**
     * @return 사용자 ID
     */
    long getId();

    /**
     * @return 해당 앱에서 유일한 친구의 code
     * 가변적인 데이터.
     */
    String getUUID();

    /**
     * @return 친구의 카카오 회원번호. 앱의 특정 카테고리나 특정 권한에 한해 내려줌.
     */
    long getServiceUserId();
}
