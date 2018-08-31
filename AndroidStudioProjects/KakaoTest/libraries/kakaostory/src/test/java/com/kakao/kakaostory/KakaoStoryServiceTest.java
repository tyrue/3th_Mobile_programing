package com.kakao.kakaostory;

import com.kakao.kakaostory.callback.StoryResponseCallback;
import com.kakao.kakaostory.request.PostRequest;
import com.kakao.kakaostory.response.model.MyStoryInfo;
import com.kakao.network.ErrorResult;
import com.kakao.util.KakaoParameterException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kevin.kang on 16. 8. 12..
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class KakaoStoryServiceTest {

    /**
     * KakaoStoryService 메소드들의 인자들로 쓰이게 될 상수/변수들.
     */
    private final String noteContent = "A Rainbow - William Wordsworth\n" +
            "\n" +
            "My heart leaps up when I behold\n" +
            "A rainbow in the sky:\n" +
            "So was it when my life began;\n" +
            "So is it now I am a man;\n" +
            "So be it when I shall grow old,\n" +
            "Or let me die!\n" +
            "The Child is father of the Man;\n" +
            "I could wish my days to be\n" +
            "Bound each to each by natural piety.";

    private final String photoContent = "This cafe is really awesome!";
    private final String linkContent = "better than expected!";
    private final String execParam = "place=1111";
    private final String marketParam = "referrer=kakaostory";
    private HashMap<String, String> execParamMap;
    private HashMap<String, String> marketParamMap;
    private String scrapUrl = "http://developers.kakao.com";
    private final KakaoStoryResponseCallback<MyStoryInfo>  callback = new KakaoStoryResponseCallback<MyStoryInfo>();

    /**
     * 매번 테스트 케이스 전에 불리게될 전처리 메소드. HashMap을 항상 새로 initialize한다.
     */
    @Before
    public void setup() {
        ShadowLog.stream = System.out;

        execParamMap = new HashMap<>();
        marketParamMap = new HashMap<>();
        execParamMap.put("place", "1111");
        marketParamMap.put("referrer", "kakaostory");

    }

    @Test
    public void testRequestPostLinkWithMapParameters() throws KakaoParameterException {
//        KakaoStoryService.requestPostLink(callback, scrapUrl, linkContent, PostRequest.StoryPermission.PUBLIC, true, execParam, execParam, marketParam, marketParam);
//        KakaoStoryService.requestPostLink(callback, scrapUrl, linkContent, PostRequest.StoryPermission.PUBLIC, true, execParamMap, execParamMap, marketParamMap, marketParamMap);
//        KakaoStoryService.requestPostLink(callback, scrapUrl, linkContent, PostRequest.StoryPermission.PUBLIC, true, execParam , execParam, marketParam, marketParam);
    }

    @Test
    public void testRequestPostNoteWithMapParameters() throws KakaoParameterException {
//        KakaoStoryService.requestPostNote(callback, noteContent, PostRequest.StoryPermission.PUBLIC, true, execParam, execParam, marketParam, execParam);
//        KakaoStoryService.requestPostNote(callback, noteContent, PostRequest.StoryPermission.PUBLIC, true, execParamMap, execParamMap, marketParamMap, execParamMap);
//        KakaoStoryService.requestPostNote(callback, noteContent, PostRequest.StoryPermission.PUBLIC, true, execParam, execParam, marketParam, execParam);
    }

    @Test
    public void testRequestPostPhotoWithMapParameters() throws KakaoParameterException {
//        List<File> fileList = new ArrayList<File>();
//        KakaoStoryService.requestPostPhoto(callback, fileList, photoContent, PostRequest.StoryPermission.PUBLIC, true, execParam, execParam, marketParam, execParam);
//
//        KakaoStoryService.requestPostPhoto(callback, fileList, photoContent, PostRequest.StoryPermission.PUBLIC, true, execParamMap, execParamMap, marketParamMap, execParamMap);
//        KakaoStoryService.requestPostPhoto(callback, fileList, photoContent, PostRequest.StoryPermission.PUBLIC, true, execParam, execParam, marketParam, execParam);
    }

    private static class KakaoStoryResponseCallback<T> extends StoryResponseCallback<T> {
        @Override
        public void onNotKakaoStoryUser() {
            ShadowLog.e("hihi", "1");
        }

        @Override
        public void onSessionClosed(ErrorResult errorResult) {
            ShadowLog.e("hihi", "1");
        }

        @Override
        public void onNotSignedUp() {
            ShadowLog.e("hihi", "1");
        }

        @Override
        public void onSuccess(T result) {
            ShadowLog.e("hihi", "1");
        }
    }
}
