package io.sogn.dialer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Incomming / Outgoing Call 화면
 */
public class CallActivity extends Activity implements View.OnClickListener, CallManager.StateListener, Handler.Callback {

    private static final String TAG = "CallActivity";

    private static final long PERIOD_MILLIS = 1000L;
    private static final int MSG_UPDATE_ELAPSEDTIME = 100;

    private Timer mTimer;
    private long mElapsedTime;
    private Handler mHandler;

    private TextView mTextStatus;
    private TextView mTextDuration;
    private TextView mTextDisplayName;
    private ImageView mButtonHangup;
    private ImageView mButtonAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        hideBottomNavigationBar();

        mTextStatus = findViewById(R.id.textStatus);
        mTextDuration = findViewById(R.id.textDuration);
        mTextDisplayName = findViewById(R.id.textDisplayName);

        mButtonHangup = findViewById(R.id.buttonHangup);
        mButtonAnswer = findViewById(R.id.buttonAnswer);

        mButtonHangup.setOnClickListener(this);
        mButtonAnswer.setOnClickListener(this);

        updateView(CallManager.get().getUiCall());

        mHandler = new Handler(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CallManager.get().registerListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CallManager.get().unregisterListener();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonHangup:
                CallManager.get().cancelCall();
                break;
            case R.id.buttonAnswer:
                CallManager.get().acceptCall();
                break;
        }

    }

    private void hideBottomNavigationBar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onCallStateChanged(UiCall uiCall) {
        updateView(uiCall);
    }

    /**
     * 현재 전화 상태에 따라 view 모양 변경
     *
     * @param uiCall
     */
    private void updateView(UiCall uiCall) {
        mTextStatus.setVisibility(
                (uiCall.getStatus() == UiCall.Status.ACTIVE) ? View.GONE : View.VISIBLE
        );

        mTextStatus.setText(uiCall.getStatus().toString());

        mTextDuration.setVisibility(
                (uiCall.getStatus() == UiCall.Status.ACTIVE) ? View.VISIBLE : View.GONE
        );

        mButtonHangup.setVisibility(
                (uiCall.getStatus() == UiCall.Status.DISCONNECTED) ? View.GONE : View.VISIBLE
        );

        if (uiCall.getStatus() == UiCall.Status.DISCONNECTED) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 3000);
        }

        if (uiCall.getStatus() == UiCall.Status.ACTIVE) {
            startTimer();
        } else if (uiCall.getStatus() == UiCall.Status.DISCONNECTED) {
            stopTimer();
        }

        mTextDisplayName.setText(uiCall.getDisplayName());

        mButtonAnswer.setVisibility(
                (uiCall.getStatus() == UiCall.Status.RINGING) ? View.VISIBLE : View.GONE
        );
    }

    private void startTimer() {
        stopTimer();

        mElapsedTime = 0L;
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mElapsedTime++;
                mHandler.sendEmptyMessage(MSG_UPDATE_ELAPSEDTIME);
            }
        }, 0, PERIOD_MILLIS);
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private String toDurationString(long time) {
        return String.format(Locale.US, "%02d:%02d:%02d", time / 3600, (time % 3600) / 60, (time % 60));
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_UPDATE_ELAPSEDTIME:
                mTextDuration.setText(toDurationString(mElapsedTime));
                break;
        }
        return true;
    }
}
