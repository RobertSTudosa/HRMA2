package com.bzbees.hrma.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bzbees.hrma.entities.Job;
import com.bzbees.hrma.entities.Language;
import com.bzbees.hrma.entities.Person;
import com.bzbees.hrma.entities.ProfileImg;
import com.bzbees.hrma.entities.Skill;
import com.bzbees.hrma.entities.User;
import com.bzbees.hrma.services.PersonService;

@Controller

@RequestMapping("/person")
public class PersonController {
	
	@Autowired
	PersonService persServ;
	
	@GetMapping("/sprofile")
	public String displayLoggedInProfile( Model model, Authentication auth) {
	
		
		System.out.println("User is : " + auth.getName());
		
		User user = (User) auth.getPrincipal();
		
		System.out.println("User username is " + user.getUserName());
//		Person whichPerson = (Person) model.getAttribute("person");
//		System.out.println(
//				"person in getmapping 'PROFILE' is " + whichPerson.getFirstName() + " " + whichPerson.getLastName());

		if (!model.containsAttribute("picList")) {
			List<ProfileImg> picList = new ArrayList<>();
			model.addAttribute("picList", picList);
		}

		if (!model.containsAttribute("lastPicList")) {
			List<ProfileImg> lastPicList = new ArrayList<>();
			model.addAttribute("lastPicList", lastPicList);
		}
		
		List<ProfileImg> lastPicListCheck = (List<ProfileImg>) model.getAttribute("lastPicList");
		System.out.println("lastPicListCheck size is " + lastPicListCheck.size());

		if (!model.containsAttribute("img")) {
			model.addAttribute("img", new ProfileImg());

		}

		ProfileImg whichImg = (ProfileImg) model.getAttribute("img");
		System.out.println("Image name is " + whichImg.getPicName());
		
		if (!model.containsAttribute("job")) {
			Job job = new Job();
			model.addAttribute("job", job);
			System.out.println("New job created <------------");
		}
		
		
		if (!model.containsAttribute("skill")) {
			Skill skill = new Skill();
			model.addAttribute("skill", skill);
			System.out.println("New skill created <------------");

		}
		
		if (!model.containsAttribute("lang")) {
			Language lang = new Language();
			model.addAttribute("lang", lang);
			System.out.println("New lang created <------------");
		}
		
		

		System.out.println("Hitting profile endpoint");

		return "user/session_profile";

	}


	@GetMapping("/newPerson") 
		public String displayRegisterForm (Model model) {
			Person person = new Person();
			model.addAttribute("person", person);
		
			return "register";
		}
	
	@PostMapping("savePerson") 
	public String createNewUser (Model model, Person person) {
		persServ.save(person);
		return "user/profile";
	}

}
