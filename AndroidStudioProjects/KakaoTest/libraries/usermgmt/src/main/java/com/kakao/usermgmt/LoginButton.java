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
package com.kakao.usermgmt;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kakao.auth.AuthType;
import com.kakao.auth.KakaoSDK;
import com.kakao.auth.Session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 로그인 버튼
 * 로그인 layout에 {@link LoginButton}을 선언하여 사용한다.
 * @author MJ
 */
public class LoginButton extends FrameLayout {

    private Fragment fragment;
    private android.support.v4.app.Fragment supportFragment;

    public LoginButton(Context context) {
        super(context);
    }

    public LoginButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoginButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 로그인 버튼 클릭시 세션을 오픈하도록 설정한다.
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        inflate(getContext(), R.layout.kakao_login_layout, this);
        setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // 카톡 또는 카스가 존재하면 옵션을 보여주고, 존재하지 않으면 바로 직접 로그인창.
                final List<AuthType> authTypes = getAuthTypes();
                onClickLoginButton(authTypes);
            }
        });
    }

    private List<AuthType> getAuthTypes() {
        final List<AuthType> availableAuthTypes = new ArrayList<AuthType>();
        if (Session.getAuthCodeManager().isTalkLoginAvailable()) {
            availableAuthTypes.add(AuthType.KAKAO_TALK);
        }
        if (Session.getAuthCodeManager().isStoryLoginAvailable()) {
            availableAuthTypes.add(AuthType.KAKAO_STORY);
        }
        availableAuthTypes.add(AuthType.KAKAO_ACCOUNT);

        AuthType[] authTypes = KakaoSDK.getAdapter().getSessionConfig().getAuthTypes();
        if (authTypes == null || authTypes.length == 0 || (authTypes.length == 1 && authTypes[0] == AuthType.KAKAO_LOGIN_ALL)) {
            authTypes = AuthType.values();
        }
        availableAuthTypes.retainAll(Arrays.asList(authTypes));

        // 개발자가 설정한 것과 available 한 타입이 없다면 직접계정 입력이 뜨도록 한다.
        if(availableAuthTypes.size() == 0){
            availableAuthTypes.add(AuthType.KAKAO_ACCOUNT);
        }
        return availableAuthTypes;
    }

    private void onClickLoginButton(final List<AuthType> authTypes){
        if (authTypes.size() == 1) {
            openSession(authTypes.get(0));
        } else {
            final Item[] authItems = createAuthItemArray(authTypes);
            ListAdapter adapter = createLoginAdapter(authItems);
            final Dialog dialog = createLoginDialog(authItems, adapter);
            dialog.show();
        }
    }

    /**
     * 가능한 AuhType들이 담겨 있는 리스트를 인자로 받아 로그인 어댑터의 data source로 사용될 Item array를 반환한다.
     * @param authTypes 가능한 AuthType들을 담고 있는 리스트
     * @return 실제로 로그인 방법 리스트에 사용될 Item array
     */
    private Item[] createAuthItemArray(final List<AuthType> authTypes) {
        final List<Item> itemList = new ArrayList<Item>();
        if(authTypes.contains(AuthType.KAKAO_TALK)) {
            itemList.add(new Item(R.string.com_kakao_kakaotalk_account, R.drawable.talk, R.string.com_kakao_kakaotalk_account_tts, AuthType.KAKAO_TALK));
        }
        if(authTypes.contains(AuthType.KAKAO_STORY)) {
            itemList.add(new Item(R.string.com_kakao_kakaostory_account, R.drawable.story, R.string.com_kakao_kakaostory_account_tts, AuthType.KAKAO_STORY));
        }
        if(authTypes.contains(AuthType.KAKAO_ACCOUNT)){
            itemList.add(new Item(R.string.com_kakao_other_kakaoaccount, R.drawable.account, R.string.com_kakao_other_kakaoaccount_tts, AuthType.KAKAO_ACCOUNT));
        }

        return itemList.toArray(new Item[itemList.size()]);
    }

    @SuppressWarnings("deprecation")
    private ListAdapter createLoginAdapter(final Item[] authItems) {
        /**
         * 가능한 auth type들을 유저에게 보여주기 위한 준비.
         */
        return new ArrayAdapter<Item>(
                getContext(),
                android.R.layout.select_dialog_item,
                android.R.id.text1, authItems){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) getContext()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.layout_login_item, parent, false);
                }
                ImageView imageView = (ImageView) convertView.findViewById(R.id.login_method_icon);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageView.setImageDrawable(getResources().getDrawable(authItems[position].icon, getContext().getTheme()));
                } else {
                    imageView.setImageDrawable(getResources().getDrawable(authItems[position].icon));
                }
                TextView textView = (TextView) convertView.findViewById(R.id.login_method_text);
                textView.setText(authItems[position].textId);
                return convertView;
            }
        };
    }

    /**
     * 실제로 유저에게 보여질 dialog 객체를 생성한다.
     * @param authItems 가능한 AuthType들의 정보를 담고 있는 Item array
     * @param adapter Dialog의 list view에 쓰일 adapter
     * @return 로그인 방법들을 팝업으로 보여줄 dialog
     */
    private Dialog createLoginDialog(final Item[] authItems, final ListAdapter adapter) {
        final Dialog dialog = new Dialog(getContext(), R.style.LoginDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_login_dialog);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
        }

//        TextView textView = (TextView) dialog.findViewById(R.id.login_title_text);
//        Typeface customFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/KakaoOTFRegular.otf");
//        if (customFont != null) {
//            textView.setTypeface(customFont);
//        }

        ListView listView = (ListView) dialog.findViewById(R.id.login_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final AuthType authType = authItems[position].authType;
                if (authType != null) {
                    openSession(authType);
                }
                dialog.dismiss();
            }
        });

        Button closeButton = (Button) dialog.findViewById(R.id.login_close_button);
        closeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

    public void openSession(final AuthType authType) {
        if (getFragment() != null) {
            Session.getCurrentSession().open(authType, getFragment());
        } else if (getSupportFragment() != null) {
            Session.getCurrentSession().open(authType, getSupportFragment());
        } else {
            Session.getCurrentSession().open(authType, (Activity) getContext());
        }
    }

    public void setFragment(final Fragment fragment) {
        this.fragment = fragment;
    }

    public void setSuportFragment(final android.support.v4.app.Fragment fragment) {
        this.supportFragment = fragment;
    }

    public Fragment getFragment() {
        return this.fragment;
    }

    public android.support.v4.app.Fragment getSupportFragment() {
        return this.supportFragment;
    }

    /**
     * 각 로그인 방법들의 text, icon, 실제 AuthTYpe들을 담고 있는 container class.
     */
    private static class Item {
        final int textId;
        public final int icon;
        final int contentDescId;
        final AuthType authType;
        Item(final int textId, final Integer icon, final int contentDescId, final AuthType authType) {
            this.textId = textId;
            this.icon = icon;
            this.contentDescId = contentDescId;
            this.authType = authType;
        }
    }
}
