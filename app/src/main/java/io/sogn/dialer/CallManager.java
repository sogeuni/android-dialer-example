package io.sogn.dialer;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.telecom.Call;
import android.telecom.TelecomManager;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.M)
public class CallManager {
    private static final String TAG = "CallManager";

    public interface StateListener {
        void onCallStateChanged(UiCall call);
    }

    private static CallManager sInstance = null;

    private TelecomManager mTelecomManager;
    private Call mCurrentCall = null;
    private StateListener mStateListener = null;

    public static CallManager init(Context applicationContext) {
        if (sInstance == null) {
            sInstance = new CallManager(applicationContext);
        } else {
            throw new IllegalStateException("CallManager has been initialized.");
        }
        return sInstance;
    }

    public static CallManager get() {
        if (sInstance == null) {
            throw new IllegalStateException("Call CallManager.init(Context) before calling this function.");
        }
        return sInstance;
    }

    private CallManager(Context context) {
        Log.i(TAG, "init CallManager");

        mTelecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
    }

    public void registerListener(StateListener listener) {
        mStateListener = listener;
    }

    public void unregisterListener() {
        mStateListener = null;
    }

    public UiCall getUiCall() {
        return UiCall.convert(mCurrentCall);
    }

    public void updateCall(Call call) {
        mCurrentCall = call;

        if (mStateListener != null && mCurrentCall != null) {
            mStateListener.onCallStateChanged(UiCall.convert(mCurrentCall));
        }
    }

    public void placeCall(String number) {
        Uri uri = Uri.fromParts("tel", number, null);
        mTelecomManager.placeCall(uri, null);
    }

    public void cancelCall() {
        if (mCurrentCall != null) {
            if (mCurrentCall.getState() == Call.STATE_RINGING) {
                rejectCall();
            } else {
                disconnectCall();
            }
        }
    }

    public void acceptCall() {
        Log.i(TAG, "acceptCall");

        if (mCurrentCall != null) {
            mCurrentCall.answer(mCurrentCall.getDetails().getVideoState());
        }
    }

    private void rejectCall() {
        Log.i(TAG, "rejectCall");

        if (mCurrentCall != null) {
            mCurrentCall.reject(false, "");
        }
    }

    private void disconnectCall() {
        Log.i(TAG, "disconnectCall");

        if (mCurrentCall != null) {
            mCurrentCall.disconnect();
        }
    }
}
