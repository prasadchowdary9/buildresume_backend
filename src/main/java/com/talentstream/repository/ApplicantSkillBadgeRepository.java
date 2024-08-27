package com.talentstream.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talentstream.entity.ApplicantSkillBadge;

public interface ApplicantSkillBadgeRepository extends JpaRepository<ApplicantSkillBadge, Long> {
	
    List<ApplicantSkillBadge> findByApplicantId(Long applicantId);
}
