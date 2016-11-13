package com.twilio.ipmessaging.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.twilio.common.TwilioAccessManager;
import com.twilio.common.TwilioAccessManagerFactory;
import com.twilio.common.TwilioAccessManagerListener;
import com.twilio.ipmessaging.Channel;
import com.twilio.ipmessaging.Constants.InitListener;
import com.twilio.ipmessaging.Constants.StatusListener;
import com.twilio.ipmessaging.IPMessagingClientListener;
import com.twilio.ipmessaging.TwilioIPMessagingClient;
import com.twilio.ipmessaging.TwilioIPMessagingSDK;

public class BasicIPMessagingClient implements IPMessagingClientListener, TwilioAccessManagerListener {
    private static final String TAG = "BasicIPMessagingClient";
    private TwilioIPMessagingClient ipMessagingClient;
    private Context context;
    private static String capabilityToken;
    private TwilioAccessManager accessMgr;
    private Handler loginListenerHandler;
    private String urlString;

    public BasicIPMessagingClient(Context context) {
        super();
        this.context = context;
    }

    public void setCapabilityToken(String capabilityToken) {
        this.capabilityToken = capabilityToken;
    }

    public static String getCapabilityToken() {
        return capabilityToken;
    }


    public void doLogin(final ILoginListener listener, String url) {
        this.urlString = url;
        this.loginListenerHandler = setupListenerHandler();
        TwilioIPMessagingSDK.setLogLevel(Log.DEBUG);
        if(!TwilioIPMessagingSDK.isInitialized()) {
            TwilioIPMessagingSDK.initializeSDK(this.context, new InitListener() {
                @Override
                public void onInitialized() {
                    createClientWithAccessManager(listener);
                }

                @Override
                public void onError(Exception error) {
                    Log.d(TAG, error.getMessage());
                }
            });
        } else {
            createClientWithToken(listener);
        }
    }

    @Override
    public void onChannelAdd(Channel channel) {
        if(channel != null) {
            Log.d(TAG, "A Channel :" + channel.getFriendlyName() + " got added");
        } else {
            Log.d(TAG, "Received onChannelAdd event.");
        }
    }

    @Override
    public void onChannelChange(Channel channel) {
        if(channel != null) {
            Log.d(TAG, "Channel Name : "+ channel.getFriendlyName() + " got Changed");
        } else {
            Log.d(TAG, "received onChannelChange event.");
        }
    }

    @Override
    public void onChannelDelete(Channel channel) {
        if(channel != null) {
            Log.d(TAG, "A Channel :"+ channel.getFriendlyName() + " got deleted");
        } else {
            Log.d(TAG, "received onChannelDelete event.");
        }
    }

    @Override
    public void onError(int errorCode, String errorText) {
        Log.d(TAG, "Received onError event.");
    }

    @Override
    public void onAttributesChange(String attributes) {
        Log.d(TAG, "Received onAttributesChange event.");
    }

    @Override
    public void onChannelHistoryLoaded(Channel channel) {
        Log.d(TAG, "Received onChannelHistoryLoaded callback " + channel.getFriendlyName());
    }

    @Override
    public void onAccessManagerTokenExpire(TwilioAccessManager arg0) {
        Log.d(TAG, "Received AccessManager:onAccessManagerTokenExpire.");
    }

    @Override
    public void onError(TwilioAccessManager arg0, String arg1) {
        Log.d(TAG, "Received AccessManager:onError.");
    }

    @Override
    public void onTokenUpdated(TwilioAccessManager arg0) {
        Log.d(TAG, "Received AccessManager:onTokenUpdated.");
    }

    private Handler setupListenerHandler() {
        Looper looper;
        Handler handler;
        if((looper = Looper.myLooper()) != null) {
            handler = new Handler(looper);
        } else if((looper = Looper.getMainLooper()) != null) {
            handler = new Handler(looper);
        } else {
            throw new IllegalArgumentException("Channel Listener must have a Looper.");
        }
        return handler;
    }

    public TwilioIPMessagingClient getIpMessagingClient() {
        return ipMessagingClient;
    }

    private void createClientWithAccessManager(final ILoginListener listener) {
        this.accessMgr = TwilioAccessManagerFactory.createAccessManager(this.capabilityToken, new TwilioAccessManagerListener() {
            @Override
            public void onAccessManagerTokenExpire(TwilioAccessManager twilioAccessManager) {
                Log.d(TAG, "token expired.");
                new GetCapabilityTokenAsyncTask().execute(BasicIPMessagingClient.this.urlString);
            }

            @Override
            public void onTokenUpdated(TwilioAccessManager twilioAccessManager) {
                Log.d(TAG, "token updated.");
            }

            @Override
            public void onError(TwilioAccessManager twilioAccessManager, String s) {
                Log.d(TAG, "token error: " + s);
            }
        });

        ipMessagingClient = TwilioIPMessagingSDK.createIPMessagingClientWithAccessManager(BasicIPMessagingClient.this.accessMgr, BasicIPMessagingClient.this);
        if(ipMessagingClient != null) {
            ipMessagingClient.setListener(BasicIPMessagingClient.this);
            BasicIPMessagingClient.this.loginListenerHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(listener != null) {
                        listener.onLoginFinished();
                    }
                }
            });
        } else {
            listener.onLoginError("ipMessagingClientWithAccessManager is null");
        }
    }

    private void createClientWithToken(ILoginListener listener) {
        ipMessagingClient = TwilioIPMessagingSDK.createIPMessagingClientWithToken(this.capabilityToken, BasicIPMessagingClient.this);
        Log.i("squarepants", ipMessagingClient + "");
        if(ipMessagingClient != null) {
            if(listener != null) {
                listener.onLoginFinished();
            }
        } else {
            listener.onLoginError("ipMessagingClient is null");
        }
    }

    private class GetCapabilityTokenAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ipMessagingClient.updateToken(BasicIPMessagingClient.getCapabilityToken(), new StatusListener() {

                @Override
                public void onSuccess() {
                    Log.d(TAG, "Updated Token was successfull");
                }

                @Override
                public void onError() {
                    Log.e(TAG, "Updated Token failed");
                }});
            accessMgr.updateToken(null);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                capabilityToken = HttpHelper.httpGet(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return capabilityToken;
        }
    }
}
