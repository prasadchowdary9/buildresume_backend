package com.talentstream.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.talentstream.dto.*;
import com.talentstream.entity.*;
import com.talentstream.repository.*;

@Service
public class ResumeBuilderService {

	@Autowired
	private ApplicantRepository applicantRepository;

	@Autowired
	private ResumeBuilderRepository resumeBuilderRepository;

	@Autowired
	private ResumePersonalInfoRepository resumePersonalInfoRepository;

	@Autowired
	private ResumeExperienceRepository resumeExperienceRepository;

	@Autowired
	private ResumeEducationRepository resumeEducationRepository;

	@Autowired
	private ResumeProjectRepository resumeProjectRepository;

	@Autowired
	private ResumeCertificatesRepository resumeCertificatesRepository;

	@Autowired
	private ResumeSkillsRepository resumeSkillsRepository;

	@Autowired
	private ResumeLanguagesRepository resumeLanguagesRepository;
	
	@Autowired
	private ResumeIntrestReposiotry resumeIntrestReposiotry;

	public ResumeBuilder createResume(ResumeBuilderDto resumeBuilderDto, Long applicantId) {

		// Fetch applicant details
		Applicant applicant = applicantRepository.findById(applicantId)
				.orElseThrow(() -> new RuntimeException("Applicant not found with id: " + applicantId));

		ResumeBuilder resumeBuilder = new ResumeBuilder();
		resumeBuilder.setApplicant(applicant);

		// Saving personal info
		if (resumeBuilderDto.getResumePersonalInfo() != null) {
			ResumePersonalInfo resumePersonalInfo = convertToPersonalInfoEntity(
					resumeBuilderDto.getResumePersonalInfo());
			resumePersonalInfoRepository.save(resumePersonalInfo);
			resumeBuilder.setResumePersonalInfo(resumePersonalInfo);
		}

		// Saving education details using saveAll()
		if (resumeBuilderDto.getResumeEducations() != null && !resumeBuilderDto.getResumeEducations().isEmpty()) {
			List<ResumeEducation> resumeEducations = convertToEducationEntities(resumeBuilderDto.getResumeEducations());
			resumeEducationRepository.saveAll(resumeEducations);
			resumeBuilder.setResumeEducations(resumeEducations);
		}

		// Saving experience details using saveAll()
		if (resumeBuilderDto.getResumeExperiences() != null && !resumeBuilderDto.getResumeExperiences().isEmpty()) {
			List<ResumeExperience> resumeExperiences = convertToExperienceEntities(
					resumeBuilderDto.getResumeExperiences());
			resumeExperienceRepository.saveAll(resumeExperiences);
			resumeBuilder.setResumeExperiences(resumeExperiences);
		}

		// Saving certificates using saveAll()
		if (resumeBuilderDto.getResumeCertificates() != null && !resumeBuilderDto.getResumeCertificates().isEmpty()) {
			List<ResumeCertificates> resumeCertificates = convertToCertificatesEntities(
					resumeBuilderDto.getResumeCertificates());
			resumeCertificatesRepository.saveAll(resumeCertificates);
			resumeBuilder.setResumeCertificates(resumeCertificates);
		}

		// Saving skills using saveAll()
		if (resumeBuilderDto.getResumeSkills() != null
				&& resumeBuilderDto.getResumeSkills().getTechnicalSkills() != null) {
			List<ResumeTechnicalSkills> resumeSkills = convertToSkillsEntities(
					resumeBuilderDto.getResumeSkills());
			resumeSkillsRepository.saveAll(resumeSkills);
			resumeBuilder.setResumeTechnicalSkills(resumeSkills);
		}

		// Saving projects using saveAll()
		if (resumeBuilderDto.getResumeProjects() != null && !resumeBuilderDto.getResumeProjects().isEmpty()) {
			List<ResumeProject> resumeProjects = convertToProjectEntities(resumeBuilderDto.getResumeProjects());
			resumeProjectRepository.saveAll(resumeProjects);
			resumeBuilder.setResumeProjects(resumeProjects);
		}

		// Saving languages using saveAll()
		if (resumeBuilderDto.getResumeLanguages() != null && !resumeBuilderDto.getResumeLanguages().isEmpty()) {
			List<ResumeLanguages> resumeLanguages = convertToLanguagesEntities(resumeBuilderDto.getResumeLanguages());
			resumeLanguagesRepository.saveAll(resumeLanguages);
			resumeBuilder.setResumeLanguages(resumeLanguages);
		}
		
		if(resumeBuilderDto.getResumeIntrest()!=null && resumeBuilderDto.getResumeIntrest().getIntrests() != null) {
			 List<ResumeIntrest> intrests = convertToIntrestEntities(resumeBuilderDto.getResumeIntrest());
			 resumeIntrestReposiotry.saveAll(intrests);
			 resumeBuilder.setResumeIntrests(intrests);
			 
		}

		// Save the final ResumeBuilder entity
		return resumeBuilderRepository.save(resumeBuilder);
	}

