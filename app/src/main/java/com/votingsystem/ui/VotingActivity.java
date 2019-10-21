package com.votingsystem.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.votingsystem.R;
import com.votingsystem.db.model.Candidate;
import com.votingsystem.db.model.User;
import com.votingsystem.db.model.VoteSettings;
import com.votingsystem.util.CommonUtil;
import com.votingsystem.util.StringConstants;

import java.util.Calendar;
import java.util.List;

public class VotingActivity extends BaseActivity implements ValueEventListener {

    public static void start(Context context, User user) {
        Intent intent = new Intent(context, VotingActivity.class);
        intent.putExtra("User", user);
        context.startActivity(intent);
    }


    User user;
    RecyclerView recyclerView;
    CandidateAdapter candidateAdapter;
    TextView invalidMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Vote");
        if (getIntent() != null && getIntent().getParcelableExtra("User") != null) {
            this.user = getIntent().getParcelableExtra("User");
        }
        getVotingSession();
        recyclerView = findViewById(R.id.recyclerview);
        invalidMessage = findViewById(R.id.invalid_message);
        candidateAdapter = new CandidateAdapter(true);
        recyclerView.setAdapter(candidateAdapter);
        candidateAdapter.makeVoteable(this, user.getId());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getVotingSession() {
        FirebaseDatabase.getInstance().getReference().child(StringConstants.DB_VOTE_SETTINGS).addListenerForSingleValueEvent(this);
    }


    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            VoteSettings voteSettings = dataSnapshot.getValue(VoteSettings.class);
            if (voteSettings != null) {
                if (user.getState().toLowerCase().equals(voteSettings.getVotingState().toLowerCase())) {
                    Calendar startDate = CommonUtil.getDate(voteSettings.getStartDate());
                    Calendar endDate = CommonUtil.getDate(voteSettings.getEndDate());
                    if (Calendar.getInstance().before(endDate) && Calendar.getInstance().after(startDate)) {
                        showCandidateToVote(voteSettings.getCandidates());
                    } else {
                        showInvalidSessionMessage("No Voting Session Available");
                    }
                } else {
                    showInvalidSessionMessage("No Voting Session Available In " + user.getState());
                }
            }
        }
    }

    private void showCandidateToVote(List<Candidate> candidates) {
        candidateAdapter.setCandidates(candidates);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        showMessage(databaseError.getMessage(), -1);
    }


    public void showInvalidSessionMessage(String message) {
        invalidMessage.setText(message);
    }
}
