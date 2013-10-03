package net.eledge.android.eu.europeana.gui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.R;

public class AboutDialog extends Dialog {
	
	private PackageInfo mInfo;
	
	private Typeface mEuropeanaFont;
	
	public AboutDialog(Context context, EuropeanaApplication application, PackageInfo packageInfo) {
		super(context, R.style.dialog_hidetitle);
		mInfo = packageInfo;
		mEuropeanaFont = application.getEuropeanaFont();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.dialog_about);
		setVersionNumber();
	}
	
	private void setVersionNumber() {
		TextView logo = (TextView) findViewById(R.id.dialog_about_textview_icon);
		logo.setText("a");
		logo.setTypeface(mEuropeanaFont);
		TextView version = (TextView) findViewById(R.id.dialog_about_textview_version);
		version.setText(mInfo.versionName);
		TextView build = (TextView) findViewById(R.id.dialog_about_textview_build);
		build.setText("build:" + mInfo.versionCode);
	}

}
