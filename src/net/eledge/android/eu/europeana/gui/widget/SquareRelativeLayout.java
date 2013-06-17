package net.eledge.android.eu.europeana.gui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class SquareRelativeLayout extends RelativeLayout {
	
	public SquareRelativeLayout(Context context) {
		super(context);
	}
	
	public SquareRelativeLayout(Context context, AttributeSet attr) {
		super(context, attr);
	}

	public SquareRelativeLayout(Context context, AttributeSet attr, int resourceId) {
		super(context, attr, resourceId);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}

}
