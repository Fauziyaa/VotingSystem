package com.votingsystem.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.votingsystem.R;
import com.votingsystem.db.SharedPrefs;
import com.votingsystem.util.IntegerConstants;
import com.votingsystem.util.StringConstants;

public class LoginActivity extends BaseActivity implements View.OnClickListener, ValueEventListener {

    public static void start(Context context, int type) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(StringConstants.LOGIN_TYPE, type);
        context.startActivity(intent);
    }

    int type;

    TextInputEditText id, password;
    TextInputLayout idLayout;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (getIntent() != null) {
            type = getIntent().getIntExtra(StringConstants.LOGIN_TYPE, IntegerConstants.LOGIN_USER);
        }
        init();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void init() {
        setTitle(R.string.login);
        id = findViewById(R.id.et_id);
        password = findViewById(R.id.et_password);
        idLayout = findViewById(R.id.etl_id);
        login = findViewById(R.id.btn_login);
        login.setOnClickListener(this);
        switch (type) {
            case IntegerConstants.LOGIN_ADMIN:
                idLayout.setHint(getString(R.string.admin_id));
                break;
            case IntegerConstants.LOGIN_USER:
                idLayout.setHint(getString(R.string.voter_id));
                break;
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if (validate()) {
                    FirebaseDatabase.getInstance().getReference().child(type == IntegerConstants.LOGIN_ADMIN ? StringConstants.DB_ADMIN : StringConstants.DB_USERS).child(id.getText().toString().trim()).orderByKey().addListenerForSingleValueEvent(this);
                }
                break;
        }
    }

    public boolean validate() {
        if (id.getText().toString().trim().isEmpty()) {
            id.setError("Please enter id");
            id.requestFocus();
            return false;
        }
        if (password.getText().toString().trim().isEmpty()) {
            password.setError("Please enter password");
            password.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            if (dataSnapshot.hasChild("password")) {
                if (password.getText().toString().trim().equals(dataSnapshot.child("password").getValue(String.class))) {
                    showMessage("Login Success", 1);
                    SharedPrefs.getInstance().setIsLoggedIn(true);
                    SharedPrefs.getInstance().setAdmin(type == IntegerConstants.LOGIN_ADMIN);
                    SharedPrefs.getInstance().setId(dataSnapshot.child("id").getValue(String.class));
                    switch (type) {
                        case IntegerConstants.LOGIN_USER:
                            ProfileActivity.start(this);
                            break;
                        case IntegerConstants.LOGIN_ADMIN:
                            AdminOptionActivity.start(this);
                            break;
                    }
                } else {
                    showMessage("Incorrect password", -1);
                }
            } else {
                showMessage("User Not Found", -1);
            }
        } else {
            showMessage("User Not Found", -1);
        }

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        showMessage(databaseError.getMessage(), -1);
    }
}
