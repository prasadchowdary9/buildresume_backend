package com.talentstream.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.talentstream.service.ZohoService;

import java.util.Map;

@RestController
@RequestMapping("/api/zoho")
public class ZohoController {

    private final ZohoService zohoService;

    public ZohoController(ZohoService zohoService) {
        this.zohoService = zohoService;
    }

    @PostMapping("/submit-lead")
    public ResponseEntity<String> submitLead(@RequestBody Map<String, Object> leadData) {
        return zohoService.createLead(leadData);
    }
}
