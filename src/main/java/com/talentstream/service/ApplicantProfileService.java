package com.talentstream.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.talentstream.exception.CustomException;
import com.talentstream.dto.ApplicantProfileDTO;
import com.talentstream.dto.ApplicantProfileViewDTO;
import com.talentstream.dto.BasicDetailsDTO;
import com.talentstream.entity.Applicant;
import com.talentstream.entity.ApplicantProfile;
import com.talentstream.entity.ApplicantProfileUpdateDTO;
import com.talentstream.entity.ApplicantSkills;
import com.talentstream.entity.SkillBadge;
import com.talentstream.repository.ApplicantProfileRepository;
import com.talentstream.repository.ApplicantSkillBadgeRepository;
import com.talentstream.repository.ApplicantSkillsRepository;
import com.talentstream.repository.RegisterRepository;
import com.talentstream.repository.SkillBadgeRepository;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Service
public class ApplicantProfileService {
	private final ApplicantProfileRepository applicantProfileRepository;
	private final RegisterRepository applicantService;

	@Autowired
	private ApplicantSkillsRepository applicantSkillsRepository;
	
	@Autowired
    private SkillBadgeRepository skillBadgeRepository;
	
	@Autowired
    private ApplicantSkillBadgeRepository applicantSkillBadgeRepository;

	@Autowired
	public ApplicantProfileService(ApplicantProfileRepository applicantProfileRepository,
			RegisterRepository applicantService) {
		this.applicantProfileRepository = applicantProfileRepository;
		this.applicantService = applicantService;

	}

	// Creates or updates the applicant's profile; throws CustomException if
	// applicant is not found or profile exists.
	public String createOrUpdateApplicantProfile(long applicantId, ApplicantProfileDTO applicantProfileDTO)
			throws IOException {
		Applicant applicant = applicantService.getApplicantById(applicantId);
		if (applicant == null)
			throw new CustomException("Applicant not found for ID: " + applicantId, HttpStatus.NOT_FOUND);
		else {
			ApplicantProfile existingProfile = applicantProfileRepository.findByApplicantId(applicantId);
			if (existingProfile == null) {
				ApplicantProfile applicantProfile = convertDTOToEntity(applicantProfileDTO);
				applicantProfile.setApplicant(applicant);
				applicantProfileRepository.save(applicantProfile);
				return "profile saved sucessfully";
			} else {
				throw new CustomException("Profile for this applicant already exists", HttpStatus.BAD_REQUEST);
			}
		}
	}

	// Retrieves the applicant's profile as a view DTO; throws
	// EntityNotFoundException if applicant is not found.
	public ApplicantProfileViewDTO getApplicantProfileViewDTO(long applicantId) {
		Applicant applicant = applicantService.findById(applicantId);
		ApplicantProfile applicantProfile = null;
		if (applicant == null)
			throw new EntityNotFoundException("Applicant not found with id: " + applicantId);

		try {
			applicantProfile = applicantProfileRepository.findByApplicantId(applicantId);
		} catch (Exception e) {
			return convertToDTO(applicant, applicantProfile);
		}

		return convertToDTO(applicant, applicantProfile);
	}

