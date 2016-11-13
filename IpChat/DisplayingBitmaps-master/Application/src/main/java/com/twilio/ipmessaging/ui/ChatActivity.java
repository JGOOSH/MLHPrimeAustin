package com.twilio.ipmessaging.ui;

import android.app.ProgressDialog;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.displayingbitmaps.R;
import com.example.android.displayingbitmaps.ui.ImageDetailActivity;
import com.twilio.ipmessaging.Channel;
import com.twilio.ipmessaging.ChannelListener;
import com.twilio.ipmessaging.Channels;
import com.twilio.ipmessaging.Constants;
import com.twilio.ipmessaging.Constants.CreateChannelListener;
import com.twilio.ipmessaging.Constants.StatusListener;
import com.twilio.ipmessaging.IPMessagingClientListener;
import com.twilio.ipmessaging.Member;
import com.twilio.ipmessaging.Message;
import com.twilio.ipmessaging.Messages;
import com.twilio.ipmessaging.application.TwilioApplication;
import com.twilio.ipmessaging.util.BasicIPMessagingClient;
import com.twilio.ipmessaging.util.HttpHelper;
import com.twilio.ipmessaging.util.ILoginListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import uk.co.ribot.easyadapter.EasyAdapter;


public class ChatActivity extends FragmentActivity implements ChannelListener, ILoginListener, IPMessagingClientListener {

    // Authentication
    private static final String AUTH_SCRIPT = "http://14bfae39.ngrok.io/token";
    private String capabilityToken = null;
    private BasicIPMessagingClient basicClient;
    private ProgressDialog progressDialog;

    // Chat
    private static final String TAG = "ChatActivityTag";
    private List<Message> messages = new ArrayList<>();
    private EasyAdapter<Message> adapter;

    private ListView lvChat;
    private EditText etMessage;

    private Channel channel;

    private int currentImage;

    public static String local_author;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        basicClient = TwilioApplication.get().getBasicClient();
        if(basicClient != null) {
            // Authentication
            authenticateUser();
        }

        // Chat
        currentImage = getIntent().getIntExtra(ImageDetailActivity.EXTRA_IMAGE, -1);

        // Message Text
        this.etMessage = (EditText) findViewById(R.id.etMessage);

        // Send Button
        Button btSend = (Button) findViewById(R.id.btSend);
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = etMessage.getText().toString();
                if (channel != null) {
                    Messages messagesObject = channel.getMessages();
                    final Message message = messagesObject.createMessage(input);
                    messagesObject.sendMessage(message, new StatusListener() {
                        @Override
                        public void onSuccess() {
                            Log.e(TAG, "Successful at sending message.");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    messages.add(message);
                                    adapter.notifyDataSetChanged();
                                    etMessage.setText("");
                                    etMessage.requestFocus();
                                }
                            });
                        }

