package net.eledge.android.eu.europeana.gui.dialog;

import net.eledge.android.eu.europeana.R;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Bundle;

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
		StringBuilder sb = new StringBuilder("version:");
		sb.append(mInfo.versionName).append(" - build:");
		sb.append(mInfo.versionCode);
//		TextView text = (TextView) findViewById(R.id.textview_versioninfo);
//		text.setText(sb.toString());
	}

}
