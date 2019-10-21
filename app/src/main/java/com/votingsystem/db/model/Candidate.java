package com.votingsystem.db.model;

public class Candidate {
    String partyName, candidateName,image;
    int voteCount;

    public Candidate() {
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String name) {
        this.candidateName = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
}
