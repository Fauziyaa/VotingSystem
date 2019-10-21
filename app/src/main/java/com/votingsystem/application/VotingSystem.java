package com.votingsystem.application;

import android.app.Application;

import com.votingsystem.db.FirebaseHelper;

public class VotingSystem extends Application {

    private static VotingSystem instance = null;

    public static VotingSystem getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
