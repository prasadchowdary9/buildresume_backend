package com.talentstream.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
 
@Service
public class EmailService {
 
    @Autowired
    private JavaMailSender javaMailSender;
 
    public void sendOtpEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@bitlabs.in"); // Explicitly set the from address
        message.setSubject("OTP for TalentStreamApplication Registration");
        message.setTo(to);
        message.setText(
        		"Dear Applicant,\n\n"+
        	    "Your OTP is: " + otp + "\n\n" +
        	    "We received a request to verify your identity for bitLabs Jobs. To complete the sign-up process, please use the above One-Time Password (OTP).\n\n" +
        	    "This OTP is valid for the next 1 minute. For your security, please do not share this code with anyone.\n\n" +
        	    "If you did not request this verification, please ignore this email.\n\n" +
        	    "Thank you for using bitLabs Jobs!\n\n" +
        	    "Best regards,\n" +
        	    "The bitLabs Jobs Team\n\n" +
        	    "This is an auto-generated email. Please do not reply."
        	);
        javaMailSender.send(message);
    }
}