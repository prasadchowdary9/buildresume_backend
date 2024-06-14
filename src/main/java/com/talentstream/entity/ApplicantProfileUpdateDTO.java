package com.talentstream.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ApplicantProfileUpdateDTO {
    private String experience;
    private String qualification;
    private String specialization;
    private Set<String> preferredJobLocations;
    private List<SkillDTO> skillsRequired;// Updated to accept skill names instead of IDs// Initialize to an empty set  // assuming skills are referenced by their IDs

    // Getters and Setters
    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Set<String> getPreferredJobLocations() {
        return preferredJobLocations;
    }

    public void setPreferredJobLocations(Set<String> preferredJobLocations) {
        this.preferredJobLocations = preferredJobLocations;
    }

    public List<SkillDTO> getSkillsRequired() {
        return skillsRequired;
    }

    public void setSkillsRequired(List<SkillDTO> skillsRequired) {
        this.skillsRequired = skillsRequired;
    }

    public static class SkillDTO {
        private Long id;
        private String skillName;
        private double experience;

        // Getters and Setters

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getSkillName() {
            return skillName;
        }

        public void setSkillName(String skillName) {
            this.skillName = skillName;
        }

        public double getExperience() {
            return experience;
        }

        public void setExperience(double experience) {
            this.experience = experience;
        }
    }
}
