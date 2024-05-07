package com.talentstream.service;

import java.util.Collections;
import java.util.*;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.talentstream.entity.Applicant;
import com.talentstream.entity.ApplicantProfile;
import com.talentstream.entity.ApplicantSkills;
import com.talentstream.entity.Job;
import com.talentstream.repository.ApplicantProfileRepository;
import com.talentstream.repository.ApplyJobRepository;
import com.talentstream.repository.JobRepository;
import com.talentstream.exception.CustomException;
import com.talentstream.repository.RegisterRepository;
import com.talentstream.repository.SavedJobRepository;

@Service
public class FinRecommendedJobService {

	@Autowired
    private JobRepository jobRepository;

    @Autowired
    private ApplicantProfileRepository applicantRepository;

	@Autowired
    private RegisterRepository registerRepository;
	
	@Autowired 
	private JobService jobService;
	
	@Autowired
    private RegisterRepository applicantRepository1;

	@Autowired
	   private ApplyJobRepository applyJobRepository;
	
	@Autowired
    private SavedJobRepository savedJobRepository;
	
    public List<Job> findJobsMatchingApplicantSkills(long applicantId) {
    	try {
            ApplicantProfile applicantProfile = applicantRepository.findByApplicantId(applicantId);
            Applicant applicant = registerRepository.findById(applicantId);
         //   if (applicantProfile == null || !applicant.getAppicantStatus().equalsIgnoreCase("active")) {
            if (applicantProfile == null) {
                return Collections.emptyList();
            }
 
            Set<ApplicantSkills> applicantSkills = applicantProfile.getSkillsRequired();
            Set<String> lowercaseApplicantSkillNames = applicantSkills.stream()
                    .map(skill -> skill.getSkillName().toLowerCase())
                    .collect(Collectors.toSet());
 
            List<Job> matchingJobs = jobRepository.findBySkillsRequiredIgnoreCaseAndSkillNameIn(lowercaseApplicantSkillNames);
                      
         // Filter the matching jobs by status
            matchingJobs = matchingJobs.stream()
                    .filter(job -> job.getStatus().equalsIgnoreCase("active")) // Assuming status is stored as a String
                    .collect(Collectors.toList());
            
            return matchingJobs;
        } catch (Exception e) {           
            throw new CustomException("Error while finding recommended jobs", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
   public long countRecommendedJobsForApplicant(long applicantId) {
        try {
        	Optional<ApplicantProfile> optionalApplicant = applicantRepository.findByApplicantIdWithSkills(applicantId);
        	Applicant applicant1 = registerRepository.findById(applicantId);
            //if (optionalApplicant.isEmpty() )|| !applicant1.getAppicantStatus().equalsIgnoreCase("active")){ 
            	 if (optionalApplicant.isEmpty() )
            	{
                // Return a specific indicator, for example, -1 to signify that the applicant is not found
            	List<Job> mathedJobs=jobService.getJobsByPromoteState(applicantId,"yes");
            	return mathedJobs.size();
            }
 
            ApplicantProfile applicant = optionalApplicant.get();
 
            Set<String> lowercaseApplicantSkillNames = applicant.getSkillsRequired().stream()
                    .map(skill -> skill.getSkillName().toLowerCase())
                    .collect(Collectors.toSet());
 

            List<Job> matchingJobs = findJobsMatchingApplicantProfile(applicant);
            long recommendedJobCount = matchingJobs.size();
            
         
            
            return recommendedJobCount;
        } catch (Exception e) {
        	 e.printStackTrace();
            // Handle exceptions as needed
            throw new CustomException("Error while counting recommended jobs for the applicant", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   public List<Job> findJobsMatchingApplicantProfile(ApplicantProfile applicantProfile) {
	    try {
	        Set<String> lowercaseApplicantSkillNames = applicantProfile.getSkillsRequired().stream()
	                .map(skill -> skill.getSkillName().toLowerCase())
	                .collect(Collectors.toSet());

	        Set<String> preferredLocations = applicantProfile.getPreferredJobLocations();
	        Integer experience = null;

	        try {
	            experience = Integer.parseInt(applicantProfile.getExperience());
	        } catch (NumberFormatException e) {
	            System.out.println("Warning: Unable to parse experience as Integer");
	        }

	        String specialization = applicantProfile.getSpecialization();

	        System.out.println(applicantProfile.getApplicant().getId());
	        List<Object[]> result = jobRepository.findJobsMatchingApplicantProfile(
	        		applicantProfile.getApplicant().getId(),
	                lowercaseApplicantSkillNames,
	                preferredLocations,
	                experience,
	                specialization
	        );
            
	        List<Job> matchingJobs = new ArrayList<>();
	        for (Object[] array : result) {
               Job job = (Job) array[0];
               job.setIsSaved((String)array[1]);
               String isSaved = (String) array[1];
              System.out.println(job.getId()+"-----"+job.getIsSaved());
              job.setIsSaved(isSaved != null ? isSaved : ""); 
               matchingJobs.add(job);
               System.out.println(job.getIsSaved());
           }
	        long applicantId=applicantProfile.getApplicant().getId();
	        Applicant applicant = applicantRepository1.findById(applicantId);
           matchingJobs = matchingJobs.stream()
        		   .filter(job ->
//        				    job.getStatus().equalsIgnoreCase("active") && 
        				   !applyJobRepository.existsByApplicantAndJob(applicant, job) && !isJobSavedByApplicant(job.getId(), applicantId)) // Assuming status is stored as a String
                   .collect(Collectors.toList());

	        return matchingJobs;

	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new CustomException("Error while finding recommended jobs", HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
   private boolean isJobSavedByApplicant(long jobId, long applicantId) {
       return savedJobRepository.existsByApplicantIdAndJobId(applicantId, jobId);
   }
}
