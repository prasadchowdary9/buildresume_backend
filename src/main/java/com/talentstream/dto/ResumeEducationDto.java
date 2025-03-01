package com.talentstream.dto;



import java.time.LocalDate;
import javax.validation.constraints.*;

public class ResumeEducationDto {

    private Integer id;

    @NotBlank(message = "College name cannot be empty")
    private String college;

    @NotBlank(message = "Standard cannot be empty")
    private String standard;

    @NotNull(message = "Start year is required")
    private LocalDate startYear;

    @NotNull(message = "End year is required")
    private LocalDate endYear;

    @NotNull(message = "CGPA is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "CGPA must be at least 0.0")
    @DecimalMax(value = "10.0", inclusive = true, message = "CGPA cannot exceed 10.0")
    private Double cgpa;

    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public LocalDate getStartYear() {
        return startYear;
    }

    public void setStartYear(LocalDate startYear) {
        this.startYear = startYear;
    }

    public LocalDate getEndYear() {
        return endYear;
    }

    public void setEndYear(LocalDate endYear) {
        this.endYear = endYear;
    }

    public Double getCgpa() {
        return cgpa;
    }

    public void setCgpa(Double cgpa) {
        this.cgpa = cgpa;
    }
}
