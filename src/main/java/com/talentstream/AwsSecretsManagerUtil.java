package com.talentstream;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsSecretsManagerUtil {

	
	
	public static String getSecret() {
		  String secretName = System.getenv("SECRET_NAME");
		  String region1 = System.getenv("AWS_REGION");
	        Region region = Region.of(region1);
	        String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
	        String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
          
	        System.out.println(secretName);
	        System.out.println(region1);
	        System.out.println(accessKey);
	        System.out.println(secretKey);
	        
	        if (accessKey == null || secretKey == null) {
	            System.err.println("AWS credentials are not set in environment variables.");
	            return null;
	        }

	        // Create a Secrets Manager client
	        try (SecretsManagerClient client = SecretsManagerClient.builder()
	                .region(region)
	                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
	                .build()) {

	            GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
	                    .secretId(secretName)
	                    .build();

	            GetSecretValueResponse getSecretValueResponse;

	            try {
	                getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
	            } catch (Exception e) {
	                System.err.println("Error retrieving secret: " + e.getMessage());
	                throw e;
	            }

	            String secret = getSecretValueResponse.secretString();
	           
	            return secret;
	        } catch (Exception e) {
	            System.err.println("An error occurred: " + e.getMessage());
	            return null;
	        }
    }

    
}
