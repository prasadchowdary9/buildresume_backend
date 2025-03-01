package com.talentstream.dto;

import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class ResumeExperienceDto {

	private Integer id;

	@NotBlank(message = "Company Name is required")
	private String company;

	@NotBlank(message = "Job Title is required")
	private String jobTitle;

	@NotNull(message = "Start date is required")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate startDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate endDate;

	private String description;

	
}
