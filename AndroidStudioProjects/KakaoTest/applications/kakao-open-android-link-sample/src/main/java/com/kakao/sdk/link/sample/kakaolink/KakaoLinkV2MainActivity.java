package com.kakao.sdk.link.sample.kakaolink;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.CommerceDetailObject;
import com.kakao.message.template.CommerceTemplate;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.ListTemplate;
import com.kakao.message.template.LocationTemplate;
import com.kakao.message.template.SocialObject;
import com.kakao.message.template.TextTemplate;
import com.kakao.network.ErrorResult;
import com.kakao.network.ServerProtocol;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.sdk.link.sample.R;
import com.kakao.util.helper.log.Logger;

import java.util.HashMap;
import java.util.Map;

public class KakaoLinkV2MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakao_link40_main);

        final String[] methods = new String[] {
                getString(R.string.title_default_feed),
                getString(R.string.title_default_list),
                getString(R.string.title_default_location),
                getString(R.string.title_default_commerce),
                getString(R.string.title_default_text),
                getString(R.string.title_scrap),
                getString(R.string.title_custom_feed),
        };

        final int[] images = new int[] {
                R.drawable.icon_feed,
                R.drawable.icon_list,
                R.drawable.icon_location,
                R.drawable.icon_custom,
                R.drawable.icon_custom,
                R.drawable.icon_scrap,
                R.drawable.icon_custom
        };

        ListView listView = findViewById(R.id.link40_method_list);
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return methods.length;
            }

            @Override
            public Object getItem(int position) {
                return methods[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater mInflater = (LayoutInflater) KakaoLinkV2MainActivity.this
                        .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.menu_item, parent, false);
                }

                ImageView imageView = convertView.findViewById(R.id.method_image);
                TextView textView = convertView.findViewById(R.id.method_text);
                imageView.setImageDrawable(getResources().getDrawable(images[position]));
                textView.setText(methods[position]);
                return convertView;
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0:
                    sendDefaultFeedTemplate();
                    break;
                case 1:
                    sendDefaultListTemplate();
                    break;
                case 2:
                    sendDefaultLocationTemplate();
                    break;
                case 3:
                    sendDefaultCommerceTemplate();
                    break;
                case 4:
                    sendDefaultTextTemplate();
                    break;
                case 5:
                    sendScrapMessage();
                    break;
                case 6:
                    sendFeedTemplate();
                    break;
                default:
                    break;
            }
        });
    }

    private void sendFeedTemplate() {
        String templateId;
        switch (ServerProtocol.DEPLOY_PHASE) {
            case Alpha:
                templateId = "18578";
                break;
            case Sandbox:
                templateId = "222";
                break;
            case Beta:
            case Release:
                templateId = "3135";
                break;
            default:
                return;
        }

        Map<String, String> templateArgs = new HashMap<>();
        templateArgs.put("${title}", "프로방스 자동차 여행");
        templateArgs.put("${description}", "매년 7~8월에 프로방스 발랑솔을 중심으로 라벤더가 만개한다. 이 길을 라벤더로드라고 하며 라벤더와 해바라기 밭이 가득찬 풍경을 어디서나 볼 수 있다.");
        KakaoLinkService.getInstance().sendCustom(this, templateId, templateArgs, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
                Toast.makeText(getApplicationContext(), errorResult.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
            }
        });
    }

    private void sendScrapMessage() {
        KakaoLinkService.getInstance().sendScrap(this, "https://www.kakaofriends.com", new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
                Toast.makeText(getApplicationContext(), errorResult.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
            }
        });
    }

    private void sendDefaultFeedTemplate() {
        FeedTemplate params = FeedTemplate
                .newBuilder(ContentObject.newBuilder("딸기 치즈 케익",
                "http://mud-kage.kakao.co.kr/dn/Q2iNx/btqgeRgV54P/VLdBs9cvyn8BJXB3o7N8UK/kakaolink40_original.png",
                LinkObject.newBuilder().setWebUrl("https://developers.kakao.com")
                        .setMobileWebUrl("https://developers.kakao.com").build())
                .setDescrption("#케익 #딸기 #삼평동 #카페 #분위기 #소개팅")
                .build())
                .setSocial(SocialObject.newBuilder().setLikeCount(286).setCommentCount(45)
                        .setSharedCount(845).build())
                .addButton(new ButtonObject("웹으로 보기", LinkObject.newBuilder().setWebUrl("https://developers.kakao.com").setMobileWebUrl("https://developers.kakao.com").build()))
                .addButton(new ButtonObject("앱으로 보기", LinkObject.newBuilder()
                        .setWebUrl("https://developers.kakao.com")
                        .setMobileWebUrl("https://developers.kakao.com")
                        .setAndroidExecutionParams("key1=value1")
                        .setIosExecutionParams("key1=value1")
                        .build()))
                .build();


        KakaoLinkService.getInstance().sendDefault(this, params, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
            }
        });
    }

    private void sendDefaultListTemplate() {
        ListTemplate params = ListTemplate.newBuilder("WEEKLY MAGAZINE",
                LinkObject.newBuilder()
                        .setWebUrl("https://developers.kakao.com")
                        .setMobileWebUrl("https://developers.kakao.com")
                        .build())
                .addContent(ContentObject.newBuilder("취미의 특징, 탁구",
                        "http://mud-kage.kakao.co.kr/dn/bDPMIb/btqgeoTRQvd/49BuF1gNo6UXkdbKecx600/kakaolink40_original.png",
                        LinkObject.newBuilder()
                                .setWebUrl("https://developers.kakao.com")
                                .setMobileWebUrl("https://developers.kakao.com")
                                .build())
                        .setDescrption("스포츠")
                        .build())
                .addContent(ContentObject.newBuilder("크림으로 이해하는 커피이야기",
                        "http://mud-kage.kakao.co.kr/dn/QPeNt/btqgeSfSsCR/0QJIRuWTtkg4cYc57n8H80/kakaolink40_original.png",
                        LinkObject.newBuilder()
                                .setWebUrl("https://developers.kakao.com")
                                .setMobileWebUrl("https://developers.kakao.com")
                                .build())
                        .setDescrption("음식")
                        .build())
                .addContent(ContentObject.newBuilder("신메뉴 출시❤️ 체리블라썸라떼",
                        "http://mud-kage.kakao.co.kr/dn/c7MBX4/btqgeRgWhBy/ZMLnndJFAqyUAnqu4sQHS0/kakaolink40_original.png",
                        LinkObject.newBuilder()
                                .setWebUrl("https://developers.kakao.com")
                                .setMobileWebUrl("https://developers.kakao.com")
                                .build())
                        .setDescrption("사진").build())
                .addButton(new ButtonObject("웹으로 보기", LinkObject.newBuilder()
                        .setMobileWebUrl("https://developers.kakao.com")
                        .setMobileWebUrl("https://developers.kakao.com")
                        .build()))
                .addButton(new ButtonObject("앱으로 보기", LinkObject.newBuilder()
                        .setWebUrl("https://developers.kakao.com")
                        .setMobileWebUrl("https://developers.kakao.com")
                        .setAndroidExecutionParams("key1=value1")
                        .setIosExecutionParams("key1=value1")
                        .build()))
                .build();

        KakaoLinkService.getInstance().sendDefault(this, params, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {

            }
        });
    }

    private void sendDefaultLocationTemplate() {
        LocationTemplate params = LocationTemplate.newBuilder("성남시 분당구 판교역로 235",
                ContentObject.newBuilder("신메뉴 출시❤️ 체리블라썸라떼",
                "http://mud-kage.kakao.co.kr/dn/bSbH9w/btqgegaEDfW/vD9KKV0hEintg6bZT4v4WK/kakaolink40_original.png",
                    LinkObject.newBuilder()
                            .setWebUrl("https://developers.kakao.com")
                            .setMobileWebUrl("https://developers.kakao.com")
                            .build())
                    .setDescrption("이번 주는 체리블라썸라떼 1+1").build())
                .setSocial(SocialObject.newBuilder().setLikeCount(286).setCommentCount(45).setSharedCount(845).build())
                .setAddressTitle("카카오 판교오피스")
                .build();

        KakaoLinkService.getInstance().sendDefault(this, params, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {

            }
        });
    }

    private void sendDefaultCommerceTemplate() {
        CommerceTemplate params = CommerceTemplate.newBuilder(
                ContentObject.newBuilder("신메뉴 출시❤️ 체리블라썸라떼",
                        "http://mud-kage.kakao.co.kr/dn/bSbH9w/btqgegaEDfW/vD9KKV0hEintg6bZT4v4WK/kakaolink40_original.png",
                        LinkObject.newBuilder()
                                .setWebUrl("https://developers.kakao.com")
                                .setMobileWebUrl("https://developers.kakao.com")
                                .build())
                        .setDescrption("이번 주는 체리블라썸라떼 1+1").build(),
                CommerceDetailObject.newBuilder(12345).setDiscountPrice(10000).setDiscountRate(20).build())
                .build();

        KakaoLinkService.getInstance().sendDefault(this, params, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {

            }
        });
    }

    private void sendDefaultTextTemplate() {
        TextTemplate params = TextTemplate.newBuilder("Text", LinkObject.newBuilder().setWebUrl("https://developers.kakao.com").setMobileWebUrl("https://developers.kakao.com").build()).setButtonTitle("This is button").build();

        KakaoLinkService.getInstance().sendDefault(this, params, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {
            }
        });
    }
}
