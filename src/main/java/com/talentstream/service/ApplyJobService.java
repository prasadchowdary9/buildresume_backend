package com.talentstream.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.talentstream.dto.JobDTO;
import com.talentstream.dto.RecuriterSkillsDTO;
import com.talentstream.entity.Alerts;
import com.talentstream.entity.Applicant;
import com.talentstream.entity.ApplicantJobInterviewDTO;
import com.talentstream.entity.ApplicantProfile;
import com.talentstream.entity.ApplicantStatusHistory;
import com.talentstream.entity.AppliedApplicantInfo;
import com.talentstream.entity.AppliedApplicantInfoDTO;
import com.talentstream.entity.ApplyJob;
import com.talentstream.entity.Job;
import com.talentstream.entity.MatchTypes;
import com.talentstream.entity.AppliedApplicantInfo;
import java.util.stream.Collectors;

import javax.mail.internet.InternetAddress;

import com.talentstream.entity.JobRecruiter;
import com.talentstream.entity.RecuriterSkills;
import com.talentstream.entity.SavedJob;
import com.talentstream.repository.AlertsRepository;
import com.talentstream.repository.ApplicantProfileRepository;
import com.talentstream.repository.ApplicantStatusHistoryRepository;
import com.talentstream.repository.ApplyJobRepository;
import com.talentstream.repository.JobRepository;
import com.talentstream.repository.JobRecruiterRepository;
import com.talentstream.repository.RegisterRepository;
import com.talentstream.repository.SavedJobRepository;
import com.talentstream.repository.ScheduleInterviewRepository; 
import jakarta.persistence.EntityNotFoundException;
import com.talentstream.exception.CustomException;
@Service
public class ApplyJobService {
	 @Autowired
	   private ApplyJobRepository applyJobRepository;	
	 @Autowired
	   private ScheduleInterviewRepository scheduleInterviewRepository;	
	 @Autowired
		private CompanyLogoService companyLogoService;
	    @Autowired
	    private JobRepository jobRepository;
	    @Autowired
	    private RegisterRepository applicantRepository;
	    @Autowired
	    private ApplicantStatusHistoryRepository statusHistoryRepository;
	    @Autowired
	    private JavaMailSender javaMailSender;
	    @Autowired
	    private AlertsRepository alertsRepository;
	@Autowired
	    private JobRecruiterRepository jobRecruiterRepository;
	@Autowired
	private ApplicantProfileRepository applicantProfileRepo;
	@Autowired
    private SavedJobRepository savedJobRepository;
	public void markAlertAsSeen(long alertsId) {
	    Optional<Alerts> alertOptional = alertsRepository.findById(alertsId);
	    if (alertOptional.isPresent()) {
	        Alerts alert = alertOptional.get();
	        alert.setSeen(true);
	        alertsRepository.save(alert);
	    } else {
	        throw new EntityNotFoundException("Alert with id " + alertsId + " not found.");
	    }
	}
public String ApplicantApplyJob(long  applicantId, long jobId) {
	
	    	
	    	try {
	            Applicant applicant = applicantRepository.findById(applicantId);
	            Job job = jobRepository.findById(jobId).orElse(null);
 
	            if (applicant == null || job == null) {
	                throw new CustomException("Applicant ID or Job ID not found", HttpStatus.NOT_FOUND);
	            }
 
	            else{
	            	if (applyJobRepository.existsByApplicantAndJob(applicant, job)) {
	                       	return "Job has already been applied by the applicant";
	            	}else {
	            		ApplyJob applyJob = new ApplyJob();
	    	            applyJob.setApplicant(applicant);
	    	            applyJob.setJob(job);
	    	            applyJobRepository.save(applyJob);
	    	       
	    	                if (savedJobRepository.existsByApplicantIdAndJobId(applicant.getId(), job.getId())) {
	    	               	    System.out.println("saved changed");
	    	               	 SavedJob savedJob = savedJobRepository.findByApplicantAndJob(applicant, job);
	    	               	    
	    	                    savedJob.setApplicant(applicant);                 
	    	                    savedJob.setSaveJobStatus("removed from saved");
	    	                    jobRepository.save(job);
	    	                    savedJob.setJob(job);
	    	                    savedJobRepository.save(savedJob);
	    	                } 
	    	              
	    	            
	    	            job.setJobStatus("Already Applied");
	    	            job.setAlertCount(job.getAlertCount()+1);
	    	            job.setRecentApplicationDateTime(LocalDateTime.now());
				job.setNewStatus("newapplicants");
	    				jobRepository.save(job);
	    	            
	    	            // Increment alert count
	    		        //incrementAlertCount(applyJob.getApplicant());
	    		        
	    		        //SaveStatusHistory
	    	            saveStatusHistory(applyJob, applyJob.getApplicantStatus());
	    	            Job jobs=applyJob.getJob();
	    	            if(jobs!=null) {
	    	            	JobRecruiter recruiter=jobs.getJobRecruiter();
	    	            	
	    	            	
	    	            	if(recruiter!=null) {
	    	            		String companyName=recruiter.getCompanyname();
	    	            		if(companyName!=null) {
	    	            			String cN=recruiter.getCompanyname();
	    	            			   
	    	            			String jobTitle = jobs.getJobTitle();
	    	            			recruiter.setAlertCount(recruiter.getAlertCount()+1);
	    	            			jobRecruiterRepository.save(recruiter);
	    	            			sendAlerts(applyJob,applyJob.getApplicantStatus(),cN,jobTitle);
	    	            			return "Job applied successfully";
	    	            		}
	    	            	}
	    	            }return "Company information not found for the given ApplyJob";
	            		}
	            	}
	            }catch (CustomException ex) {
	                    throw ex;
	             } catch (Exception e) {
	                    throw new CustomException("An error occurred while applying for the job: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	             }
	      }
public long countAppliedJobsForApplicant(long applicantId) {
    try {
        // Check if the applicant exists
        if (!applicantRepository.existsById(applicantId)) {
            // Throw CustomException if the applicant is not found
            throw new CustomException("Applicant not found", HttpStatus.NOT_FOUND);
        }
        // Use the custom query to count applied jobs
        return applyJobRepository.countByApplicantId(applicantId);
    } catch (CustomException e) {
        throw e; // Re-throw CustomException as is
    } catch (Exception e) {
        // Handle other exceptions as needed
        throw new CustomException("Error while counting applied jobs for the applicant", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
	    //This method is to increment count of alerts whenever recruiter updating the status
	    private void incrementAlertCount(Applicant applicant) {
			// TODO Auto-generated method stub
	    	if (applicant != null) {
	            int currentAlertCount = applicant.getAlertCount();
	            applicant.setAlertCount(currentAlertCount + 1);
	            applicantRepository.save(applicant);
	        }
		}
	    //This method is to display alerts whenever we click on Alerts
	    private
	    void sendAlerts(ApplyJob applyJob, String applicantStatus, String cN, String jobTitle) {
			// TODO Auto-generated method stub
	    	Alerts alerts=new Alerts();
			alerts.setApplyJob(applyJob);
			alerts.setApplicant(applyJob.getApplicant());
			alerts.setCompanyName(cN);
			alerts.setStatus(applicantStatus);			
			alerts.setJobTitle(jobTitle);
            LocalDateTime currentDate = LocalDateTime.now();
            
            // Get the current change date and time
            LocalDateTime currentChangeDateTime = currentDate;
          // System.out.println(currentChangeDateTime + " Utc");
            // Add 5 hours and 30 minutes to the current change date and time
            LocalDateTime updatedChangeDateTime = currentChangeDateTime
                    .plusHours(5)
                    .plusMinutes(30);
			alerts.setChangeDate(updatedChangeDateTime);
			alertsRepository.save(alerts);
			// Send email to the applicant
	        sendEmailToApplicant(applyJob.getApplicant().getEmail(), cN, applicantStatus,jobTitle);
		}
	  //This method is to send interview status to the applicant mail id
	    private void sendEmailToApplicant(String toEmail, String cN, String applicantStatus,String jobTitle) {
  			// TODO Auto-generated method stub
  			try {
  			    javax.mail.internet.MimeMessage message = javaMailSender.createMimeMessage();
  			    MimeMessageHelper helper = new MimeMessageHelper(message, true);

  			    // Set the email properties
  			    helper.setFrom(new InternetAddress( "no-reply@bitlabs.in" ,"bitLabs Jobs"));
  			    helper.setTo(toEmail);
  			    helper.setSubject("Your Application for " + jobTitle + " at " + cN + " has been Submitted");

  			    // Customize your email content
  			    String content = "Dear Applicant,\n\n"
  			            + "Thank you for applying for the position of " + jobTitle + " at " + cN + " through bitLabs Jobs. We have received your application and it has been successfully submitted to the employer. " + "\n\n"
  			            + "What’s Next?\n\n"
  			            + "1. Your application will be screened.\n"
  			            + "2. If you are shortlisted, the employer will contact you directly for the next steps.\n"
  			            + "3. Meanwhile, you can track your application status by logging into your bitLabs Jobs account & by clicking on applied jobs.\n\n"
  			            + "Happy job searching! \n\n"
  			            + "Regards\n"
  			            + "The bitLabs Jobs Team.\n\n"
  			            + "This is an auto-generated email. Please do not reply.";
  			    helper.setText(content);

  			    // Send the email
  			    javaMailSender.send(message);
  			} catch (Exception e) {
  	            // Handle exceptions, log, and consider appropriate error handling
  	        	e.printStackTrace();
  	        }
  		}
		
		//This method is to save the track of statuses that updated by recruiter
		private void saveStatusHistory(ApplyJob applyJob, String applicationStatus) {
			
			// Get the current date and time
	        LocalDateTime currentDateTime = LocalDateTime.now();
	        System.out.println("before addition "+currentDateTime);
	        
	        // Add 5 hours and 30 minutes to the current date and time
	        LocalDateTime updatedDateTime = currentDateTime.plus(Duration.ofHours(5).plusMinutes(30));
	        System.out.println("after addition "+updatedDateTime);
	        
	        // Convert to LocalDate if required
	        LocalDate updatedDate = updatedDateTime.toLocalDate();
	        
			// TODO Auto-generated method stub
			ApplicantStatusHistory statusHistory=new ApplicantStatusHistory();
			statusHistory.setApplyJob(applyJob);
			statusHistory.setStatus(applicationStatus);
			statusHistory.setChangeDate(updatedDate);
			statusHistoryRepository.save(statusHistory);
		}
	    public List<ApplyJob> getAppliedApplicantsForJob(Long jobId) {
	    	 try {
	             return applyJobRepository.findByJobId(jobId);
	         } catch (Exception e) {
	             throw new CustomException("Failed to retrieve applied applicants for the job", HttpStatus.INTERNAL_SERVER_ERROR);
	         }
	    }
	    public List<JobDTO> getAppliedJobsForApplicant(long applicantId) {
			List<JobDTO> result = new ArrayList<>();
    try {
        List<ApplyJob> appliedJobs = applyJobRepository.findByApplicantId(applicantId);
        for (ApplyJob appliedJob : appliedJobs) {
            Job job = appliedJob.getJob();
            JobDTO jobDTO = new JobDTO();
            jobDTO.setId(job.getId());
            jobDTO.setRecruiterId(job.getJobRecruiter().getRecruiterId());
            jobDTO.setCompanyname(job.getJobRecruiter().getCompanyname());
            jobDTO.setJobTitle(job.getJobTitle());
            jobDTO.setMinimumExperience(job.getMinimumExperience());
            jobDTO.setMaxSalary(job.getMaxSalary());
            jobDTO.setMinSalary(job.getMinSalary());
            jobDTO.setLocation(job.getLocation());
            jobDTO.setEmployeeType(job.getEmployeeType());
            jobDTO.setIndustryType(job.getIndustryType());
            jobDTO.setMinimumQualification(job.getMinimumQualification());
            jobDTO.setSpecialization(job.getSpecialization());
            Set<RecuriterSkillsDTO> skillsDTOSet = new HashSet<>();
            for (RecuriterSkills skill : job.getSkillsRequired()) {
                RecuriterSkillsDTO skillDTO = new RecuriterSkillsDTO();
                skillDTO.setSkillName(skill.getSkillName());
                skillsDTOSet.add(skillDTO);
            }
            jobDTO.setSkillsRequired(skillsDTOSet);
            jobDTO.setDescription(job.getDescription());
            jobDTO.setCreationDate(job.getCreationDate());
            jobDTO.setCompanyname(job.getJobRecruiter().getCompanyname());
            jobDTO.setMobilenumber(job.getJobRecruiter().getMobilenumber());
            jobDTO.setEmail(job.getJobRecruiter().getEmail());	           
            jobDTO.setApplyJobId(appliedJob.getApplyjobid());
 
            result.add(jobDTO);
        }
    } catch (Exception e) {
        throw new CustomException("Failed to get applied jobs for the applicant", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    return result;
  }
	public List<AppliedApplicantInfoDTO> getAppliedApplicants2(long jobRecruiterId, MatchTypes matchTypes, String name, String email, String mobileNumber, String jobTitle, String applicantStatus, Integer minimumExperience, String skillName, String minimumQualification, String location) {
		List<AppliedApplicantInfo> all1 = applyJobRepository.findAppliedApplicantsInfo(jobRecruiterId);
        
        List<AppliedApplicantInfoDTO> all=new ArrayList<>();
	        
	        System.out.println(matchTypes.getName());
	        System.out.println(matchTypes.getMobilenumber());
	        
	        for(AppliedApplicantInfo appliedApplicantInfo : all1) {
	        	try {
	        	long id1=appliedApplicantInfo.getId();
	            ApplicantProfile applicantProfile=applicantProfileRepo.findByApplicantId(id1);
	            AppliedApplicantInfoDTO dto1=mapToDTO(appliedApplicantInfo);
	            dto1.setExperience(applicantProfile.getExperience());
	            dto1.setMinimumQualification(applicantProfile.getQualification());
	            all.add(dto1);
	        	}catch(Exception e) {
	        		 e.printStackTrace();
	        	}
	        }

	        List<AppliedApplicantInfoDTO> filteredList = null;
	        try {
	            filteredList = all.stream()
	                    .filter(applicant ->
	                            (name == null || applyMatchType(applicant.getName(), name, matchTypes.getName(), "is")) &&
	                            (email == null || applyMatchType(applicant.getEmail(), email, matchTypes.getEmail(), "contains")) &&
	                            (mobileNumber == null || applyMobileType(applicant.getMobilenumber(), mobileNumber, matchTypes.getMobilenumber(), "is")) &&
	                            (jobTitle == null || applyMatchType(applicant.getJobTitle(), jobTitle, matchTypes.getJobTitle(), "contains")) &&
	                            (applicantStatus == null || applyMatchType(applicant.getApplicantStatus(), applicantStatus, matchTypes.getApplicantStatus(), "contains")) &&
//	                            (skillName == null || applyMatchType(applicant.getSkillName(), skillName, matchTypes.getSkillName(), "contains")) &&
	                            (minimumQualification == null || applyMatchType(applicant.getMinimumQualification(), minimumQualification, matchTypes.getMinimumQualification(), "contains")) &&
	                            (location == null || applyMatchType(applicant.getLocation(), location, matchTypes.getLocation(), "contains")) &&
	                            (minimumExperience == null || applyExperienceMatchType(applicant.getExperience(), minimumExperience, matchTypes.getMinimumExperience(), "lessThan")))
	                    .collect(Collectors.toList());
	        } catch (Exception e) {
	            
	            e.printStackTrace();
	            filteredList = new ArrayList<>();
	        }

	     // Eliminate duplicates based on applyjobid while preserving order
	        Set<Long> uniqueApplyJobIds = new HashSet<>();
	        List<AppliedApplicantInfoDTO> uniqueList = new ArrayList<>();

	        for (AppliedApplicantInfoDTO applicant : filteredList) {
	        	try {
	            long applyJobId = applicant.getApplyjobid();
	            if (!uniqueApplyJobIds.contains(applyJobId) && applyJobId >=1) {
	                uniqueApplyJobIds.add(applyJobId);
	                uniqueList.add(applicant);
	            }
	        	}catch(Exception e) {
	        		 e.printStackTrace();
	        	}
	        }
	        return uniqueList;
	    }

	    private boolean applyMatchType(String value, String filterValue, String matchValue, String matchType) {
	        if (matchValue == null) {
	            return true; // If matchValue is null, it means it's not provided, so return true
	        }
	        if (matchValue.equalsIgnoreCase("contains")) {
	            return value.toLowerCase().contains(filterValue.toLowerCase());
	        } else if (matchValue.equalsIgnoreCase("is")) {
	            return value.equalsIgnoreCase(filterValue);
	        }
	        return false;
	    }
	    private boolean applyMobileType(String value, String filterValue, String matchValue, String matchType) {
	        if (matchValue == null) {
	            return true; // If matchValue is null, it means it's not provided, so return true
	        }
	        if (matchValue.equalsIgnoreCase("contains")) {
	            return value.toLowerCase().contains(filterValue.toLowerCase());
	        } else if (matchValue.equalsIgnoreCase("is")) {
	            if (filterValue.length() == value.length()) { // Perform exact match if filter length matches value length
	                return value.equalsIgnoreCase(filterValue);
	            } else {
	                return false; // If lengths don't match, it's not an exact match
	            }
	        }
	        return false;
	    }

	    private boolean applyExperienceMatchType(String value1, int filterValue, String matchValue, String matchType) {
	    	int value=Integer.parseInt(value1);
	        if (matchValue == null) {
	            return true; // If matchValue is 0, it means it's not provided, so return true
	        }
	        if (matchValue.equalsIgnoreCase("greaterThan")) {
	            return value > filterValue;
	        } else if (matchValue.equalsIgnoreCase("lessThan")) {
	            return value < filterValue;
	        }
	        else if (matchValue.equalsIgnoreCase("is")) {
	            return value == filterValue;
	        }
	        return false;
	    }
	public Map<String, List<AppliedApplicantInfoDTO>> getAppliedApplicants(long jobRecruiterId) {
	    List<AppliedApplicantInfo> appliedApplicants = applyJobRepository.findAppliedApplicantsInfo(jobRecruiterId);
	    Map<String, List<AppliedApplicantInfoDTO>> applicantMap = new HashMap<>();
	    
	    for (AppliedApplicantInfo appliedApplicantInfo : appliedApplicants) {
	        String applicantKey = appliedApplicantInfo.getEmail() + "_" + appliedApplicantInfo.getApplyjobid();
	        if (!applicantMap.containsKey(applicantKey)) {
	            List<AppliedApplicantInfoDTO> dtoList = new ArrayList<>();
	            long id1=appliedApplicantInfo.getId();
	            AppliedApplicantInfoDTO dto1=mapToDTO(appliedApplicantInfo);
	            ApplicantProfile applicantProfile=null;
	            try {
	             applicantProfile=applicantProfileRepo.findByApplicantId(id1);
	            
	            dto1.setExperience(applicantProfile.getExperience());
	            dto1.setMinimumQualification(applicantProfile.getQualification());
	            }catch(Exception e) {
	            	 e.printStackTrace();
	            }
	            dtoList.add(dto1);
	            
	            applicantMap.put(applicantKey, dtoList);
	        } else {
	            List<AppliedApplicantInfoDTO> existingDTOList = applicantMap.get(applicantKey);
	            boolean found = false;
	            for (AppliedApplicantInfoDTO existingDTO : existingDTOList) {
	            	try {
	                if (existingDTO.getName().equals(appliedApplicantInfo.getName())) {
	                    existingDTO.addSkill(appliedApplicantInfo.getSkillName(), appliedApplicantInfo.getMinimumExperience());
	                    found = true;
	                    break;
	                }
	            	}catch(Exception e) {
	            		 e.printStackTrace();
	            	}
	            }
	            if (!found) {
	            	try {
	                AppliedApplicantInfoDTO dto = mapToDTO(appliedApplicantInfo);
	                existingDTOList.add(dto);
	            	}catch(Exception e) {
	            		 e.printStackTrace();
	            	}
	            }
	        }
	    }
	   
	    return applicantMap;
	}

	public Map<String, List<AppliedApplicantInfoDTO>> getAppliedApplicants1(long jobRecruiterId,long id) {
	    List<AppliedApplicantInfo> appliedApplicants = applyJobRepository.findAppliedApplicantsInfoWithJobId(jobRecruiterId, id);
	    Map<String, List<AppliedApplicantInfoDTO>> applicantMap = new HashMap<>();
	    for (AppliedApplicantInfo appliedApplicantInfo : appliedApplicants) {
	    	
	        String applicantKey = appliedApplicantInfo.getEmail() + "_" + appliedApplicantInfo.getApplyjobid();
	        if (!applicantMap.containsKey(applicantKey)) {
	        	try {
	            List<AppliedApplicantInfoDTO> dtoList = new ArrayList<>();
	            long id1=appliedApplicantInfo.getId();
	            ApplicantProfile applicantProfile=applicantProfileRepo.findByApplicantId(id1);
	            AppliedApplicantInfoDTO dto1=mapToDTO(appliedApplicantInfo);
	            dto1.setExperience(applicantProfile.getExperience());
	            dto1.setMinimumQualification(applicantProfile.getQualification());
	            dtoList.add(dto1);
	            
	            applicantMap.put(applicantKey, dtoList);
	        	}catch (Exception e) {
	        		e.printStackTrace();
	        	}
	        } else {
	            List<AppliedApplicantInfoDTO> existingDTOList = applicantMap.get(applicantKey);
	            boolean found = false;
	            for (AppliedApplicantInfoDTO existingDTO : existingDTOList) {
	            	try {
	                if (existingDTO.getName().equals(appliedApplicantInfo.getName())) {
	                    existingDTO.addSkill(appliedApplicantInfo.getSkillName(), appliedApplicantInfo.getMinimumExperience());
	                    found = true;
	                    break;
	                }
	            	}catch(Exception e) {
	            		e.printStackTrace();
	            	}
	            }
	            if (!found) {
	            	try {
	                AppliedApplicantInfoDTO dto = mapToDTO(appliedApplicantInfo);
	                existingDTOList.add(dto);
	            	}catch(Exception e) {
	            		e.printStackTrace();
	            	}
	            }
	        }
	    }
		Optional<Job> optionalJob = jobRepository.findById(id);

	    if (optionalJob.isPresent()) {
	        Job job = optionalJob.get();
	        job.setNewStatus("oldApplicants"); // Set the new status to the job object
	        jobRepository.save(job); // Save the updated job object
	    }
	    return applicantMap;
	}

 
private AppliedApplicantInfoDTO mapToDTO(AppliedApplicantInfo appliedApplicantInfo) {
	 AppliedApplicantInfoDTO dto = new AppliedApplicantInfoDTO();
	    dto.setApplyjobid(appliedApplicantInfo.getApplyjobid());
	    dto.setName(appliedApplicantInfo.getName());
	    dto.setId(appliedApplicantInfo.getId());
	    dto.setEmail(appliedApplicantInfo.getEmail());
	    dto.setMobilenumber(appliedApplicantInfo.getMobilenumber());
	    dto.setJobTitle(appliedApplicantInfo.getJobTitle());
	    dto.setApplicantStatus(appliedApplicantInfo.getApplicantStatus());
	    dto.setMinimumExperience(appliedApplicantInfo.getMinimumExperience());
	    dto.setMinimumQualification(appliedApplicantInfo.getMinimumQualification());
	    List<String> skills = new ArrayList<>();
	    skills.add(appliedApplicantInfo.getSkillName());
	    dto.setSkillName(skills);
	    dto.setLocation(appliedApplicantInfo.getLocation());
	    return dto;
}
 
public String updateApplicantStatus(Long applyJobId, String newStatus) {
    ApplyJob applyJob = applyJobRepository.findById(applyJobId)
            .orElseThrow(() -> new EntityNotFoundException("ApplyJob not found"));
    
    Job job = applyJob.getJob();
    if (job != null) {
        JobRecruiter recruiter = job.getJobRecruiter();
        if (recruiter != null) {
            String companyName = recruiter.getCompanyname();
            String jobTitle = job.getJobTitle();
            if (companyName != null) {
                applyJob.setApplicantStatus(newStatus);
                LocalDateTime currentDate = LocalDateTime.now();
               
                // Get the current change date and time
                LocalDateTime currentChangeDateTime = currentDate;
//                System.out.println(currentChangeDateTime + " Utc");
                // Add 5 hours and 30 minutes to the current change date and time
                LocalDateTime updatedChangeDateTime = currentChangeDateTime
                        .plusHours(5)
                        .plusMinutes(30);
                applyJob.setApplicationDate(updatedChangeDateTime); 
                applyJob.setChangeDate(updatedChangeDateTime);
//                System.out.println(updatedChangeDateTime + " IST");
                applyJobRepository.save(applyJob);
                // Increment alert count
                incrementAlertCount(applyJob.getApplicant());
                // Save status history
                saveStatusHistory(applyJob, applyJob.getApplicantStatus());
                // Send alerts
                sendAlerts(applyJob, applyJob.getApplicantStatus(), companyName, jobTitle);
                return "Applicant status updated to: " + newStatus;
            }
        }
    }
    return "Company information not found for the given ApplyJob";
}
public List<ApplicantJobInterviewDTO> getApplicantJobInterviewInfoForRecruiterAndStatus(
        long recruiterId, String applicantStatus) {
	try {
        return scheduleInterviewRepository.getApplicantJobInterviewInfoByRecruiterAndStatus(recruiterId, applicantStatus);
    } catch (Exception e) {
        throw new CustomException("Failed to retrieve applicant job interview info", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
public long countJobApplicantsByRecruiterId(Long recruiterId) {
	try {
        return applyJobRepository.countJobApplicantsByRecruiterId(recruiterId);
    } catch (Exception e) {
        throw new CustomException("Failed to count job applicants for the recruiter", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
public long countSelectedApplicants() {
	 try {
         return applyJobRepository.countByApplicantStatus("Selected");
     } catch (Exception e) {
         throw new CustomException("Failed to count selected applicants", HttpStatus.INTERNAL_SERVER_ERROR);
     }
}
public long countShortlistedAndInterviewedApplicants() {
	try {
        List<String> desiredStatusList = Arrays.asList("Shortlisted", "Interviewing");
        return applyJobRepository.countByApplicantStatusIn(desiredStatusList);
    } catch (Exception e) {
        throw new CustomException("Failed to count shortlisted and interviewed applicants", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
//This method is to get list of statuses related to particular job
public List<ApplicantStatusHistory> getApplicantStatusHistory(long applyJobId) {
	// TODO Auto-generated method stub
	return statusHistoryRepository.findByApplyJob_ApplyjobidOrderByChangeDateDesc(applyJobId);
}
//This method is to get alerts sent by recruiter
//public List<Alerts> getAlerts(long applyjobid) {
//	// TODO Auto-generated method stub
//	return alertsRepository.findByApplyJob_applyJobIdOrderByChangeDateDesc(applyjobid);
//}
public List<Alerts> getAlerts(long applicantId) {
	//return alertsRepository.findByApplicantIdOrderByChangeDateDesc(applicantId);
List<Alerts> alerts= alertsRepository.findByApplicantIdOrderByChangeDateDesc(applicantId);
	
	return alerts.stream()
            .filter(alert -> !"New".equals(alert.getApplyJob().getApplicantStatus()))
            .collect(Collectors.toList());
}
//This method is to reset count of alerts to zero once after reading all the alert messages.
public void resetAlertCount(long applicantId) {
	// TODO Auto-generated method stub
	try {
		
		Applicant applicant=applicantRepository.findById(applicantId);
		
		applicant.setAlertCount(0);
		applicantRepository.save(applicant);
		
		
		
  } catch (Exception e) {
      // Handle exceptions, log, and consider appropriate error handling
  	e.printStackTrace();
  }
}
public long countShortlistedAndInterviewedApplicants(long recruiterId) {
    try {
        List<String> desiredStatusList = Arrays.asList("shortlisted", "interviewing");
        return applyJobRepository.countShortlistedAndInterviewedApplicants(recruiterId, desiredStatusList);
    } catch (Exception e) {
        throw new CustomException("Failed to count shortlisted and interviewed applicants", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
public ApplyJob getByJobAndApplicant(Long jobId, Long applicantId) {
    try {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new EntityNotFoundException("Job not found"));
        Applicant applicant = applicantRepository.findById(applicantId);
        return applyJobRepository.findByJobAndApplicant(job, applicant);
    } catch (EntityNotFoundException e) {
        throw new CustomException("Job or Applicant not found", HttpStatus.NOT_FOUND);
    } catch (Exception e) {
        throw new CustomException("Error while retrieving ApplyJob", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
}
 
 
