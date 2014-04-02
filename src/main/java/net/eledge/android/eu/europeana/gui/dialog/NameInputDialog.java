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

import android.app.Activity;
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

import java.io.Serializable;

public class NameInputDialog extends DialogFragment {

    public static final String KEY_RESTITLE_INT = "resTitle";
    public static final String KEY_RESTEXT_INT = "resText";
    public static final String KEY_RESINPUT_INT = "resInput";
    public static final String KEY_RESPOSBUTTON_INT = "resPosButton";

    private int resTitle;
    private int resText;
    private int resInput;
    private int resPositiveButton;
    private NameInputDialogListener mListener;
    private AlertDialog mDialog;

    private EditText mInput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.resTitle = savedInstanceState.getInt(KEY_RESTITLE_INT);
        this.resText = savedInstanceState.getInt(KEY_RESTEXT_INT);
        this.resInput = savedInstanceState.getInt(KEY_RESINPUT_INT);
        this.resPositiveButton = savedInstanceState.getInt(KEY_RESPOSBUTTON_INT);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (NameInputDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NameInputDialogListener");
        }
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
                    mListener.positiveResponse(input);
                    NameInputDialog.this.dismiss();
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                mListener.negativeResponse();
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

    public interface NameInputDialogListener extends Serializable {
        void positiveResponse(String input);

        void negativeResponse();
    }
}
