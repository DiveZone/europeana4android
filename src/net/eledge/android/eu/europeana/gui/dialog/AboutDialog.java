package net.eledge.android.eu.europeana.gui.dialog;

import net.eledge.android.eu.europeana.R;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.widget.TextView;

public class AboutDialog extends Dialog {
	
	private PackageInfo mInfo;
	
	public AboutDialog(Context context, PackageInfo packageInfo) {
		super(context, R.style.dialog_hidetitle);
		mInfo = packageInfo;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.dialog_about);
		setVersionNumber();
	}
	
	private void setVersionNumber() {
		TextView version = (TextView) findViewById(R.id.dialog_about_version);
		version.setText(mInfo.versionName);
		TextView build = (TextView) findViewById(R.id.dialog_about_build);
		build.setText("build:" + mInfo.versionCode);
	}

}
