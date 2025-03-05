package com.talentstream.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.Data;


public class ResumeSkillsDto {
  
    
    @NotEmpty(message = "Technical skill cannot be blank") 
    private List<String> technicalSkillName;

	public List<String> getTechnicalSkillName() {
		return technicalSkillName;
	}

	public void setTechnicalSkillName(List<String> technicalSkillName) {
		this.technicalSkillName = technicalSkillName;
	}
    
	
    
    
}
