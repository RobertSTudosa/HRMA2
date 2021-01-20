package com.bzbees.hrma.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bzbees.hrma.entities.Agency;
import com.bzbees.hrma.entities.Person;
import com.bzbees.hrma.entities.User;
import com.bzbees.hrma.services.AgencyService;
import com.bzbees.hrma.services.PersonService;
import com.bzbees.hrma.services.UserService;

@Controller
@RequestMapping("/")
@SessionAttributes({"agency"})
public class HomeController {
	
	@Autowired
	UserService userServ;
	
	@Autowired
	PersonService persServ;
	
	@Autowired
	AgencyService agencyServ;
	
	@Autowired
	BCryptPasswordEncoder bCryptEncoder;
	
//	@Value("${version}")
//	private String ver;
	
	@GetMapping("/")
	public String displayHome ( Model model, @ModelAttribute("message") String message, 
			Authentication auth, HttpSession session ) {
		
		if(auth !=null ) {
			String name = auth.getName();
			
			User user = (User) userServ.loadUserByUsername(name);
			session.setAttribute("userAccount", user);
			
			if(user == null) {
				return "redirect:/\\logout";
			}
			
			
			//get authorities to string 			
			System.out.println("User's role in my home controller are: " + user.getRoles().toString());
			
			//get the agency if any is associated with current user 
			if(agencyServ.findAgencyByUserId(user.getUserId()) !=null) {
				Agency agency = agencyServ.findAgencyByUserId(user.getUserId());
				System.out.println("Agency in home controller " + agency.getAgencyName());
				model.addAttribute("agency", agency);
			} else {
				Agency agency = new Agency();
				model.addAttribute("agency", agency);
				System.out.println("THERE IS NO AGENCY");
			}
			
		}
		
		
		
		
//		System.out.println("Name of the user : " + auth.getName());
//		model.addAttribute("localVerNumber", ver);
		
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
	public String changePassword(Model model, Authentication auth, HttpServletRequest request,
					RedirectAttributes redirAttr) {
		
		User user = (User) userServ.loadUserByUsername(auth.getName());
		user.setPassword(bCryptEncoder.encode(request.getParameter("newPassword")));
		
		Person person = persServ.findPersonByUserId(user.getUserId());
		persServ.save(person);
		userServ.save(user);
		
		redirAttr.addAttribute("passChanged", "Password successfully updated.");
		redirAttr.addAttribute("success", "Changed password for user: " + user.getUsername());
		
		return "redirect:/person/sprofile";
		
	}
	




	

}
