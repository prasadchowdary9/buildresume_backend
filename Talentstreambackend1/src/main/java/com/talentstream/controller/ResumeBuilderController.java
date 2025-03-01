package com.talentstream.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.talentstream.dto.ResumeBuilderDto;
import com.talentstream.entity.ResumeBuilder;
import com.talentstream.service.ResumeBuilderService;

@RestController
@RequestMapping("/resume-builder")
public class ResumeBuilderController {

	@Autowired
	private ResumeBuilderService resumeBuilderService;

	@PostMapping("/saveresume/{applicantId}")
	public ResponseEntity<?> createResume(@Valid @RequestBody ResumeBuilderDto resumeDto, BindingResult bindingResult,
			@PathVariable Long applicantId) {

		try {
			if (bindingResult.hasErrors()) {
				Map<String, String> errors = new HashMap<>();
				bindingResult.getFieldErrors()
						.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
				return ResponseEntity.badRequest().body(errors);
			}

			ResumeBuilder resume = resumeBuilderService.createResume(resumeDto, applicantId);
			if (resume == null) {
				return ResponseEntity.badRequest().body("Resume not saved");
			}

			return ResponseEntity.ok("Resume Saved Successfully");
		} catch (RuntimeException e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Internal server error");
		}
	}

	@GetMapping("getResume/{applicantId}")
	public ResponseEntity<?> getResumeWithEducation(@PathVariable Long applicantId) {
		try {
			ResumeBuilder resume = resumeBuilderService.getResumeWithEducation(applicantId);
			return ResponseEntity.ok(resume);
		} catch (RuntimeException e) {
			return ResponseEntity.internalServerError().body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Internal server error");
		}
	}
}