	// Converts applicant and profile entities to a view DTO.
	private ApplicantProfileViewDTO convertToDTO(Applicant applicant, ApplicantProfile applicantProfile) {
		ApplicantProfileViewDTO dto = new ApplicantProfileViewDTO();
		if (applicantProfile == null) {
			dto.setApplicant(applicant);
		} else {
			dto.setApplicant(applicant);
			dto.setBasicDetails(applicantProfile.getBasicDetails());
			dto.setxClassDetails(applicantProfile.getxClassDetails());
			dto.setIntermediateDetails(applicantProfile.getIntermediateDetails());
			dto.setGraduationDetails(applicantProfile.getGraduationDetails());
			Set<ApplicantSkills> unmatchedSkills = applicantProfile.getSkillsRequired().stream()
				    .filter(skill -> 
				        applicantProfile.getApplicant().getApplicantSkillBadges().stream()
				            .noneMatch(badge -> 
				                badge.getSkillBadge().getName().trim().equalsIgnoreCase(skill.getSkillName().trim()) 
				                && !badge.getFlag().equalsIgnoreCase("removed") // Exclude badges with flag 'removed'
				            )
				    )
				    .collect(Collectors.toSet());  // Collect unmatched skills as a Set
			// Setting the unmatched skills into the DTO
			dto.setSkillsRequired(unmatchedSkills);
				
			dto.setExperienceDetails(applicantProfile.getExperienceDetails());
			dto.setExperience(applicantProfile.getExperience());
			dto.setQualification(applicantProfile.getQualification());
			dto.setSpecialization(applicantProfile.getSpecialization());
			dto.setPreferredJobLocations(applicantProfile.getPreferredJobLocations());
		}
		return dto;
	}

