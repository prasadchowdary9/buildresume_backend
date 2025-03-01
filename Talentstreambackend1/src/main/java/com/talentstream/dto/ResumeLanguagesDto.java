package com.talentstream.dto;

import javax.validation.constraints.*;

public class ResumeLanguagesDto {

    private Integer id;

    @NotBlank(message = "Language name is required")
    private String languageName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }
}
