package com.twilio.ipmessaging.application;

import android.app.Application;

import com.twilio.ipmessaging.util.BasicIPMessagingClient;

public class TwilioApplication extends Application {
    private BasicIPMessagingClient rtdJni;
    private static TwilioApplication instance;

    public static TwilioApplication get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TwilioApplication.instance = this;
        rtdJni = new BasicIPMessagingClient(getApplicationContext());
    }

    public BasicIPMessagingClient getBasicClient() {
        return this.rtdJni;
    }

    public BasicIPMessagingClient getRtdJni() {
        return this.rtdJni;
    }
}
