package com.bzbees.hrma.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bzbees.hrma.entities.Person;
import com.bzbees.hrma.entities.User;
import com.bzbees.hrma.services.PersonService;
import com.bzbees.hrma.services.UserService;

@Controller
@RequestMapping("/")
//@SessionAttributes()
public class HomeController {
	
	@Autowired
	UserService userServ;
	
	@Autowired
	PersonService persServ;
	
	@Autowired
	BCryptPasswordEncoder bCryptEncoder;
	
	@Value("${version}")
	private String ver;
	
	@GetMapping("/")
	public String displayHome ( Model model, @ModelAttribute("message") String message, 
			Authentication auth, HttpSession session ) {
		
		if(auth !=null ) {
			String name = auth.getName();
			User user = (User) userServ.loadUserByUsername(name);
			session.setAttribute("userAccount", user);
			
		}
		
//		System.out.println("Name of the user : " + auth.getName());
		model.addAttribute("localVerNumber", ver);
		
		return "home";
	}
	
	@GetMapping("/showChangePassword")
	public String changePassword (Model model, Authentication auth) {
		
		if(auth.getName()!=null) {
			System.out.println("is there any user? : " + auth.getName());
		}


		
		return "user/changePass";
	}
	
	@PostMapping("/changePassword")
	public String changePassword(Model model, Authentication auth, HttpServletRequest request) {
		
		User user = (User) userServ.loadUserByUsername(auth.getName());
		user.setPassword(bCryptEncoder.encode(request.getParameter("newPassword")));
		
		Person person = persServ.findPersonByUserId(user.getUserId());
		persServ.save(person);
		userServ.save(user);
		
		return "home";
		
	}
	




	

}
