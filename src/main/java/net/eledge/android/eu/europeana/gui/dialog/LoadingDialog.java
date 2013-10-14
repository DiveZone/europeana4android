package net.eledge.android.eu.europeana.gui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import net.eledge.android.eu.europeana.R;

public class LoadingDialog extends Dialog {

	public LoadingDialog(Context context) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.dialog_loading);
	}
	
}
