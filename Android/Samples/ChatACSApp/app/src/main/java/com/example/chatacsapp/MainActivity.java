package com.example.chatacsapp;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.azure.android.communication.chat.ChatAsyncClient;
import com.azure.android.communication.chat.ChatThreadAsyncClient;
import com.azure.android.communication.chat.models.ChatMessageType;
import com.azure.android.communication.chat.models.ChatParticipant;
import com.azure.android.communication.chat.models.ChatThread;
import com.azure.android.communication.chat.models.CommunicationIdentifierModel;
import com.azure.android.communication.chat.models.CreateChatThreadRequest;
import com.azure.android.communication.chat.models.CreateChatThreadResult;
import com.azure.android.communication.chat.models.SendChatMessageRequest;
import com.azure.android.core.http.Callback;
import com.azure.android.core.http.HttpHeader;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    final String endpoint = "https://prasad-testacs.communication.azure.com";
    final String userAccessToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEwMiIsIng1dCI6IjNNSnZRYzhrWVNLd1hqbEIySmx6NTRQVzNBYyIsInR5cCI6IkpXVCJ9.eyJza3lwZWlkIjoiYWNzOmJhOTRlMDI4LTMxYzEtNDJkZC1hYzA5LTRhZWY0ZTJhMmQyOF8wMDAwMDAwOC1hZWUxLTgwNmItOTgwNi0xMTNhMGQwMDY5NDkiLCJzY3AiOjE3OTIsImNzaSI6IjE2MTUxNTkzNTIiLCJpYXQiOjE2MTUxNTkzNTIsImV4cCI6MTYxNTI0NTc1MiwiYWNzU2NvcGUiOiJjaGF0IiwicmVzb3VyY2VJZCI6ImJhOTRlMDI4LTMxYzEtNDJkZC1hYzA5LTRhZWY0ZTJhMmQyOCJ9.lwUZDRv-cCv3oK7OMbwnkRHnzJcmAcyGNteri34zLvFgPip_MbyLhg0Yo3Td0K_JWYHv2QqeIUogVmxJg-4RorRCVeNwYT_YiIizRgj-_bXP2-9JQ9WIxVE-LwdB_2MKY6miki9PkAavokNRVFL1bJhrnc3IRRTbVCrByx89NZ3rr6xCgse-o5CQpQPKIlrtP0o3wY1_W1bMlH_2E3Snx0OEUs7Tz0MSL1oa6vntHjTkCw-Jrmyj_83yL9LBzZxnI5UsQh2zVoU4LmClJUmedamlbXuAsyvHT5fDgIV96YG8BnPlEg1uLZWRi2dEUpOtZ0rjGNeFtuzpwSBk_WS9PQ";

    String chatThreadId;
    ChatAsyncClient client;

    TextView output;

    @Override
    protected void onStart() {
        super.onStart();

        createChatClient();

        Button startChat = findViewById(R.id.startChat);
        startChat.setOnClickListener(l -> call_createChatThread());

        Button sendMsg = findViewById(R.id.sendMsg);
        sendMsg.setOnClickListener(l -> sendChatMessage());

        output = findViewById(R.id.output);
        //output.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);

    }

    private void sendChatMessage() {
        // <CREATE A CHAT THREAD CLIENT>
        ChatThreadAsyncClient threadClient =
                new ChatThreadAsyncClient.Builder()
                        .endpoint(endpoint)
                        .credentialInterceptor(chain -> chain.proceed(chain.request()
                                .newBuilder()
                                .header(HttpHeader.AUTHORIZATION, "Bearer " + userAccessToken)
                                .build()))
                        .build();

        // <SEND A MESSAGE>
        // The chat message content, required.
        final String content = "Test message 1";
        // The display name of the sender, if null (i.e. not specified), an empty name will be set.
        final String senderDisplayName = "An important person";
        SendChatMessageRequest message = new SendChatMessageRequest()
                .setType(ChatMessageType.TEXT)
                .setContent(content)
                .setSenderDisplayName(senderDisplayName);

        // The unique ID of the thread.

        threadClient.sendChatMessage(chatThreadId, message, new Callback<String>() {
            @Override
            public void onSuccess(String messageId, okhttp3.Response response) {
                // A string is the response returned from sending a message, it is an id,
                // which is the unique ID of the message.
                final String chatMessageId = messageId;
                output.setTextColor(Color.GREEN);
                output.setText("The Chat mesage sent successful. Message ID is: " + chatMessageId);
                Log.i(String.valueOf(this), "The Chat mesage sent successful. Message ID is: " + chatMessageId);
                // Take further action.
            }

            @Override
            public void onFailure(Throwable throwable, okhttp3.Response response) {
                output.setTextColor(Color.RED);
                output.setText("Failed in sending chat: " + response.message());
                Log.i(String.valueOf(this), "Failed in sending chat");
                Log.i(String.valueOf(this), response.message());
                // Handle error.
            }
        });

    }

    private void call_createChatThread() {
        // <CREATE A CHAT THREAD>
        //  The list of ChatParticipant to be added to the thread.
        List<ChatParticipant> participants = new ArrayList<>();
        // The communication user ID you created before, required.
        String id = "8:acs:ba94e028-31c1-42dd-ac09-4aef4e2a2d28_00000008-aee1-806b-9806-113a0d006949";
        // The display name for the thread participant.
        String displayName = "initial participant";
        participants.add(new ChatParticipant()
                //.setId(id)
                .setIdentifier(new CommunicationIdentifierModel().setRawId(id))
                .setDisplayName(displayName));

        // The topic for the thread.
        final String topic = "General";
        // The model to pass to the create method.
        CreateChatThreadRequest thread = new CreateChatThreadRequest()
                .setTopic(topic)
                .setParticipants(participants);

        // optional, set a repeat request ID
        final String repeatabilityRequestID = "123";

        client.createChatThread(thread, repeatabilityRequestID, new Callback<CreateChatThreadResult>() {
            public void onSuccess(CreateChatThreadResult result, okhttp3.Response response) {
                ChatThread chatThread = result.getChatThread();
                chatThreadId = chatThread.getId();
                output.setTextColor(Color.GREEN);
                output.setText("The Thread ID is: " + chatThreadId);
                Log.i(String.valueOf(this), "The Thread ID is: " + chatThreadId);
                // take further action
            }

            public void onFailure(Throwable throwable, okhttp3.Response response) {
                output.setTextColor(Color.RED);
                output.setText("Failed to create the chat thread " + response.message());
                Log.i(String.valueOf(this), "Failed to create the chat thread ");
                Log.i(String.valueOf(this), response.message());
            }
        });
    }

    private void createChatClient() {
        // <<Create a chat client>>

        client = new ChatAsyncClient.Builder()
                .endpoint(endpoint)
                .credentialInterceptor(chain -> chain.proceed(chain.request()
                        .newBuilder()
                        .header(HttpHeader.AUTHORIZATION, "Bearer " + userAccessToken)
                        .build()))
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}