package com.talentstream.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class ResumePersonalInfoDto {
    private Integer id;

    @NotBlank(message = "Full Name is required ")
    private String fullName;

    @NotBlank(message = "Email is required.")
    @Pattern(regexp = "^$|^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", message = "Invalid email format and white spaces are not allowed.")
    private String email;

    @NotBlank(message = "Phone Number is required")
    private String phoneNo;
    
    @NotBlank(message = "Address is required")
    private String address;
    private String linkedin;
    private String github;
    private String website;
    
    @NotBlank(message = "Role is required")
    private String role;
    
    @NotBlank(message = "Summary is required")
    private String summary;
}
