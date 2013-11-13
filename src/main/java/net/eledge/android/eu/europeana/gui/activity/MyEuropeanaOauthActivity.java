package net.eledge.android.eu.europeana.gui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.gui.view.EuropeanaOAuthWebViewClient;

import org.springframework.social.oauth2.GrantType;

public class MyEuropeanaOauthActivity extends ActionBarActivity {

    private EuropeanaApplication mApplication;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = (EuropeanaApplication) getApplication();
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
        this.webView = new WebView(this);
        setContentView(webView);



        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                MyEuropeanaOauthActivity.this.setTitle("Loading...");
                MyEuropeanaOauthActivity.this.setProgress(progress * 100);
                if (progress == 100) {
                    MyEuropeanaOauthActivity.this.setTitle(R.string.app_name);
                }
            }
        });

        // Using a custom web view client to capture the access token
        this.webView.setWebViewClient(new EuropeanaOAuthWebViewClient(this));
    }

    @Override
    public void onStart() {
        super.onStart();
        this.webView.loadUrl(mApplication.getEuropeanaConnectionFactory().getOAuthOperations().buildAuthorizeUrl(GrantType.IMPLICIT_GRANT, null));
    }

    @Override
    protected void onResume() {
        super.onResume();
        CookieManager.getInstance().removeAllCookie();
    }


}
