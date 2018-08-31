/*
  Copyright 2014-2017 Kakao Corp.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.kakao.auth.authorization.authcode;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.kakao.auth.KakaoSDK;
import com.kakao.auth.R;
import com.kakao.auth.StringSet;
import com.kakao.auth.exception.KakaoWebviewException;
import com.kakao.auth.receiver.SmsReceiver;
import com.kakao.network.ServerProtocol;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.CommonProtocol;
import com.kakao.util.helper.SystemInfo;
import com.kakao.util.helper.log.Logger;

public class KakaoWebViewActivity extends Activity {

    private static SmsReceiver smsReceiver;

    public static final int RESULT_SUCCESS = 0;
    public static final int RESULT_ERROR = 1;

    public static final String KEY_REDIRECT_URL = "key.redirect.url";
    public static final String KEY_EXCEPTION = "key.exception";

    public static final String KEY_URL = "key.url";
    public static final String KEY_EXTRA_HEADERS = "key.extra.headers";
    public static final String KEY_USE_WEBVIEW_TIMERS = "key.use.webview.timers";
    public static final String KEY_USE_SMS_RECEIVER = "key.use.sms.receiver";
    public static final String KEY_RESULT_RECEIVER = "key.result.receiver";

    private String url;
    private final Map<String, String> headers = new HashMap<String, String>();
    private boolean useWebViewTimers;
    private boolean useSmsReceiver;
    private ResultReceiver resultReceiver;

    private WebView webView;
    private ProgressBar progressBar;

    private boolean shouldProceedWithSslError;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, KakaoWebViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            super.onCreate(savedInstanceState);

            parseIntent(getIntent());

            setContentView(R.layout.activity_kakao_webview);
            initUi();
            registerSmsReceiverIfNeeded();
        } catch (Exception e) {
            sendErrorToListener(e);
            finish();
        }
    }

    private void parseIntent(Intent intent) {
        url = intent.getStringExtra(KEY_URL);
        useWebViewTimers = intent.getBooleanExtra(KEY_USE_WEBVIEW_TIMERS, false);
        useSmsReceiver = intent.getBooleanExtra(KEY_USE_SMS_RECEIVER, false);
        resultReceiver = intent.getParcelableExtra(KEY_RESULT_RECEIVER);
        Bundle extraHeaders = intent.getParcelableExtra(KEY_EXTRA_HEADERS);
        headers.put(CommonProtocol.KA_HEADER_KEY, SystemInfo.getKAHeader());
        if (extraHeaders != null && !extraHeaders.isEmpty()) {
            for (String key : extraHeaders.keySet()) {
                headers.put(key, extraHeaders.getString(key));
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        parseIntent(intent);

        setProgressBarVisibility(View.VISIBLE);
        webView.loadUrl(url, headers);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (useWebViewTimers) {
            webView.resumeTimers();
        }
    }

    @Override
    protected void onPause() {
        if (useWebViewTimers) {
            webView.pauseTimers();
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }

        sendCancelToListener();
        super.onBackPressed();
    }

    @Override
    public void finish() {
        // prevent default animation on some device
        overridePendingTransition(0, 0);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        unRegisterSmsReceiverIfNeeded();
        super.onDestroy();
    }

    private void sendCancelToListener() {
        sendErrorToListener(new KakaoException(KakaoException.ErrorType.CANCELED_OPERATION, "pressed back button or cancel button during requesting auth code."));
    }

    private void sendSuccessToListener(String redirectURL) {
        if (resultReceiver != null) {
            Bundle bundle = new Bundle();
            bundle.putString(KEY_REDIRECT_URL, redirectURL);
            resultReceiver.send(RESULT_SUCCESS, bundle);
        }
    }

    private void sendErrorToListener(Throwable error) {
        if (resultReceiver != null) {
            Bundle bundle = new Bundle();
            KakaoException kakaoException;
            if (error instanceof KakaoException) {
                kakaoException = (KakaoException) error;
            } else {
                kakaoException = new KakaoException(error.getMessage(), error);
            }
            bundle.putSerializable(KEY_EXCEPTION, kakaoException);
            resultReceiver.send(RESULT_ERROR, bundle);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @SuppressWarnings("deprecation")
    private void initUi() {

        webView = findViewById(R.id.webview);
        progressBar = findViewById(R.id.progress_bar);

        webView.setBackgroundResource(android.R.color.white);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new KakaoWebViewClient());
        webView.setWebChromeClient(new KakaoWebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSaveFormData(KakaoSDK.getAdapter().getSessionConfig().isSaveFormData());
        webView.getSettings().setSavePassword(false);

        setProgressBarVisibility(View.VISIBLE);
        webView.loadUrl(url, headers);
    }

    private void setProgressBarVisibility(int visibility) {
        if (!isFinishing()) {
            progressBar.setVisibility(visibility);
        }
    }

    private class KakaoWebViewClient extends android.webkit.WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Logger.d("(Deprecated) Redirect URL: " + url);
            return handleShouldOverrideUrlLoading(url);
        }

        @RequiresApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Logger.d("Redirect URL:" + request.getUrl());
            return handleShouldOverrideUrlLoading(request.getUrl().toString());
        }

        private boolean handleShouldOverrideUrlLoading(final String url) {
            // redirect uri
            if (url.startsWith(StringSet.REDIRECT_URL_PREFIX) && (url.contains(StringSet.REDIRECT_URL_POSTFIX) || url.contains(StringSet.AGEAUTH_REDIRECT_URL_POSTFIX))){
                sendSuccessToListener(url);
                finish();
            } else if (url.contains(ServerProtocol.AUTH_AUTHORITY) || url.contains(ServerProtocol.API_AUTHORITY) || url.contains(ServerProtocol.AGE_AUTH_AUTHORITY)) {
                // 로그인창, 동의창
                webView.loadUrl(url, headers);
            } else {
                //full browser!!!
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) return;
            super.onReceivedError(view, errorCode, description, failingUrl);
            setProgressBarVisibility(View.GONE);
            sendErrorToListener(new KakaoWebviewException(errorCode, description, failingUrl));
            finish();
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            setProgressBarVisibility(View.GONE);
            sendErrorToListener(new KakaoWebviewException(error.getErrorCode(), error.getDescription() != null ? error.getDescription().toString() : null, request.getUrl().toString()));
            finish();
        }

        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, final SslError error) {
            setProgressBarVisibility(View.GONE);
            AlertDialog.Builder builder = new AlertDialog.Builder(KakaoWebViewActivity.this);
            builder.setTitle(getString(R.string.title_for_ssl_warning));
            builder.setMessage(getString(R.string.message_for_ssl_warning));

            shouldProceedWithSslError = false;
            builder.setNegativeButton(getString(R.string.button_for_ssl_go_back), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.setPositiveButton(getString(R.string.button_for_ssl_go_forward), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    shouldProceedWithSslError = true;
                    handler.proceed();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (!shouldProceedWithSslError) {
                        // user pressed back button or cancel button in SSL error alert dialog. Finish web view and pass SSL error to listener.
                        handler.cancel();
                        sendErrorToListener(new KakaoWebviewException(ERROR_FAILED_SSL_HANDSHAKE, error == null ? null : error.toString(), null));
                        KakaoWebViewActivity.this.finish();
                    }
                }
            });
            if (!KakaoWebViewActivity.this.isFinishing()) {
                alertDialog.show();
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Logger.d("Webview loading URL: " + url);
            super.onPageStarted(view, url, favicon);
            setProgressBarVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            setProgressBarVisibility(View.GONE);
        }
    }

    /**
     * KakaoWebChromeClient
     */
    private class KakaoWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            new AlertDialog.Builder(KakaoWebViewActivity.this).setMessage(message).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm();
                }
            }).setCancelable(false).create().show();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {

            String msg = null;
            String positive = null;
            String negative = null;

            try {
                JSONObject object = new JSONObject(message);

                msg = object.optString("message");
                positive = object.optString("positive");
                negative = object.optString("negative");

            } catch (JSONException e) {
                Logger.e("JSONException: " + e.getMessage());
            } finally {

                msg = TextUtils.isEmpty(msg) ? message : msg;
                positive = TextUtils.isEmpty(positive) ? getString(android.R.string.ok) : positive;
                negative = TextUtils.isEmpty(negative) ? getString(android.R.string.cancel) : negative;

                new AlertDialog.Builder(KakaoWebViewActivity.this)
                        .setMessage(msg)
                        .setPositiveButton(positive, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        }).setNegativeButton(negative, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                    }
                }).setCancelable(false).create().show();
            }

            return true;
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Logger.d("KakaoAccountWebView", consoleMessage.message()
                    + " -- (" + consoleMessage.lineNumber() + "/" + consoleMessage.sourceId() + ")");
            return true;
        }
    }

    private void registerSmsReceiverIfNeeded() {
        if (!useSmsReceiver) {
            return;
        }

        if (smsReceiver != null) {
            return;
        }
        Logger.d("registerSmsReceiver");

        smsReceiver = new SmsReceiver(new SmsReceiver.ISmsReceiver() {
            @Override
            public void onCompleteSms(String code) {
                Logger.d("++ onCompleteSms(%s)", code);
                if (!TextUtils.isEmpty(code)) {
                    final String url = String.format(Locale.US, "javascript:insertSms('%s')", code);
                    Logger.d("++ command : " + url);
                    webView.loadUrl(url);
                }
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(SmsReceiver.ACTION);
        filter.setPriority(999);  // 행아웃에서 SMS수신 가능한 경우에도 SMS를 받으려면 priority를 3보다 높아야 한다.
        registerReceiver(smsReceiver, filter);
    }

    private void unRegisterSmsReceiverIfNeeded() {
        if (smsReceiver != null) {
            try {
                Logger.d("unregisterSmsReceiver");
                unregisterReceiver(smsReceiver);
            } catch (Exception ignore) {
            }
            smsReceiver = null;
        }
    }
}
