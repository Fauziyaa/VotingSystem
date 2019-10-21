package com.votingsystem.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.votingsystem.R;
import com.votingsystem.util.CommonUtil;

public class AdminOptionActivity extends BaseActivity {

    public static void start(final Activity context) {
        Intent intent = new Intent(context, AdminOptionActivity.class);
        context.startActivity(intent);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                context.finishAffinity();
            }
        }, 300);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_option);
        setTitle("Admin Options");
    }

    public void openAdminOptions(View view) {
        VoteSettingsActivity.start(this);
    }

    public void openDatabase(View view) {
        UserTableActivity.start(this);
    }


    public void logout(View view) {
        CommonUtil.logout(this);
    }
}
