package com.bzbees.hrma.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bzbees.hrma.entities.ConfirmationToken;
import com.bzbees.hrma.entities.Doc;
import com.bzbees.hrma.entities.Job;
import com.bzbees.hrma.entities.Language;
import com.bzbees.hrma.entities.Person;
import com.bzbees.hrma.entities.ProfileImg;
import com.bzbees.hrma.entities.Skill;
import com.bzbees.hrma.entities.User;
import com.bzbees.hrma.entities.UserRole;
import com.bzbees.hrma.services.ConfirmationTokenService;
import com.bzbees.hrma.services.DocService;
import com.bzbees.hrma.services.EmailService;
import com.bzbees.hrma.services.JobService;
import com.bzbees.hrma.services.LanguageService;
import com.bzbees.hrma.services.PersonService;
import com.bzbees.hrma.services.ProfileImgService;
import com.bzbees.hrma.services.ProfileToPDF;
import com.bzbees.hrma.services.RoleService;
import com.bzbees.hrma.services.SkillService;
import com.bzbees.hrma.services.UserService;


@Controller
@SessionAttributes({ "person", "userAccount", "skillsList", "langList", "jobList", "docList", "picList", "lastPicList" })
@RequestMapping("/user")
public class UserController {

	@Autowired
	PersonService persServ;

	@Autowired
	UserService userServ;

	@Autowired
	SkillService skillServ;

	@Autowired
	LanguageService langServ;

	@Autowired
	JobService jobServ;

	@Autowired
	DocService docServ;

	@Autowired
	ProfileImgService profileImgServ;
	
	@Autowired
	RoleService roleServ;
	
	@Autowired
	BCryptPasswordEncoder bCryptEncoder;
	
	@Autowired
	ConfirmationTokenService confTokenServ;
	
	@Autowired
	EmailService emailService;
	
	
	@Autowired
	AuthenticationManager authManager;

	@GetMapping(value={"","/"})
	public String displayRegisterForm(Model model, HttpSession session, Authentication auth) {
		
		if(auth != null) {
			System.out.println("User is " + auth.getName());
			return "redirect: ";
		}
		
		if (!model.containsAttribute("person")) {
			Person person = new Person();
			model.addAttribute("person", person);
			session.setAttribute("person", person);
		}

		if (!model.containsAttribute("userAccount")) {
			User userAccount = new User();
			model.addAttribute("userAccount", userAccount);
		}

		System.out.println("Hitting the register endpoint");

		return "user/register";
	}

	@PostMapping("/save")
	public String createAndSaveUserAndPerson(Model model, User userAccount, Person person,
			RedirectAttributes redirAttr, HttpSession session) {
		
		System.out.println("person is " + person.getLastName());
								
		
				if (person.getLastName() != null) {

					person.setAppStatus(1);

					if (persServ.checkAvailability(person) && persServ.checkBirthDate(person)) {
						
						System.out.println("Persons are : " + persServ.getAll().toString());
						
						
						//used to avoid detach exception 
						if(!persServ.getAll().contains(persServ.findPersonById(person.getPersonId()))) {
														
							UserRole role1 = new UserRole("USER");
							UserRole role2 = new UserRole("CANDIDATE");
							roleServ.saveRole(role1);
							roleServ.saveRole(role2);
							userAccount.addRole(role1);
							userAccount.addRole(role2);
							
							userAccount.setPerson(person);
							
							//set the user active 
//							userAccount.setActive(true);
							
							//encrypt the password for the user
							persServ.save(person);
							userAccount.setPassword(bCryptEncoder.encode(userAccount.getPassword()));
							userServ.save(userAccount);	
							
							ConfirmationToken confirmationToken = new ConfirmationToken(userAccount);
							System.out.println("confirmationToken is " + confirmationToken.getConfirmationToken());
							confTokenServ.saveToken(confirmationToken);
							SimpleMailMessage mailMessage = emailService.simpleConfirmationMessage(userAccount.getEmail(), 
									"BZBees - please confirm your account, jobs are ready", confirmationToken.getConfirmationToken());
							
							
			
				emailService.sendEmail(mailMessage);
				System.out.println("Email was sent according to spring boot");

						}
							
											
					} else {
						System.out.println("Invalid dates baby");
						skillServ.deleteAll();
						return "user/register";
					}
		
		}
		
		int usersNo = userServ.getAll().size();
		System.out.println("Number of users " + " " + "is "+ usersNo);

		System.out.println("Hitting the save from user/save POST ");
		
		session.setAttribute("person", person);
		session.setAttribute("userAccount", userAccount);
		
		redirAttr.addFlashAttribute("person", person);
		redirAttr.addFlashAttribute("userAccount", userAccount);


		return "redirect:/user/skills";

	}
	
