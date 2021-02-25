package com.bzbees.hrma.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bzbees.hrma.entities.Agency;
import com.bzbees.hrma.entities.Doc;
import com.bzbees.hrma.entities.Job;
import com.bzbees.hrma.entities.Language;
import com.bzbees.hrma.entities.Person;
import com.bzbees.hrma.entities.ProfileImg;
import com.bzbees.hrma.entities.Skill;
import com.bzbees.hrma.entities.User;
import com.bzbees.hrma.services.AgencyService;
import com.bzbees.hrma.services.DocService;
import com.bzbees.hrma.services.ImageResize;
import com.bzbees.hrma.services.JobService;
import com.bzbees.hrma.services.LanguageService;
import com.bzbees.hrma.services.PersonService;
import com.bzbees.hrma.services.ProfileImgService;
import com.bzbees.hrma.services.ProfileToPDF;
import com.bzbees.hrma.services.SkillService;
import com.bzbees.hrma.services.UserService;

@Controller
@SessionAttributes({ "person", "userAccount","picList", "lastPicList", "jobList"})
//, "skillsList", "langList", "docList",  
@RequestMapping("/person")
public class PersonController {
	
	@Autowired
	PersonService persServ;
	
	@Autowired
	SkillService skillServ;
	
	@Autowired
	LanguageService langServ;
	
	@Autowired
	JobService jobServ;
	
	@Autowired
	UserService userServ;
	
	@Autowired
	DocService docServ;

	@Autowired
	ProfileImgService profileImgServ;
	
	@Autowired
	ImageResize imgResizerServ;
	
