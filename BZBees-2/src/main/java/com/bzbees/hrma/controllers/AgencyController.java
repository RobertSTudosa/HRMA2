package com.bzbees.hrma.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bzbees.hrma.entities.Agency;
import com.bzbees.hrma.entities.Person;
import com.bzbees.hrma.entities.ProfileImg;
import com.bzbees.hrma.entities.User;
import com.bzbees.hrma.entities.UserRole;
import com.bzbees.hrma.services.AgencyService;
import com.bzbees.hrma.services.PersonService;
import com.bzbees.hrma.services.ProfileImgService;
import com.bzbees.hrma.services.RoleService;
import com.bzbees.hrma.services.UserService;

@Controller
@RequestMapping("/agency")
@SessionAttributes({"person", "userAccount", "agency","lastPicList"})
public class AgencyController {
	
	
	@Autowired
	private AgencyService agencyServ;
	
	
	@Autowired
	private UserService userServ;
	
	@Autowired
	private PersonService persServ;
	
	@Autowired
	private ProfileImgService profileImgServ;
	
	@Autowired
	private RoleService roleServ;
	
	
	@GetMapping("/register")
	public String shorAgencyRegistrationForm(Model model, RedirectAttributes redirAttr, 
			Authentication auth, HttpSession session) {
		
		if(auth.getName() ==null ) {
			return "home";
		}
		
		//get the user from user Principal
		User user = (User) userServ.loadUserByUsername(auth.getName());
		System.out.println("ANy users in here? " + "--->" + user.getUsername());		
				
		//get the person from repo user query
		Person person = persServ.findPersonByUserId(user.getUserId());
		
		//add a new agency instance to the model
		Agency agency = new Agency();
		
		//get the last pic of the user by person id
		List<ProfileImg> picList = profileImgServ.getPicsByPersonId(person.getPersonId());
		List<ProfileImg> lastPicList = new ArrayList();
		int index = picList.size()-1;
		if(index>=0) {
			ProfileImg lastPic = picList.get(index);

			lastPicList.add(lastPic);

		}
		
//		session.setAttribute("lastPicList", lastPicList);
//		session.setAttribute("userAccount", user);
//		session.setAttribute("person", person);
//		session.setAttribute("agency", agency);

		
		
		model.addAttribute("lastPicList", lastPicList);
		model.addAttribute("userAccount", user);
		model.addAttribute("person", person);
		model.addAttribute("agency", agency);

		
		return "agency/agencyRegister";
	}
	
	@PostMapping("/save")
	public String saveAgency(Model model, RedirectAttributes redirAttr, Authentication auth,
				Agency agency, Person person) {

		//check the user id before saving, if already there is an agency-user saved 
		User user = (User) userServ.loadUserByUsername(auth.getName());
		
		UserRole role = new UserRole("AGENCY");
		if(!user.getRoles().contains(role)) {
			
			auth = SecurityContextHolder.getContext().getAuthentication();

			List<GrantedAuthority> updatedAuthorities = new ArrayList<>(auth.getAuthorities());
			
			//add your role here [e.g., new SimpleGrantedAuthority("ROLE_NEW_ROLE")]
			updatedAuthorities.add(new SimpleGrantedAuthority("AGENCY")); 
			

			Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), updatedAuthorities);

			SecurityContextHolder.getContext().setAuthentication(newAuth);
			
			
			user.addRole(role);
			roleServ.saveRole(role);
			
		}

	
		userServ.save(user);
		agency.setUser(user);
		agencyServ.saveAgency(agency);
		
		redirAttr.addAttribute("userAccount", user);
		
		return "redirect:/person/sprofile";
	}
	
	@GetMapping("/profile") 
	public String showAgencyProfile(Model model, RedirectAttributes redirAttr, Authentication auth,
				Agency agency, Person person ) {
		
		
		
		return "agency/profile";
	}
	

}
