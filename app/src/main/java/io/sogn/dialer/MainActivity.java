package io.sogn.dialer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telecom.TelecomManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_SET_DEFAULT_DIALER = 100;
    private static final int REQUEST_CODE_GRANT_PERMISSIONS = 200;

    private static String[] sRequiredPermissions = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.MANAGE_OWN_CALLS
    };

    private EditText mTextNumber;
    private Button mButtonCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextNumber = findViewById(R.id.textNumber);
        mButtonCall = findViewById(R.id.buttonCall);

        mButtonCall.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermissions();
        }
        checkDefaultDialer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SET_DEFAULT_DIALER) {
            checkSetDefaultDialerResult(resultCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (REQUEST_CODE_GRANT_PERMISSIONS == requestCode) {
            int check = 0;

            for (Integer result : grantResults) {
                check += result;
            }

            if (check < 0) {
                Toast.makeText(this, "need more permissions", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonCall:

                String number = mTextNumber.getText().toString();

                if (number.length() > 0) {
                    CallManager.get().placeCall(number);
                }
                break;
        }
    }

    /**
     * 기본 Dialer를 체크하고 변경
     */
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
                message = "기본 전화 앱으로 설정하였습니다.";
                break;
            case RESULT_CANCELED:
                message = "기본 전화 앱으로 설정하지 않았습니다.";
                break;
            default:
                message = "Unexpected result code " + resultCode;
        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void checkPermissions() {
        if (ifNeededRequestPermission()) {
            ActivityCompat.requestPermissions(this, sRequiredPermissions, REQUEST_CODE_GRANT_PERMISSIONS);
        }
    }

    private boolean ifNeededRequestPermission() {
        int check = 0;

        /**
         * 하나라도 permission이 없으면 check는 음수가 됨
         */
        for (String permission : sRequiredPermissions) {
            check += ActivityCompat.checkSelfPermission(this, permission);
        }

        return (check < 0);
    }
}
