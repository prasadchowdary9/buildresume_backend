package com.talentstream.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.talentstream.entity.ApplicantTest;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicantTestRepository extends JpaRepository<ApplicantTest, Long> {
    List<ApplicantTest> findByApplicantId(Long applicantId);

	Optional<ApplicantTest> findByApplicantIdAndTestName(Long applicantId, String testName);
}
