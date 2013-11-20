package net.eledge.android.eu.europeana.gui.view;

import android.content.Context;
import android.view.MotionEvent;
import android.webkit.WebView;

public class FocusableWebView extends WebView {

    public FocusableWebView(Context context) {
        super(context);
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
                if (!hasFocus())
                    requestFocus();
                break;
        }
        return super.onTouchEvent(ev);
    }
}
