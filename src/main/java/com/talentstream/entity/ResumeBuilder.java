package com.talentstream.entity;

import lombok.Data;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resume_builder")
@Data
public class ResumeBuilder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "resume_personal_info_id", referencedColumnName = "id")
	private ResumePersonalInfo resumePersonalInfo;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "resume_builder_skills", joinColumns = @JoinColumn(name = "resume_builder_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
	private List<ResumeTechnicalSkills> resumeTechnicalSkills = new ArrayList<>();

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "applicant_id", referencedColumnName = "id")
	@JsonIgnore
	private Applicant applicant;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "resume_builder_experience", joinColumns = @JoinColumn(name = "resume_builder_id"), inverseJoinColumns = @JoinColumn(name = "experience_id"))
	private List<ResumeExperience> resumeExperiences = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "resume_builder_education", joinColumns = @JoinColumn(name = "resume_builder_id"), inverseJoinColumns = @JoinColumn(name = "education_id"))
	private List<ResumeEducation> resumeEducations = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "resume_builder_project", joinColumns = @JoinColumn(name = "resume_builder_id"), inverseJoinColumns = @JoinColumn(name = "project_id"))
	private List<ResumeProject> resumeProjects = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "resume_builder_certificate", joinColumns = @JoinColumn(name = "resume_builder_id"), inverseJoinColumns = @JoinColumn(name = "certificate_id"))
	private List<ResumeCertificates> resumeCertificates = new ArrayList<>();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "resume_builder_language", joinColumns = @JoinColumn(name = "resume_builder_id"), inverseJoinColumns = @JoinColumn(name = "language_id"))
	private List<ResumeLanguages> resumeLanguages = new ArrayList<>();
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinTable(name = "resume_builder_intrest", joinColumns = @JoinColumn(name = "resume_builder_id"), inverseJoinColumns = @JoinColumn(name = "intrest_id"))
	private List<ResumeIntrest>resumeIntrests  = new ArrayList<>();
	
}
