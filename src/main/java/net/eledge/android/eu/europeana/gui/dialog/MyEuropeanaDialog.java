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
import android.view.LayoutInflater;
import android.widget.TextView;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.myeuropeana.task.GetProfileTask;
import net.eledge.android.toolkit.async.listener.TaskListener;

import org.springframework.social.europeana.api.model.Profile;

public class MyEuropeanaDialog extends DialogFragment implements TaskListener<Profile> {

    private EuropeanaApplication mApplication;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mApplication = (EuropeanaApplication) getActivity().getApplication();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_myeuropeana_title);
        builder.setView(inflater.inflate(R.layout.dialog_myeuropeana, null));
        builder.setPositiveButton(R.string.dialog_myeuropeana_button_close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                MyEuropeanaDialog.this.dismiss();
            }
        });
        builder.setNegativeButton(R.string.dialog_myeuropeana_button_logoff, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                mApplication.getConnectionRepository().removeConnections(mApplication.getEuropeanaConnectionFactory().getProviderId());
                MyEuropeanaDialog.this.dismiss();
            }
        });
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        new GetProfileTask(getActivity(), mApplication.getMyEuropeanaApi(), this).execute();
    }

    @Override
    public void onTaskStart() {

    }

    @Override
    public void onTaskFinished(Profile profile) {
        if (profile != null) {
            TextView email = (TextView) getDialog().findViewById(R.id.dialog_myeuropeana_textview_email);
            email.setText(profile.getEmail());
            TextView username = (TextView) getDialog().findViewById(R.id.dialog_myeuropeana_textview_username);
            username.setText(profile.getUserName());
            TextView saveditem = (TextView) getDialog().findViewById(R.id.dialog_myeuropeana_textview_saveditem);
            saveditem.setText(String.valueOf(profile.getNrOfSavedItems()));
            TextView savedsearch = (TextView) getDialog().findViewById(R.id.dialog_myeuropeana_textview_savedsearch);
            savedsearch.setText(String.valueOf(profile.getNrOfSavedSearches()));
            TextView tags = (TextView) getDialog().findViewById(R.id.dialog_myeuropeana_textview_tags);
            tags.setText(String.valueOf(profile.getNrOfSocialTags()));
        } else {
            mApplication.getConnectionRepository().removeConnections(mApplication.getEuropeanaConnectionFactory().getProviderId());
            MyEuropeanaDialog.this.dismiss();
        }
    }

}
