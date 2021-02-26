package com.bzbees.hrma.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bzbees.hrma.entities.Agency;
import com.bzbees.hrma.entities.CompanyDoc;
import com.bzbees.hrma.entities.Doc;
import com.bzbees.hrma.entities.Job;
import com.bzbees.hrma.entities.Language;
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
import com.bzbees.hrma.services.PersonService;
import com.bzbees.hrma.services.ProfileImgService;
import com.bzbees.hrma.services.RoleService;
import com.bzbees.hrma.services.SocialMediaService;
import com.bzbees.hrma.services.TagService;
import com.bzbees.hrma.services.UserService;

@Controller
@RequestMapping("/agency")
@SessionAttributes({ "person", "userAccount", "agency", "lastAgencyPicList", "lastPicList", "companyDocList",
		"socialMediaList", "affiliatedPersonsList","agencyJobList","jobTagsList","lastJobPicList" })
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
			Agency agency,
			Person person) {

		if (auth != null) {
			
		

		// get the user from user Principal
		User user = (User) userServ.loadUserByUsername(auth.getName());
		model.addAttribute("userAccount", user);

		// get the person from repo user query
		person = persServ.findPersonByUserId(user.getUserId());
		model.addAttribute("person", person);

		Agency theAgency = agencyServ.findAgencyByID(agency.getAgencyId());
		model.addAttribute("agency", theAgency);

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
					System.out.println("Id ul ultimei imagini este null");;
				}
			}
			model.addAttribute("affiliatedPersonsList", personsAffiliated);
			
			System.out.println("Any affiliated users around here?");
		} else {
			System.out.println("No no users here");
			model.addAttribute("affiliatedPersonsList", new ArrayList<Person>());
		}
		
		//get the jobs of the agency in the profile
		if(jobServ.findJobsByAgencyId(theAgency.getAgencyId()) !=null) {
			List<Job> agencyJobs = jobServ.findJobsByAgencyId(theAgency.getAgencyId());
			model.addAttribute("agencyJobList", agencyJobs);
			List<Tag> agencyJobsTags = new ArrayList<>();
			for(Job job : agencyJobs) {
				List<Tag> jobsTags = tagServ.findTagsByJobId(job.getJobId());
				agencyJobsTags.addAll(jobsTags);
			}
			
			model.addAttribute("jobTagsList", agencyJobsTags);
			
		} else {
			model.addAttribute("agencyJobList", new ArrayList<>());
			model.addAttribute("jobTagsList", new ArrayList<>());
		}
		
		if(!model.containsAttribute("job")) {
			model.addAttribute("job", new Job());
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
		agency.setLastImageId(smallImg.getPicId());
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
	public String showCandidateProfile (@RequestParam("id") long id, Model model) {
		
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
				tagServ.deleteJobTags(jobTags);
				jobServ.deleteJobById(job);
			}
		}
			
		redirAttr.addFlashAttribute("agencyJobList", jobServ.findJobsByAgencyId(agency.getAgencyId()));

		return "redirect:/agency/profile";
				
	}
	
	@PostMapping(value="/makeJobPrivate")
	public String makePrivate(@Valid @ModelAttribute("job") Job setJob, 
			BindingResult bindingResult, @RequestParam("id") long id, 
			Model model, Person person, 
			Agency agency, RedirectAttributes redirAttr ) throws ResourceNotFoundException {
		
		Job job = jobServ.findJobById(id);
		
		job.setJobPrivate(setJob.isJobPrivate());
		jobServ.save(job);	
		

		List<Job> agencySavedJobs = jobServ.findJobsByAgencyId(agency.getAgencyId());
		redirAttr.addFlashAttribute("agencyJobList", agencySavedJobs);
		
		return "redirect:/agency/profile";
	}
	
	@PostMapping(value="/editJob", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, "multipart/form-data"})
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
	
	
	@PostMapping(value="/affiliate")
	public String affiliateUser(@PathVariable("id") long agencyId, Model model, Agency agency, Authentication auth) {
		//get the user that is logged in
		
		//get the agency which is clicked
		
		/*
		 * create notification for user admin of the agency and make it is_read = false
		 * get the admin username and save the notification on his side
		 */
		
		
		
		/*
		 * notify the user that he's request is on the agency's admin side waiting to be
		 * approved
		 */
		
		
		
		return "redirect:/agency/profile";
		
	}
	
	@PostMapping(value="/editAgencyDetails")
	public String editAgencyDetails (@Valid @ModelAttribute("agency") Agency patchAgency,
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

		
		
		agencyServ.saveAgency(agency);
		
		redirAttr.addFlashAttribute("agency", agency);
		
		return "redirect:/agency/profile";
	}
	


}
