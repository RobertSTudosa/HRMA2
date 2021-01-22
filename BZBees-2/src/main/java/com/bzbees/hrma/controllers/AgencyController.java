package com.bzbees.hrma.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bzbees.hrma.entities.Agency;
import com.bzbees.hrma.entities.CompanyDoc;
import com.bzbees.hrma.entities.Doc;
import com.bzbees.hrma.entities.Person;
import com.bzbees.hrma.entities.ProfileImg;
import com.bzbees.hrma.entities.User;
import com.bzbees.hrma.entities.UserRole;
import com.bzbees.hrma.services.AgencyService;
import com.bzbees.hrma.services.CompanyDocService;
import com.bzbees.hrma.services.ImageResize;
import com.bzbees.hrma.services.PersonService;
import com.bzbees.hrma.services.ProfileImgService;
import com.bzbees.hrma.services.RoleService;
import com.bzbees.hrma.services.UserService;

@Controller
@RequestMapping("/agency")
@SessionAttributes({"person", "userAccount", "agency","lastAgencyPicList",
					"lastPicList", "companyDocList"})
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
		session.setAttribute("agency", agency);

		
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
				Agency agency, Person person ) {
		
		Agency theAgency=agencyServ.findAgencyByID(agency.getAgencyId());
		model.addAttribute("agency", theAgency);
		
		
		//get the agency pics from the profileImg repo
		List<ProfileImg> agencyPics = profileImgServ.getPicsByAgencyId(agency.getAgencyId());
		
		//get the last pic of the agency's profile from profileImg repo
		ProfileImg theImg = profileImgServ.getLastAgencyPic(agency.getAgencyId());
		if(theImg != null) {
			System.out.println("in the if !null of the image");
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
		

		
		if(!model.containsAttribute("companyDocList")) {
			List<CompanyDoc> docList = agency.getCompanyDocs();
			model.addAttribute("companyDocList", docList);
			System.out.println("Existing comp doc list");
		} 
		
//		else {
//			
			
//		}

			
		return "agency/agency_profile";
	}
	
	
	//must implement another form of deleting/detaching an agency from the person
	//keep in place to resolve the profile menu null's profile
		@Transactional
		@GetMapping("/deleteAgency")
		public String deleteAgency(@RequestParam("id") long id, Model model,
									RedirectAttributes redirAttr, Person person, Authentication auth,
									Agency agency)	{
			
			agencyServ.deleteAgencyById(id);
			
			User user = (User) userServ.loadUserByUsername(auth.getName());
			
			if(!user.getRoles().contains("AGENCY")) {
				
				auth = SecurityContextHolder.getContext().getAuthentication();

				List<GrantedAuthority> updatedAuthorities = new ArrayList<>(auth.getAuthorities());
				
				updatedAuthorities.remove(new SimpleGrantedAuthority("AGENCY"));
								
				Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), updatedAuthorities);

				SecurityContextHolder.getContext().setAuthentication(newAuth);
				
				
				UserRole role = new UserRole("AGENCY");
				user.removeRole(role);
				
			}
			
			userServ.save(user);
			
			redirAttr.addAttribute("userAccount", user);
			
			
			
			return "redirect:/person/sprofile";
		}
		
		
		
		@PostMapping(value = "/addAgencyImg", consumes = { "multipart/form-data" })
		public String addProfileImg(Model model, Person person, 
				Agency agency,
				@RequestParam("img") MultipartFile img,
				RedirectAttributes redirAttr) {
		
			//test if agency is present in the method
			System.out.println("Agency is " + agency.getAgencyName());
			
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
				
				List<ProfileImg> picList = profileImgServ.getPicsByAgencyId(agency.getAgencyId());			
				
				profileImgServ.savePic(smallImg);

				picList.add(smallImg);

				List<ProfileImg> lastPicList = new ArrayList<>();
				
				System.out.println("last agency pic is " + profileImgServ.getLastAgencyPic(agency.getAgencyId()));

				lastPicList.add(profileImgServ.getLastAgencyPic(agency.getAgencyId()));

				agency.setPics(picList);

				
				
				agencyServ.saveAgency(agency);

				model.addAttribute("lastAgencyPicList", lastPicList);
				redirAttr.addFlashAttribute("lastAgencyPicList", lastPicList);
				redirAttr.addFlashAttribute("picList", picList);
				redirAttr.addFlashAttribute("img", smallImg);

			return "redirect:/agency/profile";
		}
		
		@GetMapping(value = "/img")
		public ResponseEntity<?> showAgencyImg(@RequestParam("imgId") long id, Person person,
												Model model,
												Agency agency) {
//			ProfileImg profilePic = profileImgServ.findProfilePicById(id);	
			System.out.println("Agency present here is " + agency.getAgencyName());
			if(profileImgServ.getLastAgencyPic(agency.getAgencyId())==null) {
				model.addAttribute("lastAgencyPicList", null);
				return null;
			} 			
			
			ProfileImg agencyPic = profileImgServ.getLastAgencyPic(agency.getAgencyId());
			
//			System.out.println("profile pic that is not displaying is " + agencyPic.getPicName());
			
				return ResponseEntity.ok()
						.contentType(MediaType
								.parseMediaType(agencyPic.getPicType()))
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename =\"" + agencyPic.getPicName() + "\"")
						.body(new ByteArrayResource(agencyPic.getData()));

			 
		}
		
		
		@PostMapping(value = "/addRegCertModal", consumes = { "multipart/form-data" })
		public String addDocsByProfile(Model model, 
				Agency agency, Person person, @RequestParam("doc") MultipartFile[] files,
				RedirectAttributes redirAttr) {
			
			
			List<CompanyDoc> docList = companyDocServ.getCompanyDocsByAgencyId(agency.getAgencyId());
			companyDocServ.deleteAll(docList);
			docList = new ArrayList<>();

			for (MultipartFile file : files) {

				String docName = agency.getAgencyName() + " " + "reg_cert " + StringUtils.cleanPath(file.getOriginalFilename());

				try {

					if (docName.contains("..")) {
						throw new RuntimeException(
								"Cannot store file with relative path outside current directory " + docName);
					}

					CompanyDoc theDoc = new CompanyDoc(docName, file.getContentType(), file.getBytes());
					
						if( companyDocServ.getCompanyDocsByAgencyId(agency.getAgencyId()).contains(theDoc)) {
							System.out.println("Document " + theDoc.getDocName() + " is already saved");
						} else {
							
							System.out.println(theDoc.getDocName() + " is to be saved");
							

							
							for(CompanyDoc doc : docList) {
								if(docList.size() > 0) {
									System.out.println("docs are " + doc.getDocName());									
								} else {
									System.out.println("There's no document in the list");
								}

							}
//							docServ.saveDoc(theDoc);
							companyDocServ.saveAndFlush(theDoc);
//							person.addDoc(theDoc);
							docList.add(theDoc);							
							theDoc.setAgency(agency);
							companyDocServ.flushCompDocDb();
							
						}
					
					
				} catch (IOException e) {
					e.printStackTrace();

				}
			}
			
			agency.setCompanyDocs(docList);
			agencyServ.saveAgency(agency);
						
			redirAttr.addFlashAttribute("companyDocList", companyDocServ.getCompanyDocsByAgencyId(agency.getAgencyId()));

			return "redirect:/agency/profile";
		}
		
		
		@GetMapping("/deleteAgencyDoc")
		public String deleteProfileDocById(@RequestParam("id") long id, Model model, 
				Agency agency, Person person, RedirectAttributes redirAttr) {

			System.out.println("agency present is " + agency.getAgencyName());
			List<CompanyDoc> docList = companyDocServ.getCompanyDocsByAgencyId(agency.getAgencyId());
			
			for (CompanyDoc doc : docList) {
				if(doc.getCompDocId() == id) {
					System.out.println("agency doc name is " + doc.getDocName());
					companyDocServ.deleteCompDocById(doc);
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

		


	

}