	public ResumeBuilder getResumeWithEducation(Long applicantId) {
		ResumeBuilder resumeBuilder = resumeBuilderRepository.findByApplicantId(applicantId)
				.orElseThrow(() -> new RuntimeException("Resume not found for Applicant  id: " + applicantId));
		
		return resumeBuilder;
	}

	// Conversion method for personal info
	private ResumePersonalInfo convertToPersonalInfoEntity(ResumePersonalInfoDto dto) {
		ResumePersonalInfo personalInfo = new ResumePersonalInfo();
		personalInfo.setFullName(dto.getFullName());
		personalInfo.setEmail(dto.getEmail());
		personalInfo.setPhoneNo(dto.getPhoneNo());
		personalInfo.setAddress(dto.getAddress());
		personalInfo.setLinkedin(dto.getLinkedin());
		personalInfo.setGithub(dto.getGithub());
		personalInfo.setWebsite(dto.getWebsite());
		personalInfo.setRole(dto.getRole());
		personalInfo.setSummary(dto.getSummary());
		return personalInfo;
	}

	// conversion method for education
	private List<ResumeEducation> convertToEducationEntities(List<ResumeEducationDto> dtos) {
		return dtos.stream().map(dto -> {
			ResumeEducation resumeEducation = new ResumeEducation();
			resumeEducation.setCollege(dto.getCollege());
			resumeEducation.setStandard(dto.getStandard());
			resumeEducation.setStartYear(dto.getStartYear());
			resumeEducation.setEndYear(dto.getEndYear());
			resumeEducation.setCgpa(dto.getCgpa());
			return resumeEducation;
		}).collect(Collectors.toList());
	}

	// conversion method for experience
	private List<ResumeExperience> convertToExperienceEntities(List<ResumeExperienceDto> dtos) {
		return dtos.stream().map(dto -> {
			ResumeExperience resumeExperience = new ResumeExperience();
			resumeExperience.setCompany(dto.getCompany());
			resumeExperience.setJobTitle(dto.getJobTitle());
			resumeExperience.setStartDate(dto.getStartDate());
			resumeExperience.setEndDate(dto.getEndDate());
			resumeExperience.setDescription(dto.getDescription());
			return resumeExperience;
		}).collect(Collectors.toList());
	}

	// conversion method for certificates
	private List<ResumeCertificates> convertToCertificatesEntities(List<ResumeCertificatesDto> dtos) {
		return dtos.stream().map(dto -> {
			ResumeCertificates resumeCertificates = new ResumeCertificates();
			resumeCertificates.setTitle(dto.getTitle());
			resumeCertificates.setIssuedBy(dto.getIssuedBy());
			resumeCertificates.setYear(dto.getYear());
			return resumeCertificates;
		}).collect(Collectors.toList());
	}

	// conversion method for skills
	private List<ResumeTechnicalSkills> convertToSkillsEntities(ResumeSkillsDto dto) {
		return dto.getTechnicalSkills().stream().map(skill -> {
			ResumeTechnicalSkills resumeTechnicalSkill = new ResumeTechnicalSkills();
			resumeTechnicalSkill.setTechnicalSkillName(skill);
			return resumeTechnicalSkill;
		}).collect(Collectors.toList());
	}

	// conversion method for projects
	private List<ResumeProject> convertToProjectEntities(List<ResumeProjectDto> projectDtos) {
		return projectDtos.stream().map(dto -> {
			ResumeProject project = new ResumeProject();
			project.setTitle(dto.getTitle());
			project.setDescription(dto.getDescription());
			project.setStartDate(dto.getStartDate());
			project.setEndDate(dto.getEndDate());
			project.setLink(dto.getLink());
			return project;
		}).collect(Collectors.toList());
	}

