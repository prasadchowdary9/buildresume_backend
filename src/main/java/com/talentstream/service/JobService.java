package com.talentstream.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import com.talentstream.dto.JobDTO;
import com.talentstream.dto.RecuriterSkillsDTO;
import com.talentstream.entity.CompanyProfile;
import com.talentstream.entity.Job;
import com.talentstream.entity.JobRecruiter;
import com.talentstream.entity.JobSearchCriteria;
import com.talentstream.entity.JobSpecifications;
import com.talentstream.entity.RecuriterSkills;
import com.talentstream.entity.SavedJob;
import com.talentstream.exception.CustomException;
import com.talentstream.repository.CompanyProfileRepository;
import com.talentstream.repository.JobRecruiterRepository;
import com.talentstream.repository.JobRepository;
import com.talentstream.repository.RecuriterSkillsRepository;
import com.talentstream.repository.SavedJobRepository;

@Service
public class JobService {
	
	private final CompanyProfileRepository companyProfileRepository;
	
	private final ModelMapper modelMapper = new ModelMapper();

	
	JobRepository jobRepository;	   
	    private RecuriterSkillsRepository skillsRepository;
	    
	    @Autowired
	    JobRecruiterRepository jobRecruiterRepository;
	    
	    @Autowired
	    private SavedJobRepository savedJobRepository;
	    
    @Autowired
    public JobService(JobRepository jobRepository, RecuriterSkillsRepository skillsRepository,CompanyProfileRepository companyProfileRepository) {
        this.jobRepository = jobRepository;
        this.skillsRepository = skillsRepository;
        this.companyProfileRepository=companyProfileRepository;
    }

    public List<Job> searchJobs(JobSearchCriteria searchCriteria) {
    	 try {
             Page<Job> jobPage = jobRepository.findAll(
                     JobSpecifications.searchJobs(
                             searchCriteria.getSkillName(),
                             searchCriteria.getJobTitle(),
                             searchCriteria.getLocation(),
                             searchCriteria.getIndustryType(),
                             searchCriteria.getEmployeeType(),
                             searchCriteria.getMinimumQualification(),
                             searchCriteria.getSpecialization()
                     ),
                     Pageable.unpaged()
             );

             return jobPage.getContent();
         } catch (Exception e) {
             throw new CustomException("Error while searching jobs", HttpStatus.INTERNAL_SERVER_ERROR);
         }
    }
    public List<Job> getJobsByPromoteState(long applicantId,String promote) {
    	List<SavedJob> savedJobs = savedJobRepository.findByApplicantId(applicantId);
    	System.out.println(savedJobs.size());
    	List<Object[]> result =  jobRepository.findByPromote(applicantId,promote);
        List<Job> matchingJobs = new ArrayList<>();
        List<Long> savedJobsIds=new ArrayList<>();
        
        for(SavedJob saved: savedJobs) {
        	System.out.println("Saved Jobs Id "+saved.getJob().getId());
        	savedJobsIds.add(saved.getJob().getId());
        }
        
        for (Object[] array : result) {
            Job job = (Job) array[0];
            job.setIsSaved((String)array[1]);
            
            	if(!savedJobsIds.contains(job.getId())) {
                System.out.println(job.getId());
            	matchingJobs.add(job);
            }
           
        }
        
        return matchingJobs;
    }

    public ResponseEntity<String> saveJob(JobDTO jobDTO, Long jobRecruiterId) {
    	 try {
             JobRecruiter jobRecruiter = jobRecruiterRepository.findByRecruiterId(jobRecruiterId);
             if (jobRecruiter != null) {
                 Job job = convertDTOToEntity(jobDTO);
                 job.setJobRecruiter(jobRecruiter);
                 jobRepository.save(job);
                 return ResponseEntity.status(HttpStatus.OK).body("Job saved successfully.");
             } else {
                 throw new CustomException("JobRecruiter with ID " + jobRecruiterId + " not found.", HttpStatus.NOT_FOUND);
             }
         } catch (CustomException ce) {
             throw ce;
         } catch (Exception e) {
             throw new CustomException("Error while saving job", HttpStatus.INTERNAL_SERVER_ERROR);
         }
    	

}

