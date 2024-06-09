package com.talentstream.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.talentstream.dto.ApplicantProfileDTO;
import com.talentstream.dto.ApplicantProfileViewDTO;
import com.talentstream.dto.BasicDetailsDTO;
import com.talentstream.exception.CustomException;
import com.talentstream.service.ApplicantProfileService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@CrossOrigin("*")
@RestController
@RequestMapping("/applicantprofile")
public class ApplicantProfileController {
	private final ApplicantProfileService applicantProfileService;
	
	  private static final Logger logger = LoggerFactory.getLogger(ApplicantProfileController.class);
    
	  @Autowired
    public ApplicantProfileController(ApplicantProfileService applicantProfileService) {
        this.applicantProfileService = applicantProfileService;		
        logger.debug("ApplicantProfileController initialized");
    }

    @PostMapping("/createprofile/{applicantid}")
    public ResponseEntity<String> createOrUpdateApplicantProfile(@PathVariable long applicantid, @RequestBody ApplicantProfileDTO applicantProfileDTO) throws IOException {
    	 logger.debug("Request to create/update profile for applicantId: {}", applicantid);
        
        try {
            String result = applicantProfileService.createOrUpdateApplicantProfile(applicantid, applicantProfileDTO);
            logger.info("Profile created/updated successfully for applicantId: {}", applicantid);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (CustomException e) {
        	  logger.error("Error creating/updating profile for applicantId: {}: {}", applicantid, e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        }
        catch (Exception e) {
            logger.error("Unexpected error creating/updating profile for applicantId: {}", applicantid, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getdetails/{applicantid}")
    public ResponseEntity<?> getApplicantProfileById(@PathVariable long applicantid) {
    	logger.debug("Fetching details for applicantId: {}", applicantid);
        try {
            ApplicantProfileDTO applicantProfileDTO = applicantProfileService.getApplicantProfileById(applicantid);
            logger.info("Details fetched successfully for applicantId: {}", applicantid);
            return ResponseEntity.ok(applicantProfileDTO);
        } catch (CustomException e) {
        	 logger.error("Failed to fetch details for applicantId: {}: {}", applicantid, e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(errorResponse);
        }
        
    }
       @DeleteMapping("/deletedetails/{applicantId}")
       public ResponseEntity<Void> deleteApplicantProfile(@PathVariable int applicantId) {
    	   logger.debug("Request to delete profile for applicantId: {}", applicantId);
    	   try {
               applicantProfileService.deleteApplicantProfile(applicantId);
               logger.info("Profile deleted successfully for applicantId: {}", applicantId);
               return ResponseEntity.noContent().build();
           } catch (CustomException e) {
        	   logger.error("Error deleting profile for applicantId: {}: {}", applicantId, e.getMessage());
               return ResponseEntity.status(e.getStatus()).build();
           }
       }
       
       @PutMapping("/updateprofile/{applicantid}")
       public ResponseEntity<String> updateApplicantProfile(@PathVariable long applicantid, @RequestBody ApplicantProfileViewDTO updatedProfileDTO) throws IOException {
    	    logger.debug("Request to update profile for applicantId: {}", applicantid);
           try {
        	           	   
               String result = applicantProfileService.updateApplicantProfile(applicantid, updatedProfileDTO);
               logger.info("Profile updated successfully for applicantId: {}", applicantid);
               return ResponseEntity.status(HttpStatus.OK).body(result);
           } catch (CustomException e) {
        	   logger.error("Error updating profile for applicantId: {}: {}", applicantid, e.getMessage());
               return ResponseEntity.status(e.getStatus()).body(e.getMessage());
           }
       }
       
       @GetMapping("/{applicantId}/profile-view")
       public ResponseEntity<ApplicantProfileViewDTO> getApplicantProfileViewDTO(@PathVariable long applicantId) {
    	   logger.debug("Fetching profile view for applicantId: {}", applicantId);
           try {
               ApplicantProfileViewDTO dto = applicantProfileService.getApplicantProfileViewDTO(applicantId);
               logger.info("Profile view fetched successfully for applicantId: {}", applicantId);
               return ResponseEntity.ok(dto);
           } catch (EntityNotFoundException e) {
        	   logger.error("Profile not found for applicantId: {}", applicantId);
               return ResponseEntity.notFound().build();
           }
       }
       @GetMapping("/{applicantId}/profile-view1")
       public ResponseEntity<ApplicantProfileViewDTO> getApplicantProfileViewDTO1(@PathVariable long applicantId) {
    	   logger.debug("Fetching profile view for applicantId: {}", applicantId);
           try {
               ApplicantProfileViewDTO dto = applicantProfileService.getApplicantProfileViewDTO(applicantId);
               logger.info("Profile view fetched successfully for applicantId: {}", applicantId);
               return ResponseEntity.ok(dto);
           } catch (EntityNotFoundException e) {
        	   logger.error("Profile not found for applicantId: {}", applicantId);
               return ResponseEntity.notFound().build();
           }
       }
	@GetMapping("/{applicantId}/profileid")
       public int getApplicantProfileId(@PathVariable int applicantId) {
		logger.debug("Fetching profile ID for applicantId: {}", applicantId);
           return applicantProfileService.getApplicantProfileById1(applicantId);
       }
	 @PutMapping("/{id}/basic-details")
	    public ResponseEntity<Void> updateBasicDetails(@PathVariable Long id, @RequestBody BasicDetailsDTO basicDetailsDTO) {
	        applicantProfileService.updateBasicDetails(id, basicDetailsDTO);
	        return ResponseEntity.ok().build();
	    }
      
}
