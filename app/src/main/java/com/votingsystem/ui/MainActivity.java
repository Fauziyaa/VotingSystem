package com.votingsystem.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.votingsystem.R;
import com.votingsystem.db.SharedPrefs;
import com.votingsystem.util.IntegerConstants;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    Button admin, user;

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPrefs.getInstance().isLoggedIn()) {
            if (SharedPrefs.getInstance().isAdmin())
                AdminOptionActivity.start(this);
            else
                ProfileActivity.start(this);
            return;
        } else {
            setContentView(R.layout.activity_main);
            admin = findViewById(R.id.btn_admin);
            user = findViewById(R.id.btn_user);
            admin.setOnClickListener(this);
            user.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_admin:
                LoginActivity.start(this, IntegerConstants.LOGIN_ADMIN);
                break;
            case R.id.btn_user:
                LoginActivity.start(this, IntegerConstants.LOGIN_USER);
                break;
        }
    }
}
