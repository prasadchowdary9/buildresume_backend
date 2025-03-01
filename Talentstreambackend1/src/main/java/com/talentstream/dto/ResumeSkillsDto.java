package com.talentstream.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.Data;


public class ResumeSkillsDto {
    private Integer id;
    
    @NotEmpty(message = "Technical skill cannot be blank") 
    private List<String> technicalSkills;
    
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public List<String> getTechnicalSkills() {
		return technicalSkills;
	}
	public void setTechnicalSkills(List<String> technicalSkills) {
		this.technicalSkills = technicalSkills;
	}
    
    
}
