package com.talentstream.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    // Sends an OTP verification email to the applicant for identity verification.
    public void sendOtpEmail(String to, String otp) {
        try {
            javax.mail.internet.MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(new InternetAddress("no-reply@bitlabs.in", "bitLabs Jobs"));
            helper.setTo(to);
            helper.setSubject("OTP verification for bitLabs Jobs");

            String content = "Dear Applicant,\n\n" +
                    "Your OTP is: " + otp + "\n\n" +
                    "We received a request to verify your identity for bitLabs Jobs. To complete the sign-up process, please use the above One-Time Password (OTP).\n\n"
                    +
                    "This OTP is valid for the next 1 minute. For your security, please do not share this code with anyone.\n\n"
                    +
                    "If you did not request this verification, please ignore this email.\n\n" +
                    "Thank you for using bitLabs Jobs!\n\n" +
                    "Best regards,\n" +
                    "The bitLabs Jobs Team\n\n" +
                    "This is an auto-generated email. Please do not reply.";

            helper.setText(content);

            // Send the email
            javaMailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}