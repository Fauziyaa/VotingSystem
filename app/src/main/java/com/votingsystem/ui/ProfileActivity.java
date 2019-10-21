package com.votingsystem.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.votingsystem.R;
import com.votingsystem.db.SharedPrefs;
import com.votingsystem.db.model.User;
import com.votingsystem.util.CommonUtil;
import com.votingsystem.util.StringConstants;

import co.infinum.goldfinger.Goldfinger;

public class ProfileActivity extends BaseActivity implements ValueEventListener, View.OnClickListener {

    public static void start(final Activity context) {
        Intent intent = new Intent(context, ProfileActivity.class);
        context.startActivity(intent);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                context.finishAffinity();
            }
        }, 300);
    }

    Goldfinger goldfinger;
    FirebaseDatabase firebaseDatabase;
    private User user;
    TextView voterId, electorname, dob, sex, address, state;
    ImageView profilePic;
    Button vote, logout;
    View progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
        if (enrollFinger()) {
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.getReference().child(StringConstants.DB_USERS).child(SharedPrefs.getInstance().getId()).addValueEventListener(this);
        }
    }

    private void init() {
        voterId = findViewById(R.id.voter_id);
        electorname = findViewById(R.id.elector_name);
        dob = findViewById(R.id.dob);
        sex = findViewById(R.id.sex);
        address = findViewById(R.id.address);
        profilePic = findViewById(R.id.profile_pic);
        vote = findViewById(R.id.btn_vote);
        logout = findViewById(R.id.btn_logout);
        state = findViewById(R.id.state);
        progress = findViewById(R.id.progress);

        vote.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    private boolean enrollFinger() {
        if (SharedPrefs.getInstance().hasFingerprintEnrolled())
            return true;
        goldfinger = new Goldfinger.Builder(this).build();
        if (goldfinger.canAuthenticate()) {
            Goldfinger.PromptParams promptParams = new Goldfinger.PromptParams.Builder(this)
                    .title("Enroll your finger")
                    .description("You must enroll your finger to use this application")
                    .confirmationRequired(true)
                    .negativeButtonText("Cancel")
                    .build();
            goldfinger.authenticate(promptParams, new Goldfinger.Callback() {
                @Override
                public void onResult(@NonNull Goldfinger.Result result) {
                    switch (result.type()) {
                        case SUCCESS:
                            showMessage("Enrollment Successful", 1);
                            SharedPrefs.getInstance().setFingerprintEnrolled(true);
                            break;
                        case ERROR:
                            showMessage(result.message(), -1);
                            finish();
                            break;
                    }
                }

                @Override
                public void onError(@NonNull Exception e) {
                    showMessage(e.getMessage(), -1);
                    finish();
                }
            });
            return true;
        } else {
            showMessage("Cannot Authenticate", -1);
            return false;
        }
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (!isFinishing()) {
            progress.setVisibility(View.GONE);
            if (dataSnapshot.exists()) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    this.user = user;
                    setUserData(user);
                } else {
                    showMessage("User data not found", -1);
                    CommonUtil.logout(this);
                }
            } else {
                showMessage("Error Occurred", -1);
                CommonUtil.logout(this);
            }
        }
    }

    private void setUserData(User user) {
        Glide.with(this).load(user.getImage()).placeholder(R.drawable.ic_person).into(profilePic);
        voterId.setText(user.getId());
        electorname.setText("ELECTOR'S NAME : " + user.getName());
        dob.setText("DATE OF BIRTH : " + user.getDob());
        sex.setText("SEX : " + user.getGender());
        address.setText("ADDRESS : " + user.getAddress());
        state.setText("STATE : " + user.getState());
        if (user.isVoted()) {
            vote.setVisibility(View.GONE);
        } else {
            vote.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        showMessage(databaseError.getMessage(), -1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_logout:
                CommonUtil.logout(this);
                break;
            case R.id.btn_vote:
                VotingActivity.start(this, user);
                break;
        }
    }
}
