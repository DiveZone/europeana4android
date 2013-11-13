package net.eledge.android.eu.europeana.gui.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.R;

import org.springframework.social.europeana.api.Europeana;
import org.springframework.social.europeana.api.model.Profile;

public class MyEuropeanaActivity extends ActionBarActivity {
    private static final String TAG = MyEuropeanaActivity.class.getSimpleName();

    private EuropeanaApplication mApplication;

    private Europeana mEuropeanaApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = (EuropeanaApplication) getApplication();
        mEuropeanaApi = mApplication.getMyEuropeanaApi();
        setContentView(R.layout.activity_myeuropeana);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Button disconnectButton = (Button) findViewById(R.id.activity_myeuropeana_button_disconnect);
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApplication.getConnectionRepository().removeConnections(mApplication.getEuropeanaConnectionFactory().getProviderId());
                MyEuropeanaActivity.this.finish();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        new FetchProfileTask().execute();
    }

    public void showProfile(Profile profile) {
        if (profile != null) {
            TextView email = (TextView) findViewById(R.id.activity_myeuropeana_textview_email);
            email.setText(profile.getEmail());
            TextView username = (TextView) findViewById(R.id.activity_myeuropeana_textview_username);
            username.setText(profile.getUserName());
            TextView saveditem = (TextView) findViewById(R.id.activity_myeuropeana_textview_saveditem);
            saveditem.setText(String.valueOf(profile.getNrOfSavedItems()));
            TextView savedsearch = (TextView) findViewById(R.id.activity_myeuropeana_textview_savedsearch);
            savedsearch.setText(String.valueOf(profile.getNrOfSavedSearches()));
            TextView tags = (TextView) findViewById(R.id.activity_myeuropeana_textview_tags);
            tags.setText(String.valueOf(profile.getNrOfSocialTags()));
        }
    }

    private class FetchProfileTask extends AsyncTask<Void, Void, Profile> {

        @Override
        protected void onPreExecute() {
//            showProgressDialog("Fetching profile...");
        }

        @Override
        protected Profile doInBackground(Void... params) {
            try {
                return mEuropeanaApi.profileOperations().getProfile();
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Profile profile) {
//            dismissProgressDialog();
            showProfile(profile);
        }

    }
}
