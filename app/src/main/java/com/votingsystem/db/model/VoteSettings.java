package com.votingsystem.db.model;


import com.google.firebase.database.PropertyName;

import java.util.List;

public class VoteSettings {

    private String votingState;
    private String startDate;
    private String endDate;
    private List<Candidate> candidates;

    @PropertyName("voting_state")
    public String getVotingState() {
        return votingState;
    }

    @PropertyName("voting_state")
    public void setVotingState(String votingState) {
        this.votingState = votingState;
    }

    @PropertyName("start_date")
    public String getStartDate() {
        return startDate;
    }

    @PropertyName("start_date")
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @PropertyName("end_date")
    public String getEndDate() {
        return endDate;
    }

    @PropertyName("end_date")
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<Candidate> candidates) {
        this.candidates = candidates;
    }
}
