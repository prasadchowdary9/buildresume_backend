package com.talentstream.controller;
 
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.talentstream.entity.Applicant;
import com.talentstream.entity.ApplicantResume;
import com.talentstream.exception.CustomException;
import com.talentstream.repository.ApplicantRepository;
import com.talentstream.repository.ApplicantResumeRepository;
import com.talentstream.repository.RegisterRepository;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
 
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
@RestController
@RequestMapping("/resume")
public class PdfController {

	 @Value("${aws.s3.bucketName}")
	    private String bucketName;
	 
	    @Value("${aws.accessKey}")
	    private String accessKey;
	 
	    @Value("${aws.secretKey}")
	    private String secretKey;
	    
	    @Value("${aws.region}")
	    private String region;
	    
    @Autowired
    private RegisterRepository registerRepo;
   
    @Autowired
    private ApplicantResumeRepository applicantResumeRepository;
    
    @Autowired
    private ApplicantRepository applicantRepository;
 
    @GetMapping("/pdf/{id}")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable long id) {
     
       
        Applicant applicant=registerRepo.findById(id);
        String resumeId= applicant.getResumeId();
        String fileKey=resumeId+".pdf";
        System.out.println(fileKey);
        try {
             BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
             AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                     .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                     .withRegion(Regions.US_WEST_2) // Replace with your desired AWS region
                     .build();
            S3Object s3Object = s3Client.getObject(bucketName, fileKey);
            InputStream inputStream = s3Object.getObjectContent();
            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", fileKey);
            byte[] content = inputStream.readAllBytes();
            return new ResponseEntity<>(content, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
   
    @PostMapping("/upload/{id}")
    public ResponseEntity<String> uploadFile(@RequestParam("resume") MultipartFile file, @PathVariable long id) {
        
       
        if (file.getSize() > 1 * 1024 * 1024) {
            throw new CustomException("File size should be less than 1MB.", HttpStatus.BAD_REQUEST);
        }
 
        String contentType = file.getContentType();
        if (!"application/pdf".equals(contentType)) {
            throw new CustomException("Only PDF file types are allowed.", HttpStatus.BAD_REQUEST);
        }
       
        Applicant applicant=registerRepo.findById(id);
        String resumeId= applicant.getResumeId();
        String fileKey=resumeId+".pdf";
        System.out.println(fileKey);
        if (applicant == null)
            throw new CustomException("Applicant not found for ID: " + id, HttpStatus.NOT_FOUND);
        else {
        try {
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                    .withRegion(Regions.US_WEST_2) // Replace with your desired AWS region
                    .build();
 
            // Generate unique file name
            String fileName =  fileKey;
 
            // Upload file to S3
            s3Client.putObject(bucketName, fileName, file.getInputStream(), new ObjectMetadata());
 
            // Construct the S3 URL
            String fileUrl = s3Client.getUrl(bucketName, fileName).toString();
           Optional<Applicant> applicant2=applicantRepository.findById(id);
           
           if (applicant2.isPresent()) {
        	    Applicant applicant1 = applicant2.get();
        	    applicant1.setLocalResume(true);
        	    applicantRepository.save(applicant1);
           }
           
           
            return ResponseEntity.ok("File uploaded successfully. S3 URL: " + fileUrl);
        } catch (AmazonServiceException | IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
        }
        }
    }
 
}