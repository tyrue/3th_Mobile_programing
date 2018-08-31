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
package com.kakao.sdk.link.sample.kakaolink;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;

import com.kakao.kakaolink.AppActionBuilder;
import com.kakao.kakaolink.AppActionInfoBuilder;
import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.sdk.link.sample.R;
import com.kakao.sdk.link.sample.common.widget.DialogBuilder;
import com.kakao.sdk.link.sample.common.widget.KakaoDialogSpinner;
import com.kakao.util.KakaoParameterException;

/**
 * 텍스트, 이미지, 링크, 버튼 타입으로 메시지를 구성하여 카카오톡으로 전송한다.
 */
public class KakaoLinkMainActivity extends Activity {
    private KakaoLink kakaoLink;
    private KakaoDialogSpinner text, link, image, button;
    private CheckBox forwardable;
    private KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder;
    private final String imageSrc;
    private final String weblink;
    private final String inWeblink;

    {
        imageSrc = "http://mud-kage.kakao.co.kr/14/dn/btqb9rFG3H5/esjGPSigv4Gv2qokXyTbGK/o.jpg";
        weblink = "http://www.kakao.com/services/8";
        inWeblink = "http://www.kakao.com/services/8";
    }

    /**
     * 메시지를 구성할 텍스트, 이미지, 링크, 버튼을 위한 spinner를 구성한다.
     * 메시지 전송 버튼과 메시지 다시 구성하기 버튼을 만든다.
     * @param savedInstanceState activity 내려가지 전에 저장한 객체
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_kakaolink_main);
        try {
            kakaoLink = KakaoLink.getKakaoLink(getApplicationContext());
            kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
        } catch (KakaoParameterException e) {
            alert(e.getMessage());
        }

        initializeView();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    private void initializeView() {
        text = (KakaoDialogSpinner) findViewById(R.id.text);
        image = (KakaoDialogSpinner) findViewById(R.id.image);
        link = (KakaoDialogSpinner) findViewById(R.id.link);
        button = (KakaoDialogSpinner) findViewById(R.id.button);
        forwardable = (CheckBox) findViewById(R.id.forwardable);

        findViewById(R.id.forwardable_box).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                forwardable.toggle();
            }
        });

        addListenerOnSendButton();
        addListenerOnClearButton();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.text_types, R.layout.view_sample_title);
        adapter.setDropDownViewResource(R.layout.view_spinner_item);
    }

    // get the selected dropdown list value
    void addListenerOnSendButton() {
        Button sendButton = (Button) findViewById(R.id.send);

        sendButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String textType = String.valueOf(text.getSelectedItem());
                final String linkType = String.valueOf(link.getSelectedItem());
                final String imageType = String.valueOf(image.getSelectedItem());
                final String buttonType = String.valueOf(button.getSelectedItem());

                final String message = "Text : " + textType +
                    "\nLink : " + linkType +
                    "\nImage : " + imageType +
                    "\nButton : " + buttonType;

                new DialogBuilder(KakaoLinkMainActivity.this)
                        .setTitle(R.string.send_message)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendKakaoTalkLink(textType, linkType, imageType, buttonType);
                                kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
            }

        });
    }

    private void addListenerOnClearButton() {
        Button clearButton = (Button) findViewById(R.id.clear);
        clearButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogBuilder(KakaoLinkMainActivity.this)
                    .setTitle(R.string.app_name)
                    .setMessage(R.string.reset_message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            text.setSelection(0);
                            image.setSelection(0);
                            link.setSelection(0);
                            button.setSelection(0);
                            forwardable.setChecked(false);
                            kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
            }
        });
    }

    private void sendKakaoTalkLink(String textType, String linkType, String imageType, String buttonType) {
        try {
            if (textType.equals(getString(R.string.use_text)))
                kakaoTalkLinkMessageBuilder.addText(getString(R.string.kakaolink_text));

            if (imageType.equals(getString(R.string.use_image)))
                kakaoTalkLinkMessageBuilder.addImage(imageSrc, 300, 200);

            // 앱이 설치되어 있는 경우 kakao<app_key>://kakaolink?execparamkey1=1111 로 이동. 앱이 설치되어 있지 않은 경우 market://details?id=com.kakao.sample.kakaolink&referrer=kakaotalklink 또는 https://itunes.apple.com/app/id12345로 이동
            if (linkType.equals(getString(R.string.use_applink))){
                kakaoTalkLinkMessageBuilder.addAppLink(getString(R.string.kakaolink_applink),
                    new AppActionBuilder()
                            .addActionInfo(AppActionInfoBuilder.createAndroidActionInfoBuilder().setExecuteParam("execparamkey1=1111").setMarketParam("referrer=kakaotalklink").build())
                            .addActionInfo(AppActionInfoBuilder.createiOSActionInfoBuilder(AppActionBuilder.DEVICE_TYPE.PHONE).setExecuteParam("execparamkey1=1111").build())
                            .setUrl("http://www.kakao.com")
                            .build());
            }
            // 웹싸이트에 등록한 "http://www.kakao.com"을 overwrite함. overwrite는 같은 도메인만 가능.
            else if (linkType.equals(getString(R.string.use_weblink))) {
                kakaoTalkLinkMessageBuilder.addWebLink(getString(R.string.kakaolink_weblink), weblink);
            }
            else if (linkType.equals(getString(R.string.use_inweblink))) {
                kakaoTalkLinkMessageBuilder.addInWebLink(getString(R.string.use_inweblink), inWeblink);
            }

            // 웹싸이트에 등록된 kakao<app_key>://kakaolink로 이동
            if (buttonType.equals(getString(R.string.use_appbutton)))
                kakaoTalkLinkMessageBuilder.addAppButton(getString(R.string.kakaolink_appbutton), new AppActionBuilder()
                        .addActionInfo(AppActionInfoBuilder.createAndroidActionInfoBuilder().setExecuteParam("execparamkey2=2222").setMarketParam("referrer=kakaotalklink").build())
                        .addActionInfo(AppActionInfoBuilder.createiOSActionInfoBuilder(AppActionBuilder.DEVICE_TYPE.PHONE).setExecuteParam("execparamkey2=2222").build())
                        .setUrl("http://www.kakao.com").build());
            // 웹싸이트에 등록한 "http://www.kakao.com"으로 이동.
            else if (buttonType.equals(getString(R.string.use_webbutton)))
                kakaoTalkLinkMessageBuilder.addWebButton(getString(R.string.kakaolink_webbutton), null);

            kakaoTalkLinkMessageBuilder.setForwardable(forwardable.isChecked());

            kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder, this);
        } catch (KakaoParameterException e) {
            alert(e.getMessage());
        }
    }

    private void alert(String message) {
        new DialogBuilder(KakaoLinkMainActivity.this)
//        new AlertDialog.Builder(this)
//                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .create().show();
    }

}
