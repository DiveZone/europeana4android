package net.eledge.android.eu.europeana.gui.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import net.eledge.android.eu.europeana.EuropeanaApplication;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.europeana.api.Europeana;
import org.springframework.social.oauth2.AccessGrant;

public class EuropeanaOAuthWebViewClient extends WebViewClient {

    private static final String TAG = EuropeanaOAuthWebViewClient.class.getSimpleName();

    private Activity mActivity;

    private EuropeanaApplication mApplication;

    public EuropeanaOAuthWebViewClient(Activity activity) {
        super();
        mActivity = activity;
        mApplication = (EuropeanaApplication) mActivity.getApplication();
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        Uri uri = Uri.parse(url);
        Log.d(TAG, url);
        AccessGrant accessGrant = createAccessGrantFromUriFragment(uri.getFragment());
        if (accessGrant != null) {
            new CreateConnectionTask().execute(accessGrant);
        }
        if (uri.getQueryParameter("error") != null) {
            CharSequence errorReason = uri.getQueryParameter("error_description").replace("+", " ");
            Toast.makeText(mActivity.getApplicationContext(), errorReason, Toast.LENGTH_LONG).show();
            //displayFacebookMenuOptions();
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.d(TAG, url);
        view.loadUrl(url);
        return false;
    }

    private AccessGrant createAccessGrantFromUriFragment(String uriFragment) {
        // confirm we have the fragment, and it has an access_token parameter
        if (uriFragment != null && uriFragment.startsWith("access_token=")) {
            try {
                String[] params = uriFragment.split("&");
                String[] accessTokenParam = params[0].split("=");
                String accessToken = accessTokenParam[1];
                return new AccessGrant(accessToken);
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage(), e);
            }
        }
        return null;
    }

    private class CreateConnectionTask extends AsyncTask<AccessGrant, Void, Void> {

        @Override
        protected Void doInBackground(AccessGrant... params) {
            if (params[0] != null) {
                Connection<Europeana> connection = mApplication.getEuropeanaConnectionFactory().createConnection(params[0]);
                try {
                    mApplication.getConnectionRepository().addConnection(connection);
                } catch (DuplicateConnectionException e) {
                    Log.d(TAG, e.getLocalizedMessage(), e);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mActivity.finish();
            //displayFacebookMenuOptions();
        }

    }
}