	@Autowired
	AgencyService agencyServ;

	
	@GetMapping("/sprofile")
	public String displayLoggedInProfile( Model model, 
			@ModelAttribute("passChanged") String passChanged,
			@ModelAttribute("success") String success,
			Authentication auth) {
	
		if(auth.getName() ==null) {
			return "home";
		}
		//get the user from user Principal
		User user = (User) userServ.loadUserByUsername(auth.getName());
		
		
		//get the person from repo user query
		Person person = persServ.findPersonByUserId(user.getUserId());

		System.out.println("What user is in the profiles? : " + user.getUserId());
		
		//get the personId in a long variable
		long personId = person.getPersonId();
		

		
		//get the skills from repo skills
		List<Skill> personSkills = skillServ.getSavedSkillsByPersonId(personId);
		
		//get the lang from repo lang
		List<Language> personLang = langServ.getLangsSavedByPersonId(personId);
		
		//get the jobs from the job repo
		List<Job> personJobs = jobServ.getJobsByPersonId(personId);
		
		//get the docs from the docs repo
		List<Doc> personDocs = docServ.getDocsByPersonId(personId);
		
		//get the pics from the profileImg repo
		List<ProfileImg> personPics = profileImgServ.getPicsByPersonId(personId);
		
		//get the last pic of the person's profile from profileImg repo
		ProfileImg theImg = profileImgServ.getLastProfilePic(personId);
		if(theImg != null) {
			System.out.println("in the if !null of the image");
			model.addAttribute("img", theImg);	
			
			List<ProfileImg> lastPicList = new ArrayList<>();
			lastPicList.add(profileImgServ.getLastProfilePic(person.getPersonId()));
			model.addAttribute("lastPicList", lastPicList);
			
		} else {
			personPics.clear();
			System.out.println("ALL CLEAR");
			model.addAttribute("img", new ProfileImg());
			model.addAttribute("lastPicList", new ArrayList<>());
		}
		
		
		//get the agency is any is associated with current user 
		if(agencyServ.findAgencyByUserId(user.getUserId()) !=null) {
			Agency agency = agencyServ.findAgencyByUserId(user.getUserId());
			model.addAttribute("agency", agency);
			System.out.println("Agency in the PersonController is " + agency.getAgencyName());
		} else {
			Agency agency = new Agency();
			model.addAttribute("agency", agency);
			System.out.println("NEW Agency in the PersonController is added ");
		}

		

		model.addAttribute("picList", personPics);
		model.addAttribute("docList", personDocs);
		model.addAttribute("jobList", personJobs);
		model.addAttribute("langList", personLang);
		model.addAttribute("skillsList", personSkills);
		model.addAttribute("userAccount", user);
		model.addAttribute("person", person);
		
	
		
		List<ProfileImg> lastPicListCheck = (List<ProfileImg>) model.getAttribute("lastPicList");
		System.out.println("lastPicListCheck size is " + lastPicListCheck.size());
		
	
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
	
	@PostMapping("/addSkillModal")
	public String addSkillByModal(Model model, Person person, 
			Skill skill, RedirectAttributes redirAttr) {
		
		List<Skill> savedSkills = skillServ.getSavedSkillsByPersonId(person.getPersonId());

		skillServ.save(skill);
			
//		person.addSkill(skill); 
			
		savedSkills.add(skill);
		
		person.setSkills(savedSkills);
		
		persServ.save(person);

		redirAttr.addFlashAttribute("skillsList", savedSkills);
		
		return "redirect:/person/sprofile";
		
	}
	
	@GetMapping("/deleteSkillbyProfile")
	public String deleteSkillFromProfile(@RequestParam("id") long id, Person person, Model model, RedirectAttributes redirAttr) {

		List<Skill> sessionPersonSkills = skillServ.getSavedSkillsByPersonId(person.getPersonId());
		
		for(Skill skill : sessionPersonSkills) {
			if(skill.getSkillId() == id) {
				skillServ.deleteSkill(skill);
			} else {
				System.out.println("No skill here by this id --->" + id);
			}
		}		

		redirAttr.addFlashAttribute("skillsList", skillServ.getSavedSkillsByPersonId(person.getPersonId()));

		return "redirect:/person/sprofile";
	}

	@PostMapping("/addLangModal")
	public String addLangByProfile (Model model, Person person, 
			Language lang, RedirectAttributes redirAttr) {
		
		List<Language> savedLangs = langServ.getLangsSavedByPersonId(person.getPersonId());
		
		langServ.save(lang);
		
//		person.addLang(lang);
		
		savedLangs.add(lang);
		
		person.setLanguages(savedLangs);
		
		persServ.save(person);
		
		redirAttr.addFlashAttribute("langList", savedLangs);
		
		return "redirect:/person/sprofile";
	}
	
	
	@GetMapping("/deleteLangbyProfile")
	public String deleteLangbyProfile(@RequestParam("id") long id, Model model, 
									Person person,
									RedirectAttributes redirAttr) {
		
		List<Language> sessionPersonLanguages = langServ.getLangsSavedByPersonId(person.getPersonId());
		
		for(Language lang : sessionPersonLanguages) {
			if(lang.getLangId() == id) {
				langServ.deleteLang(lang);
			}
		}
		

		redirAttr.addFlashAttribute("langList", langServ.getLangsSavedByPersonId(person.getPersonId()));

		
		return "redirect:/person/sprofile";
	}
	
	@PostMapping("/addModalJob")
	public String addJobFromProfile(Model model, Person person, 
			Job job, RedirectAttributes redirAttr) {
		
		
		List<Job> savedJobs = jobServ.getJobsByPersonId(person.getPersonId());
		
		jobServ.save(job);
		
//		person.addJob(job);
		
		savedJobs.add(job);
		
		person.setJobs(savedJobs);
		
		persServ.save(person);

		
		redirAttr.addFlashAttribute("jobList", savedJobs);
		
		
		return "redirect:/person/sprofile";
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
		
		return "redirect:/person/sprofile";
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
		
		return "redirect:/person/sprofile";
	}
	
	
	@GetMapping("/deleteJobByModal")
	public String deleteJobModalById(@RequestParam("id") long id, Model model, Person person,
			RedirectAttributes redirAttr) {

		List<Job> sessionPersonJobs = jobServ.getJobsByPersonId(person.getPersonId());
		
		for (Job job : sessionPersonJobs) {
			if(job.getJobId() == id) {
				jobServ.deleteJobById(job);
			}
		}
			
		redirAttr.addFlashAttribute("jobList", jobServ.getJobsByPersonId(person.getPersonId()));

		return "redirect:/person/sprofile";
				
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
//						person.addDoc(theDoc);
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

		return "redirect:/person/sprofile";
	}

	
	@PostMapping(value="/makeDocPrivate")
	public String makeDocPrivate(@Valid @ModelAttribute("doc") Doc setDoc, 
			BindingResult bindingResult, @RequestParam("id") long id, 
			Model model, Person person, RedirectAttributes redirAttr ) throws ResourceNotFoundException {
		
		Doc doc = docServ.findDocById(id);
		
		doc.setDocPrivate(setDoc.isDocPrivate());
		docServ.saveDoc(doc);
		
		
		redirAttr.addFlashAttribute("docList", docServ.getDocsByPersonId(person.getPersonId()));
		
		return "redirect:/person/sprofile";
	}
	
	@GetMapping("/deleteProfileDoc")
	public String deleteProfileDocById(@RequestParam("id") long id, Model model, 
			Person person, RedirectAttributes redirAttr) {

		
		List<Doc> docList = docServ.getDocsByPersonId(person.getPersonId());
		
		for (Doc doc : docList) {
			if(doc.getDocId() == id) {
				docServ.deleteDocById(doc);
			}

		}

//		persServ.save(person);
		docServ.flushDocDb();
		List<Doc> reSavedDocList = docServ.getDocsByPersonId(person.getPersonId());

		redirAttr.addFlashAttribute("docList", reSavedDocList);


		return "redirect:/person/sprofile";
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

		
		Person person = persServ.findPersonById(id);
		System.out.println("Which person is here? " + " "+ person.getPersonId() + " " + person.getLastName());
		model.addAttribute("person", person);

		if (!model.containsAttribute("picList")) {
			List<ProfileImg> picList = person.getPics();
			model.addAttribute("picList", picList);
		}

		if (!model.containsAttribute("lastPicList")) {
			List<ProfileImg> lastPicList = new ArrayList<>();
			lastPicList.add(profileImgServ.getLastProfilePic(person.getPersonId()));
			model.addAttribute("lastPicList", lastPicList);
		}
		
		@SuppressWarnings("unchecked")
		List<ProfileImg> lastPicListCheck = (List<ProfileImg>) model.getAttribute("lastPicList");
		System.out.println("lastPicListCheck size is " + lastPicListCheck.size());

		if (!model.containsAttribute("img")) {
			model.addAttribute("img", new ProfileImg());

		}

		
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



	@PostMapping(value = "/addProfileImg", consumes = { "multipart/form-data" })
	public String addProfileImg(Model model, Person person, @RequestParam("img") MultipartFile img,
			RedirectAttributes redirAttr) {
	
	
		String imgName = StringUtils.cleanPath(img.getOriginalFilename());
	
		//init a new byte array	
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
	    //the bytes you want for writing your class image 
	    byte[] newBytes = null;
		    
	    	baos = imgResizerServ.resizeImage(img);
		
			newBytes = baos.toByteArray();
		    
		    try {
				baos.close();
			} catch (IOException e) {
				System.out.println("ByteArrayOutputSteam is not closed " );
				e.printStackTrace();
			}
		    
		    System.out.println("newbytes ====== new money " + newBytes.toString());   
		    
			ProfileImg smallImg = new ProfileImg(imgName, img.getContentType(), newBytes);

			List<ProfileImg> picList = profileImgServ.getPicsByPersonId(person.getPersonId());

			profileImgServ.savePic(smallImg);

			picList.add(smallImg);

			List<ProfileImg> lastPicList = new ArrayList<>();

			lastPicList.add(profileImgServ.getLastProfilePic(person.getPersonId()));

			person.setPics(picList);

			persServ.save(person);				
		
			redirAttr.addFlashAttribute("lastPicList", lastPicList);
			redirAttr.addFlashAttribute("picList", picList);
			redirAttr.addFlashAttribute("img", smallImg);

		return "redirect:/person/sprofile";
	}

	@GetMapping(value = "/img")
	public ResponseEntity<?> showProfileImg(@RequestParam("imgId") long id, Person person) {
//		ProfileImg profilePic = profileImgServ.findProfilePicById(id);
		if(profileImgServ.getLastProfilePic(person.getPersonId()) != null) {
			ProfileImg profilePic = profileImgServ.getLastProfilePic(person.getPersonId());
			
			System.out.println("profile pic that is not displaying is " + profilePic.getPicName());

			return ResponseEntity.ok()
					.contentType(MediaType
							.parseMediaType(profilePic.getPicType()))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename =\"" + profilePic.getPicName() + "\"")
					.body(new ByteArrayResource(profilePic.getData()));
			
		}
		
		return null;

	}
	
	@GetMapping("/profilePDF")
	public ResponseEntity<?> displayProfilePDF(Person person) {
		ByteArrayInputStream ptf = ProfileToPDF.exportProfile(person);

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename =\"" + "profilePDF" + "\"")
				.body(new InputStreamResource(ptf));
	}
	
	@PostMapping("/finishAccount")
	public String displayHomePage(SessionStatus status) {
		
//		status.setComplete();
		return "redirect:/";
	}
	
	
	


}
