package com.bzbees.hrma.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bzbees.hrma.entities.ConfirmationToken;

@Service("emailService")
public class EmailService {
	
	private JavaMailSender javaMailSender;
	
	
	
	@Autowired
	public EmailService(JavaMailSender javaMailSender) {
		this.javaMailSender= javaMailSender;
	}
	
	 @Async
	    public void sendEmail(SimpleMailMessage email) {
	        javaMailSender.send(email);
	    }
	 
	 public SimpleMailMessage simpleConfirmationMessage (String to, String subject, String confirmationToken ) {
		 
		 
		 SimpleMailMessage mailMessage = new SimpleMailMessage();
		 mailMessage.setTo(to);
		 mailMessage.setSubject(subject);
		 mailMessage.setFrom("no_reply@bpeople.ro");
		 mailMessage.setText("To confirm your account, please click here: " + 
				 			"http://localhost:8080/user/confirm-account?t0=" + 
				 			confirmationToken);
		 return mailMessage;
		 
	 }
	 
	 public SimpleMailMessage simpleChangePassMessage (String to, String subject, String confirmationToken ) {
		 
		 
		 SimpleMailMessage mailMessage = new SimpleMailMessage();
		 mailMessage.setTo(to);
		 mailMessage.setSubject(subject);
		 mailMessage.setFrom("no_reply@bpeople.ro");
		 mailMessage.setText("To change your password, please click here: " + 
				 			"http://localhost:8080/login/emailChangePassword?c4=" + 
				 			confirmationToken);
		 return mailMessage;
		 
	 }
	 
	
	
}
