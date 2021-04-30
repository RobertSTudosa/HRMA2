package com.bzbees.hrma.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
import com.bzbees.hrma.entities.Notification;
import com.bzbees.hrma.entities.Person;
import com.bzbees.hrma.entities.ProfileImg;
import com.bzbees.hrma.entities.Skill;
import com.bzbees.hrma.entities.SocialMedia;
import com.bzbees.hrma.entities.User;
import com.bzbees.hrma.services.AgencyService;
import com.bzbees.hrma.services.DocService;
import com.bzbees.hrma.services.ImageResize;
import com.bzbees.hrma.services.JobService;
import com.bzbees.hrma.services.LanguageService;
import com.bzbees.hrma.services.LikeService;
import com.bzbees.hrma.services.NotificationService;
import com.bzbees.hrma.services.PersonService;
import com.bzbees.hrma.services.ProfileImgService;
import com.bzbees.hrma.services.ProfileToPDF;
import com.bzbees.hrma.services.SkillService;
import com.bzbees.hrma.services.SocialMediaService;
import com.bzbees.hrma.services.UserService;

@Controller
@SessionAttributes({ "person", "userAccount","picList", "lastPicList", "jobList","socialMediaList","userNotifs","userJobsInList",
	"userJobsLiked","userJobsSaved"})
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
	
	@Autowired
	SocialMediaService socialMediaServ;
	
	@Autowired
	NotificationService notifServ;
	
	@Autowired
	LikeService likeServ;

	
	@GetMapping("/sprofile")
	public String displayLoggedInProfile( Model model, 
			@ModelAttribute("passChanged") String passChanged,
			@ModelAttribute("success") String success,
			Authentication auth) {
	
		if(auth ==null) {
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
		
		
		//get the agency if any is associated with current user 
		if(agencyServ.findAgencyByUserId(user.getUserId()) !=null) {
			Agency agency = agencyServ.findAgencyByUserId(user.getUserId());
			model.addAttribute("agency", agency);
			System.out.println("Agency in the PersonController is " + agency.getAgencyName());
		} else {
			Agency agency = new Agency();
			model.addAttribute("agency", agency);
			System.out.println("NEW Agency in the PersonController is added ");
		}
		
		//get the list of job add in a list 
		Set <Job> userJobsInList = jobServ.findJobsAddedToListByPersonId(personId);
		
		Set<Long> userJobsIdInList = jobServ.findJobsIdAddedToListByPersonId(user.getUserId());
			
		Set<Long> userJobsIdLiked = likeServ.findLikedJobsIdsByUsername(auth.getName());
		
		Set<Long> userJobsAppliedTo = jobServ.findJobsIdAppliedToByPersonId(user.getUserId());
				
		Set<Job> userJobsApplied = jobServ.findJobsAppliedByPersonId(personId);
		
					
		model.addAttribute("picList", personPics);
		model.addAttribute("docList", personDocs);
		model.addAttribute("jobList", personJobs);
		model.addAttribute("langList", personLang);
		model.addAttribute("skillsList", personSkills);
		model.addAttribute("userAccount", user);
		model.addAttribute("person", person);
		model.addAttribute("userJobsSaved", userJobsInList);
		model.addAttribute("userJobsLiked", userJobsIdLiked);
		model.addAttribute("userJobsInList", userJobsIdInList);
		model.addAttribute("userJobsIdApplied", userJobsAppliedTo);
		model.addAttribute("userJobsApplied", userJobsApplied);
		
	
		
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
		
		
		if (socialMediaServ.getSocialMediaByPersonId(person.getPersonId()).isEmpty()) {
			System.out.println("Social Media List is empty");
			model.addAttribute("socialMediaList", new ArrayList<>());
			
		} else {
			List<SocialMedia> socList = socialMediaServ.getSocialMediaByPersonId(person.getPersonId());
			for(SocialMedia link : socList) {
				System.out.println("name of the link --->" + link.getName());
				if(link.getName().contains("FACEBook")) {
					System.out.println("Inside facebook");
					String FBIcon = "<span style=\"color: #E9D415;background-color: #484848\" class=\"iconify\" data-inline=\"false\" data-icon=\"dashicons:facebook\"></span>";
					model.addAttribute("spanFB", FBIcon);
				}
				
				if(link.getName().contains("LINKedin")) {
					System.out.println("Finding nemo on linkedin");
					String LNIcon = "<span style=\"color: #E9D415;background-color: #484848;\" class=\"iconify\" data-inline=\"false\" data-icon=\"el:linkedin\"></span>";
					model.addAttribute("spanLN", LNIcon);
				}
				
				if(link.getName().contains("TWITter")) {
					System.out.println("Fly little bird, fly");
					String TWIcon = "<span style=\"color:#484848;background-color: #E9D415;\" class=\"iconify\" data-inline=\"false\" data-icon=\"vaadin:twitter-square\"></span>";
					model.addAttribute("spanTW", TWIcon);
				}
				
				if(link.getName().contains("INSTAgram")) {
					System.out.println("It's all for the gram");
					String INIcon = "<span style=\"color: #E9D415;background-color: #484848;\" class=\"iconify\" data-icon=\"entypo-social:instagram\" data-inline=\"false\"></span>";
					model.addAttribute("spanIN", INIcon);
				}
			
			}
			
			model.addAttribute("socialMediaList", socList);
		}
		
		
		//get the logged in user notifs
		if(!notifServ.findNotificationsByUserId(user.getUserId()).isEmpty()) {
			List<Notification> allUserNotif = notifServ.reverseFindNotificationsByUserId(user.getUserId());
			List<Notification> showUserNotifs = new ArrayList<>();
			int count = allUserNotif.size();
			for(int i = 0; i < 4; i++) {
				
				if(count == 0) {
					break;
				}
				showUserNotifs.add(allUserNotif.get(i));
				count = count -1;
				
			}
			
			model.addAttribute("userNotifs", allUserNotif);
			
		} else {
			model.addAttribute("userNotifs", new ArrayList<>());
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
	
	//, consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
	@GetMapping(value="/editJob")
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
		persServ.save(person);
		
		List<Job> savedJobs = jobServ.getJobsByPersonId(person.getPersonId());
		
		redirAttr.addFlashAttribute("jobList", savedJobs);
		
		return "redirect:/person/sprofile";
	}
	
	
	@GetMapping(value="/makeJobPrivate")
	public String makePrivate(@Valid @ModelAttribute("job") Job setJob, 
			BindingResult bindingResult, @RequestParam("id") long id, 
			Model model, Person person, RedirectAttributes redirAttr ) throws ResourceNotFoundException {
		
		Job job = jobServ.findJobById(id);
		
		if(job.isJobPrivate()) {
			job.setJobPrivate(false);
			jobServ.save(job);	
			
		} else {
			job.setJobPrivate(true);
			jobServ.save(job);	
		}
		
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

	
	@GetMapping(value="/makeDocPrivate")
	public String makeDocPrivate(
			@RequestParam("id") long id, 
			Model model, Person person, RedirectAttributes redirAttr ) throws ResourceNotFoundException {
		
		Doc doc = docServ.findDocById(id);
		
		if(doc.isDocPrivate()) {
			doc.setDocPrivate(false);
			System.out.println("This document is set false (cut) ====> " + doc.getDocId());
			docServ.saveDoc(doc);
		} else {
			doc.setDocPrivate(true);
			System.out.println("This document is set true (cut) ====> " + doc.getDocId());
			docServ.saveDoc(doc);
		}
		
		
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
	public String displayProfileById(@PathVariable ("id") Long id, Model model, Authentication auth) {

		
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
		
		if(auth != null) {
			User user = (User) userServ.loadUserByUsername(auth.getName());
			if(!notifServ.findNotificationsByUserId(user.getUserId()).isEmpty()) {
				List<Notification> allUserNotif = notifServ.reverseFindNotificationsByUserId(user.getUserId());
				List<Notification> showUserNotifs = new ArrayList<>();
				int count = allUserNotif.size();
				for(int i = 0; i < 4; i++) {
				
					if(count == 0) {
						break;
					}
					showUserNotifs.add(allUserNotif.get(i));
					count = count -1;
					
				}

				model.addAttribute("userNotifs", showUserNotifs);
				
			} else {
				model.addAttribute("userNotifs", new ArrayList<>());
			}
			
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
		
		
		
		
		ProfileImg img = null;
		
		List<Doc> docs = new ArrayList<Doc>();

		List<Job> jobs = new ArrayList<Job>();

		List<Skill> skills = new ArrayList<Skill>();

		List<Language> langs = new ArrayList<Language>();
		
		
		
		if(profileImgServ.getLastProfilePic(person.getPersonId()) != null) { 
		
			if(profileImgServ.getLastProfilePic(person.getPersonId()) != null) {
				img = profileImgServ.getLastProfilePic(person.getPersonId());
			} 
			
			if(docServ.getDocsByPersonId(person.getPersonId()) != null) {
				docs = docServ.getDocsByPersonId(person.getPersonId());
			
			}
			
			if(jobServ.getJobsByPersonId(person.getPersonId()) != null) {
				jobs = jobServ.getJobsByPersonId(person.getPersonId());
			}
			
			if(langServ.getLangsSavedByPersonId(person.getPersonId()) != null) {
				langs = langServ.getLangsSavedByPersonId(person.getPersonId());
			}
			
			if(skillServ.getSavedSkillsByPersonId(person.getPersonId()) != null) {
				skills = skillServ.getSavedSkillsByPersonId(person.getPersonId());
			}
			
			
		
		}	
		

		ByteArrayInputStream ptf = ProfileToPDF.exportProfile(person, img, docs, jobs, skills, langs);

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename =\"" + "profilePDF" + "\"")
				.body(new InputStreamResource(ptf));
		

	
	}
	
	@PostMapping("/finishAccount")
	public String displayHomePage(SessionStatus status) {
		
//		status.setComplete();
		return "redirect:/";
	}
	
	@GetMapping("/editProfile")
	public String updatePersonDetails (@Valid @ModelAttribute Person patchPerson, Model model, Person person, 
			User user,	RedirectAttributes redirAttr) {
		
		person.setFirstName(patchPerson.getFirstName());
		person.setLastName(patchPerson.getLastName());
		person.setLocation(patchPerson.getLocation());
		person.setCurrentJob(patchPerson.getCurrentJob());
		person.setPrivateCurrentjob(patchPerson.isPrivateCurrentjob());
		person.setAvailability(patchPerson.getAvailability());
		person.setWorkExperience(patchPerson.getWorkExperience());
		person.setJobWishDesc(patchPerson.getJobWishDesc());
		
		
		
		
		persServ.save(person);

		
		redirAttr.addFlashAttribute("person", person);
		
		return "redirect:/person/sprofile";
	}
	
	
	@PostMapping(value = "/addSocialMediaFB")
	public String addSocialMediabyAgencyProfile(Model model, Agency agency, Person person,
			@RequestParam("url") String url, RedirectAttributes redirAttr) {
		
		if(url.contains("https://www.facebook.com/")) {
			
			String name = "FACEBook";
			
			for(SocialMedia link : socialMediaServ.getSocialMediaByPersonId(person.getPersonId())) {
				if(link.getName().contains(name)) {
					socialMediaServ.deleteSocialMedia(link);
					socialMediaServ.flushSocMediaDb();
					socialMediaServ.getSocialMediaByPersonId(person.getPersonId()).remove(link.getSocialMediaId());
					persServ.save(person);
					
				}
			}
			
			SocialMedia FBSocMedia = new SocialMedia(name,url,null,person);
			
			socialMediaServ.saveAndFlush(FBSocMedia);
			
//			model.addAttribute("span", url);
			redirAttr.addFlashAttribute("span", url);
			
			
		}
		
		if(url.contains("https://www.linkedin.com/")) {
			String name="LINKedin";
			
			for(SocialMedia link : socialMediaServ.getSocialMediaByPersonId(person.getPersonId())) {
				if(link.getName().contains(name)) {
					socialMediaServ.deleteSocialMedia(link);
					socialMediaServ.flushSocMediaDb();
					socialMediaServ.getSocialMediaByPersonId(person.getPersonId()).remove(link.getSocialMediaId());
					persServ.save(person);
					
				}
			}
			
			SocialMedia LNSocMedia = new SocialMedia(name, url, null, person);
			
			socialMediaServ.saveAndFlush(LNSocMedia);
			
			model.addAttribute("span", url);
		}
		
		if(url.contains("https://twitter.com/")) {
			String name="TWITter";
			
			for(SocialMedia link : socialMediaServ.getSocialMediaByPersonId(person.getPersonId())) {
				if(link.getName().contains(name)) {
					socialMediaServ.deleteSocialMedia(link);
					socialMediaServ.flushSocMediaDb();
					socialMediaServ.getSocialMediaByPersonId(person.getPersonId()).remove(link.getSocialMediaId());
					persServ.save(person);
					
				}
			}
			
			SocialMedia TWSocMedia = new SocialMedia(name, url, null, person);
			
			socialMediaServ.saveAndFlush(TWSocMedia);
			
			model.addAttribute("span", url);
		}
		
		if(url.contains("https://www.instagram.com/")) {
			
			String name="INSTAgram";
			
			for(SocialMedia link : socialMediaServ.getSocialMediaByPersonId(person.getPersonId())) {
				if(link.getName().contains(name)) {
					socialMediaServ.deleteSocialMedia(link);
					socialMediaServ.flushSocMediaDb();
					socialMediaServ.getSocialMediaByPersonId(person.getPersonId()).remove(link.getSocialMediaId());
					persServ.save(person);
					
				}
			}
			
			SocialMedia INSocMedia = new SocialMedia(name, url, null, person);
			
			socialMediaServ.saveAndFlush(INSocMedia);
			
			model.addAttribute("span", url);
		}
		
		
		List<SocialMedia> socList = socialMediaServ.getSocialMediaByPersonId(person.getPersonId());
			
		redirAttr.addFlashAttribute("socialMediaList", socList);

		return "redirect:/person/sprofile";
	}
	
	@GetMapping(value="/deleteSocialMedia")
	public String deleteSocialMediaPersonProfile (@RequestParam("url") String url, Model model, Agency agency, 
			Person person, RedirectAttributes redirAttr) {
		
		if(url.contains("https://www.facebook.com/")) {
			
			String name = "FACEBook";
			
			for(SocialMedia link : socialMediaServ.getSocialMediaByPersonId(person.getPersonId())) {
				if(link.getName().contains(name)) {
					socialMediaServ.deleteSocialMedia(link);
					socialMediaServ.flushSocMediaDb();
					socialMediaServ.getSocialMediaByPersonId(person.getPersonId()).remove(link.getSocialMediaId());
					persServ.save(person);
					
				}
			}
			
			
			redirAttr.addFlashAttribute("span", url);
			
			
		}
		
		if(url.contains("https://www.linkedin.com/")) {
			String name="LINKedin";
			
			for(SocialMedia link : socialMediaServ.getSocialMediaByPersonId(person.getPersonId())) {
				if(link.getName().contains(name)) {
					socialMediaServ.deleteSocialMedia(link);
					socialMediaServ.flushSocMediaDb();
					socialMediaServ.getSocialMediaByPersonId(person.getPersonId()).remove(link.getSocialMediaId());
					persServ.save(person);
					
				}
			}
			
			
			model.addAttribute("span", url);
		}
		
		if(url.contains("https://www.linkedin.com/")) {
			String name="LINKedin";
			
			for(SocialMedia link : socialMediaServ.getSocialMediaByPersonId(person.getPersonId())) {
				if(link.getName().contains(name)) {
					socialMediaServ.deleteSocialMedia(link);
					socialMediaServ.flushSocMediaDb();
					socialMediaServ.getSocialMediaByPersonId(person.getPersonId()).remove(link.getSocialMediaId());
					persServ.save(person);
					
				}
			}
			

			
			model.addAttribute("span", url);
		}
		
		if(url.contains("https://twitter.com/")) {
			String name="TWITter";
			
			for(SocialMedia link : socialMediaServ.getSocialMediaByPersonId(person.getPersonId())) {
				if(link.getName().contains(name)) {
					socialMediaServ.deleteSocialMedia(link);
					socialMediaServ.flushSocMediaDb();
					socialMediaServ.getSocialMediaByPersonId(person.getPersonId()).remove(link.getSocialMediaId());
					persServ.save(person);
					
				}
			}
			model.addAttribute("span", url);

		}
		
		if(url.contains("https://www.instagram.com/")) {
			
			String name="INSTAgram";
			
			for(SocialMedia link : socialMediaServ.getSocialMediaByPersonId(person.getPersonId())) {
				if(link.getName().contains(name)) {
					socialMediaServ.deleteSocialMedia(link);
					socialMediaServ.flushSocMediaDb();
					socialMediaServ.getSocialMediaByPersonId(person.getPersonId()).remove(link.getSocialMediaId());
					persServ.save(person);
					
				}
			}
			model.addAttribute("span", url);

		}

		
		List<SocialMedia> socList = socialMediaServ.getSocialMediaByPersonId(person.getPersonId());
		
		redirAttr.addFlashAttribute("socialMediaList", socList);
		
		return "redirect:/person/sprofile";
	}
	


}
