package io.sogn.dialer;

import android.annotation.TargetApi;
import android.os.Build;
import android.telecom.Call;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.M)
public class CallManager {
    private static final String TAG = "CallManager";

    private static CallManager sInstance = null;
    private Call mCurrentCall = null;

    public static CallManager get() {
        if (sInstance == null) {
            sInstance = new CallManager();
        }
        return sInstance;
    }

    private CallManager() {

    }

    public void updates() {

    }

    public void updateCall(Call call) {
        mCurrentCall = call;
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
