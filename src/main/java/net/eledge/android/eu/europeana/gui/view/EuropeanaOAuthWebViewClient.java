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

    /*
                 * The WebViewClient has another method called shouldOverridUrlLoading which does not capture the javascript
                 * redirect to the success page. So we're using onPageStarted to capture the url.
                 */
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        // parse the captured url
        Uri uri = Uri.parse(url);
        Log.d(TAG, url);

                        /*
                         * The access token is returned in the URI fragment of the URL. See the Desktop Apps section all the way
                         * at the bottom of this link:
                         *
                         * http://developers.facebook.com/docs/authentication/
                         *
                         * The fragment will be formatted like this:
                         *
                         * #access_token=A&expires_in=0
                         */
        AccessGrant accessGrant = createAccessGrantFromUriFragment(uri.getFragment());
        if (accessGrant != null) {
            new CreateConnectionTask().execute(accessGrant);
        }

                        /*
                         * if there was an error with the oauth process, return the error description
                         *
                         * The error query string will look like this:
                         *
                         * ?error_reason=user_denied&error=access_denied&error_description=The +user+denied+your+request
                         */
        if (uri.getQueryParameter("error") != null) {
            CharSequence errorReason = uri.getQueryParameter("error_description").replace("+", " ");
            Toast.makeText(mActivity.getApplicationContext(), errorReason, Toast.LENGTH_LONG).show();
            //displayFacebookMenuOptions();
        }
    }

    private AccessGrant createAccessGrantFromUriFragment(String uriFragment) {
        // confirm we have the fragment, and it has an access_token parameter
        if (uriFragment != null && uriFragment.startsWith("access_token=")) {

                                /*
                                 * The fragment also contains an "expires_in" parameter. In this
                                 * example we requested the offline_access permission, which
                                 * basically means the access will not expire, so we're ignoring
                                 * it here
                                 */
            try {
                // split to get the two different parameters
                String[] params = uriFragment.split("&");

                // split to get the access token parameter and value
                String[] accessTokenParam = params[0].split("=");

                // get the access token value
                String accessToken = accessTokenParam[1];

                // create the connection and persist it to the repository
                return new AccessGrant(accessToken);
            } catch (Exception e) {
                // don't do anything if the parameters are not what is expected
                Log.d(TAG, e.getLocalizedMessage(), e);
            }
        }
        return null;
    }

    private class CreateConnectionTask extends AsyncTask<AccessGrant, Void, Void> {

        @Override
        protected Void doInBackground(AccessGrant... params) {
            if (params[0] != null) {
                // this method makes a network request to Facebook, so it must run off of the UI thread
                Connection<Europeana> connection = mApplication.getEuropeanaConnectionFactory().createConnection(params[0]);
                try {
                    // persist connection to the repository
                    mApplication.getConnectionRepository().addConnection(connection);
                } catch (DuplicateConnectionException e) {
                    // connection already exists in repository!
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
