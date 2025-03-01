package com.talentstream.dto;

import java.time.LocalDate;
import javax.validation.constraints.*;

public class ResumeProjectDto {

    private Integer id;

    @NotBlank(message = "Project title is required")
    private String title;

    @NotBlank(message = "Project description is required")
    private String description;

    @NotNull(message = "Project start date is required")
    private LocalDate startDate;

    @NotNull(message = "Project end date is required")
    private LocalDate endDate;

    @NotBlank(message = "Project link is required")
    private String link;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
