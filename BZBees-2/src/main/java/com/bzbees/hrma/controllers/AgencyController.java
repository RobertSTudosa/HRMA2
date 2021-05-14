package com.bzbees.hrma.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bzbees.hrma.entities.Agency;
import com.bzbees.hrma.entities.CompanyDoc;
import com.bzbees.hrma.entities.Doc;
import com.bzbees.hrma.entities.Job;
import com.bzbees.hrma.entities.Language;
import com.bzbees.hrma.entities.Message;
import com.bzbees.hrma.entities.Notification;
import com.bzbees.hrma.entities.Person;
import com.bzbees.hrma.entities.ProfileImg;
import com.bzbees.hrma.entities.Skill;
import com.bzbees.hrma.entities.SocialMedia;
import com.bzbees.hrma.entities.Tag;
import com.bzbees.hrma.entities.User;
import com.bzbees.hrma.entities.UserRole;
import com.bzbees.hrma.services.AgencyService;
import com.bzbees.hrma.services.CompanyDocService;
import com.bzbees.hrma.services.ImageResize;
import com.bzbees.hrma.services.JobService;
import com.bzbees.hrma.services.LikeService;
import com.bzbees.hrma.services.MessageService;
import com.bzbees.hrma.services.NotificationService;
import com.bzbees.hrma.services.PersonService;
import com.bzbees.hrma.services.ProfileImgService;
import com.bzbees.hrma.services.ProfileToPDF;
import com.bzbees.hrma.services.RoleService;
import com.bzbees.hrma.services.SocialMediaService;
import com.bzbees.hrma.services.TagService;
import com.bzbees.hrma.services.UserService;

@Controller
@RequestMapping("/agency")
@SessionAttributes({ "person", "userAccount", "agency", "lastAgencyPicList", "lastPicList", "companyDocList",
		"socialMediaList", "affiliatedPersonsList","agencyJobList","jobTagsList","lastJobPicList","userNotifs","userJobsLiked" })
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
	ImageResize imgResizerServ;

	@Autowired
	private RoleService roleServ;

	@Autowired
	private CompanyDocService companyDocServ;

	@Autowired
	private SocialMediaService socialMediaServ;
	
	@Autowired
	private JobService jobServ;
	
	@Autowired
	private TagService tagServ;
	
	@Autowired
	private NotificationService notifServ;
	
	@Autowired
	private MessageService messServ;
	
	@Autowired
	private LikeService likeServ;
	

	@GetMapping("/register")
	public String shorAgencyRegistrationForm(Model model, RedirectAttributes redirAttr, Authentication auth,
			HttpSession session) {

		if (auth.getName() == null) {
			return "home";
		}

		// get the user from user Principal
		User user = (User) userServ.loadUserByUsername(auth.getName());
		System.out.println("ANy users in here? " + "--->" + user.getUsername());

		// get the person from repo user query
		Person person = persServ.findPersonByUserId(user.getUserId());

		// add a new agency instance to the model
		Agency agency = new Agency();

		// get the last pic of the user by person id
		List<ProfileImg> picList = profileImgServ.getPicsByPersonId(person.getPersonId());
		List<ProfileImg> lastPicList = new ArrayList();
		int index = picList.size() - 1;
		if (index >= 0) {
			ProfileImg lastPic = picList.get(index);

			lastPicList.add(lastPic);

		}

		model.addAttribute("lastPicList", lastPicList);
		model.addAttribute("userAccount", user);
		model.addAttribute("person", person);
		model.addAttribute("agency", agency);
		session.setAttribute("agency", agency);

		return "agency/agencyRegister";
	}

	@PostMapping("/save")
	public String saveAgency(Model model, RedirectAttributes redirAttr, Authentication auth, Agency agency,
			Person person) {

		// check the user id before saving, if already there is an agency-user saved
		User user = (User) userServ.loadUserByUsername(auth.getName());

		UserRole role = new UserRole("AGENCY");
		if (!user.getRoles().contains(role)) {

			auth = SecurityContextHolder.getContext().getAuthentication();

			List<GrantedAuthority> updatedAuthorities = new ArrayList<>(auth.getAuthorities());

			// add your role here [e.g., new SimpleGrantedAuthority("ROLE_NEW_ROLE")]
			updatedAuthorities.add(new SimpleGrantedAuthority("AGENCY"));

			Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(),
					updatedAuthorities);

			SecurityContextHolder.getContext().setAuthentication(newAuth);

			user.addRole(role);
			roleServ.saveRole(role);

		}

		userServ.save(user);
		agency.setUser(user);
		agency.setAdminName(user.getUsername());
		agencyServ.saveAgency(agency);

		redirAttr.addAttribute("userAccount", user);
		redirAttr.addAttribute("agency", agency);
		model.addAttribute("agency", agency);

		return "redirect:/person/sprofile";
	}

	@GetMapping("/profile")
