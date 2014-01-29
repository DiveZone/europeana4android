/*
 * Copyright (c) 2014 eLedge.net and the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.eledge.android.eu.europeana.gui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.gui.view.EuropeanaOAuthWebViewClient;
import net.eledge.android.eu.europeana.gui.view.FocusableWebView;

import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Parameters;

public class MyEuropeanaOauthDialog extends DialogFragment {

    private EuropeanaApplication mApplication;
    private WebView webView;
    private Dialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = (EuropeanaApplication) getActivity().getApplication();
        webView = new FocusableWebView(getActivity());
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    MyEuropeanaOauthDialog.this.getDialog().setTitle(R.string.app_name);
                } else {
                    MyEuropeanaOauthDialog.this.getDialog().setTitle("Loading...");
                }
            }
        });
        this.webView.setWebViewClient(new EuropeanaOAuthWebViewClient(getActivity(), new EuropeanaOAuthWebViewClient.AuthorisationListener() {
            @Override
            public void finished() {
                mDialog.dismiss();
            }
        }));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_myeuropeana_title);
        builder.setView(webView);
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyEuropeanaOauthDialog.this.webView.stopLoading();
                dialog.dismiss();
            }
        });
        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        return mDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        OAuth2Parameters params = new OAuth2Parameters();
        params.setRedirectUri("http://www.europeana.eu/api/index.html");
        this.webView.loadUrl(mApplication.getEuropeanaConnectionFactory().getOAuthOperations().buildAuthorizeUrl(GrantType.IMPLICIT_GRANT, params));
    }

    @Override
    public void onResume() {
        super.onResume();
        CookieManager.getInstance().removeAllCookie();
    }


}
