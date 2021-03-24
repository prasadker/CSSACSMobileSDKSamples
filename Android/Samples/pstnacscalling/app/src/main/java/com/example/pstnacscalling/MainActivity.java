package com.example.pstnacscalling;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.azure.android.communication.calling.Call;
import com.azure.android.communication.calling.CallAgent;
import com.azure.android.communication.calling.CallAgentOptions;
import com.azure.android.communication.calling.CallClient;
import com.azure.android.communication.calling.HangUpOptions;
import com.azure.android.communication.calling.StartCallOptions;
import com.azure.android.communication.calling.VideoOptions;
import com.azure.android.communication.common.CommunicationTokenCredential;
import com.azure.android.communication.common.PhoneNumberIdentifier;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String[] allPermissions = new String[] { Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE };
    private static final String UserToken = "<token>";

    TextView statusBar;
    private CallAgent callAgent;
    private Call call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getAllPermissions();
        createAgent();
        // Bind call button to call `startCall`
        Button callButton = findViewById(R.id.call_button);
        callButton.setOnClickListener(l -> startCall());

        Button hangupButton = findViewById(R.id.hangup_button);
        hangupButton.setOnClickListener(l -> endCall());

        statusBar = findViewById(R.id.status_bar);

    }

    private void startCall() {
        EditText calleeIdView = findViewById(R.id.callee_id);
        String calleeId = calleeIdView.getText().toString();
        if (calleeId.isEmpty()) {
            Toast.makeText(this, "Please enter callee", Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneNumberIdentifier callerPhone = new PhoneNumberIdentifier("<toll free>");
        StartCallOptions startCallOptions = new StartCallOptions();
        startCallOptions.setAlternateCallerId(callerPhone);
        startCallOptions.setVideoOptions(new VideoOptions(null));
        call = callAgent.startCall(getApplicationContext(), new PhoneNumberIdentifier[] {new PhoneNumberIdentifier(calleeId)}, startCallOptions);
    }

    /**
     * Ends the call previously started
     */
    private void endCall() {
        call.hangUp(new HangUpOptions());
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
     * Shows message in the status bar
     */
    private void setStatus(String status) {
        runOnUiThread(() -> statusBar.setText(status));
    }

    /**
     * Create the call agent
     */
    private void createAgent() {
        try {
            Log.i("MyApp", "Trying to create call agent");
            CallClient callClient = new CallClient();
            CommunicationTokenCredential communicationTokenCredential = new CommunicationTokenCredential(UserToken);
            android.content.Context appContext = this.getApplicationContext();
            CallAgentOptions callAgentOptions = new CallAgentOptions();
            callAgentOptions.setDisplayName("Alice");
            callAgent = callClient.createCallAgent(appContext, communicationTokenCredential, callAgentOptions).get();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Failed to create call agent.", Toast.LENGTH_SHORT).show();
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
