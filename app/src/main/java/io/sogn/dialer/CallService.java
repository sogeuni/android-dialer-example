package io.sogn.dialer;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.telecom.Call;
import android.telecom.InCallService;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.M)
public class CallService extends InCallService {

    private static final String TAG = "CallService";

    @Override
    public void onCallAdded(Call call) {
        super.onCallAdded(call);
        Log.i(TAG, "onCallAdded: " + call);

        call.registerCallback(callCallback);
        startActivity(new Intent(this, CallActivity.class));
        CallManager.get().updateCall(call);
    }

    @Override
    public void onCallRemoved(Call call) {
        super.onCallRemoved(call);
        Log.i(TAG, "onCallRemoved: " + call);

        call.unregisterCallback(callCallback);
        CallManager.get().updateCall(null);
    }

    private Call.Callback callCallback = new Call.Callback() {

        @Override
        public void onStateChanged(Call call, int state) {
            Log.i(TAG, "Call.Callback onStateChanged: " + call + "state: " + state);
            CallManager.get().updateCall(call);
        }
    };
}