                        @Override
                        public void onError() {
                            Log.e(TAG, "Error sending message.");
                        }
                    });
                }
            }
        });
    }

    private class CustomMessageComparator implements Comparator<Message> {
        @Override
        public int compare(Message lhs, Message rhs) {
            if (lhs == null) {
                return (rhs == null) ? 0 : -1;
            }
            if (rhs == null) {
                return 1;
            }
            return lhs.getTimeStamp().compareTo(rhs.getTimeStamp());
        }
    }

    private void setupListView() {
        final Messages messagesObject = channel.getMessages();

        if(messagesObject != null) {
            Message[] messagesArray = messagesObject.getMessages();
            if(messagesArray.length > 0 ) {
                messages = new ArrayList<>(Arrays.asList(messagesArray));
                Collections.sort(messages, new CustomMessageComparator());
            }
        }

        adapter = new EasyAdapter<>(this, MessageViewHolder.class, messages,
                new MessageViewHolder.OnMessageClickListener() {

                    @Override
                    public void onMessageClicked(Message message) {
                        // TODO: Implement options for deletion or edit
                    }
                });

        // List View
        lvChat = (ListView) findViewById(R.id.lvChat);
        lvChat.setAdapter(adapter);

        if (lvChat != null) {
            lvChat.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
            lvChat.setStackFromBottom(true);
            adapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    lvChat.setSelection(adapter.getCount() - 1);
                }
            });
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMessageAdd(Message message) {
        if(message != null) {
            Log.d(TAG, "Received onMessageAdd event");
        }
    }

    @Override
    public void onMessageChange(Message message) {
        if(message != null) {
            Log.d(TAG, "Received onMessageChange event");
        }
    }

    @Override
    public void onMessageDelete(Message message) {

    }

    @Override
    public void onMemberJoin(Member member) {
        if(member != null) {
            Log.d(TAG, member.getIdentity() + " joined");
        }
    }

    @Override
    public void onMemberChange(Member member) {

    }

    @Override
    public void onMemberDelete(Member member) {

    }

    @Override
    public void onAttributesChange(Map<String, String> map) {

    }

    @Override
    public void onTypingStarted(Member member) {

    }

    @Override
    public void onTypingEnded(Member member) {

    }

    @Override
    public void onChannelAdd(Channel channel) {
        Log.d(TAG, "Received onChannelAdd callback "+channel.getUniqueName());
    }

    @Override
    public void onChannelChange(Channel channel) {
        Log.d(TAG, "Received onChannelChange callback "+channel.getUniqueName());
    }

    @Override
    public void onChannelDelete(Channel channel) {

    }

    @Override
    public void onError(int errorCode, String errorText) {
        Log.d(TAG, "Received onError callback "+ errorCode + " " + errorText);
    }

    @Override
    public void onAttributesChange(String s) {
        Log.d(TAG, "Received onAttributesChange callback");
    }

    @Override
    public void onChannelHistoryLoaded(Channel channel) {
        Log.d(TAG, "Received onChannelHistoryLoaded callback " + channel.getUniqueName());
    }

    @Override
    public void onLoginStarted() {
        Log.d(TAG, "Log in started");
    }

    @Override
    public void onLoginFinished() {
        ChatActivity.this.progressDialog.dismiss();
        basicClient.getIpMessagingClient().setListener(ChatActivity.this);

        final String channelName = "TestChannel" + String.valueOf(currentImage);
        Channels channelsLocal = basicClient.getIpMessagingClient().getChannels();
        // Creates a new public channel if one doesn't already exist
        if (channelsLocal.getChannelByUniqueName(channelName) != null) {
            //join it
            channel = channelsLocal.getChannelByUniqueName(channelName);
            channel.setListener(ChatActivity.this);

            // Listen for channel join status
            StatusListener joinListener = new StatusListener(){
                @Override
                public void onSuccess() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setupListView();
                        }
                    });
                    Log.d(TAG, "Successfully joined existing channel");
                }

                @Override
                public void onError() {
                    Log.e(TAG, "failed to join existing channel");
                }
            };

            // join the channel
            channel.join(joinListener);
        } else {

            final Map<String, String> attributes = new HashMap<>();
            attributes.put("topic", "Discussion on image : " + String.valueOf(currentImage));

            Map<String, Object> options = new HashMap<>();
            options.put(Constants.CHANNEL_FRIENDLY_NAME, channelName);
            options.put(Constants.CHANNEL_UNIQUE_NAME, channelName);
            options.put(Constants.CHANNEL_TYPE, Channel.ChannelType.CHANNEL_TYPE_PUBLIC);
            options.put("attributes", attributes);

            channelsLocal.createChannel(options, new CreateChannelListener() {
                @Override
                public void onCreated(final Channel newChannel) {
                    Log.e(TAG, "Successfully created a channel with options");
                    channel = newChannel;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setupListView();
                        }
                    });
                }


                @Override
                public void onError() {
                    Log.e(TAG, "failed to create new channel");
                }
            });
        }
    }

    @Override
    public void onLoginError(String errorMessage) {
        ChatActivity.this.progressDialog.dismiss();
        Log.d(TAG, "Error logging in : " + errorMessage);
        Toast.makeText(getBaseContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLogoutFinished() {

    }

    private void authenticateUser() {
        try {
            new GetCapabilityTokenAsyncTask().execute(AUTH_SCRIPT).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private class GetCapabilityTokenAsyncTask extends AsyncTask<String, Void, String> {
        private String urlString;
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            new Thread(new Runnable() {
                @Override
                public void run() {
                    ChatActivity.this.basicClient.doLogin(ChatActivity.this, urlString);
                }
            }).start();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ChatActivity.this.progressDialog = ProgressDialog.show(ChatActivity.this, "",
                    "Logging in. Please wait...", true);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                urlString = params[0];
                capabilityToken = HttpHelper.httpGet(urlString);

                JSONObject responseObject = new JSONObject(capabilityToken);
                String token = responseObject.getString("token");
                ChatActivity.local_author = responseObject.getString("identity");

                basicClient.setCapabilityToken(token);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return capabilityToken;
        }
    }
}
