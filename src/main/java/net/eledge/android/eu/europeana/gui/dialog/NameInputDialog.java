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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import net.eledge.android.eu.europeana.R;

import org.apache.commons.lang3.StringUtils;

public class NameInputDialog extends DialogFragment {

    private final int resTitle;
    private final int resText;
    private final int resInput;
    private final int resPositiveButton;
    private final NameInputDialogResponse response;
    private AlertDialog mDialog;

    private EditText mInput;

    public NameInputDialog(int resTitle, int resText, int resInput, int resPositiveButton, NameInputDialogResponse response) {
        this.resTitle = resTitle;
        this.resText = resText;
        this.resInput = resInput;
        this.resPositiveButton = resPositiveButton;
        this.response = response;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_nameinput, null);
        TextView text = (TextView) view.findViewById(R.id.dialog_nameinput_textview);
        if (resText != -1) {
            text.setText(resText);
        } else {
            text.setVisibility(View.GONE);
        }
        mInput = (EditText) view.findViewById(R.id.dialog_nameinput_edittext);
        mInput.setHint(resInput);
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                mDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(StringUtils.isNotBlank(s.toString()));
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        if (resTitle != -1) {
            builder.setTitle(resTitle);
        }
        builder.setPositiveButton(resPositiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                String input = mInput.getText().toString();
                if (StringUtils.isNotBlank(input)) {
                    response.positiveResponse(input);
                    NameInputDialog.this.dismiss();
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                response.negativeResponse();
                mDialog.dismiss();
            }
        });
        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        return mDialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        mDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
    }

    public interface NameInputDialogResponse {
        void positiveResponse(String input);

        void negativeResponse();
    }
}