	// conversion method for languages
	private List<ResumeLanguages> convertToLanguagesEntities(List<ResumeLanguagesDto> dtos) {
		return dtos.stream().map(dto -> {
			ResumeLanguages resumeLanguages = new ResumeLanguages();
			resumeLanguages.setLanguageName(dto.getLanguageName());
			return resumeLanguages;
		}).collect(Collectors.toList());
	}
	
	private List<ResumeIntrest> convertToIntrestEntities(ResumeIntrestDto dto) {
		return dto.getIntrests().stream().map(intrest -> {
			ResumeIntrest resumeIntrest=new ResumeIntrest();
			resumeIntrest.setIntrest(intrest);
			return resumeIntrest;
			
		}).collect(Collectors.toList());
		
	}
	public ResumeBuilder updateResume(Long applicantId, ResumeBuilderDto resumeDto) {
	    // Fetch the existing resume for the applicant
	    ResumeBuilder existingResume = resumeBuilderRepository.findByApplicantId(applicantId)
	            .orElseThrow(() -> new RuntimeException("Resume not found for applicant ID: " + applicantId));
	    
	    // Update personal info
	    if (resumeDto.getResumePersonalInfo() != null) {
	        ResumePersonalInfo updatedPersonalInfo = convertToPersonalInfoEntity(resumeDto.getResumePersonalInfo());
	        resumePersonalInfoRepository.save(updatedPersonalInfo);
	        existingResume.setResumePersonalInfo(updatedPersonalInfo);
	    }

	    // Update skills
	    if (resumeDto.getResumeSkills() != null && resumeDto.getResumeSkills().getTechnicalSkills() != null) {
	        List<ResumeTechnicalSkills> updatedSkills = convertToSkillsEntities(resumeDto.getResumeSkills());
	        resumeSkillsRepository.saveAll(updatedSkills);
	        existingResume.setResumeTechnicalSkills(updatedSkills);
	    }

	    // Update education
	    if (resumeDto.getResumeEducations() != null && !resumeDto.getResumeEducations().isEmpty()) {
	        List<ResumeEducation> updatedEducation = convertToEducationEntities(resumeDto.getResumeEducations());
	        resumeEducationRepository.saveAll(updatedEducation);
	        existingResume.setResumeEducations(updatedEducation);
	    }

	    // Update experience
	    if (resumeDto.getResumeExperiences() != null && !resumeDto.getResumeExperiences().isEmpty()) {
	        List<ResumeExperience> updatedExperiences = convertToExperienceEntities(resumeDto.getResumeExperiences());
	        resumeExperienceRepository.saveAll(updatedExperiences);
	        existingResume.setResumeExperiences(updatedExperiences);
	    }

	    // Update projects
	    if (resumeDto.getResumeProjects() != null && !resumeDto.getResumeProjects().isEmpty()) {
	        List<ResumeProject> updatedProjects = convertToProjectEntities(resumeDto.getResumeProjects());
	        resumeProjectRepository.saveAll(updatedProjects);
	        existingResume.setResumeProjects(updatedProjects);
	    }

	    // Update certificates
	    if (resumeDto.getResumeCertificates() != null && !resumeDto.getResumeCertificates().isEmpty()) {
	        List<ResumeCertificates> updatedCertificates = convertToCertificatesEntities(resumeDto.getResumeCertificates());
	        resumeCertificatesRepository.saveAll(updatedCertificates);
	        existingResume.setResumeCertificates(updatedCertificates);
	    }

	    // Update languages
	    if (resumeDto.getResumeLanguages() != null && !resumeDto.getResumeLanguages().isEmpty()) {
	        List<ResumeLanguages> updatedLanguages = convertToLanguagesEntities(resumeDto.getResumeLanguages());
	        resumeLanguagesRepository.saveAll(updatedLanguages);
	        existingResume.setResumeLanguages(updatedLanguages);
	    }

	    // Update interests
	    if (resumeDto.getResumeIntrest() != null && resumeDto.getResumeIntrest().getIntrests() != null) {
	        List<ResumeIntrest> updatedInterests = convertToIntrestEntities(resumeDto.getResumeIntrest());
	        resumeIntrestReposiotry.saveAll(updatedInterests);
	        existingResume.setResumeIntrests(updatedInterests);
	    }

	    // Save and return the updated resume
	    return resumeBuilderRepository.save(existingResume);
	}

	
}