    public Job getJobById(Long jobId) {
    	  try {
              return jobRepository.findById(jobId).orElse(null);
          } catch (Exception e) {
              throw new CustomException("Error while retrieving job by ID", HttpStatus.INTERNAL_SERVER_ERROR);
          }
    }
   
    public List<Job> getAllJobs() {
    	 try {
             return jobRepository.findAll();
         } catch (Exception e) {
             throw new CustomException("Error while retrieving all jobs", HttpStatus.INTERNAL_SERVER_ERROR);
         }
    }
    public List<Job> getJobsByRecruiter(Long jobRecruiterId) {
    	try {
    		System.out.println("before find job recruiter");
            return jobRepository.findByJobRecruiterId(jobRecruiterId);
        } catch (Exception e) {
            throw new CustomException("Error while retrieving jobs by recruiter ID", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    

	public long countJobsByRecruiterId(Long recruiterId) {
		 try {
	            return jobRepository.countJobsByRecruiterId(recruiterId);
	        } catch (Exception e) {
	            throw new CustomException("Error while counting jobs by recruiter ID", HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
	private Job convertDTOToEntity(JobDTO jobDTO) {
        Job job = new Job();       
        job.setJobTitle(jobDTO.getJobTitle());
        job.setMinimumExperience(jobDTO.getMinimumExperience());
        job.setMaximumExperience(jobDTO.getMaximumExperience());
        job.setMinSalary(jobDTO.getMinSalary());
        job.setMaxSalary(jobDTO.getMaxSalary());
        job.setLocation(jobDTO.getLocation());
        job.setEmployeeType(jobDTO.getEmployeeType());
        job.setIndustryType(jobDTO.getIndustryType());
        job.setMinimumQualification(jobDTO.getMinimumQualification());
        job.setSpecialization(jobDTO.getSpecialization());
        job.setSkillsRequired(convertSkillsDTOToEntity(jobDTO.getSkillsRequired()));
 //       job.setJobHighlights(jobDTO.getJobHighlights());
        job.setDescription(jobDTO.getDescription());
        job.setCreationDate(jobDTO.getCreationDate());
              //  job.setUploadDocument(Base64.getDecoder().decode(jobDTO.getUploadDocument())); // Decode base64 string

        return job;
    }
	
	private Set<RecuriterSkills> convertSkillsDTOToEntity(Set<RecuriterSkillsDTO> skillsRequired) {
		 return skillsRequired.stream()
		            .map(skillDTO -> {
		                RecuriterSkills skill = new RecuriterSkills();
		                skill.setSkillName(skillDTO.getSkillName());
		   //             skill.setMinimumExperience(skillDTO.getMinimumExperience());
		                return skill;
		            })
		            .collect(Collectors.toSet());
		}
	public void changeJobStatus(Long jobId, String newStatus) {
	        try {
	            Job job = jobRepository.findById(jobId)
	                    .orElseThrow(() -> new CustomException("Job not found", HttpStatus.NOT_FOUND));
 
	            // Validate newStatus (optional, depending on your requirements)
 
	            job.setStatus(newStatus.toLowerCase());
	            jobRepository.save(job);
	        } catch (CustomException ce) {
	            throw ce;
	        } catch (Exception e) {
	            throw new CustomException("Error changing job status", HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
	

	  public List<Job> getActiveJobsForRecruiter(Long recruiterId) {
	        return jobRepository.findJobsByRecruiterAndStatus(recruiterId, "active");
	    }

	    public List<Job> getInactiveJobsForRecruiter(Long recruiterId) {
	        return jobRepository.findJobsByRecruiterAndStatus(recruiterId, "inactive");
	    }
	 public String getJobStatus(Long jobId) {
	        Optional<Job> optionalJob = jobRepository.findById(jobId);
 
	        if (optionalJob.isPresent()) {
	            Job job = optionalJob.get();
	            return job.getStatus(); // Assuming the status is a field in the Job entity
	        } else {
	            throw new CustomException("Job not found",HttpStatus.NOT_FOUND);
	        }
	    }
	public ResponseEntity<String> editJob(JobDTO jobDTO, Long jobId) {
	        Optional<Job> existingJobOptional = jobRepository.findById(jobId);
 
	        if (existingJobOptional.isPresent()) {
	            Job existingJob = existingJobOptional.get();
 
	            // Update the job details with the new values from jobDTO
	            existingJob.setJobTitle(jobDTO.getJobTitle());
	            existingJob.setMinimumExperience(jobDTO.getMinimumExperience());
	            existingJob.setMaximumExperience(jobDTO.getMaximumExperience());
	            existingJob.setMinSalary(jobDTO.getMinSalary());
	            existingJob.setMaxSalary(jobDTO.getMaxSalary());
	            existingJob.setLocation(jobDTO.getLocation());
	            existingJob.setEmployeeType(jobDTO.getEmployeeType());
	            existingJob.setIndustryType(jobDTO.getIndustryType());
	            existingJob.setMinimumQualification(jobDTO.getMinimumQualification());
	            existingJob.setSpecialization(jobDTO.getSpecialization());
	     //       existingJob.setJobHighlights(jobDTO.getJobHighlights());
	            existingJob.setDescription(jobDTO.getDescription());
	           // existingJob.setSaveJobStatus(jobDTO.getSaveJobStatus());
 
	            // Update skillsRequired - Assuming jobDTO has a similar structure as Job
	            Set<RecuriterSkills> updatedSkills = new HashSet<>();
	            for (RecuriterSkillsDTO skillDTO : jobDTO.getSkillsRequired()) {
	                RecuriterSkills skill = new RecuriterSkills();
	                skill.setSkillName(skillDTO.getSkillName());
	        //       skill.setMinimumExperience(skillDTO.getMinimumExperience());
	                updatedSkills.add(skill);
	            }
	            existingJob.setSkillsRequired(updatedSkills);
 
	            jobRepository.save(existingJob);
 
	            return ResponseEntity.ok("Job updated successfully.");
	        } else {
	            throw new CustomException("Job not found", HttpStatus.NOT_FOUND);
	        }
	    }
	
	public String cloneJob(Long jobId,Long jobRecruiterId) {
	    // Fetch the details of the existing job
	    Job existingJob = jobRepository.findById(jobId).orElseThrow(() -> new CustomException("Job not found", HttpStatus.NOT_FOUND));
	    JobRecruiter jobRecruiter = jobRecruiterRepository.findByRecruiterId(jobRecruiterId);

	    // Create a new Job entity by copying the details of the existing job
	    Job clonedJob = new Job();
	    // Copy details like title, description, salary, etc.
	    // Assign a new ID to the cloned job
	    clonedJob.setId(null); // This will generate a new ID automatically
	    clonedJob.setJobTitle(existingJob.getJobTitle());
	    clonedJob.setMaximumExperience(existingJob.getMaximumExperience());
	    clonedJob.setMinimumExperience(existingJob.getMinimumExperience());
	    clonedJob.setMaxSalary(existingJob.getMaxSalary());
	    clonedJob.setMinSalary(existingJob.getMinSalary());
	    clonedJob.setLocation(existingJob.getLocation());
	    clonedJob.setEmployeeType(existingJob.getEmployeeType());
	    clonedJob.setIndustryType(existingJob.getIndustryType());
	    clonedJob.setMinimumQualification(existingJob.getMinimumQualification());
	    clonedJob.setSpecialization(existingJob.getSpecialization());
	 // Update skillsRequired - Assuming jobDTO has a similar structure as Job
        Set<RecuriterSkills> updatedSkills = new HashSet<>();
        for (RecuriterSkills skillDTO : existingJob.getSkillsRequired()) {
            RecuriterSkills skill = new RecuriterSkills();
            skill.setSkillName(skillDTO.getSkillName());
    //       skill.setMinimumExperience(skillDTO.getMinimumExperience());
            updatedSkills.add(skill);
        }
	    clonedJob.setSkillsRequired(updatedSkills);
	    clonedJob.setDescription(existingJob.getDescription());
	    clonedJob.setJobRecruiter(jobRecruiter);
	    // Save the cloned job to the database
	    System.out.println("About to saved");
	    try {
	    Job savedClonedJob = jobRepository.save(clonedJob);
	    }catch(Exception e) {
	    	 System.out.println(e.getMessage());
	    }
	    System.out.println("jOB sAVED");
	    // Convert the saved cloned job entity to DTO and return
	    return "Job Reposted successfully";
	}

	
}
