package com.example.smsacs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.azure.communication.common.PhoneNumber;
import com.azure.communication.sms.SmsClient;
import com.azure.communication.sms.SmsClientBuilder;
import com.azure.communication.sms.models.SendSmsOptions;
import com.azure.communication.sms.models.SendSmsResponse;
import com.azure.core.http.HttpClient;
import com.azure.core.http.netty.NettyAsyncHttpClientBuilder;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String[] allPermissions = new String[] { Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE };

    private String connectionString = "endpoint=https://prasad-testacs.communication.azure.com/;accesskey=ymzvlgn4KiMq1cylbzEXbpFkQarvvH0Ps9ULSLdMidx4V4klv7++SZfa9y4pgx86MdkwKkdX5GI8glGz4fW6BA==";
    private SmsClient smsClient;
    EditText phoneText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart(){
        super.onStart();

        getAllPermissions();
        createSmsClient();

        phoneText = findViewById(R.id.phoneid);
        phoneText.setText("+12672257727");

        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(l -> sendSMS());
    }

    private void sendSMS() {

        String phoneId = phoneText.getText().toString();
        if (phoneId.isEmpty()) {
            Toast.makeText(this, "Please enter callee", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText msg = findViewById(R.id.msgText);
        String msgText = msg.getText().toString();
        if (msgText.isEmpty()) {
            Toast.makeText(this, "Please enter SMS text", Toast.LENGTH_SHORT).show();
            return;
        }

        List<PhoneNumber> to = new ArrayList<PhoneNumber>();
        to.add(new PhoneNumber(phoneId));

        SendSmsOptions options = new SendSmsOptions();
        options.setEnableDeliveryReport(true);

        SendSmsResponse response = smsClient.sendMessage(new PhoneNumber("+18332153352"),
                                   to,
                                    msgText,
                                    options);

        Log.i("My App", "Message ID: " + response.getMessageId());

        if (!response.getMessageId().isEmpty()) {
            Toast.makeText(this, "SMS sent successfully", Toast.LENGTH_SHORT).show();
        }

    }

    private void createSmsClient() {

        // Instantiate the http client
        HttpClient httpClient = new NettyAsyncHttpClientBuilder().build();

        smsClient = new SmsClientBuilder()
                .connectionString(connectionString)
                .httpClient(httpClient)
                .buildClient();

    }


    /**
     * Ensure all permissions were granted, otherwise inform the user permissions are missing.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        boolean allPermissionsGranted = true;
        for (int result : grantResults) {
            allPermissionsGranted &= (result == PackageManager.PERMISSION_GRANTED);
        }
        if (!allPermissionsGranted) {
            Toast.makeText(this, "All permissions are needed to make the call.", Toast.LENGTH_LONG).show();
            finish();
        }
    }


    /**
     * Request each required permission if the app doesn't already have it.
     */
    private void getAllPermissions() {
        ArrayList<String> permissionsToAskFor = new ArrayList<>();
        for (String permission : allPermissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToAskFor.add(permission);
            }
        }
        if (!permissionsToAskFor.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToAskFor.toArray(new String[0]), 1);
        }
    }
}