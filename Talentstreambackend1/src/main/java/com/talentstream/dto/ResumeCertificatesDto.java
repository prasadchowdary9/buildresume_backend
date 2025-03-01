package com.talentstream.dto;


import java.time.LocalDate;
import javax.validation.constraints.*;

public class ResumeCertificatesDto {

    private Integer id;

    @NotBlank(message = "Certificate title is required")
    private String title;

    @NotBlank(message = "Issuing organization is required")
    private String issuedBy;

    @NotNull(message = "Year of certification is required")
    @PastOrPresent(message = "Year must be in the past or present")
    private LocalDate year;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    public LocalDate getYear() {
        return year;
    }

    public void setYear(LocalDate year) {
        this.year = year;
    }
}
