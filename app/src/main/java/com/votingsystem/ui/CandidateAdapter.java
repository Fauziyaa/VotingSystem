package com.votingsystem.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.votingsystem.R;
import com.votingsystem.application.VotingSystem;
import com.votingsystem.db.model.Candidate;
import com.votingsystem.db.model.User;
import com.votingsystem.util.StringConstants;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import co.infinum.goldfinger.Goldfinger;

public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.CandidateViewHolder> {

    private List<Candidate> candidates;
    private boolean isVoting;
    private Goldfinger goldfinger;
    private FragmentActivity fragmentActivity;
    private String voterId;

    public void makeVoteable(FragmentActivity context,String userId) {
        this.fragmentActivity = context;
        this.voterId = userId;
        this.goldfinger = new Goldfinger.Builder(context).build();
    }

    public CandidateAdapter(boolean isVoting) {
        this.isVoting = isVoting;
    }

    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
        Collections.sort(this.candidates, new Comparator<Candidate>() {
            @Override
            public int compare(Candidate candidate, Candidate t1) {
                return t1.getVoteCount() - candidate.getVoteCount();
            }
        });
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CandidateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CandidateViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_candidate, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CandidateViewHolder holder, int position) {
        if (candidates != null && position >= 0 && position < candidates.size()) {
            holder.setCandidate(candidates.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (candidates != null)
            return candidates.size();
        return 0;
    }

    public class CandidateViewHolder extends RecyclerView.ViewHolder {

        ImageView partyImage;
        TextView partyName, candidateName, voteCount;

        public CandidateViewHolder(@NonNull View itemView) {
            super(itemView);
            partyImage = itemView.findViewById(R.id.party_pic);
            partyName = itemView.findViewById(R.id.party_name);
            candidateName = itemView.findViewById(R.id.candidate_name);
            voteCount = itemView.findViewById(R.id.vote_count);
            voteCount.setVisibility(isVoting ? View.GONE : View.VISIBLE);
        }

        public void setCandidate(final Candidate candidate) {
            Glide.with(VotingSystem.getInstance())
                    .load(candidate.getImage())
                    .placeholder(R.drawable.ic_person)
                    .into(partyImage);
            partyName.setText(candidate.getPartyName());
            candidateName.setText(candidate.getCandidateName());
            voteCount.setText("Votes:" + candidate.getVoteCount());
            if (isVoting) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Goldfinger.PromptParams promptParams = new Goldfinger.PromptParams.Builder(fragmentActivity)
                                .title(candidate.getPartyName())
                                .description("Please confirm to vote " + candidate.getCandidateName())
                                .confirmationRequired(true)
                                .negativeButtonText("Cancel")
                                .build();
                        goldfinger.authenticate(promptParams, new Goldfinger.Callback() {
                            @Override
                            public void onResult(@NonNull Goldfinger.Result result) {
                                switch (result.type()) {
                                    case SUCCESS:
                                        vote(candidate);
                                        break;
                                }
                            }

                            @Override
                            public void onError(@NonNull Exception e) {
                                showMessage(e.getMessage(), -1);
                            }
                        });
                    }
                });
            }
        }

    }

    public void showMessage(String message, int type){
        if (fragmentActivity!=null){
            ((BaseActivity)fragmentActivity).showMessage(message,type);
        }
    }

    private void vote(Candidate candidate) {
        candidate.setVoteCount(candidate.getVoteCount()+1);
        FirebaseDatabase.getInstance().getReference()
                .child(StringConstants.DB_VOTE_SETTINGS)
                .child(StringConstants.DB_CANDIDATES)
                .setValue(candidates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseDatabase.getInstance().getReference()
                                .child(StringConstants.DB_USERS)
                                .child(voterId)
                                .child(StringConstants.DB_IS_VOTED)
                                .setValue(true)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showMessage("Voted Successfully",1);
                                fragmentActivity.finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showMessage(e.getMessage(),-1);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage(e.getMessage(),-1);
                    }
                });
    }
}

