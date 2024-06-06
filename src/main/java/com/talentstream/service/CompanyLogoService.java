package com.talentstream.service;

import java.io.ByteArrayInputStream;
import java.io.File;

import java.io.IOException;

import java.nio.file.Files;

import java.nio.file.Path;

import java.nio.file.Paths;

import java.nio.file.StandardCopyOption;

import java.util.Arrays;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.Resource;

import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.util.StringUtils;

import org.springframework.web.multipart.MaxUploadSizeExceededException;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.InputStreamResource;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.talentstream.entity.CompanyLogo;

import com.talentstream.entity.JobRecruiter;

import com.talentstream.exception.CustomException;
 
import com.talentstream.exception.UnsupportedFileTypeException;

import com.talentstream.repository.CompanyLogoRepository;

import com.talentstream.repository.JobRecruiterRepository;


@Service
public class CompanyLogoService {
	
	 private static final long MAX_FILE_SIZE_BYTES = 1024 * 1024; // 1 MB = 1024 * 1024 bytes
	    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "gif"};
 
        	@Autowired
    		private CompanyLogoRepository companyLogoRepository;
    		@Autowired
    		private  JobRecruiterRepository jobRecruiterRepository;
    		
    		@Value("${aws.s3.bucketName}")
    	    private String bucketName;
    	 
    	    @Value("${aws.accessKey}")
    	    private String accessKey;
    	 
    	    @Value("${aws.secretKey}")
    	    private String secretKey;
    	    
    	    @Value("${aws.region}")
    	    private String region;
    	
    		  
    		    public String saveCompanyLogo(long jobRecruiterId, MultipartFile imageFile) throws IOException {
    		 
    			  if (imageFile.getSize() > 1 * 1024 * 1024) {
    		            throw new CustomException("File size should be less than 1MB.", HttpStatus.BAD_REQUEST);
    		        }
    		        String contentType = imageFile.getContentType();
    		        if (!"image/jpeg".equals(contentType) && !"image/png".equals(contentType)) {
    		            throw new CustomException("Only JPG and PNG file types are allowed.", HttpStatus.BAD_REQUEST);
    		        }
    		   
    		      	  JobRecruiter recruiter = jobRecruiterRepository.findByRecruiterId(jobRecruiterId);
    		          if (recruiter == null) {
    		    		            throw new CustomException("Recruiter not found for ID: " + jobRecruiterId, HttpStatus.NOT_FOUND);
    		    		 } else {
    		   
    		      	    // Logic for S3 upload
    		      	   
    		      	    String objectKey =  String.valueOf(jobRecruiterId)+".jpg"; // Generate unique object key
    		   
    		      	    try {
    		      	      AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
    		      	          .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
    		      	          .withRegion(Regions.US_WEST_2)
    		      	          .build();
    		   
    		      	      s3Client.putObject(
    		      	          new PutObjectRequest(bucketName, objectKey, imageFile.getInputStream(), createObjectMetadata(imageFile))
    		      	      );
    		      	      
    		      	    
    		      	      
    		   
    		      	      return objectKey; // Return the object key for reference
    		   
    		      	    } catch (AmazonServiceException ase) {
    		      	      // Handle exceptions appropriately, e.g., log the error
    		      	      throw new RuntimeException("Failed to upload image to S3", ase);
    		      	    } catch (IOException e) {
    		      	      // Handle potential IO exceptions during stream processing
    		      	      throw new RuntimeException("Error processing image file", e);
    		      	    }
    		      	  }

    		  }
    		  
    		 	private ObjectMetadata createObjectMetadata(MultipartFile imageFile) throws IOException {
    		      	  ObjectMetadata objectMetadata = new ObjectMetadata();
    		      	  objectMetadata.setContentType(imageFile.getContentType());
    		      	  objectMetadata.setContentLength(imageFile.getSize());
    		      	  return objectMetadata;
    		      	}

    		
    		    
    		    public byte[] getCompanyLogo(long jobRecruiterId) {
    		    	 try {
	              	       
    	      	            String objectKey = String.valueOf(jobRecruiterId)+".jpg";

    	      	            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
    	      	                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
    	      	                    .withRegion(Regions.US_WEST_2)
    	      	                    .build();

    	      	            S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, objectKey));
    	      	            S3ObjectInputStream inputStream = s3Object.getObjectContent();

    	      	            MediaType mediaType;
    	      	            if (objectKey.toLowerCase().endsWith(".png")) {
    	      	                mediaType = MediaType.IMAGE_PNG;
    	      	            } else if (objectKey.toLowerCase().endsWith(".jpg") || objectKey.toLowerCase().endsWith(".jpeg")) {
    	      	                mediaType = MediaType.IMAGE_JPEG;
    	      	            } else {
    	      	                throw new RuntimeException("Unsupported image file format for applicant ID: " + jobRecruiterId);
    	      	            }

    	      	          byte[] bytes = IOUtils.toByteArray(inputStream); // Convert input stream to byte array

    	      	            return bytes;

    	      	    } catch (Exception e) {
    	      	        String errorMessage = "Internal Server Error";
    	      	      byte[] errorBytes = errorMessage.getBytes();
    	      	    ByteArrayInputStream errorStream = new ByteArrayInputStream(errorBytes);

    	      	        System.out.println(e.getMessage());
    	      	      return errorBytes;
    	      	    }

    		    }
}
 