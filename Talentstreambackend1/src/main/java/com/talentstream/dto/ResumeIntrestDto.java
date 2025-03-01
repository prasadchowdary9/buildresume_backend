package com.talentstream.dto;

import java.util.List;

import javax.validation.constraints.NotEmpty;

public class ResumeIntrestDto {

    @NotEmpty(message = "Interest list cannot be empty") 
    private List<String> intrests;

    public List<String> getIntrests() {
        return intrests;
    }

    public void setInterests(List<String> intrests) {
        this.intrests = intrests;
    }
}
