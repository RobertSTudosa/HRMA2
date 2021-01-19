package com.bzbees.hrma.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.bzbees.hrma.entities.ConfirmationToken;

@Service("emailService")
public class EmailService {
	
	@Autowired
	private JavaMailSender javaMailSender;
	

	
	public String localhost1 = "hrm.bpeople.ro:8080";
	
	
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
		 //1st. try was http://hrm.bpeople.ro
		 //2nd try is going to be: http://hrm.bpeople.ro:8080 - aaaannd is right... we have a winner ladies and gentleman
		 mailMessage.setText("To confirm your account, please click here: " + 
				 			"http://hrm.bpeople.ro:8080/user/confirm-account?t0=" + 
				 			confirmationToken);
		 return mailMessage;
		 
	 }
	 
	 public SimpleMailMessage simpleChangePassMessage (String to, String subject, String confirmationToken ) {
		 
		 
		 SimpleMailMessage mailMessage = new SimpleMailMessage();
		 mailMessage.setTo(to);
		 mailMessage.setSubject(subject);
		 mailMessage.setFrom("no_reply@bpeople.ro");
		 mailMessage.setText("To change your password, please click here: " + 
				 			"http://hrm.bpeople.ro:8080/login/emailChangePassword?c4=" + 
				 			confirmationToken);
		 return mailMessage;
		 
	 }
	 
	
	
}
