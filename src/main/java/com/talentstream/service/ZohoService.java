package com.talentstream.service;

import java.util.Map;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ZohoService {

    private static final String ZOHO_API_URL = "https://www.zohoapis.com/crm/v2/Leads";
    private static final String REFRESH_TOKEN = "1000.e87ebe40cd761f2da55c0d17cf43d087.d70ca272e7ca5d8094a2683e611f2aa6"; // Your refresh token
    private static final String CLIENT_ID = "1000.PIKC518LPD7Z9XBJ2M4TPGCYDG8GFZ"; // Your client ID
    private static final String CLIENT_SECRET = "668f94ef6076942497c1b61a6a7c2f08d3028db121"; // Your client secret
    private static final String ACCESS_TOKEN_URL = "https://accounts.zoho.com/oauth/v2/token"; // Zoho token URL

    private String accessToken = null; // No need to store permanently

    // ✅ Get a new access token using the refresh token
    private String getAccessToken() {
        if (accessToken == null || accessToken.isEmpty()) {
            accessToken = refreshAccessToken();
        }
        return accessToken;
    }

    // ✅ Refresh access token using the refresh token
    private String refreshAccessToken() {
        // Construct URL for getting a new access token
        String url = UriComponentsBuilder.fromHttpUrl(ACCESS_TOKEN_URL)
                .queryParam("client_id", CLIENT_ID)
                .queryParam("client_secret", CLIENT_SECRET)
                .queryParam("refresh_token", REFRESH_TOKEN)
                .queryParam("grant_type", "refresh_token")
                .toUriString();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, null, String.class);

        return parseAccessTokenFromResponse(response.getBody());
    }

    // ✅ Parse the access token from Zoho's JSON response
    private String parseAccessTokenFromResponse(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            System.err.println("Error parsing access token: " + e.getMessage());
            return null;
        }
    }

    // ✅ Create a lead using Zoho CRM API
    public ResponseEntity<String> createLead(Map<String, Object> leadData) {
        RestTemplate restTemplate = new RestTemplate();
        String accessToken = getAccessToken(); // Get a fresh access token

        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to retrieve access token.");
        }

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Zoho-oauthtoken " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(leadData, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(ZOHO_API_URL, HttpMethod.POST, request, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException e) {
            System.err.println("Client error: " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body("Client error: " + e.getResponseBodyAsString());
        } catch (HttpServerErrorException e) {
            System.err.println("Server error: " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body("Server error: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }
}
