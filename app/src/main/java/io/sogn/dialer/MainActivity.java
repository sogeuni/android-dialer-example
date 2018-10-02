package io.sogn.dialer;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SET_DEFAULT_DIALER = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkDefaultDialer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SET_DEFAULT_DIALER) {
            checkSetDefaultDialerResult(resultCode);
        }
    }

    private void checkDefaultDialer() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        TelecomManager telecomManager = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
        boolean isAlreadyDefaultDialer;

        try {
            isAlreadyDefaultDialer = telecomManager.getDefaultDialerPackage().equals(getPackageName());
        } catch(NullPointerException e) {
            isAlreadyDefaultDialer = false;
        }

        if (isAlreadyDefaultDialer) {
            return;
        }

        Intent intent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER);
        intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, getPackageName());
        startActivityForResult(intent, REQUEST_CODE_SET_DEFAULT_DIALER);
    }

    private void checkSetDefaultDialerResult(int resultCode) {
        String message;

        switch(resultCode) {
            case RESULT_OK:
                message = "User accepted request to become default dialer";
                break;
            case RESULT_CANCELED:
                message = "User declined request to become default dialer";
                break;
            default:
                message = "Unexpected result code " + resultCode;
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
