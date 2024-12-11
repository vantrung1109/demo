package digi.kitplay.ui.main.layout;


import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import lombok.Setter;
import timber.log.Timber;

public class TVRootLayout extends LinearLayout {
    private static final int TIME_BOUND = 5000; // 3 second

    @Setter
    private AdminEventListener listener;

    private int count = 0;
    private long lastTime = System.currentTimeMillis();

    public TVRootLayout(Context context) {
        super(context);
    }

    public TVRootLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TVRootLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TVRootLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Timber.d("touch");
        this.requestFocus();
        if (System.currentTimeMillis() - lastTime > TIME_BOUND) {
            lastTime = System.currentTimeMillis();
            count = 0;
        }
        count++;
        if (count == 7) {
            count = 0;
            lastTime = System.currentTimeMillis();
            if (listener != null) {
                Timber.d("------> call mListener");
                listener.onAdminEvent();
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    public interface AdminEventListener {
        void onAdminEvent();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Timber.d("event %d ",event.getAction());
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            if (System.currentTimeMillis() - lastTime > TIME_BOUND) {
                lastTime = System.currentTimeMillis();
                count = 0;
            }
            count++;
            if (count == 7) {
                count = 0;
                lastTime = System.currentTimeMillis();
                if (listener != null) {
                    Timber.d("------> call mListener");
                    listener.onAdminEvent();
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