	// Retrieves the applicant's profile by ID and converts it to DTO; throws
	// CustomException if not found.
	public ApplicantProfileDTO getApplicantProfileById(long applicantId) {
		try {
			ApplicantProfile applicantProfile = applicantProfileRepository.findByApplicantId(applicantId);

			if (applicantProfile != null) {
				return convertEntityToDTO(applicantProfile);
			} else {

				throw new CustomException("Please Fill your  Profile", HttpStatus.NOT_FOUND);
			}
		} catch (CustomException e) {
			if (HttpStatus.NOT_FOUND.equals(e.getStatus())) {
				throw e;
			} else {

				throw new CustomException("Failed to get profile for applicant ID: " + applicantId,
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	// Converts a DTO to an applicant profile entity.
	private ApplicantProfile convertDTOToEntity(ApplicantProfileDTO applicantProfileDTO) {
		ApplicantProfile applicantProfile = new ApplicantProfile();
		applicantProfile.setBasicDetails(applicantProfileDTO.getBasicDetails());
		applicantProfile.setSkillsRequired(applicantProfileDTO.getSkillsRequired());
		applicantProfile.setGraduationDetails(applicantProfileDTO.getGraduationDetails());
		applicantProfile.setIntermediateDetails(applicantProfileDTO.getIntermediateDetails());
		applicantProfile.setxClassDetails(applicantProfileDTO.getxClassDetails());
		applicantProfile.setExperienceDetails(applicantProfileDTO.getExperienceDetails());
		applicantProfile.setRoles(applicantProfileDTO.getRoles());
		applicantProfile.setExperience(applicantProfileDTO.getExperience());
		applicantProfile.setQualification(applicantProfileDTO.getQualification());
		applicantProfile.setSpecialization(applicantProfileDTO.getSpecialization());
		applicantProfile.setPreferredJobLocations(applicantProfileDTO.getPreferredJobLocations());
		if (applicantProfileDTO.getRoles() == null) {
			applicantProfile.setRoles("ROLE_JOBAPPLICANT");
		} else {
			applicantProfile.setRoles(applicantProfileDTO.getRoles());
		}

		return applicantProfile;

	}

	// Converts an applicant profile entity to a DTO; returns null if entity is
	// null.
	public static ApplicantProfileDTO convertEntityToDTO(ApplicantProfile applicantProfile) {
		if (applicantProfile == null) {
			System.out.println("not exist");
			return null;
		}
		ApplicantProfileDTO applicantProfileDTO = new ApplicantProfileDTO();
		applicantProfileDTO.setBasicDetails(applicantProfile.getBasicDetails());
		applicantProfileDTO.setGraduationDetails(applicantProfile.getGraduationDetails());
		applicantProfileDTO.setIntermediateDetails(applicantProfile.getIntermediateDetails());
		applicantProfileDTO.setxClassDetails(applicantProfile.getxClassDetails());
		applicantProfileDTO.setSkillsRequired(applicantProfile.getSkillsRequired());
		applicantProfileDTO.setExperienceDetails(applicantProfile.getExperienceDetails());
		applicantProfileDTO.setRoles(applicantProfile.getRoles());
		return applicantProfileDTO;
	}

	// Updates the applicant's basic details and profile; throws CustomException if
	// applicant is not found.
	public String updateApplicantProfile(long applicantId, ApplicantProfileViewDTO updatedProfileDTO) {
		Applicant applicant = applicantService.getApplicantById(applicantId);
		if (applicant == null)
			throw new CustomException("Applicant not found " + applicantId, HttpStatus.NOT_FOUND);
		else {
			applicant.setName(updatedProfileDTO.getApplicant().getName());
			applicant.setMobilenumber(updatedProfileDTO.getApplicant().getMobilenumber());
			applicantService.save(applicant);
		}
		ApplicantProfile existingProfile = applicantProfileRepository.findByApplicantId(applicantId);
		if (existingProfile == null) {
			throw new CustomException("Your  profile not found and please fill profile " + applicantId,
					HttpStatus.NOT_FOUND);
		} else {
			existingProfile.setBasicDetails(updatedProfileDTO.getBasicDetails());
			existingProfile.setExperienceDetails(updatedProfileDTO.getExperienceDetails());
			existingProfile.setGraduationDetails(updatedProfileDTO.getGraduationDetails());
			existingProfile.setIntermediateDetails(updatedProfileDTO.getIntermediateDetails());
			existingProfile.setSkillsRequired(updatedProfileDTO.getSkillsRequired());
			existingProfile.setQualification(updatedProfileDTO.getQualification());
			existingProfile.setSpecialization(updatedProfileDTO.getSpecialization());
			existingProfile.setxClassDetails(updatedProfileDTO.getxClassDetails());
			applicantProfileRepository.save(existingProfile);
		}
		return "profile saved sucessfully";
	}

	// Deletes the applicant's profile by ID; throws CustomException if deletion
	// fails.
	public void deleteApplicantProfile(long applicantId) {
		try {
			applicantProfileRepository.deleteById((int) applicantId);
		} catch (Exception e) {
			throw new CustomException("Failed to delete profile for applicant ID: " + applicantId,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Retrieves the profile ID for the applicant; returns 0 if not found.
	public int getApplicantProfileById1(int applicantId) {

		ApplicantProfile applicantProfile = applicantProfileRepository.findByApplicantId(applicantId);

		return applicantProfile != null ? applicantProfile.getProfileid() : 0;

	}

	// Updates the applicant's basic details in the profile; uses @Transactional for
	// atomicity.
	@Transactional
	public void updateBasicDetails(Long applicantId, BasicDetailsDTO basicDetailsDTO) {
		ApplicantProfile applicantProfile = applicantProfileRepository.findByApplicantId(applicantId);

		applicantProfile.getBasicDetails().setFirstName(basicDetailsDTO.getFirstName());
		applicantProfile.getBasicDetails().setLastName(basicDetailsDTO.getLastName());
		applicantProfile.getBasicDetails().setDateOfBirth(basicDetailsDTO.getDateOfBirth());
		applicantProfile.getBasicDetails().setAddress(basicDetailsDTO.getAddress());
		applicantProfile.getBasicDetails().setCity(basicDetailsDTO.getCity());
		applicantProfile.getBasicDetails().setState(basicDetailsDTO.getState());
		applicantProfile.getBasicDetails().setPincode(basicDetailsDTO.getPincode());
		applicantProfile.getBasicDetails().setEmail(basicDetailsDTO.getEmail());
		applicantProfile.getBasicDetails().setAlternatePhoneNumber(basicDetailsDTO.getAlternatePhoneNumber());

		applicantProfileRepository.save(applicantProfile);
	}

	// Updates the applicant's profile with new information; throws CustomException
	// if profile is not found.
	@Transactional
	public String updateApplicantProfile1(long applicantId, ApplicantProfileUpdateDTO updatedProfileDTO) {
		// Find applicant
		Applicant applicant = applicantService.getApplicantById(applicantId);

		// Find existing profile
		ApplicantProfile existingProfile = applicantProfileRepository.findByApplicantId(applicantId);
		if (existingProfile == null) {
			throw new CustomException("Your profile not found and please fill profile " + applicantId,
					HttpStatus.NOT_FOUND);
		} else {
			
			 // Extract existing skills from the database
	        Set<String> existingSkillNames = existingProfile.getSkillsRequired().stream()
	                .map(ApplicantSkills::getSkillName)
	                .collect(Collectors.toSet());

	        // Extract updated skills from the DTO
	        Set<String> updatedSkillNames = new HashSet<>();
	        if (updatedProfileDTO.getSkillsRequired() != null) {
	            for (ApplicantProfileUpdateDTO.SkillDTO skillDTO : updatedProfileDTO.getSkillsRequired()) {
	                updatedSkillNames.add(skillDTO.getSkillName());
	            }
	        }

	        // Find removed skills (skills in the database but not in the updated list)
	        Set<String> removedSkills = new HashSet<>(existingSkillNames);
	        removedSkills.removeAll(updatedSkillNames);
	        
	     // Find added skills (skills in the updated list but not in the database)
	        Set<String> addedSkills = new HashSet<>(updatedSkillNames);
	        addedSkills.removeAll(existingSkillNames);
	        
	        if(addedSkills != null) {
	        	for(String skillBadgeName: addedSkills) {
	        		try {
	        			SkillBadge skillBadge = skillBadgeRepository.findByName(skillBadgeName);
	        	    	applicantSkillBadgeRepository.updateFlagAsAdded(applicantId, skillBadge.getId());
	        	    }catch(Exception e) {
	        	    	System.out.println(e.getMessage());
	        	    }
	        	}
	        }
	        
	        if(removedSkills != null) {
	        for(String skillBadgeName: removedSkills ) {
	        	
	        	 
	        	    SkillBadge skillBadge = skillBadgeRepository.findByName(skillBadgeName);
	        	    System.out.println(skillBadge.getId()+"   "+skillBadge.getName());
	        	    try {
	        	    	applicantSkillBadgeRepository.updateFlagAsRemoved(applicantId, skillBadge.getId());
//	        	    applicantSkillBadgeRepository.deleteByApplicantIdAndSkillBadgeId(applicantId, skillBadge.getId());
	        	    }catch(Exception e) {
	        	    	System.out.println(e.getMessage());
	        	    }
	        }
	        }
			// Update the necessary fields
			existingProfile.setExperience(updatedProfileDTO.getExperience());
			existingProfile.setQualification(updatedProfileDTO.getQualification());
			existingProfile.setSpecialization(updatedProfileDTO.getSpecialization());
			existingProfile.setPreferredJobLocations(new HashSet<>(updatedProfileDTO.getPreferredJobLocations()));

			 
			// Update skills required
			Set<ApplicantSkills> updatedSkills = new HashSet<>();
			if (updatedProfileDTO.getSkillsRequired() != null) {
				for (ApplicantProfileUpdateDTO.SkillDTO skillDTO : updatedProfileDTO.getSkillsRequired()) {
					ApplicantSkills skill = new ApplicantSkills();
					skill.setSkillName(skillDTO.getSkillName());
					skill.setExperience(skillDTO.getExperience());
					updatedSkills.add(skill);
				}
			}
			existingProfile.setSkillsRequired(updatedSkills);

			// Save the updated profile
			applicantProfileRepository.save(existingProfile);
		}

		return "Profile saved successfully";
	}

	public Applicant changeResumeSource(long applicantid) {
		Applicant applicant = applicantService.getApplicantById(applicantid);

		applicant.setLocalResume(false);
		return applicantService.save(applicant);

	}

}