//	@Transactional
	public String showAgencyProfile(Model model, RedirectAttributes redirAttr, Authentication auth, 
			
			Person person) {

		if (auth != null) {
			


		// get the user from user Principal
		User user = (User) userServ.loadUserByUsername(auth.getName());
		model.addAttribute("userAccount", user);

		
		Agency agency = agencyServ.findAgencyByUserId(user.getUserId());
		
		// get the person from repo user query
		person = persServ.findPersonByUserId(user.getUserId());
		model.addAttribute("person", person);

//		Agency theAgency = agencyServ.findAgencyByID(agency.getAgencyId());
		model.addAttribute("agency", agency);

		// get the agency pics from the profileImg repo
		List<ProfileImg> agencyPics = profileImgServ.getPicsByAgencyId(agency.getAgencyId());

		// get the last pic of the agency's profile from profileImg repo
		ProfileImg theImg = profileImgServ.getLastAgencyPic(agency.getAgencyId());
		if (theImg != null) {
			model.addAttribute("img", theImg);

			List<ProfileImg> lastPicList = new ArrayList<>();
			lastPicList.add(profileImgServ.getLastAgencyPic(agency.getAgencyId()));
			model.addAttribute("lastAgencyPicList", lastPicList);

		} else {
			agencyPics.clear();
			System.out.println("ALL CLEAR");
			model.addAttribute("img", new ProfileImg());
			model.addAttribute("lastAgencyPicList", new ArrayList<>());
		}



		if (companyDocServ.getCompanyDocsByAgencyId(agency.getAgencyId()) == null) {
			model.addAttribute("companyDocList", new ArrayList<>());
			System.out.println("list is empty");
		} else {
			List<CompanyDoc> docList = companyDocServ.getCompanyDocsByAgencyId(agency.getAgencyId());
			model.addAttribute("companyDocList", docList);

			// insert below check validation code for different documents uploaded
		}

	
		
		if (socialMediaServ.getSocialMediaByAgencyId(agency.getAgencyId()).isEmpty()) {
			System.out.println("Social Media List is empty");
			model.addAttribute("socialMediaList", new ArrayList<>());
			
		} else {
			List<SocialMedia> socList = socialMediaServ.getSocialMediaByAgencyId(agency.getAgencyId());
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
		
		
		if(userServ.getAllAffiliatedUsersByAgencyId(agency.getAgencyId()) != null) {
			List<User> affiliatedUsersList = userServ.getAllAffiliatedUsersByAgencyId(agency.getAgencyId());
			List<Person> personsAffiliated = new ArrayList<>();
			Person affiliatedPerson = new Person ();
			for (User affUser : affiliatedUsersList) {
				affiliatedPerson = persServ.findPersonByUserId(affUser.getUserId());
				personsAffiliated.add(affiliatedPerson);
				if(profileImgServ.getLastProfilePic(affiliatedPerson.getPersonId()) != null) {
					ProfileImg lastImg = profileImgServ.getLastProfilePic(affiliatedPerson.getPersonId());
					affiliatedPerson.setLastImgId(lastImg.getPicId());
				} else {
					System.out.println("Id ul ultimei imagini este null");
				}
				
				//insert the job affiliated with this user crossed checked with the agency.
				
				
			}
			model.addAttribute("affiliatedPersonsList", personsAffiliated);
			
			System.out.println("Any affiliated users around here?");
		} else {
			System.out.println("No no users here");
			model.addAttribute("affiliatedPersonsList", new ArrayList<Person>());
		}
		
		
		
		//get the jobs of the agency in the profile
		if(jobServ.findJobsByAgencyId(agency.getAgencyId()) !=null) {
			List<Job> agencyJobs = jobServ.findJobsByAgencyId(agency.getAgencyId());
			model.addAttribute("agencyJobList", agencyJobs);
			List<Tag> agencyJobsTags = new ArrayList<>();
			Set<Person> allApplicantsApproved = new HashSet<Person>();
			Set<Long> allCandidatesIdsWithValidDate = new HashSet<Long>();
			for(Job job : agencyJobs) {
				List<Tag> jobsTags = tagServ.findTagsByJobId(job.getJobId());
				agencyJobsTags.addAll(jobsTags);
	
				Set<Person> applicantsApproved = persServ.getCandidatesApprovedToJob(job.getJobId());
				allApplicantsApproved.addAll(applicantsApproved);

				Set<Long> applicantsWithValidDate = persServ.getCandidatesIdsWithValidDatesByJobId(job.getJobId());

				allCandidatesIdsWithValidDate.addAll(applicantsWithValidDate);

			}
			
			
			model.addAttribute("allApplicantsApproved", allApplicantsApproved);

			if(allCandidatesIdsWithValidDate != null  ) {
				model.addAttribute("allCandidatesIdsWithValidDate", allCandidatesIdsWithValidDate);
			} 
			
			Set<Long> userJobsIdLiked = likeServ.findLikedJobsIdsByUsername(auth.getName());
			Set<Long> userJobsIdInList = jobServ.findJobsIdAddedToListByPersonId(user.getUserId());
			Set<Long> userJobsAppliedTo = jobServ.findJobsIdAppliedToByPersonId(user.getUserId());
			model.addAttribute("userJobsIdApplied", userJobsAppliedTo);
			model.addAttribute("userJobsInList", userJobsIdInList);
			model.addAttribute("userJobsLiked", userJobsIdLiked);
			model.addAttribute("jobTagsList", agencyJobsTags);
			
		} else {
			model.addAttribute("userJobsIdApplied", new HashSet<Long>());
			model.addAttribute("userJobsLiked", new HashSet<Long>());
			model.addAttribute("userJobsInList", new HashSet<Long>());
			model.addAttribute("agencyJobList", new ArrayList<>());
			model.addAttribute("jobTagsList", new ArrayList<>());
			
		}
		
		if(!model.containsAttribute("job")) {
			model.addAttribute("job", new Job());
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

			model.addAttribute("userNotifs", showUserNotifs);
			
		} else {
			model.addAttribute("userNotifs", new ArrayList<>());
		}
		
		
			
			//implement a db query to get all pending users for the agency via agency id
			if(userServ.getAllPendingUsersByAgencyId(agency.getAgencyId()) != null) {						
			List<User> pendingUsers = userServ.getAllPendingUsersByAgencyId(agency.getAgencyId());
				for(User pendingUser : pendingUsers) {
					if(pendingUser.getUserId() == user.getUserId()) {
						model.addAttribute("match", true);
						model.addAttribute("agencyMessage", "You're affiliation with this agency is in pending. Please wait until the agency will approve your request.");
					} else {
						model.addAttribute("match", false);
						
					}
				}
			
			} else {
				model.addAttribute("match", false);
				
			}

		return "agency/agency_profile";
		
		}
		
		return "home";
	}
	
	
	

	// must implement another form of deleting/detaching an agency from the person
	// keep in place to resolve the profile menu null's profile
	@Transactional
	@GetMapping("/deleteAgency")
	public String deleteAgency(@RequestParam("id") long id, Model model, RedirectAttributes redirAttr, Person person,
			Authentication auth, Agency agency) {

		agencyServ.deleteAgencyById(id);

		User user = (User) userServ.loadUserByUsername(auth.getName());

		if (!user.getRoles().contains("AGENCY")) {

			auth = SecurityContextHolder.getContext().getAuthentication();

			List<GrantedAuthority> updatedAuthorities = new ArrayList<>(auth.getAuthorities());

			updatedAuthorities.remove(new SimpleGrantedAuthority("AGENCY"));

			Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(),
					updatedAuthorities);

			SecurityContextHolder.getContext().setAuthentication(newAuth);

			UserRole role = new UserRole("AGENCY");
			user.removeRole(role);

		}

		userServ.save(user);

		redirAttr.addAttribute("userAccount", user);

		return "redirect:/person/sprofile";
	}

	@PostMapping(value = "/addAgencyImg", consumes = { "multipart/form-data" })
	public String addProfileImg(Model model, Person person, Agency agency, @RequestParam("img") MultipartFile img,
			RedirectAttributes redirAttr) {

		// test if agency is present in the method
		System.out.println("Agency is " + agency.getAgencyName());

		String imgName = StringUtils.cleanPath(img.getOriginalFilename());

		// init a new byte array
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// the bytes you want for writing your class image
		byte[] newBytes = null;

		baos = imgResizerServ.resizeImage(img);

		newBytes = baos.toByteArray();

		try {
			baos.close();
		} catch (IOException e) {
			System.out.println("ByteArrayOutputSteam is not closed ");
			e.printStackTrace();
		}


		ProfileImg smallImg = new ProfileImg(imgName, img.getContentType(), newBytes);
		
		profileImgServ.savePic(smallImg);

		
		
		List<ProfileImg> picList = profileImgServ.getPicsByAgencyId(agency.getAgencyId());
		picList.add(smallImg);
		
		ProfileImg lastPic = profileImgServ.getLastAgencyPic(agency.getAgencyId());
		
		List<ProfileImg> lastPicList = new ArrayList<>();
		lastPicList.add(lastPic);
		agency.setLastAgencyImageId(smallImg.getPicId());
		agency.setPics(picList);
		
		agencyServ.saveAgency(agency);
		
		
		
		model.addAttribute("lastAgencyPicList", lastPicList);
		redirAttr.addFlashAttribute("lastAgencyPicList", lastPicList);
		redirAttr.addFlashAttribute("picList", picList);
		redirAttr.addFlashAttribute("img", smallImg);

		return "redirect:/agency/profile";
	}

	@GetMapping(value = "/img")
	public ResponseEntity<?> showAgencyImg(@RequestParam("imgId") long id, 
									@RequestParam("agencyId") long agencyId, Model model ) {
		
		Agency agency = agencyServ.findAgencyByID(agencyId);
		
		if (profileImgServ.getLastAgencyPic(agency.getAgencyId()) == null) {
			System.out.println("Agency is not present when no one is logged in... OBVIOUSLY");
			model.addAttribute("lastAgencyPicList", null);
			return null;
		}

		ProfileImg agencyPic = profileImgServ.getLastAgencyPic(agency.getAgencyId());
		System.out.println("did I get the last one or not?");

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(agencyPic.getPicType()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename =\"" + agencyPic.getPicName() + "\"")
				.body(new ByteArrayResource(agencyPic.getData()));

	}
	
	@GetMapping(value="/deleteSocialMedia")
	public String deleteSocialMediaAgencyProfile (@RequestParam("url") String url, Model model, Agency agency, 
			Person person, RedirectAttributes redirAttr) {
		
		if(url.contains("https://www.facebook.com/")) {
			
			String name = "FACEBook";
			System.out.println("Hey Zucke? Are you here? ");
			
			for(SocialMedia link : socialMediaServ.getSocialMediaByAgencyId(agency.getAgencyId())) {
				if(link.getName().contains(name)) {
					socialMediaServ.deleteSocialMedia(link);
					socialMediaServ.flushSocMediaDb();
					socialMediaServ.getSocialMediaByAgencyId(agency.getAgencyId()).remove(link.getSocialMediaId());
					agencyServ.saveAgency(agency);
					
				}
			}
			
			
			redirAttr.addFlashAttribute("span", url);
			
			
		}
		
		if(url.contains("https://www.linkedin.com/")) {
			String name="LINKedin";
			
			for(SocialMedia link : socialMediaServ.getSocialMediaByAgencyId(agency.getAgencyId())) {
				if(link.getName().contains(name)) {
					socialMediaServ.deleteSocialMedia(link);
					socialMediaServ.flushSocMediaDb();
					socialMediaServ.getSocialMediaByAgencyId(agency.getAgencyId()).remove(link.getSocialMediaId());
					agencyServ.saveAgency(agency);
					
				}
			}
			
			
			model.addAttribute("span", url);
		}
		
		if(url.contains("https://www.linkedin.com/")) {
			String name="LINKedin";
			
			for(SocialMedia link : socialMediaServ.getSocialMediaByAgencyId(agency.getAgencyId())) {
				if(link.getName().contains(name)) {
					socialMediaServ.deleteSocialMedia(link);
					socialMediaServ.flushSocMediaDb();
					socialMediaServ.getSocialMediaByAgencyId(agency.getAgencyId()).remove(link.getSocialMediaId());
					agencyServ.saveAgency(agency);
					
				}
			}
			

			
			model.addAttribute("span", url);
		}
		
		if(url.contains("https://twitter.com/")) {
			String name="TWITter";
			
			for(SocialMedia link : socialMediaServ.getSocialMediaByAgencyId(agency.getAgencyId())) {
				if(link.getName().contains(name)) {
					socialMediaServ.deleteSocialMedia(link);
					socialMediaServ.flushSocMediaDb();
					socialMediaServ.getSocialMediaByAgencyId(agency.getAgencyId()).remove(link.getSocialMediaId());
					agencyServ.saveAgency(agency);
					
				}
			}
			model.addAttribute("span", url);

		}
		
		if(url.contains("https://www.instagram.com/")) {
			
			String name="INSTAgram";
			
			for(SocialMedia link : socialMediaServ.getSocialMediaByAgencyId(agency.getAgencyId())) {
				if(link.getName().contains(name)) {
					socialMediaServ.deleteSocialMedia(link);
					socialMediaServ.flushSocMediaDb();
					socialMediaServ.getSocialMediaByAgencyId(agency.getAgencyId()).remove(link.getSocialMediaId());
					agencyServ.saveAgency(agency);
					
				}
			}
			model.addAttribute("span", url);

		}

		
		List<SocialMedia> socList = socialMediaServ.getSocialMediaByAgencyId(agency.getAgencyId());
		
		redirAttr.addFlashAttribute("socialMediaList", socList);
		
		return "redirect:/agency/profile";
	}

	@PostMapping(value = "/addSocialMediaFB")
	public String addSocialMediabyAgencyProfile(Model model, Agency agency, Person person,
			@RequestParam("url") String url, RedirectAttributes redirAttr) {
		
		if(url.contains("https://www.facebook.com/")) {
			
			String name = "FACEBook";
			
			for(SocialMedia link : socialMediaServ.getSocialMediaByAgencyId(agency.getAgencyId())) {
				if(link.getName().contains(name)) {
					socialMediaServ.deleteSocialMedia(link);
					socialMediaServ.flushSocMediaDb();
					socialMediaServ.getSocialMediaByAgencyId(agency.getAgencyId()).remove(link.getSocialMediaId());
					agencyServ.saveAgency(agency);
					
				}
			}
			
			SocialMedia FBSocMedia = new SocialMedia(name,url,agency,null);
			
			socialMediaServ.saveAndFlush(FBSocMedia);
			
//			model.addAttribute("span", url);
			redirAttr.addFlashAttribute("span", url);
			
			
		}
		
		if(url.contains("https://www.linkedin.com/")) {
			String name="LINKedin";
			
			for(SocialMedia link : socialMediaServ.getSocialMediaByAgencyId(agency.getAgencyId())) {
				if(link.getName().contains(name)) {
					socialMediaServ.deleteSocialMedia(link);
					socialMediaServ.flushSocMediaDb();
					socialMediaServ.getSocialMediaByAgencyId(agency.getAgencyId()).remove(link.getSocialMediaId());
					agencyServ.saveAgency(agency);
					
				}
			}
			
			SocialMedia LNSocMedia = new SocialMedia(name, url, agency, null);
			
			socialMediaServ.saveAndFlush(LNSocMedia);
			
			model.addAttribute("span", url);
		}
		
		if(url.contains("https://twitter.com/")) {
			String name="TWITter";
			
			for(SocialMedia link : socialMediaServ.getSocialMediaByAgencyId(agency.getAgencyId())) {
				if(link.getName().contains(name)) {
					socialMediaServ.deleteSocialMedia(link);
					socialMediaServ.flushSocMediaDb();
					socialMediaServ.getSocialMediaByAgencyId(agency.getAgencyId()).remove(link.getSocialMediaId());
					agencyServ.saveAgency(agency);
					
				}
			}
			
			SocialMedia TWSocMedia = new SocialMedia(name, url, agency, null);
			
			socialMediaServ.saveAndFlush(TWSocMedia);
		}
		
		if(url.contains("https://www.instagram.com/")) {
			
			String name="INSTAgram";
			
			for(SocialMedia link : socialMediaServ.getSocialMediaByAgencyId(agency.getAgencyId())) {
				if(link.getName().contains(name)) {
					socialMediaServ.deleteSocialMedia(link);
					socialMediaServ.flushSocMediaDb();
					socialMediaServ.getSocialMediaByAgencyId(agency.getAgencyId()).remove(link.getSocialMediaId());
					agencyServ.saveAgency(agency);
					
				}
			}
			
			SocialMedia INSocMedia = new SocialMedia(name, url, agency, null);
			
			socialMediaServ.saveAndFlush(INSocMedia);
		}
		
		
		List<SocialMedia> socList = socialMediaServ.getSocialMediaByAgencyId(agency.getAgencyId());
			
		redirAttr.addFlashAttribute("socialMediaList", socList);

		return "redirect:/agency/profile";
	}

	@PostMapping(value = "/addRegCertModal", consumes = { "multipart/form-data" })
	public String addDocsByProfile(Model model, Agency agency, Person person,
			@RequestParam("doc") MultipartFile[] files, RedirectAttributes redirAttr) {

		// getting all the docs of the agency --> HIBERNATE select
		List<CompanyDoc> docList = companyDocServ.getCompanyDocsByAgencyId(agency.getAgencyId());

		// going through the list of docs of the agency
		for (CompanyDoc doc : docList) {
			String reg_certCheck = doc.getDocName();

			// finding the reg_cert doc
			if (reg_certCheck.contains("reg_cert")) {

				// delete the doc that has reg_cert from the agency's list of docs
				// HIBERNATE delete statement
				companyDocServ.deleteCompDoc(doc);

				// don't forget to flush:) after deleting
				companyDocServ.flushCompDocDb();

				// remove from list the deleted document ???
				docList.remove(doc.getCompDocId());

				// save the agency so it can save all children accordingly
				agencyServ.saveAgency(agency);
			}
		}

//			docList = new ArrayList<>();

		for (MultipartFile file : files) {

			String docName = agency.getAgencyName() + " " + "reg_cert "
					+ StringUtils.cleanPath(file.getOriginalFilename());

			try {

				if (docName.contains("..")) {
					throw new RuntimeException(
							"Cannot store file with relative path outside current directory " + docName);
				}

				CompanyDoc theDoc = new CompanyDoc(docName, file.getContentType(), file.getBytes());

				// check is the document exists in the agency's list of documents
				// HIBERNATE 2nd select statement (same as 1st)
				if (companyDocServ.getCompanyDocsByAgencyId(agency.getAgencyId()).contains(theDoc)) {
					System.out.println("Document " + theDoc.getDocName() + " is already saved");
				} else {

					System.out.println(theDoc.getDocName() + " is to be saved");

					// HIBERNATE next val for sequence and insert into the table
					companyDocServ.saveAndFlush(theDoc);
					companyDocServ.flushCompDocDb();
					docList.add(theDoc);
					theDoc.setAgency(agency);
					agencyServ.saveAgency(agency);

				}

			} catch (IOException e) {
				e.printStackTrace();

			}
		}

		agency.setCompanyDocs(docList);

		redirAttr.addFlashAttribute("companyDocList", companyDocServ.getCompanyDocsByAgencyId(agency.getAgencyId()));

		return "redirect:/agency/profile";
	}

	@PostMapping(value = "/addCompDetailsModal", consumes = { "multipart/form-data" })
	public String addCompDetailsDocsByProfile(Model model, Agency agency, Person person,
			@RequestParam("doc") MultipartFile[] files, RedirectAttributes redirAttr) {

		// getting all the docs of the agency --> HIBERNATE select
		List<CompanyDoc> docList = companyDocServ.getCompanyDocsByAgencyId(agency.getAgencyId());

		// going through the list of docs of the agency
		for (CompanyDoc doc : docList) {
			String reg_certCheck = doc.getDocName();

			// finding the reg_cert doc
			if (reg_certCheck.contains("comp_details")) {

				// delete the doc that has reg_cert from the agency's list of docs
				// HIBERNATE delete statement
				companyDocServ.deleteCompDoc(doc);

				// don't forget to flush:) after deleting
				companyDocServ.flushCompDocDb();

				// remove from list the deleted document ???
				docList.remove(doc.getCompDocId());

				// save the agency so it can save all children accordingly
				agencyServ.saveAgency(agency);
			}
		}

//			docList = new ArrayList<>();

		for (MultipartFile file : files) {

			String docName = agency.getAgencyName() + " " + "comp_details "
					+ StringUtils.cleanPath(file.getOriginalFilename());

			try {

				if (docName.contains("..")) {
					throw new RuntimeException(
							"Cannot store file with relative path outside current directory " + docName);
				}

				CompanyDoc theDoc = new CompanyDoc(docName, file.getContentType(), file.getBytes());

				// check is the document exists in the agency's list of documents
				// HIBERNATE 2nd select statement (same as 1st)
				if (companyDocServ.getCompanyDocsByAgencyId(agency.getAgencyId()).contains(theDoc)) {
					System.out.println("Document " + theDoc.getDocName() + " is already saved");
				} else {

					System.out.println(theDoc.getDocName() + " is to be saved");

					// HIBERNATE next val for sequence and insert into the table
					companyDocServ.saveAndFlush(theDoc);
					companyDocServ.flushCompDocDb();
					docList.add(theDoc);
					theDoc.setAgency(agency);
					agencyServ.saveAgency(agency);

				}

			} catch (IOException e) {
				e.printStackTrace();

			}
		}

		agency.setCompanyDocs(docList);

		redirAttr.addFlashAttribute("companyDocList", companyDocServ.getCompanyDocsByAgencyId(agency.getAgencyId()));

		return "redirect:/agency/profile";
	}

	@PostMapping(value = "/addAdminIdModal", consumes = { "multipart/form-data" })
	public String addAdminIdDocByAgencyProfile(Model model, Agency agency, Person person,
			@RequestParam("doc") MultipartFile[] files, RedirectAttributes redirAttr) {

		// getting all the docs of the agency --> HIBERNATE select
		List<CompanyDoc> docList = companyDocServ.getCompanyDocsByAgencyId(agency.getAgencyId());

		// going through the list of docs of the agency
		for (CompanyDoc doc : docList) {
			String reg_certCheck = doc.getDocName();

			// finding the reg_cert doc
			if (reg_certCheck.contains("admin_Id")) {

				// delete the doc that has reg_cert from the agency's list of docs
				// HIBERNATE delete statement
				companyDocServ.deleteCompDoc(doc);

				// don't forget to flush:) after deleting
				companyDocServ.flushCompDocDb();

				// remove from list the deleted document ???
				docList.remove(doc.getCompDocId());

				// save the agency so it can save all children accordingly
				agencyServ.saveAgency(agency);
			}
		}

//			docList = new ArrayList<>();

		for (MultipartFile file : files) {

			String docName = agency.getAgencyName() + " " + agency.getAdminName() + " admin_Id "
					+ StringUtils.cleanPath(file.getOriginalFilename());

			try {

				if (docName.contains("..")) {
					throw new RuntimeException(
							"Cannot store file with relative path outside current directory " + docName);
				}

				CompanyDoc theDoc = new CompanyDoc(docName, file.getContentType(), file.getBytes());

				// check is the document exists in the agency's list of documents
				// HIBERNATE 2nd select statement (same as 1st)
				if (companyDocServ.getCompanyDocsByAgencyId(agency.getAgencyId()).contains(theDoc)) {
					System.out.println("Document " + theDoc.getDocName() + " is already saved");
				} else {

					System.out.println(theDoc.getDocName() + " is to be saved");

					// HIBERNATE next val for sequence and insert into the table
					companyDocServ.saveAndFlush(theDoc);
					companyDocServ.flushCompDocDb();
					docList.add(theDoc);
					theDoc.setAgency(agency);
					agencyServ.saveAgency(agency);

				}

			} catch (IOException e) {
				e.printStackTrace();

			}
		}

		agency.setCompanyDocs(docList);

		redirAttr.addFlashAttribute("companyDocList", companyDocServ.getCompanyDocsByAgencyId(agency.getAgencyId()));

		return "redirect:/agency/profile";
	}

	@PostMapping(value = "/addOtherDocsModal", consumes = { "multipart/form-data" })
	public String addOtherCompanyDocsByAgencyProfile(Model model, Agency agency, Person person,
			@RequestParam("doc") MultipartFile[] files, RedirectAttributes redirAttr) {

		// getting all the docs of the agency --> HIBERNATE select
		List<CompanyDoc> docList = companyDocServ.getCompanyDocsByAgencyId(agency.getAgencyId());

		for (MultipartFile file : files) {

			String docName = agency.getAgencyName() + " " + agency.getAdminName() + " otherDoc "
					+ StringUtils.cleanPath(file.getOriginalFilename());

			try {

				if (docName.contains("..")) {
					throw new RuntimeException(
							"Cannot store file with relative path outside current directory " + docName);
				}

				CompanyDoc theDoc = new CompanyDoc(docName, file.getContentType(), file.getBytes());

				// check is the document exists in the agency's list of documents
				// HIBERNATE 2nd select statement (same as 1st)
				if (companyDocServ.getCompanyDocsByAgencyId(agency.getAgencyId()).contains(theDoc)) {
					System.out.println("Document " + theDoc.getDocName() + " is already saved");
				} else {

					System.out.println(theDoc.getDocName() + " is to be saved");

					// HIBERNATE next val for sequence and insert into the table
					companyDocServ.saveAndFlush(theDoc);
					companyDocServ.flushCompDocDb();
					docList.add(theDoc);
					theDoc.setAgency(agency);
					agencyServ.saveAgency(agency);

				}

			} catch (IOException e) {
				e.printStackTrace();

			}
		}

		agency.setCompanyDocs(docList);

		redirAttr.addFlashAttribute("companyDocList", companyDocServ.getCompanyDocsByAgencyId(agency.getAgencyId()));

		return "redirect:/agency/profile";
	}

	@GetMapping("/deleteAgencyDoc")
	public String deleteProfileDocById(@RequestParam("id") long id, Model model, Agency agency, Person person,
			RedirectAttributes redirAttr) {

		System.out.println("agency present is " + agency.getAgencyName());
		List<CompanyDoc> docList = companyDocServ.getCompanyDocsByAgencyId(agency.getAgencyId());

		for (CompanyDoc doc : docList) {
			if (doc.getCompDocId() == id) {
				System.out.println("agency doc name is " + doc.getDocName());
				companyDocServ.deleteCompDoc(doc);
			}

		}

//			persServ.save(person);
		companyDocServ.flushCompDocDb();
		List<CompanyDoc> reSavedDocList = companyDocServ.getCompanyDocsByAgencyId(agency.getAgencyId());

		redirAttr.addFlashAttribute("companyDocList", reSavedDocList);

		return "redirect:/agency/profile";
	}

	@GetMapping("/downloadFile")
	public ResponseEntity<?> downloadDoc(@RequestParam("docId") long docId) {
		CompanyDoc dbDoc = companyDocServ.findCompDocById(docId);

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(dbDoc.getDocType()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename =\"" + dbDoc.getDocName() + "\"")
				.body(new ByteArrayResource(dbDoc.getData()));
	}

	@GetMapping("/displayFile")
	public ResponseEntity<?> displayDoc(@RequestParam("docId") long docId) {
		CompanyDoc dbDoc = companyDocServ.findCompDocById(docId);

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(dbDoc.getDocType()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename =\"" + dbDoc.getDocName() + "\"")
				.body(new ByteArrayResource(dbDoc.getData()));
	}
	
	
	@GetMapping(value = "/candidateImg")
	public ResponseEntity<?> showCandidateProfileImg(@RequestParam("imgId") long id, 
													@RequestParam("personId") long personId) {
		Person person = persServ.findPersonById(personId);
//		ProfileImg profilePic = profileImgServ.findProfilePicById(id);
		if(profileImgServ.getLastProfilePic(person.getPersonId()) == null) {
			return null;
		}
		ProfileImg profilePic = profileImgServ.getLastProfilePic(person.getPersonId());
		
		System.out.println("profile pic that is not displaying is " + profilePic.getPicName());

		return ResponseEntity.ok()
				.contentType(MediaType
						.parseMediaType(profilePic.getPicType()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename =\"" + profilePic.getPicName() + "\"")
				.body(new ByteArrayResource(profilePic.getData()));

	}
	

	@GetMapping(value="/cprofile")
	public String showCandidateProfile (@RequestParam("id") long id, Model model, Authentication auth) {
		
		Person person = persServ.findPersonById(id);
		User userAccount = userServ.findUserByPersonId(id);
		model.addAttribute("person", person);
		model.addAttribute("userAccount", userAccount);


		if (!model.containsAttribute("picList")) {
			List<ProfileImg> picList = person.getPics();
			model.addAttribute("picList", picList);
		}
		
		//get the pics from the profileImg repo
		List<ProfileImg> personPics = profileImgServ.getPicsByPersonId(id);

		//get the last pic of the person's profile from profileImg repo
		ProfileImg theImg = profileImgServ.getLastProfilePic(id);
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
		if(auth != null) {
			User user = (User) userServ.loadUserByUsername(auth.getName());
			Person personLoggedIn = (Person) persServ.findPersonByUserId(user.getUserId());
			if(!notifServ.findNotificationsByUserId(user.getUserId()).isEmpty()) {
				List<Notification> allUserNotif = notifServ.reverseFindNotificationsByUserId(user.getUserId());
				List<Notification> showUserNotifs = new ArrayList<>();
				int count = allUserNotif.size();
				for(int i = 0; i < 4; i++) {
					if(count == 0) {
						break;
					}
					showUserNotifs.add(allUserNotif.get(i));
					count=count -1;
				}

				model.addAttribute("userNotifs", showUserNotifs);
			} else {
				model.addAttribute("userNotifs", new ArrayList<>());
			}
			
			Set<Long> userJobsIdLiked = likeServ.findLikedJobsIdsByUsername(user.getUsername());
			model.addAttribute("userJobsLiked", userJobsIdLiked);
			
			Set<Long> userJobsIdInList = jobServ.findJobsIdAddedToListByPersonId(personLoggedIn.getPersonId());
			model.addAttribute("userJobsInList", userJobsIdInList);
			
			Set <Job> userJobsInList = jobServ.findJobsAddedToListByPersonId(person.getPersonId());
			model.addAttribute("userJobsSaved", userJobsInList);
			
			Set<Long> userJobsAppliedTo = jobServ.findJobsIdAppliedToByPersonId(user.getUserId());
			model.addAttribute("userJobsIdApplied", userJobsAppliedTo);
			
			Set<Job> userJobsApplied = jobServ.findJobsAppliedByPersonId(person.getPersonId());
			model.addAttribute("userJobsApplied", userJobsApplied);
		
		}
		
		
		return "user/session_profile";
	}
	
	@PostMapping("/addModalJob")
	public String addJobFromAgencyProfile(Model model, Agency agency, @RequestParam("img") MultipartFile img,
			Job job, RedirectAttributes redirAttr) {

		
		List<Job> savedJobs = jobServ.findJobsByAgencyId(agency.getAgencyId());
		
		job.setCompanyName(agency.getAgencyName());

		job.setTheAgency(agency);
		
//		jobServ.saveAndFlush(job);

		savedJobs.add(job);

		agency.setPostedJobs(savedJobs);
		
		//check if the img is not null 
		if(!img.isEmpty()) {
			//save the picture to the saved job
			String imgName = StringUtils.cleanPath(img.getOriginalFilename());

			// init a new byte array
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			// the bytes you want for writing your class image
			byte[] newBytes = null;

			baos = imgResizerServ.resizeImage(img);

			newBytes = baos.toByteArray();

			try {
				baos.close();
			} catch (IOException e) {
				System.out.println("ByteArrayOutputSteam is not closed ");
				e.printStackTrace();
			}


			ProfileImg smallImg = new ProfileImg(imgName, img.getContentType(), newBytes);
			
			profileImgServ.savePic(smallImg);
		
			List<ProfileImg> picList = profileImgServ.getPicsByJobId(job.getJobId());
			picList.add(smallImg);
			
			ProfileImg lastPic = profileImgServ.getLastJobPic(job.getJobId());
			
			List<ProfileImg> lastPicList = new ArrayList<>();
			lastPicList.add(lastPic);
			job.setLastImageId(smallImg.getPicId());
			job.setPics(picList);
			
			model.addAttribute("lastJobPicList", lastPicList);
			redirAttr.addFlashAttribute("lastJobPicList", lastPicList);
			redirAttr.addFlashAttribute("picList", picList);
			redirAttr.addFlashAttribute("jobimg", smallImg);

			//ending the if for checking the image param if it's null
		}
		
		

		jobServ.save(job);

		
		List<Tag> jobTags = job.getJobTags();
		
		String [] trimmedTags = job.getTags().trim().split("\\s+");
		for(String s : trimmedTags) {
			Tag tag = new Tag(s);
			tag.setTheJob(job);
			jobTags.add(tag);
			tagServ.tagSave(tag);
		}

		agencyServ.saveAgency(agency);
		



		redirAttr.addFlashAttribute("agencyJobList", savedJobs);
		redirAttr.addFlashAttribute("jobTagsList", jobTags);

		return "redirect:/agency/profile";
	}
	
	@GetMapping("/deleteJobByModal")
	public String deleteJobModalById(@RequestParam("id") long id, Model model, Agency agency,
			RedirectAttributes redirAttr) {

		List<Job> agencyJobs = jobServ.findJobsByAgencyId(agency.getAgencyId());
		
		for (Job job : agencyJobs) {
			if(job.getJobId() == id) {
				List<Tag> jobTags = tagServ.findTagsByJobId(job.getJobId());
				
				try {
					tagServ.deleteJobTags(jobTags);
					jobServ.deleteJobById(job);					
				} catch(EmptyResultDataAccessException e) {
					e.printStackTrace();
				}

			}
		}
			
		redirAttr.addFlashAttribute("agencyJobList", jobServ.findJobsByAgencyId(agency.getAgencyId()));

		return "redirect:/agency/profile";
				
	}
	
	@GetMapping(value="/makeJobPrivate")
	public String makePrivate(@Valid @ModelAttribute("job") Job setJob, 
			BindingResult bindingResult, @RequestParam("id") long id, 
			Model model, Person person, 
			Agency agency, RedirectAttributes redirAttr ) throws ResourceNotFoundException {
		
		Job job = jobServ.findJobById(id);
		
		if(job.isJobPrivate()) {
			job.setJobPrivate(false);
			jobServ.save(job);	
			
		} else {
			job.setJobPrivate(true);
			jobServ.save(job);	
		}
		
			
		

		List<Job> agencySavedJobs = jobServ.findJobsByAgencyId(agency.getAgencyId());
		redirAttr.addFlashAttribute("agencyJobList", agencySavedJobs);
		
		return "redirect:/agency/profile";
	}
	//MediaType.APPLICATION_FORM_URLENCODED_VALUE, { "multipart/form-data"}
	@PostMapping(value="/editJob" , consumes = {MediaType.ALL_VALUE,  "multipart/form-data" })
	public String partialUpdate(@Valid @ModelAttribute("job") Job patchJob, 
//			@ModelAttribute("jobTagList") List<Tag> patchTags,
			BindingResult bindingResult, @RequestParam("id") long id, 
			@RequestParam("imgEdit") MultipartFile img,
			Model model, Person person, 
			Agency agency, RedirectAttributes redirAttr ) throws ResourceNotFoundException {
		

		
		Job job = jobServ.findJobById(id);
		
		System.out.println("patch job is " + patchJob.getJobTitle());

		job.setJobPrivate(patchJob.isJobPrivate());
		job.setJobTitle(patchJob.getJobTitle());
		job.setStartDate(patchJob.getStartDate());
		job.setSalary(patchJob.getSalary());
		job.setJobLocation(patchJob.getJobLocation());
		job.setNecessaryDocuments(patchJob.getNecessaryDocuments());
		job.setWorkingConditions(patchJob.getWorkingConditions());
		job.setResponsabilities(patchJob.getResponsabilities());
		
		//check if the img is there or not
		if(!img.isEmpty()) {
			
		
		
		job.setLastImageId(patchJob.getLastImageId());
		
		
	
		//save the picture to the saved job
				String imgName = StringUtils.cleanPath(img.getOriginalFilename());

				// init a new byte array
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				// the bytes you want for writing your class image
				byte[] newBytes = null;

				baos = imgResizerServ.resizeImage(img);

				newBytes = baos.toByteArray();

				try {
					baos.close();
				} catch (IOException e) {
					System.out.println("ByteArrayOutputSteam is not closed ");
					e.printStackTrace();
				}


				ProfileImg smallImg = new ProfileImg(imgName, img.getContentType(), newBytes);
				
				profileImgServ.savePic(smallImg);
			
				List<ProfileImg> picList = profileImgServ.getPicsByJobId(job.getJobId());
				picList.add(smallImg);
				
				ProfileImg lastPic = profileImgServ.getLastJobPic(job.getJobId());
				
				List<ProfileImg> lastPicList = new ArrayList<>();
				lastPicList.add(lastPic);
				job.setLastImageId(smallImg.getPicId());
				job.setPics(picList);
		
				model.addAttribute("lastJobPicList", lastPicList);
				redirAttr.addFlashAttribute("lastJobPicList", lastPicList);
				redirAttr.addFlashAttribute("picList", picList);
				redirAttr.addFlashAttribute("jobimg", smallImg);
				
		}
				
		job.setTags(patchJob.getTags());
		
		
		List<Tag> jobTags = tagServ.findTagsByJobId(id);
		tagServ.deleteJobTags(jobTags);
		
		

		String [] trimmedTags = patchJob.getTags().trim().split("\\s+");

		for(String s : trimmedTags) {
			Tag tag = new Tag(s);
			tag.setTheJob(job);
			jobTags.add(tag);
			tagServ.tagSave(tag);
		}

		jobServ.save(job);		
		
		
		List<Job> agencySavedJobs = jobServ.findJobsByAgencyId(agency.getAgencyId());
		
		
		
		redirAttr.addFlashAttribute("agencyJobList", agencySavedJobs);
		
		return "redirect:/agency/profile";
	}
	
	@GetMapping(value = "/jobimg")
	public ResponseEntity<?> showJobImg(@RequestParam("imgId") long id, 
									@RequestParam("jobId") long jobId, Model model ) {
		
		System.out.println("The job id from the view is " + jobId);
		Job job = jobServ.findJobById(jobId);

		if (profileImgServ.getLastJobPic(job.getJobId()) == null) {
			System.out.println("Job is not present when no one is logged in... OBVIOUSLY");
			model.addAttribute("lastJobPicList", null);
			return null;
		}

		ProfileImg jobPic = profileImgServ.getLastJobPic(job.getJobId());
		System.out.println("did I get the last job pic one or not?");

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(jobPic.getPicType()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename =\"" + jobPic.getPicName() + "\"")
				.body(new ByteArrayResource(jobPic.getData()));

	}
	
	
	@GetMapping(value="/affiliate", produces = MediaType.TEXT_HTML_VALUE)
	public String affiliateUser(@RequestParam("id") long agencyId, Model model, Agency theAgency, Authentication auth) {
		
		if(auth == null) {
			System.out.println("No user is logged in. Testing the affiliate button");
			return "home";
		}
		
		//get the user that is logged in
		String name = auth.getName();
		
		User loggedInUser = (User) userServ.loadUserByUsername(name);
		Person userPerson = (Person) persServ.findPersonByUserId(loggedInUser.getUserId());
		
		Agency agency = agencyServ.findAgencyByID(agencyId);
		//get the user that is an admin for the agency
		User agencyAdmin = (User) userServ.loadUserByUsername(agency.getAdminName());//Robert
		Person agencyPerson = persServ.findPersonByUserId(agencyAdmin.getUserId());
		
		
		//!!!!!!!check is the candidate is not already approved. 
		Person loggedInPerson = persServ.findPersonByUserId(loggedInUser.getUserId());
	
		//!!!!!if approved then 
			//hasApprove = false 
			//send message to model
			//return to profile
		
		
		//get the id of the logged in user and create messages
		//for the admin user of the agency
		long candidateId = loggedInUser.getUserId();
		Message shortHref = new Message("/agency/cprofile?id=" + Long.toString(candidateId));
		Message messageText = new Message("Candidate");
		Message longText = new Message("is requesting to affiliate with " + agency.getAgencyName() + ".");
		Message nameText = new Message(loggedInUser.getUsername());
		
		//add messages to a list so it can be passed to a
		// notification object
		List<Message> candidateNotifs = new ArrayList<>();
		candidateNotifs.add(0, messageText);
		candidateNotifs.add(1, nameText);
		candidateNotifs.add(2, shortHref);
		candidateNotifs.add(3, longText);

		Notification adminNotif = new Notification(candidateNotifs,false, true, messageText.getMessage(), new Date());

		notifServ.saveNotif(adminNotif);
		messageText.setNotification(adminNotif);
		nameText.setNotification(adminNotif);
		shortHref.setNotification(adminNotif);
		longText.setNotification(adminNotif);
		
		model.addAttribute("progressCount", 20);
		
		//get the notifications of the present agency admin user notification
		//add the above created notification object - 
		List<Notification> agencyPersonNotifs = notifServ.findNotificationsByUserId(agencyAdmin.getUserId());
		agencyPersonNotifs.add(adminNotif);
		
		//set the notification object to the agency
		//so hibernate mapping takes effect and write corectly to db
		agencyPerson.setNotifications(agencyPersonNotifs);
		agencyPerson.setUnreadNotifs(true);
		
		//get the logged in person object
		//and the agency id and create message for the logged in user
		
		long agencyID = agency.getAgencyId();
		Message agencyShortHref = new Message("/agencyProfile?id=" + Long.toString(agencyID));				
		Message agencyMessage = new Message("Agency");
		Message agencyLongText = new Message("received your request to affiliate.");
		Message agencyName = new Message(agency.getAgencyName());
		//create a list of messages so it can be added to the notification object 
		List<Message> userNotifsMessages = new ArrayList<> ();
		userNotifsMessages.add(0,agencyMessage);
		userNotifsMessages.add(1, agencyName);
		userNotifsMessages.add(2, agencyShortHref);
		userNotifsMessages.add(3, agencyLongText);
		 
		//create notification object for the agency admin user		
		Notification userNotif = new Notification(userNotifsMessages,false,false, agencyMessage.getMessage(), new Date());				
		List<Notification> userNotifs = notifServ.findNotificationsByUserId(loggedInUser.getUserId());
		
		model.addAttribute("progressCount", 40);
		
		//add the notification object to the logged in user notifications
		userNotifs.add(userNotif);
		
		
		loggedInPerson.setNotifications(userNotifs);
		loggedInPerson.setUnreadNotifs(true);
		
		//add the pending user to the agency
		//List<User> pendingUsers = new ArrayList<User>();
		List<User> pendingUsers = agency.getPendingUsers();		
		pendingUsers.add(loggedInUser);
//		agency.setPendingUsers(pendingUsers);
		loggedInUser.setPendingAgency(agency);

		//set the messages to the notification object of the logged in user
		agencyMessage.setNotification(userNotif);
		agencyName.setNotification(userNotif);		
		agencyShortHref.setNotification(userNotif);
		agencyLongText.setNotification(userNotif);
		
		
		//save the messages (2 of them will suffice)---> because hibernate cascade
		messServ.messageSave(agencyMessage);
		messServ.messageSave(agencyShortHref);
		model.addAttribute("progressCount", 60);
		notifServ.saveNotif(userNotif);
		userServ.save(loggedInUser);
		agencyServ.saveAgency(agency);
		
		List<Notification> reverseUserNotifs = notifServ.reverseFindNotificationsByUserId(loggedInUser.getUserId());
		
		model.addAttribute("progressCount", 80);
		
		model.addAttribute("agencyPersonNotifs", agencyPersonNotifs);
		
		int count = reverseUserNotifs.size();
		List<Notification> showUserNotifs = new ArrayList<>();
		for(int i = 0; i < 4; i++) {
			count = count -1;
			if(count == 0) {
				break;
			}
			showUserNotifs.add(reverseUserNotifs.get(i));					
		}

		model.addAttribute("userNotifs", showUserNotifs);

			
		return "redirect:/agencyProfile?id=" + agency.getAgencyId();
		
	}
	
	@GetMapping(value="/affiliateCandidate")
	public String approveAffiliationRequest(@RequestParam("id") long notifId, Model model, Agency agency, Authentication auth) {
		if(auth == null) {
			return "user/login";
		}
		
		//get the user that is an admin for the agency
		User agencyAdmin = (User) userServ.loadUserByUsername(agency.getAdminName());//Robert
		Person agencyPerson = persServ.findPersonByUserId(agencyAdmin.getUserId());
		//get the loggedinUser
		User loggedInUser = (User) userServ.loadUserByUsername(auth.getName());
		
		
		Notification notifToAprove = notifServ.findNotifByNotifId(notifId);
		
			if(notifToAprove.getFirstText().contains("Candidate")) {
				List<Message> messagesToApprove = notifToAprove.getMessages();
				User userToApprove = (User) userServ.loadUserByUsername(messagesToApprove.get(1).getMessage());
				System.out.println("Check user to approve ======> " + userToApprove.getUsername());
				
				if(userServ.getAllPendingUsersByAgencyId(agency.getAgencyId()) != null) {
					List<User> pendingUsersList = userServ.getAllPendingUsersByAgencyId(agency.getAgencyId());
				
					if(!pendingUsersList.contains(userToApprove)) {
						notifToAprove.setHasApprove(false);	
						System.out.println("in the false approve");
						return "redirect:/agency/profile";
					}
				}

		
				//we set the notification is_read to true
				notifToAprove.setRead(true);
				
				List<Message> getMessages = messServ.findMessagesByNotificationId(notifToAprove.getNotificationId());
				String candidateName = getMessages.get(1).getMessage();
				User candidate = (User) userServ.loadUserByUsername(candidateName.toLowerCase());
				
				//add the retrived user to the affiliated users of the agency
				List<User> affiliatedUsers = userServ.getAllAffiliatedUsersByAgencyId(agency.getAgencyId());
				candidate.setOneAgency(agency);
				affiliatedUsers.add(candidate);
				agency.setAffiliatedUsers(affiliatedUsers);
				
				
				//remove the recently affiliated user from the pending affiliation users list
				List<User> pendingUsers = userServ.getAllPendingUsersByAgencyId(agency.getAgencyId());
				pendingUsers.remove(candidate);
				agency.setPendingUsers(pendingUsers);
				candidate.setPendingAgency(null);
		
				//give a new notification for the candidate that he was accepted				
				Message textMessage = new Message ("Agency");
				Message agencyName = new Message(agency.getAgencyName());
				long agencyID = agency.getAgencyId();
				Message agencyShortHref = new Message("/agencyProfile?id=" + Long.toString(agencyID));		
				Message agencyLongText = new Message("accepted your request to affiliate.");
				List<Message> messageList = new ArrayList<>();
				messageList.add(0, textMessage);
				messageList.add(1, agencyName);
				messageList.add(2, agencyShortHref);
				messageList.add(3, agencyLongText);
								
				Notification candidateAccepted = new Notification(messageList,false,false,textMessage.getMessage(), new Date());
				List<Notification> candidateNotifs = notifServ.findNotificationsByUserId(candidate.getUserId());
				candidateNotifs.add(candidateAccepted);
								
				textMessage.setNotification(candidateAccepted);
				agencyName.setNotification(candidateAccepted);
				agencyShortHref.setNotification(candidateAccepted);
				agencyLongText.setNotification(candidateAccepted);
				
				//set the notification to the person so Hibernate can persist
				Person candidatePerson = persServ.findPersonByUserId(candidate.getUserId());
				candidatePerson.setNotifications(candidateNotifs);
				candidatePerson.setUnreadNotifs(true);
				
				//give also a new notification to the agency admin ?
				
				
				
				notifServ.saveNotif(candidateAccepted);				
				userServ.save(candidate);
				agencyServ.saveAgency(agency);
				
				List<Notification> reverseCandidateNotifs = notifServ.reverseFindNotificationsByUserId(candidate.getUserId());
				
				model.addAttribute("userNotifs", reverseCandidateNotifs);				
				
				
				
			}
		
		
		
		return "redirect:/agency/profile";
	}
	
	@GetMapping(value="/disaffiliate")
	@Transactional
	public String disaffiliateCandidate(@RequestParam("id") long agencyId, Model model,  Authentication auth ) {
		
		if(auth == null) {
			return "user/login";
		}
		
		User user = (User) userServ.loadUserByUsername(auth.getName());
		Agency agency = agencyServ.findAgencyByID(agencyId);
		
		User agencyAdmin = (User) userServ.loadUserByUsername(agency.getAdminName());
		Person agencyPerson = persServ.findPersonByUserId(agencyAdmin.getUserId());
		List<Notification> agencyPersonNotifs = notifServ.findNotificationsByUserId(agencyAdmin.getUserId());
		
		System.out.println("where my users at? ----> " + user.getUsername());
		Person loggedInPerson = persServ.findPersonByUserId(user.getUserId());
		List<Notification> loggedInPersonNotifs = notifServ.findNotificationsByUserId(user.getUserId());
		
		
		//send notification to agency admin that the user has dissafliated
		
		//create notification object with messages for agency admin
		long userId = user.getUserId();
		Message shortHref = new Message("/agency/cprofile?id=" + Long.toString(userId));
		Message messageText = new Message("Candidate");
		Message longText = new Message("has been  disaffiliated from " + agency.getAgencyName() + ".");
		Message nameText = new Message(user.getUsername());
		List<Message> adminMessages = new ArrayList<>();
		adminMessages.add(0, messageText);
		adminMessages.add(1, nameText);
		adminMessages.add(2, shortHref);
		adminMessages.add(3, longText);		
		Notification agencyNotif = new Notification(adminMessages, false,false, messageText.getMessage(), new Date());
		messageText.setNotification(agencyNotif);
		longText.setNotification(agencyNotif);
		nameText.setNotification(agencyNotif);
		shortHref.setNotification(agencyNotif);
		
		//add notification to the list of agency admin notifications
		List<Notification> agencyNotifs = agencyPerson.getNotifications();
		agencyNotifs.add(agencyNotif);
		
		//set notification to agency admin
		agencyPerson.setNotifications(agencyNotifs);
		agencyPerson.setUnreadNotifs(true);
		notifServ.saveNotif(agencyNotif);
		persServ.save(agencyPerson);
		userServ.save(agencyAdmin);
		
		//send notification to logged in user that he dissafliated 
		
		//create notification object with messages for user
		long agencyAdminId = agency.getAgencyId();
		shortHref = new Message("/agencyProfile?id=" + Long.toString(agencyAdminId));
		messageText = new Message("Agency");
		longText = new Message("is no longer affiliated with you. ");
		nameText = new Message(agency.getAgencyName());
		List<Message> userMessages = new ArrayList<>();
		adminMessages.add(0, messageText);
		adminMessages.add(1, nameText);
		adminMessages.add(2, shortHref);
		adminMessages.add(3, longText);		
		Notification userNotif = new Notification(userMessages, false,false, messageText.getMessage(), new Date());
		messageText.setNotification(userNotif);
		longText.setNotification(userNotif);
		nameText.setNotification(userNotif);
		shortHref.setNotification(userNotif);
		
		//add notification to the list of user notifications
		List<Notification> userNotifs = loggedInPerson.getNotifications();
		userNotifs.add(userNotif);
		
		//set notification to agency admin
		loggedInPerson.setNotifications(userNotifs);
		loggedInPerson.setUnreadNotifs(true);
		
		
		//make one_agency null for logged in user 
		user.setOneAgency(null);
		
//		//removed from persons_applied_jobs 
//		persServ.deletePersonApplicationsBypersonId(loggedInPerson.getPersonId());
		Set<Job> appliedJobs = (Set<Job>) loggedInPerson.getJobsApplied();		
		Set<Job> noJobApplied = new HashSet<Job>();
		loggedInPerson.setJobsApplied(noJobApplied);
		
		notifServ.saveNotif(userNotif);
		
		persServ.save(loggedInPerson);
		userServ.save(user);
		agencyServ.saveAgency(agency);
		
		List<Notification> reverseUserNotifs = notifServ.reverseFindNotificationsByUserId(user.getUserId());
	
		model.addAttribute("userNotifs", reverseUserNotifs);
		model.addAttribute("agencyMessage", "You are now disaffiliated with this agency.");


		
		
		return "redirect:/person/sprofile";
	}
	
	
	@GetMapping(value="/editAgencyDetails")
	public String editAgencyDetails (@Valid @ModelAttribute Agency patchAgency,
			Model model, Person person, Agency agency,
			RedirectAttributes redirAttr ) {
		
		agency.setAgencyName(patchAgency.getAgencyName());
		agency.setAdminName(patchAgency.getAdminName());
		agency.setUniqueRegCode(patchAgency.getUniqueRegCode());
		agency.setRegComNumber(patchAgency.getRegComNumber());
		agency.setLegalAddress(patchAgency.getLegalAddress());
		agency.setPhoneNumber(patchAgency.getPhoneNumber());
		agency.setEmail(patchAgency.getEmail());
		agency.setWebAddress(patchAgency.getWebAddress());
		agency.setShortDescription(patchAgency.getShortDescription());
		
		System.out.println("PathcAgency is ---->" + patchAgency.getAgencyName() );
		System.out.println("Agency to save ---->" + agency.getAgencyName() );
		agencyServ.saveAgency(agency);
		System.out.println("Saving agency?!?!?!");
				
		redirAttr.addFlashAttribute("agency", agency);
		
		return "redirect:/agency/profile";
	}
	
	
	@GetMapping("/profilePDF")
	public ResponseEntity<?> displayProfilePDF(Agency agency) {
		
		ProfileImg img = null;
		
		List<CompanyDoc> compdocs = new ArrayList<CompanyDoc>();

		List<Job> agencyJobs = new ArrayList<Job>();

		List<Person> affiliatedCandidates = new ArrayList<Person>();

		
		if(profileImgServ.getLastAgencyPic(agency.getAgencyId()) != null) {
			img = profileImgServ.getLastAgencyPic(agency.getAgencyId());
		}
		
		if(jobServ.findJobsByAgencyId(agency.getAgencyId()) != null) {
			agencyJobs = jobServ.findJobsByAgencyId(agency.getAgencyId());
		}
		
		if(companyDocServ.getCompanyDocsByAgencyId(agency.getAgencyId()) != null) {
			companyDocServ.getCompanyDocsByAgencyId(agency.getAgencyId());
		}
		
		
		if(userServ.getAllAffiliatedUsersByAgencyId(agency.getAgencyId()) != null) {
			List<User> affiliatedUsersList = userServ.getAllAffiliatedUsersByAgencyId(agency.getAgencyId());
			affiliatedCandidates = new ArrayList<>();
			Person affiliatedPerson = new Person ();
			for (User affUser : affiliatedUsersList) {
				affiliatedPerson = persServ.findPersonByUserId(affUser.getUserId());
				affiliatedCandidates.add(affiliatedPerson);
				if(profileImgServ.getLastProfilePic(affiliatedPerson.getPersonId()) != null) {
					ProfileImg lastImg = profileImgServ.getLastProfilePic(affiliatedPerson.getPersonId());
					affiliatedPerson.setLastImgId(lastImg.getPicId());
				}
		
		
			
			}
			
		}

		ByteArrayInputStream ptf = ProfileToPDF.exportAgencyProfile(agency, img, agencyJobs, affiliatedCandidates, compdocs); 

		return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename =\"" + "profilePDF" + "\"")
				.body(new InputStreamResource(ptf));
		

	
	}
	
	@RequestMapping(value = "/showApplicants")
	@ResponseStatus(value = HttpStatus.OK)
	public String getJobApplicants(@RequestParam("jobId") long jobId, Model model, Authentication auth, RedirectAttributes rediAttr) {
		Set<Person> applicants = persServ.findCandidatesAppliedToJob(jobId);
		//find the agency that posted the job
		Agency theAgency = agencyServ.findAgencyByJobId(jobId);
		// check if the applicants are affiliated with that agency
			//start a new Set of Person to add the user who is affiliated
		
		//test if this is working
		model.addAttribute("applicantsTo", applicants);
		model.addAttribute("theJobId", jobId);
		
		//get the personsIds approved for this job 
		
		model.addAttribute("approvedPersonIds", persServ.getPersonsIdsApprovedToJob(jobId));
		
		
	return "modals :: #jobApplicantsTo";
	
	
	}
	
	@GetMapping(value="approveCandidate")
	@ResponseStatus(value = HttpStatus.OK)
	public void approveCandidateToJob(@RequestParam("personId") long personId, @RequestParam("jobId") long jobId, 
				Model model, Authentication auth, RedirectAttributes redirAttr) {
		//approve means the job and the person must be saved and
		//establish relationships through getters and setters so that 
		//the many to many to populate persons_jobs_approved table
		
		//get the person from the line 
		Person person = (Person) persServ.findPersonById(personId);
		
		//get the person's approved jobs
		Set<Job> personsJob = person.getJobsApproved();
		
		//get the job from the read more
		Job job = (Job) jobServ.findJobById(jobId);
		
		//add to the person's list of approved jobs
		personsJob.add(job);
		
		//set the list of job to the person
		person.setJobsApproved(personsJob);
		
		persServ.save(person);
		
		//get the  job's approved person
		Set<Person> jobsPerson = job.getPersonsApproved();
		
		//add the person to the list 
		jobsPerson.add(person);
		
		//set the list to the jobs approved persons
		job.setPersonsApproved(jobsPerson);
		
		//save the entities
		jobServ.save(job);
		
		model.addAttribute("acceptedInList", true);
		
				
						
	}

}