	@RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST}) 
	public String confirmAccount (Model model, @RequestParam("t0") String confirmationToken, User userAccount, Person person,
			HttpServletRequest request, RedirectAttributes redirAttr	) {
		
		ConfirmationToken checkedToken = confTokenServ.findConfirmationTokenByConfirmationToken(confirmationToken);
		if(checkedToken !=null) {
			
			userAccount = checkedToken.getUser();
			person = userAccount.getPerson();
					
			System.out.println("Who is this person ? " + person.getLastName());
			System.out.println("Who is this user? " + userAccount.getUsername());
			System.out.println("authorities are : " + userAccount.getAuthorities().toString());
			System.out.println("What password is passed here via user.getpassword " + userAccount.getPassword());
			
			userAccount.setActive(true);
			userAccount.setPerson(person);
			persServ.save(person);
			userServ.save(userAccount);
			
			//used to auto login the user coming from email			
			request.getSession();		
			Authentication authentication = new UsernamePasswordAuthenticationToken(userAccount,null, userAccount.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			
			System.out.println("This is user activation is " + userAccount.isActive());
			redirAttr.addFlashAttribute("message", "User " + userAccount.getUsername() + " is confirmed");
		}
		

	
	return "home";
	}	
	
	
	

	@GetMapping("/skills")
	public String displayAddSkill(Model model, Authentication auth) {
		
		if(auth != null) {
			System.out.println("User is " + auth.getName());
			return "redirect: ";
		}

		Person whichPerson = (Person) model.getAttribute("person");
		System.out.println(
				"person in getmapping 'skills' is " + whichPerson.getFirstName() + " " + whichPerson.getLastName());

		if (!model.containsAttribute("skillsList")) {
			List<Skill> skillList = new ArrayList<>();
			model.addAttribute("skillsList", skillList);
		} else {
			List<Skill> whichSkillSet = (List<Skill>) model.getAttribute("skillsList");
			for(Skill aSkill : whichSkillSet) {
				System.out.println("GetMapping 'skills' getSkills() " + aSkill.getSkillName());				
			}
		}

		if (!model.containsAttribute("skill")) {
			Skill skill = new Skill();
			model.addAttribute("skill", skill);
			System.out.println("New skill created <------------");

		} else {
			Skill whichSkill = (Skill) model.getAttribute("skill");
			System.out.println("skill in getmapping 'skills' is " + whichSkill.getSkillName());
		}

		System.out.println("Hitting skills endpoint");

		return "user/skills";

	}

	@PostMapping("/addSkill")
	public String addSkill(Model model, Person person, Skill skill, RedirectAttributes redirAttr) {

		List<Skill> savedSkills = skillServ.getSavedSkillsByPersonId(person.getPersonId());
		
		skillServ.save(skill);
		
		person.addSkill(skill); 
		
		savedSkills.add(skill);

		person.setSkills(savedSkills);
		
		persServ.save(person);

		redirAttr.addFlashAttribute("skillsList", savedSkills);

		return "redirect:/user/skills";
	}
	
	
	@PostMapping("/addSkillModal")
	public String addSkillByModal(Model model, Person person, 
			Skill skill, RedirectAttributes redirAttr) {

		List<Skill> savedSkills = skillServ.getSavedSkillsByPersonId(person.getPersonId());
		
		skillServ.save(skill);
			
		person.addSkill(skill); 
			
		savedSkills.add(skill);
		
		person.setSkills(savedSkills);
		
		persServ.save(person);

		redirAttr.addFlashAttribute("skillsList", savedSkills);
		
		return "redirect:/user/profile";
		
	}

	@GetMapping("/deleteSkill")
	public String deleteSkillFromList(@RequestParam("id") long id, Person person, Model model, 
				RedirectAttributes redirAttr) {
		
		List<Skill> savedSkills = skillServ.getSavedSkillsByPersonId(person.getPersonId());//0  //1
		
		Skill theSkill = skillServ.findSkillById(id);
		skillServ.deleteSkill(theSkill);
		person.removeSkill(theSkill.getSkillId());
		savedSkills.remove(theSkill.getSkillId());

		
		List<Skill> reSavedSkills = skillServ.getSavedSkillsByPersonId(person.getPersonId());
		
		redirAttr.addFlashAttribute("skillsList", reSavedSkills);

		return "redirect:/user/skills";
	}
	
	
	@GetMapping("/deleteSkillbyProfile")
	public String deleteSkillFromProfile(@RequestParam("id") long id, Person person, Model model, RedirectAttributes redirAttr) {
		
		
		Skill theSkill = skillServ.findSkillById(id);
		
		person.removeSkill(theSkill.getSkillId());
		
		skillServ.deleteSkill(theSkill);
		

		
		

		redirAttr.addFlashAttribute("skillsList", skillServ.getSavedSkillsByPersonId(person.getPersonId()));

		return "redirect:/user/profile";
	}

	@PostMapping(value = "/saveSkills", params = "next")
	public String saveListOfSkills(Model model, Person person, Skill skill, RedirectAttributes redirAttr) {
		
		
		redirAttr.addFlashAttribute("skillsList", skillServ.getSavedSkillsByPersonId(person.getPersonId()));

		return "redirect:/user/lang";

	}



	@GetMapping("/lang")
	public String displayLang(Model model) {

		Person whichPerson = (Person) model.getAttribute("person");
		System.out.println(
				"person in getmapping 'LANG' is " + whichPerson.getFirstName() + " " + whichPerson.getLastName());

		if (!model.containsAttribute("langList")) {
			List<Language> langList = new ArrayList<>();
			model.addAttribute("langList", langList);
		} else {
			List<Language> whichLangSet = (List<Language>) model.getAttribute("langList");
			System.out.println("GetMapping 'skills' getLanguages() " + whichPerson.getLanguages().toString());

			System.out.println("whichLangSet is " + whichLangSet.toString());
		}

		if (!model.containsAttribute("lang")) {
			Language lang = new Language();
			model.addAttribute("lang", lang);
			System.out.println("New lang created <------------");

		} else {
			Language whichLang = (Language) model.getAttribute("lang");
			System.out.println("Lang in getmapping 'LANG' is " + whichLang.getName());
		}

		System.out.println("Hitting lang endpoint");

		return "user/lang";
	}

	@PostMapping(value = "/addLang")
	public String addLang(Model model, Person person, Language lang, RedirectAttributes redirAttr) {
		
		List<Language> savedLangs = langServ.getLangsSavedByPersonId(person.getPersonId());
		
		langServ.save(lang);
		
		person.addLang(lang);
		
		savedLangs.add(lang);
		
		person.setLanguages(savedLangs);
		
		persServ.save(person);
		
		redirAttr.addFlashAttribute("langList", savedLangs);

		return "redirect:/user/lang";
	}
	
	@PostMapping("/addLangModal")
	public String addLangByProfile (Model model, Person person, 
			Language lang, RedirectAttributes redirAttr) {
		
		List<Language> savedLangs = langServ.getLangsSavedByPersonId(person.getPersonId());
		
		langServ.save(lang);
		
		person.addLang(lang);
		
		savedLangs.add(lang);
		
		person.setLanguages(savedLangs);
		
		persServ.save(person);
		
		redirAttr.addFlashAttribute("langList", savedLangs);
		
		return "redirect:/user/profile";
	}
	

	@GetMapping("/deleteLang")
	public String deleteLangById(@RequestParam("id") long id, Model model,Person person, RedirectAttributes redirAttr) {

		List<Language> savedLangs = langServ.getLangsSavedByPersonId(person.getPersonId());
		
		Language theLang = langServ.findLangById(id);
		langServ.deleteLang(theLang);
		person.removeLang(theLang.getLangId());
		
		savedLangs.remove(theLang.getLangId());
		
		List<Language> reSavedLangs = langServ.getLangsSavedByPersonId(person.getPersonId());

		redirAttr.addFlashAttribute("langList", reSavedLangs);

		return "redirect:/user/lang";
	}
	
	@GetMapping("/deleteLangbyProfile")
	public String deleteLangbyProfile(@RequestParam("id") long id, Model model, 
									Person person,
									RedirectAttributes redirAttr) {
		

		
		Language theLang = langServ.findLangById(id);
		langServ.deleteLang(theLang);
		person.removeLang(theLang.getLangId());
		

		redirAttr.addFlashAttribute("langList", langServ.getLangsSavedByPersonId(person.getPersonId()));

		
		return "redirect:/user/profile";
	}

	@PostMapping(value = "/saveLang", params = "next")
	public String saveListOfLangs(Model model, Person person, Language lang, RedirectAttributes redir) {
		
		redir.addFlashAttribute("langList", langServ.getLangsSavedByPersonId(person.getPersonId()));

		return "redirect:/user/job";

	}


	@GetMapping("/job")
	public String displayJobForm(Model model) {

		Person whichPerson = (Person) model.getAttribute("person");
		System.out.println(
				"person in getmapping 'JOB' is " + whichPerson.getFirstName() + " " + whichPerson.getLastName());

		if (!model.containsAttribute("jobList")) {
			List<Job> jobList = new ArrayList<>();
			model.addAttribute("jobList", jobList);
			System.out.println("added 'jobList' to model");
		} else {
			List<Job> whichJobSet = (List<Job>) model.getAttribute("jobList");
			System.out.println("GetMapping 'job' getJobs() " + whichPerson.getJobs().toString());

			System.out.println("whichJobSet is " + whichJobSet.toString());
		}

		if (!model.containsAttribute("job")) {
			Job job = new Job();
			model.addAttribute("job", job);
			System.out.println("New job created <------------");

		} else {
			Job whichJob = (Job) model.getAttribute("job");
			System.out.println("Job in getmapping 'JOB' is " + whichJob.getJobTitle());
		}

		System.out.println("Hitting job endpoint");

		return "user/job";
	}

	@PostMapping("/addJob")
	public String addJobsToPerson(Model model, Person person, Job job, RedirectAttributes redirAttr) {
		
		List<Job> savedJobs = jobServ.getJobsByPersonId(person.getPersonId());
	
		jobServ.save(job);
		
		person.addJob(job);
		
		savedJobs.add(job);
		
		person.setJobs(savedJobs);
		
		persServ.save(person);

		
		redirAttr.addFlashAttribute("jobList", savedJobs);
		

		return "redirect:/user/job";
	}
	
	@PostMapping("/addModalJob")
	public String addJobFromProfile(Model model, Person person, 
			Job job, RedirectAttributes redirAttr) {
		
		
		List<Job> savedJobs = jobServ.getJobsByPersonId(person.getPersonId());
		
		jobServ.save(job);
		
		person.addJob(job);
		
		savedJobs.add(job);
		
		person.setJobs(savedJobs);
		
		persServ.save(person);

		
		redirAttr.addFlashAttribute("jobList", savedJobs);
		
		
		return "redirect:/user/profile";
	}

	@PostMapping(value = "/saveJob", params = "next")
	public String saveListOfJobs(Model model, Person person, Job job, RedirectAttributes redirAttr,
			SessionStatus status) {
		
		redirAttr.addFlashAttribute("jobList", jobServ.getJobsByPersonId(person.getPersonId()));

		return "redirect:/user/upload";
	}
	
	@PostMapping(value="/editJob", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
	public String partialUpdate(@Valid @ModelAttribute("job") Job patchJob, 
			BindingResult bindingResult, @RequestParam("id") long id, 
			Model model, Person person, RedirectAttributes redirAttr ) throws ResourceNotFoundException {
		

		
		Job job = jobServ.findJobById(id);

		job.setJobPrivate(patchJob.isJobPrivate());
		job.setJobTitle(patchJob.getJobTitle());
		job.setCompanyName(patchJob.getCompanyName());
		job.setStartDate(patchJob.getStartDate());
		job.setEndDate(patchJob.getEndDate());
		job.setResponsabilities(patchJob.getResponsabilities());

		
		jobServ.save(job);
		
		List<Job> savedJobs = jobServ.getJobsByPersonId(person.getPersonId());
		
		redirAttr.addFlashAttribute("jobList", savedJobs);
		
		return "redirect:/user/profile";
	}
	
	
	@PostMapping(value="/makeJobPrivate")
	public String makePrivate(@Valid @ModelAttribute("job") Job setJob, 
			BindingResult bindingResult, @RequestParam("id") long id, 
			Model model, Person person, RedirectAttributes redirAttr ) throws ResourceNotFoundException {
		
		Job job = jobServ.findJobById(id);
		
		job.setJobPrivate(setJob.isJobPrivate());
		jobServ.save(job);	
		
		List<Job> savedJobs = jobServ.getJobsByPersonId(person.getPersonId());
		redirAttr.addFlashAttribute("jobList", savedJobs);
		
		return "redirect:/user/profile";
	}
	


	@GetMapping("/deleteJob")
	public String deleteJobById(@RequestParam("id") long id, Model model, Person person, RedirectAttributes redirAttr) {
		
		List<Job> savedJobs = jobServ.getJobsByPersonId(person.getPersonId());
		
		Job theJob = jobServ.findJobById(id);
		jobServ.deleteJobById(theJob);
		
		savedJobs.remove(theJob.getJobId());
	
		redirAttr.addFlashAttribute("jobList", jobServ.getJobsByPersonId(person.getPersonId()));

		return "redirect:/user/job";
	}
	
	@GetMapping("/deleteJobByModal")
	public String deleteJobModalById(@RequestParam("id") long id, Model model, Person person,
			RedirectAttributes redirAttr) {

		Job theJob = jobServ.findJobById(id);
		jobServ.deleteJobById(theJob);
		
		System.out.println("job deleted was " + theJob.getJobTitle());
	
		redirAttr.addFlashAttribute("jobList", jobServ.getJobsByPersonId(person.getPersonId()));

		return "redirect:/user/profile";
		
		
		
	}


	@GetMapping("/upload")
	public String displayUploadForm(Model model) {
		System.out.println("before whichPerson");
		Person whichPerson = (Person) model.getAttribute("person");
		System.out.println(
				"person in getmapping 'UPLOAD' is " + whichPerson.getFirstName() + " " + whichPerson.getLastName());

		if (!model.containsAttribute("docList")) {
			List<Doc> docList = new ArrayList<>();
			model.addAttribute("docList", docList);
			System.out.println("added 'docList' to model");
		} else {
			List<Doc> whichDocSet = (List<Doc>) model.getAttribute("docList");
			System.out.println("GetMapping 'UPLOAD' getDocs() " + whichPerson.getDocs().toString());

			System.out.println("whichDocSet is " + whichDocSet.toString());
		}
		
		if (!model.containsAttribute("doc")) {
			Doc doc = new Doc();
			model.addAttribute("doc", doc);
			System.out.println("New doc created <------------");
		}

		System.out.println("Hitting doc endpoint");

		return "user/upload";

	}



	@PostMapping(value = "/addDocs", consumes = { "multipart/form-data" })
	public String addDocs(Model model, Person person, @RequestParam("doc") MultipartFile[] files,
			RedirectAttributes redirAttr) {
		
		List<Doc> docList = docServ.getDocsByPersonId(person.getPersonId());

		for (MultipartFile file : files) {

			String docName = StringUtils.cleanPath(file.getOriginalFilename());

			try {

				if (docName.contains("..")) {
					throw new RuntimeException(
							"Cannot store file with relative path outside current directory " + docName);
				}

				Doc theDoc = new Doc(docName, file.getContentType(), file.getBytes());
				
					if( docServ.getDocsByPersonId(person.getPersonId()).contains(theDoc)) {
						System.out.println("Document " + theDoc.getDocName() + " is already saved");
					} else {
						
						System.out.println(theDoc.getDocName() + " is saved");
						
//						docServ.saveDoc(theDoc);
						docServ.saveAndFlush(theDoc);
						person.addDoc(theDoc);
						docList.add(theDoc);
						
						
						docServ.flushDocDb();


					}
				

				System.out.println("doc is saved" + file.getOriginalFilename());
			} catch (IOException e) {
				e.printStackTrace();

			}
		}
		
		person.setDocs(docList);
		persServ.save(person);
		
		
		redirAttr.addFlashAttribute("docList", docServ.getDocsByPersonId(person.getPersonId()));

		return "redirect:/user/upload";
	}
	
	
	@PostMapping(value = "/addDocsModal", consumes = { "multipart/form-data" })
	public String addDocsByProfile(Model model, Person person, @RequestParam("doc") MultipartFile[] files,
			RedirectAttributes redirAttr) {
		
		List<Doc> docList = docServ.getDocsByPersonId(person.getPersonId());

		for (MultipartFile file : files) {

			String docName = StringUtils.cleanPath(file.getOriginalFilename());

			try {

				if (docName.contains("..")) {
					throw new RuntimeException(
							"Cannot store file with relative path outside current directory " + docName);
				}

				Doc theDoc = new Doc(docName, file.getContentType(), file.getBytes());
				
					if( docServ.getDocsByPersonId(person.getPersonId()).contains(theDoc)) {
						System.out.println("Document " + theDoc.getDocName() + " is already saved");
					} else {
						
						System.out.println(theDoc.getDocName() + " is saved");
						
//						docServ.saveDoc(theDoc);
						docServ.saveAndFlush(theDoc);
						person.addDoc(theDoc);
						docList.add(theDoc);
						
						
						docServ.flushDocDb();


					}
				

				System.out.println("doc is saved" + file.getOriginalFilename());
			} catch (IOException e) {
				e.printStackTrace();

			}
		}
		
		person.setDocs(docList);
		persServ.save(person);
		
		
		redirAttr.addFlashAttribute("docList", docServ.getDocsByPersonId(person.getPersonId()));

		return "redirect:/user/profile";
	}

	@PostMapping(value = "/saveDocs", params = "next")
	public String saveDocs(Model model, Person person, RedirectAttributes redirAttr) {
		
		redirAttr.addFlashAttribute("docList", docServ.getDocsByPersonId(person.getPersonId()));

		return "redirect:/user/profile";
	}



	@GetMapping("/deleteDoc")
	public String deleteDocById(@RequestParam("id") long id, Model model, 
			Person person, RedirectAttributes redirAttr) {
		
		List<Doc> docList = docServ.getDocsByPersonId(person.getPersonId());
		
		Doc theDoc = docServ.findDocById(id);
		docServ.deleteDocById(theDoc);
		
		docList.remove(theDoc.getDocId());
//		persServ.save(person);
		docServ.flushDocDb();
		List<Doc> reSavedDocList = docServ.getDocsByPersonId(person.getPersonId());

		redirAttr.addFlashAttribute("docList", reSavedDocList);

		return "redirect:/user/upload";
	}
	
	@PostMapping(value="/makeDocPrivate")
	public String makeDocPrivate(@Valid @ModelAttribute("doc") Doc setDoc, 
			BindingResult bindingResult, @RequestParam("id") long id, 
			Model model, Person person, RedirectAttributes redirAttr ) throws ResourceNotFoundException {
		
		Doc doc = docServ.findDocById(id);
		
		doc.setDocPrivate(setDoc.isDocPrivate());
		docServ.saveDoc(doc);
		
		
		redirAttr.addFlashAttribute("docList", docServ.getDocsByPersonId(person.getPersonId()));
		
		return "redirect:/user/profile";
	}
	
	@GetMapping("/deleteProfileDoc")
	public String deleteProfileDocById(@RequestParam("id") long id, Model model, 
			Person person, RedirectAttributes redirAttr) {

		
		List<Doc> docList = docServ.getDocsByPersonId(person.getPersonId());
		
		Doc theDoc = docServ.findDocById(id);
		docServ.deleteDocById(theDoc);
		
		docList.remove(theDoc.getDocId());
//		persServ.save(person);
		docServ.flushDocDb();
		List<Doc> reSavedDocList = docServ.getDocsByPersonId(person.getPersonId());

		redirAttr.addFlashAttribute("docList", reSavedDocList);


		return "redirect:/user/profile";
	}
	


	@GetMapping("/downloadFile")
	public ResponseEntity<?> downloadDoc(@RequestParam("docId") long docId) {
		Doc dbDoc = docServ.findDocById(docId);

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(dbDoc.getDocType()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename =\"" + dbDoc.getDocName() + "\"")
				.body(new ByteArrayResource(dbDoc.getData()));
	}
	
	@GetMapping("/displayFile")
	public ResponseEntity<?> displayDoc(@RequestParam("docId") long docId) {
		Doc dbDoc = docServ.findDocById(docId);

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(dbDoc.getDocType()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename =\"" + dbDoc.getDocName() + "\"")
				.body(new ByteArrayResource(dbDoc.getData()));
	}
	
	
	@GetMapping("/profile/{id}")
	public String displayProfileById(@PathVariable ("id") Long id, Model model) {

//		System.out.println("before whichPerson");
//		Person whichPerson = (Person) model.getAttribute("person");
//		System.out.println(
//				"person in getmapping 'PROFILE' is " + whichPerson.getFirstName() + " " + whichPerson.getLastName());
		
		Person person = persServ.findPersonById(id);
		model.addAttribute("person", person);

		if (!model.containsAttribute("picList")) {
			List<ProfileImg> picList = person.getPics();
			model.addAttribute("picList", picList);
		}

		if (!model.containsAttribute("lastPicList")) {
			List<ProfileImg> lastPicList = person.getPics();
			model.addAttribute("lastPicList", lastPicList);
		}
		
		List<ProfileImg> lastPicListCheck = (List<ProfileImg>) model.getAttribute("lastPicList");
		System.out.println("lastPicListCheck size is " + lastPicListCheck.size());

		if (!model.containsAttribute("img")) {
			model.addAttribute("img", new ProfileImg());

		}

		ProfileImg whichImg = (ProfileImg) model.getAttribute("img");
		System.out.println("Image name is " + whichImg.getPicName());
		

		
		if (!model.containsAttribute("docList")) {
			List<Doc> docList = person.getDocs();
			model.addAttribute("docList", docList);
		}
	
		
		if(!model.containsAttribute("skillList")) {
			List<Skill> skillList = person.getSkills();
			model.addAttribute("skillsList", skillList);
		}
		
		if(!model.containsAttribute("langList")) {
			List<Language> langList = person.getLanguages();
			model.addAttribute("langList", langList);
		}
		
		if(!model.containsAttribute("jobList")) {
			List<Job> jobList = person.getJobs();
			model.addAttribute("jobList",jobList);
		}
		
	
			
		
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

		return "user/profile";

	}

	

	@GetMapping("/profile")
	public String displayProfile(Model model) {
		System.out.println("before whichPerson");
		

		Person whichPerson = (Person) model.getAttribute("person");
		System.out.println(
				"person in getmapping 'PROFILE' is " + whichPerson.getFirstName() + " " + whichPerson.getLastName());

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

		return "user/profile";

	}



	@PostMapping(value = "/addProfileImg", consumes = { "multipart/form-data" })
	public String addProfileImg(Model model, Person person, @RequestParam("img") MultipartFile img,
			RedirectAttributes redirAttr) {

		String imgName = StringUtils.cleanPath(img.getOriginalFilename());

		try {
			ProfileImg theImg = new ProfileImg(imgName, img.getContentType(), img.getBytes());
			

			person.addPic(theImg);
			profileImgServ.savePic(theImg);
			persServ.save(person);

			List<ProfileImg> picList = profileImgServ.getProfilePicsToSave();
			

			List<ProfileImg> lastPicList = new ArrayList<>();
			lastPicList.add(profileImgServ.getLastPic(person.getPersonId()));
			System.out.println("lastPicList size is " + lastPicList.size());

			redirAttr.addFlashAttribute("lastPicList", lastPicList);
			redirAttr.addFlashAttribute("picList", picList);
			redirAttr.addFlashAttribute("img", theImg);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return "redirect:/user/profile";
	}

	@GetMapping(value = "/img")
	public ResponseEntity<?> showProfileImg(@RequestParam("imgId") long id) {
		ProfileImg profilePic = profileImgServ.findProfilePicById(id);
		
		System.out.println("profile pic that is not displaying is " + profilePic.getPicName());

		return ResponseEntity.ok()
				.contentType(MediaType
						.parseMediaType(profilePic.getPicType()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename =\"" + profilePic.getPicName() + "\"")
				.body(new ByteArrayResource(profilePic.getData()));

	}
	
	@GetMapping("/profilePDF")
	public ResponseEntity<?> displayProfilePDF(Person person) {
		ByteArrayInputStream ptf = ProfileToPDF.exportProfile(person);

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename =\"" + "profilePDF" + "\"")
				.body(new InputStreamResource(ptf));
	}
	
	@PostMapping("/finishAccount")
	public String displayHomePage(SessionStatus status, Model model, HttpServletRequest request,
			User userAccount) {
		
		model.addAttribute("message", "Account is set up, please login or confirm you account.");

		status.setComplete();
		return "redirect:/";
	}

}
